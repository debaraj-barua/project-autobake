package maas.models;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Date implements Serializable, Comparable<Date> {

	private int day;
	private int hour;
	private int min;

	public int getDay() {
		return day;
	}

	public int getHour() {
		return hour;
	}

	public int getMin() {
		return min;
	}

	public Date(int day, int hour) {
		super();
		this.day = day;
		this.hour = hour;
		this.min = 0;
	}
	public Date(int day, int hour, int min) {
		super();
		this.day = day;
		this.hour = hour;
		this.min = min;
	}
	
	public Date(int seconds) {
		super();
		this.min = seconds / 60;
		this.hour = this.min / 60;
		this.min = this.min % 60;
		this.day = this.hour / 24;
		this.hour = this.hour % 24;
		
	}

	public Date() {
		super();
		this.day = 0;
		this.hour = 0;
		this.min = 0;
	}

	public int toSeconds() {
		return (day * 24 * 60 * 60) + (hour * 60 * 60) +
				(min * 60);
	}

	@Override
	public String toString() {
		return "Date [day=" + day + ", hour=" + hour+ ", min=" + min + "]";
	}
	
	public int toHours() {
		return (24 * day) + hour;
	}
	
	public int toMinutes() {
		return (24 * 60 * day) + (60 * hour) + min;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		result = prime * result + hour;
		result = prime * result + min;
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
		if (!(obj instanceof Date)) {
			return false;
		}
		Date other = (Date) obj;
		return (day == other.getDay() && 
				hour == other.getHour() &&
				min == other.getMin());
	}

	@Override
	public int compareTo(Date date) {
		int compareDay = ((Integer) (this.day)).compareTo((Integer) (date.getDay()));

		if (compareDay == 0) {
			int compareHour = ((Integer) (this.hour)).compareTo((Integer) (date.getHour()));
			if (compareHour == 0) {
				return ((Integer)(this.min)).compareTo((Integer)(date.getMin()));
			}
			else{
				return compareHour;
			}
		}
		else {
			return compareDay;
		}
	}
	
	public Date add(Date date) {
		return new Date(this.toSeconds()
				+ date.toSeconds());
	}

	public Date subtract(Date date) {
		
		int newSeconds=this.toSeconds()-date.toSeconds();
		if(newSeconds < 0) {
			throw new IllegalArgumentException();
		}
		return new Date(newSeconds);
	}

}
