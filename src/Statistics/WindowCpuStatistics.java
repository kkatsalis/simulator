/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Statistics;


import BL.Configuration;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Kostas Katsalis
 */
public class WindowCpuStatistics {

    int _numberOfDomains;
    double   _idleTime;
    double   _absTotalDeviation;
    
    double[] _deviationPSD;   
     
    double   _cpuTimeOfOperation;
    List<double[]> _cpuTimeOfOperation_PSD;     //PSD: per service domain
    List<double[]> _windowAreaPSD;     //PSD: per service domain
    List<Double> _roundStartTime;     //PSD: per service domain
    
    double[] _cpuUtilization_PSD;
    double[] _timeMeasurementPSD;
    
    double _lastMeasurementTime;
   
    int _runningRound=0;
    int windowSize;
    
    
   // <editor-fold desc="Constructor">
   public WindowCpuStatistics(int numberOfDomains, int windowSize)
    {
       this._numberOfDomains=numberOfDomains;
       this.windowSize=windowSize;  

         Initialize();      
    }
  // </editor-fold>
    
   private void Initialize() {
 
     _idleTime=0;
     _cpuTimeOfOperation=0;
     _absTotalDeviation=0;
         
     _deviationPSD=new double[_numberOfDomains];   
     _cpuTimeOfOperation_PSD=new ArrayList<>();     //PSD: per service domain
      _windowAreaPSD=new ArrayList<>();     //PSD: per service domain
     _cpuUtilization_PSD=new double[_numberOfDomains]; 
     _timeMeasurementPSD=new double[_numberOfDomains]; 
     
     _roundStartTime=new ArrayList<>();
     _roundStartTime.add(0.0);
     
        for (int i = 0; i < _numberOfDomains; i++) {
            _deviationPSD[i]=0;
            _cpuUtilization_PSD[i]=0;
            _timeMeasurementPSD[i]=0;
        }
    }

   private void updateUtilization(double lastMeasurementTime){
       
       double duration=0;
       double windowStartTime=_roundStartTime.get(0);
           
            duration=lastMeasurementTime-windowStartTime;
       
           for (int i = 0; i < _numberOfDomains; i++) {
                  _cpuUtilization_PSD[i]=0;
           }

           for (int i = 0; i <_cpuTimeOfOperation_PSD.size(); i++) {
               for (int j = 0; j < _numberOfDomains; j++) {
                   _cpuUtilization_PSD[j]+=_cpuTimeOfOperation_PSD.get(i)[j];
                  // duration+=_cpuTimeOfOperation_PSD.get(i)[j];
               }
           }   

           if(_cpuTimeOfOperation_PSD.size()>0)
             
               for (int j = 0; j < _numberOfDomains; j++) {
                _cpuUtilization_PSD[j]=_cpuUtilization_PSD[j]/duration;
               
         //       System.out.print(_cpuUtilization_PSD[j]+"-");
              
               
               }
          // System.out.println();
           

   }
    public void addMeasurement(int signaledRound,int domainID, double measurement, double measurementTime){
    
        if(signaledRound==_runningRound){
            _timeMeasurementPSD[domainID]+=measurement;
        }
        
        if( _runningRound<signaledRound){
        
            if(_cpuTimeOfOperation_PSD.size()>=windowSize){
                _roundStartTime.remove(0);
               
                _cpuTimeOfOperation_PSD.remove(0);
                
            }
        
            _cpuTimeOfOperation_PSD.add(_timeMeasurementPSD);
            _roundStartTime.add(measurementTime);
           
            updateUtilization(_lastMeasurementTime);
            
            _timeMeasurementPSD=new double[_numberOfDomains];
            
            for (int i = 0; i <_numberOfDomains ; i++) {
                _timeMeasurementPSD[i]=0;
            }
             
            _timeMeasurementPSD[domainID]+=measurement;
            
             _runningRound=signaledRound;
             _lastMeasurementTime=measurementTime;
        }
    }
   
    private double area(double x, double y1, double y2){
    
        double area=0;
    
        area=x*(y1/2+y2/2);
                
        return area;
    }
   
   
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

    public List<double[]> getCpuTimeOfOperation_PSD() {
        return _cpuTimeOfOperation_PSD;
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

  
    public int getNumberOfDomains() {
        return _numberOfDomains;
    }

    public void setNumberOfDomains(int _numberOfDomains) {
        this._numberOfDomains = _numberOfDomains;
    }


    public double[] getDeviationPSD() {
        return _deviationPSD;
    }

    public void setDeviationPSD(double[] _totalDeviationPSD) {
        this._deviationPSD = _totalDeviationPSD;
    }
    
    
    
}
