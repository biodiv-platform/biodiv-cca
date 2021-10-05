package com.strandls.cca.pojo;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;

import com.strandls.cca.IdInterface;
import com.strandls.cca.util.DFSTreeIterator;

public class CCAData implements IdInterface {

	@BsonId
	private String id;

	private String shortName;
	private String userId;
	private Date createdOn;
	private Date updatedOn;

	private List<CCAFieldValue> ccaFieldValues;

	public Iterator<CCAFieldValue> iterator() {
		return new DFSTreeIterator<>(ccaFieldValues);
	}

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

	public List<CCAFieldValue> getCcaFieldValues() {
		return ccaFieldValues;
	}

	public void setCcaFieldValues(List<CCAFieldValue> ccaFieldValues) {
		this.ccaFieldValues = ccaFieldValues;
	}
}
