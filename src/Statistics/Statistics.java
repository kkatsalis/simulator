/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Statistics;


import java.util.ArrayList;

import BL.Configuration;
import Enumerators.ENodeType;


/**
 *
 * @author Kostas Katsalis
 */
public class Statistics {

    int _numberOfDomains;
    int _numberOfLevels;
    int _numberOfQueues;
    String _nodeType;
    
    int     _numberOfSchedulingRounds;
    
    int     _requestArrivals;
    int[]   _requestArrivals_PL;
    int[]   _requestArrivals_PSD;   //[sdID]
    int[][] _requestArrivals_PLPSD; //[LevelID][sdID]
    
    int     _requestsServed;
    int[]   _requestsServed_PL;
    int[]   _requestsServed_PSD;
    int[][] _requestsServed_PLPSD; 
    
    int     _requestsDelayed;
    int[]   _requestsDelayed_PL;
    int[]   _requestsDelayed_PSD;
    int[][] _requestsDelayed_PLPSD;
        
    int     _requestsRejected;
    int[]   _requestsRejected_PL;
    int[]   _requestsRejected_PSD;
    int[][] _requestsRejected_PLPSD; 
    
    int     _requestsInQueues;
    int[]   _requestsInQueues_PL;
    int[]   _requestsInQueues_PSD;
    int[][] _requestsInQueues_PLPSD;
    long[][]   _requestsEnqueued_PQPSD;
     
    ArrayList<Integer[][]>  _requestsInQueues_PLPQPSD; //[levelID][queueID][sdID]
  
    double     _avgDelayInService;
    double[]   _avgDelayInService_PL;
    double[]   _avgDelayInService_PSD;
    double[][] _avgDelayInService_PLPSD; 
    
    double     _avgDelayInQueue;
    double[]   _avgDelayInQueue_PL;
    double[]   _avgDelayInQueue_PSD;
    double[][] _avgDelayInQueue_PLPSD;
   
       
      
     // <editor-fold desc="Constructor">
    public Statistics(int numberOfDomains,int numberOfLevels, int numberOfQueues )
    {
    	
         _numberOfDomains=numberOfDomains;
         _numberOfLevels=numberOfLevels;    
         _numberOfQueues=numberOfQueues;
         
         CreateArrays();      
         InitialValues();     
            
    }

    // </editor-fold>
    
    // <editor-fold desc="Methods">
  
    private void CreateArrays() {
       
               _requestArrivals_PL=new int[_numberOfLevels];
               _requestArrivals_PSD=new int[_numberOfDomains];   //[sdID]
               _requestArrivals_PLPSD=new int[_numberOfLevels][_numberOfDomains]; //[LevelID][sdID]
    
             
               _requestsServed_PL=new int[_numberOfLevels];
               _requestsServed_PSD=new int[_numberOfDomains];
               _requestsServed_PLPSD=new int[_numberOfLevels][_numberOfDomains]; 
    
              
               _requestsDelayed_PL=new int[_numberOfLevels];
               _requestsDelayed_PSD=new int[_numberOfDomains];
               _requestsDelayed_PLPSD=new int[_numberOfLevels][_numberOfDomains];
        
              
               _requestsRejected_PL=new int[_numberOfLevels];
               _requestsRejected_PSD=new int[_numberOfDomains];
               _requestsRejected_PLPSD=new int[_numberOfLevels][_numberOfDomains]; 
    
              
               _requestsInQueues_PL=new int[_numberOfLevels];
               _requestsInQueues_PSD=new int[_numberOfDomains];
               _requestsInQueues_PLPSD=new int[_numberOfLevels][_numberOfDomains];
               _requestsInQueues_PLPQPSD=new ArrayList<Integer[][]>(); //[levelID][queueID][sdID]
               _requestsEnqueued_PQPSD=new long[_numberOfQueues][_numberOfDomains];
                           
               _avgDelayInService_PL=new double[_numberOfLevels];
               _avgDelayInService_PSD=new double[_numberOfDomains];
               _avgDelayInService_PLPSD=new double[_numberOfLevels][_numberOfDomains]; 
    
         
               _avgDelayInQueue_PL=new double[_numberOfLevels];
               _avgDelayInQueue_PSD=new double[_numberOfDomains];
               _avgDelayInQueue_PLPSD=new double[_numberOfLevels][_numberOfDomains];
    
               
              
    }

