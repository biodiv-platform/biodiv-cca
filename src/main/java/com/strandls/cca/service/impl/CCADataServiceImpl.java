package com.strandls.cca.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.pac4j.core.profile.CommonProfile;

import com.strandls.authentication_utility.util.AuthUtil;
import com.strandls.cca.dao.CCADataDao;
import com.strandls.cca.file.upload.FileUploadFactory;
import com.strandls.cca.file.upload.IFileUpload;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCAFieldValue;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.filter.IFilter;
import com.strandls.cca.service.CCADataService;
import com.strandls.cca.service.CCATemplateService;

public class CCADataServiceImpl implements CCADataService {

	@Inject
	private CCATemplateService ccaTemplateService;

	@Inject
	private CCADataDao ccaDataDao;

	@Inject
	public CCADataServiceImpl() {
		// Just for the injection purpose
	}

	@Override
	public List<CCAData> getAllCCA(HttpServletRequest request, IFilter ccaFilters) {
		return ccaDataDao.getAll(ccaFilters);
	}

	@Override
	public List<CCAData> uploadCCADataFromFile(HttpServletRequest request, FormDataMultiPart multiPart)
			throws IOException {
		CommonProfile profile = AuthUtil.getProfileFromRequest(request);
		IFileUpload fileUpload = FileUploadFactory.getFileUpload(multiPart, "csv", profile.getId());
		return fileUpload.upload(ccaTemplateService, this);
	}

	@Override
	public CCAData save(HttpServletRequest request, CCAData ccaData) {

		String shortName = ccaData.getShortName();
		CCATemplate ccaTemplate = ccaTemplateService.getCCAByShortName(shortName);

		validateData(ccaData, ccaTemplate);

		return ccaDataDao.save(ccaData);
	}
	
	@Override
	public CCAData update(HttpServletRequest request, CCAData ccaData) {

		String shortName = ccaData.getShortName();
		CCATemplate ccaTemplate = ccaTemplateService.getCCAByShortName(shortName);

		validateData(ccaData, ccaTemplate);

		return ccaDataDao.updateOne(ccaData);
	}

	@Override
	public void validateData(CCAData ccaData, CCATemplate ccaTemplate) {
		Map<String, CCAFieldValue> ccaFieldValues = ccaData.getCcaFieldValues();
		Iterator<CCAField> templateIterator = ccaTemplate.iterator();

		Map<String, CCAField> ccaFields = new HashMap<>();
		while (templateIterator.hasNext()) {
			CCAField field = templateIterator.next();
			ccaFields.put(field.getFieldId(), field);
		}

		for (Map.Entry<String, CCAFieldValue> e : ccaFieldValues.entrySet()) {
			String fieldId = e.getKey();
			CCAFieldValue fieldValue = e.getValue();
			if (fieldId == null)
				throw new IllegalArgumentException(fieldValue.getName() + " : FieldId can't be null");

			CCAField field = ccaFields.get(fieldId);
			if (field == null) {
				// The value is not part of the template
				continue;
			}

			validateWithRespectToField(fieldValue, field);
		}
	}

	private void validateWithRespectToField(CCAFieldValue fieldValue, CCAField field) {
		if (fieldValue.getFieldId() == null)
			throw new IllegalArgumentException(field.getName() + " : FieldId can't be null");

		if (!fieldValue.getFieldId().equals(field.getFieldId()))
			throw new IllegalArgumentException(field.getName() + " : Invalid template mapping");

		boolean validationWithValue = fieldValue.validate(field);

		if (!validationWithValue) {
			throw new IllegalArgumentException(
					field.getName() + " " + field.getFieldId() + " : Failed in value validation");
		}
	}

	@Override
	public CCAData remove(CCAData ccaData) {
		return ccaDataDao.remove(ccaData);
	}

	@Override
	public CCAData remove(String id) {
		CCAData data = ccaDataDao.findByProperty("_id", id);
		return remove(data);
	}

	@Override
	public List<CCAData> insertBulk(List<CCAData> ccaDatas) {
		return ccaDataDao.insertBulk(ccaDatas);
	}

}
