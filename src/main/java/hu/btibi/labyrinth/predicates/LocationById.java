package hu.btibi.labyrinth.predicates;

import hu.btibi.labyrinth.domain.Location;

import com.google.common.base.Predicate;

public class LocationById implements Predicate<Location> {
	private String locationId;

	public LocationById(String locationId) {
		this.locationId = locationId;
	}

	public boolean apply(Location location) {
		return locationId.equals(location.getLocationId());
	}
}
