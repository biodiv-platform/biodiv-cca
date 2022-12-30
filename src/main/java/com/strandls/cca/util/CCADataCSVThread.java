package com.strandls.cca.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;
import com.strandls.activity.controller.ActivitySerivceApi;
import com.strandls.cca.CCAConfig;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.user.ApiException;
import com.strandls.user.controller.UserServiceApi;
import com.strandls.user.pojo.DownloadLogData;

public class CCADataCSVThread implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(CCADataCSVThread.class);
	private final String modulePath = CCAConfig.getProperty("csv_module_path ");
	private final String basePath = CCAConfig.getProperty("base_path");
	private List<CCAData> ccaData;
	private String notes;
	private String url;
	private UserServiceApi userServiceApi;
	private ActivitySerivceApi activityService;
	private CCATemplate template;

	public CCADataCSVThread() {
		super();
	}

	public CCADataCSVThread(List<CCAData> ccaData, String notes, String url, UserServiceApi userServiceApi,
			ActivitySerivceApi activityService, CCATemplate template) {
		super();

		this.ccaData = ccaData;
		this.notes = notes;
		this.url = url;
		this.userServiceApi = userServiceApi;
		this.activityService = activityService;
		this.template = template;

	}

	@Override
	public void run() {

		CCADataCSVUtil obUtil = new CCADataCSVUtil();
		String fileName = obUtil.getCsvFileNameDownloadPath();

		String filePath = basePath + File.separator + fileName;

		CSVWriter writer = obUtil.getCsvWriter(filePath);

		List<String> headerNames = obUtil.getCsvHeaders(template, "name");

		List<String> headerFieldIds = obUtil.getCsvHeaders(template, "fieldId");

		obUtil.writeIntoCSV(writer, headerNames);

		String fileGenerationStatus = "Pending";
		String fileType = "CSV";

		try {
			fileGenerationStatus = "SUCCESS";

			obUtil.insertListToCSV(ccaData, writer, headerFieldIds);
			activityService.ccaDownloadMail(fileName, fileType);

		} catch (Exception e) {
			logger.error(e.getMessage());
			fileGenerationStatus = "FAILED";
		} finally {
			obUtil.closeWriter();
			DownloadLogData data = new DownloadLogData();
			data.setFilePath(modulePath + File.separator + fileName);
			data.setFileType(fileType);
			data.setFilterUrl(url);
			data.setStatus(fileGenerationStatus);
			data.setNotes(notes);
			data.setSourcetype("CCA");
			try {
				userServiceApi.logDocumentDownload(data);
			} catch (ApiException e) {
				logger.error(e.getMessage());
			}
		}
		if (fileGenerationStatus.equalsIgnoreCase("failed")) {
			try {
				Files.deleteIfExists(Paths.get(filePath));
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}

	}

}
