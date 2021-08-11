/**
 * 
 */
package com.strandls.cca.service.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.strandls.cca.service.CCATemplateService;

/**
 * 
 * @author vilay
 *
 */
public class CCAServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(CCATemplateService.class).to(CCATemplateServiceImpl.class).in(Scopes.SINGLETON);
	}
}
