package com.strandls.cca.pojo.fields.value;

import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCAFieldValue;
import com.strandls.cca.pojo.FieldType;

public class CCAFieldValueFactory {

	private CCAFieldValueFactory() {
	}

	public static CCAFieldValue createFieldValue(CCAField ccaField, String dataValue) {
		CCAFieldValue fieldValue;

		FieldType dataType = ccaField.getType();

		switch (dataType) {
		case CHECKBOX:
			fieldValue = new CheckboxFieldValue(dataValue);
			break;
		case DATE:
			fieldValue = new DateFieldValue(dataValue);
			break;
		case DATE_RANGE:
			fieldValue = new DateRangeFieldValue(dataValue);
			break;
		case FILE:
			fieldValue = new FileFieldValue(dataValue);
			break;
		case GEOMETRY:
			fieldValue = new GeometryFieldValue(dataValue);
			break;
		case HEADING:
			fieldValue = new HeaderFieldValue(dataValue);
			break;
		case MULTI_SELECT:
			fieldValue = new MultiSelectFieldValue(dataValue);
			break;
		case NUMBER:
			fieldValue = new NumberFieldValue(dataValue);
			break;
		case NUMBER_RANGE:
			fieldValue = new NumberRangeFieldValue(dataValue);
			break;
		case RADIO:
			fieldValue = new RadioFieldValue(dataValue);
			break;
		case RICHTEXT:
			fieldValue = new RichtextFieldValue(dataValue);
			break;
		case SINGLE_SELECT:
			fieldValue = new SingleSelectFieldValue(dataValue);
			break;
		case TEXT:
			fieldValue = new TextFieldValue(dataValue);
			break;
		default:
			throw new IllegalArgumentException("Invalid data type");
		}

		fieldValue.setFieldId(ccaField.getFieldId());
		fieldValue.setName(ccaField.getName());
		fieldValue.setType(ccaField.getType());

		return fieldValue;
	}
}
