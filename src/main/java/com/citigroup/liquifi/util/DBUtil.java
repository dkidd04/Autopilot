/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.citigroup.liquifi.util;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;
import com.citigroup.liquifi.autopilot.db.BrokerInfoManager;
import com.citigroup.liquifi.autopilot.db.CategoryManager;
import com.citigroup.liquifi.autopilot.db.InputStepManager;
import com.citigroup.liquifi.autopilot.db.LabelManager;
import com.citigroup.liquifi.autopilot.db.TagManager;
import com.citigroup.liquifi.autopilot.db.TemplateManager;
import com.citigroup.liquifi.autopilot.db.TestCaseManager;
import com.citigroup.liquifi.autopilot.db.TestOutcomeManager;
import com.citigroup.liquifi.autopilot.db.TopicManager;
import com.citigroup.liquifi.autopilot.db.dao.BrokerInfo;
import com.citigroup.liquifi.entities.LFCategory;
import com.citigroup.liquifi.entities.LFLabel;
import com.citigroup.liquifi.entities.LFOutputMsg;
import com.citigroup.liquifi.entities.LFOutputTag;
import com.citigroup.liquifi.entities.LFTag;
import com.citigroup.liquifi.entities.LFTemplate;
import com.citigroup.liquifi.entities.LFTestCase;
import com.citigroup.liquifi.entities.LFTestInputSteps;
import com.citigroup.liquifi.entities.LFTestOutcome;
import com.citigroup.liquifi.entities.LFTopic;
import com.citigroup.liquifi.autopilot.db.OutputStepManager;
import com.citigroup.liquifi.autopilot.db.CommonOverwriteTagManager;
import com.citigroup.liquifi.entities.LFCommonOverwriteTag;

public class DBUtil {
	public static DBUtil dbUtil;
	private BrokerInfoManager bim;
	private TopicManager tm;
	private TemplateManager tem;
	private TestCaseManager tcm;
	private InputStepManager ism;
	private TagManager itm;
	private OutputStepManager osm;
	private List<BrokerInfo> brokerInfoList;
	private List<LFTopic> topicList;
	private SessionFactory sf = new Configuration().configure().buildSessionFactory();
	private TestOutcomeManager tom;
	private CommonOverwriteTagManager com;
	private CategoryManager ctm;
	private LabelManager lbm;

	public static DBUtil getInstance(){
		if (dbUtil == null){
			dbUtil = new DBUtil();
		}
		return dbUtil;
	}
	public Session openSession(){
		Session session = sf.openSession();
		return session;
	}
	public DBUtil(){
		bim = new BrokerInfoManager(openSession());
		tm = new TopicManager(openSession());
		tem = new TemplateManager(openSession());
		tcm = new TestCaseManager(openSession());
		ism = new InputStepManager(openSession());
		itm = new TagManager(openSession());
		osm = new OutputStepManager(openSession());
		tom = new TestOutcomeManager(openSession());
		com = new CommonOverwriteTagManager(openSession());
		ctm = new CategoryManager(openSession());
		lbm = new LabelManager(openSession());
	}

