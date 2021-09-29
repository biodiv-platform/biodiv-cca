package com.strandls.cca.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.List;

import com.strandls.cca.pojo.CCAField;

import net.vz.mongodb.jackson.internal.stream.JacksonDBObject;

public class ValidationUtil {

	private ValidationUtil() {
	}

	public static boolean isValidDate(String inDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:ms");
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(inDate.trim());
		} catch (ParseException pe) {
			return false;
		}
		return true;
	}

	public static boolean allNumeric(List<String> numList) {
		for (String num : numList) {
			if (!isNumeric(num))
				return false;
		}
		return true;
	}

	public static boolean isNumeric(String number) {
		ParsePosition parsePosition = new ParsePosition(0);
		NumberFormat.getInstance().parse(number, parsePosition);
		return number.length() == parsePosition.getIndex();
	}

	// Check for the isRangedValidation
	public static boolean isRanged(List<String> ranged, CCAField ccaField) {
		JacksonDBObject<Object> maxObject = ccaField.getValidation().getMax();
		JacksonDBObject<Object> minObject = ccaField.getValidation().getMin();

		if (maxObject == null || minObject == null)
			return true;

		Double max = Double.parseDouble(maxObject.toString());
		Double min = Double.parseDouble(minObject.toString());

		if (ranged.size() == 1) {
			Double value = Double.parseDouble(ranged.get(0));

			if (value < min || value > max)
				return false;

		} else if (ranged.size() == 2) {
			Double minValue = Double.parseDouble(ranged.get(0));
			Double maxValue = Double.parseDouble(ranged.get(1));

			if (minValue > maxValue)
				return false;

			if (minValue < min || minValue > max || maxValue < min || maxValue > max)
				return false;
		}

		return true;
	}

	public static boolean isDateRanged(List<String> values, CCAField field) {
		if(values.size() == 1) {
			
		} else if(values.size() == 2) {
			
		}
		return true;
	}

}
