package com.strandls.cca.pojo;

import java.util.List;

import net.vz.mongodb.jackson.ObjectId;

public class CCAContext {

	@ObjectId
	private String _id;

	private String name;

	private String description;

	private String contextId;

	private List<CCAField> fields;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
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

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public List<CCAField> getFields() {
		return fields;
	}

	public void setFields(List<CCAField> fields) {
		this.fields = fields;
	}

}