    private void InitialValues() {
     
       _numberOfSchedulingRounds=0;
       _requestArrivals=0;
       _requestsServed=0;
       _requestsDelayed=0;
       _requestsRejected=0;
       _requestsInQueues=0;
       _avgDelayInService=0;
       _avgDelayInQueue=0;
      
       
        for (int i = 0; i < _numberOfDomains; i++) {
             
               _requestArrivals_PSD[i]=0;   //[sdID]
               _requestsServed_PSD[i]=0;
               _requestsDelayed_PSD[i]=0;
               _requestsRejected_PSD[i]=0;
               _requestsInQueues_PSD[i]=0;
               _avgDelayInService_PSD[i]=0;
               _avgDelayInQueue_PSD[i]=0;
           
            }
        
         for (int i = 0; i < _numberOfLevels; i++) {
               _requestArrivals_PL[i]=0;
               _requestsServed_PL[i]=0;
               _requestsDelayed_PL[i]=0;
               _requestsRejected_PL[i]=0;
               _requestsInQueues_PL[i]=0;
               _avgDelayInService_PL[i]=0;
               _avgDelayInQueue_PL[i]=0;
               
            }
             
            for (int i = 0; i < _numberOfLevels; i++) {
                for (int j = 0; j < _numberOfDomains; j++) {
             
               _requestArrivals_PLPSD[i][j]=0;
               _requestsServed_PLPSD[i][j]=0;
               _requestsDelayed_PLPSD[i][j]=0;
               _requestsRejected_PLPSD[i][j]=0;
               _requestsInQueues_PLPSD[i][j]=0;
               _avgDelayInService_PLPSD[i][j]=0;
               _avgDelayInQueue_PLPSD[i][j]=0;
    
                    
                }  
            }
    
            for (int k = 0; k < _numberOfLevels; k++) {
                for (int i = 0; i < _numberOfQueues; i++) {
                    for (int j = 0; j < _numberOfDomains; j++) {
       
                    _requestsInQueues_PLPQPSD.add(new Integer[_numberOfQueues][_numberOfDomains]); 

                    }
                }
            }
    
    }

    // </editor-fold>
    
    
    // <editor-fold desc="Properties">
    public double getAvgDelayInQueue() {
        return _avgDelayInQueue;
    }
   
    public void setAvgDelayInQueue(double _avgDelayInQueue) {
        this._avgDelayInQueue = _avgDelayInQueue;
    }

    public double[] getAvgDelayInQueue_PL() {
        return _avgDelayInQueue_PL;
    }

    public void setAvgDelayInQueue_PL(double[] _avgDelayInQueue_PL) {
        this._avgDelayInQueue_PL = _avgDelayInQueue_PL;
    }

    public double[][] getAvgDelayInQueue_PLPSD() {
        return _avgDelayInQueue_PLPSD;
    }

    public void setAvgDelayInQueue_PLPSD(double[][] _avgDelayInQueue_PLPSD) {
        this._avgDelayInQueue_PLPSD = _avgDelayInQueue_PLPSD;
    }

    public double[] getAvgDelayInQueue_PSD() {
        return _avgDelayInQueue_PSD;
    }

    public void setAvgDelayInQueue_PSD(double[] _avgDelayInQueue_PSD) {
        this._avgDelayInQueue_PSD = _avgDelayInQueue_PSD;
    }

    public double getAvgDelayInService() {
        return _avgDelayInService;
    }

    public void setAvgDelayInService(double _avgDelayInSys) {
        this._avgDelayInService = _avgDelayInSys;
    }

    public double[] getAvgDelayInSys_PL() {
        return _avgDelayInService_PL;
    }

    public void setAvgDelayInSys_PL(double[] _avgDelayInSys_PL) {
        this._avgDelayInService_PL = _avgDelayInSys_PL;
    }

    public double[][] getAvgDelayInSys_PLPSD() {
        return _avgDelayInService_PLPSD;
    }

    public void setAvgDelayInSys_PLPSD(double[][] _avgDelayInSys_PLPSD) {
        this._avgDelayInService_PLPSD = _avgDelayInSys_PLPSD;
    }

    public double[] getAvgDelayInService_PSD() {
        return _avgDelayInService_PSD;
    }

    public void setAvgDelayInService_PSD(double[] _avgDelayInSys_PSD) {
        this._avgDelayInService_PSD = _avgDelayInSys_PSD;
    }

  

    public String getNodeType() {
        return _nodeType;
    }

    public void setNodeType(String _nodeType) {
        this._nodeType = _nodeType;
    }

    public int getNumberOfDomains() {
        return _numberOfDomains;
    }

    public void setNumberOfDomains(int _numberOfDomains) {
        this._numberOfDomains = _numberOfDomains;
    }

    public int getNumberOfLevels() {
        return _numberOfLevels;
    }

    public void setNumberOfLevels(int _numberOfLevels) {
        this._numberOfLevels = _numberOfLevels;
    }

    public int getNumberOfQueues() {
        return _numberOfQueues;
    }

    public void setNumberOfQueues(int _numberOfQueues) {
        this._numberOfQueues = _numberOfQueues;
    }

    public int getRequestArrivals() {
        return _requestArrivals;
    }

    public void setRequestArrivals(int _requestArrivals) {
        this._requestArrivals = _requestArrivals;
    }

    public int[] getRequestArrivals_PL() {
        return _requestArrivals_PL;
    }

    public void setRequestArrivals_PL(int[] _requestArrivals_PL) {
        this._requestArrivals_PL = _requestArrivals_PL;
    }

    public int[][] getRequestArrivals_PLPSD() {
        return _requestArrivals_PLPSD;
    }

    public void setRequestArrivals_PLPSD(int[][] _requestArrivals_PLPSD) {
        this._requestArrivals_PLPSD = _requestArrivals_PLPSD;
    }

