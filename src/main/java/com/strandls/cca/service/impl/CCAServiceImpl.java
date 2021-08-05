package com.strandls.cca.service.impl;

import java.util.List;
import java.util.UUID;

import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.service.CCAService;
import com.strandls.cca.util.AbstractService;

import net.vz.mongodb.jackson.JacksonDBCollection;

/**
 * 
 * @author vilay
 *
 */
public class CCAServiceImpl extends AbstractService<CCATemplate> implements CCAService {
	
	private static final String TEMPLATE_ID = "templateId";

	@Inject
	public CCAServiceImpl(DB db) {
		super(CCATemplate.class, db);
	}

	@Override
	public CCATemplate getCCAByTemplateId(String ccaTemplate) {
		JacksonDBCollection<CCATemplate, String> collection = getJacksonDBCollection();
		return collection.findOne(new BasicDBObject(TEMPLATE_ID, ccaTemplate));
	}

	public void createUniqueIndexOnConextId() {
		JacksonDBCollection<CCATemplate, String> collection = getJacksonDBCollection();
		collection.ensureIndex(new BasicDBObject(TEMPLATE_ID, 1), new BasicDBObject("unique", true));
	}

	@Override
	public CCATemplate saveOrUpdate(CCATemplate context) {
		JacksonDBCollection<CCATemplate, String> collection = getJacksonDBCollection();

		CCATemplate contextToUpdate = collection.findOne(new BasicDBObject(TEMPLATE_ID, context.getTemplateId()));
		addFieldId(context.getFields());
		if (contextToUpdate == null)
			context = super.save(context);
		else
			collection.update(contextToUpdate, context);

		return collection.findOne(new BasicDBObject(TEMPLATE_ID, context.getTemplateId()));
	}

	private void addFieldId(List<CCAField> ccaFields) {
		if (ccaFields == null || ccaFields.isEmpty())
			return;

		for (CCAField ccaField : ccaFields) {
			if (ccaField.getFieldId() == null) {
				ccaField.setFieldId(UUID.randomUUID().toString());
			}
			addFieldId(ccaField.getChildrens());
		}
	}

}
