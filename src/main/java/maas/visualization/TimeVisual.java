package maas.visualization;



import javafx.animation.*;
import javafx.scene.control.Label;
import javafx.util.Duration;
import utils.Time;

public class TimeVisual extends Label {
	public TimeVisual() {
		bindToTime();
	}

	private void bindToTime() {
		Timeline timeline = new Timeline(
			    new KeyFrame(Duration.seconds(0),
			    event->setText(Time.getTime().getCurrentDate().toString())			      
			    ),
			    new KeyFrame(Duration.seconds(1))
			  );
			  timeline.setCycleCount(Animation.INDEFINITE);
			  timeline.play();
			 }

}
