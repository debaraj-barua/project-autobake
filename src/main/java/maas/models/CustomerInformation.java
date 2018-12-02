package maas.models;

import jade.util.leap.Serializable;

@SuppressWarnings("serial")
public class CustomerInformation implements Serializable{

	private int total_type1;
	private int total_type2;
	private int total_type3;
	
	public CustomerInformation(int totalType1, int totalType2, int totalType3) {
		this.total_type1 = totalType1;
		this.total_type2 = totalType2;
		this.total_type3 = totalType3;
	}

	public int getTotalType1() {
		return total_type1;
	}

	public int getTotalType2() {
		return total_type2;
	}

	public int getTotalType3() {
		return total_type3;
	}

	public CustomerInformation() {
		this.total_type1 = 0;
		this.total_type2 = 0;
		this.total_type3 = 0;
	}

	@Override
	public String toString() {
		return "CustomerInformation [total_type1=" + total_type1 + ", total_type2=" + total_type2 + ", total_type3="
				+ total_type3 + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + total_type1;
		result = prime * result + total_type2;
		result = prime * result + total_type3;
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
		if (!(obj instanceof CustomerInformation)) {
			return false;
		}
		return (this.toString().equals(((CustomerInformation) obj).toString()));
	}
	
}
