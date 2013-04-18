package hu.btibi.labyrinth.domain;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Location {

	private List<String> exits;
	private String locationId;
	private LocationType locationType;

	public LocationType getLocationType() {
		return locationType;
	}

	public String getLocationId() {
		return locationId;
	}

	public List<String> getExits() {
		return exits;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof Location) {
			return new EqualsBuilder().append(locationId, ((Location) o).getLocationId()).isEquals();
		}

		return true;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(locationId).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(locationId).append(locationType).toString();
	}

	Location() {
	}

}
