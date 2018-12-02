package maas.agents;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import maas.messages.PreparationMessage;
import utils.Time;

@SuppressWarnings("serial")
public class DoughPrepTableController extends Agent implements Serializable {

	private AID[] doughPrepTableAgents;
	private AID bakeryAgent;

	private boolean[] doughPrepTableFree;
	private LinkedList<PreparationMessage> productQueue;

	public DoughPrepTableController(AID[] doughPrepTableAgents, AID bakeryAgent) {
		this.doughPrepTableAgents = doughPrepTableAgents;
		this.bakeryAgent = bakeryAgent;

		this.doughPrepTableFree = new boolean[doughPrepTableAgents.length];
		Arrays.fill(doughPrepTableFree, true);

		this.productQueue = new LinkedList<>();
	}

	@Override
	protected void setup() {
		Logger log = LogManager.getLogger(DoughPrepTableController.class);
		log.info("Hello! DoughPrepTableController-agent " + getAID().getName() + " is ready.");

		addBehaviour(new PreparationHandleServer());
		addBehaviour(new SendToPreparation());
		addBehaviour(new FinishedProducts());
	}

	@Override
	protected void takeDown() {
		Logger log = LogManager.getLogger(DoughPrepTableController.class);
		log.info("DoughPrepTableController-agent " + getAID().getName() + " terminated.");
	}

	private class PreparationHandleServer extends CyclicBehaviour implements Serializable {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				Logger log = LogManager.getLogger(DoughPrepTableController.class);
				String jsonContent = msg.getContent();

				Gson gson = new Gson();

				PreparationMessage content = gson.fromJson(jsonContent, PreparationMessage.class);

				log.info("Received " + content.getQuantity() + " " + content.getProductId() + " from Bakery: " + msg.getSender().getName());

				addProduct(content);
			} else {
				block();
			}
		}

		private void addProduct(PreparationMessage preparationMessage) {
			int index = productQueue.indexOf(preparationMessage);
			if(index >= 0) {
				PreparationMessage old = productQueue.get(index);
				PreparationMessage newMessage = new PreparationMessage(old.getProductId(), old.getQuantity() + preparationMessage.getQuantity(), old.getPreparingTime());
				productQueue.set(index, newMessage);
			} else {
				productQueue.add(preparationMessage);
			}
		}
	}

	private class SendToPreparation extends CyclicBehaviour {

		private int step = 0;
		private int indexOfFreeTable;

		public void action() {

			if (!productQueue.isEmpty()) {
				Logger log = LogManager.getLogger(DoughPrepTableController.class);

				if (step == 0) {
					boolean foundFreeTable = false;

					for (int i = 0; i < doughPrepTableFree.length; ++i) {
						if (doughPrepTableFree[i]) {
							indexOfFreeTable = i;
							foundFreeTable = true;
							break;
						}
					}

					if (!foundFreeTable) {
						block(Time.getMillisecondsForMin());
					} else {
						step = 1;
					}

				} else if (step == 1) {
					Gson gson = new Gson();

					ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);

					msg.addReceiver(doughPrepTableAgents[indexOfFreeTable]);

					PreparationMessage product = productQueue.remove();

					msg.setContent(gson.toJson(product));

					log.info("Sending product " + product.getProductId() + " to Preparation Table No. "
							+ (indexOfFreeTable + 1));

					myAgent.send(msg);

					doughPrepTableFree[indexOfFreeTable] = false;

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

				AID doughPrepTableAID = msg.getSender();

				for (int i = 0; i < doughPrepTableAgents.length; ++i) {
					if (doughPrepTableAgents[i].equals(doughPrepTableAID)) {
						doughPrepTableFree[i] = true;
						break;
					}
				}
				
				if(bakeryAgent != null) {
					ACLMessage informBakery = new ACLMessage(ACLMessage.INFORM);
					
					informBakery.addReceiver(bakeryAgent);
					informBakery.setContent(jsonContent);
					informBakery.setConversationId("finished-preparation");
					
					myAgent.send(informBakery);
				}

			} else {
				block();
			}
		}
	}

}
