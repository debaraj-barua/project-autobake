package maas.messages;

import java.util.List;

import jade.util.leap.Serializable;
import maas.models.Order;
import maas.models.Product;

@SuppressWarnings("serial")
public class DeliveryMessage implements Serializable{
	private List<Order> ordersToProcess;
	private Product product;
	private int quantity;
	
	public DeliveryMessage(List<Order> ordersToProcess, Product product, int quantity) {
		this.ordersToProcess = ordersToProcess;
		this.product = product;
		this.quantity = quantity;
	}
	@Override
	public String toString() {
		return "DeliveryMessage [ordersToProcess=" + ordersToProcess + ", finishedProduct=" + product
				+ ", quantity=" + quantity + "]";
	}
	
	public List<Order> getOrdersToProcess() {
		return ordersToProcess;
	}
	public Product getProduct() {
		return product;
	}
	public int getQuantity() {
		return quantity;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		result = prime * result + ((ordersToProcess == null) ? 0 : ordersToProcess.hashCode());
		result = prime * result + quantity;
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
		if (!(obj instanceof DeliveryMessage)) {
			return false;
		}
		return (this.toString().equals(((DeliveryMessage) obj).toString()));
	}

}
