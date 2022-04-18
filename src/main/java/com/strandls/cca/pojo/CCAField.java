package com.strandls.cca.pojo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.Facet;
import com.strandls.cca.ApiConstants;
import com.strandls.cca.CCAConfig;
import com.strandls.cca.FieldConstants;
import com.strandls.cca.dao.CCATemplateDao;
import com.strandls.cca.pojo.fields.CheckboxField;
import com.strandls.cca.pojo.fields.DateField;
import com.strandls.cca.pojo.fields.DateRangeField;
import com.strandls.cca.pojo.fields.FileField;
import com.strandls.cca.pojo.fields.GeometryField;
import com.strandls.cca.pojo.fields.HeaderField;
import com.strandls.cca.pojo.fields.MultiSelectField;
import com.strandls.cca.pojo.fields.NumberField;
import com.strandls.cca.pojo.fields.NumberRangeField;
import com.strandls.cca.pojo.fields.RadioField;
import com.strandls.cca.pojo.fields.RichtextField;
import com.strandls.cca.pojo.fields.SingleSelectField;
import com.strandls.cca.pojo.fields.TextAreaField;
import com.strandls.cca.pojo.fields.TextField;
import com.strandls.cca.pojo.filter.IFilter;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({ @JsonSubTypes.Type(value = CheckboxField.class, name = FieldConstants.MULTI_SELECT_CHECKBOX),
		@JsonSubTypes.Type(value = DateField.class, name = FieldConstants.DATE),
		@JsonSubTypes.Type(value = DateField.class, name = FieldConstants.YEAR),
		@JsonSubTypes.Type(value = DateRangeField.class, name = FieldConstants.DATE_RANGE),
		@JsonSubTypes.Type(value = FileField.class, name = FieldConstants.FILE),
		@JsonSubTypes.Type(value = GeometryField.class, name = FieldConstants.GEOMETRY),
		@JsonSubTypes.Type(value = HeaderField.class, name = FieldConstants.HEADING),
		@JsonSubTypes.Type(value = MultiSelectField.class, name = FieldConstants.MULTI_SELECT_DROPDOWN),
		@JsonSubTypes.Type(value = NumberField.class, name = FieldConstants.NUMBER),
		@JsonSubTypes.Type(value = NumberRangeField.class, name = FieldConstants.NUMBER_RANGE),
		@JsonSubTypes.Type(value = RadioField.class, name = FieldConstants.SINGLE_SELECT_RADIO),
		@JsonSubTypes.Type(value = RichtextField.class, name = FieldConstants.RICHTEXT),
		@JsonSubTypes.Type(value = SingleSelectField.class, name = FieldConstants.SINGLE_SELECT_DROPDOWN),
		@JsonSubTypes.Type(value = TextField.class, name = FieldConstants.TEXT),
		@JsonSubTypes.Type(value = TextAreaField.class, name = FieldConstants.TEXT_AREA) })
@BsonDiscriminator()
public abstract class CCAField implements IChildable<CCAField> {

	private String fieldId;

	/*
	 * Not storing name, question and help text in the mongo DB Taking it from the
	 * translation
	 */
	@BsonIgnore
	private String name;
	@BsonIgnore
	private String question;
	@BsonIgnore
	private String helpText = "";

	private Boolean isRequired = false;
	private Boolean isMasterField = false;
	private Boolean isSummaryField = false;
	private Boolean isFilterable = false;
	private Boolean isTitleColumn = false;

	private FieldType type;
	private Date createdOn;
	private Date updatedOn;
	private List<CCAField> children;

	@JsonIgnore
	@BsonProperty("translations")
	private Map<String, CCAFieldTranslations> translations = new HashMap<>();

	@JsonIgnore
	@BsonIgnore
	public String getFieldHierarchy() {
		return IFilter.CCA_FIELD_VALUES + "." + getFieldId() + ".value";
	}

