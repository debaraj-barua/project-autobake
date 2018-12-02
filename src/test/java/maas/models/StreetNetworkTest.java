package maas.models;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;


import testUtilities.utilityClasses.TestReaderUtility;

public class StreetNetworkTest {

	private boolean directed;
	private List<Node> nodes=new ArrayList<Node>();
	private List<Link> links=new ArrayList<Link>();

	StreetNetwork streetNetwork;



	public StreetNetworkTest() {
		TestReaderUtility readerUtility = new TestReaderUtility();
		streetNetwork=readerUtility.getStreetNetwork();
		directed=true;
		Node [] nodesArray=readerUtility.getNodes();
		nodes=Arrays.asList(nodesArray);
		links.add(new Link("node-032", "edge-001", (float) 2.8773383615449761, "node-020"));
		links.add(new Link("node-032", "edge-002", (float) 1.4339855408383024, "node-001"));
	}


	@Test
	public void testIsDirected() {
		assertEquals(directed, streetNetwork.isDirected());
	}


	@Test
	public void testGetNodes() {
		assertEquals(nodes, streetNetwork.getNodes());
	}

	@Test
	public void testGetLinks() {
		assertEquals(links, streetNetwork.getLinks());
	}
}
