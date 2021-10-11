package com.strandls.cca.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCATemplate;

public interface CCADataService {

	public void validateData(CCAData ccaData, CCATemplate ccaTemplate);
	
	public CCAData saveOrUpdate(HttpServletRequest request, CCAData ccaData);

	public CCAData remove(CCAData ccaData);

	public CCAData remove(String id);

	public List<CCAData> getAllCCA(HttpServletRequest request, UriInfo info, String shortName);

	public List<CCAData> insertBulk(List<CCAData> ccaDatas);

	public List<CCAData> uploadCCADataFromFile(HttpServletRequest request, FormDataMultiPart multiPart) throws IOException;

}
