package com.citigroup.liquifi.autopilot.db;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.citigroup.liquifi.entities.LFTopic;

public class TopicManager {
	private Session session;
	
	public TopicManager(Session session){
		this.session = session;
	}
	
	public void saveTopic(LFTopic topic){
		session.save(topic);
		session.flush();
	}
	
	public void updateTopic(LFTopic topic){
		session.update(topic);
		session.flush();
	}
	
	public void deleteTopic (LFTopic topic){
		session.delete(topic);
		session.flush();
	}

	public List<LFTopic> getAllTopics() {
		Query q = session.createQuery("from LFTopic");
		return q.list();
	}
}
