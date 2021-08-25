package com.strandls.cca.service.impl;

import java.util.List;

import javax.inject.Inject;

import com.mongodb.DB;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCAFieldValues;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.service.CCADataService;
import com.strandls.cca.service.CCATemplateService;
import com.strandls.cca.util.AbstractService;

public class CCADataServiceImpl extends AbstractService<CCAData> implements CCADataService {

	@Inject
	private CCATemplateService ccaTemplateService;

	@Inject
	public CCADataServiceImpl(DB db) {
		super(CCAData.class, db);
	}

	@Override
	public CCAData saveOrUpdate(CCAData ccaData) {
		String shortName = ccaData.getShortName();
		CCATemplate ccaTemplate = ccaTemplateService.getCCAByShortName(shortName);

		validateData(ccaData, ccaTemplate);

		ccaData = save(ccaData);
		return ccaData;
	}

	private void validateData(CCAData ccaData, CCATemplate ccaTemplate) {
		validateDataUtil(ccaData.getCcaFieldValues(), ccaTemplate.getFields());
	}

	private void validateDataUtil(List<CCAFieldValues> ccaFieldValues, List<CCAField> fields) {
		if (ccaFieldValues.size() != fields.size())
			throw new IllegalArgumentException("Invalid template mapping");

		for (int i = 0; i < ccaFieldValues.size(); i++) {
			if (!ccaFieldValues.get(i).getFieldId().equals(fields.get(i).getFieldId()))
				throw new IllegalArgumentException("Invalid template mapping");
			
			validateDataUtil(ccaFieldValues.get(i).getChildren(), fields.get(i).getChildren());
		}
	}

	@Override
	public CCAData remove(String id) {
		CCAData ccaData = getById(id);
		return remove(ccaData);
	}

}
