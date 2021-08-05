/**
 * 
 */
package com.strandls.cca.service.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.strandls.cca.service.CCAService;

/**
 * 
 * @author vilay
 *
 */
public class CCAServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(CCAService.class).to(CCAServiceImpl.class).in(Scopes.SINGLETON);
	}
}
