package maas.models;

import static org.junit.Assert.*;

import org.junit.Test;

public class ProductsToOrderTest {
	private String productid="bread";
	private int quantity=5;
	ProductsToOrder productToOrder =new ProductsToOrder(productid,quantity);
	

	@Test
	public void testGetProductid() {
		assertEquals(productid,productToOrder.getProductid());
	}
	@Test
	public void testGetQuantity() {
		assertEquals(quantity, productToOrder.getQuantity());
	}
	@Test
	public void testToString() {
		assertEquals(productid + ":" + quantity,productToOrder.toString());
	}

}
