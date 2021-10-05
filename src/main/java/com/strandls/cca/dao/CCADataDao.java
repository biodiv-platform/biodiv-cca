package com.strandls.cca.dao;

import javax.inject.Inject;

import com.mongodb.client.MongoDatabase;
import com.strandls.cca.pojo.CCAData;

public class CCADataDao extends AbstractDao<CCAData> {

	@Inject
	public CCADataDao(MongoDatabase db) {
		super(CCAData.class, db);
	}

}
