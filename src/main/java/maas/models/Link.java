package maas.models;

import jade.util.leap.Serializable;

@SuppressWarnings("serial")
public class Link implements Serializable {
	
	private String source;
	private String guid;
	private float dist;
	private String target;
	
	public Link( String source,String guid,float dist,String target) {
		this.source=source;
		this.guid=guid;
		this.dist = dist;
		this.target = target;
		
	}
	
	public String getSource() {
		return source;
	}
	public String getGuid() {
		return guid;
	}
	public float getDist() {
		return dist;
	}
	public String getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return "Link [source=" + source + ", guid=" + guid + ", dist=" + dist + ", target=" + target + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(dist);
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		if (!(obj instanceof Link)) {
			return false;
		}
		return (this.toString().equals(((Link) obj).toString()));
	}
	
	
}
