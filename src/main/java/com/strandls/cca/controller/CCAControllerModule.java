/**
 * 
 */
package com.strandls.cca.controller;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * 
 * @author vilay
 *
 */
public class CCAControllerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(CCATemplateController.class).in(Scopes.SINGLETON);
		bind(CCADataController.class).in(Scopes.SINGLETON);
	}
}
