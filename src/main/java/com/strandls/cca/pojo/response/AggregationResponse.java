package com.strandls.cca.pojo.response;

import java.util.List;
import java.util.Map;

public class AggregationResponse {
	private Map<String, Object> aggregation;
	private List<CCADataList> ccaDataList;

	public Map<String, Object> getAggregation() {
		return aggregation;
	}

	public void setAggregation(Map<String, Object> aggregation) {
		this.aggregation = aggregation;
	}

	public List<CCADataList> getCcaDataList() {
		return ccaDataList;
	}

	public void setCcaDataList(List<CCADataList> ccaDataList) {
		this.ccaDataList = ccaDataList;
	}

}
