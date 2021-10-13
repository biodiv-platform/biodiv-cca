package com.strandls.cca.pojo.filter;

import org.bson.conversions.Bson;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.strandls.cca.FieldConstants;
import com.strandls.cca.pojo.filter.field.CheckboxFilter;
import com.strandls.cca.pojo.filter.field.DateFilter;
import com.strandls.cca.pojo.filter.field.DateRangeFilter;
import com.strandls.cca.pojo.filter.field.FileFilter;
import com.strandls.cca.pojo.filter.field.GeometryFilter;
import com.strandls.cca.pojo.filter.field.HeaderFilter;
import com.strandls.cca.pojo.filter.field.MultiSelectFilter;
import com.strandls.cca.pojo.filter.field.NumberFilter;
import com.strandls.cca.pojo.filter.field.NumberRangeFilter;
import com.strandls.cca.pojo.filter.field.RadioFilter;
import com.strandls.cca.pojo.filter.field.RichtextFilter;
import com.strandls.cca.pojo.filter.field.SingleSelectFilter;
import com.strandls.cca.pojo.filter.field.TextFilter;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type", visible = true, defaultImpl = CompoundFilter.class)
@JsonSubTypes({ @JsonSubTypes.Type(value = CheckboxFilter.class, name = FieldConstants.CHECKBOX),
		@JsonSubTypes.Type(value = DateFilter.class, name = FieldConstants.DATE),
		@JsonSubTypes.Type(value = DateRangeFilter.class, name = FieldConstants.DATE_RANGE),
		@JsonSubTypes.Type(value = FileFilter.class, name = FieldConstants.FILE),
		@JsonSubTypes.Type(value = GeometryFilter.class, name = FieldConstants.GEOMETRY),
		@JsonSubTypes.Type(value = HeaderFilter.class, name = FieldConstants.HEADING),
		@JsonSubTypes.Type(value = MultiSelectFilter.class, name = FieldConstants.MULTI_SELECT),
		@JsonSubTypes.Type(value = NumberFilter.class, name = FieldConstants.NUMBER),
		@JsonSubTypes.Type(value = NumberRangeFilter.class, name = FieldConstants.NUMBER_RANGE),
		@JsonSubTypes.Type(value = RadioFilter.class, name = FieldConstants.RADIO),
		@JsonSubTypes.Type(value = RichtextFilter.class, name = FieldConstants.RICHTEXT),
		@JsonSubTypes.Type(value = SingleSelectFilter.class, name = FieldConstants.SINGLE_SELECT),
		@JsonSubTypes.Type(value = TextFilter.class, name = FieldConstants.TEXT),
		@JsonSubTypes.Type(value = AndFilter.class, name = FieldConstants.AND),
		@JsonSubTypes.Type(value = OrFilter.class, name = FieldConstants.OR) })
public interface IFilter {

	public static final String CCA_FIELD_VALUES = "ccaFieldValues";

	public abstract Bson getFilter();

}
