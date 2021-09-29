package com.strandls.cca.service.impl;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.pac4j.core.profile.CommonProfile;

import com.mongodb.DB;
import com.strandls.authentication_utility.util.AuthUtil;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCAFieldValues;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.enumtype.DataType;
import com.strandls.cca.pojo.upload.FileUploadFactory;
import com.strandls.cca.pojo.upload.IFileUpload;
import com.strandls.cca.service.CCADataService;
import com.strandls.cca.service.CCATemplateService;
import com.strandls.cca.util.AbstractService;

public class CCADataServiceImpl extends AbstractService<CCAData> implements CCADataService {

	@Inject
	private CCATemplateService ccaTemplateService;

	@Inject
	public CCADataServiceImpl(DB db) {
		super(CCAData.class, db);
	}

	@Override
	public List<CCAData> getAllCCA(HttpServletRequest request) {
		return getAll();
	}

	@Override
	public List<CCAData> uploadCCADataFromFile(HttpServletRequest request, FormDataMultiPart multiPart)
			throws IOException {
		CommonProfile profile = AuthUtil.getProfileFromRequest(request);
		IFileUpload fileUpload = FileUploadFactory.getFileUpload(multiPart, "csv", profile.getId());
		return fileUpload.upload(ccaTemplateService, this);
	}

	@Override
	public CCAData saveOrUpdate(HttpServletRequest request, CCAData ccaData) {

		String shortName = ccaData.getShortName();
		CCATemplate ccaTemplate = ccaTemplateService.getCCAByShortName(shortName);

		validateData(ccaData, ccaTemplate);

		ccaData = save(ccaData);
		return ccaData;
	}

	@Override
	public void validateData(CCAData ccaData, CCATemplate ccaTemplate) {
		validateDataUtil(ccaData.getCcaFieldValues(), ccaTemplate.getFields());
	}

	private void validateDataUtil(List<CCAFieldValues> ccaFieldValues, List<CCAField> fields) {
		if (ccaFieldValues.size() != fields.size())
			throw new IllegalArgumentException("Invalid template mapping");

		if (ccaFieldValues.isEmpty())
			return;

		for (int i = 0; i < ccaFieldValues.size(); i++) {
			CCAField field = fields.get(i);
			CCAFieldValues fieldValue = ccaFieldValues.get(i);

			validateWithRespectToField(fieldValue, field);

			validateDataUtil(fieldValue.getChildren(), field.getChildren());
		}
	}

	private void validateWithRespectToField(CCAFieldValues fieldValue, CCAField field) {
		if (fieldValue.getFieldId() == null)
			throw new IllegalArgumentException("FieldId can't be null");

		if (!fieldValue.getFieldId().equals(field.getFieldId()))
			throw new IllegalArgumentException("Invalid template mapping");

		if (field.getValidation().getIsRequired().booleanValue()
				&& (fieldValue.getValue() == null || fieldValue.getValue().isEmpty())) {
			throw new IllegalArgumentException("Field is required");
		}

		DataType fieldType = field.getType();
		
		fieldType.validate(fieldValue, field);
		
	}

	@Override
	public CCAData remove(String id) {
		CCAData ccaData = getById(id);
		return remove(ccaData);
	}

}
