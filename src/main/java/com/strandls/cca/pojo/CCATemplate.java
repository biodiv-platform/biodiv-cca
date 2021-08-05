package com.strandls.cca.pojo;

import java.util.List;

import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.ObjectId;

public class CCATemplate {

	@Id
	@ObjectId
	private String id;

	private String name;

	private String description;

	private String templateId;

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

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public List<CCAField> getFields() {
		return fields;
	}

	public void setFields(List<CCAField> fields) {
		this.fields = fields;
	}

}
