/**
 * 
 */
package com.strandls.cca.pojo;

/**
 * @author Arun
 *
 * 
 */
public enum TreeRoles {

	DATACURATOR("ROLE_DATACURATOR"), TEMPLATECURATOR("ROLE_TEMPLATECURATOR"),
	EXTDATACONTRIBUTOR("ROLE_EXTDATACONTRIBUTOR");

	String value;

	private TreeRoles(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
