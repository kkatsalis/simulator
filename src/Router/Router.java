package Router;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import BL.Configuration;
import BL.Controller;
import BL.DataCenter;
import BL.Request;
import BL.Sla;
import BL.Tracker;
import Enumerators.EEventType;
import Enumerators.EGeneratorType;
import Enumerators.EInternalArrivalEvent;
import Enumerators.EInternalServiceEvent;
import Enumerators.ENodeType;
import Enumerators.ETrafficEvent;
import Events.EventMonitor;
import Events.TrafficEvent;
import Network.Edge;
import Statistics.CpuStatistics;
import Statistics.Statistics;
import java.util.LinkedList;
import java.util.Queue;

public class Router  {

    String _nodeType=ENodeType.Router.toString();
     
    Configuration _config;
    DataCenter _dataCenter;
    Tracker _tracker;
    EventMonitor _eventMonitor;
    Controller _globalController;
    
    int _nodeID;
    boolean _isAlive=true;
    boolean _acceptExternalTraffic=true;
   
    HashMap _routerConfig;
    
    int _numberOfQueues;
    int _numberOfDomains;
    
    Queue<Request>[] _queue;
        
    RouterController _routerController;
    
    WebRequestGenerator _generator;
   
    RouterSchedulingService _schedulingService;
    RouterEnqueueService _enqueueingService;
        
     
    Statistics _statistics;
    
    Request _requestInService;
   
    
    public Router(int nodeID,HashMap nodeConfig,Tracker tracker,Configuration config,EventMonitor eventMonitor,DataCenter dataCenter,Controller controller) { 
        
        this._nodeID= nodeID;
        this._config=config;
        this._tracker=tracker;
        this._eventMonitor = eventMonitor;
        this._dataCenter=dataCenter;
        this._isAlive = true;
        this._routerConfig=nodeConfig;
        this._globalController=controller;
        this._generator=new WebRequestGenerator(config, nodeID,_tracker,_eventMonitor);
                
       _numberOfDomains=( Integer )_config.getNumberOfDomains();
        _numberOfQueues=( ( Integer )_config.getRouterConfiguration().get("QueuesNumber")).intValue();
        
        PrepareRouter();
        
    }

    private void PrepareRouter() {
   
          buildQueue();
 
         _routerController=new RouterController(this,true);
         _schedulingService=new RouterSchedulingService(this);
         _enqueueingService=new RouterEnqueueService(this);

         _statistics=new Statistics(_numberOfDomains,1,_numberOfQueues);
      
    	
	}

    // QUEUE Methods
    
    private void buildQueue() {
  
    _queue=new Queue[_numberOfQueues];
	         
    for (int i = 0; i < this._numberOfQueues; i++) {
        _queue[i]=new LinkedList<Request>();
        }
	        
    }
	
   
    
    
    // HandleEvent Methods
    
    public void RouterGetRequest(int domainID,int requestID)
    {
        Request request=_generator.returnRequest(domainID, requestID);
        
    	if (_routerController.getUtilities().findNumberOfRequestsInQueue(_queue)>=((Integer)_routerConfig.get("NodeMaxBuffering")).intValue())        {
            UpdateIncomingRequestOnArrival(request, EInternalArrivalEvent.DropRequest.toString());
            UpdateNodeStatisticsOnArrival(request, EInternalArrivalEvent.DropRequest.toString());
	}
	else	{
	
            EnQueueRequest(request);

            UpdateIncomingRequestOnArrival(request, EInternalArrivalEvent.EnqueueRequest.toString());
            UpdateNodeStatisticsOnArrival(request, EInternalArrivalEvent.EnqueueRequest.toString());

    	}
    
        if(_config.get_generateSaturatedConditions().equals(false)){
            if(_acceptExternalTraffic){
                _generator.CreateRequest(domainID,false);
            }
        }
            
        _routerController.getUtilities().sendToIdleAppliance(_dataCenter);
        
    }
       
     public int EnQueueRequest(Request request) {
	        
	int queueID = _enqueueingService.QueueToInsertRequest(request);
	            
	    _queue[queueID].add(request);
	         
            return queueID;
	}
  

