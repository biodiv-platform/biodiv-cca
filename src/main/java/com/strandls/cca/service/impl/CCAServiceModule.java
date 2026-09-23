/**
 * 
 */
package com.strandls.cca.service.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.strandls.cca.service.CCADataService;
import com.strandls.cca.service.CCATemplateService;
import com.strandls.cca.service.GBIFObservationService;

/**
 * 
 * @author vilay
 *
 */
public class CCAServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(CCATemplateService.class).to(CCATemplateServiceImpl.class).in(Scopes.SINGLETON);
		bind(CCADataService.class).to(CCADataServiceImpl.class).in(Scopes.SINGLETON);
		bind(GBIFObservationService.class).to(GBIFObservationServiceImpl.class).in(Scopes.SINGLETON);
	}
}
