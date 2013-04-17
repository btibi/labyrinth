package hu.btibi.labyrinth;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;
import hu.btibi.labyrinth.domain.DefaultEdge;
import hu.btibi.labyrinth.domain.Location;
import hu.btibi.labyrinth.predicates.LocationById;

import java.io.IOException;
import java.util.ArrayList;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;

public class LabyrinthGetter {
	static final Logger LOG = LoggerFactory.getLogger(LabyrinthGetter.class);

	private static final String STATR_LOCATION_ID = "start";

	public static UndirectedGraph<Location, DefaultEdge> getLabyrinth(String mazeName) throws IOException {
		UndirectedGraph<Location, DefaultEdge> graph = new SimpleGraph<Location, DefaultEdge>(DefaultEdge.class);

		LOG.info("------------------- Start init {} graph -------------------", mazeName);
		
		initaliazeGraph(new LocationGetter(mazeName), STATR_LOCATION_ID, graph);
		
		LOG.info("Graph vertex number: {} Graph edge number: {}", graph.vertexSet().size(), graph.edgeSet().size());
		LOG.info("------------------- End init {} graph -------------------", mazeName);

		return graph;

	}

	private static void initaliazeGraph(LocationGetter locationGetter, String locationId, UndirectedGraph<Location, DefaultEdge> graph) throws IOException {
		Location location = LocationConverter.from(locationGetter.getLocation(locationId));
		LOG.info("Init Location LocationId: {}, LocationType: {}", location.getLocationId(), location.getLocationType());

		graph.addVertex(location);

		for (String nextFullLocationID : location.getExits()) {

			String nextLocationId = getLocationId(nextFullLocationID);

			Location previus = find(graph.vertexSet(), new LocationById(nextLocationId), null);

			if (previus == null) {
				initaliazeGraph(locationGetter, nextLocationId, graph);
			} else {
				graph.addEdge(previus, location);
			}
		}

	}

	private static String getLocationId(String fullLocationID) {
		ArrayList<String> strings = newArrayList(Splitter.on("/").split(fullLocationID));
		return strings.get(strings.size() - 1);
	}
}
