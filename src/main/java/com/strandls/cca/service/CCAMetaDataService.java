package com.strandls.cca.service;

import com.strandls.cca.pojo.CCAMetaData;

/**
 * 
 * @author vilay
 *
 */
public interface CCAMetaDataService {

	public CCAMetaData getCCAMetaDataById(Long ccaId);
	
	public CCAMetaData saveCCAMetaData(CCAMetaData ccaMetaData);
	
}
