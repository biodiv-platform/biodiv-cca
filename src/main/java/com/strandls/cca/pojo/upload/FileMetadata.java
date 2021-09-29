package com.strandls.cca.pojo.upload;

import java.util.Map;

public class FileMetadata {

	private String shortName;
	
	private Map<String, Integer> fieldToColumnIndex;

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Map<String, Integer> getFieldToColumnIndex() {
		return fieldToColumnIndex;
	}

	public void setFieldToColumnIndex(Map<String, Integer> fieldToColumnIndex) {
		this.fieldToColumnIndex = fieldToColumnIndex;
	}

}
