package com.strandls.cca.pojo;

import java.util.Date;
import java.util.Map;

import org.bson.codecs.pojo.annotations.BsonId;

import com.strandls.cca.IdInterface;

public class CCAData implements IdInterface {

	@BsonId
	private String id;

	private String shortName;
	private String userId;
	private Date createdOn;
	private Date updatedOn;

	private Map<String, CCAFieldValue> ccaFieldValues;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Map<String, CCAFieldValue> getCcaFieldValues() {
		return ccaFieldValues;
	}

	public void setCcaFieldValues(Map<String, CCAFieldValue> ccaFieldValues) {
		this.ccaFieldValues = ccaFieldValues;
	}

}
