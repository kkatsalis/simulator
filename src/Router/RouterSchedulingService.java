/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import Router.Router;
import Router.RouterController;
import BL.Configuration;
import BL.RandomCollection;
import BL.Sla;
import Enumerators.EAlgorithmScheduling;
import Enumerators.ENodeType;




   
/**
 *
 * @author maevious
 */
public class RouterSchedulingService {

    @SuppressWarnings("rawtypes")
    HashMap _nodeConfig;
    Configuration _config;
    RouterController _routerController;
    Router _hostingNode;
    int _numberOfDomains;
    int queueToSelect=0;
    
    Random _rand; 
    double _roundTimeBegin=0;
    List<Integer> _roundList;
    
    double[] _schedulingWeightsArray;
   
    Boolean hasRequestsInQueues=true;
    
    double[] credits;
    int[] conCharged;
   
    int tempNtmsID;
    
    public RouterSchedulingService(Router hostingNode) {
       
    	this._hostingNode=hostingNode;
        this._nodeConfig = _hostingNode.getNodeConfig();
        this._routerController = _hostingNode.getRouterController();
        this._config=_hostingNode.getConfig();
        this._numberOfDomains=_config.getNumberOfDomains();
        this._rand=new Random();
        this._schedulingWeightsArray=new double[_config.getNumberOfDomains()];
        this._roundList=new ArrayList<>();
        
       
        if(_nodeConfig.get("SchedulingAlgorithm").equals(EAlgorithmScheduling.Credit.toString())){
            this.credits=new double[_numberOfDomains]; //used by Credit scheduling only
            
            for (int i = 0; i < _numberOfDomains; i++) {
                credits[i]=20;
            }
           
        }
    }
    

	public int SelectQueuetoServe() {
      
        int queueID=-1;
      
      
        //--------------------------------------------------
        //			 Round per Round Scheduling
        //--------------------------------------------------
      
        if(_nodeConfig.get("SchedulingAlgorithm").equals(EAlgorithmScheduling.FIFO.toString())){
            queueID = roundScheduler(EAlgorithmScheduling.FIFO.toString()); 
        }
        // RANDOM
        if(_nodeConfig.get("SchedulingAlgorithm").equals(EAlgorithmScheduling.Random.toString())){
            queueID = roundScheduler(EAlgorithmScheduling.Random.toString()); 
        }
        // ROUND ROBIN 
        else if(_nodeConfig.get("SchedulingAlgorithm").equals(EAlgorithmScheduling.RoundRobin.toString())){            
            queueID = roundScheduler(EAlgorithmScheduling.RoundRobin.toString());
        }
        // SATURATED BGP 
        else if(_nodeConfig.get("SchedulingAlgorithm").equals(EAlgorithmScheduling.SaturatedBGP.toString())){
            queueID=roundScheduler(EAlgorithmScheduling.SaturatedBGP.toString());
        }
        // SATURATED OMS
        else if(_nodeConfig.get("SchedulingAlgorithm").equals(EAlgorithmScheduling.SaturatedOMS.toString())){
            queueID=roundScheduler(EAlgorithmScheduling.SaturatedOMS.toString());
        }
        // NON SATURATED BGP
        else if(_nodeConfig.get("SchedulingAlgorithm").equals(EAlgorithmScheduling.NonSaturatedBGP.toString())){
            queueID=roundScheduler(EAlgorithmScheduling.NonSaturatedBGP.toString());
        }
        // NON SATURATED BGPK
        else if(_nodeConfig.get("SchedulingAlgorithm").equals(EAlgorithmScheduling.NonSaturatedBGPK.toString())){
            queueID=roundScheduler(EAlgorithmScheduling.NonSaturatedBGPK.toString());
        }
        // NON SATURATED OMS
        else if(_nodeConfig.get("SchedulingAlgorithm").equals(EAlgorithmScheduling.NonSaturatedOMS.toString())){
            queueID=roundScheduler(EAlgorithmScheduling.NonSaturatedOMS.toString());
        }
        else if(_nodeConfig.get("SchedulingAlgorithm").equals(EAlgorithmScheduling.WRR.toString())){
            queueID=roundScheduler(EAlgorithmScheduling.WRR.toString());
        }
        else if(_nodeConfig.get("SchedulingAlgorithm").equals(EAlgorithmScheduling.NTMS.toString())){
            queueID=roundScheduler(EAlgorithmScheduling.NTMS.toString());
        }
        else if(_nodeConfig.get("SchedulingAlgorithm").equals(EAlgorithmScheduling.Credit.toString())){
            queueID=roundScheduler(EAlgorithmScheduling.Credit.toString());
        }
            return queueID;
     }
   

   
    
