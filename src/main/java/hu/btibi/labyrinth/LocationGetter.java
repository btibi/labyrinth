package hu.btibi.labyrinth;

import static hu.btibi.labyrinth.LocationConverter.from;
import hu.btibi.labyrinth.domain.Location;
import hu.btibi.labyrinth.properties.Properties;
import hu.btibi.labyrinth.properties.PropertiesKey;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocationGetter {
	private static final Logger LOG = LoggerFactory.getLogger(LabyrinthGetter.class);

	private static final String URL = Properties.INSTANCE.getProperty(PropertiesKey.URL_PATTERN);

	private String mazeName;

	public LocationGetter(String mazeName) {
		this.mazeName = mazeName;
	}

	public Location getLocation(String locationId) {
		return from(getLocationStream(locationId));
	}

	private String getLocationStream(String locationId) {
		String url = String.format(URL, mazeName, locationId);

		InputStream jsonStream = null;
		Scanner scanner = null;
		String locationString = null;
		try {
			jsonStream = new java.net.URL(url).openStream();
			scanner = new Scanner(jsonStream);
			locationString = scanner.useDelimiter("//Z").next();
		} catch (IOException e) {
			LOG.error("Problem with the following url: {}", url);
			throw new RuntimeException("Problem with the following url: " + url);
		} finally {
			IOUtils.closeQuietly(scanner);
			IOUtils.closeQuietly(jsonStream);
		}

		return locationString;
	}

}
