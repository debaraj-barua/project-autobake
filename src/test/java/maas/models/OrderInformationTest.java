package maas.models;

import static org.junit.Assert.*;

import org.junit.Test;

public class OrderInformationTest {
	private String total="total";
	private String avg_per_day="avg";
	
	OrderInformation orderInfo = new OrderInformation(total, avg_per_day); 
	@Test
	public void testGetTotal() {
		assertEquals(total,orderInfo.getTotal());
	}

	@Test
	public void testGetAvgPerDay() {
		assertEquals(avg_per_day,orderInfo.getAvgPerDay());
	}

}
