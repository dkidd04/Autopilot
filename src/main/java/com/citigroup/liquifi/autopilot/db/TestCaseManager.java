package com.citigroup.liquifi.autopilot.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;
import com.citigroup.liquifi.entities.LFLabel;
import com.citigroup.liquifi.entities.LFTestCase;

public class TestCaseManager {
	private Session session;
	private Transaction tx;
	private Map<String, LFTestCase> testcaseMap = new HashMap<String, LFTestCase>();
	private List<String> appName;
	private List<String> category;

	public TestCaseManager(Session session) {
		this.session = session;
	}

	public TestCaseManager() {
	}

	public void saveTestCase(LFTestCase testcase) throws Exception {
		try {
			tx = session.beginTransaction();
			session.clear();
			session.save(testcase);
			session.flush();
			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			throw e;
		}

	}

	public void updateTestCase(LFTestCase orig, LFTestCase testcase)
			throws Exception {
		try {
			tx = session.beginTransaction();
			session.clear();
			session.delete(orig);
			session.flush();
			session.clear();
			session.replicate(testcase, ReplicationMode.LATEST_VERSION);
			session.flush();
			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();

			throw new Exception(e);
		} finally {
		}
	}

	public void deleteTestCase(LFTestCase testcase) throws Exception {
		try {
			tx = session.beginTransaction();
			session.clear();
			session.delete(testcase);
			session.flush();
			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			throw e;
		}
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public LFTestCase getTestCase(String testID) {
		Query q = session.createQuery("from LFTestCase where testID = '" + testID + "'");
		// TestID is PK so the query should only return one item
		if (q.list().size() > 0) {
			return (LFTestCase) q.list().get(0);
		} else {
			return null;
		}
	}

	public List<String> getTestCaseList(String criteria) {
		List<?> tcList;
		List<String> tCaseIDList = new ArrayList<String>();

		try {
			SQLQuery q = session.createSQLQuery(criteria);
			q.addEntity(LFTestCase.class);
			if (q.list().size() > 0) {
				tcList = q.list();

				for (Object tCase : tcList) {
					tCaseIDList.add(((LFTestCase) tCase).getTestID());
				}

				if (tCaseIDList != null && tCaseIDList.size() != 0){
					return tCaseIDList;
				}else{
					return null;
				}
			} else {
				return null;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return tCaseIDList;
		}
	}

	public List<String> getTestCaseListByLabel(String labelCriteria) {
		List<?> labelList;
		List<String> tCaseIDList = new ArrayList<String>();

		labelCriteria = labelCriteria.replace("," , "','");

		try {
			Query q = session.createQuery("FROM LFLabel WHERE label IN ('" +  labelCriteria + "')");

			if (q.list().size() > 0) {
				labelList = q.list();

				for (Object obj : labelList) {

					for (LFTestCase tCase : ((LFLabel) obj).getTestcases()) {

						if (tCase.getActive() == 'Y') {
							tCaseIDList.add(tCase.getTestID());
						}
					}
				}

				if (tCaseIDList != null && tCaseIDList.size() != 0){
					return tCaseIDList;
				}else{
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return tCaseIDList;
		}
	}

	public Map<String, LFTestCase> getTestcaseMap() {
		return testcaseMap;
	}

	public void setTestcaseMap(Map<String, LFTestCase> testcaseMap) {
		this.testcaseMap = testcaseMap;
	}

	public void setTestcaseMap(List<LFTestCase> testcaseList) {
		testcaseMap.clear();
		for (LFTestCase testcase : testcaseList) {
			testcaseMap.put(testcase.getTestID(), testcase);
		}
	}

	public List<String> getAppName() {
		return appName;
	}

	public void setAppName(List<String> appName) {
		this.appName = appName;
	}

	public List<String> getCategory() {
		return category;
	}

	public void setCategory(List<String> category) {
		this.category = category;
	}

	public void loadAppNameFromDB() {
		String strApplication = System.getProperty("application");
		String strQuery = "";

		if (strApplication != null && strApplication.trim().length() > 0) {
			// todo - update the AppName on db
			if (strApplication.equalsIgnoreCase("AEE")) {
				strApplication = "LIQUIFI";
			}

			strQuery = "select appName from LFAppName as appNames where upper(appNames.appName) ='"
					+ strApplication.toUpperCase() + "'";
			// strQuery = "select appName from LFAppName";
		} else {
			strQuery = "select appName from LFAppName";
		}

		Query q = session.createQuery(strQuery);
		setAppName(q.list());
	}

	public void addLabelToTestCase(String label, String testID)
			throws Exception {

		try {
			tx = session.beginTransaction();
			session.clear();
			LFTestCase testcase = (LFTestCase) session.load(LFTestCase.class,
					testID);
			LFLabel lb = (LFLabel) session.load(LFLabel.class, label);
			testcase.getLabelSet().add(lb);
			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			throw e;
		} finally {
			session.flush();
			// session.close();
		}
	}

	public void removeTestCaseFromLabel(String label, String testID)
			throws Exception {

		try {
			tx = session.beginTransaction();
			session.clear();
			LFTestCase testcase = (LFTestCase) session.load(LFTestCase.class,
					testID);
			LFLabel lb = (LFLabel) session.load(LFLabel.class, label);
			testcase.getLabelSet().remove(lb);
			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			throw e;
		} finally {
			session.flush();
			// session.close();
		}
	}

	// public Set<LFLabel> getLabelsForTestCase(String testID){
	// LFTestCase testcase = (LFTestCase) session.load(LFTestCase.class,
	// testID);
	// //System.out.println("here, num of labels is: " +
	// testcase.getLabelSet().size());
	// return testcase.getLabelSet();
	//
	// }

	public List<LFTestCase> getTestcasesForCategory(String category) {

		tx = session.beginTransaction();
		session.clear();

		Criteria crit = session.createCriteria(LFTestCase.class);
		crit.add(Restrictions.eq("category", category));
		//
		// application and region are specified at app start time.
		// mask these, as otherwise the gui is very arbitrary.
		//

		// add the criteria for application
		String strApplication = System.getProperty("application");
		if (strApplication != null && strApplication.trim().length() > 0) {
			// todo - update the AppName on db
			if (strApplication.equalsIgnoreCase("AEE")) {
				strApplication = "LIQUIFI";
			}
			crit.add(Restrictions.eq("appName", strApplication));
		}
		// add the criteria for region
		String strRegion = System.getProperty("region");
		if (strRegion != null && strRegion.trim().length() > 0) {
			// todo - update the AppName on db
			crit.add(Restrictions.eq("region", strRegion));
		}

		String order = ApplicationContext.getConfig().getOrderTestcases() != null ? ApplicationContext
				.getConfig().getOrderTestcases() : "testID";

				crit.addOrder(Order.asc(order));
				List<LFTestCase> resultList = null;
				try {
					resultList = crit.list();
					tx.commit();
				} catch (Exception others) {
					others.printStackTrace();
				} finally {
					session.flush();
				}
				setTestcaseMap(resultList);
				return resultList;

				// tx = session.beginTransaction();
				// Query q = session.createQuery("from LFTestCase where Category = '" +
				// category + "'");
				// tx.commit();
				// return q.list();

	}

	public List<LFTestCase> getTestcasesForReleaseNum(String releaseNum) {

		tx = session.beginTransaction();
		session.clear();

		Criteria crit = session.createCriteria(LFTestCase.class);
		crit.add(Restrictions.eq("releaseNum", releaseNum));
		//
		// application and region are specified at app start time.
		// mask these, as otherwise the gui is very arbitrary.
		//

		// add the criteria for application
		String strApplication = System.getProperty("application");
		if (strApplication != null && strApplication.trim().length() > 0) {
			// todo - update the AppName on db
			if (strApplication.equalsIgnoreCase("AEE")) {
				strApplication = "LIQUIFI";
			}
			crit.add(Restrictions.eq("appName", strApplication));
		}
		// add the criteria for region
		String strRegion = System.getProperty("region");
		if (strRegion != null && strRegion.trim().length() > 0) {
			// todo - update the AppName on db
			crit.add(Restrictions.eq("region", strRegion));
		}

		String order = ApplicationContext.getConfig().getOrderTestcases() != null ? ApplicationContext
				.getConfig().getOrderTestcases() : "testID";

				crit.addOrder(Order.asc(order));
				List<LFTestCase> resultList = null;
				try {
					resultList = crit.list();
					tx.commit();
				} catch (Exception others) {
					others.printStackTrace();
				} finally {
					session.flush();
				}
				setTestcaseMap(resultList);
				return resultList;

				// tx = session.beginTransaction();
				// Query q = session.createQuery("from LFTestCase where Category = '" +
				// category + "'");
				// tx.commit();
				// return q.list();

	}

	public List<LFTestCase> getTestcasesForJiraNum(String jiraNum) {

		tx = session.beginTransaction();
		session.clear();

		Criteria crit = session.createCriteria(LFTestCase.class);
		crit.add(Restrictions.eq("jiraNum", jiraNum));
		//
		// application and region are specified at app start time.
		// mask these, as otherwise the gui is very arbitrary.
		//

		// add the criteria for application
		String strApplication = System.getProperty("application");
		if (strApplication != null && strApplication.trim().length() > 0) {
			// todo - update the AppName on db
			if (strApplication.equalsIgnoreCase("AEE")) {
				strApplication = "LIQUIFI";
			}
			crit.add(Restrictions.eq("appName", strApplication));
		}
		// add the criteria for region
		String strRegion = System.getProperty("region");
		if (strRegion != null && strRegion.trim().length() > 0) {
			// todo - update the AppName on db
			crit.add(Restrictions.eq("region", strRegion));
		}

		String order = ApplicationContext.getConfig().getOrderTestcases() != null ? ApplicationContext
				.getConfig().getOrderTestcases() : "testID";

				crit.addOrder(Order.asc(order));
				List<LFTestCase> resultList = null;
				try {
					resultList = crit.list();
					tx.commit();
				} catch (Exception others) {
					others.printStackTrace();
				} finally {
					session.flush();
				}
				setTestcaseMap(resultList);
				return resultList;

				// tx = session.beginTransaction();
				// Query q = session.createQuery("from LFTestCase where Category = '" +
				// category + "'");
				// tx.commit();
				// return q.list();

	}

	public List<LFTestCase> getAllTestcases() {

		tx = session.beginTransaction();
		session.clear();

		Criteria crit = session.createCriteria(LFTestCase.class);
		//
		// application and region are specified at app start time.
		// mask these, as otherwise the gui is very arbitrary.
		//

		// add the criteria for application
		String strApplication = System.getProperty("application");
		if (strApplication != null && strApplication.trim().length() > 0) {
			// todo - update the AppName on db
			if (strApplication.equalsIgnoreCase("AEE")) {
				strApplication = "LIQUIFI";
			}
			crit.add(Restrictions.eq("appName", strApplication));
		}
		// add the criteria for region
		String strRegion = System.getProperty("region");
		if (strRegion != null && strRegion.trim().length() > 0) {
			// todo - update the AppName on db
			crit.add(Restrictions.eq("region", strRegion));
		}

		String order = ApplicationContext.getConfig().getOrderTestcases() != null ? ApplicationContext
				.getConfig().getOrderTestcases() : "testID";

				crit.addOrder(Order.asc(order));
				List<LFTestCase> resultList = null;
				try {
					resultList = crit.list();
					tx.commit();
				} catch (Exception others) {
					others.printStackTrace();
				} finally {
					session.flush();
				}
				setTestcaseMap(resultList);
				return resultList;

				// tx = session.beginTransaction();
				// Query q = session.createQuery("from LFTestCase");
				// tx.commit();
				// return q.list();
	}

	public void loadCategoryFromDB() {
		tx = session.beginTransaction();
		session.clear();
		Query q = session.createQuery("select category from LFCategory");
		tx.commit();
		session.flush();
		setCategory(q.list());
	}

}
