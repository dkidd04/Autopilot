package com.citigroup.liquifi.autopilot.db;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import com.citigroup.liquifi.autopilot.db.dao.BrokerInfo;

public class BrokerInfoManager {
	private Session session;
		
	public BrokerInfoManager(){
	}
	
	public BrokerInfoManager(Session session){
		if (session != null){
			this.session = session;
		}
	}
	
	public void saveBrokerInfo(BrokerInfo brokerInfo){
		session.save(brokerInfo);
		session.flush();
	}
	
	public void updateBrokerInfo (BrokerInfo brokerInfo){
		session.update(brokerInfo);
		session.flush();
	}
	
	public void deleteBrokerInfo (BrokerInfo brokerInfo){
		session.delete(brokerInfo);
		session.flush();
	}
	
	public List<BrokerInfo> getAllBrokerInfo (){
		Query q = session.createQuery("from BrokerInfo");
		return q.list();
	}

}
