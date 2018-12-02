package maas.models;

import static org.junit.Assert.*;

import org.junit.Test;

import maas.agents.Bakery;
import maas.agents.Customer;
import testUtilities.utilityClasses.TestReaderUtility;

public class ReaderTest {

	private Meta meta;
	private Bakery[] bakeries;
	private Customer[] customers;
	private Order[] orders;
	private StreetNetwork streetNetwork;
	private Reader reader = new Reader();

	public ReaderTest() throws Exception {
		TestReaderUtility readerUtility = new TestReaderUtility();
		meta = readerUtility.getMeta();
		bakeries = readerUtility.getBakeries();
		customers = readerUtility.getCustomers();
		orders = readerUtility.getOrders();
		streetNetwork = readerUtility.getStreetNetwork();
		reader.readJsonFile("src/test/java/testUtilities/jsonFiles/testReader.json");

	}

	@Test
	public void testGetMeta() {
		assertEquals(meta.getNumberOfBakeries(), reader.getMeta().getNumberOfBakeries());
	}

	@Test
	public void testGetBakeries() {
		assertArrayEquals(bakeries, reader.getBakeries());

	}

	@Test
	public void testGetCustomers() {
		assertArrayEquals(customers, reader.getCustomers());
	}

	@Test
	public void testGetOrders() {
		assertArrayEquals(orders, reader.getOrders());
	}

	@Test
	public void testGetStreetNetwork() {
		assertEquals(streetNetwork, reader.getStreetNetwork());
	}

}