    private int roundScheduler(String algorithm) {
        
    	int queueId=-1;
        int round=_routerController.getGlobalController().getRunningRound();
        
        if(_roundList.isEmpty()){
            
            _routerController.getGlobalController().setRunningRound(round+1);
           
            hasRequestsInQueues=updateDomainsListInRound(algorithm);

                if(hasRequestsInQueues){
                   
                    queueToSelect = _roundList.remove(0);
                }
                else{
                    queueToSelect = -1;   // No available requests in queues
                }
        }
        else{       
            queueToSelect=_roundList.remove(0);
        }


        queueId=queueToSelect;

        return queueId;
       
    }
   
  

   
   public Boolean updateDomainsListInRound(String algorithm)
   {
       prepareSchedulingList(algorithm);
       
       _roundList.clear();
       int length=_schedulingWeightsArray.length;
       
       for (int i = 0; i < length; i++) {
           if(_schedulingWeightsArray[i]>0)
              
              while(_schedulingWeightsArray[i]>0){
                
                  _roundList.add(i);
                  _schedulingWeightsArray[i]--;
              }
       }
   
        if(_roundList.isEmpty()){
           return false;
        }
        
        return true;
        
   }
   
   
   

    private void prepareSchedulingList(String algorithm) {
      	
       if(algorithm.equals(EAlgorithmScheduling.RoundRobin.toString()))    // ROUND ROBIN
            prepareListForRoundRobin();
       else if(algorithm.equals(EAlgorithmScheduling.FIFO.toString()))    // FIFO
            prepareListForFIFO();
       else if(algorithm.equals(EAlgorithmScheduling.SaturatedBGP.toString())) // SATURATED BGP
            prepareListForSaturatedBGP();
       else if(algorithm.equals(EAlgorithmScheduling.SaturatedOMS.toString())) // SATURATED OMS
            prepareListForSaturatedOMS();
       else if(algorithm.equals(EAlgorithmScheduling.NonSaturatedBGP.toString())) //NON SATURATED BGP
            prepareListForNonSaturatedBGP();
       else if(algorithm.equals(EAlgorithmScheduling.NonSaturatedBGPK.toString())) //NON SATURATED BGPK
            prepareListForNonSaturatedBGPK();
       else if(algorithm.equals(EAlgorithmScheduling.NonSaturatedOMS.toString())) //NON SATURATED OMS
            prepareListForNonSaturatedOMS();
       else if(algorithm.equals(EAlgorithmScheduling.WRR.toString())) //NON SATURATED OMS
            prepareListForWRR();
       else if(algorithm.equals(EAlgorithmScheduling.NTMS.toString())) //NON SATURATED OMS
            prepareListForNTMS();
       else if(algorithm.equals(EAlgorithmScheduling.Credit.toString())) //NON SATURATED OMS
            prepareListForCredit();
       else 
            prepareListForRandom(); //DEFAULT RANDOM
    }
  
   public void prepareListForNonSaturatedOMS(){
        throw new UnsupportedOperationException("Not supported yet."); 
   }
   
    private Boolean prepareListForRoundRobin() {
          
           List<Integer> nonEmptyQueues  = _routerController.getUtilities().findNonEmptyQueues(_hostingNode.getQueue());
	
           // First Check
           if(nonEmptyQueues.isEmpty())
               return false;
           
           // Basic Algorithm
           for (int j = 0; j < _schedulingWeightsArray.length; j++) {
                if(nonEmptyQueues.contains(j)) {
                    _schedulingWeightsArray[j]=1;
                }
                else{
                    _schedulingWeightsArray[j]=0;
                }
            }
           
           // Second Check
           for (int j = 0; j < _schedulingWeightsArray.length; j++) {
              if(_schedulingWeightsArray[j]==1){
                    return true;
                }
           }
           
           return false;
        
    }
    
   private Boolean prepareListForRandom() {
      
       List<Integer> nonEmptyQueues  = _routerController.getUtilities().findNonEmptyQueues(_hostingNode.getQueue());
       int randomIndex;
       
        if(nonEmptyQueues.isEmpty())
            return false;
        else{
        
            randomIndex =nonEmptyQueues.get(_rand.nextInt(nonEmptyQueues.size()));
            _schedulingWeightsArray[randomIndex]=1;
            
            return true;
        }
   
    } 

