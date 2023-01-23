package com.strandls.cca.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.strandls.activity.pojo.Activity;
import com.strandls.activity.pojo.CcaPermission;
import com.strandls.activity.pojo.CommentLoggingData;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.EncryptedKey;
import com.strandls.cca.pojo.response.AggregationResponse;
import com.strandls.cca.pojo.response.MapInfo;
import com.strandls.cca.pojo.response.SubsetCCADataList;

public interface CCADataService {

	public void validateData(CCAData ccaData, CCATemplate ccaTemplate);

	public CCAData save(HttpServletRequest request, CCAData ccaData);

	public CCAData findById(Long id, String language);

	public CCAData update(HttpServletRequest request, CCAData ccaData, String type);

	public CCAData restore(Long id);

	public CCAData remove(HttpServletRequest request, Long id);

	public List<CCAData> getAllCCAData(HttpServletRequest request, UriInfo uriInfo, Boolean isDeletedData)
			throws JsonProcessingException;

	public List<CCAData> downloadCCAData(HttpServletRequest request, UriInfo uriInfo, Boolean isDeletedData)
			throws JsonProcessingException;

	public List<CCAData> getCCADataByShortName(HttpServletRequest request, UriInfo uriInfo, String shortName,
			Boolean isDeletedData) throws JsonProcessingException;

	public AggregationResponse getMyCCADataList(HttpServletRequest request, UriInfo uriInfo, String userId)
			throws JsonProcessingException;

	public AggregationResponse getCCADataList(HttpServletRequest request, UriInfo uriInfo, boolean myListOnly,
			String userId) throws JsonProcessingException;

	public List<CCAData> insertBulk(List<CCAData> ccaDatas);

	public List<CCAData> uploadCCADataFromFile(HttpServletRequest request, FormDataMultiPart multiPart)
			throws IOException;

	public CCAData deepRemove(Long id);

	public Map<String, Object> getCCADataAggregation(HttpServletRequest request, UriInfo uriInfo, boolean myListOnly)
			throws JsonProcessingException;

	public Map<String, Object> getCCAPageData(HttpServletRequest request, UriInfo uriInfo, boolean myListOnly)
			throws JsonProcessingException;

	public List<MapInfo> getCCAMapData(HttpServletRequest request, UriInfo uriInfo, boolean myListOnly)
			throws JsonProcessingException;

	public SubsetCCADataList getSummaryData(Long id, String language);

	public Activity addComment(HttpServletRequest request, Long userId, Long dataId, CommentLoggingData commentData);

	public Boolean sendPermissionRequest(HttpServletRequest request, CcaPermission ccaPermissionData, CCAData ccaData);

	public Boolean sendPermissionGrant(HttpServletRequest request, EncryptedKey encryptedKey);

}
