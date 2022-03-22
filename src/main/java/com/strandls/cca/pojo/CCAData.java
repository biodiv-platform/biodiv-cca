package com.strandls.cca.pojo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;

import com.strandls.cca.pojo.fields.value.GeometryFieldValue;
import com.strandls.cca.service.impl.LogActivities;

public class CCAData extends BaseEntity {

	private String shortName;

	private List<Double> centroid = new ArrayList<>();

	private Set<String> allowedUsers = new HashSet<> ();

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

	public CCAData overrideFieldData(HttpServletRequest request, CCAData ccaData, LogActivities logActivities) {

		this.shortName = ccaData.shortName;
		this.allowedUsers = ccaData.allowedUsers;
		Map<String, CCAFieldValue> fieldsMap = getCcaFieldValues();

		for (Map.Entry<String, CCAFieldValue> e : ccaData.getCcaFieldValues().entrySet()) {
			if (fieldsMap.containsKey(e.getKey())) {

				CCAFieldValue dbFieldValue = this.ccaFieldValues.get(e.getKey());
				CCAFieldValue inputFieldValue = e.getValue();

				String diff = dbFieldValue.computeDiff(inputFieldValue);
				if (diff != null) {
					diff = dbFieldValue.getName() + "\n" + diff;
					logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), diff, ccaData.getId(),
							ccaData.getId(), "ccaData", ccaData.getId(), "Data updated");
				}
				// Persist in DB
				this.ccaFieldValues.put(e.getKey(), e.getValue());

			} else {
				this.ccaFieldValues.put(e.getKey(), e.getValue());

				// Log newly added data entries
				String desc = "Added : " + e.getValue().getName();
				logActivities.logCCAActivities(request.getHeader(HttpHeaders.AUTHORIZATION), desc, ccaData.getId(),
						ccaData.getId(), "ccaData", ccaData.getId(), "Data created");
			}
		}
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

}
