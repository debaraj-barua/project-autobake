package maas.agents;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import maas.messages.BakingMessage;
import maas.models.Date;
import utils.Time;
import maas.models.Status;

@SuppressWarnings("serial")
public class Oven extends Agent implements Serializable {

	private String guid;
	private int cooling_rate;
	private int heating_rate;
	private int currentTemperature;
	private Status status;

	private AID ovenControllerAgent;

	public void setOvenControllerAgent(AID ovenControllerAgent) {
		this.ovenControllerAgent = ovenControllerAgent;
	}

	public String getGuid() {
		return guid;
	}

	public int getCoolingRate() {
		return cooling_rate;
	}

	public int getHeatingRate() {
		return heating_rate;
	}
	
	public Status getStatus() {
		return status;
	}
	public int getcurrentTemperature(){
		return currentTemperature;
	}

	@Override
	protected void setup() {
		Logger log = LogManager.getLogger(Oven.class);
		log.info("Hello! Oven-agent " + getAID().getName() + " is ready.");
		
		status = Status.WAITING;

		addBehaviour(new BakingProcess("slot0"));
		addBehaviour(new BakingProcess("slot1"));
		addBehaviour(new BakingProcess("slot2"));
		addBehaviour(new BakingProcess("slot3"));
	}

	@Override
	protected void takeDown() {
		Logger log = LogManager.getLogger(Oven.class);
		log.info("Oven-agent " + getAID().getName() + " terminated.");
		status = Status.FINISHED;
	}

	private class BakingProcess extends SequentialBehaviour {

		BakingMessage bakingMessage;
		String conversationId;
		

		public BakingProcess(String slot) {
			this.addSubBehaviour(new ReceiveRequests());
			this.addSubBehaviour(new ChangeTemperature());
			this.addSubBehaviour(new Bake());
			this.addSubBehaviour(new SendBack());
			conversationId = slot;
			currentTemperature = 0;
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
				MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
						MessageTemplate.MatchConversationId(conversationId));
				ACLMessage msg = myAgent.receive(mt);

				if (msg != null) {
					Gson gson = new Gson();

					bakingMessage = gson.fromJson(msg.getContent(), BakingMessage.class);

					request = true;

					LogManager.getLogger(Oven.class).info("Received " + bakingMessage.getQuantity() + " "
							+ bakingMessage.getProductId() + " to bake in oven " + conversationId + ".");
				} else {
					block();
				}
			}

			@Override
			public boolean done() {
				return request;
			}
		}

		private class ChangeTemperature extends Behaviour {

			private boolean temperatureAdjustmentFinished;
			private Date finishDate;

			@Override
			public void onStart() {
				int seconds = 0;

				if (currentTemperature > bakingMessage.getBakingTemperature()) {
					int difference = currentTemperature - bakingMessage.getBakingTemperature();

					seconds = difference / cooling_rate;

					if ((difference % cooling_rate) > 0) {
						seconds++;
					}

				} else if (currentTemperature < bakingMessage.getBakingTemperature()) {
					int difference = bakingMessage.getBakingTemperature() - currentTemperature;

					seconds = difference / heating_rate;

					if ((difference % heating_rate) > 0) {
						seconds++;
					}
				}

				if (seconds > 0) {
					int minutes = seconds / 60;

					if ((seconds % 60) > 0) {
						minutes++;
					}

					Date temperatureAdjustmentTime = new Date(minutes * 60);

					this.finishDate = Time.getTime().getCurrentDate().add(temperatureAdjustmentTime);
					this.temperatureAdjustmentFinished = false;

					LogManager.getLogger(Oven.class)
							.info("Adjust temperature of oven " + conversationId + " from " + currentTemperature
									+ " to " + bakingMessage.getBakingTemperature() + " takes " + minutes
									+ " minutes.");
				} else {
					this.finishDate = null;
					this.temperatureAdjustmentFinished = false;
				}
			}

			@Override
			public void action() {
				if (finishDate != null) {
					status = Status.CHANGINGTEMPERATURE;
					Date currentDate = Time.getTime().getCurrentDate();

					if (currentDate.compareTo(finishDate) >= 0) {
						temperatureAdjustmentFinished = true;
						currentTemperature = bakingMessage.getBakingTemperature();
						status = Status.BAKING;
					} else {
						block(finishDate.subtract(currentDate).toMinutes() * Time.getMillisecondsForMin());
					}
				} else {
					temperatureAdjustmentFinished = true;
					status = Status.BAKING;
				}
			}

			@Override
			public boolean done() {
				return temperatureAdjustmentFinished;
			}
		}

		private class Bake extends Behaviour {
			private boolean bakingFinished;
			private Date finishDate;

			@Override
			public void onStart() {
				int numOfBakingProcesses = bakingMessage.getQuantity() / bakingMessage.getProductPerOven();

				if ((bakingMessage.getQuantity() % bakingMessage.getProductPerOven()) > 0) {
					numOfBakingProcesses++;
				}

				Date bakingTime = new Date(bakingMessage.getBakingTime() * numOfBakingProcesses * 60);

				this.finishDate = Time.getTime().getCurrentDate().add(bakingTime);
				this.bakingFinished = false;

				Logger log = LogManager.getLogger(Oven.class);
				log.info("Bake " + bakingMessage.getQuantity() + " " + bakingMessage.getProductId() + " in oven "
						+ conversationId + " till " + finishDate + ".");
			}

			@Override
			public void action() {
				Date currentDate = Time.getTime().getCurrentDate();

				if (currentDate.compareTo(finishDate) >= 0) {
					bakingFinished = true;
					status = Status.WAITING;
				} else {
					block(finishDate.subtract(currentDate).toMinutes() * Time.getMillisecondsForMin());
				}
			}

			@Override
			public boolean done() {
				return bakingFinished;
			}
		}

		private class SendBack extends OneShotBehaviour {
			@Override
			public void action() {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

				Gson gson = new Gson();
				msg.setContent(gson.toJson(bakingMessage));

				msg.addReceiver(ovenControllerAgent);

				msg.setConversationId(conversationId);

				myAgent.send(msg);
			}
		}
	}

	@Override
	public String toString() {
		return "Oven [guid=" + guid + ", cooling_rate=" + cooling_rate + ", heating_rate=" + heating_rate + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cooling_rate;
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		result = prime * result + heating_rate;
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
		if (!(obj instanceof Oven)) {
			return false;
		}
		return (this.toString().equals(((Oven) obj).toString()));
	}

}
