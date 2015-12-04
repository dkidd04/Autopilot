package com.citigroup.liquifi.autopilot.db;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.citigroup.liquifi.entities.LFCategory;


public class CategoryManager {
	
	private Session session;
	private List<LFCategory> categories;
	private Transaction tx;
	
	public CategoryManager(Session session){
		this.session = session;
	}
	
	public void saveCategory(LFCategory category) throws Exception{
		try{
			tx = session.beginTransaction();
			session.clear();
			session.save(category);
			session.flush();
			//session.refresh(testcase);
			tx.commit();
		}catch (Exception e){
			if (tx != null) tx.rollback();
			throw e;
		}

	}

	public void updateCategory(LFCategory orig, LFCategory category) throws Exception{
		try{
			tx = session.beginTransaction();					
			session.clear();
			session.delete(orig);
			session.flush();
			session.clear();
			session.replicate(category, ReplicationMode.LATEST_VERSION);
			session.flush();
			tx.commit();
		}catch(Exception e){
			if(tx != null) tx.rollback();

			throw new Exception(e);
		}

	}

	public void deleteCategory (LFCategory category) throws Exception{
		try{
			tx = session.beginTransaction();
			session.clear();
			session.delete(category);
			session.flush();
			tx.commit();
		}catch(Exception e){
			if(tx != null) tx.rollback();
			throw e;
		}
//		finally{
//			session.close();
//		}
	}
	

	public Session getSession() {
		return session;
	}
	
	public void setSession(Session session){
		this.session = session;
	}
	
	public void loadCategoryFromDB(){
		Query q = session.createQuery("from LFCategory");
		setCategory(q.list());
	}
	
	public List<String> loadReleaseNumFromDB(){
//		tx = session.beginTransaction();
//		session.clear();
		String strRegion = System.getProperty("region");
		String strApplication = System.getProperty("application");
		Query q = session.createSQLQuery("select distinct ReleaseNum from LFTestCase where Region = '" + strRegion + "' and AppName = '" + strApplication + "'");
//		tx.commit();
//		session.flush();
		return q.list();
	}
	
	public List<String> loadJiraNumFromDB(){
//		tx = session.beginTransaction();
//		session.clear();
		String strRegion = System.getProperty("region");
		String strApplication = System.getProperty("application");
		Query q = session.createSQLQuery("select distinct JiraNum from LFTestCase where Region = '" + strRegion + "' and AppName = '" + strApplication + "'");
		System.out.println("select distinct JiraNum from LFTestCase where Region = '" + strRegion + "' and AppName = '" + strApplication + "'");
//		tx.commit();
//		session.flush();
		return q.list();
	}
	
	public void setCategory(List<LFCategory> categories){
		this.categories = categories;
	}
	
	public List<LFCategory> getCategories(){
		return this.categories;
	}
	
	public List<LFCategory> getCategoriesFromDB(){
		loadCategoryFromDB();
		return this.categories;
	}
	
	
}

