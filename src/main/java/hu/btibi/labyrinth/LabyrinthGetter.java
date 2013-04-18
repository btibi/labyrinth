package hu.btibi.labyrinth;

import static com.google.common.collect.Lists.newArrayList;
import hu.btibi.labyrinth.domain.DefaultEdge;
import hu.btibi.labyrinth.domain.Location;
import hu.btibi.labyrinth.predicates.LocationById;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public class LabyrinthGetter {
	static final Logger LOG = LoggerFactory.getLogger(LabyrinthGetter.class);

	private static final String STATR_LOCATION_ID = "start";

	public static UndirectedGraph<Location, DefaultEdge> getLabyrinth(String mazeName) throws IOException {
		LocationGetter locationGetter = new LocationGetter(mazeName);

		LOG.info("------------------- Start init {} graph -------------------", mazeName);

		UndirectedGraph<Location, DefaultEdge> graph = new SimpleGraph<Location, DefaultEdge>(DefaultEdge.class);

		Location startLocation = locationGetter.getLocation(STATR_LOCATION_ID);
		List<Location> locations = newArrayList(startLocation);
		graph.addVertex(locations.get(0));

		while (!locations.isEmpty()) {

			locations = getExitsLocation(locations, locationGetter, graph);

		}

		LOG.info("Graph vertex number: {} Graph edge number: {}", graph.vertexSet().size(), graph.edgeSet().size());
		LOG.info("------------------- End init {} graph -------------------", mazeName);

		return graph;

	}

	private static List<Location> getExitsLocation(List<Location> locations, LocationGetter locationGetter, UndirectedGraph<Location, DefaultEdge> graph) throws IOException {
		List<Location> nextLocations = newArrayList();
		for (Location from : locations) {

			LOG.info("Init Location LocationId: {}, LocationType: {}", from.getLocationId(), from.getLocationType());

			for (String fullLocationId : from.getExits()) {
				String nextLocationId = getLocationId(fullLocationId);
				Location source = Iterables.find(graph.vertexSet(), new LocationById(nextLocationId), null);
				if (source == null) {
					source = locationGetter.getLocation(nextLocationId);
					nextLocations.add(source);
					graph.addVertex(source);
				}
				graph.addEdge(from, source);
				LOG.info("Init Edge: {} - {}", from.getLocationId(), source.getLocationId());

			}
		}
		return nextLocations;
	}

	private static String getLocationId(String fullLocationID) {
		ArrayList<String> strings = newArrayList(Splitter.on("/").split(fullLocationID));
		return strings.get(strings.size() - 1);
	}
}
