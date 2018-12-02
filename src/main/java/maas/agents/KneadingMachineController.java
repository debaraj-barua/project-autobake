package maas.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import maas.messages.KneadingMessage;
import maas.models.Date;
import utils.Time;

@SuppressWarnings("serial")
public class KneadingMachineController extends Agent implements Serializable {

	private AID[] kneadingMachineAgents;
	private AID bakeryAgent;

	private boolean[] kneadingMachineFree;
	private Set<String> productInProcess;
	private Queue<KneadingMessage> productQueue;

	public KneadingMachineController(AID[] kneadingMachineAgents, AID bakeryAgent) {
		this.kneadingMachineAgents = kneadingMachineAgents;
		this.bakeryAgent = bakeryAgent;
		this.kneadingMachineFree = new boolean[kneadingMachineAgents.length];
		Arrays.fill(kneadingMachineFree, true);
		
		this.productInProcess = new HashSet<>();
		this.productQueue = new LinkedList<>();
	}

	@Override
	protected void setup() {
		Logger log = LogManager.getLogger(KneadingMachineController.class);
		log.info("Hello! KneadingMachineController-agent " + getAID().getName() + " is ready.");

		addBehaviour(new KneadingHandleServer());
		addBehaviour(new SendToKnead());
		addBehaviour(new FinishedProducts());
	}

	@Override
	protected void takeDown() {
		Logger log = LogManager.getLogger(KneadingMachineController.class);
		log.info("KneadingMachineController-agent " + getAID().getName() + " terminated.");
	}

	private class KneadingHandleServer extends CyclicBehaviour implements Serializable {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				Logger log = LogManager.getLogger(KneadingMachineController.class);
				String jsonContent = msg.getContent();
				
				Gson gson = new Gson();
				
				KneadingMessage content = gson.fromJson(jsonContent, KneadingMessage.class);
				
				log.info("\nReceived Order id: " + content.getProductId() + " from Bakery: " + msg.getSender());

				addProduct(content);
			} else {
				block();
			}
		}
		
		private void addProduct(KneadingMessage kneadingMessage) {
			if(!productInProcess.contains(kneadingMessage.getProductId()) && !productQueue.contains(kneadingMessage)) {
				productQueue.add(kneadingMessage);
			}
		}
	}

	private class SendToKnead extends CyclicBehaviour {

		private int step = 0;
		private int indexOfFreeMachine;

		public void action() {

			if (!productQueue.isEmpty()) {
				Logger log = LogManager.getLogger(KneadingMachineController.class);
				
				if(step == 0) {
					boolean foundFreeMachine = false;
					
					for(int i = 0; i < kneadingMachineFree.length; ++i) {
						if(kneadingMachineFree[i]) {
							indexOfFreeMachine = i;
							foundFreeMachine = true;
							break;
						}
					}
					
					if(!foundFreeMachine) {
						block(Time.getMillisecondsForMin());
					} else {
						step = 1;
					}

					

				} else if(step == 1) {
					Gson gson = new Gson();
					
					ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
					
					msg.addReceiver(kneadingMachineAgents[indexOfFreeMachine]);
					
					KneadingMessage product = productQueue.remove();
					
					msg.setContent(gson.toJson(product));
					
					log.info("Sending product " + product.getProductId() + " to Kneading Machine No. " + (indexOfFreeMachine + 1));
					
					myAgent.send(msg);
					
					kneadingMachineFree[indexOfFreeMachine] = false;
					productInProcess.add(product.getProductId());
					
					step = 0;
				}
			}
		}
	}

	private class FinishedProducts extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msg = myAgent.receive(mt);

			if (msg != null) {
				String jsonContent = msg.getContent();
				
				Gson gson = new Gson();
				
				KneadingMessage content = gson.fromJson(jsonContent, KneadingMessage.class);

				AID kneadingMachineAID = msg.getSender();
				
				for(int i = 0; i < kneadingMachineAgents.length; ++i) {
					if(kneadingMachineAgents[i].equals(kneadingMachineAID)) {
						kneadingMachineFree[i] = true;
						break;
					}
				}
				
				myAgent.addBehaviour(new RestProduct(content));

			} else {
				block();
			}
		}
	}
	
	private class RestProduct extends Behaviour {
		
		private KneadingMessage product;
		private boolean restingFinished;
		private Date finishDate;
		
		public RestProduct(KneadingMessage product) {
			this.product = product;
		}
		
		@Override
		public void onStart() {
			Date restingTime = new Date(product.getRestingTime() * 60);
			
			this.finishDate = Time.getTime().getCurrentDate().add(restingTime);
			this.restingFinished = false;
			
			Logger log = LogManager.getLogger(KneadingMachineController.class);
			log.info("Received product " + product.getProductId() + ", rest till " + finishDate + ".");
		}
		
		@Override
		public void action() {
			Date currentDate = Time.getTime().getCurrentDate();
			
			if(currentDate.compareTo(finishDate) >= 0) {
				Logger log = LogManager.getLogger(KneadingMachineController.class);
				log.info("Resting of " + product.getProductId() + " finished. Current time: " + currentDate + " Comparison: " + currentDate.compareTo(finishDate));
				
				productInProcess.remove(product.getProductId());
				
				if(bakeryAgent != null) {
					ACLMessage informBakery = new ACLMessage(ACLMessage.INFORM);
					
					informBakery.addReceiver(bakeryAgent);
					informBakery.setContent(product.getProductId());
					informBakery.setConversationId("finished-kneading");
					
					myAgent.send(informBakery);
				}
				
				restingFinished = true;
			} else {
				block(finishDate.subtract(currentDate).toMinutes() * Time.getMillisecondsForMin());
			}
		}
		
		@Override
		public boolean done() {
			return restingFinished;
		}
	}
}
