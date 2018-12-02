package maas.agents;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import maas.models.Location;
import testUtilities.utilityClasses.TestReaderUtility;

public class TruckTest {
	Truck truck;
	
	public TruckTest() {
		truck =  new Truck("T1",10,new Location(1f, 2f));
	}
	
	@Test
	public void testGetGuid() {
		assertEquals("T1", truck.getGuid());
	}

	@Test
	public void testGetLoadCapacity() {
		assertEquals(10, truck.getLoadCapacity());
	}

	@Test
	public void testGetLocation() {
		assertEquals(1f, truck.getLocation().getX(),0.01);
		assertEquals(2f, truck.getLocation().getY(),0.01);
	}

	@Test
	public void testSetLocation() {
		truck.setLocation(new Location(0f, 0f));
		assertEquals(0f, truck.getLocation().getX(),0.01);
		assertEquals(0f, truck.getLocation().getY(),0.01);
	}

	@Test
	public void testGetCustomers() {
		assertEquals(null, truck.getCustomers());
	}
	@Test
	public void testSetCustomers() {
		TestReaderUtility readerUtility = new TestReaderUtility();
		truck.setCustomers(Arrays.asList(readerUtility.getCustomers()));
		assertEquals("customer-001",truck.getCustomers().get(0).getGuid());
	}

	

}
