package com.strandls.cca.service.impl;

import java.io.IOException;
import java.util.Collections;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Filters;
import com.strandls.activity.ApiException;
import com.strandls.activity.controller.ActivitySerivceApi;
import com.strandls.activity.pojo.Activity;
import com.strandls.activity.pojo.CcaPermission;
import com.strandls.activity.pojo.CommentLoggingData;
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
import com.strandls.cca.pojo.EncryptedKey;
import com.strandls.cca.pojo.FieldType;
import com.strandls.cca.pojo.ValueWithLabel;
import com.strandls.cca.pojo.fields.value.CheckboxFieldValue;
import com.strandls.cca.pojo.fields.value.FileFieldValue;
import com.strandls.cca.pojo.fields.value.FileMeta;
import com.strandls.cca.pojo.fields.value.NumberFieldValue;
import com.strandls.cca.pojo.fields.value.TextFieldValue;
import com.strandls.cca.pojo.response.AggregationResponse;
import com.strandls.cca.pojo.response.CCADataList;
import com.strandls.cca.pojo.response.MapInfo;
import com.strandls.cca.pojo.response.SubsetCCADataList;
import com.strandls.cca.service.CCADataService;
import com.strandls.cca.service.CCATemplateService;
import com.strandls.cca.util.AuthorizationUtil;
import com.strandls.cca.util.CCADataCSVThread;
import com.strandls.cca.util.CCAUtil;
import com.strandls.cca.util.EncryptionUtils;
import com.strandls.cca.util.TokenGenerator;
import com.strandls.user.controller.UserServiceApi;
import com.strandls.userGroup.controller.UserGroupSerivceApi;

import net.minidev.json.JSONArray;

import com.strandls.cca.Headers;

public class CCADataServiceImpl implements CCADataService {

	@Inject
	private CCATemplateService ccaTemplateService;

	@Inject
	private CCADataDao ccaDataDao;

	@Inject
	private LogActivities logActivities;

	@Inject
	private ActivitySerivceApi activityService;

	@Inject
	private Headers headers;

	@Inject
	private UserServiceApi userService;

	@Inject
	private ObjectMapper om;

	@Inject
	private EncryptionUtils encryptUtils;

	@Inject
	private UserGroupSerivceApi userGroupService;

	@Inject
	private CCATemplateService ccaContextService;

	private final Logger logger = LoggerFactory.getLogger(CCADataServiceImpl.class);

	private static final String PROJECT_ALL = "projectAll";

	private static final String NOTES = "notes";

	private static final String SHORT_NAME = "shortName";

	private static final String UPDATEUSERGROUP = "UpdateUsergroup";

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
	public List<CCAData> getAllCCAData(HttpServletRequest request, UriInfo uriInfo, Boolean isDeletedData)
			throws JsonProcessingException {
		Boolean projectAll = false;
		MultivaluedMap<String, String> queryParameter = uriInfo.getQueryParameters();
		if (queryParameter.containsKey(PROJECT_ALL)) {
			// if not list page it returns all CCA field values data
			projectAll = Boolean.parseBoolean(queryParameter.get(PROJECT_ALL).get(0));
		}
		String userId = queryParameter.containsKey(CCAConstants.USER_ID)
				? queryParameter.get(CCAConstants.USER_ID).get(0)
				: null;
		return ccaDataDao.getAll(uriInfo, projectAll, userId, isDeletedData);
	}

	@Override
	public List<CCAData> downloadCCAData(HttpServletRequest request, UriInfo uriInfo, Boolean isDeletedData)
			throws JsonProcessingException {

		Boolean projectAll = false;
		String notes = "";
		String url = "";
		String shortName = "";

		MultivaluedMap<String, String> queryParameter = uriInfo.getQueryParameters();

		if (queryParameter.containsKey(PROJECT_ALL)) {
			projectAll = Boolean.parseBoolean(queryParameter.get(PROJECT_ALL).get(0));
		}

		if (queryParameter.containsKey(NOTES)) {
			notes = queryParameter.get(NOTES).get(0);
		}

		if (queryParameter.containsKey(SHORT_NAME)) {
			shortName = queryParameter.get(SHORT_NAME).get(0);
		}

		if (uriInfo.getRequestUri() != null) {
			url = uriInfo.getRequestUri().toString();
		}

		String userId = queryParameter.containsKey(CCAConstants.USER_ID)
				? queryParameter.get(CCAConstants.USER_ID).get(0)
				: null;

		List<CCAData> ccaData = ccaDataDao.getAll(uriInfo, projectAll, userId, isDeletedData);
		CCATemplate template = ccaContextService.getCCAByShortName(shortName, null, false);

		userService = headers.addUserHeaders(userService, request.getHeader(HttpHeaders.AUTHORIZATION));
		activityService = headers.addActivityHeader(activityService, request.getHeader(HttpHeaders.AUTHORIZATION));

		CCADataCSVThread csvThread = new CCADataCSVThread(ccaData, notes, url, userService, activityService, template);
		Thread thread = new Thread(csvThread);
		thread.start();

		return ccaDataDao.getAll(uriInfo, projectAll, userId, isDeletedData);
	}

