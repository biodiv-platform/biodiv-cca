/**
 * 
 */
package com.strandls.cca.service.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.strandls.cca.service.CCADataService;
import com.strandls.cca.service.CCAMetaDataService;

/**
 * 
 * @author vilay
 *
 */
public class CCAServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(CCADataService.class).to(CCADataServiceImpl.class).in(Scopes.SINGLETON);
		bind(CCAMetaDataService.class).to(CCAMetaDataServiceImpl.class).in(Scopes.SINGLETON);
	}
}
