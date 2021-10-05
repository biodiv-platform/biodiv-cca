package com.strandls.cca.file.upload;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.opencsv.CSVReader;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCAFieldValue;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.fields.value.CCAFieldValueFactory;
import com.strandls.cca.service.CCADataService;
import com.strandls.cca.service.CCATemplateService;

public class CSVFileUpload implements IFileUpload {

	protected InputStream inputStream;
	protected FileMetadata metaData;
	protected String userId;
	private CCATemplate ccaTemplate;

	public CSVFileUpload(InputStream inputStream, FileMetadata metaData, String userId) {
		this.inputStream = inputStream;
		this.metaData = metaData;
		this.userId = userId;
	}

	@Override
	public FileValidationResponse validate(CCATemplate ccaTemplate, CCADataService ccaDataService) throws IOException {
		InputStreamReader inputStreamReader = new InputStreamReader(this.inputStream);
		CSVReader reader = new CSVReader(inputStreamReader);
		Iterator<String[]> it = reader.iterator();
		String[] headers = it.next();

		FileValidationResponse uploadResponse = new FileValidationResponse();

		try {
			validataHeader(headers, ccaTemplate);
		} catch (Exception e) {
			uploadResponse.addError("Header: ", e.getMessage());
		}

		if (uploadResponse.getErrorObject().size() > 0) {
			reader.close();
			return uploadResponse;
		}

		// Validate the data
		int line = 1;
		while (it.hasNext()) {
			String[] data = it.next();
			try {
				CCAData ccaData = convertToCCAData(data, ccaTemplate);
				ccaDataService.validateData(ccaData, ccaTemplate);
				uploadResponse.addCorrectObject(data[0], ccaData);
			} catch (Exception e) {
				uploadResponse.addError(data[0] + " line : " + line, e.getMessage());
			}
			line++;
		}
		reader.close();
		return uploadResponse;
	}

	@SuppressWarnings("rawtypes")
	private CCAData convertToCCAData(String[] data, CCATemplate ccaTemplate) {
		Map<String, Integer> fieldToColumnIndex = metaData.getFieldToColumnIndex();

		Date date = new Date();

		CCAData ccaData = new CCAData();
		ccaData.setShortName(ccaTemplate.getShortName());
		ccaData.setCreatedOn(date);
		ccaData.setUpdatedOn(date);
		ccaData.setUserId(userId);
		Map<String, CCAFieldValue> fieldValues = convertToCCADataUtil(data, fieldToColumnIndex, ccaTemplate);
		ccaData.setCcaFieldValues(fieldValues);

		return ccaData;
	}

	/**
	 * Operating this one recursively rather than using iterator, reason being we
	 * want the child value of each field as well.
	 * 
	 * @param data
	 * @param fieldToColumnIndex
	 * @param fields
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Map<String, CCAFieldValue> convertToCCADataUtil(String[] data, Map<String, Integer> fieldToColumnIndex,
			CCATemplate ccaTemplate) {

		Map<String, CCAFieldValue> fieldValues = new HashMap<>();

		Iterator<CCAField> it = ccaTemplate.iterator();
		while (it.hasNext()) {
			CCAField ccaField = it.next();
			Integer index = fieldToColumnIndex.get(ccaField.getFieldId());
			if (index != null) {
				CCAFieldValue fieldValue = CCAFieldValueFactory.createFieldValue(ccaField, data[index]);
				fieldValues.put(ccaField.getFieldId(), fieldValue);
			}
		}

		return fieldValues;
	}

	private void validataHeader(String[] headers, CCATemplate ccaTemplate) {
		int size = headers.length;
		Map<String, Integer> fieldToColumnIndex = metaData.getFieldToColumnIndex();

		// Validate all the ccaField IDS
		validateWithCCAFields(fieldToColumnIndex, ccaTemplate);

		// Validate all the indices as well
		Set<Integer> indices = new HashSet<>();
		for (Entry<String, Integer> e : fieldToColumnIndex.entrySet()) {
			String fieldId = e.getKey();
			Integer index = e.getValue();

			if (indices.contains(index))
				throw new IllegalArgumentException("Duplicate index for the ID : " + fieldId);

			if (index >= size)
				throw new IllegalArgumentException("Index value greater than number of column for field : " + fieldId);

		}
	}

	private void validateWithCCAFields(Map<String, Integer> fieldToColumnIndex, CCATemplate ccaTemplate) {

		Iterator<CCAField> it = ccaTemplate.iterator();
		while (it.hasNext()) {
			CCAField ccaField = it.next();
			if (ccaField.getIsRequired().booleanValue() && !fieldToColumnIndex.containsKey(ccaField.getFieldId())) {
				throw new IllegalArgumentException(
						"Missing required field with ID: " + ccaField.getFieldId() + " Name: " + ccaField.getName());
			}
		}
	}

	/**
	 * This method does the validation and upload both
	 * 
	 * @throws IOException
	 */
	@Override
	public List<CCAData> upload(CCATemplateService ccaTemplateService, CCADataService ccaDataService)
			throws IOException {
		if (this.ccaTemplate == null) {
			this.ccaTemplate = ccaTemplateService.getCCAByShortName(metaData.getShortName());
		}
		FileValidationResponse fileValidationResponse = validate(ccaTemplate, ccaDataService);
		return upload(fileValidationResponse, ccaDataService);
	}

	/**
	 * This method required the validation result to process
	 * 
	 * @param uploadResponse
	 * @param ccaTemplate
	 * @return
	 */
	public List<CCAData> upload(FileValidationResponse uploadResponse, CCADataService ccaDataService) {
		if (!uploadResponse.getErrorObject().isEmpty())
			throw new IllegalArgumentException("Error in the validation" + uploadResponse.getErrorObject());

		List<CCAData> ccaDatas = new ArrayList<>();
		for (Map.Entry<String, List<CCAData>> e : uploadResponse.getCorrectObject().entrySet()) {
			ccaDatas.addAll(e.getValue());
		}
		return ccaDataService.insertBulk(ccaDatas);
	}

}
