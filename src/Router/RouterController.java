/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Router;



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
public class RouterController {
    
    Router _hostingRouter;
    Configuration _config;
    Controller _globalController;
    boolean _isAlive;
    
    List<Edge> _edgeList;
    List<Integer> _domainsToServe;        
   
    Utilities _utilities;
   
    public RouterController(Router hostingNode, Boolean isAlive) {
     
        this._hostingRouter=hostingNode;
        this._isAlive=isAlive;
        this._edgeList=new ArrayList<>();
        this._domainsToServe=new ArrayList<Integer>();
    	
    	this._globalController= _hostingRouter.getController();
    	this._config=_hostingRouter.getConfig();
    	
    	this._utilities=new Utilities();
        
    }
    
   public int checkIfApplianceIsWaiting(){
   
       int applianceID=-1;
   
       for (int i = 0; i < _config.getNumberOfAppliances(); i++) {
           if(!_globalController.getApplianceNeedsRequest().isEmpty())
               applianceID=_globalController.getApplianceNeedsRequest().get(0);
       }
       
       return applianceID;
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

    public Controller getGlobalController() {
        return _globalController;
    }

   
   
   
   
   
   
   }
 

	


	
	

    
    
