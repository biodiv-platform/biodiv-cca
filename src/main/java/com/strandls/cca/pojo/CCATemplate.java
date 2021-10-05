package com.strandls.cca.pojo;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;

import com.strandls.cca.IdInterface;
import com.strandls.cca.util.DFSTreeIterator;

public class CCATemplate implements IdInterface {

	@BsonId
	private String id;

	private String name;

	private String description;

	private String shortName;

	private List<Platform> platform;

	private Date createOn;

	private Date updatedOn;

	private List<CCAField> fields;

	public Iterator<CCAField> iterator() {
		return new DFSTreeIterator<>(fields);
	}

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

	public List<Platform> getPlatform() {
		return platform;
	}

	public void setPlatform(List<Platform> platform) {
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
