package com.citigroup.liquifi.autopilot.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.citigroup.liquifi.entities.LFCommonOverwriteTag;

public class CommonOverwriteTagManager {
	private Session session;
	private Map <String, List<LFCommonOverwriteTag> > commonOverwriteTagMap = new TreeMap<String, List<LFCommonOverwriteTag>>();
	private Transaction tx;
	
	public CommonOverwriteTagManager(Session session){
		if (session != null){
			this.session = session;
		}
	}
	
	public void commit(){
		session.flush();
		session.close();
	}
	
	public void rollback(){
		session.close();
	}
	
	public void saveTag(LFCommonOverwriteTag tag){
		tx = session.beginTransaction();					
		session.clear();
		session.save(tag);
		tx.commit();
		session.flush();
	}
	
	public void updateTag(LFCommonOverwriteTag tag){
		session.update(tag);
		//session.flush();
	}
	
	public void deleteTag(LFCommonOverwriteTag tag){
		session.delete(tag);
		//session.flush();
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	public void loadAllCommonOverwriteTagFromDB (){	
		Query q = session.createQuery("from LFCommonOverwriteTag where IsSmartTagEntry is null order by CommonOverwriteTagListName, TagID");
		//Query q = session.createQuery("from LFCommonOverwriteTag where IsFunction is null");
		List<LFCommonOverwriteTag> listTag = q.list();
		
//		Query q = session.createQuery("from LFCommonOverwriteTag");
		//get a list of CommonOverwriteTagListName(unique)
//		Query q2 = session.createQuery("distinct CommonOverwriteTagListName from LFCommonOverwriteTag order by CommonOverwriteTagListName");
//		List<String> listLocalName = q2.list();
		
		commonOverwriteTagMap.clear();
		for (LFCommonOverwriteTag tag : listTag) {
			if (tag != null) {
				String strCommonOverwriteTagListName = tag.getCommonOverwriteTagListName();
				if ( commonOverwriteTagMap.containsKey(strCommonOverwriteTagListName)) {
					commonOverwriteTagMap.get(strCommonOverwriteTagListName).add(tag);
				}
				else {
					List<LFCommonOverwriteTag> listTagEmpty = new ArrayList<LFCommonOverwriteTag> ();
					listTagEmpty.add(tag);
					commonOverwriteTagMap.put(strCommonOverwriteTagListName, listTagEmpty);
				}
			}

		}
		
		session.close();
		
	}

	public Map<String, List<LFCommonOverwriteTag>> getCommonOverwriteTagMap() {
		return commonOverwriteTagMap;
	}
}