   private Boolean prepareListForFIFO() {
        
        List<Integer> nonEmptyQueues  = _routerController.getUtilities().findNonEmptyQueues(_hostingNode.getQueue());
        
        if(nonEmptyQueues.isEmpty())
            return false;
        else{
            
            _schedulingWeightsArray[0]=1;
            return true;
        }
    }
    
   private Boolean prepareListForSaturatedBGP(){
	   
       
        List<Integer> domainsBelowGoal=_hostingNode.getRouterController().getGlobalController().findAllDomainsBelowGoal();

        for (int j = 0; j < _schedulingWeightsArray.length; j++) {
            if(domainsBelowGoal.contains(j)) {
                    _schedulingWeightsArray[j]=1;
            }
            else{
                    _schedulingWeightsArray[j]=0;
            }
        }
	
        return true;
   }
    
   private Boolean prepareListForSaturatedOMS(){
	   
       int minDomain=_hostingNode.getRouterController().getGlobalController().findDomainWithMinUtilizationBelowGoal("Appliance");  

       for (int j = 0; j < _schedulingWeightsArray.length; j++) {
               if(minDomain==j){
                        _schedulingWeightsArray[j]=1;
                    }
                    else {
                            _schedulingWeightsArray[j]=0;
                    }
            }
       
       return true;
   }
   
   private Boolean prepareListForNonSaturatedBGP()
   {
	   Random rand=new Random();
	 		 
	   List<Integer> domainsBelowGoal=_hostingNode.getRouterController().getGlobalController().findAllDomainsBelowGoal();
           List<Integer> nonEmptyQueues  = _routerController.getUtilities().findNonEmptyQueues(_hostingNode.getQueue());
	
           // First Check
           if(nonEmptyQueues.isEmpty())
               return false;
           
           // Basic Algorithm
           for (int j = 0; j < _schedulingWeightsArray.length; j++) {
                if(domainsBelowGoal.contains(j)&nonEmptyQueues.contains(j)) {
                    _schedulingWeightsArray[j]=1;
                }
                else{
                    _schedulingWeightsArray[j]=0;
                }
            }
           

          boolean weightAllocated=false;

           // Second Check
           for (int j = 0; j < _schedulingWeightsArray.length; j++) {
              if(_schedulingWeightsArray[j]>=1){
                    weightAllocated=true;
                }
           }
           if((!nonEmptyQueues.isEmpty())&weightAllocated==false){
                
               int id;
               //Random Scheduling                
             id=NonSaturatedBGP_Random(nonEmptyQueues);
             
               //Less Requests
             //  id=NonSaturatedBGP_MinQueue(nonEmptyQueues,domainsBelowGoal);
              
               //More Requests
             //   id=NonSaturatedBGP_MaxQueue(nonEmptyQueues,domainsBelowGoal);
                
               _schedulingWeightsArray[id]=1;
                weightAllocated=true;
           }

           
           return weightAllocated;
          
   }
   
   private Boolean prepareListForNonSaturatedBGPK()
   {
       
	   Random rand=new Random();
	 		 
	   List<Integer> domainsBelowGoal=_hostingNode.getRouterController().getGlobalController().findAllDomainsBelowGoal();
           List<Integer> nonEmptyQueues  = _routerController.getUtilities().findNonEmptyQueues(_hostingNode.getQueue());
	
           // First Check
           if(nonEmptyQueues.isEmpty())
               return false;
           
           // Basic Algorithm
           for (int j = 0; j < _schedulingWeightsArray.length; j++) {
                if(domainsBelowGoal.contains(j)&nonEmptyQueues.contains(j)) {
                    _schedulingWeightsArray[j]= (int)_routerController.getUtilities().findDomainQueueSize(_hostingNode.getQueue(),j);
                }
                else{
                    _schedulingWeightsArray[j]=0;
                }
            }
           

          boolean weightAllocated=false;

           // Second Check
           for (int j = 0; j < _schedulingWeightsArray.length; j++) {
              if(_schedulingWeightsArray[j]>=1){
                    weightAllocated=true;
                }
           }
           if((!nonEmptyQueues.isEmpty())&weightAllocated==false){
                
               int id;
               //Random Scheduling                
           //    id=NonSaturatedBGP_Random(nonEmptyQueues);
             
               //Less Requests
            //   id=NonSaturatedBGP_MinQueue(nonEmptyQueues,domainsBelowGoal);
              
               //More Requests
                id=NonSaturatedBGP_MaxQueue(nonEmptyQueues,domainsBelowGoal);
                
               _schedulingWeightsArray[id]=1;
                weightAllocated=true;
           }

           
           return weightAllocated;
          
   }
   
