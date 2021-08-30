package com.strandls.cca.pojo.enumtype;

public enum Platform {

	MOBILE("MOBILE"),
	DESKTOP("DESKTOP");
	
	private String value;
	
	private Platform(String value) {
		this.value = value;
	}
	
	public static Platform fromString(final String value) {
		for(Platform plateform : Platform.values()) {
			if(plateform.value.equals(value))
				return plateform;
		}
		throw new IllegalArgumentException(value);
	}
	
	public String getValue() {
		return value;
	}
	
}
