package com.strandls.cca.pojo;

public class CCATemplateTranslations {

	private String name;
	private String description;

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

	public CCATemplate translate(CCATemplate ccaTemplate) {
		ccaTemplate.setName(name);
		ccaTemplate.setDescription(description);
		return ccaTemplate;
	}

	public void addUpdateTranslation(CCATemplate template) {
		this.setName(template.getName());
		this.setDescription(template.getDescription());
	}

}
