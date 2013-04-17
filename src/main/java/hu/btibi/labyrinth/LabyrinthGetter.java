package hu.btibi.labyrinth;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import hu.btibi.labyrinth.domain.DefaultEdge;
import hu.btibi.labyrinth.domain.LocationType;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import hu.btibi.labyrinth.domain.Location;

public class LabyrinthGetter {
	static final Logger LOG = LoggerFactory.getLogger(LabyrinthGetter.class);

	public void getLabyrint(String mazeName) throws IOException {
		LocationGetter locationGetter = new LocationGetter(mazeName);

		Map<String, Location> locationStorage = newHashMap();
		UndirectedGraph<Location, DefaultEdge> graph = new SimpleGraph(DefaultEdge.class);

		get(locationGetter, "start", locationStorage, graph);

        Location start = Iterables.find(locationStorage.values(), getPredicate(LocationType.START));
        Location exit = Iterables.find(locationStorage.values(), getPredicate(LocationType.EXIT));

        List<DefaultEdge> pathBetween = DijkstraShortestPath.findPathBetween(graph, start, exit);

        StringBuilder s = new StringBuilder(start.getLocationId());
        for (DefaultEdge edge : pathBetween) {
            s.append("," + ((Location) edge.getSource()).getLocationId());
        }

        LOG.info(s.toString());





	}

    private Predicate<Location> getPredicate(final LocationType type) {
        return new Predicate<Location>() {
            @Override
            public boolean apply(Location location) {
                return location.getLocationType() == type;
            }
        };
    }

    private void get(LocationGetter locationGetter, String locationId, Map<String, Location> locationStorage, UndirectedGraph<Location, DefaultEdge> graph) throws IOException {

		Location location = LocationConverter.from(locationGetter.getLocation(locationId));

		locationStorage.put(location.getLocationId(), location);
        graph.addVertex(location);

		LOG.info("LocationId: {}, LocationType: {}", location.getLocationId(), location.getLocationType());

		for (final String fullLocationID : location.getExits()) {
			String nextLocationId = getLocationID(fullLocationID);

            Location existsLocation = locationStorage.get(nextLocationId);

            if (existsLocation == null) {
				get(locationGetter, nextLocationId, locationStorage, graph);
			} else {
                graph.addEdge(location, existsLocation);
            }
		}

	}

	private String getLocationID(String fullLocationID) {
		ArrayList<String> strings = Lists.newArrayList(Splitter.on("/").split(fullLocationID));
		return strings.get(strings.size() - 1);
	}
}
