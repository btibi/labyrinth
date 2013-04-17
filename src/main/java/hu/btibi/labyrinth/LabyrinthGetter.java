package hu.btibi.labyrinth;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import hu.btibi.labyrinth.domain.Location;

public class LabyrinthGetter {
	static final Logger LOG = LoggerFactory.getLogger(LabyrinthGetter.class);

	public void getLabyrint(String mazeName) throws IOException {
		LocationGetter locationGetter = new LocationGetter(mazeName);

		Map<String, Location> locationStorage = newHashMap();
		get(locationGetter, "start", locationStorage);

	}

	private void get(LocationGetter locationGetter, String locationId, Map<String, Location> locationStorage) throws IOException {

		Location location = LocationConverter.from(locationGetter.getLocation(locationId));

		locationStorage.put(location.getLocationId(), location);

		LOG.info("LocationId: {}, LocationType: {}", location.getLocationId(), location.getLocationType());

		for (final String fullLocationID : location.getExits()) {
			String nextLocationId = getLocationID(fullLocationID);

			String existsLocation = find(locationStorage.keySet(), Predicates.<String> equalTo(nextLocationId), null);

			if (existsLocation == null) {
				get(locationGetter, nextLocationId, locationStorage);
			}
		}

	}

	private String getLocationID(String fullLocationID) {
		ArrayList<String> strings = Lists.newArrayList(Splitter.on("/").split(fullLocationID));
		return strings.get(strings.size() - 1);
	}
}
