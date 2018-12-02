package maas.agents;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import maas.messages.KneadingMessage;
import maas.models.Date;
import utils.Time;
import maas.models.Status;

@SuppressWarnings("serial")
public class KneadingMachine extends Agent implements Serializable {

	private String guid;
	
	private Status status;

	private AID kneadingMachineController;

	public void setKneadingMachineController(AID kneadingMachineConroller) {
		this.kneadingMachineController = kneadingMachineConroller;
	}

	public String getGuid() {
		return guid;
	}
	
	public Status getStatus() {
		return status;
	}

	@Override
	protected void setup() {
		Logger log = LogManager.getLogger(KneadingMachine.class);
		log.info("Hello! KneadingMachine-agent " + getAID().getName() + " is ready.");
		
		status = Status.WAITING;

		addBehaviour(new ProductHandleServer());
	}

	@Override
	protected void takeDown() {
		Logger log = LogManager.getLogger(KneadingMachine.class);
		log.info("KneadingMachine-agent " + getAID().getName() + " terminated.");
	}

	private class ProductHandleServer extends SequentialBehaviour {
		
		KneadingMessage kneadingMessage;

		public ProductHandleServer() {
			this.addSubBehaviour(new ReceiveRequests());
			this.addSubBehaviour(new Knead());
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
				
				if(msg != null) {
					Gson gson = new Gson();
					
					kneadingMessage = gson.fromJson(msg.getContent(), KneadingMessage.class);
					
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
		
		private class Knead extends Behaviour {
			private boolean kneadingFinished;
			private Date finishDate;
			
			@Override
			public void onStart() {
				Date kneadingTime = new Date(kneadingMessage.getKneadingTime() * 60);
				
				this.finishDate = Time.getTime().getCurrentDate().add(kneadingTime);
				this.kneadingFinished = false;
				
				Logger log = LogManager.getLogger(KneadingMachine.class);
				log.info("Received product " + kneadingMessage.getProductId() + ", knead till " + finishDate + ".");
			}
			
			@Override
			public void action() {
				status = Status.KNEADING;
				Date currentDate = Time.getTime().getCurrentDate();
				
				if(currentDate.compareTo(finishDate) >= 0) {
					kneadingFinished = true;
					status = Status.WAITING;
				} else {
					block(finishDate.subtract(currentDate).toMinutes() * Time.getMillisecondsForMin());
				}
			}
			
			@Override
			public boolean done() {
				return kneadingFinished;
			}
		}
		
		private class SendBack extends OneShotBehaviour {
			@Override
			public void action() {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

				Gson gson = new Gson();
				msg.setContent(gson.toJson(kneadingMessage));

				msg.addReceiver(kneadingMachineController);

				myAgent.send(msg);
			}
		}
	}

	@Override
	public String toString() {
		return "KneadingMachine [guid=" + guid + "]";
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
		if (!(obj instanceof KneadingMachine)) {
			return false;
		}
		return (this.guid==((KneadingMachine) obj).getGuid());
	}
}
