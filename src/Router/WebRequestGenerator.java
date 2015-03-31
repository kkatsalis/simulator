/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Router;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import Enumerators.*;
import Events.TrafficEvent;
import BL.Configuration;
import BL.Request;
import BL.Tracker;
import Events.EventMonitor;
import jsc.distributions.Exponential;
import jsc.distributions.Pareto;


/**
 *
 * @author Kostas Katsalis
 */
public class WebRequestGenerator  {
  
    static int uniqueID=0;
     
    int _nodeID;
    int _numberOfDomains;
    
    Configuration _config;
    List<Request>[] _request;
    Tracker _tracker;
    EventMonitor _eventMonitor;
    
    // How to create requests per Service Domain
    String[] _generatorType; 
    Exponential[] _exponentialArrivalGenerator;
    Pareto[] _paretoArrivalGenerator;
    
    Random rand;
    Random _arrivalTimeRandom;
    
    double maxPareto=-5.0;
    double maxExpo=-5.0;
    double minPareto=5000.0;
    double minExpo=5000.0;
    
    //Burst 
    Exponential[] _onPeriodExponentialGenerator;
    Exponential[] _offPeriodExponentialGenerator;
    Pareto[] _onPeriodParetoGenerator;
    Pareto[] _offPeriodParetoGenerator;
    String[] _onGeneratorType;
    String[] _offGeneratorType;
    double[] _onPeriodDuration;
    double[] _offPeriodDuration;
   
    Traces traces;
    double[] interarrivalTime;
    int traceIndex[];
    int requestsSend[];
    
    public WebRequestGenerator(Configuration config,int attachedNodeID,Tracker tracker,EventMonitor eventMonitor) {
       
        uniqueID++;
        
        this._config=config;
        this._numberOfDomains=_config.getNumberOfDomains();
        this._nodeID=attachedNodeID;
        this._tracker=tracker;
        this._eventMonitor=eventMonitor;
        this._request=new ArrayList[_config.getNumberOfDomains()];
        
        this.rand=new Random();
        _arrivalTimeRandom=new Random();
        
        for (int i = 0; i < _numberOfDomains; i++) {
            _request[i]=new ArrayList<Request>();
        }
        
        _onPeriodDuration=new double[_numberOfDomains];
        _offPeriodDuration=new double[_numberOfDomains];
         
        buildArrivalGenerators();
        BuildBurstGenerators();
        
         // Used when we load traces
       
    //    initializeTraces();
    }
  
    private void initializeTraces(){
    
    
        traces=new Traces();
        interarrivalTime=new double[_numberOfDomains]; 
        traceIndex=new int[_numberOfDomains]; 
        requestsSend=new int[_numberOfDomains]; 
        
        for (int i = 0; i < _numberOfDomains; i++) {
            traceIndex[i]=0;
            requestsSend[i]=0;
            interarrivalTime[i]=(double)60/traces.getTraces()[i][0];
        }
        
    }
    
    private void buildArrivalGenerators()
    {
        _generatorType=new String[_numberOfDomains];
        
        for (int i = 0; i < _numberOfDomains; i++) {
            _generatorType[i]=_config.getRequestArrivalTimeConfiguration()[i].get("arrType").toString();
        }
        
        // 2. Build the array of generators
        double lamda;
        double location;
        double shape;
         
        this._exponentialArrivalGenerator=new Exponential[_numberOfDomains];
        this._paretoArrivalGenerator=new Pareto[_numberOfDomains];
        
            for (int i = 0; i < _numberOfDomains; i++) {
                
                if(_generatorType[i].equals(EGeneratorType.Exponential.toString())){
                
                    lamda=((Double)_config.getRequestArrivalTimeConfiguration()[i].get("mean")).doubleValue();
                    this._exponentialArrivalGenerator[i]=new Exponential(lamda);
                    this._paretoArrivalGenerator[i]=null;
                }
                else if(_generatorType[i].equals(EGeneratorType.Pareto.toString())){
                    
                  _exponentialArrivalGenerator[i]=null;  
                  location=((Double)_config.getRequestArrivalTimeConfiguration()[i].get("location")).doubleValue();
                  shape =((Double)_config.getRequestArrivalTimeConfiguration()[i].get("shape")).doubleValue();
                  
                  this._paretoArrivalGenerator[i]=new Pareto(location, shape);
                  double mean=_paretoArrivalGenerator[i].mean();
                  System.out.print(mean);
                }
       
            }      
        
    }
      
