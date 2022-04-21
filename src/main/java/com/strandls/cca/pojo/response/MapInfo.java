package com.strandls.cca.pojo.response;

import java.text.DecimalFormat;

public class MapInfo {
	private Long id;
	private Double lat;
	private Double lng;

	public MapInfo(Long id, Double lat, Double lng) {
		this.id = id;
		DecimalFormat df = new DecimalFormat("#.####");
		this.lat = Double.parseDouble(df.format(lat));
		this.lng = Double.parseDouble(df.format(lng));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}
}
