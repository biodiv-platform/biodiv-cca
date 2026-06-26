package com.strandls.cca.pojo;

public class SpeciesAggregation {
	private String scientificName;
	private Long count;
	private String iucnRedListCategory;

	public SpeciesAggregation() {
	}

	public SpeciesAggregation(String scientificName, Long count, String iucnRedListCategory) {
		this.scientificName = scientificName;
		this.count = count;
		this.iucnRedListCategory = iucnRedListCategory;
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

	public String getIucnRedListCategory() {
		return iucnRedListCategory;
	}

	public void setIucnRedListCategory(String iucnRedListCategory) {
		this.iucnRedListCategory = iucnRedListCategory;
	}
}
