package com.strandls.cca.pojo;

public class SpeciesAggregation {
	private String scientificName;
	private Long count;

	public SpeciesAggregation() {
	}

	public SpeciesAggregation(String scientificName, Long count) {
		this.scientificName = scientificName;
		this.count = count;
	}

	public String getScientificName() {
		return scientificName;
	}

	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}
