package com.citigroup.liquifi.autopilot.messaging;

import java.util.HashMap;

import com.citigroup.get.quantum.messaging.Acceptor;
import com.citigroup.get.quantum.messaging.Initiator;
import com.citigroup.get.quantum.messaging.MessageListener;
import com.citigroup.get.quantum.messaging.MessagingException;
import com.citigroup.get.quantum.messaging.transport.JMSTransportInfo;
import com.citigroup.get.quantum.messaging.transport.TransportCredentials;
import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;
import com.citigroup.liquifi.autopilot.logger.AceLogger;
import com.citigroup.liquifi.util.AutoPilotConstants;
import com.citigroup.liquifi.util.Util;

public enum ConnectionManager {
	INSTANCE;

	private AceLogger logger = AceLogger.getLogger(this.getClass().getSimpleName());
	HashMap<String, Initiator> initiatorHash = new HashMap <String, Initiator>();
	HashMap<String, Acceptor> acceptorHash = new HashMap <String, Acceptor>();
	AutoPilotBrokerInfoFactory brokerFactory = null;

	ConnectionManager()	{
		try {
			brokerFactory = ApplicationContext.getBrokerFactory();
			logger.info("ConnectionManager initialized.");

		} catch (Exception e) {
			e.printStackTrace();
			logger.severe(AutoPilotConstants.AutoPilotError_CannotLoadTopicInitiator);
		}
	}

	public synchronized Initiator getInitiator(String strSymbol, String topicName) {
		String topic = getInitiatorTopic(strSymbol, topicName);

		Initiator initiator = initiatorHash.get(topic);

		if(initiator == null) {
			try {
				AutoPilotBrokerInfo broker = brokerFactory.getInitiatorBrokerMapping().get(topicName);

				TransportCredentials tc = new TransportCredentials(broker.getUser(), 
						broker.getPassword(),
						null );	

				JMSTransportInfo transportInfo = broker.newTransportInfo();
				transportInfo.setUrl(broker.getBrokerURL());
				transportInfo.setSendDestinationName(topic);
				transportInfo.setTransportCredentials(tc);
				transportInfo.setDeliveryMode(JMSTransportInfo.DeliveryMode.Reliable);
				if(isQueueAcceptor(broker)){
					transportInfo.setSelector(broker.getSelector());
					transportInfo.setPointToPoint(true);
					initiator = new QueueInitiator(transportInfo);
				} else {
					initiator = new Initiator(transportInfo);
				}
				initiator.start();
				initiatorHash.put(topic, initiator);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}

		return initiator;
	}

	private boolean isQueueAcceptor(AutoPilotBrokerInfo broker) {
		return broker.getSelector()!=null;
	}

	public String getInitiatorTopic(String strSymbol, String topicName) {
		String value = ApplicationContext.getBrokerFactory().getInitiatorBrokerMap().get(topicName+"_"+strSymbol);
		if (value == null) {
			String letter = ((Character) strSymbol.charAt(0)).toString().toUpperCase();
			value = ApplicationContext.getBrokerFactory().getInitiatorBrokerMap().get(topicName+"_"+letter);
		}
		return value;
	}

	public synchronized Acceptor getAcceptor(String strSymbol, String topicName) throws Exception {
		String topic = getAcceptorTopic(strSymbol, topicName);

		if(topic == null) {
			throw new Exception("No topic defined for "+topicName+" and symbol "+strSymbol+" - please add into broker.xml");
		}

		Acceptor acceptor = acceptorHash.get(topic);

		if(acceptor == null) {
			try {
				AutoPilotBrokerInfo broker = brokerFactory.getAcceptorBrokerMapping().get(topicName);

				TransportCredentials tc = new TransportCredentials(broker.getUser(), 
						broker.getPassword(), null);

				JMSTransportInfo transportInfo = broker.newTransportInfo();
				transportInfo.setUrl(broker.getBrokerURL());
				transportInfo.setReceiveDestinationNames(new String[]{topic});
				transportInfo.setTransportCredentials(tc);
				if(isQueueAcceptor(broker)){
					transportInfo.setSelector("JMSCorrelationID = '"+broker.getSelector()+"'");
					transportInfo.setPointToPoint(true);
				}

				acceptor = new Acceptor(transportInfo);
				acceptor.setMessageListener((MessageListener) new AutoPilotListener(topic));
				acceptor.start();
				acceptorHash.put(topic, acceptor);
			} catch (Exception e) {
				throw new Exception("Unable to connect to topic "+topic+" defined for "+topicName+" and symbol "+strSymbol+" - please add to broker.xml\n" + Util.getStackTrace(e));
			}
		}

		return acceptor;
	}

	public String getAcceptorTopic(String strSymbol, String topicName) {
		if(strSymbol == null || strSymbol.length() == 0) {
			return null;
		}

		// why are we looking topic up using symbol???
		String value = ApplicationContext.getBrokerFactory().getAcceptorBrokerMap().get(topicName+"_"+strSymbol);

		if (value == null) {
			String letter = String.valueOf(strSymbol.charAt(0)).toUpperCase();//TODO
			value = ApplicationContext.getBrokerFactory().getAcceptorBrokerMap().get(topicName+"_"+letter);
		}
		return value;
	}

	public String getAcceptorTopicId(String topic) {
		return ApplicationContext.getBrokerFactory().getReverseAcceptorBrokerMap().get(topic);
	}

}
