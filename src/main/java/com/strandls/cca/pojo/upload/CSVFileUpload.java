package com.strandls.cca.pojo.upload;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVReader;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCAFieldValues;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.service.CCADataService;
import com.strandls.cca.service.CCATemplateService;

public class CSVFileUpload implements IFileUpload {

	private static final String COLUMN_SEPARATOR = ";";

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
		} catch (IllegalArgumentException e) {
			uploadResponse.addError("Header: ", e.getMessage());
		}

		if (uploadResponse.getErrorObject().size() > 0) {
			reader.close();
			return uploadResponse;
		}

		// Validate the data
		while (it.hasNext()) {
			String[] data = it.next();
			try {
				CCAData ccaData = convertToCCAData(data, ccaTemplate);
				ccaDataService.validateData(ccaData, ccaTemplate);
				uploadResponse.addCorrectObject(data[0], ccaData);
			} catch (IllegalArgumentException e) {
				uploadResponse.addError(data[0], e.getMessage());
			}
		}
		reader.close();
		return uploadResponse;
	}

	private CCAData convertToCCAData(String[] data, CCATemplate ccaTemplate) {
		Map<String, Integer> fieldToColumnIndex = metaData.getFieldToColumnIndex();

		Date date = new Date();

		CCAData ccaData = new CCAData();
		ccaData.setShortName(ccaTemplate.getShortName());
		ccaData.setCreatedOn(date);
		ccaData.setUpdatedOn(date);
		ccaData.setUserId(userId);
		List<CCAFieldValues> fieldValues = convertToCCADataUtil(data, fieldToColumnIndex, ccaTemplate.getFields());
		ccaData.setCcaFieldValues(fieldValues);

		return ccaData;
	}

	private List<CCAFieldValues> convertToCCADataUtil(String[] data, Map<String, Integer> fieldToColumnIndex,
			List<CCAField> fields) {
		List<CCAFieldValues> fieldValues = new ArrayList<>();
		for (CCAField ccaField : fields) {
			CCAFieldValues fieldValue = new CCAFieldValues();

			fieldValue.setFieldId(ccaField.getFieldId());
			fieldValue.setName(ccaField.getName());

			String fieldId = ccaField.getFieldId();
			List<String> values;
			if (fieldToColumnIndex.containsKey(fieldId)) {
				String dataValue = data[fieldToColumnIndex.get(ccaField.getFieldId())];
				values = Arrays.asList(StringUtils.split(dataValue, COLUMN_SEPARATOR));
			} else
				values = new ArrayList<>();
			fieldValue.setValue(values);
			fieldValue.setChildren(convertToCCADataUtil(data, fieldToColumnIndex, ccaField.getChildren()));

			fieldValues.add(fieldValue);
		}
		return fieldValues;
	}

	private void validataHeader(String[] headers, CCATemplate ccaTemplate) {
		int size = headers.length;
		Map<String, Integer> fieldToColumnIndex = metaData.getFieldToColumnIndex();

		// Validate all the ccaField IDS
		validateWithCCAFields(fieldToColumnIndex, ccaTemplate.getFields());

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

	private void validateWithCCAFields(Map<String, Integer> fieldToColumnIndex, List<CCAField> ccaFields) {

		if (ccaFields.isEmpty())
			return;

		for (CCAField ccaField : ccaFields) {

			if (ccaField.getValidation().getIsRequired().booleanValue()
					&& !fieldToColumnIndex.containsKey(ccaField.getFieldId())) {
				throw new IllegalArgumentException(
						"Missing required field with ID: " + ccaField.getFieldId() + " Name: " + ccaField.getName());
			}

			validateWithCCAFields(fieldToColumnIndex, ccaField.getChildren());
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
