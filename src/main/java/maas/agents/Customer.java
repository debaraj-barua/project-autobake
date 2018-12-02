package maas.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;
import maas.models.Location;
import maas.models.Order;
import utils.Time;
import maas.models.Status;

@SuppressWarnings("serial")
public class Customer extends Agent {

	private String guid;
	private String name;
	private String type;
	private Location location;

	private Status status;

	private Map<String, Status> orderStatus;

	private List<Order> orders = new ArrayList<>();
	private Set<Order> sentOrders;
	private Set<Order> deliveredOrders;

	private AID bakeryController;

	
	public Customer() {
		this.guid = null;
		this.name = null;
		this.type = null;
		this.location = null;
	}
	
	public Customer(String guid, String name, String type, Location location) {
		this.guid = guid;
		this.name = name;
		this.type = type;
		this.location = location;
	}

	public void setBakeryController(AID bakeryController) {
		this.bakeryController = bakeryController;
	}

	@Override
	protected void setup() {
		status = Status.WAITING;
		orderStatus = new HashMap<>();

		fillMaps();

		sentOrders = Collections.synchronizedSet(new HashSet<>());
		deliveredOrders = Collections.synchronizedSet(new HashSet<>());

		for (Order order : orders) {
			if (sentOrders.add(order)) {
				addBehaviour(new OrderProducts(order));
			}
		}
	}

	private void fillMaps() {
		for (Order order : orders) {
			orderStatus.put(order.getGuid(), Status.WAITING);
		}
	}
	
	public Map<String, Status> getOrderStatus() {
		return orderStatus;
	}

	@Override
	protected void takeDown() {
		Logger log = LogManager.getLogger(Customer.class);
		log.info("Customer-agent " + getAID().getName() + " terminated.");
		status = Status.FINISHED;
	}

	private class OrderProducts extends SequentialBehaviour {
		Order newOrder;

		public OrderProducts(Order order) {
			newOrder = order;
			this.addSubBehaviour(new CheckOrderTime());
			this.addSubBehaviour(new ExecuteOrder());
			this.addSubBehaviour(new CheckOrderDelivery());
		}

		private class CheckOrderTime extends Behaviour {

			private boolean timeToOrder = false;

			@Override
			public void action() {
				Logger log = LogManager.getLogger(Customer.class);
				if (Time.getTime().getCurrentDate().toSeconds() >= newOrder.getOrderDate().toSeconds()) {
					log.info("Time to order! Current Date: " + Time.getTime().getCurrentDate().toString()
							+ ", Order Date is: " + newOrder.getOrderDate().toString());
					timeToOrder = true;
				}

			}

			@Override
			public boolean done() {
				return timeToOrder;
			}

		}

		private class ExecuteOrder extends OneShotBehaviour {
			@Override
			public void action() {
				Logger log = LogManager.getLogger(Customer.class);
				status = Status.ORDERING;
				orderStatus.put(newOrder.getGuid(), Status.ORDERING);
				Gson gson = new Gson();

				ACLMessage aclmsg = new ACLMessage(ACLMessage.REQUEST);
				if (bakeryController != null) {
					aclmsg.addReceiver(bakeryController);

					// serialize the object
					String jsonOrder = gson.toJson(newOrder);
					aclmsg.setContent(jsonOrder);
					aclmsg.setConversationId("customer-order");
					aclmsg.setReplyWith("aclmsg" + System.currentTimeMillis());
					send(aclmsg);
				}
				MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("customer-order"),
						MessageTemplate.MatchInReplyTo(aclmsg.getReplyWith()));
				ACLMessage reply = myAgent.receive(mt);
				if ((reply != null)) {
					if (reply.getPerformative() == ACLMessage.FAILURE) {
						orderStatus.put(newOrder.getGuid(), Status.FAILED);
						log.info("Order ID " + newOrder.getGuid() + " unsuccessful. No bakeries found.");
						popDeliveredOrders(newOrder.getGuid());
					} else {
						orderStatus.put(newOrder.getGuid(), Status.PREPARING);
						log.info("Order ID " + newOrder.getGuid() + " sent to Bakery Controller.");
					}
				}
				if (sentOrders.isEmpty() && !deliveredOrders.isEmpty()) {
					status = Status.FINISHED;
				}
				status = Status.WAITING;
			}

		}

		private class CheckOrderDelivery extends CyclicBehaviour {
			String conversationId = "customer-confirmation";

			@Override
			public void action() {
				Logger log = LogManager.getLogger(Customer.class);
				MessageTemplate mt = MessageTemplate.MatchConversationId(conversationId);
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					log.info("Order Delivery Status:: " + reply.getContent());
					String orderId = (reply.getContent().split("#"))[1];
					if ((reply.getContent().split("#"))[0].equals("delivered")) {
						log.info("Order " + orderId + " recieved by customer " + guid + " on time.");
					} else if ((reply.getContent().split("#"))[0].equals("delayed")) {
						log.info("Order " + orderId + " recieved by customer " + guid + " later.");
					}
					popDeliveredOrders(orderId);
				} else {
					block(Time.getMillisecondsForMin());
				}
				if (sentOrders.isEmpty() && !deliveredOrders.isEmpty()) {
					status = Status.FINISHED;
				}

			}

		}

		private void popDeliveredOrders(String orderId) {
			List<Order> ordersToremove = new ArrayList<>();
			for (Order order : sentOrders) {
				if ((order.getGuid().equals(orderId)) && (deliveredOrders.add(order))) {
					orderStatus.put(newOrder.getGuid(), Status.FINISHED);
					ordersToremove.add(order);
				}
			}
			sentOrders.removeAll(ordersToremove);
		}

	}

	public String getGuid() {
		return guid;
	}

	public String getCustomerName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public Location getLocation() {
		return location;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Customer [guid=" + guid + ", name=" + name + ", type=" + type + ", location=" + location + ", status="
				+ status + ", orders=" + orders + ", bakeryController=" + bakeryController + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((orders == null) ? 0 : orders.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (!(obj instanceof Customer)) {
			return false;
		}
		return (this.toString().equals(((Customer) obj).toString()));
	}
}
