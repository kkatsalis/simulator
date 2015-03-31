/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Events;

import BL.Configuration;
import BL.Controller;
import BL.DataCenter;
import BL.Tracker;
import Enumerators.*;


/**
 *
 * @author maevious
 */
public class EventHandler {
    
    DataCenter _dataCenter;
    EventMonitor _eventMonitor;
    Configuration _config;
    Controller _controller;
    int previousArrivalEvents;
    int requestEvents;
    Tracker _tracker;
    int appArrivals=0;
    
    public EventHandler(DataCenter dataCenter,EventMonitor eventMonitor, Configuration config,  Tracker tracker) {
        this._tracker=tracker;
        this._dataCenter = dataCenter;
        this._eventMonitor=eventMonitor;
        this._config=config;
        this._controller=_dataCenter.getController();
        
        this.requestEvents=0;
        this.previousArrivalEvents=0;
    }
    
    // <editor-fold desc="Event Handlers">

    public void EventHandingMethod(Event event)
    {        
         if (EEventType.TrafficEvent.toString().equals(event.getEventType())){
              TrafficEventHandler((TrafficEvent)event);
         }    

    }

  
   
   
    private void TrafficEventHandler(TrafficEvent trafficEvent) {

        //Update System Statistics
        _dataCenter.getController().RunControl();
        
//        System.out.println(
//                trafficEvent.getTimeOfOccurance()+" - "+
//                trafficEvent.getTrafficEventType()+" - "+
//                trafficEvent.getRequestID()+" - ("+
//                _dataCenter.getRouters().get(0).getStatistics().getRequestsInQueues_PSD()[0] +" - "+
//               _dataCenter.getRouters().get(0).getStatistics().getRequestsInQueues_PSD()[1] +")");
//        
//        
        
        if (ETrafficEvent.ArrivalInRouter.toString().equals(trafficEvent.getTrafficEventType())){
            ArrivalInRouterHandler(trafficEvent);
        }
        if (ETrafficEvent.AskServiceInRouter.toString().equals(trafficEvent.getTrafficEventType())){
            AskServiceInRouter(trafficEvent);
        }
        
        if (ETrafficEvent.ArrivalInAppliance.toString().equals(trafficEvent.getTrafficEventType())){
            ArrivalInApplianceHandler(trafficEvent);
        }
        if (ETrafficEvent.ServiceInAppliance.toString().equals(trafficEvent.getTrafficEventType())){
            ServiceInApplianceHandler(trafficEvent);
        }
     
    }

    private void ArrivalInRouterHandler(TrafficEvent trafficEvent)
    {
        int domainID=trafficEvent.getDomainID();
        int requestID=trafficEvent.getRequestID();
        
      
        
        for (int i = 0; i < _dataCenter.getRouters().size(); i++) {
           if(_dataCenter.getRouters().get(i).getNodeID()==trafficEvent.getNodeID()){
                    _dataCenter.getRouters().get(i).RouterGetRequest(domainID,requestID);
                }
            }
            
         
    }
    
     private void ArrivalInApplianceHandler(TrafficEvent trafficEvent)
    {
        int domainID=trafficEvent.getDomainID();
        int requestID=trafficEvent.getRequestID();
        int initiatorID=trafficEvent.getInitiatorID();
        
        if(appArrivals==0)
            _dataCenter.getController().getApplianceNeedsRequest().add(initiatorID);
        
        for (int i = 0; i < _dataCenter.getAppliances().size(); i++) {
           if(_dataCenter.getAppliances().get(i).getNodeID()==trafficEvent.getInitiatorID()){
                    _dataCenter.getAppliances().get(i).ApplianceGetRequest(domainID,requestID);
                }
            }
            
         appArrivals++;
    }

     
    private void AskServiceInRouter(TrafficEvent trafficEvent) {

        //int routerID=trafficEvent.getNodeID();
        
        int routerID=0;
        
         if(_dataCenter.getRouters().get(routerID).getStatistics().getRequestsInQueues()>0){
        
            for (int i = 0; i < _dataCenter.getRouters().size(); i++) {
                if(_dataCenter.getRouters().get(i).getNodeID()==routerID){
                    _dataCenter.getRouters().get(i).ServiceRequest(trafficEvent.getInitiatorID());
                }
            }
         }
       
    }
    
     private void ServiceInApplianceHandler(TrafficEvent trafficEvent) {

        int domainID=trafficEvent.getDomainID();
        int requestID=trafficEvent.getRequestID();
        
         for (int i = 0; i < _dataCenter.getRouters().size(); i++) {
           if(_dataCenter.getAppliances().get(i).getNodeID()==trafficEvent.getInitiatorID()){
                    _dataCenter.getAppliances().get(i).ServiceRequest(domainID,requestID);
                }
           }
    }
    
       

    }

    // </editor-fold>

  
   