	@JsonIgnore
	@BsonIgnore
	public Facet getGroupAggregation(MultivaluedMap<String, String> queryParameter, CCATemplateDao templateDao,
			ObjectMapper objectMapper, String userId) throws JsonProcessingException {
		return null;
	}

	/**
	 * Do the translation for CCA field
	 * 
	 * @param language
	 * @return
	 */
	public CCAField translate(String language) {
		CCAFieldTranslations ccaFieldTranslations = translations.get(language);
		if (ccaFieldTranslations == null)
			ccaFieldTranslations = translations.get(CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE));
		if (ccaFieldTranslations == null)
			throw new IllegalArgumentException(
					"No translation support for given language and default language as well for + " + this.name
							+ " Id : " + this.fieldId);
		return ccaFieldTranslations.translate(this);
	}

	/**
	 * Add update the language for current field based
	 * 
	 * @param ccaField - This is the history node. Need to copy previous translation
	 *                 for this
	 * @param language - Language for translation
	 * @return
	 */
	public CCAField addUpdateTranslation(CCAField ccaField, String language) {
		// Add previous translation to the current field
		if (ccaField != null)
			getTranslations().putAll(ccaField.getTranslations());

		// Create the translation for given language.
		CCAFieldTranslations ccaFieldTranslations = new CCAFieldTranslations();
		ccaFieldTranslations.addUpdateTranslation(this);
		getTranslations().put(language, ccaFieldTranslations);

		// Translate the current node.
		return translate(language);
	}

	public void pullTranslationFromMaster(CCAField ccaField) {
		this.name = ccaField.getName();
		this.helpText = ccaField.getHelpText();
		this.question = ccaField.getQuestion();
	}

	public void validate() {
		// Nothing to do here
	}

	public String equals(Object obj, String language) {
		if (!(obj instanceof CCAField))
			return null;

		if (language == null)
			language = CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE);

		CCAField field = (CCAField) obj;

		CCAFieldTranslations fieldTranslations = getTranslations().get(language);
		if (fieldTranslations == null)
			return null;

		String diff = "";

		diff += fieldTranslations.equalsTo(field);

		diff += getIsRequired().equals(field.getIsRequired()) ? ""
				: "Required : " + getIsRequired() + "→" + field.getIsRequired() + "\n";

		diff += getIsMasterField().equals(field.getIsMasterField()) ? ""
				: "Master field : " + getIsMasterField() + "→" + field.getIsMasterField() + "\n";

		diff += getIsSummaryField().equals(field.getIsSummaryField()) ? ""
				: "Summary field : " + getIsSummaryField() + "→" + field.getIsSummaryField() + "\n";

		diff += getIsFilterable().equals(field.getIsFilterable()) ? ""
				: "Filterable : " + getIsFilterable() + "→" + field.getIsFilterable() + "\n";

		return diff.equals("") ? null : diff;

	}

	public String getFieldId() {
		return fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	public Boolean getIsMasterField() {
		return isMasterField;
	}

	public void setIsMasterField(Boolean isMasterField) {
		this.isMasterField = isMasterField;
	}

	public Boolean getIsSummaryField() {
		return isSummaryField;
	}

	public void setIsSummaryField(Boolean isSummaryField) {
		this.isSummaryField = isSummaryField;
	}

	public Boolean getIsFilterable() {
		return isFilterable;
	}

	public void setIsFilterable(Boolean isFilterable) {
		this.isFilterable = isFilterable;
	}

	public Boolean getIsTitleColumn() {
		return isTitleColumn;
	}

	public void setIsTitleColumn(Boolean isTitleColumn) {
		this.isTitleColumn = isTitleColumn;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createOn) {
		this.createdOn = createOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public List<CCAField> getChildren() {
		return children;
	}

	public void setChildren(List<CCAField> children) {
		this.children = children;
	}

	public Map<String, CCAFieldTranslations> getTranslations() {
		return translations;
	}

	public void setTranslations(Map<String, CCAFieldTranslations> translations) {
		this.translations = translations;
	}
}
