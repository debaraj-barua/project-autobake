package maas.models;

import jade.util.leap.Serializable;

@SuppressWarnings("serial")
public class Box implements Serializable{
	Product packedProduct;
	int quantity;
	Order parentOrder;
	
	public Box(Product packedProduct, int quantity, Order parentOrder) {
		this.packedProduct = packedProduct;
		this.quantity = quantity;
		this.parentOrder = parentOrder;
	}

	public Product getPackedProduct() {
		return packedProduct;
	}

	public int getQuantity() {
		return quantity;
	}

	public Order getParentOrder() {
		return parentOrder;
	}

	@Override
	public String toString() {
		return "Package [packedProduct=" + packedProduct + ", quantity=" + quantity + ", parentOrder=" + parentOrder
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((packedProduct == null) ? 0 : packedProduct.hashCode());
		result = prime * result + ((parentOrder == null) ? 0 : parentOrder.hashCode());
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
		if (!(obj instanceof Box)) {
			return false;
		}
		
		return (this.toString().equals(((Box)obj).toString()));
	}
	
	
	
	
}
