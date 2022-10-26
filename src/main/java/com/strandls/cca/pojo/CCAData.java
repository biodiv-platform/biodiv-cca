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

import com.strandls.activity.pojo.MailData;
import com.strandls.cca.pojo.fields.value.GeometryFieldValue;
import com.strandls.cca.service.impl.LogActivities;
import com.strandls.cca.util.CCAUtil;
import com.strandls.user.controller.UserServiceApi;
import com.strandls.user.pojo.User;

public class CCAData extends BaseEntity {

	private final Logger logger = LoggerFactory.getLogger(CCAData.class);

	private String shortName;

	private List<Double> centroid = new ArrayList<>();

	private Set<String> allowedUsers = new HashSet<> ();

	private Set<String> removedUsers = new HashSet<> ();

	private Set<String> newUsers = new HashSet<> ();

	private Set<String> followers = new HashSet<> ();
 
	private int richTextCount, textFieldCount , traitsFieldCount;

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

	public CCAData overrideFieldData(HttpServletRequest request, CCAData ccaData, LogActivities logActivities, String type, 
			Map<String, Object> summaryInfo ,CCAData dataInMem ,UserServiceApi userService) {

		this.shortName = ccaData.shortName;
		this.setUpdatedOn(ccaData.getUpdatedOn());

		if (type.equalsIgnoreCase("permission")) {
			if(!ccaData.allowedUsers.equals(dataInMem.allowedUsers)) {
				// getting permission removed users
		        for (String prev: dataInMem.getAllowedUsers()){
		        	if(!(ccaData.getAllowedUsers().contains(prev))) {
		        		removedUsers.add(prev);
		        	}
		        }
		        // getting permission added users
		        for (String newUser: ccaData.getAllowedUsers()) {
		        	if(!(dataInMem.getAllowedUsers().contains(newUser))) {
		        		newUsers.add(newUser);
		        	}
		        }

		        if(!removedUsers.isEmpty()) {
		        	this.allowedUsers.removeAll(removedUsers);
					MailData mailData = CCAUtil.generateMailData(this, "Permission removed", null, summaryInfo, removedUsers);
					String activityDescription ="Removed permission for users "+getusername(userService,removedUsers);
					logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), activityDescription,
							ccaData.getId(), ccaData.getId(), "ccaData", ccaData.getId(), "Permission removed", mailData);
		        }

		        if(!newUsers.isEmpty()) {
					this.allowedUsers.addAll(newUsers);
					this.followers.addAll(newUsers);
					MailData mailData = CCAUtil.generateMailData(this, "Permission added", null, summaryInfo, newUsers);
					String activityDescription ="Added permission for users "+getusername(userService,newUsers);
					logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), activityDescription,
							ccaData.getId(), ccaData.getId(), "ccaData", ccaData.getId(), "Permission added", mailData);
		        }
			}
		} else if(type.equalsIgnoreCase("follow")) {
			this.followers.addAll(ccaData.followers);
			MailData mailData = CCAUtil.generateMailData(this, "Follower added", null, summaryInfo, ccaData.followers);
			String activityDescription ="Followed users "+getusername(userService,ccaData.followers);
			logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), activityDescription, ccaData.getId(),
					ccaData.getId(), "ccaData", ccaData.getId(), "Follower added", mailData);
		} else if(type.equalsIgnoreCase("unfollow")) {
			this.followers.removeAll(ccaData.followers);
			MailData mailData = CCAUtil.generateMailData(this, "Follower removed", null, summaryInfo, ccaData.followers);
			String activityDescription ="Unfollowed users "+getusername(userService,ccaData.followers);
			logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), activityDescription, ccaData.getId(),
					ccaData.getId(), "ccaData", ccaData.getId(), "Follower removed", mailData);
		}
  
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
							ccaData.getId(), "ccaData", ccaData.getId(), "Data updated", mailData);
				}
				// Persist in DB
				this.ccaFieldValues.put(e.getKey(), e.getValue());

			} else {
				this.ccaFieldValues.put(e.getKey(), e.getValue());

				// Log newly added data entries
				String desc = "Added : " + e.getValue().getName();
				logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), desc, ccaData.getId(),
						ccaData.getId(), "ccaData", ccaData.getId(), "Data updated", 
						CCAUtil.generateMailData(ccaData, "Data updated", desc, summaryInfo, null));
			}
		}

		this.richTextCount = CCAUtil.countFieldType(this, FieldType.RICHTEXT);
		this.textFieldCount = CCAUtil.countFieldType(this, FieldType.TEXT);
		this.traitsFieldCount = CCAUtil.countFieldType(this, FieldType.SINGLE_SELECT_RADIO)
				+ CCAUtil.countFieldType(this, FieldType.MULTI_SELECT_CHECKBOX)
				+ CCAUtil.countFieldType(this, FieldType.SINGLE_SELECT_DROPDOWN)
				+ CCAUtil.countFieldType(this, FieldType.MULTI_SELECT_DROPDOWN);

		return this;
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

	public String getusername(UserServiceApi userService,Set<String> users) {
		String userNames ="";
		for (String user : users) {
			User userDetails;
			try {
				userDetails = userService.getUser(user);
				userNames = userNames.concat("@["+userDetails.getUserName()+"]("+user+")");
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return userNames;
	}

}
