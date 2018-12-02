package maas.agents;

import java.io.Serializable;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import maas.messages.BakingMessage;
import maas.models.Date;
import utils.Time;

@SuppressWarnings("serial")
public class OvenController extends Agent implements Serializable {

	private AID[] ovenAgents;
	private AID bakeryAgent;

	private boolean[][] ovensFree;
	private LinkedList<BakingMessage> productQueue;

	public OvenController(AID[] ovenAgents, AID bakeryAgent) {
		this.ovenAgents = ovenAgents;
		this.bakeryAgent = bakeryAgent;

		this.ovensFree = new boolean[ovenAgents.length][4];

		for (int i = 0; i < ovenAgents.length; ++i) {
			for (int j = 0; j < 4; ++j) {
				this.ovensFree[i][j] = true;
			}
		}

		this.productQueue = new LinkedList<>();
	}

	@Override
	protected void setup() {
		Logger log = LogManager.getLogger(OvenController.class);
		log.info("Hello! OvenController-agent " + getAID().getName() + " is ready.");

		addBehaviour(new GetProductsReadyToBake());
		addBehaviour(new SendToOven());
		addBehaviour(new FinishedBaking());
	}

	@Override
	protected void takeDown() {
		Logger log = LogManager.getLogger(OvenController.class);
		log.info("OvenController-agent " + getAID().getName() + " terminated.");
	}

	private class GetProductsReadyToBake extends CyclicBehaviour implements Serializable {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				Logger log = LogManager.getLogger(OvenController.class);
				String jsonContent = msg.getContent();

				Gson gson = new Gson();

				BakingMessage content = gson.fromJson(jsonContent, BakingMessage.class);

				log.info("Received " + content.getQuantity() + " " + content.getProductId() + " from Bakery: "
						+ msg.getSender().getName());

				addProduct(content);
			} else {
				block();
			}
		}

		private void addProduct(BakingMessage bakingMessage) {
			int index = productQueue.indexOf(bakingMessage);
			if (index >= 0) {
				BakingMessage old = productQueue.get(index);
				BakingMessage newMessage = new BakingMessage(old.getProductId(),
						old.getQuantity() + bakingMessage.getQuantity(), old.getBakingTemperature(),
						old.getBakingTime(), old.getProductPerOven(), old.getBoxingTemperature());
				productQueue.set(index, newMessage);
			} else {
				productQueue.add(bakingMessage);
			}
		}
	}

	private class SendToOven extends CyclicBehaviour {

		private int step = 0;
		private int indexOfFreeOven;
		private int indexOfFreeSlot;

		public void action() {

			if (!productQueue.isEmpty()) {
				if (step == 0) {
					if (!findFreeOven()) {
						block(Time.getMillisecondsForMin());
					} else {
						step = 1;
					}

				} else if (step == 1) {
					Gson gson = new Gson();

					ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);

					msg.addReceiver(ovenAgents[indexOfFreeOven]);

					BakingMessage product = productQueue.remove();

					msg.setContent(gson.toJson(product));
					msg.setConversationId("slot" + indexOfFreeSlot);

					myAgent.send(msg);

					ovensFree[indexOfFreeOven][indexOfFreeSlot] = false;

					step = 0;
				}
			}
		}
		
		private boolean findFreeOven() {
			boolean foundFreeOven = false;
			
			for (int i = 0; i < ovensFree.length; ++i) {
				for (int j = 0; j < ovensFree[0].length; ++j) {
					if (ovensFree[i][j]) {
						indexOfFreeOven = i;
						indexOfFreeSlot = j;
						foundFreeOven = true;
						break;
					}
				}
				if(foundFreeOven) {
					break;
				}
			}
			
			return foundFreeOven;
		}
	}

	private class FinishedBaking extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msg = myAgent.receive(mt);

			if (msg != null) {
				AID ovenAID = msg.getSender();
				
				String conversationId = msg.getConversationId();
				
				int slot = Integer.parseInt(conversationId.substring(4, 5));

				for (int i = 0; i < ovenAgents.length; ++i) {
					if (ovenAgents[i].equals(ovenAID)) {
						ovensFree[i][slot] = true;
						break;
					}
				}
				
				Gson gson = new Gson();				

				myAgent.addBehaviour(new CoolProduct(gson.fromJson(msg.getContent(), BakingMessage.class)));

			} else {
				block();
			}
		}
	}
	
private class CoolProduct extends Behaviour {
		
		private BakingMessage product;
		private boolean coolingFinished;
		private Date finishDate;
		
		public CoolProduct(BakingMessage product) {
			this.product = product;
		}
		
		@Override
		public void onStart() {
			int difference = product.getBakingTemperature() - product.getBoxingTemperature();
			
			int seconds = difference / 2;
			
			int minutes = seconds / 60;
			
			if ((seconds % 60) > 0) {
				minutes++;
			}
			
			Date coolingTime = new Date(minutes * 60);
			
			this.finishDate = Time.getTime().getCurrentDate().add(coolingTime);
			this.coolingFinished = false;
			
			Logger log = LogManager.getLogger(OvenController.class);
			log.info("Received product " + product.getProductId() + ", cool for " + minutes + " minutes.");
		}
		
		@Override
		public void action() {
			Date currentDate = Time.getTime().getCurrentDate();
			
			if(currentDate.compareTo(finishDate) >= 0) {
				if(bakeryAgent != null) {
					ACLMessage informBakery = new ACLMessage(ACLMessage.INFORM);
					
					Gson gson = new Gson();
					
					informBakery.addReceiver(bakeryAgent);
					informBakery.setContent(gson.toJson(product));
					informBakery.setConversationId("finished-baking");
					
					myAgent.send(informBakery);
				}
				
				coolingFinished = true;
			} else {
				block(finishDate.subtract(currentDate).toMinutes() * Time.getMillisecondsForMin());
			}
		}
		
		@Override
		public boolean done() {
			return coolingFinished;
		}
	}
}
