package com.strandls.cca.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.response.AggregationResponse;

public interface CCADataService {

	public void validateData(CCAData ccaData, CCATemplate ccaTemplate);

	public CCAData save(HttpServletRequest request, CCAData ccaData);

	public CCAData findById(Long id, String language);

	public CCAData update(HttpServletRequest request, CCAData ccaData);

	public CCAData restore(Long id);

	public CCAData remove(Long id);

	public List<CCAData> getAllCCAData(HttpServletRequest request, UriInfo uriInfo) throws JsonProcessingException;

	public AggregationResponse getMyCCADataList(HttpServletRequest request, UriInfo uriInfo)
			throws JsonProcessingException;

	public AggregationResponse getCCADataList(HttpServletRequest request, UriInfo uriInfo, boolean myListOnly)
			throws JsonProcessingException;

	public List<CCAData> insertBulk(List<CCAData> ccaDatas);

	public List<CCAData> uploadCCADataFromFile(HttpServletRequest request, FormDataMultiPart multiPart)
			throws IOException;

	public CCAData deepRemove(Long id);

}
