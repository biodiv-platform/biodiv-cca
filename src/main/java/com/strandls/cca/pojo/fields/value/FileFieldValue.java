package com.strandls.cca.pojo.fields.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCAFieldValue;

public class FileFieldValue extends CCAFieldValue {

	private List<FileMeta> value;

	public FileFieldValue() {
	}

	public FileFieldValue(String dataValue) {
		List<String> files = Arrays.asList(dataValue.split(","));
		List<FileMeta> fileMetas = new ArrayList<>();
		for (String file : files) {
			FileMeta fileMeta = new FileMeta();
			fileMeta.setPath(file);
			fileMetas.add(fileMeta);
		}
		this.value = fileMetas;
	}

	@Override
	public String computeDiff(CCAFieldValue value) {
		if (this.value != null) {
			if (this.value.size() > ((FileFieldValue) value).getValue().size()) {
				return "File removed";
			} else if (this.value.size() < ((FileFieldValue) value).getValue().size()) {
				return "File added";
			}
		}
		return null;
	}

	public List<FileMeta> getValue() {
		return value;
	}

	public void setValue(List<FileMeta> value) {
		this.value = value;
	}

	@Override
	public boolean validate(CCAField field) {
		super.validate(field);

		if (field.getIsRequired().booleanValue() && getValue() == null)
			throw new IllegalArgumentException("Field is required");

		if (!field.getIsRequired().booleanValue() && (value == null || value.isEmpty()))
			return true;

		for (FileMeta fileMeta : value) {
			if (!validate(fileMeta))
				return false;
		}
		return true;
	}

	private boolean validate(FileMeta fileMeta) {
		return fileMeta.getPath() != null && !"".equals(fileMeta.getPath());
	}
}
