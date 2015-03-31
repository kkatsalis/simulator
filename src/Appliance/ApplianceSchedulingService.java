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
import Enumerators.EAlgorithmScheduling;
import Enumerators.ENodeType;




   
/**
 *
 * @author maevious
 */
public class ApplianceSchedulingService {

    Appliance _hostingAppliance;
    ApplianceController _applianceController;
    HashMap _applianceConfig;
    Configuration _config;
   
       
  
    
    public ApplianceSchedulingService(Appliance hostingAppliance) {
       this._hostingAppliance=hostingAppliance;
        this._applianceController = _hostingAppliance.getApplianceController();
        this._applianceConfig = _hostingAppliance.getApplianceConfig();
        this._config = _hostingAppliance.getConfig();
        
    }
    
    public int SelectQueuetoServe() {
      
        int queueID=0;
       
        return queueID;
     }
    
    
}

    
   
   
    

