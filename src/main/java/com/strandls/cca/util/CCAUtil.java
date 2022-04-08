package com.strandls.cca.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCAFieldValue;
import com.strandls.cca.pojo.FieldType;
import com.strandls.cca.pojo.fields.value.GeometryFieldValue;

public class CCAUtil {

	private CCAUtil() {
	}

	public static final String COLUMN_SEPARATOR = "\\|";

	private static final String[] ALLOWED_DATE_FORMAT = { "YYYY", "MM/YYYY", "MM-YYYY", "DD/MM/YYYY", "DD-MM-YYYY",
			"DD/MM/YYYY HH:mm:ss:ms", "dd-MM-yyyy HH:mm:ss:ms" };

	public static Date parseDate(String value) {
		for (String allowedFormat : ALLOWED_DATE_FORMAT) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(allowedFormat);
			dateFormat.setLenient(false);
			try {
				return dateFormat.parse(value.replaceAll("\\s*", ""));
			} catch (ParseException pe) {
			}
		}
		return null;
	}

	public static boolean isRanged(Date date, Date min, Date max) {
		return date.compareTo(min) >= 0 && date.compareTo(max) <= 0;
	}

	public static boolean isRanged(Double date, Double min, Double max) {
		return date.compareTo(min) >= 0 && date.compareTo(max) <= 0;
	}

	public static int countFieldType(CCAData ccaData, FieldType type) {
		int count = 0;
		for (Map.Entry<String, CCAFieldValue> e : ccaData.getCcaFieldValues().entrySet()) {
			if (e.getValue().getType().equals(type)) {
				count++;
			}
		}
		return count;
	}
}
