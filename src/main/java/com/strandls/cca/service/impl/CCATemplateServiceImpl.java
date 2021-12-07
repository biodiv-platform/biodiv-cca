package com.strandls.cca.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;
import org.pac4j.core.profile.CommonProfile;

import com.google.inject.Inject;
import com.strandls.authentication_utility.util.AuthUtil;
import com.strandls.cca.ApiConstants;
import com.strandls.cca.CCAConfig;
import com.strandls.cca.CCAConstants;
import com.strandls.cca.dao.CCATemplateDao;
import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.Platform;
import com.strandls.cca.service.CCATemplateService;

/**
 * 
 * @author vilay
 *
 */
public class CCATemplateServiceImpl implements CCATemplateService {

	@Inject
	private CCATemplateDao ccaTemplateDao;

	@Inject
	public CCATemplateServiceImpl() {
		// Just for the injection purpose
	}

	@Override
	public CCATemplate getCCAByShortName(String shortName, String language) {
		CCATemplate ccaTemplate = ccaTemplateDao.findByProperty(CCAConstants.SHORT_NAME, shortName);
		if (language == null || "".equals(language))
			language = CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE);
		return ccaTemplate.translate(language);
	}

	@Override
	public CCATemplate save(HttpServletRequest request, CCATemplate context) {
		CommonProfile profile = AuthUtil.getProfileFromRequest(request);

		if (context.getId() == null) {
			ObjectId id = new ObjectId();
			context.setId(id.toHexString());
		}
		CCATemplate template = ccaTemplateDao.findByProperty(CCAConstants.SHORT_NAME, context.getShortName());
		if (template != null)
			throw new IllegalArgumentException(
					"Can't create new with same short name. Either update or create new one");

		context.addUpdateTranslation(context, CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE));

		validateField(context);
		context.setCreatedOn(new Timestamp(new Date().getTime()));
		context.setUpdatedOn(new Timestamp(new Date().getTime()));
		context.setUserId(profile.getId());

		return ccaTemplateDao.save(context);

	}

	@Override
	public CCATemplate update(HttpServletRequest request, CCATemplate context) {
		CCATemplate template = ccaTemplateDao.findByProperty(CCAConstants.SHORT_NAME, context.getShortName());
		if (template == null)
			throw new IllegalArgumentException("Can't update the template, template does not exit");

		String language = context.getLanguage();
		if (language == null)
			language = CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE);

		context.addUpdateTranslation(template, language);

		validateField(context);
		context.setUpdatedOn(new Timestamp(new Date().getTime()));

		return ccaTemplateDao.replaceOne(context);
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

		CCATemplate ccaTemplate = getCCAByShortName(shortName, language);
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

		List<CCATemplate> templates = ccaTemplateDao.getAllCCATemplateWithoutFields(platform, excludeFields);

		for (CCATemplate template : templates) {
			template.translate(language);
		}

		return templates;
	}

	@Override
	public CCATemplate revoke(HttpServletRequest request, String shortName) {
		return ccaTemplateDao.revoke(shortName);
	}

	@Override
	public CCATemplate remove(HttpServletRequest request, String shortName) {
		return ccaTemplateDao.remove(shortName);
	}

	@Override
	public CCATemplate deepRemove(HttpServletRequest request, String shortName) {
		return ccaTemplateDao.deepRemoveByShortName(shortName);
	}

}
