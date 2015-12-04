package com.citigroup.liquifi.autopilot.db;

import org.hibernate.Session;

import com.citigroup.liquifi.entities.LFOutputTag;
import com.citigroup.liquifi.entities.LFTag;

public class TagManager {
	private Session session;
	
	public TagManager(Session session){
		this.session = session;
	}
	
	public void saveTag(LFTag tag){
		session.save(tag);
		session.flush();
	}
	
	public void updateTag(LFTag tag){
		session.update(tag);
		session.flush();
	}
	
	public void deleteTag(LFTag tag){
		session.delete(tag);
		session.flush();
	}

	public Session getSession() {
		return session;
	}

	public void deleteTag(LFOutputTag tag) {
		session.delete(tag);
		session.flush();
	}
}
