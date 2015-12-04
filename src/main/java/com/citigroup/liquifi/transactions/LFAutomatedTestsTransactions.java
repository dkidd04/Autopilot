package com.citigroup.liquifi.transactions;

/**
 * This class encapsulates the database transactions for LFActions.
 * @author ac26780
 */
import java.util.List;

import org.hibernate.Session;

import com.citigroup.liquifi.entities.LFAutomatedTests;
import com.citigroup.liquifi.util.HibernateUtil;


public class LFAutomatedTestsTransactions {
	
	public LFAutomatedTestsTransactions() {
		
	}

    public void createAndStoreLFAutomatedTestsTransactions(String testID, String testCategory, 
    		                        String testDesc, String appName, String region,
    		                        String releaseNum, char active) {

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        LFAutomatedTests lFAutomatedTests = new LFAutomatedTests();
        lFAutomatedTests.setTestID(testID);
        lFAutomatedTests.setTestCategory(testCategory);
        lFAutomatedTests.setTestDesc(testDesc);
        lFAutomatedTests.setAppName(appName);
        lFAutomatedTests.setRegion(region);
        lFAutomatedTests.setReleaseNumber(releaseNum);
        lFAutomatedTests.setActive(active);
        
        session.save(lFAutomatedTests);

        session.getTransaction().commit();

        return;
    }
    
    public List<Object> listLFAutomatedTests() {

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        List<Object> result = session.createQuery("from LFAutomatedTests").list();

        session.getTransaction().commit();

        return result;
    }
    
    public static void main(String[] args) {
    	LFAutomatedTestsTransactions mgr = new LFAutomatedTestsTransactions();

        if (args[0].equals("actions")) {
            mgr.createAndStoreLFAutomatedTestsTransactions("test", "test", "test", "test", "test", "test", 'y');
        }
        else if (args[0].equals("list")) {
            List<Object> lFAutomatedTestsList = mgr.listLFAutomatedTests();
            for (int i = 0; i < lFAutomatedTestsList.size(); i++) {
            	LFAutomatedTests lFAutomatedTests = (LFAutomatedTests) lFAutomatedTestsList.get(i);
                System.out.println("Test ID: " + lFAutomatedTests.getTestID() +
                                   " Test Category " + lFAutomatedTests.getTestCategory() +
                                   " Test Desc " + lFAutomatedTests.getTestDesc() +
                                   " Application Name " + lFAutomatedTests.getAppName() +
                                   " Region " + lFAutomatedTests.getRegion() +
                                   " Release Number " + lFAutomatedTests.getReleaseNumber() +
                                   " Active " + lFAutomatedTests.getActive() +
                                   "");
            }
        }
        HibernateUtil.getSessionFactory().close();
    }
    
}
