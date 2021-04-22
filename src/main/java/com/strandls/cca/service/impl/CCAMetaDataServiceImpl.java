package com.strandls.cca.service.impl;

import com.google.inject.Inject;
import com.mongodb.DB;
import com.strandls.cca.pojo.CCAMetaData;
import com.strandls.cca.service.CCAMetaDataService;
import com.strandls.cca.util.AbstractService;

import net.vz.mongodb.jackson.JacksonDBCollection;

/**
 * 
 * @author vilay
 *
 */
public class CCAMetaDataServiceImpl extends AbstractService<CCAMetaData> implements CCAMetaDataService {
	
	@Inject
	public CCAMetaDataServiceImpl(DB db) {
		super(CCAMetaData.class, db);
	}

	@Override
	public CCAMetaData getCCAMetaDataById(Long ccaId) {
		CCAMetaData ccaMetaData = new CCAMetaData();
		return getJacksonDBCollection().findOne(ccaMetaData);
	}

	@Override
	public CCAMetaData saveCCAMetaData(CCAMetaData ccaMetaData) {
		JacksonDBCollection<CCAMetaData, String> collection = getJacksonDBCollection();
		return collection.insert(ccaMetaData).getSavedObject();
		//return collection.save(ccaMetaData).getSavedObject();
	}
}
