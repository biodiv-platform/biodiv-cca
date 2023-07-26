package com.strandls.cca.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.strandls.activity.pojo.Activity;
import com.strandls.activity.pojo.CommentLoggingData;
import com.strandls.activity.pojo.MailData;
import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.Platform;

/**
 * 
 * @author vilay
 *
 */
public interface CCATemplateService {

	public CCATemplate getCCAByShortName(String ccaId, String language, boolean isDeleted);

	public CCATemplate pullTranslationFromMaster(HttpServletRequest request, Long templateId, String language);

	public CCATemplate update(HttpServletRequest request, CCATemplate ccaMetaData);

	public CCATemplate save(HttpServletRequest request, CCATemplate ccaMasterField);

	public List<CCAField> getFilterableFields(HttpServletRequest request, String shortName, String language);

	public List<CCATemplate> getAllCCATemplate(HttpServletRequest request, Platform plateform, String language,
			Boolean excludeFields);

	public CCATemplate remove(HttpServletRequest request, String shortName);

	public CCATemplate deepRemove(HttpServletRequest request, String shortName);

	public CCATemplate restore(HttpServletRequest request, String shortName);

	public MailData generateMailData(CCATemplate ccaTemplate, String label, String value);

	public Activity addComment(HttpServletRequest request, Long userId, CommentLoggingData commentData);



	public List<String> getFieldIds(String shortName, String language);

	public List<String> getValueFields(List<String> fieldIds);

}
