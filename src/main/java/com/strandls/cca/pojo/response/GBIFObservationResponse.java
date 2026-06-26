package com.strandls.cca.pojo.response;

import java.util.List;

import com.strandls.cca.pojo.GBIFObservation;

public class GBIFObservationResponse {
	private Long totalCount;
	private Integer offset;
	private Integer limit;
	private List<GBIFObservation> observations;

	public GBIFObservationResponse() {
	}

	public GBIFObservationResponse(Long totalCount, Integer offset, Integer limit, List<GBIFObservation> observations) {
		this.totalCount = totalCount;
		this.offset = offset;
		this.limit = limit;
		this.observations = observations;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public List<GBIFObservation> getObservations() {
		return observations;
	}

	public void setObservations(List<GBIFObservation> observations) {
		this.observations = observations;
	}
}
