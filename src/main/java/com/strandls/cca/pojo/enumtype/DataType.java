package com.strandls.cca.pojo.enumtype;

import java.util.List;

import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCAFieldValues;
import com.strandls.cca.util.ValidationUtil;

import net.vz.mongodb.jackson.internal.stream.JacksonDBObject;

public enum DataType {

	TEXT("TEXT"), RICHTEXT("RICHTEXT"), NUMBER("NUMBER"), NUMBER_RANGE("NUMBER_RANGE"), DATE("DATE"),
	DATE_RANGE("DATE_RANGE"), RADIO("RADIO"), CHECKBOX("CHECKBOX"), SINGLE_SELECT("SINGLE_SELECT"),
	MULTI_SELECT("MULTI_SELECT"), GEOMETRY("GEOMETRY"), FILE("FILE"), HEADING("HEADING");

	private String value;

	private DataType(String value) {
		this.value = value;
	}

	public static DataType fromString(final String value) {
		for (DataType dataType : DataType.values()) {
			if (dataType.value.equals(value))
				return dataType;
		}
		throw new IllegalArgumentException(value);
	}

	public String getValue() {
		return value;
	}

	public void validate(CCAFieldValues fieldValue, CCAField field) {
		List<String> values = fieldValue.getValue();
		switch (this) {
		case TEXT:
			break;
		case RICHTEXT:
			break;
		case NUMBER:
			if (values.size() != 1 || !ValidationUtil.isNumeric(values.get(0))
					|| !ValidationUtil.isRanged(values, field))
				throw new IllegalArgumentException(
						"Number format exception with : " + field.getName() + " value : " + values);
			break;
		case NUMBER_RANGE:
			if (values.size() != 2 || !ValidationUtil.allNumeric(values) || !ValidationUtil.isRanged(values, field))
				throw new IllegalArgumentException(
						"Number format exception with : " + field.getName() + " value : " + values);
			break;
		case DATE:
			if(values.size() != 1 || !ValidationUtil.isValidDate(values.get(0)) || !ValidationUtil.isDateRanged(values, field))
				throw new IllegalArgumentException(
						"Date format exception with : " + field.getName() + " value : " + values);
			break;
		case DATE_RANGE:
			break;
		case RADIO:
			break;
		case CHECKBOX:
			break;
		case SINGLE_SELECT:
			break;
		case MULTI_SELECT:
			break;
		case GEOMETRY:
			break;
		case FILE:
			break;
		case HEADING:
			break;
		default:
			throw new IllegalArgumentException("Data validation failed");
		}

		JacksonDBObject<Object> min = field.getValidation().getMin();
		JacksonDBObject<Object> max = field.getValidation().getMax();
		if (min != null && max != null) {
			// TODO : Do the min max validation
		}

	}
}
