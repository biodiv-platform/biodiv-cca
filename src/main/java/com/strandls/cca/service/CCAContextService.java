package com.strandls.cca.service;

import java.util.List;

import com.strandls.cca.pojo.CCAContext;

/**
 * 
 * @author vilay
 *
 */
public interface CCAContextService {

	public CCAContext getCCAContextById(String ccaId);

	public CCAContext save(CCAContext ccaMetaData);

	public List<CCAContext> getAll();

	public CCAContext update(CCAContext ccaMetaData);
	
	
}
