package maas.messages;

import jade.util.leap.Serializable;

@SuppressWarnings("serial")
public class TruckStatusMessage implements Serializable{
	private String status;
	private String currentLocation;
	private int remainingCapacity;
	
	public TruckStatusMessage(String status, String currentLocation, int remainingCapacity) {
		this.status = status;
		this.currentLocation = currentLocation;
		this.remainingCapacity = remainingCapacity;
	}

	public String getStatus() {
		return status;
	}

	public String getCurrentLocation() {
		return currentLocation;
	}

	public int getRemainingCapacity() {
		return remainingCapacity;
	}

	@Override
	public String toString() {
		return "TruckStatusMessage [status=" + status + ", currentLocation=" + currentLocation + ", remainingCapacity="
				+ remainingCapacity + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currentLocation == null) ? 0 : currentLocation.hashCode());
		result = prime * result + remainingCapacity;
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		if (!(obj instanceof TruckStatusMessage)) {
			return false;
		}
		return (this.toString().equals(((TruckStatusMessage)obj).toString()));
	}
}
