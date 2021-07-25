/**
 * 
 */
package com.strandls.cca;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoURI;
import com.strandls.cca.controller.CCAControllerModule;
import com.strandls.cca.service.impl.CCAServiceModule;

/**
 * 
 * @author vilay
 *
 */
public class CCAServeletContextListener extends GuiceServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(CCAServeletContextListener.class);

	@Override
	protected Injector getInjector() {

		return Guice.createInjector(new ServletModule() {
			@Override
			protected void configureServlets() {
				
				try {
					MongoURI mongoURI = new MongoURI(CCAConfig.getProperty("mongoUrl"));
					Mongo mongo = new Mongo(mongoURI);
					DB db = mongo.getDB(CCAConfig.getProperty("mongoDB"));
					bind(DB.class).toInstance(db);
				} catch (MongoException | UnknownHostException e) {
					logger.error(e.getMessage());
				}
				
				Map<String, String> props = new HashMap<>();
				props.put("javax.ws.rs.Application", ApplicationConfig.class.getName());
				props.put("jersey.config.server.provider.packages", "com");
				props.put("jersey.config.server.wadl.disableWadl", "true");

				bind(ServletContainer.class).in(Scopes.SINGLETON);

				serve("/api/*").with(ServletContainer.class, props);
			}
		}, new CCAControllerModule(), new CCAServiceModule());
	}
}
