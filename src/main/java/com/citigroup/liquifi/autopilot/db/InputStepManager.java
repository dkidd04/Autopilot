package com.citigroup.liquifi.autopilot.db;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.citigroup.liquifi.entities.LFOutputMsg;
import com.citigroup.liquifi.entities.LFStepLinkEntry;
import com.citigroup.liquifi.entities.LFTestInputSteps;

public class InputStepManager {
	private Session session;
	private Transaction tx;
	
	public InputStepManager(Session session){
		this.session = session;
	}
	
	public void saveInputStep (LFTestInputSteps input){
		session.save(input);
		session.flush();
	}
	public void updateInputStep (LFTestInputSteps input){
		session.update(input);
		session.flush();
		
	}
	public void deleteInputStep (LFTestInputSteps input){
		session.delete(input);
		session.flush();
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
		
	}
	
	
	public void addLinkedOutputMsgToInputStep(int inputStepNum, LFOutputMsg linkedOutputStep) throws Exception{
		
		try{
			session.clear();
			tx = session.beginTransaction();
			
			Query q = session.createQuery("from LFTestInputSteps where testID = '" + linkedOutputStep.getTestID() 
					                                                           + "' and actionSequence = " + inputStepNum);
			List<LFTestInputSteps> results = q.list();
			
			if(results.size() == 1){ //should always return 1 as we used the key
				LFTestInputSteps inputStep = results.get(0);
				inputStep.getLinkedOutputStepList().add(linkedOutputStep);
			}

			tx.commit();
		}catch(Exception e){
			if(tx != null) tx.rollback();
			throw e;
		}finally{
  
//			session.close();
		}
	}
	
	public int getTestIDListSizeForATemplate (String template){		
	
		try {
			Query q = session.createQuery("from LFTestInputSteps where Template ='" + template + "'");
			return q.list().size();
		} catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}
}
