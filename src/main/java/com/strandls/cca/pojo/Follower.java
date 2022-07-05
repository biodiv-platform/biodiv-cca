package com.strandls.cca.pojo;

import java.util.List;

public class Follower {
	private Long id;
	private List<String> followers;
	private String type;
	
	public List<String> getfollowers() {
		return followers;
	}
	
	public void setfollowers(List<String> followers) {
		this.followers = followers;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
}
