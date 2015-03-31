/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Network;

import java.util.List;
import BL.DataCenter;
import Enumerators.EEdgeBadwidth;

/**
 *
 * @author maevious
 */
public class Edge {

    static int uniqueEdgeID=0;
   
    int edgeID;
    int AnodeID;
    int BnodeID;

    boolean isLive;
    String bandwidth; 
    double bandwidthBPS; //in bps
    DataCenter _dataCenter;
    int requestsPassed;
    
    public Edge(int AnodeID, int BnodeID, String bandwidth, DataCenter dataCenter) {
        this.AnodeID = AnodeID;
        this.BnodeID = BnodeID;
        this.bandwidth = bandwidth;
        this.isLive=true;
        this.requestsPassed=0;
        this._dataCenter=dataCenter;
      
        
        bandwidthBPS=100000000;
        if(EEdgeBadwidth.Megabit100.toString().equals(bandwidth))
            bandwidthBPS=100000000;
        if(EEdgeBadwidth.Gigabit.toString().equals(bandwidth))
            bandwidthBPS=1000000000;
       
        edgeID=uniqueEdgeID;
        uniqueEdgeID++;
        
        AddEdgeToBothNodes();
        
    }

    public int getAnodeID() {
        return AnodeID;
    }

    public int getBnodeID() {
        return BnodeID;
    }

    public String getBandwidth() {
        return bandwidth;
    }

    public int getEdgeID() {
        return edgeID;
    }

    public boolean isIsLive() {
        return isLive;
    }

    public void setIsLive(boolean isLive) {
        this.isLive = isLive;
    }

    public int getRequestsPassed() {
        return requestsPassed;
    }

    public void setRequestsPassed(int requestsPassed) {
        this.requestsPassed = requestsPassed;
    }

   

    public double getBandwidthBPS() {
        return bandwidthBPS;
    }

    private void AddEdgeToBothNodes() {
      
        int nodeID;
        
        for(int i=0; i<_dataCenter.getRouters().size();i++)
        {
            nodeID=_dataCenter.getRouters().get(i).getNodeID();
            
            if(nodeID==AnodeID||nodeID==BnodeID)
                _dataCenter.getRouters().get(i).getRouterController().getEdgeList().add(this);
        }
        
        for(int i=0; i<_dataCenter.getAppliances().size();i++)
        {
            nodeID=_dataCenter.getAppliances().get(i).getNodeID();
            
            if(nodeID==AnodeID||nodeID==BnodeID)
                _dataCenter.getAppliances().get(i).getApplianceController().getEdgeList().add(this);
        }
    }
    
    
    

}
