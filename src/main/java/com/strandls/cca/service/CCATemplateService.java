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

	public CCATemplate update(HttpServletRequest request, CCATemplate ccaMetaData);

	public CCATemplate save(HttpServletRequest request, CCATemplate ccaMasterField);

	public List<CCATemplate> getAllCCATemplate(HttpServletRequest request, Platform plateform, String language);

	public CCATemplate remove(HttpServletRequest request, String shortName);

	public CCATemplate deepRemove(HttpServletRequest request, String shortName);

	public CCATemplate revoke(HttpServletRequest request, String shortName);

}