    public int[] getRequestArrivals_PSD() {
        return _requestArrivals_PSD;
    }

    public void setRequestArrivals_PSD(int[] _requestArrivals_PSD) {
        this._requestArrivals_PSD = _requestArrivals_PSD;
    }

    public int getRequestsDelayed() {
        return _requestsDelayed;
    }

    public void setRequestsDelayed(int _requestsDelayed) {
        this._requestsDelayed = _requestsDelayed;
    }

    public int[] getRequestsDelayed_PL() {
        return _requestsDelayed_PL;
    }

    public void setRequestsDelayed_PL(int[] _requestsDelayed_PL) {
        this._requestsDelayed_PL = _requestsDelayed_PL;
    }

    public int[][] getRequestsDelayed_PLPSD() {
        return _requestsDelayed_PLPSD;
    }

    public void setRequestsDelayed_PLPSD(int[][] _requestsDelayed_PLPSD) {
        this._requestsDelayed_PLPSD = _requestsDelayed_PLPSD;
    }

    public int[] getRequestsDelayed_PSD() {
        return _requestsDelayed_PSD;
    }

    public void setRequestsDelayed_PSD(int[] _requestsDelayed_PSD) {
        this._requestsDelayed_PSD = _requestsDelayed_PSD;
    }

    public int getRequestsInQueues() {
        return _requestsInQueues;
    }

    public void setRequestsInQueues(int _requestsInQueues) {
        this._requestsInQueues = _requestsInQueues;
    }

    public int[] getRequestsInQueues_PL() {
        return _requestsInQueues_PL;
    }

    public void setRequestsInQueues_PL(int[] _requestsInQueues_PL) {
        this._requestsInQueues_PL = _requestsInQueues_PL;
    }

    public ArrayList<Integer[][]> getRequestsInQueues_PLPQPSD() {
        return _requestsInQueues_PLPQPSD;
    }

    public void setRequestsInQueues_PLPQPSD(ArrayList<Integer[][]> _requestsInQueues_PLPQPSD) {
        this._requestsInQueues_PLPQPSD = _requestsInQueues_PLPQPSD;
    }

    public int[][] getRequestsInQueues_PLPSD() {
        return _requestsInQueues_PLPSD;
    }

    public void setRequestsInQueues_PLPSD(int[][] _requestsInQueues_PLPSD) {
        this._requestsInQueues_PLPSD = _requestsInQueues_PLPSD;
    }

    public int[] getRequestsInQueues_PSD() {
        return _requestsInQueues_PSD;
    }

    public void setRequestsInQueues_PSD(int[] _requestsInQueues_PSD) {
        this._requestsInQueues_PSD = _requestsInQueues_PSD;
    }

    public int getRequestsRejected() {
        return _requestsRejected;
    }

    public void setRequestsRejected(int _requestsRejected) {
        this._requestsRejected = _requestsRejected;
    }

    public int[] getRequestsRejected_PL() {
        return _requestsRejected_PL;
    }

    public void setRequestsRejected_PL(int[] _requestsRejected_PL) {
        this._requestsRejected_PL = _requestsRejected_PL;
    }

    public int[][] getRequestsRejected_PLPSD() {
        return _requestsRejected_PLPSD;
    }

    public void setRequestsRejected_PLPSD(int[][] _requestsRejected_PLPSD) {
        this._requestsRejected_PLPSD = _requestsRejected_PLPSD;
    }

    public int[] getRequestsRejected_PSD() {
        return _requestsRejected_PSD;
    }

    public void setRequestsRejected_PSD(int[] _requestsRejected_PSD) {
        this._requestsRejected_PSD = _requestsRejected_PSD;
    }

    public int getRequestsServed() {
        return _requestsServed;
    }

    public void setRequestsServed(int _requestsServed) {
        this._requestsServed = _requestsServed;
    }

    public int[] getRequestsServed_PL() {
        return _requestsServed_PL;
    }

    public void setRequestsServed_PL(int[] _requestsServed_PL) {
        this._requestsServed_PL = _requestsServed_PL;
    }

    public int[][] getRequestsServed_PLPSD() {
        return _requestsServed_PLPSD;
    }

    public void setRequestsServed_PLPSD(int[][] _requestsServed_PLPSD) {
        this._requestsServed_PLPSD = _requestsServed_PLPSD;
    }

    public int[] getRequestsServed_PSD() {
        return _requestsServed_PSD;
    }

    public void setRequestsServed_PSD(int[] _requestsServed_PSD) {
        this._requestsServed_PSD = _requestsServed_PSD;
    }
    
    
    // </editor-fold>

    public int getNumberOfSchedulingRounds() {
        return _numberOfSchedulingRounds;
    }

    public void setNumberOfSchedulingRounds(int _numberOfSchedulingRounds) {
        this._numberOfSchedulingRounds = _numberOfSchedulingRounds;
    }

    public long[][] getRequestsEnqueued_PQPSD() {
        return _requestsEnqueued_PQPSD;
    }

    
    
    
    
}
