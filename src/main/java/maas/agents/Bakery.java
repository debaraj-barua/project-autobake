package maas.agents;

import maas.models.Location;
import maas.models.Order;
import maas.models.Product;
import maas.models.ProductsToOrder;
import maas.models.Status;
import maas.messages.BakingMessage;
import maas.messages.DeliveryMessage;
import utils.Time;
import maas.messages.KneadingMessage;
import maas.messages.PreparationMessage;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

@SuppressWarnings("serial")
public class Bakery extends Agent implements Serializable {

	private String guid;
	private String name;
	private Location location;
	private List<Oven> ovens;
	private List<Product> products;
	private List<Truck> trucks;
	private List<KneadingMachine> kneading_machines;
	private List<DoughPrepTable> dough_prep_tables;

	private AID kneadingMachineControllerAgent;
	private AID doughPrepTableControllerAgent;
	private AID ovenControllerAgent;
	private AID deliveryAgent;

	private List<Order> ordersToProcess;

	private Status status;

	private static final Logger log = LogManager.getLogger(Bakery.class);

	private Map<String, Integer> readyToKnead;
	private Set<String> currentlyKneading;
	private Map<String, Integer> readyToPrepare;
	private Map<String, Integer> readyToBake;
	private Map<String, Integer> readyToDeliver;

	public Status getStatus() {
		return status;
	}

	public void setKneadingMachineControllerAgent(AID kneadingMachineControllerAgent) {
		this.kneadingMachineControllerAgent = kneadingMachineControllerAgent;
	}

	public void setDoughPrepTableControllerAgent(AID doughPrepTableControllerAgent) {
		this.doughPrepTableControllerAgent = doughPrepTableControllerAgent;
	}

	public void setOvenControllerAgent(AID ovenControllerAgent) {
		this.ovenControllerAgent = ovenControllerAgent;
	}

	public void setDeliveryAgent(AID deliveryAgent) {
		this.deliveryAgent = deliveryAgent;
	}

	public String getGuid() {
		return guid;
	}

	public String getBakeryName() {
		return name;
	}

	public Location getLocation() {
		return location;
	}

	public List<Oven> getOvens() {
		return ovens;
	}

	public List<Product> getProducts() {
		return products;
	}

	public List<Truck> getTrucks() {
		return trucks;
	}

	public List<KneadingMachine> getKneadingMachines() {
		return kneading_machines;
	}

	public List<DoughPrepTable> getDoughPrepTables() {
		return dough_prep_tables;
	}

	@Override
	protected void setup() {
		log.info("Hello! Bakery-agent " + getAID().getName() + " is ready.");

		status = Status.WAITING;

		ordersToProcess = new ArrayList<>();
		readyToKnead = new HashMap<>();
		readyToPrepare = new HashMap<>();
		readyToBake = new HashMap<>();
		readyToDeliver = new HashMap<>();
		currentlyKneading = new HashSet<>();

		fillMaps();

		addBehaviour(new OrderHandleServer());

		addBehaviour(new SendToKnead());
		addBehaviour(new FinishedKneading());

		addBehaviour(new SendToPreparation());
		addBehaviour(new FinishedPreparing());

		addBehaviour(new SendToBaking());
		addBehaviour(new FinishedBaking());

		addBehaviour(new SendToDelivery());
	}

	@Override
	protected void takeDown() {
		log.info("Bakery-agent " + getAID().getName() + " terminated.");
		status = Status.FINISHED;
	}

	private void fillMaps() {
		for (Product product : products) {
			String productId = product.getGuid();
			readyToKnead.put(productId, 0);
			readyToPrepare.put(productId, 0);
			readyToBake.put(productId, 0);
			readyToDeliver.put(productId, 0);
		}
	}

	public double getOrderPrice(Order order) {
		// Check if all products exist, and if delivery date is viable
		boolean allProductsValid = true;
		double orderPrepTime = 0;
		double orderPrice = 0;
		double timeRemaining = 0;
		boolean orderDeliverable = false;
		for (ProductsToOrder productsToOrder : order.getProducts()) {
			boolean foundProduct = false;
			for (Product product : products) {
				if (product.getGuid().equals(productsToOrder.getProductid())) {
					orderPrepTime += product.getTotalTime() * 60;
					orderPrice += product.getSalesPrice();
					foundProduct = true;
					break;
				}
			}
			if (!foundProduct) {
				log.info("product " + productsToOrder.getProductid() + " not found in " + this.name);
				allProductsValid = false;
			}
		}
		try {
			timeRemaining = order.getDeliveryDate().subtract(Time.getTime().getCurrentDate()).toSeconds();
			orderDeliverable = (orderPrepTime < timeRemaining);
		} catch (IllegalArgumentException e) {
			orderDeliverable = false;
			log.warn("Delivery Date: " + order.getDeliveryDate() + "; Order Date: " + order.getOrderDate());
		}
		if (allProductsValid && orderDeliverable) {
			return orderPrice;
		}
		return -1;
	}

