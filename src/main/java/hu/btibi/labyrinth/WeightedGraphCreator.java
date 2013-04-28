package hu.btibi.labyrinth;

import hu.btibi.labyrinth.domain.DefaultWeightedEdge;
import hu.btibi.labyrinth.domain.Location;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class WeightedGraphCreator {

	public static SimpleDirectedWeightedGraph<Location, DefaultWeightedEdge> createWeightedGraph(DistanceMatrix distanceMatrix) {
		SimpleDirectedWeightedGraph<Location, DefaultWeightedEdge> directedWeightedGraph = new SimpleDirectedWeightedGraph<Location, DefaultWeightedEdge>(DefaultWeightedEdge.class);

		for (Entry<Location, Map<Location, List<Location>>> startEntry : distanceMatrix.getDistanceMatrix().entrySet()) {
			Location startLocation = startEntry.getKey();
			directedWeightedGraph.addVertex(startLocation);

			for (Entry<Location, List<Location>> endEntry : startEntry.getValue().entrySet()) {
				Location endLocation = endEntry.getKey();
				directedWeightedGraph.addVertex(endLocation);

				directedWeightedGraph.addEdge(startLocation, endLocation, new DefaultWeightedEdge(endEntry.getValue().size()));
			}
		}

		return directedWeightedGraph;
	}
}
