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
	public Object getSource() {
		return super.getSource();
	}

	@Override
	public Object getTarget() {
		return super.getTarget();
	}

}
