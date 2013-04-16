package hu.btibi.labyrinth;

import hu.btibi.labyrinth.domain.Location;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Labyrinth {

	static final Logger LOG = LoggerFactory.getLogger(Labyrinth.class);

	public static void main(String[] args) throws IOException {

		LocationGetter locationGetter = new LocationGetter("easy");

		String locationString = locationGetter.getLocation("start");

		LOG.info("Locatoion JSON string: {}", locationString);

		Location location = LocationConverter.from(locationString);

		LOG.info("Locatoion object: {}", location);

	}

}
