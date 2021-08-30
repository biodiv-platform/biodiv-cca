package com.strandls.cca.pojo;

import java.util.Date;
import java.util.List;

import com.strandls.cca.pojo.enumtype.Platform;

import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.ObjectId;

public class CCATemplate {

	@Id
	@ObjectId
	private String id;

	private String name;

	private String description;

	private String shortName;

	private Platform platform;

	private Date createOn;

	private Date updatedOn;

	private List<CCAField> fields;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Platform getPlatform() {
		return platform;
	}
	
	public void setPlatform(Platform platform) {
		this.platform = platform;
	}
	
	public Date getCreateOn() {
		return createOn;
	}

	public void setCreateOn(Date createOn) {
		this.createOn = createOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public List<CCAField> getFields() {
		return fields;
	}

	public void setFields(List<CCAField> fields) {
		this.fields = fields;
	}
}
