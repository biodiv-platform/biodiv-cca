package com.strandls.cca.service.impl;

import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.strandls.cca.pojo.CCAContext;
import com.strandls.cca.service.CCAContextService;
import com.strandls.cca.util.AbstractService;

import net.vz.mongodb.jackson.JacksonDBCollection;

/**
 * 
 * @author vilay
 *
 */
public class CCAContextServiceImpl extends AbstractService<CCAContext> implements CCAContextService {

	@Inject
	public CCAContextServiceImpl(DB db) {
		super(CCAContext.class, db);
	}

	@Override
	public CCAContext getCCAContextById(String ccaId) {
		CCAContext ccaContext = new CCAContext();
		return getJacksonDBCollection().findOne(ccaContext);
	}

	public void createUniqueIndexOnConextId() {
		JacksonDBCollection<CCAContext, String> collection = getJacksonDBCollection();
		collection.ensureIndex(new BasicDBObject("contextId", 1), new BasicDBObject("unique", true));
	}

	@Override
	public CCAContext save(CCAContext context) {
		context = super.save(context);
		//createUniqueIndexOnConextId();
		return context;
	}

}
