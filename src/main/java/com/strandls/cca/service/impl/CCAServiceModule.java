/**
 * 
 */
package com.strandls.cca.service.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.strandls.cca.service.CCAContextService;

/**
 * 
 * @author vilay
 *
 */
public class CCAServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(CCAContextService.class).to(CCAContextServiceImpl.class).in(Scopes.SINGLETON);
	}
}
