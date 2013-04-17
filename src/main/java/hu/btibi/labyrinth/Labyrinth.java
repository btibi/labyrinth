package hu.btibi.labyrinth;

import static com.google.common.collect.Iterables.find;
import hu.btibi.labyrinth.domain.DefaultEdge;
import hu.btibi.labyrinth.domain.Location;
import hu.btibi.labyrinth.domain.LocationType;
import hu.btibi.labyrinth.predicates.LocationByType;

import java.io.IOException;
import java.util.List;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Labyrinth {
	static final Logger LOG = LoggerFactory.getLogger(Labyrinth.class);

    public static void main(String[] args) throws IOException {

        UndirectedGraph<Location, DefaultEdge> labyrinth = LabyrinthGetter.getLabyrinth("easy");
        
        Location start = find(labyrinth.vertexSet(), new LocationByType(LocationType.START));
		Location exit = find(labyrinth.vertexSet(), new LocationByType(LocationType.EXIT));

		List<DefaultEdge> pathBetween = DijkstraShortestPath.findPathBetween(labyrinth, start, exit);

		StringBuilder s = new StringBuilder();
		for (DefaultEdge edge : pathBetween) {
			s.append(((Location) edge.getSource()).getLocationId() + ",");
		}
		s.append(exit.getLocationId());

		LOG.info(s.toString());

		UndirectedGraph<Location, DefaultEdge> labyrinth2 = LabyrinthGetter.getLabyrinth("pacman");

	}



}
