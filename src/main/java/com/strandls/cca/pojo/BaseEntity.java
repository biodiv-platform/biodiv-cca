package com.strandls.cca.pojo;

import java.util.Date;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.strandls.cca.IdInterface;

public class BaseEntity implements IdInterface {

	@BsonId
	@BsonProperty("_id")
	@JsonIgnore
	private String basonId;

	private Long id;

	private String userId;
	private Date createdOn;
	private Date updatedOn;

	@JsonIgnore
	@BsonProperty
	private Boolean isDeleted = false;

	public String getBasonId() {
		return basonId;
	}

	public void setBasonId(String basonId) {
		this.basonId = basonId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

}
