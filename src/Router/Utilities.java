/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Router;

import BL.DataCenter;
import BL.Request;
import BL.Sla;
import Enumerators.EEventType;
import Enumerators.ETrafficEvent;
import Events.Event;
import Events.EventMonitor;
import Events.TrafficEvent;
import Statistics.CpuStatistics;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 *
 * @author kostas
 */
public class Utilities {
 
   public List<Integer> findNonEmptyQueues( Queue<Request>[] _queue )
   { 
       
        int queueSize=-1;
        
        List<Integer> _validQueues=new ArrayList<>();
       
        for (int i = 0; i < _queue.length; i++) {
           
            queueSize=_queue[i].size();
            
            if(queueSize>0)
                _validQueues.add(i);
        }
    
        return _validQueues;
    }
  
   public int findQueueSize(Queue<Request>[] _queue,int domainID){
   
        return  _queue[domainID].size();
        
   }
   public int findMinNonEmptyQueueInList( Queue<Request>[] _queue, List<Integer> _domainsBelowGoal )
   { 
       Random rand=new Random();
        List<Integer> nonEmptyQueues= findNonEmptyQueues(_queue);
       
       int id=-1;
       
       int[] queueSize=new int[_queue.length];
       
       for (int i = 0; i < _queue.length; i++) {
       
           queueSize[i]=0;
           
           if(!_domainsBelowGoal.contains(i)&nonEmptyQueues.contains(i))
               queueSize[i]=_queue[i].size();
       }
       
      
       int minSize=1000000000;
       

       for (int i = 0; i < queueSize.length; i++) {
         if(queueSize[i]>0&queueSize[i]<minSize){
            minSize=queueSize[i];
            id=i;
         }
             
       }
      
       if (id==-1)
            id=rand.nextInt(nonEmptyQueues.size());
    
        return id;
    }
   
   public int findMaxNonEmptyQueueInList( Queue<Request>[] _queue, List<Integer> _domainsBelowGoal )
   { 
       Random rand=new Random();
        List<Integer> nonEmptyQueues= findNonEmptyQueues(_queue);
       
       int id=-1;
       
       int[] queueSize=new int[_queue.length];
       
       for (int i = 0; i < _queue.length; i++) {
       
           queueSize[i]=0;
           
           if(!_domainsBelowGoal.contains(i)&nonEmptyQueues.contains(i))
               queueSize[i]=_queue[i].size();
       }
       
      
       int maxSize=0;
       

       for (int i = 0; i < queueSize.length; i++) {
         if(queueSize[i]>0&queueSize[i]>maxSize){
            maxSize=queueSize[i];
            id=i;
         }
             
       }
      
       if (id==-1)
            id=rand.nextInt(nonEmptyQueues.size());
    
        return id;
    }
   
   
   public int findDomainQueueSize(Queue<Request>[] _queue, int domainID){
       
       int queueSize=_queue[domainID].size();
       
       return  queueSize;
   }
    
    public int findNumberOfRequestsInQueue( Queue<Request>[] _queue){
         
         int numberOfRequests=0;

         for (int i = 0; i < _queue.length; i++) {
                 numberOfRequests+=_queue[i].size(); 
         }

         return numberOfRequests;
    }
    
    public int findDomainWithMaximumLocalUtilization(CpuStatistics cpu,Sla sla){
        
        List<Double> _deviationList=new ArrayList<Double>();
        
        double tempDeviation;
        double maxSatisfiedDeviation;
        
        
        for (int i = 0; i <cpu.getCpuUtilization_PSD().length; i++) {
            tempDeviation=cpu.getCpuUtilization_PSD()[i]-sla.getCpuUtilizationSLA()[i];
            _deviationList.add(tempDeviation);
            } 
        
        maxSatisfiedDeviation=Collections.max(_deviationList);
        
        // Find the most suffering and the most satisfied SD
        for (int i = 0; i < cpu.getCpuUtilization_PSD().length; i++) {
            tempDeviation=cpu.getCpuUtilization_PSD()[i]-sla.getCpuUtilizationSLA()[i];  
            
           
                if(maxSatisfiedDeviation==tempDeviation){
                       return i;
                   }
           
        
        }
        return -1;
        
     }
    
    public int findDomainWithMinimumLocalUtilization(CpuStatistics cpu,Sla sla){
        
      List<Double> _deviationList=new ArrayList<Double>();
        
        double tempDeviation;
        double minSatisfiedDeviation;
        
        
        for (int i = 0; i <cpu.getCpuUtilization_PSD().length; i++) {
            tempDeviation=cpu.getCpuUtilization_PSD()[i]-sla.getCpuUtilizationSLA()[i];
            _deviationList.add(tempDeviation);
            } 
        
        minSatisfiedDeviation=Collections.min(_deviationList);
        
        // Find the most suffering and the most satisfied SD
          // Find the most suffering and the most satisfied SD
        for (int i = 0; i < cpu.getCpuUtilization_PSD().length; i++) {
            tempDeviation=cpu.getCpuUtilization_PSD()[i]-sla.getCpuUtilizationSLA()[i];  
            
           
                if(minSatisfiedDeviation==tempDeviation){
                       return i;
                   }
           
        
        }
        return -1;
        
     }
    
