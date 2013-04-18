package hu.btibi.labyrinth;

import static com.google.common.collect.Iterables.find;
import hu.btibi.labyrinth.domain.DefaultEdge;
import hu.btibi.labyrinth.domain.Location;
import hu.btibi.labyrinth.domain.LocationType;
import hu.btibi.labyrinth.predicates.LocationByType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class Labyrinth {
	static final Logger LOG = LoggerFactory.getLogger(Labyrinth.class);

	public static void main(String[] args) throws IOException {

		UndirectedGraph<Location, DefaultEdge> labyrinth = LabyrinthGetter.getLabyrinth("easy");
		Location start = find(labyrinth.vertexSet(), new LocationByType(LocationType.START));
		Location exit = find(labyrinth.vertexSet(), new LocationByType(LocationType.EXIT));
		String path = path(labyrinth, start, exit);
		LOG.info("Utvonal {} lepest tartalmaz. Lepesek: {}", path.split(",").length, path);

		UndirectedGraph<Location, DefaultEdge> labyrinth2 = LabyrinthGetter.getLabyrinth("pacman");
		Location start2 = find(labyrinth2.vertexSet(), new LocationByType(LocationType.START));
		Location powerpill = find(labyrinth2.vertexSet(), new LocationByType(LocationType.POWERPILL));
		Location exit2 = find(labyrinth2.vertexSet(), new LocationByType(LocationType.EXIT));
		String subPath1 = path(labyrinth2, start2, powerpill);
		String subPath2 = path(labyrinth2, powerpill, exit2);
		ArrayList<String> l = Lists.newArrayList(subPath2.split(","));
		l.remove(0);
		subPath2 = Joiner.on(",").join(l);
		String path2 = subPath1 + "," + subPath2;
		String[] split = path2.split(",");
		LOG.info("Utvonal {} lepest tartalmaz. Lepesek: {}", split.length, path2);

		UndirectedGraph<Location, DefaultEdge> labyrinth3 = LabyrinthGetter.getLabyrinth("glasgow");
		Location start3 = find(labyrinth3.vertexSet(), new LocationByType(LocationType.START));
		List<Location> pills = Lists.newArrayList(Iterables.filter(labyrinth3.vertexSet(), new LocationByType(LocationType.POWERPILL)));
		Location exit3 = find(labyrinth3.vertexSet(), new LocationByType(LocationType.EXIT));

		String subPath11 = path(labyrinth3, start3, pills.get(0));
		String subPath22 = path(labyrinth3, pills.get(0), pills.get(1));
		String subPath33 = path(labyrinth3, pills.get(1), exit3);

		String path3 = subPath11 + "," + s(subPath22) + "," + s(subPath33);
		String[] split3 = path3.split(",");
		LOG.info("Utvonal {} lepest tartalmaz. Lepesek: {}", split3.length, path3);

		UndirectedGraph<Location, DefaultEdge> labyrinth4 = LabyrinthGetter.getLabyrinth("chucknorris");

		
		/*<GameMessage xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
		<MessageType>Info</MessageType>
		<MessageContent>Well Done. Now go to a hard place 'glasgow'</MessageContent>
		</GameMessage>*/
	}

	private static String s(String path) {
		List<String> l = Lists.newArrayList(path.split(","));
		l.remove(0);
		path = Joiner.on(",").join(l);
		return path;
	}

	private static String path(UndirectedGraph<Location, DefaultEdge> labyrinth, Location start, Location exit) {

		List<DefaultEdge> pathBetween = DijkstraShortestPath.findPathBetween(labyrinth, start, exit);

		List<String> path = Lists.newArrayList(start.getLocationId());
		for (DefaultEdge edge : pathBetween) {
			path.add(path.contains(((Location) edge.getSource()).getLocationId()) ? ((Location) edge.getTarget()).getLocationId() : ((Location) edge.getSource()).getLocationId());
		}

		return Joiner.on(",").join(path);
	}

}
