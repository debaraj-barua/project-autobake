package maas.models;

import static org.junit.Assert.*;

import org.junit.Test;

import maas.models.Meta;
import maas.models.CustomerInformation;

public class MetaTest {
	int bakeries=10;
	int products=3;
	int orders=100;
	CustomerInformation customers=new CustomerInformation(1,2,3);
	int duration_days=30;
	Meta actual_meta;
	
	public MetaTest() {
		actual_meta=new Meta(bakeries, products,
				orders,customers,duration_days);
	}

	@Test
	public void testgetOrders() {
		assertEquals(orders, actual_meta.getNumberOfOrders());
	}
	@Test
	public void testgetTotal_bakeries() {
		assertEquals(bakeries, actual_meta.getNumberOfBakeries());
	}
	@Test
	public void testgetDuration_days() {
		assertEquals(duration_days, actual_meta.getDurationDays());
	}
	@Test
	public void testgetNumberOfProducts() {
		assertEquals(products, actual_meta.getNumberOfProducts());
	}
	@Test
	public void testgetCustomers() {
		assertEquals(customers, actual_meta.getCustomers());
	}
}

