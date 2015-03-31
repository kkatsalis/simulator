/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Appliance;

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
public class ApplianceEnqueueService {
   
    Appliance _hostingAppliance;
    ApplianceController _applianceController;
    HashMap _applianceConfig;
    Configuration _config;
    
    
    public ApplianceEnqueueService(Appliance hostingAppliance) {
       
    	this._hostingAppliance=hostingAppliance;
        this._applianceController = _hostingAppliance.getApplianceController();
        this._applianceConfig = _hostingAppliance.getApplianceConfig();
        this._config = _hostingAppliance.getConfig();
     
      
    }
    
    public int QueueToInsertRequest(Request request)
    {   
        int queueId=-1;
        //2. Decide where to route
         if(_applianceConfig.get("EnQueueingAlgorithm").equals(EAlgorithmEnQueueing.FIFO.toString())){
            queueId=algorithm_FifoEnqueuing();
        }
        
       
       return queueId;
    }

    private int algorithm_FifoEnqueuing() {
        
        int queueID=0;
        
        return queueID;
    }

   
    
    
}
