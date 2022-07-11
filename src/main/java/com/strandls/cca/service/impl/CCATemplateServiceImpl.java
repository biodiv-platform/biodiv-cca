package com.strandls.cca.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.HttpHeaders;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.strandls.activity.ApiException;
import com.strandls.activity.controller.ActivitySerivceApi;
import com.strandls.activity.pojo.Activity;
import com.strandls.activity.pojo.CCAMailData;
import com.strandls.activity.pojo.CommentLoggingData;
import com.strandls.activity.pojo.MailData;
import com.strandls.authentication_utility.util.AuthUtil;
import com.strandls.cca.ApiConstants;
import com.strandls.cca.CCAConfig;
import com.strandls.cca.CCAConstants;
import com.strandls.cca.dao.CCADataDao;
import com.strandls.cca.dao.CCATemplateDao;
import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.Platform;
import com.strandls.cca.service.CCATemplateService;
import com.strandls.cca.util.AuthorizationUtil;
import com.strandls.cca.Headers;

/**
 * 
 * @author vilay
 *
 */
public class CCATemplateServiceImpl implements CCATemplateService {

	@Inject
	private CCATemplateDao ccaTemplateDao;

	@Inject
	private LogActivities logActivities;

	@Inject
	private CCADataDao ccaDataDao;

	@Inject
	private ActivitySerivceApi activityService;

	@Inject
	private Headers headers;

	private final Logger logger = LoggerFactory.getLogger(CCATemplateServiceImpl.class);

	@Inject
	public CCATemplateServiceImpl() {
		// Just for the injection purpose
	}

