package com.strandls.cca.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;

import com.google.inject.Inject;
import com.strandls.cca.ApiConstants;
import com.strandls.cca.CCAConfig;
import com.strandls.cca.FieldConstants;
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
		CCATemplate ccaTemplate = ccaTemplateDao.findByProperty(FieldConstants.SHORT_NAME, shortName);
		return ccaTemplate.translate(language);
	}

	@Override
	public CCATemplate save(CCATemplate context) {
		if (context.getId() == null) {
			ObjectId id = new ObjectId();
			context.setId(id.toHexString());
		}
		CCATemplate template = ccaTemplateDao.findByProperty(FieldConstants.SHORT_NAME, context.getShortName());
		if (template != null)
			throw new IllegalArgumentException(
					"Can't create new with same short name. Either update or create new one");

		context.addUpdateTranslation(context, CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE));

		validateField(context);
		context.setCreateOn(new Timestamp(new Date().getTime()));
		context.setUpdatedOn(new Timestamp(new Date().getTime()));

		return ccaTemplateDao.save(context);

	}

	@Override
	public CCATemplate update(CCATemplate context) {
		CCATemplate template = ccaTemplateDao.findByProperty(FieldConstants.SHORT_NAME, context.getShortName());
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
			if (ccaField.getCreateOn() == null) {
				ccaField.setCreateOn(currentTime);
				ccaField.setUpdatedOn(currentTime);
			} else
				ccaField.setUpdatedOn(currentTime);

			ccaField.validate();

			if (ccaField.getChildren() == null)
				ccaField.setChildren(new ArrayList<>());
		}
	}

	@Override
	public List<CCATemplate> getAllCCATemplate(HttpServletRequest request, Platform platform) {
		return ccaTemplateDao.getAllCCATemplateWithoutFields(platform);
	}

	@Override
	public CCATemplate remove(String shortName) {
		return ccaTemplateDao.removeByShortName(shortName);
	}

}
