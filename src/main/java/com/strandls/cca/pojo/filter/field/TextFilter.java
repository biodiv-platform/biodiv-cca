package com.strandls.cca.pojo.filter.field;

import java.util.regex.Pattern;

import javax.ws.rs.DefaultValue;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.strandls.cca.pojo.filter.Filter;

public class TextFilter extends Filter {

	private String value;
	
	@DefaultValue("false")
	private Boolean isExactMatch;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Boolean getIsExactMatch() {
		return isExactMatch;
	}

	public void setIsExactMatch(Boolean isExactMatch) {
		this.isExactMatch = isExactMatch;
	}

	@Override
	public Bson getFilter() {
		String fieldHierarchy = getFieldHierarchy();
		Bson filter;
		if (isExactMatch.booleanValue())
			filter = Filters.eq(fieldHierarchy, value);
		else {
			Pattern pattern = Pattern.compile(".*" + value + ".*", Pattern.CASE_INSENSITIVE);
			filter = Filters.regex(fieldHierarchy, pattern);
		}
		return filter;
	}

}
