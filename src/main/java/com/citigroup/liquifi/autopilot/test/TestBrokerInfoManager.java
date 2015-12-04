package com.citigroup.liquifi.autopilot.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.citigroup.liquifi.autopilot.db.BrokerInfoManager;
import com.citigroup.liquifi.autopilot.db.dao.BrokerInfo;

public class TestBrokerInfoManager {
	
	public Session openSession(){
		SessionFactory sf = new Configuration().configure().buildSessionFactory();
		Session session = sf.openSession();
		return session;
	}
	
	public BrokerInfo getBrokerInfo(){
		BrokerInfo brokerInfo = new BrokerInfo();
		brokerInfo.setBrokerName("LiquiFi");
		brokerInfo.setBrokerURL("eqzms3d.nam.nsroot.net");
		brokerInfo.setUserID("aeeprod");
		brokerInfo.setPassword("aeeprod");
		brokerInfo.setComments("Testing Hibernate Connectivity");
		return brokerInfo;
	}
	
	public BrokerInfo testSaveBrokerInfo(BrokerInfoManager bm){
		BrokerInfo brokerInfo = getBrokerInfo();
		bm.saveBrokerInfo(brokerInfo);
		System.out.println("Saved BrokerInfo " + brokerInfo.toString());
		return brokerInfo;
	}
	
	
	public BrokerInfo testUpdateBrokerInfo(BrokerInfoManager bm, BrokerInfo brokerInfo){
		brokerInfo.setBrokerName("BDMA");
		bm.updateBrokerInfo(brokerInfo);
		System.out.println("Update BrokerInfo " + brokerInfo.toString());
		return brokerInfo;
	}
	
	public BrokerInfo testDeleteBrokerInfo(BrokerInfoManager bm, BrokerInfo brokerInfo){
		bm.deleteBrokerInfo(brokerInfo);
		System.out.println("Delete BrokerInfo " + brokerInfo.toString());
		return brokerInfo;
	}
	
	public static void main(String args[]){
		TestBrokerInfoManager tbim = new TestBrokerInfoManager();
		Session session = tbim.openSession();
		BrokerInfoManager bm = new BrokerInfoManager (session);
		BrokerInfo brokerInfo = tbim.testSaveBrokerInfo(bm);
		brokerInfo = tbim.testUpdateBrokerInfo(bm, brokerInfo);
		tbim.testDeleteBrokerInfo(bm, brokerInfo);
		session.flush();
	}


}
