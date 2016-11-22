package com.citigroup.liquifi.autopilot.db;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.citigroup.liquifi.entities.LFLabel;
import com.citigroup.liquifi.entities.LFTestCase;



public class LabelManager {
	
	private Session session;
	private List<LFLabel> labels;
	private Map<String, LFLabel> nameToLabels = new TreeMap<String, LFLabel>();
	private Transaction tx;
	
	public LabelManager(Session session){
		this.session = session;
	}
	
	public void saveLabel(LFLabel label) throws Exception{
		try{
			tx = session.beginTransaction();
			session.clear();
			session.save(label);
			//session.refresh(testcase);
			tx.commit();
		}catch (Exception e){
			if (tx != null) tx.rollback();
			throw e;
		}finally{
			session.flush();
		}

	}

	public void updateLabel(LFLabel orig, LFLabel label) throws Exception{
		try{
			tx = session.beginTransaction();					
			session.clear();
			session.delete(orig);
			session.flush();
			session.clear();
			session.replicate(label, ReplicationMode.LATEST_VERSION);
			tx.commit();
		}catch(Exception e){
			if(tx != null) tx.rollback();
			throw new Exception(e);
		}
		finally{
			session.flush();
		}
	}

	public void deleteLabel (LFLabel label) throws Exception{
		try{
			tx = session.beginTransaction();
			session.clear();
			session.delete(label);
			tx.commit();
		}catch(Exception e){
			if(tx != null) tx.rollback();
			throw e;
		}finally{
			session.flush();
		}
	}
	

	public Session getSession() {
		return session;
	}
	
	public void setSession(Session session){
		this.session = session;
	}
	
	public void loadLabelFromDB(){
		
		tx = session.beginTransaction();
		Criteria crit = session.createCriteria(LFLabel.class);

		String strApplication = System.getProperty("application");
		if (strApplication!=null && strApplication.trim().length()>0) {
			//todo - update the AppName on db
			if (strApplication.equalsIgnoreCase("AEE")) {
				strApplication="LIQUIFI";				
			}
			crit.add(Restrictions.eq("appName", strApplication.toUpperCase() ));
		}
		//add the criteria for region
		String strRegion = System.getProperty("region");
		
		if (strRegion.equals("emea")) {
			strRegion = "EMEA";
		}
		if (strRegion!=null && strRegion.trim().length()>0) {
			//todo - update the AppName on db
			crit.add(Restrictions.eq("region", strRegion.toUpperCase() ));
		}
		
		@SuppressWarnings("unchecked")
		List<LFLabel> list = (List<LFLabel>)crit.list();
		
		Collections.sort(list, new Comparator<LFLabel>() {
			@Override
			public int compare(LFLabel o1, LFLabel o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}} );
		
		setLabel(list);
		
		for(LFLabel lb : this.labels){
			this.nameToLabels.put(lb.getLabel(), lb);
		}
		
		tx.commit();
	}
	
	public Map<String, LFLabel> getNameToLabels() {
		return nameToLabels;
	}

	public void setNameToLabels(HashMap<String, LFLabel> nameToLabels) {
		this.nameToLabels = nameToLabels;
	}

	public void setLabel(List<LFLabel> labels){
		this.labels = labels;
	}
	
	public List<LFLabel> getLabels(){
		return this.labels;
	}
	
	public List<LFLabel> getLabelsFromDB(){
		loadLabelFromDB();
		return this.labels;
	}
	
	public void addTestCaseToLabel(String label, String testID) throws Exception{
		
		try{
			session.clear();
			tx = session.beginTransaction();
			LFTestCase testcase = (LFTestCase) session.load(LFTestCase.class, testID);

			tx = session.beginTransaction();
			Criteria crit = session.createCriteria(LFLabel.class);

			String strApplication = System.getProperty("application");
			if (strApplication!=null && strApplication.trim().length()>0) {
				//todo - update the AppName on db
				if (strApplication.equalsIgnoreCase("AEE")) {
					strApplication="LIQUIFI";				
				}
				crit.add(Restrictions.eq("appName", strApplication ));
			}
			//add the criteria for region
			String strRegion = System.getProperty("region");
			
			if (strRegion.equals("emea")) {
				strRegion = "EMEA";
			}
			
			if (strRegion!=null && strRegion.trim().length()>0) {
				//todo - update the AppName on db
				crit.add(Restrictions.eq("region", strRegion ));
			}
			crit.add(Restrictions.eq("label", label));
			if(crit.list().size() > 0){
				LFLabel lb = (LFLabel) crit.list().get(0);
				lb.getTestcases().add(testcase);
				testcase.getLabelSet().add(lb);
				tx.commit();
			}
			
		}catch(Exception e){
			if(tx != null) tx.rollback();
			throw e;
		}finally{
			session.flush();
//			session.close();
		}
	}
	
