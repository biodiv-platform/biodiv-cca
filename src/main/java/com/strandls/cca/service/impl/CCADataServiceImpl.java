package com.strandls.cca.service.impl;

import com.google.inject.Inject;
import com.mongodb.DB;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.service.CCADataService;
import com.strandls.cca.util.AbstractService;

/**
 * 
 * @author vilay
 *
 */
public class CCADataServiceImpl extends AbstractService<CCAData> implements CCADataService {
	
	@Inject
	public CCADataServiceImpl(DB db) {
		super(CCAData.class, db);
	}

	@Override
	public CCAData getCCADataById(Long ccaId) {
		CCAData cca = new CCAData();
		return getJacksonDBCollection().findOne(cca);
	}
}
