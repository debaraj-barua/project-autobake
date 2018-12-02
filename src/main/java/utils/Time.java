package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import maas.models.Date;

public class Time implements Runnable {
	
	private static Time	sysTime= new Time();
	private int currentSeconds;
	private static final double MAXTIME=3*(Math.pow(10,16));
	private int maxDay = 20;
	
	private static final int MILLISECONDSPERMIN = 10;
	private boolean isRunning=false;
	
	//Declaring private constructor for singleton
	private Time(){ 
		currentSeconds = 0;
		isRunning=true;
	}
	
	public static synchronized Time getTime() {
		return sysTime;		
	}
	
	public int getMaxDay( ) {
		return maxDay;
	}
	
	public void setMaxDay(int maxDay) {
		this.maxDay = maxDay;
	}
	
	public Date getCurrentDate() {
		return new Date(currentSeconds);
	}
	
	public boolean isTime(Date date){
		
		return getCurrentDate().equals(date);
	}
	
	public static long getMillisecondsForMin() {
		return MILLISECONDSPERMIN;
	}
	
	public Thread start() {
		Thread thread = new Thread(this);
		thread.start();
		return thread;
	}
	
	@Override
	public void run() {
		Logger log=LogManager.getLogger(Time.class); 
		while(currentSeconds<MAXTIME){
			//Maximum number of days=20
			if(currentSeconds / (24 * 60 * 60)>=maxDay){
				isRunning=false;
				break;				
			}
			try {
				Thread.sleep(MILLISECONDSPERMIN);
			} catch (InterruptedException e) {
				log.error("Time interrupted! :: "+ e);
				Thread.currentThread().interrupt();
			}
			currentSeconds+=60;
		}
		
		log.info("Time thread ended at time: ",getCurrentDate().toString()+" current seconds: "+currentSeconds);
	}

	public boolean isRunning() {
		return isRunning;
	}
	
}
