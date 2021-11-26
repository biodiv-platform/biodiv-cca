package com.strandls.cca.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.Platform;

/**
 * 
 * @author vilay
 *
 */
public interface CCATemplateService {

	public CCATemplate getCCAByShortName(String ccaId, String language);

	public CCATemplate update(CCATemplate ccaMetaData);

	public CCATemplate save(CCATemplate ccaMasterField);

	public List<CCATemplate> getAllCCATemplate(HttpServletRequest request, Platform plateform, String language);

	public CCATemplate remove(String shortName);

}
