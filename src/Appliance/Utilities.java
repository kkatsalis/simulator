/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Appliance;

import BL.DataCenter;
import BL.Request;
import Enumerators.EEventType;
import Enumerators.ETrafficEvent;
import Events.EventMonitor;
import Events.TrafficEvent;

/**
 *
 * @author kostas
 */
public class Utilities {
    
    EventMonitor eventMonitor;
    DataCenter _dataCenter;
    Appliance _hostingAppliance;
    
    public Utilities(Appliance hostingAppliance,DataCenter dataCenter) {
        
        this._hostingAppliance=hostingAppliance;
        this._dataCenter=dataCenter;
        this.eventMonitor = _dataCenter.getController().getEventMonitor();
    }

    public void scheduleServiceEvent(Request request) {
      	
        eventMonitor.NewEventEnterList(
	                 new TrafficEvent(
	                   EEventType.TrafficEvent.toString(),
	                   ETrafficEvent.ServiceInAppliance.toString(),
	                   request.getInNode_timeLeaveServer(),
                           request.getInNode_NodeiD(),
                           request.getRequestID(),
                           request.getServiceDomainID(),
                           request.getInNode_NodeiD()));
                
		}

    public void askRouterForFirstRequest(double time) {
     
        
        eventMonitor.AddInitialArrivalEvents(
	                 new TrafficEvent(
	                   EEventType.TrafficEvent.toString(),
	                   ETrafficEvent.AskServiceInRouter.toString(),
	                  time,
                          -1,
                           -1,
                           -1,
                           _hostingAppliance.getNodeID()));
                
    }
    public void askRouterForNewRequest() {
        
        eventMonitor.NewEventEnterList(
	                 new TrafficEvent(
	                   EEventType.TrafficEvent.toString(),
	                   ETrafficEvent.AskServiceInRouter.toString(),
	                   _dataCenter.getController().getTracker().getRunningEventTime(),
                           -1,
                           -1,
                           -1,
                           _hostingAppliance.getNodeID()));
                
    }

    public void removeRequestFromSystem(Request request) {
        
       _dataCenter.getRouters().get(0).getGenerator().removeRequestFromSystem(request);
    
    }

    public void addToWaitingList()
    {
      _dataCenter.getController().getApplianceNeedsRequest().add(_hostingAppliance.getNodeID());
    
    }
    
    public void removeFromWaitingList()
    {
        int size=_dataCenter.getAppliances().size();
        int index=-1;
        
        for (int i = 0; i < size; i++) {
            if(_dataCenter.getAppliances().get(i).getNodeID()==_hostingAppliance.getNodeID()){
                index=i;
                  _dataCenter.getController().getApplianceNeedsRequest().remove(index);
            }
    
        }
        
        
    }
    
    
    
}
    
    

