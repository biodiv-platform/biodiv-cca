package com.strandls.cca.pojo;

public class SpeciesAggregation {
	private String scientificName;
	private Long count;
	private String iucnRedListCategory;
	private String speciesGroup;

	public SpeciesAggregation() {
	}

	public SpeciesAggregation(String scientificName, Long count, String iucnRedListCategory, String speciesGroup) {
		this.scientificName = scientificName;
		this.count = count;
		this.iucnRedListCategory = iucnRedListCategory;
		this.speciesGroup = speciesGroup;
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

	public String getSpeciesGroup() {
		return speciesGroup;
	}

	public void setSpeciesGroup(String speciesGroup) {
		this.speciesGroup = speciesGroup;
	}
}
