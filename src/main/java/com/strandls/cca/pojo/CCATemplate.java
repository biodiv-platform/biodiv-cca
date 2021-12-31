package com.strandls.cca.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.strandls.cca.ApiConstants;
import com.strandls.cca.CCAConfig;
import com.strandls.cca.CCAConstants;
import com.strandls.cca.util.DFSTreeIterator;
import com.strandls.cca.util.Permissions;

public class CCATemplate extends BaseEntity {

	/*
	 * Taking the name and description from the translations
	 */
	@BsonIgnore
	private String name;
	@BsonIgnore
	private String description;

	private String shortName;

	private List<Permissions> permissions = new ArrayList<>();

	private List<Platform> platform;

	private List<CCAField> fields = new ArrayList<>();

	@BsonIgnore
	private String language;

	@JsonIgnore
	@BsonProperty("translations")
	private Map<String, CCATemplateTranslations> translations = new HashMap<>();

	/**
	 * This method take care of translation while getting the record
	 * 
	 * @param language
	 * @return
	 */
	public CCATemplate translate(String language) {

		Iterator<CCAField> it = iterator();
		while (it.hasNext()) {
			CCAField ccaField = it.next();
			ccaField.translate(language);
		}

		CCATemplateTranslations ccaTemplateTranslations = getTranslations().get(language);
		if (ccaTemplateTranslations == null) {
			ccaTemplateTranslations = getTranslations().get(CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE));
		}
		if (ccaTemplateTranslations == null)
			throw new IllegalArgumentException(
					"No translation support for given language and default language as well for " + this.shortName);
		return ccaTemplateTranslations.translate(this);
	}

	public CCATemplate pullTranslationFromMaster(CCATemplate master) {

		// If the current template is master then no need to populate translation
		if (CCAConstants.MASTER.equals(this.getShortName()))
			return this;

		Map<String, CCAField> masterFields = master.getAllFields();

		// For the field if it is new then
		Iterator<CCAField> it = iterator();
		while (it.hasNext()) {
			CCAField field = it.next();
			String fieldId = field.getFieldId();
			CCAField masterField = masterFields.get(fieldId);
			// Field is of master type, copied from master and it is not present in the DB.
			if (masterField != null && field.getIsMasterField().booleanValue()) {
				field.pullTranslationFromMaster(masterField);
			}
		}
		return this;
	}

	/**
	 * This will add the translation in specified language
	 * 
	 * @param template
	 * @param language
	 * @return
	 */
	public CCATemplate addUpdateTranslation(CCATemplate template, String language) {

		getTranslations().putAll(template.getTranslations());

		CCATemplateTranslations ccaTemplateTranslations = new CCATemplateTranslations();
		ccaTemplateTranslations.addUpdateTranslation(this);
		getTranslations().put(language, ccaTemplateTranslations);

		Map<String, CCAField> fieldsMap = template.getAllFields();
		Iterator<CCAField> it = iterator();
		while (it.hasNext()) {
			CCAField field = it.next();
			CCAField fieldInDB = fieldsMap.get(field.getFieldId());
			field.addUpdateTranslation(fieldInDB, language);
		}
		return translate(language);
	}

	/**
	 * This is iterator over the field
	 * 
	 * @return
	 */
	public Iterator<CCAField> iterator() {
		return new DFSTreeIterator<>(fields);
	}

	/**
	 * Get all the fields in map format
	 * 
	 * @return
	 */
	@JsonIgnore
	@BsonIgnore
	public Map<String, CCAField> getAllFields() {
		Map<String, CCAField> fieldsMap = new HashMap<>();
		Iterator<CCAField> it = iterator();
		while (it.hasNext()) {
			CCAField field = it.next();
			fieldsMap.put(field.getFieldId(), field);
		}
		return fieldsMap;
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

	public List<Permissions> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permissions> permissions) {
		this.permissions = permissions;
	}

	public List<Platform> getPlatform() {
		return platform;
	}

	public void setPlatform(List<Platform> platform) {
		this.platform = platform;
	}

	public List<CCAField> getFields() {
		return fields;
	}

	public void setFields(List<CCAField> fields) {
		this.fields = fields;
	}

	public Map<String, CCATemplateTranslations> getTranslations() {
		return translations;
	}

	public void setTranslations(Map<String, CCATemplateTranslations> translations) {
		this.translations = translations;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}