	@Override
	public List<CCAData> getCCADataByShortName(HttpServletRequest request, UriInfo uriInfo, String shortName,
			Boolean isDeletedData) throws JsonProcessingException {
		List<CCAData> temp = this.getAllCCAData(request, uriInfo, isDeletedData);
		List<CCAData> result = new ArrayList<>();
		for (int i = 0; i < temp.size(); i++) {
			if (temp.get(i).getShortName().equals(shortName)) {
				result.add(temp.get(i));
			}
		}

		return result;
	}

	@Override
	public AggregationResponse getMyCCADataList(HttpServletRequest request, UriInfo uriInfo)
			throws JsonProcessingException {
		return getCCADataList(request, uriInfo, false);
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
		} else if (queryParams.containsKey(CCAConstants.USER_ID)) {
			userId = queryParams.get(CCAConstants.USER_ID).get(0);
		}

		List<CCAData> ccaDatas = ccaDataDao.getAll(uriInfo, false, userId, false);

		String language = queryParams.getFirst(CCAConstants.LANGUAGE);
		if (language == null)
			language = CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE);

		List<CCADataList> ccaDataList = mergeToCCADataList(ccaDatas, language);
		Bson searchQuery = new Document();

		AggregateIterable<Map> aggregation = ccaDataDao.getAggregation(uriInfo, userId, searchQuery);

		AggregationResponse aggregationResponse = new AggregationResponse();
		aggregationResponse.setCcaDataList(ccaDataList);
		aggregationResponse.setAggregation(aggregation.first());
		return aggregationResponse;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public Map<String, Object> searchChartData(String query, HttpServletRequest request, UriInfo uriInfo) {
		try {
			// Set the default value for the query parameter if not provided
			if (query.isEmpty()) {
				query = "";
			}

			Map<String, Object> res = new HashMap<>();
			List<String> fieldIds = ccaTemplateService.getFieldIds("", "");
			List<String> valueFields = ccaTemplateService.getValueFields(fieldIds);

			// Add multiple fields to the search query
			List<Bson> fieldQueries = new ArrayList<>();
			for (String valueField : valueFields) {
				Bson fieldQuery = Filters.regex(valueField, query, "i");
				fieldQueries.add(fieldQuery);
			}

			Bson searchQuery = Filters.or(fieldQueries);
			List<CCAData> ccaData = ccaDataDao.getSearchMapCCAData(uriInfo, searchQuery);

			List<Map<String, CCAFieldValue>> ccaFieldValuesList = ccaData.stream().map(CCAData::getCcaFieldValues)
					.map(fieldValues -> fieldValues.entrySet().stream()
							.filter(entry -> entry.getValue().getType().getValue().equals("NUMBER"))
							.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
					.filter(fieldValues -> !fieldValues.isEmpty()).collect(Collectors.toList());

			String userId = null;

			AggregateIterable<Map> aggregation = ccaDataDao.getAggregation(uriInfo, userId, searchQuery);

			res.put("numericAggregation", ccaFieldValuesList);
			res.put("aggregation", aggregation.first());
			res.put("satewiseAggregation", aggregateByState(ccaData));
			res.put("total", ccaData.size());

			return res;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return Collections.emptyMap();
	}

	public static Map<String, Integer> aggregateByState(List<CCAData> ccaDataList) {
		Map<String, Integer> stateCountMap = new HashMap<>();

		for (CCAData ccaData : ccaDataList) {
			if (ccaData.getLocation() != null) {
				String state = ccaData.getLocation().getState();
				stateCountMap.put(state, stateCountMap.getOrDefault(state, 0) + 1);
			}
		}

		return stateCountMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Object> getCCADataAggregation(String query, HttpServletRequest request, UriInfo uriInfo,
			boolean myListOnly) throws JsonProcessingException {

		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

		// Set the default value for the query parameter if not provided
		if (query == null) {
			query = "";
		}

		List<String> fieldIds = ccaTemplateService.getFieldIds("", "");
		List<String> valueFields = ccaTemplateService.getValueFields(fieldIds);

		// Add multiple fields to the search query
		List<Bson> fieldQueries = new ArrayList<>();
		for (String valueField : valueFields) {
			Bson fieldQuery = Filters.regex(valueField, query, "i");
			fieldQueries.add(fieldQuery);
		}

		Bson searchQuery = Filters.or(fieldQueries);

		String userId = null;
		if (myListOnly) {
			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			userId = profile.getId();
		} else if (queryParams.containsKey(CCAConstants.USER_ID)) {
			userId = queryParams.get(CCAConstants.USER_ID).get(0);
		}

		AggregateIterable<Map> aggregation = ccaDataDao.getAggregation(uriInfo, userId, searchQuery);
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
		for (CCAField ccaField : template.getFields()) {
			for (CCAField ccaFieldChild : ccaField.getChildren()) {
				if (temp.containsKey(ccaFieldChild.getFieldId()) && ccaFieldChild.getIsTitleColumn()) {
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

		JSONArray roles = (JSONArray) profile.getAttribute("roles");

		validateData(ccaData, ccaTemplate);

		Timestamp time = new Timestamp(new Date().getTime());
		ccaData.setCreatedOn(time);
		ccaData.setUpdatedOn(time);

		ccaData.setUserId(ccaData.getUserId() != null ? ccaData.getUserId() : profile.getId());

		ccaData.reComputeCentroid();

		ccaData.setRichTextCount(CCAUtil.countFieldType(ccaData, FieldType.RICHTEXT));
		ccaData.setTextFieldCount(CCAUtil.countFieldType(ccaData, FieldType.TEXT));
		ccaData.setTraitsFieldCount(CCAUtil.countFieldType(ccaData, FieldType.SINGLE_SELECT_RADIO)
				+ CCAUtil.countFieldType(ccaData, FieldType.MULTI_SELECT_CHECKBOX)
				+ CCAUtil.countFieldType(ccaData, FieldType.SINGLE_SELECT_DROPDOWN)
				+ CCAUtil.countFieldType(ccaData, FieldType.MULTI_SELECT_DROPDOWN));

		Set<String> groups = ccaData.getUsergroups();

		ccaData.setUsergroups(roles.contains("ROLE_ADMIN") ? groups : null);

		ccaData = ccaDataDao.save(ccaData);

		String jwtString = null;

		try {
			TokenGenerator tokenGenerator = new TokenGenerator();
			jwtString = tokenGenerator.generate(userService.getUser(ccaData.getUserId()));
		} catch (Exception e) {
			logger.error("Token generation failed: " + e.getMessage());
		}

		logActivities.logCCAActivities(jwtString, "", ccaData.getId(), ccaData.getId(), "ccaData", ccaData.getId(),
				"Data created", CCAUtil.generateMailData(ccaData, null, null, getSummaryInfo(ccaData), null));

		if (!roles.contains("ROLE_ADMIN")) {
			ccaData.setUsergroups(groups);
			update(request, ccaData, UPDATEUSERGROUP);
		}

		return ccaData;
	}

	@Override
	public CCAData update(HttpServletRequest request, CCAData ccaData, String type) {

		if (type.equalsIgnoreCase(UPDATEUSERGROUP)) {
			Set<String> groupsToRemove = new HashSet<>();
			Boolean eligible = null;
			userGroupService = headers.addUserGroupHeader(userGroupService,
					request.getHeader(HttpHeaders.AUTHORIZATION));

			for (String usergroupId : ccaData.getUsergroups()) {
				try {
					eligible = userGroupService.checkUserMember(usergroupId);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
				if (Boolean.FALSE.equals(eligible)) {
					groupsToRemove.add(usergroupId);
				}
			}

			ccaData.getUsergroups().removeAll(groupsToRemove);
		}

		String shortName = ccaData.getShortName();
		CCATemplate ccaTemplate = ccaTemplateService.getCCAByShortName(shortName,
				CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE), false);

		if (!type.equalsIgnoreCase("Permission") && !type.equalsIgnoreCase("Follow")
				&& !type.equalsIgnoreCase("Unfollow") && !type.equalsIgnoreCase("Location")
				&& !type.equalsIgnoreCase(UPDATEUSERGROUP))

			validateData(ccaData, ccaTemplate);

		Timestamp time = new Timestamp(new Date().getTime());
		ccaData.setUpdatedOn(time);

		if (ccaData.getId() == null)
			throw new IllegalArgumentException("Please specify the id for cca data");

		CCAData dataInMem = ccaDataDao.getById(ccaData.getId());

		dataInMem = dataInMem.overrideFieldData(request, ccaData, logActivities, type, getSummaryInfo(dataInMem),
				dataInMem, userService, userGroupService);

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
	public CCAData remove(HttpServletRequest request, Long id) {
		CCAData ccaData = ccaDataDao.remove(id);

		logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), "", ccaData.getId(),
				ccaData.getId(), "ccaData", ccaData.getId(), "Data deleted",
				CCAUtil.generateMailData(ccaData, null, null, getSummaryInfo(ccaData), null));

		return ccaData;
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
		} else if (queryParams.containsKey(CCAConstants.USER_ID)) {
			userId = queryParams.get(CCAConstants.USER_ID).get(0);
		}

		int offset = Integer.parseInt(queryParams.get("offset").get(0));
		int limit = Integer.parseInt(queryParams.get("limit").get(0));

		List<CCAData> ccaDatas = ccaDataDao.getAll(uriInfo, false, userId, false, limit, offset);

		String language = queryParams.getFirst(CCAConstants.LANGUAGE);
		if (language == null)
			language = CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE);

		List<SubsetCCADataList> list = mergeToSubsetCCADataList(ccaDatas, language);
		Map<String, Object> res = new HashMap<>();

		res.put("totalCount", ccaDataDao.totalDataCount(uriInfo));
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
		} else if (queryParams.containsKey(CCAConstants.USER_ID)) {
			userId = queryParams.get(CCAConstants.USER_ID).get(0);
		}

		List<CCAData> ccaDataList = ccaDataDao.getAll(uriInfo, false, userId, false);

		List<MapInfo> mapInfoList = new ArrayList<>();
		for (CCAData ccaData : ccaDataList) {
			if (ccaData.getCentroid().size() >= 2) {
				mapInfoList
						.add(new MapInfo(ccaData.getId(), ccaData.getCentroid().get(1), ccaData.getCentroid().get(0)));
			}
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
		for (CCAField ccaField : template.getFields()) {
			for (CCAField ccaFieldChild : ccaField.getChildren()) {
				if (temp.containsKey(ccaFieldChild.getFieldId())) {
					CCAFieldValue ccaFV = temp.get(ccaFieldChild.getFieldId());
					if (Boolean.TRUE.equals(ccaFieldChild.getIsSummaryField())
							&& !ccaFV.getType().equals(FieldType.GEOMETRY))
						res.add(ccaFV);
					if (Boolean.TRUE.equals(ccaFieldChild.getIsTitleColumn()))
						titlesValues.add(ccaFV);
					if (ccaFV.getType().equals(FieldType.FILE)) {
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

	public Map<String, Object> getSummaryInfo(CCAData ccaData) {
		Map<String, Object> result = new HashMap<>();

		List<CCAFieldValue> titlesValues = new ArrayList<>();
		CCATemplate template = ccaTemplateService.getCCAByShortName(ccaData.getShortName(),
				ApiConstants.DEFAULT_LANGUAGE, false);

		Map<String, CCAFieldValue> temp = ccaData.getCcaFieldValues();
		for (CCAField ccaField : template.getFields()) {
			for (CCAField ccaFieldChild : ccaField.getChildren()) {
				if (temp.containsKey(ccaFieldChild.getFieldId())) {
					CCAFieldValue ccaFV = temp.get(ccaFieldChild.getFieldId());
					if (Boolean.TRUE.equals(ccaFieldChild.getIsSummaryField())
							&& !ccaFV.getType().equals(FieldType.GEOMETRY)) {
						if (ccaFV.getType() == FieldType.TEXT) {
							TextFieldValue textFieldValue = (TextFieldValue) ccaFV;
							if (textFieldValue.getName().equals("Name of CCA")) {
								result.put("nameOfCCA", textFieldValue.getValue());
							}
						} else if (ccaFV.getType() == FieldType.NUMBER) {
							NumberFieldValue nf = (NumberFieldValue) ccaFV;
							result.put("area", nf.getValue());
						} else if (ccaFV.getType() == FieldType.MULTI_SELECT_CHECKBOX) {
							CheckboxFieldValue cfv = (CheckboxFieldValue) ccaFV;
							if (cfv.getName().equals("Ecosystem Type")) {
								String s1 = "";
								for (ValueWithLabel vwl : cfv.getValue()) {
									s1 += " " + vwl.getValue();
								}
								result.put("ecosystem", s1);
							} else if (cfv.getName().equals("Legal Status ")) {
								String s = "";
								for (ValueWithLabel vwl : cfv.getValue()) {
									s += " " + vwl.getValue();
								}
								result.put("legalStatus", s);
							}
						}
					}

					if (Boolean.TRUE.equals(ccaFieldChild.getIsTitleColumn())) {
						titlesValues.add(ccaFV);
					}

					if (ccaFV.getType().equals(FieldType.FILE) && ((FileFieldValue) ccaFV).getValue() != null) {
						List<FileMeta> fileMetas = ((FileFieldValue) ccaFV).getValue();
						if (fileMetas.size() != 0)
							result.put("image", fileMetas.get(0).getPath());
					}
				}
			}
		}

		if (!titlesValues.isEmpty())
			result.put("title", getTitle(titlesValues));

		return result.isEmpty() ? null : result;
	}

	private String getTitle(List<CCAFieldValue> titlesValues) {
		String title = "";

		for (CCAFieldValue ccaFV : titlesValues) {
			if (ccaFV.getType() == FieldType.TEXT) {
				TextFieldValue textFieldValue = (TextFieldValue) ccaFV;
				if (textFieldValue.getName().equals("Name of CCA")) {
					title += " " + textFieldValue.getValue();
				}
			} else if (ccaFV.getType() == FieldType.NUMBER) {
				NumberFieldValue nf = (NumberFieldValue) ccaFV;
				title += " " + nf.getValue();
			} else if (ccaFV.getType() == FieldType.MULTI_SELECT_CHECKBOX) {
				CheckboxFieldValue cfv = (CheckboxFieldValue) ccaFV;
				if (cfv.getName().equals("Ecosystem Type")) {
					String s1 = "";
					for (ValueWithLabel vwl : cfv.getValue()) {
						s1 += " " + vwl.getValue();
					}
					title += " " + s1;
				} else if (cfv.getName().equals("Legal Status ")) {
					String s = "";
					for (ValueWithLabel vwl : cfv.getValue()) {
						s += " " + vwl.getValue();
					}
					title += " " + s;
				}
			}
		}

		return title;
	}

	@Override
	public Activity addComment(HttpServletRequest request, Long userId, Long dataId, CommentLoggingData commentData) {
		CCAData ccaData = this.findById(dataId, ApiConstants.DEFAULT_LANGUAGE);

		if (ccaData == null) {
			throw new NotFoundException("Not found cca data with id : " + dataId);
		}
		Set<String> s = new HashSet<>();
		s.add(userId.toString());
		ccaData.setFollowers(s);
		CCAData temp = this.update(request, ccaData, "Follow");

		commentData.setMailData(
				CCAUtil.generateMailData(temp, "Commented", commentData.getBody(), getSummaryInfo(ccaData), null));
		activityService = headers.addActivityHeader(activityService, request.getHeader(HttpHeaders.AUTHORIZATION));
		Activity activity = null;
		try {
			activity = activityService.addComment("cca", commentData);
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return activity;
	}

	@Override
	public Boolean sendPermissionRequest(HttpServletRequest request, CcaPermission ccaPermissionData, CCAData ccaData) {
		try {
			CommonProfile requestorProfile = AuthUtil.getProfileFromRequest(request);
			Long requestorId = Long.parseLong(requestorProfile.getId());
			Boolean result = null;

			// check if the user is already a allowed user
			if (!ccaData.getAllowedUsers().contains(requestorId.toString())) {

				CcaPermission permissionReq = new CcaPermission();
				permissionReq.setCcaid(ccaPermissionData.getCcaid());
				permissionReq.setOwnerId(Long.parseLong((ccaData.getUserId())));
				permissionReq.setShortName(ccaData.getShortName());
				permissionReq.setRequestorId(requestorId);
				permissionReq.setRole(ccaPermissionData.getRole());
				permissionReq.setRequestorMessage(ccaPermissionData.getRequestorMessage());

				String reqText = om.writeValueAsString(permissionReq);
				String encryptedKey = encryptUtils.encrypt(reqText);
				permissionReq.setEncryptKey(encryptedKey);
				permissionReq.setData(getSummaryInfo(ccaData));

				result = activityService.ccaMailRequest(permissionReq);
			}
			return result;

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	@Override
	public Boolean sendPermissionGrant(HttpServletRequest request, EncryptedKey encryptedKey) {
		try {
			String reqdata = encryptUtils.decrypt(encryptedKey.getToken());
			CcaPermission permissionReq = om.readValue(reqdata, CcaPermission.class);

			Long ccaId = permissionReq.getCcaid();
			Long requestorId = permissionReq.getRequestorId();
			CCAData originalDocs = findById(ccaId, null);

			if (!originalDocs.getAllowedUsers().contains(requestorId.toString())) {
				originalDocs.getAllowedUsers().add(requestorId.toString());
				update(request, originalDocs, "permission");
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return false;
	}

	@Override
	public Map<String, Object> searchCCAData(String query, HttpServletRequest request, UriInfo uriInfo)
			throws JsonProcessingException {
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

			// Set default values for offset and limit parameters if not provided
			int offset = Integer.parseInt(queryParams.getOrDefault("offset", Collections.singletonList("0")).get(0));
			int limit = Integer.parseInt(queryParams.getOrDefault("limit", Collections.singletonList("100")).get(0));

			String language = queryParams.getFirst("language");
			if (language == null || "".equals(language))
				language = CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE);

			// Set the default value for the query parameter if not provided
			if (query.isEmpty()) {
				query = "";
			}

			List<String> fieldIds = ccaTemplateService.getFieldIds("", "");
			List<String> valueFields = ccaTemplateService.getValueFields(fieldIds);

			// Add multiple fields to the search query
			List<Bson> fieldQueries = new ArrayList<>();
			for (String valueField : valueFields) {
				Bson fieldQuery = Filters.regex(valueField, query, "i");
				fieldQueries.add(fieldQuery);
			}

			Bson searchQuery = Filters.or(fieldQueries);

			List<CCAData> ccaData = ccaDataDao.getSearchCCAData(uriInfo, searchQuery, limit, offset);

			List<SubsetCCADataList> list = mergeToSubsetCCADataList(ccaData, language);
			Map<String, Object> res = new HashMap<>();

			res.put("totalCount", list.size());
			res.put("data", list);

			// Return the search results as a JSON response
			return res;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return Collections.emptyMap();
	}

	@Override
	public List<MapInfo> searchMapCCAData(String query, HttpServletRequest request, UriInfo uriInfo)
			throws JsonProcessingException {
		try {

			// Set the default value for the query parameter if not provided
			if (query.isEmpty()) {
				query = "";
			}

			List<String> fieldIds = ccaTemplateService.getFieldIds("", "");
			List<String> valueFields = ccaTemplateService.getValueFields(fieldIds);

			// Add multiple fields to the search query
			List<Bson> fieldQueries = new ArrayList<>();
			for (String valueField : valueFields) {
				Bson fieldQuery = Filters.regex(valueField, query, "i");
				fieldQueries.add(fieldQuery);
			}

			Bson searchQuery = Filters.or(fieldQueries);

			List<CCAData> ccaData = ccaDataDao.getSearchMapCCAData(uriInfo, searchQuery);
			List<MapInfo> mapInfoList = new ArrayList<>();
			for (CCAData ccadata : ccaData) {
				if (ccadata.getCentroid().size() >= 2) {
					mapInfoList.add(
							new MapInfo(ccadata.getId(), ccadata.getCentroid().get(1), ccadata.getCentroid().get(0)));
				}
			}
			return mapInfoList;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return Collections.emptyList();
	}

	@Override
	public List<CCAData> saveCCADataInBulk(HttpServletRequest request, List<CCAData> ccaDataList) {
		List<CCAData> savedDataList = new ArrayList<>();
		for (CCAData ccaData : ccaDataList) {
			CCAData savedData = save(request, ccaData);
			savedDataList.add(savedData);
		}
		return savedDataList;
	}

}
