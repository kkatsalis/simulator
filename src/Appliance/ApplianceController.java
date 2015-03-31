/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Appliance;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import BL.Configuration;
import BL.Controller;
import BL.Request;
import BL.Sla;
import Enumerators.EAlgorithmScheduling;
import Enumerators.EEventType;
import Enumerators.ENodeType;
import Enumerators.ETrafficEvent;
import Events.TrafficEvent;
import Network.Edge;
import java.text.*;



/**
 *
 * @author maevious
 */
public class ApplianceController {
    
    Appliance _hostingAppliance;
    Configuration _config;
    Controller _globalController;
    boolean _isAlive;
    
    List<Edge> _edgeList;
    List<Integer> _domainsToServe;        
    Utilities _utilities;
     
    double[] _creditsToCharge; //used by Credit Scheduler

	public ApplianceController(Appliance hostingNode,Boolean isAlive) {
     
        this._hostingAppliance=hostingNode;
        this._isAlive=isAlive;
        this._edgeList=new ArrayList<>();
        this._domainsToServe=new ArrayList<Integer>();
    	
    	this._globalController= _hostingAppliance.getGlobalController();
    	this._config=_hostingAppliance.getConfig();
      	this._utilities=hostingNode.getUtililities();
         this._creditsToCharge=new double[_config.getNumberOfDomains()];
    
    }
    
  

    public List<Edge> getEdgeList() {
        return _edgeList;
    }

    public List<Integer> getDomainsToServe() {
        return _domainsToServe;
    }

 
    public Utilities getUtilities() {
        return _utilities;
    }

  

    public Configuration getConfig() {
        return _config;
    }
	
   public double[] getCreditsToCharge() {
        return _creditsToCharge;
    }
   
    //info used by Credit Scheduler
    public void updateCreditsToCharge(Request request) {
       
        int sdID=request.getServiceDomainID();
        
        for (int i = 0; i <_config.getNumberOfDomains(); i++) {
            if(i==sdID)
               _creditsToCharge[i]=request.getInNode_ServiceDuration();    
            else
                _creditsToCharge[i]=0;
        }
    }
   
   
   }
 

	


	
	

    
    
