package com.strandls.cca.pojo;

public class Counter {

	private String collectionName;
	private Long idValue;

	public Counter() {
	}

	public Counter(String collectionName, Long idValue) {
		this.collectionName = collectionName;
		this.idValue = idValue;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public Long getIdValue() {
		return idValue;
	}

	public void setIdValue(Long idValue) {
		this.idValue = idValue;
	}

}
