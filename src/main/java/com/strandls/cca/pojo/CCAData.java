package com.strandls.cca.pojo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strandls.activity.pojo.MailData;
import com.strandls.activity.pojo.UserGroupActivity;
import com.strandls.activity.pojo.UserGroupMailData;
import com.strandls.cca.pojo.fields.value.GeometryFieldValue;
import com.strandls.cca.service.impl.LogActivities;
import com.strandls.cca.util.CCAUtil;
import com.strandls.user.controller.UserServiceApi;
import com.strandls.user.pojo.User;
import com.strandls.userGroup.controller.UserGroupSerivceApi;
import com.strandls.userGroup.pojo.UserGroupIbp;

public class CCAData extends BaseEntity {

	private final Logger logger = LoggerFactory.getLogger(CCAData.class);

	private static final String CCA_DATA = "ccaData";

	private String shortName;

	private List<Double> centroid = new ArrayList<>();

	private Set<String> allowedUsers = new HashSet<>();

	private Set<String> followers = new HashSet<>();

	private Set<String> usergroups = new HashSet<>();

	private CCALocation location;

	private int richTextCount, textFieldCount, traitsFieldCount;

	private Map<String, CCAFieldValue> ccaFieldValues;

	public void reComputeCentroid() {
		centroid = new ArrayList<>();
		Double x = 0.0;
		Double y = 0.0;
		boolean coordinateFound = false;
		Long n = 0L;
		for (Map.Entry<String, CCAFieldValue> e : ccaFieldValues.entrySet()) {
			if (e.getValue().getType().equals(FieldType.GEOMETRY)) {
				GeometryFieldValue fieldValue = (GeometryFieldValue) e.getValue();
				List<Double> c = fieldValue.getCentroid();
				if (!c.isEmpty()) {
					coordinateFound = true;
					x += c.get(0);
					y += c.get(1);
					n++;
				}
			}
		}
		if (!coordinateFound)
			return;
		x /= n;
		y /= n;
		centroid.add(x);
		centroid.add(y);
	}

	public Set<String> getAllowedUsers() {
		return allowedUsers;
	}

	public void setAllowedUsers(Set<String> allowedUsers) {
		this.allowedUsers = allowedUsers;
	}

	public Set<String> getFollowers() {
		return followers;
	}

	public void setFollowers(Set<String> followers) {
		this.followers = followers;
	}

	public int getTextFieldCount() {
		return textFieldCount;
	}

	public void setTextFieldCount(int textFieldCount) {
		this.textFieldCount = textFieldCount;
	}

	public int getRichTextCount() {
		return richTextCount;
	}

	public void setRichTextCount(int richTextCount) {
		this.richTextCount = richTextCount;
	}

	public int getTraitsFieldCount() {
		return traitsFieldCount;
	}

	public void setTraitsFieldCount(int traitsFieldCount) {
		this.traitsFieldCount = traitsFieldCount;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public List<Double> getCentroid() {
		return centroid;
	}

	public void setCentroid(List<Double> centroid) {
		this.centroid = centroid;
	}

	public Map<String, CCAFieldValue> getCcaFieldValues() {
		return ccaFieldValues;
	}

	public void setCcaFieldValues(Map<String, CCAFieldValue> ccaFieldValues) {
		this.ccaFieldValues = ccaFieldValues;
	}

	public Set<String> getUsergroups() {
		return usergroups;
	}

	public void setUsergroups(Set<String> usergroups) {
		this.usergroups = usergroups;
	}

	public CCALocation getLocation() {
		return location;
	}

	public void setLocation(CCALocation location) {
		this.location = location;
	}

	public CCAData overrideFieldData(HttpServletRequest request, CCAData ccaData, LogActivities logActivities,
			String type, Map<String, Object> summaryInfo, CCAData dataInMem, UserServiceApi userService,
			UserGroupSerivceApi userGroupService) {

		this.shortName = ccaData.shortName;
		this.setUpdatedOn(ccaData.getUpdatedOn());

		switch (type.toLowerCase()) {
		case "permission":
			handlePermissionChanges(request, ccaData, logActivities, summaryInfo, dataInMem, userService);
			break;
		case "updateusergroup":
			handleUsergroupChanges(request, ccaData, userGroupService, logActivities, summaryInfo, dataInMem);
			break;
		case "follow":
			handleFollowActions(request, ccaData, logActivities, userService, summaryInfo);
			break;
		case "unfollow":
			handleUnfollowActions(request, ccaData, logActivities, userService, summaryInfo);
			break;
		default:
			break;
		}

		processFieldValues(request, ccaData, summaryInfo, logActivities);
		updateFieldCounts();

		return this;
	}

	private void handlePermissionChanges(HttpServletRequest request, CCAData ccaData, LogActivities logActivities,
			Map<String, Object> summaryInfo, CCAData dataInMem, UserServiceApi userService) {
		Set<String> ccaAllowedUsers = new HashSet<>(ccaData.getAllowedUsers());
		Set<String> inMemAllowedUsers = new HashSet<>(dataInMem.getAllowedUsers());

		Set<String> removedUsers = new HashSet<>(inMemAllowedUsers);
		removedUsers.removeAll(ccaAllowedUsers);

		Set<String> newUsers = new HashSet<>(ccaAllowedUsers);
		newUsers.removeAll(inMemAllowedUsers);

		if (!removedUsers.isEmpty()) {
			this.allowedUsers.removeAll(removedUsers);
			MailData mailData = CCAUtil.generateMailData(this, "Permission removed", null, summaryInfo, removedUsers);
			String activityDescription = "Removed permission for users " + getusername(userService, removedUsers);
			logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), activityDescription,
					ccaData.getId(), ccaData.getId(), CCA_DATA, ccaData.getId(), "Permission removed", mailData);
		}

