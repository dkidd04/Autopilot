package com.citigroup.liquifi.autopilot.messaging;

import java.util.Map;

import com.citigroup.get.quantum.messaging.transport.JMSTransportInfo;
import com.citigroup.get.quantum.messaging.transport.mx.MXJMSTransportInfo;

public class AutoPilotBrokerInfo {
	public static enum BrokerType {
		SONIC {
			@Override
			public JMSTransportInfo getTransportInfo() {
				return new MXJMSTransportInfo();
			}
		},
		TIBCO {
			@Override
			public JMSTransportInfo getTransportInfo() {
				return new JMSTransportInfo();
			}
		};

		public abstract JMSTransportInfo getTransportInfo();
	}

	private BrokerType brokerType = BrokerType.TIBCO;
	private String brokerURL;
	private String user;
	private String password;
	private Map<String, String> setup;
	private String replyAcceptor;
	private boolean defaultTopic;

	public void setBrokerURL(String brokerURL) {
		this.brokerURL = brokerURL;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBrokerURL() {
		return this.brokerURL;
	}

	public String getUser() {
		return this.user;
	}

	public String getPassword() {
		return this.password;
	}

	public Map<String, String> getSetup() {
		return setup;
	}

	public void setSetup(Map<String, String> setup) {
		this.setup = setup;
	}

	public String getReplyAcceptor() {
		return replyAcceptor;
	}

	public void setReplyAcceptor(String replyAcceptor) {
		this.replyAcceptor = replyAcceptor;
	}

	public BrokerType getBrokerType() {
		return brokerType;
	}

	public void setBrokerType(BrokerType brokerType) {
		this.brokerType = brokerType;
	}

	public JMSTransportInfo newTransportInfo() {
		return getBrokerType().getTransportInfo();
	}

	public boolean isDefaultTopic() {
		return defaultTopic;
	}

	public void setDefaultTopic(boolean defaultTopic) {
		this.defaultTopic = defaultTopic;
	}
}
