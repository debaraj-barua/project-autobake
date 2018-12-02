package maas.models;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import maas.agents.Bakery;
import maas.agents.Customer;

public class Reader {

	private Meta meta;
	private Bakery[] bakeries;
	private Customer[] customers;
	private Order[] orders;
	private StreetNetwork street_network;

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
		return street_network;
	}

	public void readJsonFile(String path) {
		Logger log = LogManager.getLogger(Reader.class);
		
		try (
				InputStream stream = new FileInputStream(path);
				JsonReader reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
				){
			Gson gson = new GsonBuilder().create();

			reader.beginObject();

			while (reader.hasNext()) {
				String name = reader.nextName();

				switch (name) {
				case "meta":
					parseMeta(reader);
					break;
				case "bakeries":
					bakeries = gson.fromJson(reader, Bakery[].class);
					break;
				case "customers":
					customers = gson.fromJson(reader, Customer[].class);
					break;
				case "orders":
					parseOrders(gson, reader);
					break;
				case "street_network":
					street_network = gson.fromJson(reader, StreetNetwork.class);
					break;
				default:
					reader.skipValue();
					break;
				}
			}
			log.info("Successfully read json file!");
		} catch (UnsupportedEncodingException e) {
			log.error("Unsupported Encoding for Json file", e);
		} catch (IOException e) {
			log.error("Error in reading Json File", e);
		}
	}

	private void parseOrders(Gson gson, JsonReader reader) {
		Order[] createdOrders = new Order[meta.getNumberOfOrders()];

		try {
			reader.beginArray();

			for (int i = 0; i < meta.getNumberOfOrders(); ++i) {
				reader.beginObject();
				
				reader.nextName();
				
				Date orderDate = gson.fromJson(reader, Date.class);
				
				reader.nextName();
				
				String guid = reader.nextString();
				
				reader.nextName();
				
				reader.beginObject();
				
				ArrayList<ProductsToOrder> list = new ArrayList<>();
				
				while(reader.peek() != JsonToken.END_OBJECT) {
					String productName = reader.nextName();
					int quantity = reader.nextInt();
					ProductsToOrder productsToOrder = new ProductsToOrder(productName, quantity);
					list.add(productsToOrder);
				}
				
				reader.endObject();
				
				reader.nextName();
				
				String customerId = reader.nextString();
				
				reader.nextName();
				
				Date deliveryDate = gson.fromJson(reader, Date.class);
				
				createdOrders[i] = new Order(guid, customerId, orderDate, deliveryDate, list);
				
				reader.endObject();
			}
			
		} catch (IOException e) {
			Logger log = LogManager.getLogger(Reader.class);
			log.error("Error in processing orders from Json File", e);
		}

		this.orders = createdOrders;
	}
	
	private void parseMeta(JsonReader reader) {
		try {
			reader.beginObject();
			
			int numBakeries = 0;
			CustomerInformation customerInformation = null;
			int durationDays = 0;
			int numProducts = 0;
			int numOrders = 0;
			
			while(reader.peek() != JsonToken.END_OBJECT) {
				String name = reader.nextName();
				
				switch (name) {
				case "bakeries":
					numBakeries = reader.nextInt();
					break;
				case "customers":
					reader.beginObject();
					
					reader.nextName();
					
					int numType1 = reader.nextInt();
					
					reader.nextName();
					
					int numType2 = reader.nextInt();
					
					reader.nextName();
					
					int numType3 = reader.nextInt();
					
					reader.endObject();
					
					customerInformation = new CustomerInformation(numType1, numType2, numType3);
					break;
				case "duration_days":
					durationDays = reader.nextInt();
					break;
				case "products":
					numProducts = reader.nextInt();
					break;
				case "orders":
					numOrders = reader.nextInt();
					break;
				default:
					reader.skipValue();
					break;
				}
			}
			
			reader.endObject();
			
			this.meta = new Meta(numBakeries, numProducts, numOrders, customerInformation, durationDays);
			
		} catch (IOException e) {
			Logger log = LogManager.getLogger(Reader.class);
			log.error("Error in processing meta information from Json File", e);
		}	
	}
}
