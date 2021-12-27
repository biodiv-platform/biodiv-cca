package com.strandls.cca.service.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.pac4j.core.profile.CommonProfile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.AggregateIterable;
import com.strandls.authentication_utility.util.AuthUtil;
import com.strandls.cca.ApiConstants;
import com.strandls.cca.CCAConfig;
import com.strandls.cca.CCAConstants;
import com.strandls.cca.dao.CCADataDao;
import com.strandls.cca.file.upload.FileUploadFactory;
import com.strandls.cca.file.upload.IFileUpload;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCAFieldValue;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.response.AggregationResponse;
import com.strandls.cca.pojo.response.CCADataList;
import com.strandls.cca.service.CCADataService;
import com.strandls.cca.service.CCATemplateService;
import com.strandls.cca.util.AuthorizationUtil;

public class CCADataServiceImpl implements CCADataService {

	@Inject
	private CCATemplateService ccaTemplateService;

	@Inject
	private CCADataDao ccaDataDao;

	@Inject
	private LogActivities logActivities;

	@Inject
	public CCADataServiceImpl() {
		// Just for the injection purpose
	}

	@Override
	public CCAData findById(Long id) {
		return ccaDataDao.findByProperty(CCAConstants.ID, id);
	}

	@Override
	public List<CCAData> getAllCCAData(HttpServletRequest request, UriInfo uriInfo) throws JsonProcessingException {
		return ccaDataDao.getAll(uriInfo, true, null);
	}

	@Override
	public AggregationResponse getMyCCADataList(HttpServletRequest request, UriInfo uriInfo)
			throws JsonProcessingException {
		return getCCADataList(request, uriInfo, true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public AggregationResponse getCCADataList(HttpServletRequest request, UriInfo uriInfo, boolean myListOnly)
			throws JsonProcessingException {
		String userId = null;
		if (myListOnly) {
			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			userId = profile.getId();
		}

		List<CCAData> ccaDatas = ccaDataDao.getAll(uriInfo, false, userId);
		List<CCADataList> ccaDataList = mergeToCCADataList(ccaDatas);

		AggregateIterable<Map> aggregation = ccaDataDao.getAggregation(uriInfo, userId);

		AggregationResponse aggregationResponse = new AggregationResponse();
		aggregationResponse.setCcaDataList(ccaDataList);
		aggregationResponse.setAggregation(aggregation.first());
		return aggregationResponse;
	}

	private List<CCADataList> mergeToCCADataList(List<CCAData> ccaDatas) {
		List<CCADataList> result = new ArrayList<>();
		for (CCAData ccaData : ccaDatas) {
			CCADataList listCard = new CCADataList(ccaData);
			result.add(listCard);
		}
		return result;
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
		CCATemplate ccaTemplate = ccaTemplateService.getCCAByShortName(shortName,
				CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE));

		if (!AuthorizationUtil.checkAuthorization(request, ccaTemplate.getPermissions(), ccaData.getId())) {
			throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
					.entity(AuthorizationUtil.UNAUTHORIZED_MESSAGE).build());
		}

		CommonProfile profile = AuthUtil.getProfileFromRequest(request);

		validateData(ccaData, ccaTemplate);

		Timestamp time = new Timestamp(new Date().getTime());
		ccaData.setCreatedOn(time);
		ccaData.setUpdatedOn(time);

		ccaData.setUserId(profile.getId());

		String desc = "Data created with template : " + ccaData.getShortName();
		logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), desc, ccaData.getId(),
				ccaData.getId(), "ccaData", ccaData.getId(), "Data created");

		ccaData.reComputeCentroid();
		return ccaDataDao.save(ccaData);
	}

	@Override
	public CCAData update(HttpServletRequest request, CCAData ccaData) {

		String shortName = ccaData.getShortName();
		CCATemplate ccaTemplate = ccaTemplateService.getCCAByShortName(shortName,
				CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE));

		validateData(ccaData, ccaTemplate);

		Timestamp time = new Timestamp(new Date().getTime());
		ccaData.setUpdatedOn(time);

		if (ccaData.getId() == null)
			throw new IllegalArgumentException("Please specify the id for cca data");

		CCAData dataInMem = ccaDataDao.getById(ccaData.getId());

		dataInMem = dataInMem.overrideFieldData(request, ccaData, logActivities);

		dataInMem.reComputeCentroid();
		return ccaDataDao.replaceOne(dataInMem);
	}

	@Override
	public void validateData(CCAData ccaData, CCATemplate ccaTemplate) {

		Map<String, CCAFieldValue> ccaFieldValues = ccaData.getCcaFieldValues();
		Map<String, CCAField> ccaFields = ccaTemplate.getAllFields();

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
	public CCAData restore(Long id) {
		return ccaDataDao.restore(id);
	}

	@Override
	public CCAData remove(Long id) {
		return ccaDataDao.remove(id);
	}

	@Override
	public CCAData deepRemove(Long id) {
		return ccaDataDao.deepRemove(id);
	}

	@Override
	public List<CCAData> insertBulk(List<CCAData> ccaDatas) {
		ccaDatas.forEach(CCAData::reComputeCentroid);
		return ccaDataDao.insertBulk(ccaDatas);
	}

}
