package com.citigroup.liquifi.entities;

import java.util.List;

import org.hibernate.Session;

import com.citigroup.liquifi.util.HibernateUtil;

 
public class TestHarnessManager {

    public static void main(String[] args) {
    	TestHarnessManager mgr = new TestHarnessManager();

        if (args[0].equals("actions")) {
            mgr.createAndStoreLFActions("test", "test", "test");
        }
        else if (args[0].equals("list")) {
            List<?> actionsList = mgr.listLFActions();
            for (int i = 0; i < actionsList.size(); i++) {
            	LFActions actions = (LFActions) actionsList.get(i);
                System.out.println("Action Name: " + actions.getActionName() +
                                   "Action Details " + actions.getActionDetails() +
                                   "Msg Template " + actions.getMsgTemplate() +
                                   "");
            }
        }
        HibernateUtil.getSessionFactory().close();
    }

    private void createAndStoreLFActions(String actionName, String actionDetails, String msgTemplate) {

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


    private List<?> listLFActions() {

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        List<?> result = (List<?>)session.createQuery("from LFActions").list();

        session.getTransaction().commit();

        return result;
    }
    
}