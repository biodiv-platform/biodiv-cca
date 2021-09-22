package com.strandls.cca.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.strandls.cca.pojo.CCAData;

public interface CCADataService {

	public CCAData saveOrUpdate(HttpServletRequest request, CCAData ccaData);

	public CCAData remove(CCAData ccaData);

	public CCAData remove(String id);

	public List<CCAData> getAllCCA(HttpServletRequest request);

}
