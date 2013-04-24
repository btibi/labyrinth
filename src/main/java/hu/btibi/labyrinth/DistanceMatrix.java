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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.UndirectedGraph;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class DistanceMatrix {
	private Map<Location, Map<Location, List<Location>>> distanceMatrix;

	public DistanceMatrix(UndirectedGraph<Location, DefaultEdge> labyrinth) {
		distanceMatrix = createDistanceMatrix(labyrinth);
	}

	private static Map<Location, Map<Location, List<Location>>> createDistanceMatrix(UndirectedGraph<Location, DefaultEdge> labyrinth) {
		List<Location> locations = newArrayList();
		locations.add(find(labyrinth.vertexSet(), new LocationByType(START)));
		locations.addAll(newArrayList(filter(labyrinth.vertexSet(), new LocationByType(POWERPILL))));
		locations.add(find(labyrinth.vertexSet(), new LocationByType(EXIT)));

		Map<Location, Map<Location, List<Location>>> distanceMatrix = new HashMap<Location, Map<Location, List<Location>>>();
		for (int i = 0; i < locations.size(); i++) {
			Location start = locations.get(i);
			if(!distanceMatrix.containsKey(start)) {
				distanceMatrix.put(start, new HashMap<Location, List<Location>>());
			}
			Map<Location, List<Location>> sub = distanceMatrix.get(start);
			for (int j = i + 1; j < locations.size(); j++) {
				Location end = locations.get(j);
				List<Location> path = createPath(start, findPathBetween(labyrinth, start, end));
				sub.put(end, path);

				if (!distanceMatrix.containsKey(end)) {
					distanceMatrix.put(end, new HashMap<Location, List<Location>>());
				}
				Map<Location, List<Location>> reverse = distanceMatrix.get(end);
				reverse.put(start, Lists.reverse(path));
			}
		}

		return distanceMatrix;
	}

	private static List<Location> createPath(Location start, List<DefaultEdge> findPathBetween) {
		List<Location> path = newArrayList(start);
		for (DefaultEdge edge : findPathBetween) {
			path.add(path.contains(((Location) edge.getSource())) ? ((Location) edge.getTarget()) : ((Location) edge.getSource()));
		}
		return path;
	}

	public List<Location> getPath(Location start, Location end) {
		return distanceMatrix.get(start).get(end);
	}

	public String getPathToString(Location start, Location end) {
		return Joiner.on(",").join(Iterables.transform(getPath(start, end), new com.google.common.base.Function<Location, String>() {
			public String apply(Location input) {
				return input.getLocationId();
			}
		}));
	}
}
