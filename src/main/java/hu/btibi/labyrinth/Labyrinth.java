package hu.btibi.labyrinth;

import static com.google.common.collect.Lists.newArrayList;
import hu.btibi.labyrinth.domain.DefaultEdge;
import hu.btibi.labyrinth.domain.Location;

import java.io.IOException;
import java.util.List;

import org.jgrapht.DirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * You are The Daddy! If you'd like to work for LBi send an email with your CV
 * to jim.macaulay@lbi.com with a subject of 'I am the Daddy'
 * 
 * @author btibi
 * 
 */
public class Labyrinth {
	private static final Logger LOG = LoggerFactory.getLogger(Labyrinth.class);

	private static final List<String> mazeNames = Lists.newArrayList("easy", "pacman", "glasgow", "chucknorris", "lucifer", "thedaddy");

	public static void main(String[] args) throws IOException {

		for (String mazeName : mazeNames) {
			LOG.info("------------------- Labyrinth: {} -------------------", mazeName);
			DirectedGraph<Location, DefaultEdge> labyrinth = LabyrinthGetter.getLabyrinth(mazeName);
			String path = new PathFinder().getShortestPath(labyrinth);
			List<String> pathList = newArrayList(Splitter.on(",").split(path));
			LOG.info("Steps number: {}", pathList.size());
			LOG.info("Path: {}", path);
		}
	}

}