	public void saveToDB(LFCategory category){
		try {
			ctm.saveCategory(category);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveToDB(BrokerInfo brokerInfo){
		bim.saveBrokerInfo(brokerInfo); 
	}
	public void saveToDB(LFTopic topic) {
		tm.saveTopic(topic);
	}
	public void saveToDB(LFTemplate template){
		tem.saveTemplate(template);
	}
	
	public void saveToDB(LFTestOutcome tOutcome) throws Exception{

		try {
			if(tom.getSession()== null || !tom.getSession().isOpen()){
				tom.setSession(openSession());
			}	
			tom.saveTestOutcome(tOutcome);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void saveToDB(LFCommonOverwriteTag coTag) {
		try {
			if(com.getSession()== null || !com.getSession().isOpen()){
				com.setSession(openSession());
			}	
			com.saveTag(coTag);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void saveToDB(LFTestCase testcase) {
		try {
			if(tcm.getSession()== null || !tcm.getSession().isOpen()){
				tcm.setSession(openSession());
			}
			tcm.saveTestCase(testcase);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void removeFromDB(LFLabel label){
		
		try {
			lbm.deleteLabel(label);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeFromDB(LFCategory category){
		try {
			ctm.deleteCategory(category);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void removeFromDB(LFTag tag) {
		itm.deleteTag(tag);
	}
	
	public void removeFromDB(LFOutputTag tag) {
		itm.deleteTag(tag);
	}
	public void removeFromDB(LFTestInputSteps step){
		ism.deleteInputStep(step);
	}
	public void removeFromDB(LFOutputMsg step){	
		osm.deleteOutputStep(step);
	}

	public List<BrokerInfo> getBrokerInfo(){
		if(brokerInfoList==null){
			brokerInfoList =(List<BrokerInfo>) bim.getAllBrokerInfo();
		}
		return brokerInfoList;
	}

	public List<LFTopic> getTopics() {
		if(topicList == null){
			topicList = (List<LFTopic>) tm.getAllTopics();
		}
		return topicList;
	}
	
	public List<LFCategory> getCategories(){
		return ctm.getCategories();
	}
	
	public List<LFTestCase> getTestCases(String criteriaField, String criteriaValue) {
		if (tcm.getSession() != null && tcm.getSession().isOpen()){
			tcm.getSession().close();
		}
		tcm.setSession(openSession()); 
		Criteria crit = tcm.getSession().createCriteria(LFTestCase.class);
		if(criteriaValue != null && !criteriaValue.equals("")){
			criteriaField = criteriaField.replace(criteriaField.charAt(0), Character.toLowerCase(criteriaField.charAt(0)));
			crit.add(Restrictions.ilike(criteriaField,"%"+criteriaValue+"%"));			
		}
		
		//
		// application and region are specified at app start time.
		// mask these, as otherwise the gui is very arbitrary.
		//
		
		//add the criteria for application
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
		if (strRegion!=null && strRegion.trim().length()>0) {
			//todo - update the AppName on db
			crit.add(Restrictions.eq("region", strRegion ));
		}
		

		crit.addOrder(Order.asc(ApplicationContext.getConfig().getOrderTestcases()));
		List<LFTestCase> resultList = null;
		try {
			resultList =  crit.list();
		} catch( Exception others ) {
			others.printStackTrace();
		}
		tcm.setTestcaseMap(resultList);
		return  resultList;
		
	}
	
	public List<LFTestInputSteps> getInputStep (String criteriaField, String criteriaValue){
		if(ism.getSession() != null && ism.getSession().isOpen()){
			ism.getSession().close();
		}
		
		ism.setSession(openSession());
		Criteria crit = ism.getSession().createCriteria(LFTestInputSteps.class);
		crit.add(Restrictions.eq(criteriaField,criteriaValue));
		crit.addOrder(Order.asc("testID"));
		List<LFTestInputSteps> resultList =  crit.list();
		return  resultList;
	}
	public int getInputStepSize (String criteriaField, String criteriaValue){
		List<LFTestInputSteps> resultList = getInputStep(criteriaField,criteriaValue);
		return resultList.size();
	}


	
	public LFTestCase getTestCase(String testID) {
		if(tcm.getSession()== null || !tcm.getSession().isOpen()){
			tcm.setSession(openSession());
		}	
		return tcm.getTestCase(testID);
	}


	public void updateDB(LFTestCase testcase) throws Exception{
			if(tcm.getSession() != null && tcm.getSession().isOpen()){
				tcm.getSession().close();
			}
			
			if(lbm.getSession() != null && lbm.getSession().isOpen()){
				lbm.getSession().close();
			}
			
			lbm.setSession(openSession());
			tcm.setSession(openSession());
			Criteria crit = tcm.getSession().createCriteria(LFTestCase.class);
			crit.add(Restrictions.eq("testID",testcase.getTestID()));
			crit.addOrder(Order.asc("testID"));
			List<LFTestCase> resultList =  crit.list();
		
			if(resultList.size() == 0) {
				tcm.saveTestCase(testcase);
			} else {
				tcm.updateTestCase(resultList.get(0), testcase);
			}
 	}
	
	public void updateDB(LFTemplate template) throws Exception{
			if (tem.getSession() == null || !tem.getSession().isOpen()){
				tem.setSession(openSession());
			}
			tem.updateTemplate(template);

	}
	
	public Set<LFTestCase> getTestCasesForLabel(String labelName){
		return lbm.getTestCasesForLabel(labelName);
	}
	
	public List<LFTestCase> getTestCaseForCategoryAndLabel(String category, String labelName){
		return lbm.getTestCaseForCategoryAndLabel(category, labelName);
	}
	
//	public void updateDB(LFCategory category){
//		if (ctm.getSession() == null || !ctm.getSession().isOpen()){
//			ctm.setSession(openSession());
//		}
//		ctm.updateCategory(category);
//	}


	public TestCaseManager getTcm() {
		return tcm;
	}
	public TemplateManager getTem() {
		tem.setSession(openSession());
		return tem;
	}

	public OutputStepManager getOsm() {
		return osm;
	}
	public TestOutcomeManager getTom() {
		return tom;
	}
	public CommonOverwriteTagManager getCom() {
		return com;
	}
	public CategoryManager getCtm(){
		return ctm;
	}
	public LabelManager getLbm(){
		return lbm;
	}
	
	public InputStepManager getIsm(){
		return ism;
	}

	public String getTemplateDescription(String templateDataIndex){
		return tem.getInputTemplateMap().get(templateDataIndex).getDescription().toString();
	}
}
