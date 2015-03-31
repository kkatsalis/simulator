package Appliance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import BL.Configuration;
import BL.Controller;
import BL.DataCenter;
import BL.Request;
import BL.Sla;
import BL.Tracker;
import Enumerators.EGeneratorType;
import Enumerators.EInternalArrivalEvent;
import Enumerators.ENodeType;
import Events.EventMonitor;
import Network.Edge;

import Statistics.CpuStatistics;
import Statistics.Statistics;
import java.util.Queue;

import Generators.TimeGenerator;
import Statistics.WindowCpuStatistics;
import java.util.Random;

public class Appliance  {

    String _nodeType=ENodeType.Appliance.toString();
    
    Configuration _config;
    DataCenter _dataCenter;
    Tracker _tracker;
    EventMonitor _eventMonitor;
    Controller _globalController;
    ApplianceController _applianceController;
    int _nodeID;
    
    HashMap _applianceConfig;
    
    Statistics _statistics;
    CpuStatistics _cpuStatistics;
    WindowCpuStatistics _windowCpuStatistics;
    
    Request _requestInService;

    int[] lastIncomingRequestID;
    Utilities _utililities;
    TimeGenerator _serviceTimeGenerator;
    double[] _timeOfOperationMeasurementPSD;
            
    public Appliance(int nodeID,HashMap applianceConfig,Tracker tracker,Configuration config,EventMonitor eventMonitor,DataCenter dataCenter,Controller controller) { 
        
        _config=config;
        _dataCenter=dataCenter;
        _tracker=tracker;
        _eventMonitor=eventMonitor;
        _globalController=controller;

        _nodeID=nodeID;
   
        _nodeType=ENodeType.Appliance.toString();
        _applianceConfig=applianceConfig;

        _statistics=new Statistics(_config.getNumberOfDomains(), 1,1 );
        _cpuStatistics=new CpuStatistics(_config.getNumberOfDomains(), 1);
        _windowCpuStatistics=new WindowCpuStatistics(_config.getNumberOfDomains(), _config.getWindowSize());
        
        _applianceController=new ApplianceController(this, true);
       
        lastIncomingRequestID=new int[_config.getNumberOfDomains()];
        _utililities=new Utilities(this,_dataCenter);
        this._serviceTimeGenerator=new TimeGenerator(_config,_nodeType);
        
        this._timeOfOperationMeasurementPSD=new double[_config.getNumberOfDomains()];
      
    }


    // <editor-fold desc="Properties region">
    // </editor-fold>
  
   
    public void ApplianceGetRequest(int domainID,int requestID)
    {  
        _utililities.removeFromWaitingList();
        
        updateIncomingRequest(domainID,requestID);
	updateNodeStatisticsOnArrival(domainID,requestID);
        
        Request request=_dataCenter.getRouters().get(0).getGenerator().returnRequest(domainID, requestID);
        _utililities.scheduleServiceEvent(request);
    }

   
   private void updateNodeStatisticsOnArrival(int domainID,int requestID)
    {
        
        int levelID=0;
        
        int sdID=domainID;
        int requestArrivals=_statistics.getRequestArrivals();

        lastIncomingRequestID[domainID]=requestID;
        
        _statistics.setRequestArrivals(requestArrivals+1);
        _statistics.getRequestArrivals_PL()[levelID]++;
        _statistics.getRequestArrivals_PSD()[sdID]++;
        _statistics.getRequestArrivals_PLPSD()[levelID][sdID]++; 
      
    }
    
    private void updateIncomingRequest(int domainID,int requestID) {
		
        Request request=_dataCenter.getRouters().get(0).getGenerator().returnRequest(domainID, requestID);
    
	request.setInNode_InternalArrivalEventType(EInternalArrivalEvent.DirectService.toString());
	request.setInNode_NodeiD(_nodeID);
	request.getPath().add(_nodeID);
	request.getTierPath().add(_nodeType);
	request.setInNode_ArrivalMoment(request.getNewNodeArrivalMoment());
        	
	double serviceTime=calculateServiceDuration(request);
        
	request.setInNode_ServiceDuration(serviceTime);
	request.setInNode_timeEnterServer(request.getInNode_ArrivalMoment());
	request.setInNode_timeLeaveServer(request.getInNode_ArrivalMoment()+serviceTime);
	request.setInNode_delayInQueue(0);
      
    }
    
