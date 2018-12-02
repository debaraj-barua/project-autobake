package maas.agents;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import maas.messages.PreparationMessage;
import maas.models.Date;
import utils.Time;
import maas.models.Status;

@SuppressWarnings("serial")
public class DoughPrepTable extends Agent implements Serializable {

	private String guid;
	
	private Status status;

	private AID doughPrepTableController;

	public String getGuid() {
		return guid;
	}

	public void setDoughPrepTableController(AID doughPrepTableController) {
		this.doughPrepTableController = doughPrepTableController;
	}
	
	public Status getStatus() {
		return status;
	}

	@Override
	protected void setup() {
		Logger log = LogManager.getLogger(DoughPrepTable.class);
		log.info("Hello! DoughPrepTable-agent " + getAID().getName() + " is ready.");
		
		status = Status.WAITING;

		addBehaviour(new ProductHandleServer());
	}

	@Override
	protected void takeDown() {
		Logger log = LogManager.getLogger(DoughPrepTable.class);
		log.info("DoughPrepTable-agent " + getAID().getName() + " terminated.");
	}

	private class ProductHandleServer extends SequentialBehaviour {

		PreparationMessage preparationMessage;

		public ProductHandleServer() {
			this.addSubBehaviour(new ReceiveRequests());
			this.addSubBehaviour(new Prepare());
			this.addSubBehaviour(new SendBack());
		}

		@Override
		public int onEnd() {
			reset();
			myAgent.addBehaviour(this);
			return super.onEnd();
		}

		private class ReceiveRequests extends Behaviour {

			private boolean request = false;

			@Override
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
				ACLMessage msg = myAgent.receive(mt);

				if (msg != null) {
					Gson gson = new Gson();

					preparationMessage = gson.fromJson(msg.getContent(), PreparationMessage.class);

					request = true;
				} else {
					block();
				}
			}

			@Override
			public boolean done() {
				return request;
			}
		}

		private class Prepare extends Behaviour {
			private boolean preparationFinished;
			private Date finishDate;

			@Override
			public void onStart() {
				Date preparationTime = new Date(
						preparationMessage.getPreparingTime() * preparationMessage.getQuantity() * 60);

				this.finishDate = Time.getTime().getCurrentDate().add(preparationTime);
				this.preparationFinished = false;

				Logger log = LogManager.getLogger(DoughPrepTable.class);
				log.info("Received " + preparationMessage.getQuantity() + " " + preparationMessage.getProductId()
						+ ", prepare till " + finishDate + ".");
			}

			@Override
			public void action() {
				status = Status.PREPARING;
				Date currentDate = Time.getTime().getCurrentDate();

				if (currentDate.compareTo(finishDate) >= 0) {
					preparationFinished = true;
					status = Status.WAITING;
				} else {
					block(finishDate.subtract(currentDate).toMinutes() * Time.getMillisecondsForMin());
				}
			}

			@Override
			public boolean done() {
				return preparationFinished;
			}
		}

		private class SendBack extends OneShotBehaviour {
			@Override
			public void action() {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

				Gson gson = new Gson();
				msg.setContent(gson.toJson(preparationMessage));

				msg.addReceiver(doughPrepTableController);

				myAgent.send(msg);
			}
		}
	}

	@Override
	public String toString() {
		return "DoughPrepTable [guid=" + guid + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj){
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DoughPrepTable)) {
			return false;
		}
		return (this.guid==((DoughPrepTable) obj).getGuid());
	}
	
	
}