   private void UpdateNodeStatisticsOnArrival(Request request, String internalArrivalEvent)
    {
        
        int levelID=0; // Not implemented yet
        int sdID=request.getServiceDomainID();
        int requestArrivals=_statistics.getRequestArrivals();
        int requestsInQueues=_statistics.getRequestsInQueues();
        int requestsDelayed=_statistics.getRequestsDelayed();
        _statistics.setRequestArrivals(requestArrivals+1);
        _statistics.getRequestArrivals_PL()[levelID]++;
        _statistics.getRequestArrivals_PSD()[sdID]++;
        _statistics.getRequestArrivals_PLPSD()[levelID][sdID]++; 
      
        if(internalArrivalEvent.contentEquals(EInternalArrivalEvent.DropRequest.toString()))
        {
            _statistics.setRequestsRejected( _statistics.getRequestsRejected()+1);
            _statistics.getRequestsRejected_PL()[levelID]++;
            _statistics.getRequestsRejected_PSD()[sdID]++;
            _statistics.getRequestsRejected_PLPSD()[levelID][sdID]++; 
        }
        if(internalArrivalEvent.contentEquals(EInternalArrivalEvent.EnqueueRequest.toString()))
        {
             _statistics.setRequestsInQueues(requestsInQueues+1);
            _statistics.getRequestsInQueues_PL()[levelID]++;
            _statistics.getRequestsInQueues_PSD()[sdID]++;
            _statistics.getRequestsInQueues_PLPSD()[levelID][sdID]++;
           
            _statistics.setRequestsDelayed(requestsDelayed+1);
            _statistics.getRequestsDelayed_PL()[levelID]++;
            _statistics.getRequestsDelayed_PSD()[sdID]++;
            _statistics.getRequestsDelayed_PLPSD()[levelID][sdID]++;
    
        }
       
    }

    private void UpdateIncomingRequestOnArrival(Request request, String internalArrivalEventType)
    {
    	request.setInNode_InternalArrivalEventType(internalArrivalEventType);
	request.setInNode_NodeiD(_nodeID);
	request.getPath().add(_nodeID);
	request.getTierPath().add(_nodeType);
	request.setInNode_ArrivalMoment(request.getNewNodeArrivalMoment());
            
	if(internalArrivalEventType.contentEquals(EInternalArrivalEvent.DropRequest.toString()))	{
           _generator.removeRequestFromSystem(request);
        }
    	
    }
        


    public void ServiceRequest(int applianceID)
    {
        int selectSDtoServe=-1;
		//Case A: Saturated Conditions - Router 
    	if(_config.get_generateSaturatedConditions().equals(true)){
           fillEmptyQueues(); 
        }
        
      
        selectSDtoServe = _schedulingService.SelectQueuetoServe();
         
        if(selectSDtoServe!=-1){
            _requestInService=_queue[selectSDtoServe].poll();

            if(_requestInService!=null){
                UpdateNodeStatisticsOnService(_requestInService);
                UpdateQueueStatisticsOnService(_requestInService);
                UpdateRequestToSend(_requestInService,applianceID);

                // Send Request
                SendRequest(_requestInService);
            }
        }
    }
       
    
    private void UpdateQueueStatisticsOnService(Request request) {
        
        int levelID=0;
        int sdID=request.getServiceDomainID();
        
        _statistics.setRequestsInQueues(_statistics.getRequestsInQueues()-1);
        _statistics.getRequestsInQueues_PL()[levelID]--;
        _statistics.getRequestsInQueues_PSD()[sdID]--;
        _statistics.getRequestsInQueues_PLPSD()[levelID][sdID]--;
        
    }   
    
