package com.strandls.cca.service;

import java.util.List;

import com.strandls.cca.pojo.CCATemplate;

/**
 * 
 * @author vilay
 *
 */
public interface CCATemplateService {

	public CCATemplate getCCAByShortName(String ccaId);

	public CCATemplate update(CCATemplate ccaMetaData);

	public CCATemplate save(CCATemplate ccaMasterField);

	public List<CCATemplate> getAllCCATemplate();

	public CCATemplate remove(String shortName);

}
