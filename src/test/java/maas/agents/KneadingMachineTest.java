package maas.agents;

import static org.junit.Assert.*;

import org.junit.Test;

import testUtilities.utilityClasses.TestReaderUtility;

public class KneadingMachineTest {
	private KneadingMachine kneadingMachine;

	public KneadingMachineTest() {
		TestReaderUtility readerUtility = new TestReaderUtility();
		kneadingMachine = readerUtility.getBakeries()[0].getKneadingMachines().get(1);
	}

	@Test
	public void testGetGuid() {
		assertEquals("kneading-machine-002", kneadingMachine.getGuid());
	}

	@Test
	public void testGetStatus() {
		assertEquals(null, kneadingMachine.getStatus());
	}

}
