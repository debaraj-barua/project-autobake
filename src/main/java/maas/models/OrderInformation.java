package maas.models;

import jade.util.leap.Serializable;

@SuppressWarnings("serial")
public class OrderInformation implements Serializable{
	
	private String total;
	private String avg_per_day;
	
	public OrderInformation(String total, String avgPerDay) {
		super();
		this.total = total;
		this.avg_per_day = avgPerDay;
	}
	public String getTotal() {
		return total;
	}
	public String getAvgPerDay() {
		return avg_per_day;
	}

}
