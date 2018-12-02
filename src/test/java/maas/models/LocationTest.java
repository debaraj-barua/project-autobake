package maas.models;

import static org.junit.Assert.*;

import org.junit.Test;

import maas.models.Location;

public class LocationTest {
	float x = 1;
	float y = 2;
	Location actual_location;
	
	public LocationTest() {
		actual_location=new Location(x,y);
	}
	
	@Test
	public void testgetX() {
		assertEquals(x, actual_location.getX(), 0.0001);
	}

	@Test
	public void testgetY() {
		assertEquals(y, actual_location.getY(), 0.0001);
	}

}

