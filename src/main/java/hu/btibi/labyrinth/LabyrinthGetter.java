package hu.btibi.labyrinth;

import static com.google.common.collect.Lists.newArrayList;
import static hu.btibi.labyrinth.properties.PropertiesKey.STORE_MAZE_IN_DB;
import hu.btibi.labyrinth.database.Database;
import hu.btibi.labyrinth.domain.DefaultEdge;
import hu.btibi.labyrinth.domain.Location;
import hu.btibi.labyrinth.predicates.LocationById;
import hu.btibi.labyrinth.properties.Properties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public class LabyrinthGetter {
	private static final Logger LOG = LoggerFactory.getLogger(LabyrinthGetter.class);

	private static final String STATR_LOCATION_ID = "start";

	public static DirectedGraph<Location, DefaultEdge> getLabyrinth(String mazeName) throws IOException {
		DirectedGraph<Location, DefaultEdge> labyrinth;
		boolean storeMazeInDb = Properties.INSTANCE.getBooleanProperty(STORE_MAZE_IN_DB);
		if (storeMazeInDb && Database.isExists(mazeName)) {
			labyrinth = getLabyrinthFromDb(mazeName);
		} else {
			labyrinth = getLabyrinthFromWeb(mazeName);
			if (storeMazeInDb) {
				Database.save(mazeName, labyrinth);
			}
		}
		return labyrinth;
	}

	private static DirectedGraph<Location, DefaultEdge> getLabyrinthFromDb(String mazeName) {
		return Database.getGraph(mazeName);
	}

	private static DirectedGraph<Location, DefaultEdge> getLabyrinthFromWeb(String mazeName) throws IOException {
		LocationGetter locationGetter = new LocationGetter(mazeName);

		LOG.info("------------------- Start init {} graph -------------------", mazeName);

		DirectedGraph<Location, DefaultEdge> graph = new SimpleDirectedGraph<Location, DefaultEdge>(DefaultEdge.class);

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

	private static List<Location> getExitsLocation(List<Location> locations, LocationGetter locationGetter, DirectedGraph<Location, DefaultEdge> graph) throws IOException {
		List<Location> nextLocations = newArrayList();
		for (Location source : locations) {

			LOG.info("Init Location LocationId: {}, LocationType: {}", source.getLocationId(), source.getLocationType());

			for (String fullLocationId : source.getExits()) {
				String nextLocationId = getLocationId(fullLocationId);
				Location target = Iterables.find(graph.vertexSet(), new LocationById(nextLocationId), null);
				if (target == null) {
					target = locationGetter.getLocation(nextLocationId);
					nextLocations.add(target);
					graph.addVertex(target);
				}
				graph.addEdge(source, target);
				LOG.info("Init Edge: {} - {}", source.getLocationId(), target.getLocationId());

			}
		}
		return nextLocations;
	}

	private static String getLocationId(String fullLocationID) {
		ArrayList<String> strings = newArrayList(Splitter.on("/").split(fullLocationID));
		return strings.get(strings.size() - 1);
	}
}
