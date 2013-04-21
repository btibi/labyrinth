package hu.btibi.labyrinth;

import hu.btibi.labyrinth.domain.DefaultEdge;
import hu.btibi.labyrinth.domain.Location;

import java.io.File;

import org.jgrapht.UndirectedGraph;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database {
	private static final Logger LOG = LoggerFactory.getLogger(Database.class);

	private static final String DB_PATH = "d:\\workspace\\labyrinth\\";
	private static final String LOCATION_ID_KEY = "locationId";
	private static final String LOCATION_TYPE_KEY = "locationType";

	private GraphDatabaseService graphDb;
	private Index<Node> nodeIndex;

	private Database() {
		super();
	}

	public static boolean isExists(String dbName) {
		return new File(dbName).exists();
	}

	public UndirectedGraph<Location, DefaultEdge> getGraph(String dbName) {
		LOG.info("------------------- Start get {} graph -------------------", dbName);
		Database db = new Database();
		db.createDb(dbName);
		UndirectedGraph<Location, DefaultEdge> graph = db.getData();
		db.shutDown(dbName);
		LOG.info("------------------- End get {} graph -------------------", dbName);

		return graph;
	}

	private UndirectedGraph<Location, DefaultEdge> getData() {
		// TODO Auto-generated method stub
		return null;
	}

	public static void save(String dbName, UndirectedGraph<Location, DefaultEdge> graph) {
		LOG.info("------------------- Start save {} graph -------------------", dbName);
		Database db = new Database();
		db.createDb(dbName);
		db.saveData(graph);
		db.shutDown(dbName);
		LOG.info("------------------- End save {} graph -------------------", dbName);
	}

	private void createDb(String mazeName) {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH + mazeName);
		nodeIndex = graphDb.index().forNodes("nodes");
		registerShutdownHook(graphDb);
	}

	private void saveData(UndirectedGraph<Location, DefaultEdge> graph) {
		Transaction tx = graphDb.beginTx();
		try {
			saveNodesAndIndexes(graph);

			saveRelations(graph);

			tx.success();
		} finally {
			tx.finish();
		}
	}

	private void saveNodesAndIndexes(UndirectedGraph<Location, DefaultEdge> graph) {
		for (Location location : graph.vertexSet()) {
			Node node = graphDb.createNode();
			node.setProperty(LOCATION_ID_KEY, location.getLocationId());
			node.setProperty(LOCATION_TYPE_KEY, location.getLocationType().name());
			nodeIndex.add(node, LOCATION_ID_KEY, location.getLocationId());
			LOG.info("Save Location LocationId: {}", location.getLocationId());
		}
	}

	private void saveRelations(UndirectedGraph<Location, DefaultEdge> graph) {
		for (DefaultEdge edge : graph.edgeSet()) {
			final Location source = (Location) edge.getSource();
			final Location target = (Location) edge.getTarget();

			Node sourceNode = nodeIndex.get(LOCATION_ID_KEY, source.getLocationId()).getSingle();
			Node targetNode = nodeIndex.get(LOCATION_ID_KEY, target.getLocationId()).getSingle();

			sourceNode.createRelationshipTo(targetNode, new RelationshipType() {

				public String name() {
					return source.getLocationId() + " - " + target.getLocationId();
				}
			});
			LOG.info("Save Edge: {} - {}", source.getLocationId(), target.getLocationId());
		}
	}

	private void shutDown(String mazeName) {
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

}
