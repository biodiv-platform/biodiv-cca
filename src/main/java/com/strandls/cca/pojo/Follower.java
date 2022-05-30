package com.strandls.cca.pojo;

import java.util.List;

public class Follower {
	private Long id;
	private List<String> followers;
	
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
}
