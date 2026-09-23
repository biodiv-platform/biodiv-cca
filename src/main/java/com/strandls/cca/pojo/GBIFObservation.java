package com.strandls.cca.pojo;

public class GBIFObservation {
	private String gbifID;
	private String scientificName;
	private Double decimalLatitude;
	private Double decimalLongitude;
	private String stateProvince;
	private String eventDate;
	private String locality;

	public GBIFObservation() {
	}

	public GBIFObservation(String gbifID, String scientificName, Double decimalLatitude, Double decimalLongitude,
			String stateProvince, String eventDate, String locality) {
		this.gbifID = gbifID;
		this.scientificName = scientificName;
		this.decimalLatitude = decimalLatitude;
		this.decimalLongitude = decimalLongitude;
		this.stateProvince = stateProvince;
		this.eventDate = eventDate;
		this.locality = locality;
	}

	public String getGbifID() {
		return gbifID;
	}

	public void setGbifID(String gbifID) {
		this.gbifID = gbifID;
	}

	public String getScientificName() {
		return scientificName;
	}

	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}

	public Double getDecimalLatitude() {
		return decimalLatitude;
	}

	public void setDecimalLatitude(Double decimalLatitude) {
		this.decimalLatitude = decimalLatitude;
	}

	public Double getDecimalLongitude() {
		return decimalLongitude;
	}

	public void setDecimalLongitude(Double decimalLongitude) {
		this.decimalLongitude = decimalLongitude;
	}

	public String getStateProvince() {
		return stateProvince;
	}

	public void setStateProvince(String stateProvince) {
		this.stateProvince = stateProvince;
	}

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}
}
