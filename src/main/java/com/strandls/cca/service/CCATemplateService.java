package com.strandls.cca.service;

import java.util.List;

import com.strandls.cca.pojo.CCATemplate;

/**
 * 
 * @author vilay
 *
 */
public interface CCATemplateService {

	public CCATemplate getCCAByTemplateId(String ccaId);

	public CCATemplate saveOrUpdate(CCATemplate ccaMetaData);

	public List<CCATemplate> getAll();

	public CCATemplate update(CCATemplate ccaMetaData);

	public List<CCATemplate> getAllCCATemplate();
	
	
}
