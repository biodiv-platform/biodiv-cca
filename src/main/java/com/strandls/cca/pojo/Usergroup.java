package com.strandls.cca.pojo;

import java.util.List;

public class Usergroup {

	private Long id;
	private List<String> usergroups;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<String> getUsergroups() {
		return usergroups;
	}

	public void setUsergroups(List<String> usergroups) {
		this.usergroups = usergroups;
	}

}
