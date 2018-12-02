package testUtilities.utilityClasses;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import maas.agents.Bakery;
import maas.agents.Customer;
import maas.models.Date;
import maas.models.Meta;
import maas.models.Node;
import maas.models.Order;
import maas.models.ProductsToOrder;
import maas.models.ReaderTest;
import maas.models.StreetNetwork;

public class TestReaderUtility {
	private Meta meta;
	private Bakery[] bakeries;
	private Customer[] customers;
	private Order[] orders;
	private StreetNetwork streetNetwork;
	private Node [] nodesArray;

	public TestReaderUtility() {
		meta = (Meta) parseFromJson("src/test/java/testUtilities/jsonFiles/testMeta.json", Meta.class);
		bakeries = (Bakery[]) parseFromJson("src/test/java/testUtilities/jsonFiles/testBakeries.json", Bakery[].class);
		customers = (Customer[]) parseFromJson("src/test/java/testUtilities/jsonFiles/testCustomers.json",
				Customer[].class);
		orders = parseOrder("src/test/java/testUtilities/jsonFiles/testOrders.json");
		streetNetwork = (StreetNetwork) parseFromJson("src/test/java/testUtilities/jsonFiles/testStreetNetwork.json",
				StreetNetwork.class);
		nodesArray=(Node[]) parseFromJson("src/test/java/testUtilities/jsonFiles/testNode.json", Node[].class);
	}
	
	public Node[] getNodes() {
		return this.nodesArray;		
	}

	public Meta getMeta() {
		return meta;
	}

	public Bakery[] getBakeries() {
		return bakeries;
	}

	public Customer[] getCustomers() {
		return customers;
	}

	public Order[] getOrders() {
		return orders;
	}

	public StreetNetwork getStreetNetwork() {
		return streetNetwork;
	}

	@SuppressWarnings("rawtypes")
	public Object parseFromJson(String path, Class classType) {
		Logger log = LogManager.getLogger(ReaderTest.class);
		Gson gson = new GsonBuilder().create();
		JsonReader jReader;
		Object obj = null;
		try {
			jReader = new JsonReader(new InputStreamReader(new FileInputStream(path)));
			jReader.beginObject();
			jReader.nextName();
			obj = gson.fromJson(jReader, classType);
			jReader.endObject();

		} catch (Exception e) {
			log.error(e);
			;
		}

		return obj;

	}

	public Order[] parseOrder(String path) {
		Order[] createdOrders = new Order[meta.getNumberOfOrders()];
		Logger log = LogManager.getLogger(ReaderTest.class);
		Gson gson = new GsonBuilder().create();
		JsonReader jReader;
		try {
			jReader = new JsonReader(new InputStreamReader(new FileInputStream(path)));
			jReader.beginObject();
			jReader.nextName();
			jReader.beginArray();
			for (int i = 0; i < 2; ++i) {
				jReader.beginObject();

				jReader.nextName();

				Date order_date = gson.fromJson(jReader, Date.class);

				jReader.nextName();

				String guid = jReader.nextString();

				jReader.nextName();

				jReader.beginObject();

				ArrayList<ProductsToOrder> list = new ArrayList<ProductsToOrder>();

				while (jReader.peek() != JsonToken.END_OBJECT) {
					String product_name = jReader.nextName();
					int quantity = jReader.nextInt();
					ProductsToOrder productsToOrder = new ProductsToOrder(product_name, quantity);
					list.add(productsToOrder);
				}

				jReader.endObject();
				jReader.nextName();

				String customer_id = jReader.nextString();

				jReader.nextName();

				Date delivery_date = gson.fromJson(jReader, Date.class);

				createdOrders[i] = new Order(guid, customer_id, order_date, delivery_date, list);

				jReader.endObject();
			}
			jReader.endArray();
			jReader.endObject();

		} catch (Exception e) {
			log.error(e);
		}
		return createdOrders;

	}

}