    public void ServiceRequest(int domainID,int requestID)
    {        
        
        Request request=_dataCenter.getRouters().get(0).getGenerator().returnRequest(domainID,requestID);
         
        _utililities.addToWaitingList();
        _utililities.askRouterForNewRequest();
        updateRequestToSend(request);
        updateNodeStatisticsOnService(request);
        _utililities.removeRequestFromSystem(request);
     
        _applianceController.updateCreditsToCharge(request); //used by Credit Scheduler
    }
       
    
       
    
    private void updateNodeStatisticsOnService(Request request)
    {
        int levelID=0;
        int sdID=request.getServiceDomainID();
        
        
        _statistics.setRequestsServed(_statistics.getRequestsServed()+1);
        _statistics.getRequestsServed_PL()[levelID]++;
        _statistics.getRequestsServed_PSD()[sdID]++;
        _statistics.getRequestsServed_PLPSD()[levelID][sdID]++;
        
        double timeOfOperation=_cpuStatistics.getCpuTimeOfOperation();
       
        _cpuStatistics.setCpuTimeOfOperation(timeOfOperation+request.getInNode_ServiceDuration());
        _cpuStatistics.getCpuTimeOfOperation_PL()[levelID]+=request.getInNode_ServiceDuration();
        _cpuStatistics.getCpuTimeOfOperation_PSD()[sdID]+=request.getInNode_ServiceDuration();       
        _cpuStatistics.getCpuTimeOfOperation_PLPSD()[levelID][sdID]+=request.getInNode_ServiceDuration();   
  
        _cpuStatistics.setIdleTime(_tracker.getRunningEventTime()-_cpuStatistics.getCpuTimeOfOperation());
        
        int requestsServed=_statistics.getRequestsServed();
     
        double avgDelayInQueue=_statistics.getAvgDelayInQueue();
        _statistics.setAvgDelayInQueue((avgDelayInQueue*(requestsServed-1)/requestsServed)+(1/requestsServed)*request.getInNode_delayInQueue());
        
        double avgDelayInService=_statistics.getAvgDelayInService();
        _statistics.setAvgDelayInService((avgDelayInService*(requestsServed-1)/requestsServed)+(1/requestsServed)*request.getInNode_ServiceDuration());
        
        double cpuTime=_cpuStatistics.getCpuTimeOfOperation();
        double tempSLA;
        double tempAbsSdDeviation;
        
        
        for (int i = 0; i < _config.getNumberOfDomains(); i++) {
             // 1. Utilization
            _cpuStatistics.getCpuUtilization_PSD()[i]=(double)_cpuStatistics.getCpuTimeOfOperation_PSD()[i]/cpuTime;
            // 2. Deviation 
            tempSLA=_globalController.getGlobalSla().getCpuUtilizationSLA()[i];
             _cpuStatistics.getDeviationPSD()[i]=_cpuStatistics.getCpuUtilization_PSD()[i]-tempSLA;
            //3. Absolute Deviation
             tempAbsSdDeviation=Math.abs(_cpuStatistics.getDeviationPSD()[i]);
             _cpuStatistics.setAbsTotalDeviation(_cpuStatistics.getAbsTotalDeviation()+tempAbsSdDeviation);
           
        }
        
        double tempAvgDelayInService;
        double tempAvgDelayInQueue;
        
        int requestsServedPSD;
        
        tempAvgDelayInService=_statistics.getAvgDelayInService_PSD()[sdID];
        tempAvgDelayInQueue=_statistics.getAvgDelayInQueue_PSD()[sdID];
        
        requestsServedPSD=_statistics.getRequestsServed_PSD()[sdID];
        _statistics.getAvgDelayInService_PSD()[sdID]=(tempAvgDelayInService*(requestsServedPSD-1)/requestsServedPSD)+(request.getInNode_ServiceDuration()*1/requestsServedPSD);
        _statistics.getAvgDelayInQueue_PSD()[sdID]=(tempAvgDelayInQueue*(requestsServedPSD-1)/requestsServedPSD)+(request.getInNode_delayInQueue()*1/requestsServedPSD);

        // Update Window Statistics
        // addMeasurement(int signaledRound,int domainID, double measurement, double measurementTime){
        _windowCpuStatistics.addMeasurement(_globalController.getRunningRound(), sdID, request.getInNode_ServiceDuration(), _tracker.getRunningEventTime());
    }
   
    private void updateRequestToSend(Request request)
    {
    	request.setDelayInAllQueues(request.getDelayInAllQueues()+request.getInNode_delayInQueue());
    	request.setDelayInAllService(request.getDelayInAllService()+ request.getInNode_ServiceDuration()); 
    	request.setResponseTime(request.getDelayInAllQueues()+request.getDelayInAllService()); 
    	
    }
    
    
    
    
    
    

    public int getNodeID() {
        return _nodeID;
    }

    public ApplianceController getApplianceController() {
       return _applianceController;
    }

    public Controller getGlobalController() {
        return _globalController;
    }

    public Configuration getConfig() {
        return _config;
    }

    public Statistics getStatistics() {
        return _statistics;
    }

    public CpuStatistics getCpuStatistics() {
        return _cpuStatistics;
    }

    public HashMap getApplianceConfig() {
        return _applianceConfig;
    }

   private double calculateServiceDuration(Request request) {
       
       double tempServiceDuration=-1;
       int sdID=request.getServiceDomainID();
        
        switch (EGeneratorType.valueOf(this._serviceTimeGenerator.getGeneratorType()[sdID].toString())){
            case Exponential:   tempServiceDuration = _serviceTimeGenerator.getExponentialGenerator()[sdID].random();
                                break;
            
            case Pareto:  tempServiceDuration=_serviceTimeGenerator.getParetoGenerator()[sdID].random();
                                break;
            
            case Fixed: tempServiceDuration=_serviceTimeGenerator.getFixedDuration()[sdID];
                                break;
                
            case Random: tempServiceDuration = _serviceTimeGenerator.getRandomDuration(sdID);
                                break;
            
            case Harmonic: tempServiceDuration = _serviceTimeGenerator.getHarmonicDuration(sdID);
                                break;
           
        
            default:            
                break;
        }
        
       
        return tempServiceDuration;
   }

    public Utilities getUtililities() {
        return _utililities;
    }

    public WindowCpuStatistics getWindowCpuStatistics() {
        return _windowCpuStatistics;
    }
    
   

    
  
}

        
    

