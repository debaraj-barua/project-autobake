package maas.messages;

import jade.util.leap.Serializable;

import java.util.Objects;

@SuppressWarnings("serial")
public class KneadingMessage implements Serializable {

	private String productId;
	private int kneadingTime;
	private int restingTime;

	public KneadingMessage(String productId, int kneadingTime, int restingTime) {
		this.productId = productId;
		this.kneadingTime = kneadingTime;
		this.restingTime = restingTime;
	}

	public String getProductId() {
		return productId;
	}

	public int getKneadingTime() {
		return kneadingTime;
	}

	public int getRestingTime() {
		return restingTime;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof KneadingMessage) {
			KneadingMessage message = (KneadingMessage) o;
			return message.productId.equals(this.productId);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(productId);
	}
}