    public List<Integer> findLocalListWithAllDomainsAboveGoal(CpuStatistics cpu,Sla sla)
    {
        // satisfaction = {under,over}
        List<Integer> _listOfDomainsAboveGoal=new ArrayList<Integer>();
    
        
        double tempDeviation;
        
        for (int i = 0; i <cpu.getCpuUtilization_PSD().length; i++) {
            
            tempDeviation=cpu.getCpuUtilization_PSD()[i]-sla.getCpuUtilizationSLA()[i];
            
            if(tempDeviation>0)
               _listOfDomainsAboveGoal.add(i);
          
            } 
        
           return _listOfDomainsAboveGoal;
      
    }
    
    public List<Integer> findLocalListWithAllDomainsBelowGoal(CpuStatistics cpu,Sla sla)
    {
        // satisfaction = {under,over}
        
        List<Integer> _listOfDomainsBelowGoal=new ArrayList<Integer>();
        
        double tempDeviation;

        
        for (int i = 0; i < cpu.getCpuUtilization_PSD().length; i++) {
            tempDeviation=cpu.getCpuUtilization_PSD()[i] - sla.getCpuUtilizationSLA()[i];
            if(tempDeviation<0)
               _listOfDomainsBelowGoal.add(i);
            
            } 

           return _listOfDomainsBelowGoal;
     
    }
    
    public List<Integer> ArrayNormalization(double[] arrayToNormalize)
	{
		 int counter=0;
	        double checkerVariable=0;
	        int[] countersTable=new int[arrayToNormalize.length];
	        int max;
	        List<Integer> _normalizedArrayList=new ArrayList<Integer>();
		 
	        DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00" );
	        
	        double tempNormilizer;
	       
	        for (int i = 0; i < arrayToNormalize.length; i++)
	        {
	        	tempNormilizer=new Double(df2.format(arrayToNormalize[i])).doubleValue();
	        	arrayToNormalize[i]=tempNormilizer;
	        }
	        
	      
	        
	        for (int i = 0; i < arrayToNormalize.length; i++) {
	                counter=-1;
	                do
	                {   
	                    counter++;
	                    checkerVariable=arrayToNormalize[i]*Math.pow(10,counter);
	                }while((int)checkerVariable!=checkerVariable);
	                
	                countersTable[i]=counter;
	        }
	         
	        
	        Arrays.sort(countersTable);
	        max=countersTable[countersTable.length-1];
			     
	            
	        int[] newArrayToNormalize=new int[arrayToNormalize.length];
	        
	        for (int i = 0; i < arrayToNormalize.length; i++) {
	            newArrayToNormalize[i]=(int)(arrayToNormalize[i]*Math.pow(10,max));
	        }
	        
	        for (int i = 0; i < newArrayToNormalize.length; i++) {
	            for (int j = 0; j < newArrayToNormalize[i]; j++) {
	                _normalizedArrayList.add(i);
	            }
	        }
	        
	        
	        return _normalizedArrayList;    
		
		
	}
    
    
    public void scheduleSendEvent(EventMonitor _eventMonitor,Request request,double time, int applianceID) {
	    	
        int requestID=request.getRequestID();
        int newNodeID=applianceID;
        int domainID=request.getServiceDomainID();
        
	    	_eventMonitor.NewEventEnterList(
	                 new TrafficEvent(
	                   EEventType.TrafficEvent.toString(),
	                   ETrafficEvent.ArrivalInAppliance.toString(),
	                   time,
                           newNodeID,
                           requestID,
                           domainID,
                           applianceID));
                
		}
    
    
    public void sendToIdleAppliance(DataCenter _dataCenter){
        
        
        
        int sufferingAppliancesSize=_dataCenter.getController().getApplianceNeedsRequest().size();
        
        if(sufferingAppliancesSize>0){
         //Check if there is no Event Already scheduled
         
            for (Event event : _dataCenter.getController().getEventMonitor().getEventList()) {
                if(event.getEventType().equals(EEventType.TrafficEvent.toString())){
                    if(((TrafficEvent)event).getTrafficEventType().equals(ETrafficEvent.ArrivalInAppliance.toString()))
                        return;
                }
            }
            
            _dataCenter.getAppliances().get(0).getUtililities().askRouterForNewRequest();
        }
    }
}
