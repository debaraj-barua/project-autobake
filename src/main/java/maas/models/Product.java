package maas.models;

import java.io.Serializable;
import java.util.Objects;


@SuppressWarnings("serial")
public class Product implements Serializable {
	
	private int boxing_temp;
	private float sales_price;
	private int breads_per_oven;
	private int breads_per_box;
	private int item_prep_time;
	private int dough_prep_time;
	private int baking_temp;
	private int cooling_rate;
	private String guid;
	private int baking_time;
	private int resting_time;
	private float production_cost;
	
	public Product(int[] values, float salesPrice, String guid, float productionCost) {
		this.boxing_temp = values[0];
		this.breads_per_oven = values[1];
		this.breads_per_box = values[2];
		this.item_prep_time = values[3];
		this.dough_prep_time = values[4];
		this.baking_temp = values[5];
		this.cooling_rate = values[6];
		this.baking_time = values[7];
		this.resting_time = values[8];
		this.sales_price = salesPrice;
		this.guid = guid;
		this.production_cost = productionCost;
	}

	//Overloading constructor to help search with hash maps
	public Product(String guid){
		this.guid=guid;
	}
	
	public String getGuid() {
		return guid;
	}

	public int getDoughPrepTime() {
		return dough_prep_time;
	}

	public int getRestingTime() {
		return resting_time;
	}

	public int getItemPrepTime() {
		return item_prep_time;
	}

	public int getBreadsPerOven() {
		return breads_per_oven;
	}

	public int getBakingTime() {
		return baking_time;
	}

	public int getBakingTemp() {
		return baking_temp;
	}

	public int getCoolingRate() {
		return cooling_rate;
	}

	public int getBoxingTemp() {
		return boxing_temp;
	}

	public int getBreadsPerBox() {
		return breads_per_box;
	}
	
	public float getProductionCost() {
		return production_cost;
	}

	public float getSalesPrice() {
		return sales_price;
	}
	
	@Override
	public String toString() {
		return "Product [id=" + guid + ", dough_prep_time=" + dough_prep_time + ", resting_time=" + resting_time
				+ ", item_prep_time=" + item_prep_time + ", breads_per_oven=" + breads_per_oven + ", baking_time="
				+ baking_time + ", baking_temp=" + baking_temp + ", cooling_rate=" + cooling_rate + ", boxing_temp="
				+ boxing_temp + ", breads_per_box=" + breads_per_box + ", production_cost=" + production_cost
				+ ", sales_price=" + sales_price + "]";
	}
	public double getTotalTime() {
		return (item_prep_time+dough_prep_time+resting_time+item_prep_time+baking_time);
	}
	@Override
	public int hashCode() {
		return Objects.hash(this.guid);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Product)) {
			return false;
		}
		return (this.guid.equals(((Product)obj).guid));
	}
	
	
}
