package com.strandls.cca;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CCAConfig {

	private static final Properties properties;
	
	private static final Logger logger = LoggerFactory.getLogger(CCAConfig.class);
	
	private CCAConfig() {}

	static {
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
		properties = new Properties();
		try {
			properties.load(in);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	public static Properties getProperties() {
		return properties;
	}
	
	public static String getProperty(String property) {
		return properties.getProperty(property);
	}
}
