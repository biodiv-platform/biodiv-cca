package com.strandls.cca.service.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
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
import com.strandls.cca.pojo.FieldType;
import com.strandls.cca.pojo.fields.value.FileFieldValue;
import com.strandls.cca.pojo.fields.value.FileMeta;
import com.strandls.cca.pojo.response.AggregationResponse;
import com.strandls.cca.pojo.response.CCADataList;
import com.strandls.cca.pojo.response.MapInfo;
import com.strandls.cca.pojo.response.SubsetCCADataList;
import com.strandls.cca.service.CCADataService;
import com.strandls.cca.service.CCATemplateService;
import com.strandls.cca.util.AuthorizationUtil;
import com.strandls.cca.util.CCAUtil;

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
	public CCAData findById(Long id, String language) {
		CCAData ccaData = ccaDataDao.findByProperty(CCAConstants.ID, id, false);

		if (language == null)
			language = CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE);
		CCATemplate template = ccaTemplateService.getCCAByShortName(ccaData.getShortName(), language, false);

		ccaData.translate(template);
		return ccaData;
	}

	@Override
	public List<CCAData> getAllCCAData(HttpServletRequest request, UriInfo uriInfo, Boolean isDeletedData) throws JsonProcessingException {
		MultivaluedMap<String, String> queryParameter = uriInfo.getQueryParameters();
		String userId = queryParameter.containsKey(CCAConstants.USER_ID) ? queryParameter.get(CCAConstants.USER_ID).get(0): null;
		return ccaDataDao.getAll(uriInfo, false, userId, isDeletedData);
	}

	@Override
	public List<CCAData> getCCADataByShortName(HttpServletRequest request, UriInfo uriInfo, String shortName, Boolean isDeletedData)
			throws JsonProcessingException {
		List<CCAData> temp = this.getAllCCAData(request, uriInfo, isDeletedData);
		List<CCAData> result = new ArrayList<>();
		for(int i = 0; i < temp.size(); i++) {
			if(temp.get(i).getShortName().equals(shortName)) {
				result.add(temp.get(i));
			}
		}

		return result;
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
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		if (myListOnly) {
			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			userId = profile.getId();
		} else if(queryParams.containsKey(CCAConstants.USER_ID)) {
			userId = queryParams.get(CCAConstants.USER_ID).get(0);
		}

		List<CCAData> ccaDatas = ccaDataDao.getAll(uriInfo, false, userId, false);

		String language = queryParams.getFirst(CCAConstants.LANGUAGE);
		if (language == null)
			language = CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE);

		List<CCADataList> ccaDataList = mergeToCCADataList(ccaDatas, language);

		AggregateIterable<Map> aggregation = ccaDataDao.getAggregation(uriInfo, userId);

		AggregationResponse aggregationResponse = new AggregationResponse();
		aggregationResponse.setCcaDataList(ccaDataList);
		aggregationResponse.setAggregation(aggregation.first());
		return aggregationResponse;
	}

	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Object> getCCADataAggregation(HttpServletRequest request, UriInfo uriInfo, boolean myListOnly)
			throws JsonProcessingException {
		String userId = null;
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		if (myListOnly) {
			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			userId = profile.getId();
		} else if(queryParams.containsKey(CCAConstants.USER_ID)) {
			userId = queryParams.get(CCAConstants.USER_ID).get(0);
		}

		String language = queryParams.getFirst(CCAConstants.LANGUAGE);
		if (language == null)
			language = CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE);

		AggregateIterable<Map> aggregation = ccaDataDao.getAggregation(uriInfo, userId);

		return aggregation.first();
	}

	private List<CCADataList> mergeToCCADataList(List<CCAData> ccaDatas, String language) {

		List<CCADataList> result = new ArrayList<>();
		for (CCAData ccaData : ccaDatas) {
			CCATemplate template = ccaTemplateService.getCCAByShortName(ccaData.getShortName(), language, false);
			ccaData.translate(template);
			CCADataList listCard = new CCADataList(ccaData);
			listCard.setTitlesValues(getTitleFields(ccaData, template));
			result.add(listCard);
		}
		return result;
	}

	private List<CCAFieldValue> getTitleFields(CCAData ccaData, CCATemplate template) {
		List<CCAFieldValue> res = new ArrayList<>();
		Map<String, CCAFieldValue> temp = ccaData.getCcaFieldValues();
		for(CCAField ccaField : template.getFields()) {
			for(CCAField ccaFieldChild: ccaField.getChildren()) {
				if(temp.containsKey(ccaFieldChild.getFieldId()) && ccaFieldChild.getIsTitleColumn()) {
					res.add(temp.get(ccaFieldChild.getFieldId()));
				}
			}
		}
		return res;
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
				CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE), false);

		if (!AuthorizationUtil.checkAuthorization(request, ccaTemplate.getPermissions(), ccaData.getUserId())) {
			throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
					.entity(AuthorizationUtil.UNAUTHORIZED_MESSAGE).build());
		}

		CommonProfile profile = AuthUtil.getProfileFromRequest(request);

		validateData(ccaData, ccaTemplate);

		Timestamp time = new Timestamp(new Date().getTime());
		ccaData.setCreatedOn(time);
		ccaData.setUpdatedOn(time);

		ccaData.setUserId(profile.getId());

		ccaData.reComputeCentroid();

		ccaData.setRichTextCount(CCAUtil.countFieldType(ccaData, FieldType.RICHTEXT));
		ccaData.setTextFieldCount(CCAUtil.countFieldType(ccaData, FieldType.TEXT));
		ccaData.setTraitsFieldCount(CCAUtil.countFieldType(ccaData, FieldType.SINGLE_SELECT_RADIO)
				+ CCAUtil.countFieldType(ccaData, FieldType.MULTI_SELECT_CHECKBOX)
				+ CCAUtil.countFieldType(ccaData, FieldType.SINGLE_SELECT_DROPDOWN)
				+ CCAUtil.countFieldType(ccaData, FieldType.MULTI_SELECT_DROPDOWN));

		ccaData = ccaDataDao.save(ccaData);

		logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), "", ccaData.getId(),
				ccaData.getId(), "ccaData", ccaData.getId(), "Data created");

		return ccaData;
	}

	@Override
	public CCAData update(HttpServletRequest request, CCAData ccaData, String type) {

		String shortName = ccaData.getShortName();
		CCATemplate ccaTemplate = ccaTemplateService.getCCAByShortName(shortName,
				CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE), false);

		validateData(ccaData, ccaTemplate);

		Timestamp time = new Timestamp(new Date().getTime());
		ccaData.setUpdatedOn(time);

		if (ccaData.getId() == null)
			throw new IllegalArgumentException("Please specify the id for cca data");

		CCAData dataInMem = ccaDataDao.getById(ccaData.getId());

		dataInMem = dataInMem.overrideFieldData(request, ccaData, logActivities, type);

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

	private List<SubsetCCADataList> mergeToSubsetCCADataList(List<CCAData> ccaDatas, String language) {

		List<SubsetCCADataList> result = new ArrayList<>();
		for (CCAData ccaData : ccaDatas) {
			CCATemplate template = ccaTemplateService.getCCAByShortName(ccaData.getShortName(), language, false);
			ccaData.translate(template);
			
			SubsetCCADataList listCard = new SubsetCCADataList(ccaData);
			listCard.setTitlesValues(getTitleFields(ccaData, template));
			result.add(listCard);
		}
		return result;
	}

	@Override
	public Map<String, Object> getCCAPageData(HttpServletRequest request, UriInfo uriInfo, boolean myListOnly)
			throws JsonProcessingException {
		
		String userId = null;
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		if (myListOnly) {
			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			userId = profile.getId();
		} else if(queryParams.containsKey(CCAConstants.USER_ID)) {
			userId = queryParams.get(CCAConstants.USER_ID).get(0);
		}

		int offset = Integer.parseInt(queryParams.get("offset").get(0));
		int limit = Integer.parseInt(queryParams.get("limit").get(0));

		List<CCAData> ccaDatas = ccaDataDao.getAll(uriInfo, false, userId, false, limit, offset);

		String language = queryParams.getFirst(CCAConstants.LANGUAGE);
		if (language == null)
			language = CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE);
		
		List<SubsetCCADataList> list = mergeToSubsetCCADataList(ccaDatas, language);
		Map<String, Object> res = new HashMap<String, Object>();
		
		res.put("totalCount", list.size());
		res.put("data", list);
		
		return res;
	}

	@Override
	public List<MapInfo> getCCAMapData(HttpServletRequest request, UriInfo uriInfo, boolean myListOnly)
			throws JsonProcessingException {

		String userId = null;
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		if (myListOnly) {
			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			userId = profile.getId();
		} else if(queryParams.containsKey(CCAConstants.USER_ID)) {
			userId = queryParams.get(CCAConstants.USER_ID).get(0);
		}
		
		List<CCAData> ccaDataList = ccaDataDao.getAll(uriInfo, false, userId, false);
		
		List<MapInfo> mapInfoList = new ArrayList<>();
		for(CCAData ccaData : ccaDataList) {
			mapInfoList.add(new MapInfo(ccaData.getId(), ccaData.getCentroid().get(1), ccaData.getCentroid().get(0)));
		}
		
		return mapInfoList;
	}

	@Override
	public SubsetCCADataList getSummaryData(Long id, String language) {
		CCAData ccaData = this.findById(id, language);
		List<CCAFieldValue> res = new ArrayList<>();
		List<FileMeta> files = new ArrayList<>();
		List<CCAFieldValue> titlesValues = new ArrayList<>();
		CCATemplate template = ccaTemplateService.getCCAByShortName(ccaData.getShortName(), language, false);

		Map<String, CCAFieldValue> temp = ccaData.getCcaFieldValues();
		for(CCAField ccaField : template.getFields()) {
			for(CCAField ccaFieldChild: ccaField.getChildren()) {
				if(temp.containsKey(ccaFieldChild.getFieldId())) {
					CCAFieldValue ccaFV = temp.get(ccaFieldChild.getFieldId());
					if(ccaFieldChild.getIsSummaryField() && !ccaFV.getType().equals(FieldType.GEOMETRY))
						res.add(ccaFV);
					if(ccaFieldChild.getIsTitleColumn())
						titlesValues.add(ccaFV);
					if(ccaFV.getType().equals(FieldType.FILE)) {
						List<FileMeta> fileMetas = ((FileFieldValue) ccaFV).getValue();
						files.addAll(fileMetas);
					}
				}
			}
		}

		SubsetCCADataList result = new SubsetCCADataList();
		result.setId(ccaData.getId());
		result.setFiles(files);
		result.setTitlesValues(titlesValues);
		result.setValues(res);
		return result;
	}

}
