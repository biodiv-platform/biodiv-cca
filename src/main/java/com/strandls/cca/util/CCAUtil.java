package com.strandls.cca.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.strandls.activity.pojo.CCAMailData;
import com.strandls.activity.pojo.MailData;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCAFieldValue;
import com.strandls.cca.pojo.FieldType;

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
			if(type == FieldType.RICHTEXT && e.getValue().getType().equals(type) && e.getValue().validateValue(e.getValue())) {
				count++;
			} else if(e.getValue().getType().equals(type)) {
				count++;
			}
		}
		return count;
	}
	
	public static MailData generateMailData(CCAData ccaData, String label, String value) {
		MailData mailData = null;
		try {
			CCAMailData ccaMailData = new CCAMailData();
			ccaMailData.setAuthorId(Long.parseLong(ccaData.getUserId()));
			ccaMailData.setId(ccaData.getId());
			ccaMailData.setLocation("India");

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("id", ccaData.getId());
			data.put("url", "data/show/"+ ccaData.getId());
			data.put("time", ccaData.getUpdatedOn());
			data.put("followedUser", ccaData.getFollowers());

			if(label != null && value != null ) {
				data.put("label", label);
				data.put("value", value);
			}

			Map<String, Object> activity = new HashMap<String, Object>();
			activity.put("nameOfCCA", ccaData.getShortName());

			Map<String, Object> tempData = new HashMap<String, Object>();
			tempData.put("data", data);
			tempData.put("activity", activity);

			ccaMailData.setData(tempData);
			mailData = new MailData();
			mailData.setCcaMailData(ccaMailData);
		} catch (Exception e) {
			e.getMessage();
		}
		return mailData;
	}
}
