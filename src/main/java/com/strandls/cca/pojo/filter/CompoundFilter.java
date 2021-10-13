package com.strandls.cca.pojo.filter;

import java.util.List;

public abstract class CompoundFilter implements IFilter {

	private OperatorType type;
	private List<IFilter> filters;

	public OperatorType getType() {
		return type;
	}

	public void setType(OperatorType type) {
		this.type = type;
	}

	public List<IFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<IFilter> filters) {
		this.filters = filters;
	}

}
