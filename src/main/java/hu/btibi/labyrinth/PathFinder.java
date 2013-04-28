package hu.btibi.labyrinth;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;
import static hu.btibi.labyrinth.domain.LocationType.EXIT;
import static hu.btibi.labyrinth.domain.LocationType.START;
import hu.btibi.labyrinth.domain.DefaultEdge;
import hu.btibi.labyrinth.domain.DefaultWeightedEdge;
import hu.btibi.labyrinth.domain.Location;
import hu.btibi.labyrinth.predicates.LocationByType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public class PathFinder {

	public String getShortestPath(DirectedGraph<Location, DefaultEdge> labyrinth) {
		DistanceMatrix distanceMatrix = new DistanceMatrix(labyrinth);
		SimpleDirectedWeightedGraph<Location, DefaultWeightedEdge> directedWeightedGraph = WeightedGraphCreator.createWeightedGraph(distanceMatrix);

		List<Location> pathInWeightedGraph = getShortestHamiltonPath(directedWeightedGraph);

		List<Location> path = distanceMatrix.getPath(pathInWeightedGraph);

		return pathToString(path);
	}

	public static List<Location> getShortestHamiltonPath(SimpleDirectedWeightedGraph<Location, DefaultWeightedEdge> directedWeightedGraph) {
		List<Location> vertices = newArrayList(directedWeightedGraph.vertexSet());

		Location startLocation = find(vertices, new LocationByType(START));
		Location exitLocation = find(vertices, new LocationByType(EXIT));

		List<Location> tour = new ArrayList<Location>(directedWeightedGraph.vertexSet().size());
		tour.addAll(Arrays.asList(exitLocation, startLocation));
		vertices.removeAll(tour);

		while (tour.size() != directedWeightedGraph.vertexSet().size()) {
			Location removeLocation = null;

			Location lastLocation = tour.get(tour.size() - 1);
			double minEdgeWeight = Double.MAX_VALUE;
			for (int j = 0; j < vertices.size(); j++) {
				Location actualLocation = vertices.get(j);
				DefaultWeightedEdge fromLast = directedWeightedGraph.getEdge(lastLocation, actualLocation);
				DefaultWeightedEdge toLast = directedWeightedGraph.getEdge(actualLocation, lastLocation);
				double fromWeight = fromLast.getWeight() + toLast.getWeight();
				if (fromWeight < minEdgeWeight) {
					minEdgeWeight = fromWeight;
					removeLocation = actualLocation;
				}
			}

			tour.add(removeLocation);
			vertices.remove(removeLocation);
		}
		Collections.rotate(tour, -1);
		return tour;
	}

	private String pathToString(List<Location> path) {
		return Joiner.on(",").join(Iterables.transform(path, new Function<Location, String>() {
			public String apply(Location input) {
				return input.getLocationId();
			}
		}));
	}

}
