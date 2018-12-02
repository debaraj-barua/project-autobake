package maas.agents;

import static org.junit.Assert.*;

import org.junit.Test;

import maas.models.Location;
import maas.models.Order;
import testUtilities.utilityClasses.TestReaderUtility;

public class BakeryTest {

	private Bakery bakery;
	private Order order;

	public BakeryTest() {
		TestReaderUtility readerUtility = new TestReaderUtility();
		bakery = readerUtility.getBakeries()[0];
		order = readerUtility.getOrders()[0];
	}

	@Test
	public void testGetGuid() {
		assertEquals("bakery-001", bakery.getGuid());
	}

	@Test
	public void testGetBakeryName() {
		assertEquals("Sunspear Bakery", bakery.getBakeryName());
	}

	@Test
	public void testGetLocation() {
		assertEquals(new Location(1.54f,0.1f), bakery.getLocation());
	}

	@Test
	public void testGetOvens() {
		assertEquals(3, (bakery.getOvens()).get(0).getCoolingRate());
	}

	@Test
	public void testGetProducts() {
		assertEquals(2, bakery.getProducts().size());
	}

	@Test
	public void testGetTrucks() {
		assertEquals(26, bakery.getTrucks().get(0).getLoadCapacity());
	}

	@Test
	public void testGetKneadingMachines() {
		assertEquals("kneading-machine-001", bakery.getKneadingMachines().get(0).getGuid());
	}

	@Test
	public void testGetDoughPrepTables() {
		assertEquals("prep-table-001", bakery.getDoughPrepTables().get(0).getGuid());
	}

	@Test
	public void testGetOrderPrice() {
		assertEquals(-1, bakery.getOrderPrice(order), 0.01);
	}

}
