package hu.btibi.labyrinth;

import static com.google.gson.FieldNamingPolicy.UPPER_CAMEL_CASE;
import hu.btibi.labyrinth.domain.Location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LocationConverter {
	private static final Gson gson = new GsonBuilder().setFieldNamingPolicy(UPPER_CAMEL_CASE).create();

	public static Location from(String locationString) {
		return gson.fromJson(locationString, Location.class);
	}
}
