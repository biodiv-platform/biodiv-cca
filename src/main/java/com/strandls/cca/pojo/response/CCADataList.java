package com.strandls.cca.pojo.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCAFieldValue;
import com.strandls.cca.pojo.fields.value.FileFieldValue;
import com.strandls.cca.pojo.fields.value.FileMeta;
import com.strandls.cca.pojo.fields.value.GeometryFieldValue;
import com.strandls.cca.pojo.geometry.Feature;

public class CCADataList {

	private String id;
	private String shortName;
	private String userId;

	private Date createdOn;
	private Date updatedOn;

	private GeometryFieldValue geometry;
	private List<FileMeta> files;
	private List<CCAFieldValue> values;

	public CCADataList(CCAData ccaData) {
		this.id = ccaData.getId();
		this.shortName = ccaData.getShortName();
		this.userId = ccaData.getUserId();
		this.createdOn = ccaData.getCreatedOn();
		this.updatedOn = ccaData.getUpdatedOn();

		this.geometry = new GeometryFieldValue();
		this.files = new ArrayList<>();
		this.values = new ArrayList<>();
		init(ccaData);
	}

	private void init(CCAData ccaData) {
		for (Map.Entry<String, CCAFieldValue> e : ccaData.getCcaFieldValues().entrySet()) {
			CCAFieldValue fieldValue = e.getValue();

			switch (fieldValue.getType()) {
			case GEOMETRY:
				List<Feature> features = ((GeometryFieldValue) fieldValue).getValue().getFeatures();
				geometry.getValue().getFeatures().addAll(features);
				break;
			case FILE:
				List<FileMeta> fileMetas = ((FileFieldValue) fieldValue).getValue();
				files.addAll(fileMetas);
				break;
			default:
				values.add(fieldValue);
				break;
			}
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public GeometryFieldValue getGeometry() {
		return geometry;
	}

	public void setGeometry(GeometryFieldValue geometry) {
		this.geometry = geometry;
	}

	public List<FileMeta> getFiles() {
		return files;
	}

	public void setFiles(List<FileMeta> files) {
		this.files = files;
	}

	public List<CCAFieldValue> getValues() {
		return values;
	}

	public void setValues(List<CCAFieldValue> values) {
		this.values = values;
	}

}
