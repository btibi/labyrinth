package hu.btibi.labyrinth.predicates;

import hu.btibi.labyrinth.domain.Location;
import hu.btibi.labyrinth.domain.LocationType;

import com.google.common.base.Predicate;

public class LocationByType implements Predicate<Location> {
	private LocationType locationType;

	public LocationByType(LocationType locationType) {
		this.locationType = locationType;
	}

	public boolean apply(Location location) {
		return location.getLocationType() == locationType;
	}

}
