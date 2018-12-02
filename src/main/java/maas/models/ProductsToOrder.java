package maas.models;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ProductsToOrder implements Serializable {
	
	private String productid;
	private int quantity;
	
	public ProductsToOrder(String productid, int quantity) {
		this.productid = productid;
		this.quantity = quantity;
	}

	public String getProductid() {
		return productid;
	}

	public int getQuantity() {
		return quantity;
	}

	public String toString() {
		return productid + ":" + quantity;
	}

}
