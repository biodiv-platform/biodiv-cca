package com.strandls.cca.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.opencsv.CSVWriter;
import com.strandls.cca.CCAConfig;
import com.strandls.cca.pojo.CCAData;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class CCADataCSVUtil {
	private final Logger logger = LoggerFactory.getLogger(CCADataCSVUtil.class);

	private final String[] csvCoreHeaders = { "ShortName", "userId", "created on" };
	private final String csvFileDownloadPath = CCAConfig.getProperty("csv_file_download_path");

	private CSVWriter writer;

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
			logger.error("CSVWriter error logging - ", e.getMessage());
		}
		return writer;
	}

	public void writeIntoCSV(CSVWriter writer, List<String[]> data) {
		writer.writeAll(data);
	}

	public void closeWriter() {
		try {
			writer.close();
		} catch (IOException e) {
			logger.error("CSVWriter error logging - ", e.getMessage());
		}
	}

	public List<String[]> getCsvHeaders() {

		List<String[]> headers = new ArrayList<>();
		List<String> header = Arrays.asList(csvCoreHeaders);
		headers.add(header.stream().toArray(String[]::new));
		return headers;
	}

	public void insertListToCSV(List<CCAData> records, CSVWriter writer) {

		List<String[]> rowSets = new ArrayList<>();
		for (CCAData recordData : records) {
			List<String> row = new ArrayList<String>();
			addCoreHeaderValues(row, recordData);
			rowSets.add(row.stream().toArray(String[]::new));
		}
		writer.writeAll(rowSets);

	}

	private void addCoreHeaderValues(List<String> row, CCAData recordData) {

		try {
			row.add(recordData.getShortName());
			row.add(recordData.getUserId());
			row.add(recordData.getCreatedOn().toString());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

}
