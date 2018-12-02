package maas.models;

import jade.util.leap.Serializable;

@SuppressWarnings("serial")
public class Node implements Serializable{
	
	private String name;
	private String company;
	private Location location;
	private String guid;
	private String type;
		
	public Node(String name, String company, Location location, String guid, String type) {
		this.name = name;
		this.company = company;
		this.location = location;
		this.guid = guid;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	public String getCompany() {
		return company;
	}
	public Location getLocation() {
		return location;
	}
	public String getGuid() {
		return guid;
	}
	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Node [name=" + name + ", company=" + company + ", location=" + location + ", guid=" + guid + ", type="
				+ type + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((company == null) ? 0 : company.hashCode());
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (!(obj instanceof Node)) {
			return false;
		}
		return (this.toString().equals(((Node) obj).toString()));
	}
	

}
