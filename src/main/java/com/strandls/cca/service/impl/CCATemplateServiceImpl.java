package com.strandls.cca.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.ValueWithLabel;
import com.strandls.cca.pojo.enumtype.DataType;
import com.strandls.cca.service.CCATemplateService;
import com.strandls.cca.util.AbstractService;

import net.vz.mongodb.jackson.JacksonDBCollection;

/**
 * 
 * @author vilay
 *
 */
public class CCATemplateServiceImpl extends AbstractService<CCATemplate> implements CCATemplateService {

	private static final String SHORT_NAME = "shortName";

	@Inject
	public CCATemplateServiceImpl(DB db) {
		super(CCATemplate.class, db);
	}

	@Override
	public CCATemplate getCCAByShortName(String ccaTemplate) {
		JacksonDBCollection<CCATemplate, String> collection = getJacksonDBCollection();
		return collection.findOne(new BasicDBObject(SHORT_NAME, ccaTemplate));
	}

	public void createUniqueIndexOnConextId() {
		JacksonDBCollection<CCATemplate, String> collection = getJacksonDBCollection();
		collection.ensureIndex(new BasicDBObject(SHORT_NAME, 1), new BasicDBObject("unique", true));
	}

	@Override
	public CCATemplate save(CCATemplate context) {
		JacksonDBCollection<CCATemplate, String> collection = getJacksonDBCollection();

		CCATemplate contextToUpdate = collection.findOne(new BasicDBObject(SHORT_NAME, context.getShortName()));
		if (contextToUpdate != null)
			throw new IllegalArgumentException(
					"Can't create new with same short name. Either update or create new one");

		addFieldId(context.getFields());
		context.setCreateOn(new Timestamp(new Date().getTime()));
		context.setUpdatedOn(new Timestamp(new Date().getTime()));
		context = super.save(context);

		return collection.findOne(new BasicDBObject(SHORT_NAME, context.getShortName()));
	}

	@Override
	public CCATemplate update(CCATemplate context) {
		JacksonDBCollection<CCATemplate, String> collection = getJacksonDBCollection();

		CCATemplate contextToUpdate = collection.findOne(new BasicDBObject(SHORT_NAME, context.getShortName()));

		if (contextToUpdate == null)
			throw new IllegalArgumentException("Can't update the template, template doesnot exit");

		addFieldId(context.getFields());

		context.setUpdatedOn(new Timestamp(new Date().getTime()));
		collection.update(contextToUpdate, context);

		return collection.findOne(new BasicDBObject(SHORT_NAME, context.getShortName()));
	}

	private void addFieldId(List<CCAField> ccaFields) {
		if (ccaFields == null || ccaFields.isEmpty())
			return;

		for (CCAField ccaField : ccaFields) {
			if (ccaField.getFieldId() == null) {
				ccaField.setFieldId(UUID.randomUUID().toString());
				ccaField.setCreateOn(new Timestamp(new Date().getTime()));
				ccaField.setUpdatedOn(new Timestamp(new Date().getTime()));
			} else {
				
				// TODO : update timestamp work need to be done
			}
			validateField(ccaField);
			addFieldId(ccaField.getChildren());
		}
	}

	private void validateField(CCAField ccaField) {
		DataType dataType = ccaField.getType();

		List<ValueWithLabel> valueOptions = ccaField.getValueOptions();

		switch (dataType) {
		case SINGLE_SELECT:
		case MULTI_SELECT:
		case CHECKBOX:
		case RADIO:
			if (valueOptions == null || valueOptions.isEmpty()) {
				throw new IllegalArgumentException("Value options not provided");
			}
			break;
		case DATE:
		case DATE_RANGE:
		case GEOMETRY:
		case NUMBER:
		case NUMBER_RANGE:
		case TEXT:
		case HEADING:
		case RICHTEXT:
		case FILE:
			break;
		default:
			throw new IllegalArgumentException("Invalid data type");
		}

	}

	@Override
	public List<CCATemplate> getAllCCATemplate() {
		DBCollection dbCollection = getDBCollection();
		JacksonDBCollection<CCATemplate, String> jacksonCollection = JacksonDBCollection.wrap(dbCollection,
				CCATemplate.class, String.class);
		DBObject keys = new BasicDBObject();
		keys.put("id", 1);
		keys.put("name", 1);
		keys.put("description", 1);
		keys.put(SHORT_NAME, 1);
		keys.put("createOn", 1);
		keys.put("updatedOn", 1);
		keys.put("platform", 1);
		return jacksonCollection.find(new BasicDBObject(), keys).toArray();
	}

	@Override
	public CCATemplate remove(String shortName) {
		JacksonDBCollection<CCATemplate, String> collection = getJacksonDBCollection();

		CCATemplate template = collection.findOne(new BasicDBObject(SHORT_NAME, shortName));
		
		return remove(template);
	}

}