		if (!newUsers.isEmpty()) {
			this.allowedUsers.addAll(newUsers);
			this.followers.addAll(newUsers);
			MailData mailData = CCAUtil.generateMailData(this, "Permission added", null, summaryInfo, newUsers);
			String activityDescription = "Added permission for users " + getusername(userService, newUsers);
			logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), activityDescription,
					ccaData.getId(), ccaData.getId(), CCA_DATA, ccaData.getId(), "Permission added", mailData);
		}
	}

	public void handleUsergroupChanges(HttpServletRequest request, CCAData ccaData,
			UserGroupSerivceApi userGroupService, LogActivities logActivities, Map<String, Object> summaryInfo,
			CCAData dataInMem) {

		Set<String> ccaUsergroups = new HashSet<>(ccaData.getUsergroups());
		Set<String> inMemUsergroups = new HashSet<>(dataInMem.getUsergroups());

		Set<String> newUsergroups = new HashSet<>(ccaUsergroups);
		newUsergroups.removeAll(inMemUsergroups);

		Set<String> removedUsergroups = new HashSet<>(inMemUsergroups);
		removedUsergroups.removeAll(ccaUsergroups);

		if (!newUsergroups.isEmpty()) {
			this.usergroups.addAll(newUsergroups);
			processUsergroups(request, newUsergroups, "Usergroup added", userGroupService, logActivities, summaryInfo,
					ccaData);
		}

		if (!removedUsergroups.isEmpty()) {
			this.usergroups.removeAll(removedUsergroups);
			processUsergroups(request, removedUsergroups, "Usergroup removed", userGroupService, logActivities,
					summaryInfo, ccaData);
		}

	}

	private void handleFollowActions(HttpServletRequest request, CCAData ccaData, LogActivities logActivities,
			UserServiceApi userService, Map<String, Object> summaryInfo) {
		this.followers.addAll(ccaData.followers);
		MailData mailData = CCAUtil.generateMailData(this, "Follower added", null, summaryInfo, ccaData.followers);
		String activityDescription = "Followed users " + getusername(userService, ccaData.followers);
		logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), activityDescription,
				ccaData.getId(), ccaData.getId(), CCA_DATA, ccaData.getId(), "Follower added", mailData);

	}

	private void handleUnfollowActions(HttpServletRequest request, CCAData ccaData, LogActivities logActivities,
			UserServiceApi userService, Map<String, Object> summaryInfo) {
		this.followers.removeAll(ccaData.followers);
		MailData mailData = CCAUtil.generateMailData(this, "Follower removed", null, summaryInfo, ccaData.followers);
		String activityDescription = "Unfollowed users " + getusername(userService, ccaData.followers);
		logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), activityDescription,
				ccaData.getId(), ccaData.getId(), CCA_DATA, ccaData.getId(), "Follower removed", mailData);

	}

	private void processFieldValues(HttpServletRequest request, CCAData ccaData, Map<String, Object> summaryInfo,
			LogActivities logActivities) {
		Map<String, CCAFieldValue> fieldsMap = getCcaFieldValues();

		for (Map.Entry<String, CCAFieldValue> e : ccaData.getCcaFieldValues().entrySet()) {
			if (fieldsMap.containsKey(e.getKey())) {

				CCAFieldValue dbFieldValue = this.ccaFieldValues.get(e.getKey());
				CCAFieldValue inputFieldValue = e.getValue();

				String diff = dbFieldValue.computeDiff(inputFieldValue);
				if (diff != null) {
					diff = dbFieldValue.getName() + "\n" + diff;
					MailData mailData = CCAUtil.generateMailData(this, "Data updated", diff, summaryInfo, null);
					logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), diff, ccaData.getId(),
							ccaData.getId(), CCA_DATA, ccaData.getId(), "Data updated", mailData);
				}
				// Persist in DB
				this.ccaFieldValues.put(e.getKey(), e.getValue());

			} else {
				this.ccaFieldValues.put(e.getKey(), e.getValue());

				// Log newly added data entries
				String desc = "Added : " + e.getValue().getName();
				logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), desc, ccaData.getId(),
						ccaData.getId(), CCA_DATA, ccaData.getId(), "Data updated",
						CCAUtil.generateMailData(ccaData, "Data updated", desc, summaryInfo, null));
			}
		}

	}

	private void updateFieldCounts() {
		this.richTextCount = CCAUtil.countFieldType(this, FieldType.RICHTEXT);
		this.textFieldCount = CCAUtil.countFieldType(this, FieldType.TEXT);
		this.traitsFieldCount = CCAUtil.countFieldType(this, FieldType.SINGLE_SELECT_RADIO)
				+ CCAUtil.countFieldType(this, FieldType.MULTI_SELECT_CHECKBOX)
				+ CCAUtil.countFieldType(this, FieldType.SINGLE_SELECT_DROPDOWN)
				+ CCAUtil.countFieldType(this, FieldType.MULTI_SELECT_DROPDOWN);
	}

	public void translate(CCATemplate template) {
		Map<String, CCAField> translatedFields = template.getAllFields();
		for (Map.Entry<String, CCAFieldValue> e : getCcaFieldValues().entrySet()) {
			String fieldId = e.getKey();
			CCAFieldValue ccaFieldValue = e.getValue();
			if (translatedFields.containsKey(fieldId)) {
				CCAField field = translatedFields.get(fieldId);
				ccaFieldValue.translate(field);
			}
		}
	}

	public String getusername(UserServiceApi userService, Set<String> users) {
		String userNames = "";
		String separator = "";
		for (String user : users) {
			User userDetails;
			try {
				userDetails = userService.getUser(user);
				userNames = userNames.concat(separator + "@[" + userDetails.getUserName() + "](" + user + ")");
				separator = ",  ";
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return userNames;
	}

	public void processUsergroups(HttpServletRequest request, Set<String> usergroups, String action,
			UserGroupSerivceApi userGroupService, LogActivities logActivities, Map<String, Object> summaryInfo,
			CCAData ccaData) {
		ObjectMapper om = new ObjectMapper();

		for (String userGroupId : usergroups) {
			try {
				UserGroupIbp ugIbp = userGroupService.getIbpData(userGroupId);
				UserGroupActivity ugActivity = new UserGroupActivity();
				ugActivity.setFeatured(null);
				ugActivity.setUserGroupId(ugIbp.getId());
				ugActivity.setUserGroupName(ugIbp.getName());
				ugActivity.setWebAddress(ugIbp.getWebAddress());

				String description = om.writeValueAsString(ugActivity);

				MailData mailData = CCAUtil.generateMailData(ccaData, action, null, summaryInfo, allowedUsers);
				List<UserGroupMailData> userGroupMailData = CCAUtil.generateUserGroupMailData(userGroupService,
						usergroups);

				mailData.setUserGroupData(userGroupMailData);

				logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), description,
						ccaData.getId(), ccaData.getId(), CCA_DATA, Long.parseLong(userGroupId),
						action.equals("Usergroup added") ? "Posted resource" : "Removed resource", mailData);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

}
