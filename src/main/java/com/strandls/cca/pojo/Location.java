package com.strandls.cca.pojo;

public class Location {

	private String state;
	private String district;
	private String tahsil;

	public Location() {
		super();
	}

	public Location(String state, String district, String tahsil) {
		super();
		this.state = state;
		this.district = district;
		this.tahsil = tahsil;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getTahsil() {
		return tahsil;
	}

	public void setTahsil(String tahsil) {
		this.tahsil = tahsil;
	}

}
