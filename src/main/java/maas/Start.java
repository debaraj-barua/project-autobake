package maas;

import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;


import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import utils.Time;
import maas.models.*;
import maas.visualization.*;
import maas.agents.*;
import maas.gui.*;

public class Start {
	public static void main(String[] args) {
		
		Configurator.setRootLevel(Level.ALL);

		Runtime runtime = Runtime.instance();

		runtime.setCloseVM(true);

		Profile profile = new ProfileImpl(null, 1200, null);

		AgentContainer container = runtime.createMainContainer(profile);

		Reader reader = new Reader();

		reader.readJsonFile("src/main/config/random-scenario.json");
		
		setupAgents(container, reader);
		
		Time.getTime().setMaxDay(reader.getMeta().getDurationDays() + 2);
	
		Time.getTime().start();

		AutobakeUI gui= new AutobakeUI(reader);
		
		gui.start();

		Visualization.setStreetNetwork(reader.getStreetNetwork());

		killAgents(container);
	}

	private static void setupAgents(AgentContainer container, Reader reader) {
		Bakery[] bakeries = reader.getBakeries();

		AID[] bakeryAIDs = new AID[bakeries.length];

		int i = 0;

		for (Bakery bakery : bakeries) {

			setupBakeryAgent(container, bakery);

			bakeryAIDs[i] = bakery.getAID();
			
			i++;
		}

		BakeryController bakeryController = new BakeryController(bakeryAIDs);

		acceptNewAgent(container, bakeryController, "BakeryController");

		Customer[] customers = reader.getCustomers();

		Order[] orders = reader.getOrders();

		putOrdersToCustomers(customers, orders);
		putCustomersToTrucks(customers, bakeries);

		for (Customer customer : customers) {
			customer.setBakeryController(bakeryController.getAID());
			acceptNewAgent(container, customer, customer.getCustomerName());
		}
	}
	
	private static void setupBakeryAgent(AgentContainer container, Bakery bakery) {
		String bakeryName = bakery.getBakeryName();

		// create and start bakery agent
		acceptNewAgent(container, bakery, bakeryName);

		int j = 0;

		// create agents of the bakery
		List<Oven> ovens = bakery.getOvens();
		
		AID[] ovenAIDs = new AID[ovens.size()];

		for (Oven oven : ovens) {
			acceptNewAgent(container, oven, bakeryName + ":" + oven.getGuid());
			ovenAIDs[j] = oven.getAID();
			j++;
		}
		
		OvenController ovenController = new OvenController(ovenAIDs, bakery.getAID());

		acceptNewAgent(container, ovenController, bakeryName + ":" + "OvenController");
		
		for(Oven oven : ovens) {
			oven.setOvenControllerAgent(ovenController.getAID());
		}
		
		bakery.setOvenControllerAgent(ovenController.getAID());

		List<KneadingMachine> kneadingMachines = bakery.getKneadingMachines();

		AID[] kneadingMachineAIDs = new AID[kneadingMachines.size()];

		j = 0;

		for (KneadingMachine kneadingMachine : kneadingMachines) {
			acceptNewAgent(container, kneadingMachine, bakeryName + ":" + kneadingMachine.getGuid());
			kneadingMachineAIDs[j] = kneadingMachine.getAID();
			j++;
		}

		KneadingMachineController kneadingMachineController = new KneadingMachineController(kneadingMachineAIDs,
				bakery.getAID());

		acceptNewAgent(container, kneadingMachineController, bakeryName + ":" + "KneadingMachineController");

		for (KneadingMachine kneadingMachine : kneadingMachines) {
			kneadingMachine.setKneadingMachineController(kneadingMachineController.getAID());
		}

		bakery.setKneadingMachineControllerAgent(kneadingMachineController.getAID());
		List<DoughPrepTable> doughPrepTables = bakery.getDoughPrepTables();
		AID[] doughPrepTableAIDs = new AID[doughPrepTables.size()];

		j = 0;

		for (DoughPrepTable doughPrepTable : doughPrepTables) {
			acceptNewAgent(container, doughPrepTable, bakeryName + ":" + doughPrepTable.getGuid());
			doughPrepTableAIDs[j] = doughPrepTable.getAID();
			j++;
		}

		DoughPrepTableController doughPrepTableController = new DoughPrepTableController(doughPrepTableAIDs,
				bakery.getAID());

		acceptNewAgent(container, doughPrepTableController, bakeryName + ":" + "DoughPrepTableController");

		for (DoughPrepTable doughPrepTable : doughPrepTables) {
			doughPrepTable.setDoughPrepTableController(doughPrepTableController.getAID());
		}

		bakery.setDoughPrepTableControllerAgent(doughPrepTableController.getAID());

		List<Truck> trucks = bakery.getTrucks();

		AID[] truckAIDs = new AID[trucks.size()];
		int[] truckCapacity = new int[trucks.size()];

		j = 0;

		for (Truck truck : trucks) {
			acceptNewAgent(container, truck, bakeryName + ":" + truck.getGuid());
			truckAIDs[j] = truck.getAID();
			truckCapacity[j] = truck.getLoadCapacity();
			j++;
		}

		Delivery delivery = new Delivery(truckAIDs, bakery.getProducts(),truckCapacity);

		acceptNewAgent(container, delivery, bakeryName + ":" + "Delivery");

		bakery.setDeliveryAgent(delivery.getAID());
		
		for (Truck truck: trucks){
			truck.setDeliveryAgent(delivery.getAID());
		}
	}

	private static void acceptNewAgent(AgentContainer container, Agent agent, String name) {
		Logger log = LogManager.getLogger(Start.class);

		try {
			container.acceptNewAgent(name, agent).start();
		} catch (StaleProxyException e) {
			log.fatal("Wrapper is outdated when creating the new Agent", e);
		}
	}

	private static void putOrdersToCustomers(Customer[] customers, Order[] orders) {
		Map<String, List<Order>> orderToCustomerMap = new HashMap<>(customers.length);

		for (Customer customer : customers) {
			orderToCustomerMap.put(customer.getGuid(), new ArrayList<Order>());
		}

		for (Order order : orders) {
			List<Order> orderIdList = orderToCustomerMap.get(order.getCustomerId());
			orderIdList.add(order);
		}

		for (Customer customer : customers) {
			customer.setOrders(orderToCustomerMap.get(customer.getGuid()));
		}
	}

	private static void putCustomersToTrucks(Customer[] customers, Bakery[] bakeries) {
		for (Bakery bakery : bakeries) {
			List<Truck> trucks = bakery.getTrucks();
			for (Truck truck : trucks) {
				truck.setCustomers(Arrays.asList(customers));
			}
		}
	}

	public static void killAgents(AgentContainer container) {
		Logger log = LogManager.getLogger(Start.class);
		try {
			while (Time.getTime().isRunning()) {
				log.info(Time.getTime().getCurrentDate());
				Thread.sleep(1000);
			}
			container.kill();
		} catch (StaleProxyException e) {
			log.error(e);
		} catch (InterruptedException e) {
			log.error("Closing containers ", e);
			Thread.currentThread().interrupt();
		}
	}
}
