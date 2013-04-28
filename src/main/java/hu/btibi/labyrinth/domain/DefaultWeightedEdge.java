package hu.btibi.labyrinth.domain;


public class DefaultWeightedEdge extends org.jgrapht.graph.DefaultWeightedEdge {
	private static final long serialVersionUID = -3644723579962791321L;

	private int weight;

	public DefaultWeightedEdge(int weight) {
		this.weight = weight;
	}

	@Override
	public double getWeight() {
		return weight;
	}

	@Override
	public Location getSource() {
		return (Location) super.getSource();
	}

	@Override
	public Location getTarget() {
		return (Location) super.getTarget();
	}

}
