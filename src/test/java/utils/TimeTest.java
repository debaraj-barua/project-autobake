package utils;

import static org.junit.Assert.*;
import org.junit.Test;

import maas.models.Date;

public class TimeTest {
	private int currentSeconds=Time.getTime().getCurrentDate().toSeconds();
	private static final int MILLISECONDSPERMIN = 10;
	
	@Test
	public void testGetCurrentDate() {
		assertEquals(new Date(currentSeconds), Time.getTime().getCurrentDate());
	}

	@Test
	public void testGetMillisecondsForMin() {
		assertEquals(MILLISECONDSPERMIN, Time.getMillisecondsForMin());
	}
}
