package maas.agents;

import static org.junit.Assert.*;

import org.junit.Test;

import testUtilities.utilityClasses.TestReaderUtility;

public class OvenTest {
	private Oven oven;

	public OvenTest() {
		TestReaderUtility readerUtility = new TestReaderUtility();
		oven = readerUtility.getBakeries()[0].getOvens().get(0);
	}

	@Test
	public void testGetGuid() {
		assertEquals("oven-001", oven.getGuid());
	}

	@Test
	public void testGetCoolingRate() {
		assertEquals(3, oven.getCoolingRate());
	}

	@Test
	public void testGetHeatingRate() {
		assertEquals(3, oven.getHeatingRate());
	}

	@Test
	public void testGetStatus() {
		assertEquals(null, oven.getStatus());
	}

}
