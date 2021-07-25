package com.strandls.cca.pojo;

public class StringWithLanguage {

	private String language;
	private String value;

	public StringWithLanguage(String language, String value) {
		super();
		this.language = language;
		this.value = value;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
