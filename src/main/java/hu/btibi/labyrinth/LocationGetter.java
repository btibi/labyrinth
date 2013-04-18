package hu.btibi.labyrinth;

import static hu.btibi.labyrinth.LocationConverter.from;
import hu.btibi.labyrinth.domain.Location;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocationGetter {
	static final Logger LOG = LoggerFactory.getLogger(LabyrinthGetter.class);

	private static final String URL = "http://labyrinth.lbi.co.uk/Maze/Location/%s/%s/json";

	private String mazeName;

	public LocationGetter(String mazeName) {
		this.mazeName = mazeName;
	}

	public Location getLocation(String locationId) throws IOException {
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
