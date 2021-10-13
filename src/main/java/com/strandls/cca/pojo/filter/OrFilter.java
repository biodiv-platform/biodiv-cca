package com.strandls.cca.pojo.filter;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

public class OrFilter extends CompoundFilter {

	@Override
	public Bson getFilter() {
		List<Bson> orFilters = new ArrayList<>();
		for (IFilter filter : getFilters()) {
			orFilters.add(Filters.and(filter.getFilter()));
		}
		return Filters.or(orFilters);

	}

}
