package com.citigroup.liquifi.autopilot.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.citigroup.liquifi.autopilot.db.TestCaseManager;
import com.citigroup.liquifi.entities.LFOutputMsg;
import com.citigroup.liquifi.entities.LFOutputTag;
import com.citigroup.liquifi.entities.LFTag;
import com.citigroup.liquifi.entities.LFTestCase;
import com.citigroup.liquifi.entities.LFTestInputSteps;

public class TestTestCaseManager {

	public Session openSession(){
		SessionFactory sf = new Configuration().configure().buildSessionFactory();
		Session session = sf.openSession();
		return session;
	}

	public LFTestCase getTestCase(){

		LFTestCase testcase = new LFTestCase();

		testcase.setAppName("SampeTestCase");
		testcase.setTestID("11111");
		testcase.setDescription("Sample Test Case XML File");
		testcase.setActive('Y');
		testcase.setCategory("Sample");
		testcase.setRegion("US");
		testcase.setReleaseNum("1.0");

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

		testcase.addToInputStep(inputStep);

		return testcase;
	}

	public LFTestCase testSaveTestCase(TestCaseManager tm){
		LFTestCase testcase = getTestCase();
		try {
			tm.saveTestCase(testcase);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Saved TestCase " + testcase.toString());
		return testcase;
	}


	public LFTestCase testUpdateTestCase(TestCaseManager tm, LFTestCase testcase){;
	testcase.setAppName("LIQUIFI");
	testcase.setCategory("Sample2");
	try {
		// bb tm.updateTestCase(testcase);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println("Update TestCase " + testcase.toString());
	return testcase;
	}

	public LFTestCase testDeleteInputStep(TestCaseManager tm, LFTestCase testcase){
		try {
			tm.deleteTestCase(testcase);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Delete TestCase " + testcase.toString());
		return testcase;
	}

	public static void main(String args[]){
		TestTestCaseManager tbim = new TestTestCaseManager();
		Session session = tbim.openSession();
		TestCaseManager tm = new TestCaseManager (session);
		tm.loadAppNameFromDB();
		tm.loadCategoryFromDB();
		
		System.out.println(tm.getAppName());
		System.out.println(tm.getCategory());
		
		
//		LFTestCase testcase = tbim.TestSaveTestCase(tm);
//		testcase = tbim.TestUpdateTestCase(tm, testcase);
//		tbim.TestDeleteInputStep(tm, testcase);

//		List<LFTestCase> testcaseList = DBUtil.getInstance().getTestCase("testID", "11111");
//
//		for(LFTestCase testcase : testcaseList){
//
//			List<LFTestInputSteps> inputStepList = testcase.getInputStepList();
//
//			for(LFTestInputSteps step : inputStepList){
//				if(step != null){
//					Set<LFTag> inputTags = step.getInputTagsValueList();
//					Vector<Vector<String>> tagVectorList = new Vector<Vector<String>>();
//					if(inputTags != null){
//						Iterator<LFTag> iter = inputTags.iterator();
//						for(int i=0; i< inputTags.size(); i++){
//							LFTag tag = iter.next();
//							Vector<String> tagVector = new Vector<String>(2);
//							tagVector.add(0, tag.getTagID());
//							tagVector.add(1, tag.getTagValue());
//							tagVectorList.add(tagVector);
//						}
//					}
//				}
//			}
//		}
	}
}


