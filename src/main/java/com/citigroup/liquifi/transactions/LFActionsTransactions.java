package com.citigroup.liquifi.transactions;
/**
 * This class encapsulates the database transactions for LFActions.
 * @author ac26780
 */
import java.util.List;

import org.hibernate.Session;

import com.citigroup.liquifi.entities.LFActions;
import com.citigroup.liquifi.util.HibernateUtil;


 
public class LFActionsTransactions {
	
	public LFActionsTransactions() {
		
	}

    public void createAndStoreLFActions(String actionName, String actionDetails, String msgTemplate) {

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        LFActions lfActions = new LFActions();
        
        lfActions.setActionName(actionName);
        lfActions.setActionDetails(actionDetails);
        lfActions.setMsgTemplate(msgTemplate);

        session.save(lfActions);

        session.getTransaction().commit();

        return;
    }
    
    public List<Object> listLFActions() {

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        List<Object> result = session.createQuery("from LFActions").list();

        session.getTransaction().commit();

        return result;
    }
    
    public static void main(String[] args) {
    	LFActionsTransactions mgr = new LFActionsTransactions();

        if (args[0].equals("actions")) {
            mgr.createAndStoreLFActions("test", "test", "test");
        }
        else if (args[0].equals("list")) {
            List<Object> actionsList = mgr.listLFActions();
            for (int i = 0; i < actionsList.size(); i++) {
            	LFActions actions = (LFActions) actionsList.get(i);
                System.out.println("Action Name: " + actions.getActionName() +
                                   " Action Details " + actions.getActionDetails() +
                                   " Msg Template " + actions.getMsgTemplate() +
                                   "");
            }
        }
        HibernateUtil.getSessionFactory().close();
    }
    
}