    private int NonSaturatedBGP_Random(List<Integer> nonEmptyQueues){
       return nonEmptyQueues.get(_rand.nextInt(nonEmptyQueues.size()));
    }
  
    private int NonSaturatedBGP_MinQueue(List<Integer> nonEmptyQueues, List<Integer> domainsBelowGoal) {
       
        return _routerController.getUtilities().findMinNonEmptyQueueInList(_hostingNode.getQueue(),domainsBelowGoal);
    }
    
    private int NonSaturatedBGP_MaxQueue(List<Integer> nonEmptyQueues, List<Integer> domainsBelowGoal) {
       
        return _routerController.getUtilities().findMaxNonEmptyQueueInList(_hostingNode.getQueue(),domainsBelowGoal);
    }

    public Boolean isHasRequestsInQueues() {
        return hasRequestsInQueues;
    }

    // WRR: weights are according to SLA
    private boolean prepareListForWRR() {
     
        int queueSize;
        int _numberOfDomains=_config.getNumberOfDomains();
        List<Integer> nonEmptyQueues  = _routerController.getUtilities().findNonEmptyQueues(_hostingNode.getQueue());
	double[] sla=new double[_numberOfDomains];
        System.arraycopy(_routerController.getGlobalController().getGlobalSla().getCpuUtilizationSLA(),0, sla, 0, _numberOfDomains);                
        
        for (int i = 0; i < _numberOfDomains; i++) {
            sla[i]=10*sla[i];
        }
        
        if(nonEmptyQueues.isEmpty())
               return false;
        
         // Basic Algorithm
           for (int j = 0; j < _schedulingWeightsArray.length; j++) {
               
               queueSize=_routerController.getUtilities().findQueueSize(_hostingNode.getQueue(),j);
                
               if(nonEmptyQueues.contains(j)) {
                    if(sla[j]<queueSize){
                        _schedulingWeightsArray[j]=sla[j];
                    }
                    else {
                        _schedulingWeightsArray[j]=queueSize;
                    }
                }
                else{
                    _schedulingWeightsArray[j]=0;
                }
            }
           
            boolean weightAllocated=false;

           // Second Check
           for (int j = 0; j < _schedulingWeightsArray.length; j++) {
              if(_schedulingWeightsArray[j]>=1){
                    weightAllocated=true;
                }
           }
           if((!nonEmptyQueues.isEmpty())&weightAllocated==false){
                _schedulingWeightsArray[nonEmptyQueues.get(_rand.nextInt(nonEmptyQueues.size()))]=1;
                weightAllocated=true;
           }

           
           return weightAllocated;
        
    }

    private double[] updateDynamicNtmsSLA() {
      
        int[] queueSize=new int[_numberOfDomains];
        double temp=0;
       double cpuPercentageAllShare=_config.getCpuPercentageAllShare();
        
        double runningTime=_routerController.getGlobalController().getTracker().getRunningEventTime();
        //double simulationTime=_config.getSimulationTime();
        double simulationTime=1.1*runningTime;
        
        double timeFractionA=(double)simulationTime/(simulationTime-runningTime);
        double timeFractionB=(double)runningTime/(simulationTime-runningTime);
        
        double[] sla=new double[_numberOfDomains];
        System.arraycopy(_routerController.getGlobalController().getGlobalSla().getCpuUtilizationSLA(),0, sla, 0, _numberOfDomains);                
       
        double[] dynamicSla=new double[_numberOfDomains];
     
        double util;
        double totalQueues= 0;
        
        for (int i = 0; i < _numberOfDomains; i++) {
             queueSize[i]=_routerController._hostingRouter.getQueue()[i].size();
            totalQueues+=  queueSize[i];
        }
        
      
        
        for (int i = 0; i < _numberOfDomains; i++){
           
            util=_routerController.getGlobalController().getAppsTierStatistics().getCpuUtilization_PSD()[i];

                temp=sla[i]*timeFractionA - util*timeFractionB;   
                 dynamicSla[i]=temp;
           
        }
       
       
        double totalNecativeNeeds = 0;
      
        for (int i = 0; i < _numberOfDomains; i++){
            if(dynamicSla[i]<0)
                totalNecativeNeeds +=dynamicSla[i];
        }
        
          for (int i = 0; i < _numberOfDomains; i++){
            dynamicSla[i]+=Math.abs(totalNecativeNeeds)+(double)cpuPercentageAllShare/_numberOfDomains;
          }
          
          
                return dynamicSla;
    }
    
