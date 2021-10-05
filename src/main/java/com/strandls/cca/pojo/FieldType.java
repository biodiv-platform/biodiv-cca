package com.strandls.cca.pojo;

public enum FieldType {

	TEXT("TEXT"), RICHTEXT("RICHTEXT"), NUMBER("NUMBER"), NUMBER_RANGE("NUMBER_RANGE"), DATE("DATE"),
	DATE_RANGE("DATE_RANGE"), RADIO("RADIO"), CHECKBOX("CHECKBOX"), SINGLE_SELECT("SINGLE_SELECT"),
	MULTI_SELECT("MULTI_SELECT"), GEOMETRY("GEOMETRY"), FILE("FILE"), HEADING("HEADING");

	private String value;

	private FieldType(String value) {
		this.value = value;
	}

	public static FieldType fromString(final String value) {
		for (FieldType dataType : FieldType.values()) {
			if (dataType.value.equals(value))
				return dataType;
		}
		throw new IllegalArgumentException(value);
	}

	public String getValue() {
		return value;
	}

	/*
	public boolean validate(CCAFieldValue fieldValue, CCAField field) {
		List<String> values = fieldValue.getValue();

		if (!field.getIsRequired().booleanValue() && values.isEmpty())
			return true;

		switch (this) {
		case TEXT:
			break;
		case RICHTEXT:
			break;
		case NUMBER:
			if (values.size() != 1 || !ValidationUtil.isNumeric(values.get(0))
					|| !ValidationUtil.isRanged(values, field))
				return false;
			break;
		case NUMBER_RANGE:
			if (values.size() != 2 || !ValidationUtil.allNumeric(values) || !ValidationUtil.isRanged(values, field))
				return false;
			break;
		case DATE:
			if (values.size() != 1 || !ValidationUtil.isValidDate(values.get(0))
					|| !ValidationUtil.isDateRanged(values, field))
				return false;
			break;
		case DATE_RANGE:
			if (values.size() != 2 || !ValidationUtil.isValidDate(values.get(0))
					|| !ValidationUtil.isDateRanged(values, field))
				return false;
			break;
		case RADIO:
		case SINGLE_SELECT:
			//if (values.size() != 1 || !ValidationUtil.belongsTo(field.getValueOptions(), values.get(0)))
			//	return false;
			break;
		case CHECKBOX:
		case MULTI_SELECT:
			for (String v : values) {
				//if (!ValidationUtil.belongsTo(field.getValueOptions(), v))
				//	return false;
			}
			break;
		case GEOMETRY:
			if(values.size() == 1) {
				// Do something here 
			} else if(values.size() == 2) {
				// Validate here for simple latitude longitude
				double lat = Double.parseDouble(values.get(0));
				double lon = Double.parseDouble(values.get(1));
				if(lat < -90 || lat > 90 || lon <-180 || lon > 180)
					return false;
			} else 
				return false;
			break;
		case FILE:
			break;
		case HEADING:
			break;
		default:
			throw new IllegalArgumentException("Data validation failed");
		}

		return true;
	}*/
}
