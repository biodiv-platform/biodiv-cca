package com.strandls.cca.pojo.upload;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FileUploadFactory {

	private static final String[] availableFileType = { "csv" };

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private FileUploadFactory() {
	}

	private static InputStream readFiledata(FormDataMultiPart multiPart, String type) {
		FormDataBodyPart formdata = multiPart.getField(type);
		if (formdata == null) {
			return null;
		}
		return formdata.getValueAs(InputStream.class);
	}

	public static IFileUpload getFileUpload(FormDataMultiPart multiPart, String type, String userId) throws IOException {
		InputStream metaDataInputStream = readFiledata(multiPart, "metadata");
		InputStreamReader inputStreamReader = new InputStreamReader(metaDataInputStream, StandardCharsets.UTF_8);
		FileMetadata fileMetadata = objectMapper.readValue(inputStreamReader, FileMetadata.class);

		if ("csv".equalsIgnoreCase(type)) {
			InputStream inputStream = readFiledata(multiPart, type);
			return new CSVFileUpload(inputStream, fileMetadata, userId);
		}
		throw new IOException("We are not supporting the file type : " + type
				+ " for upload. Available file type are : " + availableFileType);
	}

}
