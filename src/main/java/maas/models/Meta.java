package maas.models;

import jade.util.leap.Serializable;

@SuppressWarnings("serial")
public class Meta implements Serializable{

	private int bakeries;
	private int products;
	private int orders;
	private CustomerInformation customers;
	private int duration_days;
	
	public Meta(int bakeries, int products, int orders, CustomerInformation customers, int durationDays) {
		this.bakeries = bakeries;
		this.products = products;
		this.orders = orders;
		this.customers = customers;
		this.duration_days = durationDays;
	}
	
	public int getNumberOfOrders() {
		return orders;
	}

	public CustomerInformation getCustomers() {
		return customers;
	}

	public int getNumberOfBakeries() {
		return bakeries;
	}

	public int getDurationDays() {
		return duration_days;
	}

	@Override
	public String toString() {
		return "Meta [bakeries=" + bakeries + ", products=" + products + ", orders=" + orders
				+ ", customers=" + customers + ", duration_days=" + duration_days + "]";
	}

	public int getNumberOfProducts() {
		return products;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bakeries;
		result = prime * result + ((customers == null) ? 0 : customers.hashCode());
		result = prime * result + duration_days;
		result = prime * result + orders;
		result = prime * result + products;
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
		if (!(obj instanceof Meta)) {
			return false;
		}
		return (this.toString().equals(((Meta) obj).toString()));
	}
	
}
