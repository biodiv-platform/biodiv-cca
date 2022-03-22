package com.strandls.cca.pojo;

import java.util.List;

public class Permission {
	private Long id;
	private List<String> allowedUsers;
	
	public List<String> getAllowedUsers() {
		return allowedUsers;
	}
	
	public void setAllowedUsers(List<String> allowedUsers) {
		this.allowedUsers = allowedUsers;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
}
