package com.citigroup.liquifi.autopilot.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.citigroup.liquifi.autopilot.db.OutputStepManager;
import com.citigroup.liquifi.entities.LFOutputMsg;
import com.citigroup.liquifi.entities.LFOutputTag;

public class TestOutputStepManager {
	
	public Session openSession(){
		SessionFactory sf = new Configuration().configure().buildSessionFactory();
		Session session = sf.openSession();
		return session;
	}
	
	public LFOutputMsg getOutputStep(){
		
		LFOutputMsg outputStep = new LFOutputMsg();
		outputStep.setTestID("11111");
		outputStep.setActionSequence(1);
		outputStep.setTemplate("NEW_DAY_ORDER");
		outputStep.setOutputMsg(null);
		outputStep.setOutputMsgID(1);
		outputStep.setTopicID("ANY_TO_LIQUIFI");
		
		LFOutputTag outputTag = new LFOutputTag();
		outputTag.setTestID("11111");
		outputTag.setActionSequence(1);
		outputTag.setTagID("37");
		outputTag.setTagValue("${getOrderID}");
		
		LFOutputTag outputTag2 = new LFOutputTag();
		outputTag2.setTestID("11111");
		outputTag2.setActionSequence(1);
		outputTag2.setTagID("11");
		outputTag2.setTagValue("${getOrderID}");
		
		outputStep.addToOutputTagList(outputTag);
		outputStep.addToOutputTagList(outputTag2);
		
		return outputStep;
	}
	
	public LFOutputMsg testSaveOutputStep(OutputStepManager om){
		LFOutputMsg outputStep = getOutputStep();
		om.saveOutputStep(outputStep);
		System.out.println("Saved InputStep " + outputStep.toString());
		return outputStep;
	}
	
	
	public LFOutputMsg testUpdateInputStep(OutputStepManager om, LFOutputMsg outputStep){;
		outputStep.setActionSequence(2);
		om.updateOutputStep(outputStep);
		System.out.println("Saved InputStep " + outputStep.toString());
		return outputStep;
	}
	
	public LFOutputMsg testDeleteInputStep(OutputStepManager om, LFOutputMsg outputStep){
		om.deleteOutputStep(outputStep);
		System.out.println("Delete BrokerInfo " + outputStep.toString());
		return outputStep;
	}
	
	public static void main(String args[]){
		TestOutputStepManager tbim = new TestOutputStepManager();
		Session session = tbim.openSession();
		OutputStepManager om = new OutputStepManager (session);
		tbim.testSaveOutputStep(om);
		session.flush();
	}


}
