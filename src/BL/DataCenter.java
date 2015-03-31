/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package BL;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Router.Router;
import Enumerators.ENodeType;

import Events.EventMonitor;
import Network.Edge;
import Appliance.Appliance;


/**
 *
 * @author Kostas Katsalis
 */
public class DataCenter {

   int _dataCenterID;
 
   Configuration _config;
  
   List<Router> _routers;
   List<Appliance> _appliances;
   List<Edge> _edges;

   Tracker _tracker;
   EventMonitor _eventMonitor;
   Controller _controller;
   
   
   
   public DataCenter(int dataCenterID,Configuration config, Tracker tracker,EventMonitor eventMonitor,Sla sla ) 
   {       
        this._config=config;
        this._tracker=tracker;
        this._eventMonitor=eventMonitor;  
        this._dataCenterID = dataCenterID;
        this._routers=new ArrayList<Router>();
        this._appliances=new ArrayList<Appliance>();
        this._edges=new ArrayList<Edge>();
       
       
     }

   public void CreateNetwork(){
   
 //  System.out.println("Create Network: Started");
   
    
    createNodes();
    createNetworkEdges();
    
  
   
   
   }
    
   public void createInitialRouterArrivalEvents(){
     _routers.get(0).getGenerator().loadRequests();
     
   //    System.out.println("Initial Arrival Events: Done");
   }
   
   void createInitialServiceEvents() {
       
       double time=5.0;
       
       for (int i = 0; i < _appliances.size(); i++) {
           _appliances.get(i).getUtililities().askRouterForFirstRequest(time);
           time+=time;
       }
       
   //     System.out.println("Initial Service Events: Done");
   
   }
   
   public void createNetworkController(Sla sla) {
        
       this._controller=new Controller(_config, this, _eventMonitor, _tracker,sla);
       
   //    System.out.println("Create Controller: Done");
    }
   
    public void createNodes() {
       
        int k=0;
        
        while(k<_config.getNumberOfRouters()){
        
            _routers.add(new Router(k, _config.getRouterConfiguration(), _tracker, _config, _eventMonitor, this, _controller));
            
            k++;
        }
     //   System.out.println("Create Routers: Done");
        int a=0;
        
        while(a<_config.getNumberOfAppliances()){
            
            // Appliances configuration (start nodeID==1) 
             _appliances.add(new Appliance(k, _config.getApplianceConfiguration(), _tracker, _config, _eventMonitor, this, _controller));
            a++;
            k++;
            
        }
        
     //    System.out.println("Create Appliances: Done");
    }
      
    private void createNetworkEdges()
    {   
        int nodeAId;
        int nodeBId;
       
        // Connect Router 0 to all the appliances
        for (int i = 0; i < _config.getNumberOfRouters(); i++) {
            
            nodeAId =_routers.get(i).getNodeID();
            
            for (int j = 0; j < _config.getNumberOfAppliances(); j++) {
               
                nodeBId=_appliances.get(j).getNodeID();
                
                _edges.add(new Edge(nodeAId,nodeBId,_config.getLinkBandwidth(),this));
            
            }
        }
        
 //      System.out.println("Create Edges: Done");
    }   
    
      
       
   
 
  

    public int getDataCenterID() {
        return _dataCenterID;
    }

   

    public Controller getController() {
        return _controller;
    }

   
    public List<Edge> getEdge() {
        return _edges;
    }

    public List<Router> getRouters() {
        return _routers;
    }

    public List<Appliance> getAppliances() {
        return _appliances;
    }

    

    
   
    
       
       
    
}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

