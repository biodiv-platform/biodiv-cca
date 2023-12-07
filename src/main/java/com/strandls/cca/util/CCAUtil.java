package com.strandls.cca.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.strandls.activity.pojo.CCAMailData;
import com.strandls.activity.pojo.MailData;
import com.strandls.activity.pojo.UserGroupMailData;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCAFieldValue;
import com.strandls.cca.pojo.FieldType;
import com.strandls.userGroup.ApiException;
import com.strandls.userGroup.controller.UserGroupSerivceApi;
import com.strandls.userGroup.pojo.UserGroupIbp;

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
			if (type == FieldType.RICHTEXT && e.getValue().getType().equals(type)
					&& e.getValue().validateValue(e.getValue())) {
				count++;
			} else if (e.getValue().getType().equals(type)) {
				count++;
			}
		}
		return count;
	}

	public static MailData generateMailData(CCAData ccaData, String title, String description,
			Map<String, Object> summary, Set<String> userIds) {
		MailData mailData = null;
		try {
			CCAMailData ccaMailData = new CCAMailData();
			ccaMailData.setAuthorId(Long.parseLong(ccaData.getUserId()));
			ccaMailData.setId(ccaData.getId());
			ccaMailData.setLocation("India");

			Map<String, Object> data = new HashMap<>();
			data.put("id", ccaData.getId());
			data.put("url", "data/show/" + ccaData.getId());
			data.put("time", ccaData.getUpdatedOn());
			data.put("updated_time", ccaData.getUpdatedOn());
			data.put("followedUser", ccaData.getFollowers());

			Map<String, Object> activity = new HashMap<>();

			if (title != null && description != null) {
				activity.put("title", title);
				activity.put("description", description);
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				Date date = new Date();
				activity.put("time", formatter.format(date));
			}

			if (title != null && title.equals("Permission added")) {
				data.put("permission", userIds);
			}
			if (title != null && title.equals("Permission removed")) {
				data.put("permission removed", userIds);
			} else if (title != null && title.equals("Follower added")) {
				data.put("follower", userIds);
			}

			Map<String, Object> tempData = new HashMap<>();
			tempData.put("data", data);
			tempData.put("activity", activity);

			if (summary != null)
				tempData.put("summary", summary);

			tempData.put("recipient", ccaData.getFollowers());
			tempData.put("owner", ccaData.getUserId());

			ccaMailData.setData(tempData);
			mailData = new MailData();
			mailData.setCcaMailData(ccaMailData);
		} catch (Exception e) {
			e.getMessage();
		}
		return mailData;
	}

	public static List<UserGroupMailData> generateUserGroupMailData(UserGroupSerivceApi userGroupService,
			Set<String> usergroups) {
		List<UserGroupMailData> userGroupMailData = new ArrayList<>();
		List<UserGroupIbp> updatedUG = new ArrayList<>();

		for (String usergroupId : usergroups) {
			UserGroupIbp userGroupIbp;
			try {
				userGroupIbp = userGroupService.getIbpData(usergroupId);
				updatedUG.add(userGroupIbp);
			} catch (ApiException e) {
				e.getMessage();
			}
		}

		if (!updatedUG.isEmpty()) {
			for (UserGroupIbp ug : updatedUG) {
				UserGroupMailData ugMail = new UserGroupMailData();
				ugMail.setIcon(ug.getIcon());
				ugMail.setId(ug.getId());
				ugMail.setName(ug.getName());
				ugMail.setWebAddress(ug.getWebAddress());

				userGroupMailData.add(ugMail);
			}
		}
		return userGroupMailData;
	}
}
