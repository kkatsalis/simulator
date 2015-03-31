package Events;

import java.util.InputMismatchException;

public class Event {

    private String eventType;  // from enum EEvent
    private double timeOfOccurance;

    public Event()
    {       
        this.eventType = "";
        this.timeOfOccurance = -1;
    }

    public Event(String eventType, double timeOfOccurance) throws InputMismatchException
    {
    	this.eventType = eventType;
    	this.timeOfOccurance = timeOfOccurance;
    }

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public double getTimeOfOccurance() {
		return timeOfOccurance;
	}

	public void setTimeOfOccurance(double timeOfOccurance) {
		this.timeOfOccurance = timeOfOccurance;
	}
}
