package maas.messages;

import java.util.Objects;

import jade.util.leap.Serializable;

@SuppressWarnings("serial")
public class BakingMessage implements Serializable {

	private String productId;
	private int quantity;
	private int bakingTemperature;
	private int bakingTime;
	private int productPerOven;
	private int boxingTemperature;
	
	public BakingMessage(String productId, int quantity, int bakingTemperature, int bakingTime, int productPerOven, int boxingTemperature) {
		this.productId = productId;
		this.quantity = quantity;
		this.bakingTemperature = bakingTemperature;
		this.bakingTime = bakingTime;
		this.productPerOven = productPerOven;
		this.boxingTemperature = boxingTemperature;
	}

	public String getProductId() {
		return productId;
	}

	public int getQuantity() {
		return quantity;
	}

	public int getBakingTemperature() {
		return bakingTemperature;
	}

	public int getBakingTime() {
		return bakingTime;
	}

	public int getProductPerOven() {
		return productPerOven;
	}
	
	public int getBoxingTemperature() {
		return boxingTemperature;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof BakingMessage) {
			BakingMessage message = (BakingMessage) o;
			return message.productId.equals(this.productId);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(productId);
	}
	
}
