/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package BL;

import java.util.ArrayList;
import java.util.List;
import Enumerators.ERequestType;


/**
 * @author Kostas Katsalis
 */
public class Request {

    static int UNIQUErequestID;
       	   int requestID;     // this is a global ID
        String requestType;
           int webGeneratorID;      // GeneratorID: witch generator created this request
           int requestListIndex;
           int size;             // In KByte
           int serviceDomainID;  // Service Domain ID
	   int classID;          // The class within this service domain

        double _delayInAllQueues;   // How long this request waited in all queues
        double _delayInAllService; //How long this request waited in service
        double _delayInAllEdges;    // Delay only on the links
        double _responseTime;  // Total delay in the network

        // Inside Node Statistics
           int _inNode_NodeiD;
        String _inNode_InternalArrivalEventType;
        double _inNode_ArrivalMoment;
        double _inNode_ServiceDuration;
        double _inNode_timeEnterServer;
        double _inNode_timeLeaveServer;
        double _inNode_delayInQueue;
      
        
        int _newNodeID;
        double _newNodeArrivalMoment;
        
        String finalServiceTier; // Select from ENodeType

        //The path this request will follow e.g. [0]-[1]-[5]
	List<Integer> path;
        List<String> tierPath; // which Tiers the request passed 

    public Request() {

      requestID=UNIQUErequestID;     // this is a global ID
      webGeneratorID=0;      // GeneratorID: witch generator created this request
      size=0;             // In KB
      serviceDomainID=0;  // Service Domain ID
      classID=0;          // The class within this service domain
      requestType=ERequestType.Web.toString();

      _delayInAllQueues=0;   // How long this request waited in all queues
      _delayInAllService=0; //How long this request waited in service
      _delayInAllEdges=0;    // Delay only on the links
      _responseTime=0;  // Total delay in the network

      _inNode_NodeiD=0;
      _inNode_ArrivalMoment=0;
      _inNode_ServiceDuration=0;
      _inNode_timeEnterServer=0;
      _inNode_timeLeaveServer=0;
      _inNode_delayInQueue=0;
     
      
      _newNodeArrivalMoment=0;
      _newNodeID=-1;
//       generateInternalRequests=false;
//       numberOfInternalRequests=0;
       

        path=new ArrayList<Integer>();
        tierPath=new ArrayList<String>();
        
       
        UNIQUErequestID++;
    }

    public static int getUNIQUErequestID() {
        return UNIQUErequestID;
    }

    public double getInNode_ArrivalMoment() {
        return _inNode_ArrivalMoment;
    }

    public String getInNode_InternalArrivalEventType() {
        return _inNode_InternalArrivalEventType;
    }

    public int getInNode_NodeiD() {
        return _inNode_NodeiD;
    }

    public double getInNode_ServiceDuration() {
        return _inNode_ServiceDuration;
    }

    public double getInNode_delayInQueue() {
        return _inNode_delayInQueue;
    }

    public double getInNode_timeEnterServer() {
        return _inNode_timeEnterServer;
    }

    public double getInNode_timeLeaveServer() {
        return _inNode_timeLeaveServer;
    }

   

    public double getNewNodeArrivalMoment() {
        return _newNodeArrivalMoment;
    }

    public int getNewNodeID() {
        return _newNodeID;
    }

    public int getClassID() {
        return classID;
    }

    public String getFinalServiceTier() {
        return finalServiceTier;
    }

    public double getDelayInAllEdges() {
        return _delayInAllEdges;
    }

    public List<Integer> getPath() {
        return path;
    }

    public double getDelayInAllQueues() {
        return _delayInAllQueues;
    }

    public int getRequestID() {
        return requestID;
    }

    public String getRequestType() {
        return requestType;
    }

    public double getResponseTime() {
        return _responseTime;
    }

    public double getDelayInAllService() {
        return _delayInAllService;
    }

    public int getServiceDomainID() {
        return serviceDomainID;
    }

    public int getSize() {
        return size;
    }

    public List<String> getTierPath() {
        return tierPath;
    }

    public int getWebGeneratorID() {
        return webGeneratorID;
    }

    public void setInNode_ArrivalMoment(double _inNode_ArrivalMoment) {
        this._inNode_ArrivalMoment = _inNode_ArrivalMoment;
    }

    public void setInNode_InternalArrivalEventType(String _inNode_InternalArrivalEventType) {
        this._inNode_InternalArrivalEventType = _inNode_InternalArrivalEventType;
    }

    public void setInNode_NodeiD(int _inNode_NodeiD) {
        this._inNode_NodeiD = _inNode_NodeiD;
    }

    public void setInNode_ServiceDuration(double _inNode_ServiceDuration) {
        this._inNode_ServiceDuration = _inNode_ServiceDuration;
    }

    public void setInNode_delayInQueue(double _inNode_delayInQueue) {
        this._inNode_delayInQueue = _inNode_delayInQueue;
    }

    public void setInNode_timeEnterServer(double _inNode_timeEnterServer) {
        this._inNode_timeEnterServer = _inNode_timeEnterServer;
    }

    public void setInNode_timeLeaveServer(double _inNode_timeLeaveServer) {
        this._inNode_timeLeaveServer = _inNode_timeLeaveServer;
    }

   

    public void setNewNodeArrivalMoment(double _newNodeArrivalMoment) {
        this._newNodeArrivalMoment = _newNodeArrivalMoment;
    }

    public void setNewNodeID(int _newNodeID) {
        this._newNodeID = _newNodeID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public void setFinalServiceTier(String finalServiceTier) {
        this.finalServiceTier = finalServiceTier;
    }

    public void setDelayInAllEdges(double linkDelay) {
        this._delayInAllEdges = linkDelay;
    }

    public void setDelayInAllQueues(double queueDelay) {
        this._delayInAllQueues = queueDelay;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public void setResponseTime(double responseTime) {
        this._responseTime = responseTime;
    }

    public void setDelayInAllService(double serviceDelay) {
        this._delayInAllService = serviceDelay;
    }

    public void setServiceDomainID(int serviceDomainID) {
        this.serviceDomainID = serviceDomainID;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setWebGeneratorID(int webGeneratorID) {
        this.webGeneratorID = webGeneratorID;
    }

    public int getRequestListIndex() {
        return requestListIndex;
    }

    public void setRequestListIndex(int requestListIndex) {
        this.requestListIndex = requestListIndex;
    }


   
   
        
   
}



       
