package com.strandls.cca.pojo.response;

import java.util.List;

import com.strandls.cca.pojo.SpeciesAggregation;

public class GBIFObservationResponse {
	private Long totalCount; // Total count of unique scientificNames
	private Integer offset;
	private Integer limit;
	private List<SpeciesAggregation> aggregations;

	public GBIFObservationResponse() {
	}

	public GBIFObservationResponse(Long totalCount, Integer offset, Integer limit,
			List<SpeciesAggregation> aggregations) {
		this.totalCount = totalCount;
		this.offset = offset;
		this.limit = limit;
		this.aggregations = aggregations;
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

	public List<SpeciesAggregation> getAggregations() {
		return aggregations;
	}

	public void setAggregations(List<SpeciesAggregation> aggregations) {
		this.aggregations = aggregations;
	}
}
