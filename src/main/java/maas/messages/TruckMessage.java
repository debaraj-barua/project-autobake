package maas.messages;

import java.util.List;

import jade.util.leap.Serializable;
import maas.models.Box;
import maas.models.Order;

@SuppressWarnings("serial")
public class TruckMessage  implements Serializable{
	private Order order;
	private List<Box> boxes;
	
	public TruckMessage(Order order, List<Box> boxes) {
		this.order = order;
		this.boxes = boxes;
	}

	public Order getOrder() {
		return order;
	}

	public List<Box> getBoxes() {
		return boxes;
	}

	@Override
	public String toString() {
		return "TruckMessage [order=" + order + ", boxes=" + boxes + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((boxes == null) ? 0 : boxes.hashCode());
		result = prime * result + ((order == null) ? 0 : order.hashCode());
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
		if (!(obj instanceof TruckMessage)) {
			return false;
		}
		return (this.toString().equals(((TruckMessage)obj).toString()));
	}
}