    private void UpdateNodeStatisticsOnService(Request request)
    {
        int levelID=0;
        int sdID=request.getServiceDomainID();
        
        
        _statistics.setRequestsServed(_statistics.getRequestsServed()+1);
        _statistics.getRequestsServed_PL()[levelID]++;
        _statistics.getRequestsServed_PSD()[sdID]++;
        _statistics.getRequestsServed_PLPSD()[levelID][sdID]++;
        
      
        int requestsServed=_statistics.getRequestsServed();
     
        double avgDelayInQueue=_statistics.getAvgDelayInQueue();
        _statistics.setAvgDelayInQueue((avgDelayInQueue*(requestsServed-1)/requestsServed)+(1/requestsServed)*request.getInNode_delayInQueue());
        
        double avgDelayInService=_statistics.getAvgDelayInService();
        _statistics.setAvgDelayInService((avgDelayInService*(requestsServed-1)/requestsServed)+(1/requestsServed)*request.getInNode_ServiceDuration());
        
        double tempAvgDelayInService;
        double tempAvgDelayInQueue;
        
        int requestsServedPSD;
        
        tempAvgDelayInService=_statistics.getAvgDelayInService_PSD()[sdID];
        tempAvgDelayInQueue=_statistics.getAvgDelayInQueue_PSD()[sdID];
        
        requestsServedPSD=_statistics.getRequestsServed_PSD()[sdID];
        _statistics.getAvgDelayInService_PSD()[sdID]=(tempAvgDelayInService*(requestsServedPSD-1)/requestsServedPSD)+(request.getInNode_ServiceDuration()*1/requestsServedPSD);
        _statistics.getAvgDelayInQueue_PSD()[sdID]=(tempAvgDelayInQueue*(requestsServedPSD-1)/requestsServedPSD)+(request.getInNode_delayInQueue()*1/requestsServedPSD);
        

    }
   
    private void UpdateRequestToSend(Request request,int applianceID)
    {
        double serviceDuration=0.000000001;
    	request.setInNode_ServiceDuration(serviceDuration);
    	request.setInNode_timeEnterServer(_tracker.getRunningEventTime());
    	request.setInNode_delayInQueue(_tracker.getRunningEventTime()-request.getInNode_ArrivalMoment());
    	request.setInNode_timeLeaveServer(_tracker.getRunningEventTime()+serviceDuration);
    	request.setNewNodeID(applianceID);
    	request.setDelayInAllQueues(request.getDelayInAllQueues()+ request.getInNode_delayInQueue()); 
    	
        request.setDelayInAllQueues(request.getDelayInAllQueues()+request.getInNode_delayInQueue());
    	request.setDelayInAllService(request.getDelayInAllService()+ request.getInNode_ServiceDuration()); 
    	request.setResponseTime(request.getDelayInAllQueues()+request.getDelayInAllService()); 
    	
    }
   
       // </editor-fold>
   

    private void SendRequest(Request request)
    { 
        double delayInEdge=(double)(request.getSize()*1024)*8/this._dataCenter.getRouters().get(_nodeID).getRouterController().getEdgeList().get(0).getBandwidthBPS();
        double time=request.getInNode_timeLeaveServer()+delayInEdge;
        request.setDelayInAllEdges(request.getDelayInAllEdges()+delayInEdge);
        request.setNewNodeArrivalMoment(time);
      
        _routerController.getUtilities().scheduleSendEvent(_eventMonitor, request,time,request.getNewNodeID());
         
    }
    
    
    
    
    // <editor-fold desc="Properties region">
    // </editor-fold>
    public Configuration getConfig() {
        return _config;
    }

    public DataCenter getDataCenter() {
        return _dataCenter;
    }

    public Tracker getTracker() {
        return _tracker;
    }

    public EventMonitor getEventMonitor() {
        return _eventMonitor;
    }

    public Controller getController() {
        return _globalController;
    }

  
    public int getNodeID() {
        return _nodeID;
    }

    public boolean isIsAlive() {
        return _isAlive;
    }

    public boolean isAcceptExternalTraffic() {
        return _acceptExternalTraffic;
    }

    public String getNodeType() {
        return _nodeType;
    }

    public HashMap getNodeConfig() {
        return _routerConfig;
    }

  

    public RouterController getRouterController() {
        return _routerController;
    }

   
    public RouterSchedulingService getSchedulingService() {
        return _schedulingService;
    }

    public RouterEnqueueService getEnqueueingService() {
        return _enqueueingService;
    }

   

    public Statistics getStatistics() {
        return _statistics;
    }

    public int getNumberOfQueues() {
        return _numberOfQueues;
    }

    private void fillEmptyQueues() {
      
        for (int i = 0; i < _numberOfQueues; i++) {
            if(_queue[i].isEmpty())
                  _generator.CreateRequest(i,true);
        }
    }

    public Queue<Request>[] getQueue() {
        return _queue;
    }

    public WebRequestGenerator getGenerator() {
        return _generator;
    }

    
  
   

   
    
  
}

        
    