	public void removeLabelFromTestcase(String label, String testID) throws Exception{
		
		try{	
			session.clear();
			tx = session.beginTransaction();
			LFTestCase testcase = (LFTestCase) session.load(LFTestCase.class, testID);

			tx = session.beginTransaction();
			Criteria crit = session.createCriteria(LFLabel.class);

			String strApplication = System.getProperty("application");
			if (strApplication!=null && strApplication.trim().length()>0) {
				//todo - update the AppName on db
				if (strApplication.equalsIgnoreCase("AEE")) {
					strApplication="LIQUIFI";				
				}
				crit.add(Restrictions.eq("appName", strApplication ));
			}
			//add the criteria for region
			String strRegion = System.getProperty("region");
			
			if (strRegion.equals("emea")) {
				strRegion = "EMEA";
			}
			
			if (strRegion!=null && strRegion.trim().length()>0) {
				//todo - update the AppName on db
				crit.add(Restrictions.eq("region", strRegion ));
			}
			crit.add(Restrictions.eq("label", label));
			if(crit.list().size() > 0){
				LFLabel lb = (LFLabel) crit.list().get(0);
				lb.getTestcases().remove(testcase);
				tx.commit();
			}
			
		}catch(Exception e){
			if(tx != null) tx.rollback();
			throw e;
		}finally{
			session.flush();
//			session.close();
		}
	}
	
	public Set<LFTestCase> getTestCasesForLabel(String labelName){
		session.clear();
		tx = session.beginTransaction();
		Criteria crit = session.createCriteria(LFLabel.class);

		String strApplication = System.getProperty("application");
		if (strApplication!=null && strApplication.trim().length()>0) {
			//todo - update the AppName on db
			if (strApplication.equalsIgnoreCase("AEE")) {
				strApplication="LIQUIFI";				
			}
			crit.add(Restrictions.eq("appName", strApplication ));
		}
		//add the criteria for region
		String strRegion = System.getProperty("region");
		
		if (strRegion.equals("emea")) {
			strRegion = "EMEA";
		}
		
		if (strRegion!=null && strRegion.trim().length()>0) {
			//todo - update the AppName on db
			crit.add(Restrictions.eq("region", strRegion ));
		}
		crit.add(Restrictions.eq("label", labelName));
		if(crit.list().size() > 0){
			LFLabel lb = (LFLabel) crit.list().get(0);
			tx.commit();
			session.flush();
			return lb.getTestcases();
		}
		
		return new HashSet<LFTestCase>();
	}
	
	public List<LFTestCase> getTestCaseForCategoryAndLabel(String category, String label){
		tx = session.beginTransaction();
		Query q = session.createQuery("from LFTestCase tc join fetch tc.labelSet lbs where lbs.label = '" + label + "' and tc.category = '" + category + "'");
		tx.commit();
		return q.list();
	}
	public String getLabelNames(String testID){
		String labelArrayString = null; 
		for(LFLabel lb : getLabelsForTestcase(testID)){
			if( labelArrayString == null)
				labelArrayString = lb.getLabel();
			else
				labelArrayString = labelArrayString + "|"+lb.getLabel();
		}
		return labelArrayString;
	}
	
	
	public List<LFLabel> getLabelsForTestcase(String testID){
		tx = session.beginTransaction();
		Query q = session.createQuery("from LFLabel lb join fetch lb.testcases tc where tc.testID = '" + testID + "'");
		tx.commit();
		return q.list();
	}
	
	public List<LFLabel> getAvailableLabelsForTestcase(String testID){
		List<LFLabel> availableLabels = getLabelsFromDB();
		List<LFLabel> labelsInTestcase = getLabelsForTestcase(testID);
		for(LFLabel lb : labelsInTestcase){
			availableLabels.remove(lb);
		}
		
		return availableLabels;
	}

	
	

	
}

