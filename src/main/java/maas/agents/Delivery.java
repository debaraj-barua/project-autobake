package maas.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import maas.messages.DeliveryMessage;
import maas.messages.TruckMessage;
import maas.models.Box;
import maas.models.Date;
import maas.models.Order;
import maas.models.Product;
import maas.models.ProductsToOrder;
import utils.Time;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

@SuppressWarnings("serial")
public class Delivery extends Agent implements Serializable {

	private List<Product> listOfProducts;
	private AID[] truckAgents;

	private Set<Order> ordersToPack;
	private Set<Order> ordersToShip;
	private ConcurrentHashMap<Product, Integer> collectedProducts;
	private ConcurrentHashMap<Order, Set<Box>> readyToShip;

	private boolean[] trucksFree;
	private int[] truckCapacity;

	private static final Logger log = LogManager.getLogger(Delivery.class);

	public Delivery(AID[] truckAgents, List<Product> listOfProducts, int[] truckCapacity) {
		this.truckAgents = truckAgents;
		this.listOfProducts = listOfProducts;

		this.trucksFree = new boolean[truckAgents.length];
		this.truckCapacity = truckCapacity;

		for (int i = 0; i < truckAgents.length; ++i) {
			if (this.truckCapacity[i] > 0) {
				this.trucksFree[i] = true;
			}
		}
	}

	@Override
	protected void setup() {
		log.info("Hello! Delivery-agent " + getAID().getName() + " is ready.");
		ordersToPack = Collections.synchronizedSet(new HashSet<>());
		ordersToShip = Collections.synchronizedSet(new HashSet<>());
		collectedProducts = new ConcurrentHashMap<>();
		readyToShip = new ConcurrentHashMap<>();

		fillMaps();

		addBehaviour(new CollectOrder());
		addBehaviour(new CreatePackages());
		addBehaviour(new SendToTruck());

	}

	@Override
	protected void takeDown() {
		log.info("Delivery-agent " + getAID().getName() + " terminated.");
	}

	private void fillMaps() {
		for (Product product : listOfProducts) {
			collectedProducts.put(product, 0);
		}
	}

	private class CollectOrder extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchConversationId("send-delivery"));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				String jsonContent = msg.getContent();

				Gson gson = new Gson();

				DeliveryMessage content = gson.fromJson(jsonContent, DeliveryMessage.class);

				log.info("Received " + content.getQuantity() + " " + content.getProduct().getGuid() + " from Bakery"
						+ msg.getSender().getName());

				addOrders(content);
				int previousQuantity = collectedProducts.get(content.getProduct());
				collectedProducts.put(content.getProduct(), previousQuantity + content.getQuantity());
			} else {
				block();
			}
		}

		private void addOrders(DeliveryMessage content) {
			for (Order newOrder : content.getOrdersToProcess()) {
				ordersToPack.add(newOrder);
			}
		}
	}

	private class CreatePackages extends CyclicBehaviour {
		public void action() {
			if (!ordersToPack.isEmpty()) {
				for (Order order : ordersToPack) {
					if (isOrderComplete(order)) {
						log.info("Packing order :" + order.toString());
						ordersToShip.add(order);
						pack(order);
						break;
					}
				}
			} else {
				block();
			}
		}

		private boolean isOrderComplete(Order order) {
			if (ordersToShip.contains(order)) {
				return false;
			} else {
				for (ProductsToOrder productToOrder : order.getProducts()) {
					if (collectedProducts.get(new Product(productToOrder.getProductid())) < productToOrder
							.getQuantity()) {
						return (false);
					}
				}
			}
			return (true);
		}

		private void pack(Order parentOrder) {
			Product packedProduct;
			Set<Box> boxes = new HashSet<>();
			for (ProductsToOrder productToOrder : parentOrder.getProducts()) {
				packedProduct = listOfProducts.get(listOfProducts.indexOf(new Product(productToOrder.getProductid())));
				int quantity = productToOrder.getQuantity();
				if (collectedProducts.get(packedProduct) > quantity && quantity > 0) {
					int previousQuantity = collectedProducts.get(new Product(productToOrder.getProductid()));
					while (quantity > packedProduct.getBreadsPerBox()) {
						quantity -= packedProduct.getBreadsPerBox();
						boxes.add(new Box(packedProduct, packedProduct.getBreadsPerBox(), parentOrder));
					}
					if (quantity > 0) {
						boxes.add(new Box(packedProduct, quantity, parentOrder));
					}
					collectedProducts.put(packedProduct, previousQuantity - productToOrder.getQuantity());
				}
			}
			ordersToPack.remove(parentOrder);
			readyToShip.put(parentOrder, boxes);
			log.info("Ready to ship order " + parentOrder.getGuid() + " in " + readyToShip.get(parentOrder).size()
					+ " boxes");
		}
	}

	private class SendToTruck extends CyclicBehaviour {
		private int indexOfFreeTruck = -1;

		public void action() {
			if (!ordersToShip.isEmpty()) {
				if (findfreeTruck()) {
					Gson gson = new Gson();
					ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
					TruckMessage truckMessageContent = generateTruckMessage();
					if (truckMessageContent != null) {
						msg.addReceiver(truckAgents[indexOfFreeTruck]);
						msg.setContent(gson.toJson(truckMessageContent));
						msg.setConversationId("truck-message");
						myAgent.send(msg);
					}
					MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("truck-message"),
							MessageTemplate.MatchInReplyTo(msg.getReplyWith()));
					ACLMessage reply = myAgent.receive(mt);
					if ((reply != null) && (reply.getPerformative() == ACLMessage.REJECT_PROPOSAL)) {
						log.info("Truck full.");
					}

				} else {
					block(Time.getMillisecondsForMin());
				}
			}
		}

		private TruckMessage generateTruckMessage() {
			Order newOrder = null;
			List<Box> boxes = new ArrayList<>();
			Date deliveryDate = new Date(0);

			for (Order order : ordersToShip) {
				if (deliveryDate.toSeconds() <= 0 || order.getDeliveryDate().toSeconds() < deliveryDate.toSeconds()) {
					deliveryDate = order.getDeliveryDate();
					newOrder = order;
				}
			}

			if (newOrder != null && readyToShip.get(newOrder) != null
					&& findfreeTruck(readyToShip.get(newOrder).size())) {
				boxes.addAll(readyToShip.get(newOrder));
				readyToShip.remove(newOrder);
				return (new TruckMessage(newOrder, boxes));
			} else {
				return null;
			}

		}

		public boolean findfreeTruck() {
			for (int i = 0; i < trucksFree.length; ++i) {
				if (trucksFree[i]) {
					return true;
				}
			}
			return false;
		}

		public boolean findfreeTruck(int numOfBoxes) {
			for (int i = 0; i < trucksFree.length; ++i) {
				if (trucksFree[i] && truckCapacity[i] >= numOfBoxes) {
					truckCapacity[i] -= numOfBoxes;
					if (truckCapacity[i] <= 0) {
						trucksFree[i] = false;
					}
					indexOfFreeTruck = i;
					return true;
				}
			}
			return false;
		}

	}

}
