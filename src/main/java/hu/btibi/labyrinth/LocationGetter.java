package hu.btibi.labyrinth;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class LocationGetter {
	private static final String URL = "http://labyrinth.lbi.co.uk/Maze/Location/%s/%s/json"; 

	private String mazeName;

	public LocationGetter(String mazeName) {
		this.mazeName = mazeName;
	}

	public String getLocation(String locationId) throws IOException {
		InputStream locationStream = getLocationStream(locationId);
		Scanner scanner = new Scanner(locationStream);
		String locationString = scanner.useDelimiter("//Z").next();
		scanner.close();
		return locationString;
	}

	private InputStream getLocationStream(String locationId) throws IOException {
		String url = String.format(URL, mazeName, locationId);
		InputStream jsonStream = new java.net.URL(url).openStream();
		return jsonStream;
	}

}