	@Override
	public CCATemplate getCCAByShortName(String shortName, String language, boolean isDeleted) {
		CCATemplate ccaTemplate = ccaTemplateDao.findByProperty(CCAConstants.SHORT_NAME, shortName, isDeleted);
		if (language == null || "".equals(language))
			language = CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE);
		return ccaTemplate != null ? ccaTemplate.translate(language) : null;
	}

	@Override
	public CCATemplate save(HttpServletRequest request, CCATemplate context) {
		CommonProfile profile = AuthUtil.getProfileFromRequest(request);

		CCATemplate template = ccaTemplateDao.findByProperty(CCAConstants.SHORT_NAME, context.getShortName(), false);
		if (template != null)
			throw new IllegalArgumentException(
					"Can't create new with same short name. Either update or create new one");

		context.addUpdateTranslation(context, CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE));

		validateField(context);
		context.setCreatedOn(new Timestamp(new Date().getTime()));
		context.setUpdatedOn(new Timestamp(new Date().getTime()));
		context.setUserId(profile.getId());

		CCATemplate ccaTemplate = ccaTemplateDao.save(context);

		String desc = "Template created with short Name : " + context.getShortName();
		logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), desc, context.getId(),
				context.getId(), "ccaTempate", context.getId(), "Template created", 
				generateMailData(ccaTemplate, desc, "activity:template_created"));
		
		return ccaTemplate;
	}

	@Override
	public MailData generateMailData(CCATemplate ccaTemplate, String desc, String title) {
		MailData mailData = null;
		try {
			CCAMailData ccaMailData = new CCAMailData();
			ccaMailData.setAuthorId(Long.parseLong(ccaTemplate.getUserId()));
			ccaMailData.setId(ccaTemplate.getId());
			ccaMailData.setLocation("India");
			
			Map<String, String> template = new HashMap<>();
			template.put("short_name", ccaTemplate.getShortName());
			template.put("url", "template/list");
			template.put("time", ccaTemplate.getCreatedOn().toString());
			template.put("update_time", ccaTemplate.getUpdatedOn().toString());
			
			Map<String, String> activity = new HashMap<>();
			if(desc != null && title != null) {
				activity.put("description", desc);
				activity.put("title", title);
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
			    Date date = new Date();  
				activity.put("time", formatter.format(date));
			}
			
			Map<String, Object> data = new HashMap<>();
			data.put("template", template);
			data.put("activity", activity);
			data.put("owner", ccaTemplate.getUserId());
			
			ccaMailData.setData(data);
			mailData = new MailData();
			mailData.setCcaMailData(ccaMailData);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return mailData;
	}

	@Override
	public CCATemplate pullTranslationFromMaster(HttpServletRequest request, Long templateId, String language) {
		if (language == null)
			throw new IllegalArgumentException("Please specify the language");

		CCATemplate context = ccaTemplateDao.findByProperty(CCAConstants.ID, templateId, false);
		if (context == null)
			throw new IllegalArgumentException("Template with Id : " + templateId + " doesn't exits");

		CCATemplate masterTemplate = ccaTemplateDao.findByProperty(CCAConstants.SHORT_NAME, CCAConstants.MASTER, false);
		if (masterTemplate == null)
			throw new IllegalArgumentException("No master template, create master template first");

		masterTemplate.translate(language);
		context.translate(language);
		
		context.pullTranslationFromMaster(masterTemplate);
		context.setLanguage(language);

		return context;
	}

	@Override
	public CCATemplate update(HttpServletRequest request, CCATemplate context) {
		CCATemplate template = ccaTemplateDao.findByProperty(CCAConstants.SHORT_NAME, context.getShortName(), false);
		if (template == null)
			throw new IllegalArgumentException("Can't update the template, template does not exit");

		String language = context.getLanguage();
		if (language == null)
			language = CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE);

		logActivityForUpdate(request, context, template, language);

		context.addUpdateTranslation(template, language);

		validateField(context);
		context.setUpdatedOn(new Timestamp(new Date().getTime()));

		return ccaTemplateDao.replaceOne(context);
	}

	private void logActivityForUpdate(HttpServletRequest request, CCATemplate context, CCATemplate template,
			String language) {
		Map<String, CCAField> inputFields = context.getAllFields();
		Map<String, CCAField> dbFields = template.getAllFields();

		for (Map.Entry<String, CCAField> e : dbFields.entrySet()) {
			String fieldId = e.getKey();
			CCAField dbField = e.getValue();
			if (inputFields.containsKey(fieldId)) {
				CCAField inputField = inputFields.get(fieldId);
				String desc = dbField.equals(inputField, language);
				// Condition to get the difference of the field in case of found
				if (desc != null) {
					desc = inputField.getName() + "\n" + desc;
					logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), desc, context.getId(),
							context.getId(), "ccaTempate", context.getId(), "Field updated", 
							generateMailData(ccaTemplateDao.getById(template.getId()), desc, "Field updated"));
				}
			} else {
				// This field is not available in the input.. Got deleted from the template
				String desc = dbField.getTranslations().get(CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE))
						.getName();
				logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), desc, context.getId(),
						context.getId(), "ccaTempate", context.getId(), "Field deleted", 
						generateMailData(ccaTemplateDao.getById(template.getId()), desc, "Field deleted"));
			}
		}

		// Get all the added fields here
		for (Map.Entry<String, CCAField> e : inputFields.entrySet()) {
			String fieldId = e.getKey();
			CCAField f = e.getValue();
			if (!dbFields.containsKey(fieldId)) {
				String desc = f.getName();
				logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), desc, context.getId(),
						context.getId(), "ccaTempate", context.getId(), "Field created", 
						generateMailData(ccaTemplateDao.getById(template.getId()), desc, "Field created"));
			}
		}

	}

	private void validateField(CCATemplate context) {
		Iterator<CCAField> it = context.iterator();
		while (it.hasNext()) {
			CCAField ccaField = it.next();
			// Add Id if not present
			if (ccaField.getFieldId() == null)
				ccaField.setFieldId(UUID.randomUUID().toString());

			// Update the time-stamp
			Timestamp currentTime = new Timestamp(new Date().getTime());
			if (ccaField.getCreatedOn() == null) {
				ccaField.setCreatedOn(currentTime);
				ccaField.setUpdatedOn(currentTime);
			} else
				ccaField.setUpdatedOn(currentTime);

			ccaField.validate();

			if (ccaField.getChildren() == null)
				ccaField.setChildren(new ArrayList<>());
		}
	}

	@Override
	public List<CCAField> getFilterableFields(HttpServletRequest request, String shortName, String language) {
		if (shortName == null || "".equals(shortName))
			shortName = CCAConstants.MASTER;

		CCATemplate ccaTemplate = getCCAByShortName(shortName, language, false);
		Iterator<CCAField> it = ccaTemplate.iterator();
		List<CCAField> ccaFields = new ArrayList<>();
		while (it.hasNext()) {
			CCAField ccaField = it.next();
			if (ccaField.getIsFilterable().booleanValue())
				ccaFields.add(ccaField);
		}
		return ccaFields;
	}

	@Override
	public List<CCATemplate> getAllCCATemplate(HttpServletRequest request, Platform platform, String language,
			Boolean excludeFields) {

		if (language == null)
			language = CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE);

		List<String> permissions = AuthorizationUtil.getRoles(request);

		List<CCATemplate> templates = ccaTemplateDao.getAllCCATemplateWithoutFields(permissions, platform,
				excludeFields);

		for (CCATemplate template : templates) {
			template.translate(language);
		}

		return templates;
	}

	@Override
	public CCATemplate restore(HttpServletRequest request, String shortName) {
		CCATemplate ccaTemplate = ccaTemplateDao.restore(shortName);
		if(Boolean.FALSE.equals(ccaTemplate.getIsDeleted())) {
			ccaDataDao.removeOrRestoreManyCCDataByShortName(shortName, false);
		}
		return ccaTemplate;
	}

	@Override
	public CCATemplate remove(HttpServletRequest request, String shortName) {
		CCATemplate ccaTemplate = ccaTemplateDao.remove(shortName);
		if(Boolean.TRUE.equals(ccaTemplate.getIsDeleted())) {
			ccaDataDao.removeOrRestoreManyCCDataByShortName(shortName, true);
		}

		logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), ccaTemplate.getDescription(), 
				ccaTemplate.getId(), ccaTemplate.getId(), "ccaTempate", ccaTemplate.getId(), 
				"CCA Template Deleted", generateMailData(ccaTemplate, null, null));
		
		return ccaTemplate;
	}

	@Override
	public CCATemplate deepRemove(HttpServletRequest request, String shortName) {
		return ccaTemplateDao.deepRemoveByShortName(shortName);
	}

	@Override
	public Activity addComment(HttpServletRequest request, Long userId, CommentLoggingData commentData) {
		CCATemplate ccaTemplate = ccaTemplateDao.getById(commentData.getRootHolderId());
		if(ccaTemplate == null) {
			throw new NotFoundException("Not found template with id : " + commentData.getRootHolderId());
		}
		
		commentData.setMailData(generateMailData(ccaTemplate, commentData.getBody(), "Commented"));
		activityService = headers.addActivityHeader(activityService, request.getHeader(HttpHeaders.AUTHORIZATION));
		Activity activity = null;
		try {
			activity = activityService.addComment("cca", commentData);
		} catch (ApiException e) {
			e.printStackTrace();
		}
		
		return activity;
	}

}
