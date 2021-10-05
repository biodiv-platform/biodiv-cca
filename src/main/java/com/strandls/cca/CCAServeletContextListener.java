/**
 * 
 */
package com.strandls.cca;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.strandls.cca.controller.CCAControllerModule;
import com.strandls.cca.dao.CCADaoModule;
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

				MongoClient mongoClient = null;
				try {
					mongoClient = MongoClients.create(CCAConfig.getProperty("mongoUrl"));
					CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
							MongoClientSettings.getDefaultCodecRegistry(),
							CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
					MongoDatabase db = mongoClient.getDatabase(CCAConfig.getProperty("mongoDB"))
							.withCodecRegistry(pojoCodecRegistry);
					bind(MongoDatabase.class).toInstance(db);
					bind(MongoClient.class).toInstance(mongoClient);
				} catch (MongoException e) {
					logger.error(e.getMessage());
				} finally {
					// mongoClient.close();
				}

				Map<String, String> props = new HashMap<>();
				props.put("javax.ws.rs.Application", ApplicationConfig.class.getName());
				props.put("jersey.config.server.provider.packages", "com");
				props.put("jersey.config.server.wadl.disableWadl", "true");

				bind(ServletContainer.class).in(Scopes.SINGLETON);

				serve("/api/*").with(ServletContainer.class, props);
			}
		}, new CCAControllerModule(), new CCAServiceModule(), new CCADaoModule());
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		Injector injector = (Injector) servletContextEvent.getServletContext().getAttribute(Injector.class.getName());
		MongoClient mongoClient = injector.getInstance(MongoClient.class);
		mongoClient.close();
	}
}
