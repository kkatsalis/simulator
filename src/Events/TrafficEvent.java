package Events;

import BL.Request;



public class TrafficEvent extends Event{
	
	int nodeID;
        int requestID;
        int domainID;
        int initiatorID;
        
	String trafficEventType;  // select from enum ETrafficEvent
	
	public TrafficEvent(String eventType, String trafficEventType,double timeOfOccurance,int nodeID, int requestID,int domainID, int initiatorID ) {
		super(eventType, timeOfOccurance);
		
                this.nodeID=nodeID;
                this.requestID=requestID;
                this.domainID=domainID;
		this.trafficEventType = trafficEventType;
                this.initiatorID=initiatorID;

        } 
	public String getTrafficEventType() {
		return trafficEventType;
	}

	public void setTrafficEventType(String trafficEventType) {
		this.trafficEventType = trafficEventType;
	}

        public int getNodeID() {
            return nodeID;
        }

        public int getRequestID() {
            return requestID;
        }

        public int getDomainID() {
            return domainID;
        }

    public int getInitiatorID() {
        return initiatorID;
    }

  
  

    
        
        

  

       

    



	
	
	
	
}
