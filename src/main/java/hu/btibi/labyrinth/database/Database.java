package hu.btibi.labyrinth.database;

import static com.google.common.collect.Iterables.find;
import hu.btibi.labyrinth.domain.DefaultEdge;
import hu.btibi.labyrinth.domain.Location;
import hu.btibi.labyrinth.domain.LocationType;
import hu.btibi.labyrinth.predicates.LocationById;
import hu.btibi.labyrinth.properties.Properties;
import hu.btibi.labyrinth.properties.PropertiesKey;

import java.io.File;
import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class Database {
	private static final Logger LOG = LoggerFactory.getLogger(Database.class);

	private static final String DB_PATH = Properties.INSTANCE.getProperty(PropertiesKey.STORE_DB_PATH);
	private static final String LOCATION_ID_KEY = "locationId";
	private static final String LOCATION_TYPE_KEY = "locationType";
	private static final String LOCATION_EXIT_KEY = "locationExit";

	private GraphDatabaseService graphDb;
	private Index<Node> nodeIndex;

	private Database() {
		super();
	}

	public static boolean isExists(String dbName) {
		return new File(DB_PATH + File.separator + dbName).exists();
	}

	public static DirectedGraph<Location, DefaultEdge> getGraph(String dbName) {
		LOG.info("------------------- Start get {} graph -------------------", dbName);
		Database db = new Database();
		db.createDb(dbName);
		DirectedGraph<Location, DefaultEdge> graph = db.getData();
		db.shutDown(dbName);
		LOG.info("------------------- End get {} graph -------------------", dbName);

		return graph;
	}

	private DirectedGraph<Location, DefaultEdge> getData() {
		DirectedGraph<Location, DefaultEdge> graph = new SimpleDirectedGraph<Location, DefaultEdge>(DefaultEdge.class);
		GlobalGraphOperations globalGraphOperations = GlobalGraphOperations.at(graphDb);
		for (Node node : globalGraphOperations.getAllNodes()) {
			if (node.getId() != 0) {
				String locationId = String.valueOf(node.getProperty(LOCATION_ID_KEY));
				LocationType locationType = LocationType.valueOf(node.getProperty(LOCATION_TYPE_KEY).toString());
				List<String> exist = Lists.newArrayList((String[]) node.getProperty(LOCATION_EXIT_KEY));
				Location location = new Location(locationId, locationType, exist);
				graph.addVertex(location);
				LOG.info("Get Location LocationId: {}, LocationType: {}", location.getLocationId(), location.getLocationType());
			}
		}

		for (Relationship relationship : globalGraphOperations.getAllRelationships()) {
			Location source = find(graph.vertexSet(), new LocationById(relationship.getStartNode().getProperty(LOCATION_ID_KEY).toString()));
			Location target = find(graph.vertexSet(), new LocationById(relationship.getEndNode().getProperty(LOCATION_ID_KEY).toString()));

			graph.addEdge(source, target);
			LOG.info("Get Edge: {} - {}", source.getLocationId(), target.getLocationId());
		}

		return graph;
	}

	public static void save(String dbName, DirectedGraph<Location, DefaultEdge> graph) {
		LOG.info("------------------- Start save {} graph -------------------", dbName);
		Database db = new Database();
		db.createDb(dbName);
		db.saveData(graph);
		db.shutDown(dbName);
		LOG.info("------------------- End save {} graph -------------------", dbName);
	}

	private void createDb(String dbName) {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH + File.separator + dbName);
		nodeIndex = graphDb.index().forNodes("nodes");
		registerShutdownHook(graphDb);
	}

	private void saveData(DirectedGraph<Location, DefaultEdge> graph) {
		Transaction tx = graphDb.beginTx();
		try {
			saveNodesAndIndexes(graph);

			saveRelations(graph);

			tx.success();
		} finally {
			tx.finish();
		}
	}

	private void saveNodesAndIndexes(DirectedGraph<Location, DefaultEdge> graph) {
		for (Location location : graph.vertexSet()) {
			Node node = graphDb.createNode();
			node.setProperty(LOCATION_ID_KEY, location.getLocationId());
			node.setProperty(LOCATION_TYPE_KEY, location.getLocationType().name());
			node.setProperty(LOCATION_EXIT_KEY, location.getExits().toArray(new String[location.getExits().size()]));

			nodeIndex.add(node, LOCATION_ID_KEY, location.getLocationId());
			LOG.info("Save Location LocationId: {}", location.getLocationId());
		}
	}

	private void saveRelations(DirectedGraph<Location, DefaultEdge> graph) {
		for (DefaultEdge edge : graph.edgeSet()) {
			final Location source = edge.getSource();
			final Location target = edge.getTarget();

			Node sourceNode = nodeIndex.get(LOCATION_ID_KEY, source.getLocationId()).getSingle();
			Node targetNode = nodeIndex.get(LOCATION_ID_KEY, target.getLocationId()).getSingle();

			sourceNode.createRelationshipTo(targetNode, RelationType.GENERAL);
			LOG.info("Save Edge: {} - {}", source.getLocationId(), target.getLocationId());
		}
	}

	private void shutDown(String dbName) {
		graphDb.shutdown();
	}

	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}

	private enum RelationType implements RelationshipType {
		GENERAL;
	}

}
