package maas.models;

import static org.junit.Assert.*;

import org.junit.Test;

public class ProductTest {
	private int boxingTemp = 1;
	private float salesPrice = 50.0f;
	private int breadsPerOven = 4;
	private int breadsPerBox = 5;
	private int itemPrepTime = 5;
	private int doughPrepTime = 6;
	private int bakingTemp = 10;
	private int coolingRate = 10;
	private String guid = "a";
	private int bakingTime = 5;
	private int restingTime = 5;
	private float productionCost = 5f;
	Product actualProduct;

	public ProductTest() {
		int[] values = { boxingTemp, breadsPerOven, breadsPerBox, itemPrepTime, doughPrepTime, bakingTemp, coolingRate,
				bakingTime, restingTime };
		actualProduct = new Product(values, salesPrice, guid, productionCost);
	}

	@Test
	public void testgetGuid() {
		assertEquals(guid, actualProduct.getGuid());
	}

	@Test
	public void testgetDough_prep_time() {
		assertEquals(doughPrepTime, actualProduct.getDoughPrepTime());
	}

	@Test
	public void testgetResting_time() {
		assertEquals(restingTime, actualProduct.getRestingTime());
	}

	@Test
	public void testgetItem_prep_time() {
		assertEquals(itemPrepTime, actualProduct.getItemPrepTime());
	}

	@Test
	public void testgetBreads_per_oven() {
		assertEquals(breadsPerOven, actualProduct.getBreadsPerOven());
	}

	@Test
	public void testgetgetBaking_time() {
		assertEquals(bakingTime, actualProduct.getBakingTime());
	}

	@Test
	public void testgetBaking_temp() {
		assertEquals(bakingTemp, actualProduct.getBakingTemp());
	}

	@Test
	public void testgetCoolingRate() {
		assertEquals(coolingRate, actualProduct.getCoolingRate());
	}

	@Test
	public void testggetBoxingTemp() {
		assertEquals(boxingTemp, actualProduct.getBoxingTemp());
	}

	@Test
	public void testgetBreadsPerBox() {
		assertEquals(breadsPerBox, actualProduct.getBreadsPerBox());
	}

	@Test
	public void testgetProductionCost() {
		assertEquals(productionCost, actualProduct.getProductionCost(), 0.0001);
	}

	@Test
	public void testgetSales_price() {
		assertEquals(salesPrice, actualProduct.getSalesPrice(), 0.0001);
	}

}
