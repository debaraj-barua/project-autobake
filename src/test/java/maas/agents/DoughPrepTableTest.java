package maas.agents;

import static org.junit.Assert.*;

import org.junit.Test;

import testUtilities.utilityClasses.TestReaderUtility;

public class DoughPrepTableTest {
	private DoughPrepTable doughPrepTable;

	public DoughPrepTableTest() {
		TestReaderUtility readerUtility = new TestReaderUtility();
		doughPrepTable = readerUtility.getBakeries()[0].getDoughPrepTables().get(0);
	}

	@Test
	public void testGetGuid() {
		assertEquals("prep-table-001", doughPrepTable.getGuid());
	}

	@Test
	public void testGetStatus() {
		assertEquals(null, doughPrepTable.getStatus());
	}

}
