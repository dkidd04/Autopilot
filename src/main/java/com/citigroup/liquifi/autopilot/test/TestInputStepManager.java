package com.citigroup.liquifi.autopilot.test;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.citigroup.liquifi.autopilot.db.InputStepManager;
import com.citigroup.liquifi.entities.LFTag;
import com.citigroup.liquifi.entities.LFTestInputSteps;
import com.citigroup.liquifi.util.DBUtil;

public class TestInputStepManager {
	
	public Session openSession(){
		SessionFactory sf = new Configuration().configure().buildSessionFactory();
		Session session = sf.openSession();
		return session;
	}
	
	public LFTestInputSteps getInputStep(){
		
		LFTestInputSteps inputStep = new LFTestInputSteps();
		inputStep.setTestID("11111");
		inputStep.setActionSequence(1);
		inputStep.setTemplate("NEW_DAY_ORDER");
		inputStep.setMessage(null);
		inputStep.setMsgType("FixMsg");
		inputStep.setUseOutputMsg('N');
		inputStep.setOutputMsgID("NEWACK");
		inputStep.setTopicID("ANY_TO_LIQUIFI");
		
		LFTag inputTag = new LFTag();
		inputTag.setTestID("11111");
		inputTag.setActionSequence(1);
		inputTag.setTagID("37");
		inputTag.setTagValue("${getOrderID}");
		
		LFTag inputTag2 = new LFTag();
		inputTag2.setTestID("11111");
		inputTag2.setActionSequence(1);
		inputTag2.setTagID("11");
		inputTag2.setTagValue("${getClOrderID}");
		
		inputStep.addToInputTagValueList(inputTag);
		inputStep.addToInputTagValueList(inputTag2);
		
		return inputStep;
	}
	
	public LFTestInputSteps testSaveInputStep(InputStepManager im){
		LFTestInputSteps inputStep = getInputStep();
		im.saveInputStep(inputStep);
		System.out.println("Saved InputStep " + inputStep.toString());
		return inputStep;
	}
	
	
	public LFTestInputSteps testUpdateInputStep(InputStepManager im, LFTestInputSteps inputStep){;
		inputStep.setActionSequence(2);
		im.saveInputStep(inputStep);
		System.out.println("Saved InputStep " + inputStep.toString());
		return inputStep;
	}
	
	public LFTestInputSteps testDeleteInputStep(InputStepManager im, LFTestInputSteps inputStep){
		im.deleteInputStep(inputStep);
		System.out.println("Delete BrokerInfo " + inputStep.toString());
		return inputStep;
	}
	
	public static void main(String args[]){
		List<LFTestInputSteps> inputStepList = DBUtil.getInstance().getInputStep("testID", "11111");
		
		for(LFTestInputSteps step : inputStepList){
			System.out.println(step.toString());
			List<LFTag> inputTags = step.getInputTagsValueList();
			Vector<Vector<String>> tagVectorList = new Vector<Vector<String>>();
			if(inputTags != null){
				Iterator<LFTag> iter = inputTags.iterator();
				for(int i=0; i< inputTags.size(); i++){
					LFTag tag = iter.next();
					Vector<String> tagVector = new Vector<String>(2);
					tagVector.add(0, tag.getTagID());
					tagVector.add(1, tag.getTagValue());
					tagVectorList.add(tagVector);
				}
			}
		}
	}

}
