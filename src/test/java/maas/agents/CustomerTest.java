package maas.agents;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import maas.models.Date;
import maas.models.Location;
import maas.models.Order;
import maas.models.ProductsToOrder;
import maas.models.Status;

public class CustomerTest {
	private String guid = "customer01";
	private String name = "CustomerOne";
	private String type = "Type1";
	private Location location = new Location(10, 10);

	private Status status = Status.ORDERING;

	private List<Order> orders = new ArrayList<>();

	Customer customer;

	public CustomerTest() {
		this.customer = new Customer(guid, name, type, location);
		List<ProductsToOrder> products = new ArrayList<>();
		products.add(new ProductsToOrder("bread", 3));
		orders.add(new Order("order101", this.guid, new Date(0), new Date(1800), products));
	}	

	@Test
	public void testGetGuid() {
		assertEquals(this.guid, (customer.getGuid()));
	}

	@Test
	public void testGetCustomerName() {
		assertEquals(this.name, customer.getCustomerName());
	}

	@Test
	public void testGetType() {
		assertEquals(this.type, customer.getType());
	}

	@Test
	public void testGetLocation() {
		assertEquals(this.location, customer.getLocation());
	}
	
	@Test
	public void testGetOrders() {
		assertTrue(customer.getOrders().isEmpty());
	}

	@Test
	public void testSetOrders() {
		customer.setOrders(orders);
		assertEquals(orders, customer.getOrders());		
	}

	

	@Test
	public void testGetStatus() {
		assertEquals(null, customer.getStatus());
	}

	@Test
	public void testSetStatus() {
		customer.setStatus(status);
		assertEquals(status, customer.getStatus());
	}

}
