package com.strandls.cca.util;

import java.util.List;

import com.google.inject.Inject;
import com.mongodb.DB;

import net.vz.mongodb.jackson.JacksonDBCollection;

public abstract class AbstractService<T> {
	
	private DB db;
	
	private Class<T> collectionType;
	private JacksonDBCollection<T, String> jacksonDBCollection;
	
	@Inject
	protected AbstractService(Class<T> collectionType, DB db) {
		this.collectionType = collectionType;
		this.db = db;
		this.jacksonDBCollection = getJacksonDBCollection();
	}
	
	protected JacksonDBCollection<T, String> getJacksonDBCollection() {
		if(jacksonDBCollection == null)  
			return JacksonDBCollection.wrap(db.getCollection(collectionType.getSimpleName().toLowerCase()), collectionType, String.class);
		else 
			return jacksonDBCollection;
	}
	
	public T getById(String id) {
		return getJacksonDBCollection().findOneById(id);
	}
	
	public List<T> getAll(int limit, int offset) {
		return getJacksonDBCollection().find().skip(offset).limit(limit).toArray();
	}
	
	public List<T> getAll() {
		return getJacksonDBCollection().find().toArray();
	}
	
	public T save(T t) {
		return getJacksonDBCollection().save(t).getSavedObject();
	}
	
	public T remove(T t) {
		return getJacksonDBCollection().remove(t).getSavedObject();
	}
	
	public T update(T t) {
		return getJacksonDBCollection().update(t, t).getSavedObject();
	}
}
