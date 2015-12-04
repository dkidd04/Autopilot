package com.citigroup.liquifi.autopilot.db;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.citigroup.liquifi.entities.LFTestOutcome;

public class TestOutcomeManager {
	private Session session;
	private Transaction tx;	
	private List<LFTestOutcome> allTestOutcome = new ArrayList<LFTestOutcome>();
	
	public TestOutcomeManager(Session session){
		if (session != null){
			this.session = session;
		}
	}
	
	public void saveTestOutcome(LFTestOutcome testOutcome) throws Exception{
		try{
			tx = session.beginTransaction();
			
			session.clear();
			session.saveOrUpdate(testOutcome);
			session.flush();
			session.refresh(testOutcome);
			tx.commit();
		}catch (Exception e){
			if (tx != null) tx.rollback();
			throw e;
		}
	}
	
	public void updateTestOutcome (LFTestOutcome testOutcome) throws Exception{
		session.update(testOutcome);
		session.flush();
	}
	
	public void deleteTestOutcome (LFTestOutcome testOutcome){
		session.delete(testOutcome);
		session.flush();
	}
	
//	public void loadInputTemplateFromDB (){
//		Query q = session.createQuery("from LFTemplate where IsInput = 'Y'");
//		List<LFTemplate> templateList = q.list();
//		allTemplates.addAll(templateList);
//		
//		for(LFTemplate template  : templateList){
//			inputTemplateMap.put(template.getTemplateName(), template);
//			allTemplateMap.put(template.getTemplateName(), template);
//		}
//		session.close();
//	}
//	
//	public void loadOutputTemplateFromDB (){
//		Query q = session.createQuery("from LFTemplate where IsInput = 'N'");
//		List<LFTemplate> templateList = q.list();
//		allTemplates.addAll(templateList);
//		
//		for(LFTemplate template  : templateList){
//			outputTemplateMap.put(template.getTemplateName(), template);
//			allTemplateMap.put(template.getTemplateName(), template);
//		}
//		session.close();
//		
//	}
//	
	
	public void loadAllTestOutcomeFromDB (){		
		//to be implemented
		try {
			Query q = session.createQuery("from LFTestOutcome order by LastActivityUserID,ValidationTimestamp,TestID");
			List<LFTestOutcome> testOutcomeList = q.list();
			
			for(LFTestOutcome tOutcome  : testOutcomeList){
				allTestOutcome.add(tOutcome);
			}
			session.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public List<LFTestOutcome> getAllTestOutcome() {
		return allTestOutcome;
	}

	public void setAllTestOutcome(List<LFTestOutcome> allTestOutcome) {
		this.allTestOutcome = allTestOutcome;
	}


	
	
}
