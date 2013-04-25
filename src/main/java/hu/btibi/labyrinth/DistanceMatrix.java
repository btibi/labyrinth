package hu.btibi.labyrinth;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;
import static hu.btibi.labyrinth.domain.LocationType.EXIT;
import static hu.btibi.labyrinth.domain.LocationType.POWERPILL;
import static hu.btibi.labyrinth.domain.LocationType.START;
import static org.jgrapht.alg.DijkstraShortestPath.findPathBetween;
import hu.btibi.labyrinth.domain.DefaultEdge;
import hu.btibi.labyrinth.domain.Location;
import hu.btibi.labyrinth.predicates.LocationByType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.DirectedGraph;

import com.google.common.collect.Lists;

public class DistanceMatrix {
	private Map<Location, Map<Location, List<Location>>> distanceMatrix;

	public DistanceMatrix(DirectedGraph<Location, DefaultEdge> labyrinth) {
		distanceMatrix = createDistanceMatrix(labyrinth);
	}

	private static Map<Location, Map<Location, List<Location>>> createDistanceMatrix(DirectedGraph<Location, DefaultEdge> labyrinth) {
		List<Location> locations = newArrayList();
		locations.add(find(labyrinth.vertexSet(), new LocationByType(START)));
		locations.addAll(newArrayList(filter(labyrinth.vertexSet(), new LocationByType(POWERPILL))));
		locations.add(find(labyrinth.vertexSet(), new LocationByType(EXIT)));

		Map<Location, Map<Location, List<Location>>> distanceMatrix = new HashMap<Location, Map<Location, List<Location>>>();
		for (int i = 0; i < locations.size(); i++) {
			Location start = locations.get(i);
			if (!distanceMatrix.containsKey(start)) {
				distanceMatrix.put(start, new HashMap<Location, List<Location>>());
			}
			Map<Location, List<Location>> sub = distanceMatrix.get(start);
			for (int j = i + 1; j < locations.size(); j++) {
				Location end = locations.get(j);
				List<DefaultEdge> findPathBetween = findPathBetween(labyrinth, start, end);
				if (findPathBetween != null) {
					List<Location> path = createPath(findPathBetween);
					sub.put(end, path);

					if (!distanceMatrix.containsKey(end)) {
						distanceMatrix.put(end, new HashMap<Location, List<Location>>());
					}
					Map<Location, List<Location>> reverse = distanceMatrix.get(end);
					reverse.put(start, Lists.reverse(path));
				}
			}
		}

		return distanceMatrix;
	}

	private static List<Location> createPath(List<DefaultEdge> findPathBetween) {
		List<Location> path = newArrayList((Location) findPathBetween.get(0).getSource());
		for (DefaultEdge edge : findPathBetween) {
			path.add((Location) edge.getTarget());
		}
		return path;
	}

	public Map<Location, Map<Location, List<Location>>> getDistanceMatrix() {
		return distanceMatrix;
	}

	public List<Location> getPath(List<Location> locations) {
		ArrayList<Location> path = Lists.newArrayList();

		for (int i = 1; i < locations.size(); i++) {
			List<Location> subPath = distanceMatrix.get(locations.get(i - 1)).get(locations.get(i));
			if (path.isEmpty()) {
				path.addAll(subPath);
			} else {
				path.addAll(subPath.subList(1, subPath.size()));
			}
		}

		return path;
	}
}
