package com.strandls.cca.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.opencsv.CSVWriter;
import com.strandls.cca.pojo.CCAData;
import com.strandls.user.pojo.DownloadLog;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class CCADataCSVUtil {
	private final Logger logger = LoggerFactory.getLogger(CCADataCSVUtil.class);

	private final String[] csvCoreHeaders = { "ShortName", "userId", "created on" };

	private final String csvFileDownloadPath = "/app/data/biodiv/data-archive/listpagecsv";
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
			logger.error("CSVWriter error logging - " + e.getMessage());
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
			logger.error("CSVWriter error logging - " + e.getMessage());
		}
	}

	public List<String[]> getCsvHeaders() {
		List<String[]> headers = new ArrayList<String[]>();

		List<String> header = Arrays.asList(csvCoreHeaders);

		headers.add(header.stream().toArray(String[]::new));
		return headers;
	}

	public void insertListToCSV(List<CCAData> records, CSVWriter writer) {

		List<String[]> rowSets = new ArrayList<String[]>();
		for (CCAData record : records) {
			List<String> row = new ArrayList<String>();

			addCoreHeaderValues(row, record, "test");

			rowSets.add(row.stream().toArray(String[]::new));
		}
		writer.writeAll(rowSets);

	}

	private void addCoreHeaderValues(List<String> row, CCAData record, String fileName) {
		try {

			row.add(record.getShortName());
			row.add(record.getUserId());
			row.add(record.getCreatedOn().toString());

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	public DownloadLog createDownloadLogEntity(String filePath, Long authorId, String filterURL, String notes,
			Long offSet, String status, String type) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		DownloadLog entity = new DownloadLog();
		entity.setAuthorId(authorId);
		entity.setFilePath(filePath);
		entity.setFilterUrl(filterURL);
		entity.setNotes(notes);
		entity.setOffsetParam(offSet);
		entity.setCreatedOn(timestamp);
		entity.setStatus(status);
		entity.setType(type);
		entity.setVersion(2L);
		return entity;

	}

}
