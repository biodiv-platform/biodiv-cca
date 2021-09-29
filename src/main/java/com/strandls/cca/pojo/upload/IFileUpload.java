package com.strandls.cca.pojo.upload;

import java.io.IOException;
import java.util.List;

import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.service.CCADataService;
import com.strandls.cca.service.CCATemplateService;

public interface IFileUpload {

	/**
	 * This method does the validation and upload both
	 * @throws IOException 
	 */
	public List<CCAData> upload(CCATemplateService ccaTemplateService, CCADataService ccaDataService) throws IOException;

	public FileValidationResponse validate(CCATemplate ccaTemplate, CCADataService ccaDataService) throws IOException;
}
