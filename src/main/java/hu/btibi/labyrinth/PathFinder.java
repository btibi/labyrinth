package hu.btibi.labyrinth;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;
import static hu.btibi.labyrinth.domain.LocationType.EXIT;
import static hu.btibi.labyrinth.domain.LocationType.START;
import hu.btibi.labyrinth.domain.DefaultEdge;
import hu.btibi.labyrinth.domain.DefaultWeightedEdge;
import hu.btibi.labyrinth.domain.Location;
import hu.btibi.labyrinth.domain.LocationType;
import hu.btibi.labyrinth.predicates.LocationById;
import hu.btibi.labyrinth.predicates.LocationByType;

import java.util.Collections;
import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public class PathFinder {

	public String getShortestPath(DirectedGraph<Location, DefaultEdge> labyrinth) {
		DistanceMatrix distanceMatrix = new DistanceMatrix(labyrinth);
		SimpleWeightedGraph<Location, DefaultWeightedEdge> weightedGraph = WeightedGraphCreator.createWeightedGraph(distanceMatrix);

		List<Location> pathInWeightedGraph = getShortestHamiltonPath(weightedGraph);

		List<Location> path = distanceMatrix.getPath(pathInWeightedGraph);

		return pathToString(path);
	}

	private List<Location> getShortestHamiltonPath(SimpleWeightedGraph<Location, DefaultWeightedEdge> weightedGraph) {
		Location dummy = new Location("DUMMY", LocationType.NORMAL, null);
		weightedGraph.addVertex(dummy);
		weightedGraph.addEdge(find(weightedGraph.vertexSet(), new LocationByType(EXIT)), dummy, new DefaultWeightedEdge(0));
		weightedGraph.addEdge(dummy, find(weightedGraph.vertexSet(), new LocationByType(START)), new DefaultWeightedEdge(0));

		List<Location> output = findHamiltoCycle(weightedGraph);
		output.remove(dummy);

		Collections.rotate(output, -1 * output.indexOf(find(output, new LocationByType(START))));

		return output;
	}

	public static List<Location> findHamiltoCycle(SimpleWeightedGraph<Location, DefaultWeightedEdge> weightedGraph) {
		List<Location> vertices = newArrayList(weightedGraph.vertexSet());

		Location startLocation = find(vertices, new LocationByType(START));
		Location dummyLocation = find(vertices, new LocationById("DUMMY"));
		Location exitLocation = find(vertices, new LocationByType(EXIT));
		
		List<Location> tour = newArrayList(exitLocation, dummyLocation, startLocation);
		vertices.removeAll(tour);

		while (tour.size() != weightedGraph.vertexSet().size()) {
			Location removeLocation = null;

			Location lastLocation = tour.get(tour.size() - 1);
			double minEdgeWeight = Double.MAX_VALUE;
			for (int j = 0; j < vertices.size(); j++) {
				DefaultWeightedEdge fromLast = weightedGraph.getEdge(lastLocation, vertices.get(j));
				double fromWeight = fromLast.getWeight();
				if (fromWeight < minEdgeWeight) {
					minEdgeWeight = fromWeight;
					removeLocation = vertices.get(j);
				}
			}

			tour.add(removeLocation);
			vertices.remove(removeLocation);
		}
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
