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
import BL.Request;
import Enumerators.EAlgorithmEnQueueing;
import Enumerators.EAlgorithmRouting;

/**
 *
 * @author kostas
 */
public class RouterEnqueueService {
   
    Router _hostingNode;
    RouterController _nodeController;
    HashMap _nodeConfig;
    Configuration _config;
    Random _rand; 
   
    int lastValidRRIndex=0;
    int listSizeRR;
    
    List<Integer> _validQueues;
    
    public RouterEnqueueService(Router hostingNode) {
       
    	this._hostingNode=hostingNode;
        this._nodeController = _hostingNode.getRouterController();
        this._nodeConfig = _hostingNode.getNodeConfig();
        this._config = _hostingNode.getConfig();
        this._rand= new Random();
        this._validQueues=new ArrayList<Integer>();
    }
    
    public int QueueToInsertRequest(Request request)
    {
    
        int queueId=-1;
        int domainID=request.getServiceDomainID();
     
        //1. Update The system and Global Statistics
        _hostingNode.getController().RunControl();
             
        //2. Decide where to route
         if(_nodeConfig.get("EnQueueingAlgorithm").equals(EAlgorithmEnQueueing.FIFO.toString())){
            queueId=algorithm_FifoEnqueuing();
        }
        if(_nodeConfig.get("EnQueueingAlgorithm").equals(EAlgorithmEnQueueing.Random.toString())){
            queueId=algorithm_RandomEnqueuing();
        }
        else if(_nodeConfig.get("EnQueueingAlgorithm").equals(EAlgorithmEnQueueing.RoundRobin.toString())){
            queueId=algorithm_RoundRobin();
        }
        else if(_nodeConfig.get("EnQueueingAlgorithm").equals(EAlgorithmEnQueueing.DistributedBucket.toString())){
            queueId=algorithm_DistributedBucket(domainID);
        }
        else if(_nodeConfig.get("EnQueueingAlgorithm").equals(EAlgorithmEnQueueing.CentralizedBucket.toString())){
            queueId=algorithm_CentralizedBucket(domainID);
        }
        else if(_nodeConfig.get("EnQueueingAlgorithm").equals(EAlgorithmEnQueueing.DomainPerQueuePlusBucket.toString())){
            queueId=algorithm_DomainPerQueuePlusBucket(domainID);
        }
        else if(_nodeConfig.get("EnQueueingAlgorithm").equals(EAlgorithmEnQueueing.MinQueueSize.toString())){
            queueId=algorithm_MinQueueSize();
        }
        else if(_nodeConfig.get("EnQueueingAlgorithm").equals(EAlgorithmEnQueueing.DomainPerQueue.toString())){
            queueId=algorithm_DomainPerQueue(domainID);
        }
       
        _hostingNode.getStatistics().getRequestsEnqueued_PQPSD()[queueId][domainID]++;
       
       return queueId;
    }

   

    private int algorithm_RoundRobin() {
       
        listSizeRR=((Integer)_nodeConfig.get("QueuesNumber")).intValue();
        
        if(lastValidRRIndex==listSizeRR-1){
           lastValidRRIndex=0;
        }
        else{       
           lastValidRRIndex++; 
        }
        
                
        return lastValidRRIndex;
    }

    private int algorithm_RandomEnqueuing() {
       
        int queueID;
        int queueSize=(Integer)_nodeConfig.get("QueuesNumber");
        
        if(queueSize>1){
        queueID=_rand.nextInt(queueSize-1);
        }
        else
            queueID=0;
        
        return queueID;
    }

  
     private int algorithm_DomainPerQueuePlusBucket(int domainID) {
        
          throw new UnsupportedOperationException("Not yet implemented");
    }

    private int algorithm_MinQueueSize() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    private int algorithm_DistributedBucket(int domainID) {
         throw new UnsupportedOperationException("Not yet implemented");
       
    }


    private int algorithm_CentralizedBucket(int domainID) {
        
    	   throw new UnsupportedOperationException("Not yet implemented");
    }
    
    private int algorithm_DomainPerQueue(int domainID) {
           
        int queueID=domainID;
        
        return queueID;
        
    }
    
    // Used By BQS and Secret Queue
    // valid are the queues that don't exceed the queue Limit
    
    private int algorithm_FifoEnqueuing() {
        
        int queueID=0;
        
        return queueID;
    }

   
    
    
}
