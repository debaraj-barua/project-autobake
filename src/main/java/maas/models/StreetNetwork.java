package maas.models;

import java.util.List;

import jade.util.leap.Serializable;

@SuppressWarnings("serial")
public class StreetNetwork implements Serializable{
	
	private boolean directed;
	private List<Node> nodes;
	private List<Link> links;
	
	public boolean isDirected() {
		return directed;
	}
	public List<Node> getNodes() {
		return nodes;
	}
	public List<Link> getLinks() {
		return links;
	}
	@Override
	public String toString() {
		return "StreetNetwork [directed=" + directed + ", nodes=" + nodes + ", links=" + links + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (directed ? 1231 : 1237);
		result = prime * result + ((links == null) ? 0 : links.hashCode());
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof StreetNetwork)) {
			return false;
		}
		StreetNetwork other = (StreetNetwork) obj;
		if (directed != other.directed) {
			return false;
		}
		if (links == null) {
			if (other.links != null) {
				return false;
			}
		} else if (!links.equals(other.links)) {
			return false;
		}
		if (nodes == null) {
			if (other.nodes != null) {
				return false;
			}
		} else if (!nodes.equals(other.nodes)) {
			return false;
		}
		return true;
	}
	

}
