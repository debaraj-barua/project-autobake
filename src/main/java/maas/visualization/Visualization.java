package maas.visualization;

import javafx.application.Application;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maas.models.Node;
import maas.models.Link;
import maas.models.Location;
import maas.models.StreetNetwork;

public class Visualization extends Application {
	private static int width = 1024;
	private static int height = 768;
	private static List<Rectangle> customers = new ArrayList<>();
	private static List<Circle> bakeries = new ArrayList<>();
	private static List<Line> connections = new ArrayList<>();

	private static StreetNetwork streetNetwork = new StreetNetwork();

	public Visualization() {
		Map<String, Location> nodeIdToLocationMap = new HashMap<>();

		for (Node node : streetNetwork.getNodes()) {
			float x =  ((float) width / 2) + (node.getLocation().getX() * 40);
			float y = (float) (height / 2.5) + (node.getLocation().getY() * 40);

			Location location = new Location((float) (x + 7.5), (float) (y + 7.5));

			nodeIdToLocationMap.put(node.getGuid(), location);

			if (node.getType().equals("customer")) {
				Rectangle rect = new Rectangle(x, y, 15, 15);
				rect.setFill(Color.BLUE);

				Tooltip tooltip = new Tooltip(node.getName());
				bindTooltip(rect, tooltip);

				customers.add(rect);
			} else if (node.getType().equals("bakery")) {
				Circle circ = new Circle(x, y, 10);
				circ.setFill(Color.GREEN);

				Tooltip tooltip = new Tooltip(node.getName());
				bindTooltip(circ, tooltip);

				bakeries.add(circ);
			}
		}

		for (Link link : streetNetwork.getLinks()) {
			Location origin = nodeIdToLocationMap.get(link.getSource());
			Location destination = nodeIdToLocationMap.get(link.getTarget());

			Line line = new Line(origin.getX(), origin.getY(), destination.getX(), destination.getY());
			connections.add(line);
		}
	}

	public static void setStreetNetwork(StreetNetwork network) {
		streetNetwork = network;
	}

	// workaround to show tooltips instantly
	public static void bindTooltip(final javafx.scene.Node node, final Tooltip tooltip) {
		node.setOnMouseMoved(event -> tooltip.show(node, event.getScreenX(), event.getScreenY() + 15));
		node.setOnMouseExited(event -> tooltip.hide());
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Autobake");
		Label timeLabel=new TimeVisual();

		Pane pane = new Pane();
		pane.setPrefWidth(width);
		pane.setPrefHeight(height);
		pane.getChildren().addAll(connections);
		pane.getChildren().addAll(customers);
		pane.getChildren().addAll(bakeries);
		pane.getChildren().addAll(timeLabel);
		Scene scene = new Scene(pane);

		primaryStage.setScene(scene);
		primaryStage.show();
	}
}

