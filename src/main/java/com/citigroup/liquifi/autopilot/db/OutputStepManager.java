package com.citigroup.liquifi.autopilot.db;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.citigroup.liquifi.entities.LFLabel;
import com.citigroup.liquifi.entities.LFOutputMsg;
import com.citigroup.liquifi.entities.LFStepLinkEntry;
import com.citigroup.liquifi.entities.LFTestCase;

public class OutputStepManager {
	private Session session;
	private Transaction tx;
	
	public OutputStepManager(Session session){
		this.session = session;
	}
	
	public void saveOutputStep (LFOutputMsg output){
		session.save(output);
		session.flush();
	}
	public void updateOutputStep (LFOutputMsg output){
		session.update(output);
		session.flush();
		
	}
	public void deleteOutputStep (LFOutputMsg output){
		session.delete(output);
		session.flush();
	}
	
	public List<LFOutputMsg> getOutputMsgList (String testID, int intActionSequence){
		session.clear();
		//session.refresh();
		Query q = session.createQuery("from LFOutputMsg where testID = '" + testID + "' and ActionSequence = " + intActionSequence);
		
		return (q.list());
	}
	
	public void addStepLinkToOutputMsg(String apVar, int linkedInputStepNum, LFOutputMsg outputStep) throws Exception{
		
		try{
			session.clear();
			tx = session.beginTransaction();
			
			Query q = session.createQuery("from LFOutputMsg where testID = '" + outputStep.getTestID() 
					                                                          + "' and outputMsgID = " + outputStep.getOutputMsgID() 
					                                                          + " and actionSequence = " + outputStep.getActionSequence());
			
			List<LFOutputMsg> results = q.list();
			
			if(results.size() == 1){ //should always return 1 as we used the key
				LFOutputMsg outputMsg = results.get(0);
				LFStepLinkEntry stepLink = new LFStepLinkEntry(apVar, outputStep.getTestID(), outputStep.getOutputMsgID(), linkedInputStepNum, outputStep.getActionSequence());
				//outputMsg.getStepLinkEntries().add(stepLink);
			}


			tx.commit();
		}catch(Exception e){
			if(tx != null) tx.rollback();
			throw e;
		}finally{
  
//			session.close();
		}
	}
	
	public void removeStepLinkToOutputMsg(int inputStepNum, LFOutputMsg outputStep) throws Exception{
		
		try{
			session.clear();
			tx = session.beginTransaction();
			
			Query q = session.createQuery("from LFOutputMsg where testID = '" + outputStep.getTestID() 
					                                                          + "' and outputMsgID = " + outputStep.getOutputMsgID() 
					                                                          + " and actionSequence = " + outputStep.getActionSequence());
			
			List<LFOutputMsg> results = q.list();
			
			if(results.size() == 1){ //should always return 1 as we used the key
				LFOutputMsg outputMsg = results.get(0);
				//outputMsg.getStepLinkEntries().remove(inputStepNum);
			}
			
			tx.commit();
		}catch(Exception e){
			if(tx != null) tx.rollback();
			throw e;
		}finally{
			session.flush();
//			session.close();
		}
	}
	
	public List<LFStepLinkEntry> getStepLinkEntryTestcase(String testID){
		tx = session.beginTransaction();
		Query q = session.createQuery("from LFStepLinkEntry where testID = '" + testID + "'");
		tx.commit();
		return q.list();
	}
	public int getTestIDListSizeForATemplate (String template){		
		
		try {
			Query q = session.createQuery("from LFOutputMsg where Template ='" + template + "'");
			return q.list().size();
		} catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	
}
