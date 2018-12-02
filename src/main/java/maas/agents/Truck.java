package maas.agents;

import maas.messages.TruckMessage;
import maas.messages.TruckStatusMessage;
import maas.models.Box;
import maas.models.Location;
import maas.models.Order;
import utils.Time;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class Truck extends Agent implements Serializable {

	private String guid;
	private int load_capacity;
	private Location location;

	private int remainingCapacity;
	private LinkedList<Order> orderList;
	private Map<Order, List<Box>> orderMap;

	private List<Customer> customers;
	private AID deliveryAgent;

	String status;
	String currentLocation;
	String bakeryLocationMsg = "bakery";
	String customerLocationMsg = "customer";

	private static final Logger log = LogManager.getLogger(Truck.class);

	public Truck() {
		this.guid = null;
		this.load_capacity = 0;
		this.location = null;
	}

	public Truck(String guid, int loadCapacity, Location location) {
		this.guid = guid;
		this.load_capacity = loadCapacity;
		this.location = location;
	}

	public String getGuid() {
		return guid;
	}

	public int getLoadCapacity() {
		return load_capacity;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}

	public List<Customer> getCustomers() {
		return customers;
	}

	public AID getDeliveryAgent() {
		return deliveryAgent;
	}

	public void setDeliveryAgent(AID deliveryAgent) {
		this.deliveryAgent = deliveryAgent;
	}

	@Override
	protected void setup() {
		log.info("Hello! Truck-agent " + getAID().getName() + " is ready.");
		remainingCapacity = load_capacity;
		orderList = new LinkedList<>();
		orderMap = new HashMap<>();
		status = "free";
		currentLocation = bakeryLocationMsg;
		addBehaviour(new TruckHandleServer());
		addBehaviour(new MakeTrips());
	}

	@Override
	protected void takeDown() {
		log.info("Truck-agent " + getAID().getName() + " terminated.");
	}

	private class TruckHandleServer extends CyclicBehaviour {
		private String conversationId = "truck-message";

		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchConversationId(conversationId));
			ACLMessage msg = myAgent.receive(mt);
			Gson gson = new Gson();
			if (msg != null) {
				ACLMessage reply = msg.createReply();
				reply.setConversationId(conversationId);
				String jsonContent = msg.getContent();
				TruckMessage truckMessage = gson.fromJson(jsonContent, TruckMessage.class);
				log.info("Truck:" + getAID().getName().split(":")[0] + " received " + truckMessage.getOrder().getGuid()
						+ " in " + truckMessage.getBoxes().size() + " boxes.");
				if (remainingCapacity - truckMessage.getBoxes().size() >= 0) {
					remainingCapacity -= truckMessage.getBoxes().size();
					orderMap.put(truckMessage.getOrder(), truckMessage.getBoxes());
					orderList.add(truckMessage.getOrder());
					reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					reply.setContent(gson.toJson(new TruckStatusMessage("free", bakeryLocationMsg, remainingCapacity)));
				} else {
					reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
					reply.setContent(gson.toJson(new TruckStatusMessage("full", bakeryLocationMsg, remainingCapacity)));
				}

			} else {
				block(Time.getMillisecondsForMin());
			}
		}

	}

	private class MakeTrips extends CyclicBehaviour {

		@Override
		public void action() {
			if (!orderList.isEmpty()) {
				Order order = orderList.remove();
				currentLocation = customerLocationMsg;
				String customerId = order.getCustomerId();
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setConversationId("customer-confirmation");
				for (Customer customer : customers) {
					if (customer.getGuid().equals(customerId)) {
						msg.addReceiver(customer.getAID());
						break;
					}
				}

				if (order.getDeliveryDate().toSeconds() < Time.getTime().getCurrentDate().toSeconds()) {
					log.info("Truck delivered order#" + order.getGuid() + " to customer " + customerId);
					msg.setContent("delivered#" + order.getGuid());
				} else {
					log.info("Truck not able to deliver order#" + order.getGuid() + " to customer " + customerId
							+ " on time.");
					msg.setContent("delayed#" + order.getGuid());
				}
				myAgent.send(msg);
				remainingCapacity += orderMap.get(order).size();
				orderMap.remove(order);
				status = "free";
			}
			currentLocation = bakeryLocationMsg;
		}

	}

	@Override
	public String toString() {
		return "Truck [guid=" + guid + ", load_capacity=" + load_capacity + ", location=" + location + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		result = prime * result + load_capacity;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Truck)) {
			return false;
		}
		return (this.toString().equals(((Truck) obj).toString()));
	}
}