    private boolean prepareListForNTMS() {
           
        List<Integer> nonEmptyQueues  = _routerController.getUtilities().findNonEmptyQueues(_hostingNode.getQueue());
	double[] sla=new double[_numberOfDomains];
        System.arraycopy(updateDynamicNtmsSLA(),0, sla, 0, _numberOfDomains);                
        _rand=new Random();
        RandomCollection<Integer> harmonicItems;
        
                
        if(nonEmptyQueues.isEmpty())
               return false;
        
         // Basic Algorithm
        harmonicItems = new RandomCollection<Integer>();
        
        for (int i = 0; i < _numberOfDomains; i++) {
            
            _schedulingWeightsArray[i]=0;
            
            if(nonEmptyQueues.contains(i)){
                harmonicItems.add(sla[i], i);
            }
              
        }
       //    System.out.println(sla[0]+"-"+sla[1])  ; 
            // get a random item
        int domain= harmonicItems.next();
     
        
        while(_schedulingWeightsArray[domain]==0){
          
            if(nonEmptyQueues.contains(domain)) {
                _schedulingWeightsArray[domain]=1;
                tempNtmsID=domain;
            }
            else{         
            _schedulingWeightsArray[domain]=0;
            domain= harmonicItems.next();
         
           
          }
        }
              
           return true;
        
    }

    private boolean prepareListForCredit() {
    
      List<Integer> nonEmptyQueues  = _routerController.getUtilities().findNonEmptyQueues(_hostingNode.getQueue());
                     
        if(nonEmptyQueues.isEmpty())
               return false;
       
        double charge=0;
        // 1. charge credits
        for (int i = 0; i < _numberOfDomains; i++) {
            charge=_hostingNode.getDataCenter().getAppliances().get(0).getApplianceController().getCreditsToCharge()[i];  
            credits[i]-=charge;
         
              
        }
      
         boolean zeroCredits=true;
    
           for (int i = 0; i < _numberOfDomains; i++) {
                if(credits[i]>0)
                    zeroCredits=false;
            }
           
           if(zeroCredits)
           {
              double[] sla=new double[_numberOfDomains];
                 System.arraycopy(_routerController.getGlobalController().getGlobalSla().getCpuUtilizationSLA(),0, sla, 0, _numberOfDomains);                
        
                int[] weights=new int[_numberOfDomains];


                for (int i=0;i<_numberOfDomains;i++) {

                    weights[i]=(int)(20*sla[i]);
                    credits[i]+=weights[i];
                }
           
           }
          
           
          boolean weightAllocated=false;
         // Basic Algorithm
           for (int j = 0; j < _schedulingWeightsArray.length; j++) {
             
               if(checkWeightAllocationMade()){
                return true;
               }
              
               if(!nonEmptyQueues.contains(j)){
                  _schedulingWeightsArray[j]=0;
               }
               else{
                    if(credits[j]>0){
                        _schedulingWeightsArray[j]=1;
                 
                        weightAllocated=true;
                    }
                    else {
                        _schedulingWeightsArray[j]=0;
                    }
                }
               
            }
           
             weightAllocated=checkWeightAllocationMade();
            int domain;

            
    
                 

// Second Check
           
           if((!nonEmptyQueues.isEmpty())&weightAllocated==false){
               
               domain=nonEmptyQueues.get(_rand.nextInt(nonEmptyQueues.size()));
               _schedulingWeightsArray[domain]=1;
               
                weightAllocated=true;
           }
           

           
           return weightAllocated;
        
    }

    private boolean checkWeightAllocationMade(){
    
       boolean allocatonMade=false; //only 1 permitted
         
       for (int i = 0; i < _numberOfDomains; i++) {
            if(_schedulingWeightsArray[i]>=1)
               allocatonMade=true;
        }
        return allocatonMade;
   }

   
    
}

    
   
   
    