     private void BuildBurstGenerators()
    {
        this._onGeneratorType=new String[_numberOfDomains];
        this._offGeneratorType=new String[_numberOfDomains];
        this._onPeriodExponentialGenerator=new Exponential[_numberOfDomains];
        this._offPeriodExponentialGenerator=new Exponential[_numberOfDomains];
        this._onPeriodParetoGenerator=new Pareto[_numberOfDomains];
        this._offPeriodParetoGenerator=new Pareto[_numberOfDomains];
        
        
        for (int i = 0; i < _numberOfDomains; i++) {
            _onGeneratorType[i]=_config.getOnPeriodConfiguration()[i].get("onBurstType").toString();
            _offGeneratorType[i]=_config.getOffPeriodConfiguration()[i].get("offBurstType").toString();
        }
        
        // 2. Build the array of generators
        double lamda;
        double location;
        double shape;
        
        // ON periods Configuraiton
            for (int i = 0; i < _numberOfDomains; i++) {
                
                if(_onGeneratorType[i].equals(EGeneratorType.Exponential.toString())){
                
                    lamda=((Double)_config.getOnPeriodConfiguration()[i].get("mean")).doubleValue();
                    this._onPeriodExponentialGenerator[i]=new Exponential(lamda);
                    this._onPeriodParetoGenerator[i]=null;
                    //Initialize the first ON period
                    _onPeriodDuration[i]=_onPeriodExponentialGenerator[i].random();
                }
                else if(_onGeneratorType[i].equals(EGeneratorType.Pareto.toString())){
                    
                  _onPeriodExponentialGenerator[i]=null;  
                  location=((Double)_config.getOnPeriodConfiguration()[i].get("location")).doubleValue();
                  shape =((Double)_config.getOnPeriodConfiguration()[i].get("shape")).doubleValue();
                  
                  this._onPeriodParetoGenerator[i]=new Pareto(location, shape);
                 
                  //Initialize the first OFF period
                  _onPeriodDuration[i]=_onPeriodParetoGenerator[i].random();
                 
                }
                
            }      
            
            
            
         // OFF periods Configuraiton
             for (int i = 0; i < _numberOfDomains; i++) {
                
                if(_offGeneratorType[i].equals(EGeneratorType.Exponential.toString())){
                
                    lamda=((Double)_config.getOffPeriodConfiguration()[i].get("mean")).doubleValue();
                    this._offPeriodExponentialGenerator[i]=new Exponential(lamda);
                    this._offPeriodParetoGenerator[i]=null;
                    
                     //Initialize the first ON period
                    _offPeriodDuration[i]=_offPeriodExponentialGenerator[i].random();
                }
                else if(_offGeneratorType[i].equals(EGeneratorType.Pareto.toString())){
                    
                  _offPeriodExponentialGenerator[i]=null;  
                  location=((Double)_config.getOffPeriodConfiguration()[i].get("location")).doubleValue();
                  shape =((Double)_config.getOffPeriodConfiguration()[i].get("shape")).doubleValue();
                  
                  this._offPeriodParetoGenerator[i]=new Pareto(location, shape);
                   
                   //Initialize the first OFF period
                  _offPeriodDuration[i]=_offPeriodParetoGenerator[i].random();
                 
                }
            }      
        
    }
      
    
    public void loadRequests(){
    
        double time=0.001;
        
        for (int i = 0; i < _numberOfDomains; i++) {
                bringInitialRequests(i,time);    
                time+=time;
            
        }
        
    }
    
