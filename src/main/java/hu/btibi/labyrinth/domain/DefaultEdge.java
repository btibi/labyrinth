package hu.btibi.labyrinth.domain;

public class DefaultEdge extends org.jgrapht.graph.DefaultEdge {
	private static final long serialVersionUID = -3045027880461997868L;

	@Override
	public Location getSource() {
		return (Location) super.getSource();
	}

	@Override
	public Location getTarget() {
		return (Location) super.getTarget();
	}
}
