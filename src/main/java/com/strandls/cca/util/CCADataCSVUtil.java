package com.strandls.cca.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import com.strandls.cca.CCAConfig;
import com.strandls.cca.FieldConstants;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCATemplate;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class CCADataCSVUtil {
	private final Logger logger = LoggerFactory.getLogger(CCADataCSVUtil.class);

	private final String csvFileDownloadPath = CCAConfig.getProperty("csv_file_download_path");

	private CSVWriter writer;

	private static final String CCA_ID = "CCA ID";

	private static final String NODE_NAME = "name";

	public String getCsvFileNameDownloadPath() {

		Date date = new Date();
		String fileName = "cca_" + date.getTime() + ".csv";
		String filePathName;

		filePathName = csvFileDownloadPath + File.separator + fileName;

		File file = new File(filePathName);
		try {
			boolean isFileCreated = file.createNewFile();
			if (isFileCreated)
				return fileName;
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	public CSVWriter getCsvWriter(String fileName) {

		FileWriter outputfile = null;
		try {
			outputfile = new FileWriter(new File(fileName));
			writer = new CSVWriter(outputfile);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return writer;
	}

	public void writeIntoCSV(CSVWriter writer, List<String> data) {

		List<String[]> headers = new ArrayList<>();
		headers.add(data.stream().toArray(String[]::new));

		writer.writeAll(headers);
	}

	public void closeWriter() {
		try {
			writer.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	public List<String> getCsvHeaders(CCATemplate template) {

		List<String> header = new ArrayList<>();
		// custom header
		header.add(CCA_ID);

		ObjectMapper om = new ObjectMapper();
		String jsonInString;

		try {
			jsonInString = om.writeValueAsString(template);

			JsonNode root = om.readTree(jsonInString);
			JsonNode fieldNode = root.path("fields");

			if (fieldNode.isArray()) {
				for (JsonNode field : fieldNode) {

					String name = field.path(NODE_NAME).asText();
					JsonNode childNode = field.path("children");
					header.add(name);

					if (childNode.isArray()) {
						for (JsonNode child : childNode) {
							String childName = child.path(NODE_NAME).asText();
							header.add(childName);

						}
					}
				}
			}

		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
		}
		return header;
	}

	public void insertListToCSV(List<CCAData> records, CSVWriter writer, List<String> headers) {

		List<String[]> rowSets = new ArrayList<>();
		for (CCAData recordData : records) {
			List<String> row = new ArrayList<>();
			addHeaderValues(row, recordData, headers);
			rowSets.add(row.stream().toArray(String[]::new));
		}
		writer.writeAll(rowSets);

	}

	private void addHeaderValues(List<String> row, CCAData recordData, List<String> headers) {

		try {

			ObjectMapper om = new ObjectMapper();
			String jsonInString;

			jsonInString = om.writeValueAsString(recordData.getCcaFieldValues().values());

			JsonNode root = om.readTree(jsonInString);

			for (String header : headers) {

				Boolean flag = false;
				if (header.equals(CCA_ID)) {
					row.add(recordData.getId().toString());
					flag = true;
				} else if (header.equals("Location")) {
					row.add(recordData.getCentroid().toString());
					flag = true;
				} else {
					for (int i = 0; i < root.size(); i++) {
						String name = root.get(i).get(NODE_NAME).asText();

						if (name.equals(header)) {
							String value = stripHtmlTags(root.get(i).get("value").toString());
							String type = root.get(i).get("type").asText();
							if (!type.equals(FieldConstants.TEXT) || !type.equals(FieldConstants.NUMBER)) {
								value = processValue(value, type);

							}
							row.add(value);
							flag = true;
						}
					}

				}

				if (!flag) {
					row.add("");
				}

			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	private String stripHtmlTags(String htmlString) {
		htmlString = htmlString.replaceAll("<[^>]*>", "");
		return htmlString;
	}

	private String processValue(String value, String type) {
		ObjectMapper om = new ObjectMapper();
		JsonNode root;
		String processedValue = "";
		switch (type) {

		case FieldConstants.DATE:
		case FieldConstants.YEAR:
			Date date = new Date(Long.parseLong(value));
			String format = type.equals(FieldConstants.DATE) ? "dd/MM/yyyy" : "yyyy";
			SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
			processedValue = dateFormatter.format(date);
			break;

		case FieldConstants.SINGLE_SELECT_RADIO:
		case FieldConstants.SINGLE_SELECT_DROPDOWN:
		case FieldConstants.MULTI_SELECT_CHECKBOX:

			try {
				root = om.readTree(value);
				String nodeType = root.getNodeType().toString();
				if (nodeType.equals("ARRAY")) {
					for (int index = 0; index < root.size(); index++) {
						String label = root.get(index).get("label").asText();
						processedValue = index == 0 ? label : processedValue + " | " + label;
					}

				} else {
					processedValue = root.get("label").asText();
				}
			} catch (JsonProcessingException e) {
				logger.error(e.getMessage());
			}
			break;

		default:
			processedValue = value;
			break;

		}

		return processedValue;
	}

}
