package maas.messages;

import java.util.Objects;

import jade.util.leap.Serializable;

@SuppressWarnings("serial")
public class PreparationMessage implements Serializable {
	
	private String productId;
	private int quantity;
	private int preparingTime;

	public PreparationMessage(String productId, int quantity, int preparingTime) {
		this.productId = productId;
		this.quantity = quantity;
		this.preparingTime = preparingTime;
	}

	public String getProductId() {
		return productId;
	}
	
	public int getQuantity() {
		return quantity;
	}

	public int getPreparingTime() {
		return preparingTime;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PreparationMessage) {
			PreparationMessage message = (PreparationMessage) o;
			return message.productId.equals(this.productId);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(productId);
	}
}
