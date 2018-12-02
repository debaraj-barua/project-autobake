package maas.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import maas.models.Order;
import utils.Time;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("serial")
public class BakeryController extends Agent implements Serializable{

	private AID[] bakeryAgents;
	private static final Logger log = LogManager.getLogger(BakeryController.class);
	
	private Set<Order> ordersToSend;
	
	public BakeryController(AID[] bakeryAgents) {
		this.bakeryAgents = bakeryAgents;
	}
	
	@Override
	protected void setup() {
		log.info("Hello! BakeryController-agent " + getAID().getName() + " is ready.");
		ordersToSend = Collections.synchronizedSet(new HashSet<>());
		addBehaviour(new OrderHandleServer());
	}
	
	@Override
	protected void takeDown() {
		log.info("BakeryController-agent " + getAID().getName() + " terminated.");
	}

	private class OrderHandleServer extends CyclicBehaviour implements Serializable{
		public void action() {
			ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));	
			Gson gson = new Gson();
			if (msg != null) {
				String jsonOrder = msg.getContent();
				ACLMessage replyCustomer = msg.createReply();
				Order newOrder = gson.fromJson(jsonOrder, Order.class);
				log.info("Received Order id: " + newOrder.getGuid() + " from Customer: "
						+ newOrder.getCustomerId());
				if (ordersToSend.add(newOrder)) {
					addBehaviour(new SendToBake(newOrder, replyCustomer));
				}
				
			}
			else {
				block();
			}
		}
	}
	
	private class SendToBake extends SequentialBehaviour {
		private Order newOrder;
		private String jsonOrder;
		private AID bestSeller;
		private Double bestPrice;
		private int repliesCnt = 0;
		private MessageTemplate mt;
		private ACLMessage replyCustomer;
		private String conversationId="order-for-bakery";

		public SendToBake(Order order, ACLMessage replyCustomer) {
			Gson gson = new Gson();
			this.newOrder=order;
			this.jsonOrder=gson.toJson(order);
			this.replyCustomer=replyCustomer;
			this.addSubBehaviour(new CheckOrderTime());
			this.addSubBehaviour(new SendRequest());
			this.addSubBehaviour(new GetLowestPrice());
			this.addSubBehaviour(new SendOrder());
			this.addSubBehaviour(new Confirmation());
		}
		private class CheckOrderTime extends Behaviour{

			private boolean timeToOrder = false;

			@Override
			public void action() {
				if(Time.getTime().getCurrentDate().toSeconds()<=newOrder.getDeliveryDate().toSeconds()
						&& Time.getTime().getCurrentDate().getDay()==newOrder.getDeliveryDate().getDay()){
					log.info("Time to send to Bakery! Current Date: "+Time.getTime().getCurrentDate().toString()+
							", Delivery Date is: "+newOrder.getDeliveryDate().toString());
					timeToOrder = true;
				}

			}

			@Override
			public boolean done() {
				return timeToOrder;
			}

			
		}
		private class SendRequest extends Behaviour{
			private boolean messageSent=false;
			@Override
			public void action() {
				ACLMessage aclmsg = new ACLMessage(ACLMessage.REQUEST);
				if(bakeryAgents != null) {
					for (AID bakeryAgent:bakeryAgents){
						aclmsg.addReceiver(bakeryAgent);
					}
			 		aclmsg.setContent(jsonOrder);
			 		aclmsg.setConversationId(conversationId);
			 		send(aclmsg);
			 		messageSent=true;
				}	
			}
			@Override
			public boolean done() {
				return messageSent;
			}
		}
		
		private class GetLowestPrice extends Behaviour{
			private boolean foundLowest=false;
			@Override
			public void action() {
				mt = MessageTemplate.MatchConversationId(conversationId);
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						Double price = Double.parseDouble(reply.getContent());
						if (bestSeller == null || price < bestPrice) {
							bestPrice = price;
							bestSeller = reply.getSender();
						}
						repliesCnt++;
					}
					else if (reply.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
						repliesCnt++;
					}
					
					if (repliesCnt >= bakeryAgents.length) {
						foundLowest = true;
					}
				}
				else {
					block();
				}
			}
			@Override
			public boolean done() {
				return foundLowest;
			}			
		}

		private class SendOrder extends Behaviour{
			private boolean orderSent=false;
			@Override
			public void action() {
				ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				order.addReceiver(bestSeller);
				order.setContent(jsonOrder);
				order.setConversationId(conversationId);
				order.setReplyWith("order"+System.currentTimeMillis());
				myAgent.send(order);
				orderSent=true;
				ordersToSend.remove(newOrder);
			}
			@Override
			public boolean done() {
				return orderSent;
			}
		}
		
		private class Confirmation extends Behaviour{
			private boolean confirmed=false;
			@Override
			public void action() {
				mt =MessageTemplate.MatchConversationId(conversationId);
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					if (reply.getPerformative() == ACLMessage.CONFIRM) {
						log.info(newOrder.getGuid()+" successfully sent to bakery "
									+reply.getSender().getName()+"@Price = "+bestPrice);
					}
					else {
						log.info("Order#"+newOrder.getGuid()+" failed. No matching bakeries found.");
						replyCustomer.setPerformative(ACLMessage.FAILURE);
						reply.setContent("not-available");
						send(replyCustomer);
					}
					confirmed = true;
				}
				else {
					block();
				}				
			}
			@Override
			public boolean done() {
				return confirmed;
			}			
		}
	}
}