	private class OrderHandleServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchConversationId("order-for-bakery");
			ACLMessage msg = myAgent.receive(mt);
			Gson gson = new Gson();
			if (msg != null) {
				String jsonOrder = msg.getContent();
				Order newOrder = gson.fromJson(jsonOrder, Order.class);
				ACLMessage reply = msg.createReply();
				if (msg.getPerformative() == ACLMessage.REQUEST) {
					double orderPrice = getOrderPrice(newOrder);
					if (orderPrice > -1) {
						reply.setPerformative(ACLMessage.PROPOSE);
						reply.setContent(String.valueOf(orderPrice));
					} else {
						reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
						reply.setContent("not-available");
					}
					send(reply);
				} else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
					log.info("Bakery-agent " + name + " received an order.");
					log.info(
							"Received Order id: " + newOrder.getGuid() + " from Customer: " + newOrder.getCustomerId());
					log.info("Order Details:");
					log.info(
							"Order Date: " + newOrder.getOrderDate() + " Delivery Date: " + newOrder.getDeliveryDate());
					ordersToProcess.add(newOrder);
					reply.setPerformative(ACLMessage.CONFIRM);
					reply.setContent("Order-processing");
					send(reply);
					for (ProductsToOrder product : newOrder.getProducts()) {
						int previousNumber = readyToKnead.get(product.getProductid());
						readyToKnead.put(product.getProductid(), previousNumber + product.getQuantity());
					}
				}
			} else {
				block();
			}

		}
	}

	private class SendToKnead extends CyclicBehaviour {
		public void action() {
			if (isWorkingTime()) {
				String product = getNextProductToKnead();
				if (product != null) {
					status = Status.SENDING;
					ACLMessage aclmsg = new ACLMessage(ACLMessage.REQUEST);
					if (kneadingMachineControllerAgent != null) {
						aclmsg.addReceiver(kneadingMachineControllerAgent);

						KneadingMessage content = createKneadingMessage(product);

						Gson gson = new Gson();

						String jsonContent = gson.toJson(content);

						// serialize the object
						aclmsg.setContent(jsonContent);
						send(aclmsg);

						currentlyKneading.add(product);
					}
					status = Status.WAITING;
				} else {
					block(Time.getMillisecondsForMin());
				}
			}

		}

		private String getNextProductToKnead() {
			for (Map.Entry<String, Integer> product : readyToKnead.entrySet()) {
				if (product.getValue() > 0 && !currentlyKneading.contains(product.getKey())) {
					return product.getKey();
				}
			}
			return null;
		}

		private KneadingMessage createKneadingMessage(String product) {
			Product productInformation = getProductByGuid(product);

			int doughPrepTime = 0;
			int restingTime = 0;

			if (productInformation != null) {
				doughPrepTime = productInformation.getDoughPrepTime();
				restingTime = productInformation.getRestingTime();
			}

			return new KneadingMessage(product, doughPrepTime, restingTime);
		}
	}

	private class FinishedKneading extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("finished-kneading"));
			ACLMessage msg = myAgent.receive(mt);

			if (msg != null) {
				String productId = msg.getContent();

				int quantity = readyToKnead.put(productId, 0);

				currentlyKneading.remove(productId);

				int previousValue = readyToPrepare.get(productId);

				readyToPrepare.put(productId, previousValue + quantity);

				LogManager.getLogger(Bakery.class)
						.info("" + productId + " finished kneading and resting, ready to prepare");
			} else {
				block();
			}
		}
	}

	private class SendToPreparation extends CyclicBehaviour {
		public void action() {

			if (isWorkingTime()) {
				Map.Entry<String, Integer> product = null;

				for (Map.Entry<String, Integer> currentProduct : readyToPrepare.entrySet()) {
					if (currentProduct.getValue() > 0) {
						product = currentProduct;
					}
				}

				if (product != null) {
					status = Status.SENDING;
					ACLMessage aclmsg = new ACLMessage(ACLMessage.REQUEST);
					if (doughPrepTableControllerAgent != null) {
						aclmsg.addReceiver(doughPrepTableControllerAgent);

						PreparationMessage content = createPreparationMessage(product.getKey(), product.getValue());

						Gson gson = new Gson();

						String jsonContent = gson.toJson(content);

						aclmsg.setContent(jsonContent);
						send(aclmsg);

						product.setValue(0);
					}
					status = Status.WAITING;
				} else {
					block(Time.getMillisecondsForMin());
				}
			}
		}

		private PreparationMessage createPreparationMessage(String product, int quantity) {
			Product productInformation = getProductByGuid(product);

			int itemPrepTime = 0;

			if (productInformation != null) {
				itemPrepTime = productInformation.getItemPrepTime();
			}

			return new PreparationMessage(product, quantity, itemPrepTime);
		}
	}

	private class FinishedPreparing extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("finished-preparation"));
			ACLMessage msg = myAgent.receive(mt);

			if (msg != null) {
				String content = msg.getContent();

				Gson gson = new Gson();

				PreparationMessage msgContent = gson.fromJson(content, PreparationMessage.class);

				int previousQuantity = readyToBake.get(msgContent.getProductId());

				readyToBake.put(msgContent.getProductId(), previousQuantity + msgContent.getQuantity());

				LogManager.getLogger(Bakery.class).info(msgContent.getQuantity() + " " + msgContent.getProductId()
						+ " finished preparation, ready to bake");
			} else {
				block();
			}
		}
	}

	private class SendToBaking extends CyclicBehaviour {
		public void action() {
			if (isWorkingTime()) {
				Map.Entry<String, Integer> product = null;

				for (Map.Entry<String, Integer> currentProduct : readyToBake.entrySet()) {
					if (currentProduct.getValue() > 0) {
						product = currentProduct;
					}
				}

				if (product != null) {
					status = Status.SENDING;
					ACLMessage aclmsg = new ACLMessage(ACLMessage.REQUEST);
					if (ovenControllerAgent != null) {
						aclmsg.addReceiver(ovenControllerAgent);

						BakingMessage content = createBakingMessage(product.getKey(), product.getValue());

						Gson gson = new Gson();

						String jsonContent = gson.toJson(content);

						aclmsg.setContent(jsonContent);
						send(aclmsg);

						product.setValue(0);
					}
					status = Status.WAITING;
				} else {
					block(Time.getMillisecondsForMin());
				}
			}
		}

		private BakingMessage createBakingMessage(String product, int quantity) {
			Product productInformation = getProductByGuid(product);

			int bakingTemp = 0;
			int bakingTime = 0;
			int breadsPerOven = 0;
			int boxingTemp = 0;

			if (productInformation != null) {
				bakingTemp = productInformation.getBakingTemp();
				bakingTime = productInformation.getBakingTime();
				breadsPerOven = productInformation.getBreadsPerOven();
				boxingTemp = productInformation.getBoxingTemp();
			}

			return new BakingMessage(product, quantity, bakingTemp, bakingTime, breadsPerOven, boxingTemp);
		}
	}

	private class FinishedBaking extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("finished-baking"));
			ACLMessage msg = myAgent.receive(mt);

			if (msg != null) {
				String content = msg.getContent();

				Gson gson = new Gson();

				BakingMessage msgContent = gson.fromJson(content, BakingMessage.class);

				int previousQuantity = readyToDeliver.get(msgContent.getProductId());

				readyToDeliver.put(msgContent.getProductId(), previousQuantity + msgContent.getQuantity());

				LogManager.getLogger(Bakery.class).info(msgContent.getQuantity() + " " + msgContent.getProductId()
						+ " finished baking, ready to deliver");
			} else {
				block();
			}
		}
	}

	private class SendToDelivery extends CyclicBehaviour {
		public void action() {
			Map.Entry<String, Integer> product = null;

			for (Map.Entry<String, Integer> currentProduct : readyToDeliver.entrySet()) {
				if (currentProduct.getValue() > 0) {
					product = currentProduct;
				}
			}

			if (product != null) {

				ACLMessage aclmsg = new ACLMessage(ACLMessage.REQUEST);
				if (deliveryAgent != null) {
					aclmsg.addReceiver(deliveryAgent);

					Product finishedProduct = null;

					for (Product productInformation : products) {
						if (productInformation.getGuid().equals(product.getKey())) {
							finishedProduct = productInformation;
							break;
						}
					}

					DeliveryMessage content = new DeliveryMessage(ordersToProcess, finishedProduct, product.getValue());

					Gson gson = new Gson();

					String jsonContent = gson.toJson(content);
					aclmsg.setConversationId("send-delivery");
					aclmsg.setContent(jsonContent);
					send(aclmsg);

					product.setValue(0);
				}
			} else {
				block(Time.getMillisecondsForMin());
			}
		}

	}

	@Override
	public String toString() {
		return "Bakery [guid=" + guid + ", name=" + name + ", location=" + location + ", ovens=" + ovens + ", products="
				+ products + ", trucks=" + trucks + ", kneading_machines=" + kneading_machines + ", dough_prep_tables="
				+ dough_prep_tables + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dough_prep_tables == null) ? 0 : dough_prep_tables.hashCode());
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		result = prime * result + ((kneading_machines == null) ? 0 : kneading_machines.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ovens == null) ? 0 : ovens.hashCode());
		result = prime * result + ((products == null) ? 0 : products.hashCode());
		result = prime * result + ((trucks == null) ? 0 : trucks.hashCode());
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
		if (!(obj instanceof Bakery)) {
			return false;
		}
		return (this.toString().equals(((Bakery) obj).toString()));
	}

	private boolean isWorkingTime() {
		int currentHour = Time.getTime().getCurrentDate().getHour();

		return ((currentHour >= 0) && (currentHour <= 12));
	}

	private Product getProductByGuid(String guid) {
		Product product = null;
		for (Product productInformation : products) {
			if (productInformation.getGuid().equals(guid)) {
				product = productInformation;
				break;
			}
		}

		return product;
	}
}
