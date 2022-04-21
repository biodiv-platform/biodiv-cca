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

public class SubsetCCADataList {

	private Long id;

	private List<FileMeta> files;
	private List<CCAFieldValue> values;
	private List<CCAFieldValue> titlesValues;

	public SubsetCCADataList(CCAData ccaData) {
		this.id = ccaData.getId();
		this.files = new ArrayList<>();
		this.values = new ArrayList<>();
		this.titlesValues = new ArrayList<>();
		init(ccaData);
	}

	private void init(CCAData ccaData) {
		for (Map.Entry<String, CCAFieldValue> e : ccaData.getCcaFieldValues().entrySet()) {
			CCAFieldValue fieldValue = e.getValue();

			switch (fieldValue.getType()) {
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public List<CCAFieldValue> getTitlesValues() {
		return titlesValues;
	}

	public void setTitlesValues(List<CCAFieldValue> titlesValues) {
		this.titlesValues = titlesValues;
	}
}
