package com.strandls.cca.util;

import com.google.inject.Inject;
import com.mongodb.DB;

import net.vz.mongodb.jackson.JacksonDBCollection;

public abstract class AbstractService<T> {
	
	private DB db;
	
	private Class<T> collectionType;
	private JacksonDBCollection<T, String> jacksonDBCollection;
	
	@Inject
	public AbstractService(Class<T> collectionType, DB db) {
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
	
	
}
