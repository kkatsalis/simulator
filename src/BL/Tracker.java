package BL;

public class Tracker {

	double runningEventTime;     // The time instance on which an event occurs
        
	public void InitializationMethod(int numberOfLevels)
	{
		this.runningEventTime = 1;
    }



    public double getRunningEventTime() {
            return runningEventTime;
    }

    public void setRunningEventTime(double runningEventTime) {
            this.runningEventTime = runningEventTime;
    }


	
	
}
