package maas.models;

import static org.junit.Assert.*;

import org.junit.Test;
import maas.models.Node;
public class NodeTest {
	private String name="name";
	private String company="company";
	private Location location=new Location(1f, 1f);
	private String guid="guid";
	private String type="type";
	
	Node node = new Node(name, company, location, guid, type);
	@Test
	public void testGetName() {
		assertEquals(name, node.getName());
	}

	@Test
	public void testGetCompany() {
		assertEquals(company, node.getCompany());
	}

	@Test
	public void testGetLocation() {
		assertEquals(location, node.getLocation());
	}

	@Test
	public void testGetGuid() {
		assertEquals(guid, node.getGuid());
	}

	@Test
	public void testGetType() {
		assertEquals(type, node.getType());
	}


}
