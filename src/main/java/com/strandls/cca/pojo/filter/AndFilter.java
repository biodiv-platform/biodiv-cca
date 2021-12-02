package com.strandls.cca.pojo.filter;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

public class AndFilter extends CompoundFilter {

	@Override
	public Bson getFilter() {
		List<Bson> andFilters = new ArrayList<>();
		for (IFilter filter : getFilters()) {
			andFilters.add(filter.getFilter());
		}
		return Filters.and(andFilters);
	}

}
