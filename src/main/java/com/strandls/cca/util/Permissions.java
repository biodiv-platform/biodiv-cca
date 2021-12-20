package com.strandls.cca.util;

public enum Permissions {
	ROLE_ADMIN("ROLE_ADMIN"), ROLE_USER("ROLE_USER"), ROLE_DATACURATOR("ROLE_DATACURATOR"),
	ROLE_TEMPLATECURATOR("ROLE_TEMPLATECURATOR"), ROLE_EXTDATACONTRIBUTOR("ROLE_EXTDATACONTRIBUTOR");

	String value;

	private Permissions(String value) {
		this.value = value;
	}
}
