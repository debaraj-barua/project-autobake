package maas.models;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Location implements Serializable{

	private float x;
	private float y;
	
	public Location(float x, float y) {
		this.x=x;
		this.y=y;
	}
	
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}

	@Override
	public String toString() {
		return "Location [x=" + x + ", y=" + y + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
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
		if (!(obj instanceof Location)) {
			return false;
		}
		return (this.toString().equals(((Location) obj).toString()));
	}
	
}
