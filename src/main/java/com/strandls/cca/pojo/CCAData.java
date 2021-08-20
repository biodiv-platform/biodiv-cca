package com.strandls.cca.pojo;

import java.util.Date;
import java.util.List;

import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.ObjectId;

public class CCAData {

	@Id
	@ObjectId
	private String id;

	private String templateId;
	private String userId;
	private Date createdOn;
	private Date updatedOn;

	private List<CCAFieldValues> ccaFieldValues;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
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

	public List<CCAFieldValues> getCcaFieldValues() {
		return ccaFieldValues;
	}

	public void setCcaFieldValues(List<CCAFieldValues> ccaFieldValues) {
		this.ccaFieldValues = ccaFieldValues;
	}
}
