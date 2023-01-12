package com.strandls.cca.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Projections;
import com.strandls.cca.CCAConstants;
import com.strandls.cca.dao.CCATemplateDao;
import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.FieldType;

public class BsonProjectionUtil {

	private BsonProjectionUtil() {
	}

	/**
	 * This method gives projection for the list page of master
	 * 
	 * @return
	 */
	public static Bson getProjectionsForListPage(CCATemplateDao templateDao, String shortName) {
		List<String> fieldNames = new ArrayList<>();

		// Compulsory field from the CCA Data - Need to change if there is modification
		// in the Model
		fieldNames.add(CCAConstants.ID);
		fieldNames.add(CCAConstants.SHORT_NAME);
		fieldNames.add(CCAConstants.USER_ID);
		fieldNames.add("centroid");
		fieldNames.add("createdOn");
		fieldNames.add("updatedOn");

		// Take a master template as reference and get all the isSummary column to be
		// projected.
		if (shortName == null)
			shortName = CCAConstants.MASTER;
		CCATemplate ccaTemplate = templateDao.findByProperty(CCAConstants.SHORT_NAME, shortName, false);
		Iterator<CCAField> it = ccaTemplate.iterator();
		while (it.hasNext()) {
			CCAField ccaField = it.next();
			if (ccaField.getType().equals(FieldType.FILE)) {
				String fieldName = "ccaFieldValues" + "." + ccaField.getFieldId();
				fieldNames.add(fieldName);
			} else if (ccaField.getIsSummaryField().booleanValue()) {
				String fieldName = "ccaFieldValues" + "." + ccaField.getFieldId();
				fieldNames.add(fieldName);
			}
		}

		return Projections.include(fieldNames);
	}
}