    public void bringInitialRequests(int serviceDomainID,double time)
    { 
      
        Request newRequest = new Request();
        
        newRequest.setServiceDomainID(serviceDomainID);
        newRequest.setClassID(0);
        newRequest.setWebGeneratorID(_nodeID);
      
        newRequest.setSize(1+rand.nextInt( _config.getMaxRequestSize()));
        newRequest.setRequestType(ERequestType.Web.toString());
        
        newRequest.setDelayInAllEdges(0);
        newRequest.setDelayInAllQueues(0);
        newRequest.setResponseTime(0);
        newRequest.setDelayInAllService(0);
        
        newRequest.setInNode_NodeiD(_nodeID);
        newRequest.setInNode_ArrivalMoment(time);
        newRequest.setInNode_ServiceDuration(0);
        newRequest.setInNode_delayInQueue(0);
        newRequest.setInNode_timeEnterServer(0);
        newRequest.setInNode_timeLeaveServer(0);
        
        newRequest.setNewNodeID(_nodeID);
        newRequest.setNewNodeArrivalMoment(time);
          
        newRequest.setFinalServiceTier(_config.getFinalServiceTier());
        //newRequest.getTierPath().add();
        
        _request[serviceDomainID].add(newRequest);
     
       
         createFirstArrivalEvent(newRequest);
    }
    
    
    public void CreateRequest(int serviceDomainID,boolean now)
    {
        
        double newInterArrivalTime;
        
        Request newRequest = new Request();
        double time=_tracker.getRunningEventTime();
        
        newRequest.setServiceDomainID(serviceDomainID);
        newRequest.setClassID(0);
        newRequest.setWebGeneratorID(_nodeID);
       
        newRequest.setSize(1+rand.nextInt(_config.getMaxRequestSize()));
        newRequest.setRequestType(ERequestType.Web.toString());

        newRequest.setDelayInAllEdges(0);
        newRequest.setDelayInAllQueues(0);
        newRequest.setResponseTime(0);
        newRequest.setDelayInAllService(0);

        if(now)
            newInterArrivalTime=0.00000001;
        else
            newInterArrivalTime= calculateInterArrivalTime(serviceDomainID);
        
        newRequest.setInNode_NodeiD(_nodeID);
        newRequest.setInNode_ArrivalMoment(time+newInterArrivalTime);
        newRequest.setInNode_ServiceDuration(0);
        newRequest.setInNode_delayInQueue(0);
        newRequest.setInNode_timeEnterServer(0);
        newRequest.setInNode_timeLeaveServer(0);
     
        newRequest.setFinalServiceTier(_config.getFinalServiceTier());
        newRequest.getTierPath().add(ENodeType.ExternalNode.toString());
       
        newRequest.setNewNodeID(_nodeID);
        newRequest.setNewNodeArrivalMoment(time+newInterArrivalTime);
        
         _request[serviceDomainID].add(newRequest);
         
         createArrivalEvent(newRequest);

    }

    
   private double calculateInterArrivalTime(int serviceDomainID) {
     
    if( _config.get_generateSaturatedConditions().equals(true)){ 
     		return 0.000000001; //saturated conditions 
    }
    else 
    	{
    		
        double interArrivalTime=-1;

        switch (EGeneratorType.valueOf(this._generatorType[serviceDomainID])){
            case Exponential:  
                interArrivalTime = _exponentialArrivalGenerator[serviceDomainID].random();
                
                   if (interArrivalTime>maxExpo)
                       maxExpo=interArrivalTime;
                   if (interArrivalTime<minExpo)
                       minExpo=interArrivalTime;
                                
                   break;
            
            case Pareto:  
                interArrivalTime=_paretoArrivalGenerator[serviceDomainID].random();
                
                if (interArrivalTime>maxPareto)
                    maxPareto=interArrivalTime;
                 if (interArrivalTime<minPareto)
                    minPareto=interArrivalTime;
                                
                break;
            
            
            case Random: interArrivalTime = _arrivalTimeRandom.nextInt();
                                break;
            
            case Fixed: interArrivalTime=((Double)_config.getRequestArrivalTimeConfiguration()[serviceDomainID].get("period")).doubleValue();
                                break;
        
            case Traces: interArrivalTime=tracesControl(serviceDomainID);
                                break;
            default:            
                break;
        }
        

        interArrivalTime=CheckOnOffPeriods(interArrivalTime,serviceDomainID);
        
        return interArrivalTime;
        
        }
    	
    }

   
   private double tracesControl(int domainID){
   
       double newinterarrivalTime;
       int index;
   
       if(requestsSend[domainID]==0){
        
           traceIndex[domainID]++;
           index=traceIndex[domainID];
      
           newinterarrivalTime=(double)60/(traces.getTraces()[domainID][index]);
           interarrivalTime[domainID]=newinterarrivalTime;
           
           requestsSend[domainID]=traces.getTraces()[domainID][index];
           requestsSend[domainID]--;
       }
       else{
        requestsSend[domainID]--;
       }
       
       return interarrivalTime[domainID];
   }
   
   
   private double calculateNextOnPeriod(int serviceDomainID) {
     		
        double nextOnPeriod=-1;

        switch (EGeneratorType.valueOf(this._onGeneratorType[serviceDomainID])){
            case Exponential:  
                nextOnPeriod = _onPeriodExponentialGenerator[serviceDomainID].random();
                break;
            
            case Pareto:  
                nextOnPeriod=_onPeriodParetoGenerator[serviceDomainID].random();
                break;
            
            default:            
                break;
        }
        
        return nextOnPeriod;
        
    }
   
