package com.strandls.cca.file.upload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.strandls.cca.pojo.CCAData;

public class FileValidationResponse {

	private Map<String, List<CCAData>> correctObject;
	private Map<String, List<Object>> errorObject;

	public FileValidationResponse() {
		correctObject = new HashMap<>();
		errorObject = new HashMap<>();
	}

	public void addCorrectObject(String key, CCAData value) {
		List<CCAData> list;
		if (correctObject.containsKey(key))
			list = correctObject.get(key);
		else
			list = new ArrayList<>();
		
		list.add(value);
		correctObject.put(key, list);
	}

	public void addError(String key, Object value) {
		List<Object> list;
		if(errorObject.containsKey(key))
			list = errorObject.get(key);
		else 
			list = new ArrayList<>();
		list.add(value);
		errorObject.put(key, list);
	}
	
	public Map<String, List<CCAData>> getCorrectObject() {
		return correctObject;
	}

	public Map<String, List<Object>> getErrorObject() {
		return errorObject;
	}

}
