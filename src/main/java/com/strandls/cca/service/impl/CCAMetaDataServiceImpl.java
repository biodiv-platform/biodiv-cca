package com.strandls.cca.service.impl;

import com.google.inject.Inject;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.service.CCAMetaDataService;
import com.strandls.cca.util.AbstractService;

/**
 * 
 * @author vilay
 *
 */
public class CCAMetaDataServiceImpl extends AbstractService<CCAData> implements CCAMetaDataService {
	
	@Inject
	public CCAMetaDataServiceImpl() {
		super(CCAData.class);
	}

	@Override
	public CCAData getCCAMetaDataById(Long ccaId) {
		CCAData cca = new CCAData();
		return getJacksonDBCollection().findOne(cca);
	}
}
