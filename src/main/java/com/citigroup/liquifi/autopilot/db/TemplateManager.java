package com.citigroup.liquifi.autopilot.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Query;
import org.hibernate.Session;

import com.citigroup.liquifi.entities.LFTemplate;
import com.citigroup.liquifi.util.DBUtil;

public class TemplateManager {
	private Session session;
	private Map<String, LFTemplate> inputTemplateMap = new TreeMap<String, LFTemplate> ();
	private Map<String, LFTemplate> outputTemplateMap = new TreeMap<String, LFTemplate> ();
	private Map<String, LFTemplate> allTemplateMap = new TreeMap<String, LFTemplate> ();
	private List<LFTemplate> allTemplates = new ArrayList<LFTemplate>();
	
	public TemplateManager(Session session){
		if (session != null){
			this.session = session;
		}
	}
	
	public void saveTemplate(LFTemplate template){
		session.save(template);	
		session.flush();
		
		// Keep map up-to-date
		setTemplate(template);
	}
	
	public void updateTemplate (LFTemplate template) throws Exception{
		session.update(template);
		session.flush();
		
		// Keep map up-to-date
		setTemplate(template);
	}
	
	public void deleteTemplate (LFTemplate template){
		session.delete(template);
		session.flush();
		
		removeTemplate(template);
		// remove template from map - at the moment delete doesn't work
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
	
	public void loadAllTemplateFromDB (){
		String strApplication = System.getProperty("application");
		String strQuery = "";

		if (strApplication!=null && strApplication.trim().length()>0) {
			//todo - update the AppName on db
			if (strApplication.equalsIgnoreCase("AEE")) {
				strApplication="LIQUIFI";				
			}
			strQuery = "from LFTemplate as templates where upper(templates.appName) ='"+strApplication.toUpperCase()+"' order by templates.templateName  ";			
		}
		else {
			strQuery = "from LFTemplate as templates order by templates.templateName ";
		}
		
		Query q = session.createQuery(strQuery);

		List<LFTemplate> templateList = q.list();
		
		for(LFTemplate template  : templateList){
			setTemplate(template);
		}
		session.close();
		
	}
	
	private void setTemplate(LFTemplate template) {
		allTemplateMap.put(template.getTemplateName(), template);
		if(template.getIsInput() == 'Y'){
			inputTemplateMap.put(template.getTemplateName(), template);
		}else{
			outputTemplateMap.put(template.getTemplateName(), template);
		}
	}
	
	private void removeTemplate(LFTemplate template) {
		allTemplateMap.remove(template.getTemplateName());
		if(template.getIsInput() == 'Y'){
			inputTemplateMap.remove(template.getTemplateName());
		}else{
			outputTemplateMap.remove(template.getTemplateName());
		}
	}


	public void setInputTemplateMap (List<LFTemplate> templateList){
		for(LFTemplate template : templateList){
			inputTemplateMap.put(template.getTemplateName(), template);
		}
	}
	
	public void setOutputTemplateMap (List<LFTemplate> templateList){
		for(LFTemplate template : templateList){
			outputTemplateMap.put(template.getTemplateName(), template);
		}
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Map<String, LFTemplate> getInputTemplateMap() {
		return inputTemplateMap;
	}

	public void setInputTemplateMap(Map<String, LFTemplate> inputTemplateMap) {
		this.inputTemplateMap = inputTemplateMap;
	}

	public Map<String, LFTemplate> getOutputTemplateMap() {
		return outputTemplateMap;
	}

	public void setOutputTemplateMap(Map<String, LFTemplate> outputTemplateMap) {
		this.outputTemplateMap = outputTemplateMap;
	}

	public List<LFTemplate> getAllTemplates() {
		return allTemplates;
	}

	public Map<String, LFTemplate> getAllTemplateMap() {
		if(allTemplateMap.size() == 0) {
			DBUtil.getInstance().getTem().loadAllTemplateFromDB();
		}
		return allTemplateMap;
	}

	public void setAllTemplateMap(Map<String, LFTemplate> allTemplateMap) {
		this.allTemplateMap = allTemplateMap;
	}

	
	
}
