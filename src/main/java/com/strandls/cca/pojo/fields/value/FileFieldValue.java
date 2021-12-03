package com.strandls.cca.pojo.fields.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.strandls.cca.pojo.CCAFieldValue;

public class FileFieldValue extends CCAFieldValue {

	private List<FileMeta> value;

	public FileFieldValue() {
	}

	public FileFieldValue(String dataValue) {
		List<String> files = Arrays.asList(dataValue.split(","));
		List<FileMeta> fileMetas = new ArrayList<>();
		for(String file : files) {
			FileMeta fileMeta = new FileMeta();
			fileMeta.setPath(file);
			fileMetas.add(fileMeta);
		}
		this.value = fileMetas;
	}

	public List<FileMeta> getValue() {
		return value;
	}

	public void setValue(List<FileMeta> value) {
		this.value = value;
	}

}
