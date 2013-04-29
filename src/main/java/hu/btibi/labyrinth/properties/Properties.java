package hu.btibi.labyrinth.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Properties {
	INSTANCE;
	
	private final Logger LOG = LoggerFactory.getLogger(Properties.class);
	
	private final static String LABYRINTH_CONFIG_FILE_PATH_ENV = "LABYRINTH_CONFIG_FILE_PATH";
	private final java.util.Properties properties = new java.util.Properties();

	Properties() {

		InputStream propertiesInputStream = null;
		try {
			String configPath = System.getenv(LABYRINTH_CONFIG_FILE_PATH_ENV);
			//TODO: remove config.properties load from classpath   
			propertiesInputStream = configPath == null ? ClassLoader.getSystemClassLoader().getResourceAsStream("config.properties") : new FileInputStream(configPath);
			if (propertiesInputStream != null) {
				LOG.info("Load config file from: {}", configPath);
				properties.load(propertiesInputStream);
			} else {
				LOG.warn("No found config file! The program use default config properties.");
			}
		} catch (IOException e) {
			IOUtils.closeQuietly(propertiesInputStream);
		}
	}

	public String getProperty(PropertiesKey key) {
		return properties.getProperty(key.name(), key.getDefaultValue()).trim();
	}

	public Boolean getBooleanProperty(PropertiesKey key) {
		return Boolean.valueOf(getProperty(key));
	}

}
