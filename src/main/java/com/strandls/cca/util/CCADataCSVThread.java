package com.strandls.cca.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;
import com.strandls.activity.controller.ActivitySerivceApi;
import com.strandls.cca.pojo.CCAData;
import com.strandls.user.ApiException;
import com.strandls.user.controller.UserServiceApi;
import com.strandls.user.pojo.DownloadLogData;

public class CCADataCSVThread implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(CCADataCSVThread.class);
	private final String modulePath = "/data-archive/listpagecsv";
	private final String basePath = "/app/data/biodiv";
	private List<CCAData> ccaData;
	private String authorId;
	private String notes;
	private String url;
	private UserServiceApi userServiceApi;
	private ActivitySerivceApi activityService;

	public CCADataCSVThread() {
		super();
	}

	public CCADataCSVThread(List<CCAData> ccaData, String authorId, String notes, String url,
			UserServiceApi userServiceApi, ActivitySerivceApi activityService) {
		super();

		this.ccaData = ccaData;
		this.authorId = authorId;
		this.notes = notes;
		this.url = url;
		this.userServiceApi = userServiceApi;
		this.activityService = activityService;

	}

	@Override
	public void run() {

		CCADataCSVUtil obUtil = new CCADataCSVUtil();
		String fileName = obUtil.getCsvFileNameDownloadPath();

		String filePath = basePath + File.separator + fileName;

		CSVWriter writer = obUtil.getCsvWriter(filePath);

		obUtil.writeIntoCSV(writer, obUtil.getCsvHeaders());

		String fileGenerationStatus = "Pending";
		String fileType = "CSV";

		try {
			fileGenerationStatus = "SUCCESS";

			obUtil.insertListToCSV(ccaData, writer);
			activityService.ccaDownloadMail(fileName, fileType);

		} catch (Exception e) {
			logger.error("file generation failed @ " + filePath + " due to - " + e.getMessage());
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
