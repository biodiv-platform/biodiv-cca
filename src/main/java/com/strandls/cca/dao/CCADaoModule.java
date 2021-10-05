package com.strandls.cca.dao;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class CCADaoModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(CCATemplateDao.class).in(Scopes.SINGLETON);
		bind(CCADataDao.class).in(Scopes.SINGLETON);
	}
}