   private double calculateNextOffPeriod(int serviceDomainID) {
     	
        double nextOnPeriod=-1;

        switch (EGeneratorType.valueOf(this._offGeneratorType[serviceDomainID])){
            case Exponential:  
                nextOnPeriod = _offPeriodExponentialGenerator[serviceDomainID].random();
                break;
            
            case Pareto:  
                nextOnPeriod=_offPeriodParetoGenerator[serviceDomainID].random();
                break;
            
            default:            
                break;
        }
        
        return nextOnPeriod;
        
     }
    	
    
   
   
    // <editor-fold defaultstate="collapsed" desc="Properties">
   

    public List<Request>[] getRequest() {
        return _request;
    }

      // </editor-fold>

   
   
   public Request returnRequest(int domainID,int requestID){
   
         for (int i = 0; i < _numberOfDomains; i++) {
         
             if(i==domainID){
                for (int j = 0; j < _request[i].size(); j++) {
                    if(_request[i].get(j).getRequestID()==requestID){
                        return _request[i].get(j);
                    }
                  
               }
            }
         }
         return null;
   }
   
   public void createFirstArrivalEvent(Request request)
   {
       double time=request.getNewNodeArrivalMoment();
       int requestID=request.getRequestID();
       int domainID=request.getServiceDomainID();
      
               
       _eventMonitor.AddInitialArrivalEvents(new TrafficEvent(
	               EEventType.TrafficEvent.toString(),
	               ETrafficEvent.ArrivalInRouter.toString(),
	               time,
                       _nodeID,
                       requestID,
                       domainID,
                       _nodeID));
    }
   
   public void createArrivalEvent(Request request)
   {
       double time=request.getNewNodeArrivalMoment();
       int requestID=request.getRequestID();
       int domainID=request.getServiceDomainID();
   
       _eventMonitor.NewEventEnterList(new TrafficEvent(
	               EEventType.TrafficEvent.toString(),
	               ETrafficEvent.ArrivalInRouter.toString(),
	               time,
                       _nodeID,
                       requestID,
                       domainID,_nodeID));
	   }
 
   
    public void removeRequestFromSystem(Request request) {
         
	        int domainID=request.getServiceDomainID();
                int requestID=request.getRequestID();
                int requestIndex=-1;
	        
                  for (int i = 0; i < _numberOfDomains; i++) {

                         if(i==domainID){
                            for (int j = 0; j < _request[i].size(); j++) {
                                if(_request[i].get(j).getRequestID()==requestID){
                                  requestIndex=j;      
                                }

                           }
                        }
                     }
                
	               
	         request=null;
                 
	        _request[domainID].remove(requestIndex);
	       
	       
	    }

    private double CheckOnOffPeriods(double interArrivalTime,int serviceDomainID) {
      
        double _interArrivalTime=0;
      
        if(_onPeriodDuration[serviceDomainID]-interArrivalTime>0){
            _interArrivalTime=interArrivalTime;
            _onPeriodDuration[serviceDomainID]=_onPeriodDuration[serviceDomainID]-interArrivalTime;
                 
        }
        else if(_onPeriodDuration[serviceDomainID]-interArrivalTime<=0){
            _offPeriodDuration[serviceDomainID]=calculateNextOffPeriod(serviceDomainID);
            _interArrivalTime=_offPeriodDuration[serviceDomainID];
            
            _onPeriodDuration[serviceDomainID]=calculateNextOnPeriod(serviceDomainID);
        }
        
          return _interArrivalTime;
    }
   
   
}
