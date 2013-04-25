package hu.btibi.labyrinth;

import hu.btibi.labyrinth.domain.DefaultWeightedEdge;
import hu.btibi.labyrinth.domain.Location;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jgrapht.graph.SimpleWeightedGraph;

public class WeightedGraphCreator {

	public static SimpleWeightedGraph<Location, DefaultWeightedEdge> createWeightedGraph(DistanceMatrix distanceMatrix) {
		SimpleWeightedGraph<Location, DefaultWeightedEdge> weightedGraph = new SimpleWeightedGraph<Location, DefaultWeightedEdge>(DefaultWeightedEdge.class);

		for (Entry<Location, Map<Location, List<Location>>> startEntry : distanceMatrix.getDistanceMatrix().entrySet()) {
			Location startLocation = startEntry.getKey();
			weightedGraph.addVertex(startLocation);

			for (Entry<Location, List<Location>> endEntry : startEntry.getValue().entrySet()) {
				Location endLocation = endEntry.getKey();
				weightedGraph.addVertex(endLocation);

				weightedGraph.addEdge(startLocation, endLocation, new DefaultWeightedEdge(endEntry.getValue().size()));
			}
		}

		return weightedGraph;
	}
}
