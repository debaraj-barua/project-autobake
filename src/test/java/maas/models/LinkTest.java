package maas.models;

import static org.junit.Assert.*;

import org.junit.Test;

import maas.models.Link;

public class LinkTest {
	private String source = "a";
	private String guid = "b";
	private float dist = 5.0f;
	private String target = "c" ;
	Link  actual_link;
	public LinkTest() {
		actual_link=new Link(source,guid,dist,target);
	}
	
	@Test
	public void testgetSource() {
		assertEquals(source, actual_link.getSource());
	}

	@Test
	public void testgetGuid(){
		assertEquals(guid, actual_link.getGuid());
	}
	
	@Test
	public void testgetTarget(){
		assertEquals(target, actual_link.getTarget());
	}
	@Test
	public void testgetDist(){
		assertEquals(dist, actual_link.getDist(),0.002);
	}
}

