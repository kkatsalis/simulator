/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Statistics;


import BL.Configuration;


/**
 *
 * @author Kostas Katsalis
 */
public class CpuStatistics {

    int _numberOfDomains;
    int _numberOfLevels;
    int _numberOfQueues;

    double   _idleTime;
    double[]   _idleTime_PL;
    
    double   _absTotalDeviation;
    double[] _deviationPSD;   
     
    double   _cpuTimeOfOperation;
    double[] _cpuTimeOfOperation_PL;     //PSD: per service domain
    double[] _cpuTimeOfOperation_PSD;     //PSD: per service domain
    double[][] _cpuTimeOfOperation_PLPSD;     //PSD: per service domain
    
    double[] _cpuUtilization_PL;
    double[] _cpuUtilization_PSD;
    double[][] _cpuUtilization_PLPSD;
    
   // <editor-fold desc="Constructor">
   public CpuStatistics(int numberOfDomains, int numberOfLevels)
    {
       _numberOfDomains=numberOfDomains;
       _numberOfLevels=numberOfLevels;    

         CreateArrays();      
         InitialValues();  
    }
  // </editor-fold>
    
   // <editor-fold desc="Private methods">
   private void CreateArrays() {
 
       _idleTime_PL=new double[_numberOfLevels];
     
       _deviationPSD=new double[_numberOfDomains];   
       _cpuTimeOfOperation_PL=new double[_numberOfLevels];     //PSD: per service domain
       _cpuTimeOfOperation_PSD=new double[_numberOfDomains];     //PSD: per service domain
       _cpuTimeOfOperation_PLPSD=new double[_numberOfLevels][_numberOfDomains];      //PSD: per service domain
       _cpuUtilization_PL=new double[_numberOfLevels]; 
       _cpuUtilization_PSD=new double[_numberOfDomains]; 
       _cpuUtilization_PLPSD=new double[_numberOfLevels][_numberOfDomains]; 
    }

   private void InitialValues() {
         
          _idleTime=0;
         
          _cpuTimeOfOperation=0;
        
          _absTotalDeviation=0;
        
          
          
           for (int i = 0; i < _numberOfDomains; i++) {
     
            
              _deviationPSD[i]=0;
              _cpuTimeOfOperation_PSD[i]=0;
              _cpuUtilization_PSD[i]=0;

           }
           
           for (int i = 0; i < _numberOfLevels; i++) {
             _idleTime_PL[i]=0;
          
             _cpuTimeOfOperation_PL[i]=0;
             _cpuUtilization_PL[i]=0;
             
           }
           
             
            for (int i = 0; i < _numberOfLevels; i++) {
                for (int j = 0; j < _numberOfDomains; j++) {
            
               _cpuTimeOfOperation_PLPSD[i][j]=0;
               _cpuUtilization_PLPSD[i][j]=0; 

                }
            }
          
    }
  // </editor-fold>

    public double getAbsTotalDeviation() {
        return _absTotalDeviation;
    }

    public void setAbsTotalDeviation(double _absTotalDeviation) {
        this._absTotalDeviation = _absTotalDeviation;
    }

   

   

    public double getCpuTimeOfOperation() {
        return _cpuTimeOfOperation;
    }

    public void setCpuTimeOfOperation(double _cpuTimeOfOperation) {
        this._cpuTimeOfOperation = _cpuTimeOfOperation;
    }

    public double[] getCpuTimeOfOperation_PL() {
        return _cpuTimeOfOperation_PL;
    }

    public void setCpuTimeOfOperation_PL(double[] _cpuTimeOfOperation_PL) {
        this._cpuTimeOfOperation_PL = _cpuTimeOfOperation_PL;
    }

    public double[][] getCpuTimeOfOperation_PLPSD() {
        return _cpuTimeOfOperation_PLPSD;
    }

    public void setCpuTimeOfOperation_PLPSD(double[][] _cpuTimeOfOperation_PLPSD) {
        this._cpuTimeOfOperation_PLPSD = _cpuTimeOfOperation_PLPSD;
    }

    public double[] getCpuTimeOfOperation_PSD() {
        return _cpuTimeOfOperation_PSD;
    }

    public void setCpuTimeOfOperation_PSD(double[] _cpuTimeOfOperation_PSD) {
        this._cpuTimeOfOperation_PSD = _cpuTimeOfOperation_PSD;
    }

    public double[] getCpuUtilization_PL() {
        return _cpuUtilization_PL;
    }

    public void setCpuUtilization_PL(double[] _cpuUtilization_PL) {
        this._cpuUtilization_PL = _cpuUtilization_PL;
    }

    public double[][] getCpuUtilization_PLPSD() {
        return _cpuUtilization_PLPSD;
    }

    public void setCpuUtilization_PLPSD(double[][] _cpuUtilization_PLPSD) {
        this._cpuUtilization_PLPSD = _cpuUtilization_PLPSD;
    }

    public double[] getCpuUtilization_PSD() {
        return _cpuUtilization_PSD;
    }

    public void setCpuUtilization_PSD(double[] _cpuUtilization_PSD) {
        this._cpuUtilization_PSD = _cpuUtilization_PSD;
    }

    public double getIdleTime() {
        return _idleTime;
    }

    public void setIdleTime(double _idleTime) {
        this._idleTime = _idleTime;
    }

    public double[] getIdleTime_PL() {
        return _idleTime_PL;
    }

    public void setIdleTime_PL(double[] _idleTime_PL) {
        this._idleTime_PL = _idleTime_PL;
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


    public double[] getDeviationPSD() {
        return _deviationPSD;
    }

    public void setDeviationPSD(double[] _totalDeviationPSD) {
        this._deviationPSD = _totalDeviationPSD;
    }
    
    
    
}
