package maas.models;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import maas.models.Order;
import maas.models.ProductsToOrder;
import maas.models.Date;

public class OrderTest {
	private String guid = "a";
	private String customerId = "b";
	private Date orderDate=new Date(10, 5, 2);
	private Date deliveryDate=new Date(3600);
	private List<ProductsToOrder> products=new ArrayList<ProductsToOrder>();;
	Order actualOrder;
	
	public OrderTest() {
		ProductsToOrder p1=new ProductsToOrder("product1", 10);
		ProductsToOrder p2=new ProductsToOrder("product2", 20);
		products.add(p1);
		products.add(p2);
		actualOrder=new Order(guid, customerId, orderDate, deliveryDate, products);
	}

	@Test
	public void testgetGuid() {
		assertEquals(guid, actualOrder.getGuid());
	}
	@Test
	public void testgetCustomerId() {
		assertEquals(customerId, actualOrder.getCustomerId());
	}
	@Test
	public void tesgetOrderDate() {
		assertEquals(orderDate, actualOrder.getOrderDate());
	}
	@Test
	public void testgetDeliveryDate() {
		assertEquals(deliveryDate, actualOrder.getDeliveryDate());
	}
	@Test
	public void testgetProducts() {
		assertEquals(products, actualOrder.getProducts());
	}
	@Test
	public void testgetProductIds() {
		String[] productIds={"product1","product2"};
		assertArrayEquals(productIds, actualOrder.getProductIds());
	}
	
}

