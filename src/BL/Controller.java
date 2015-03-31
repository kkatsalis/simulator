/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Enumerators.EAlgorithmScheduling;
import Enumerators.ENodeType;
import Events.EventMonitor;
import Statistics.StatisticsExporter;
import Statistics.ApplianceTierStatistics;
import javax.xml.stream.XMLStreamException;


/**
 *
 * @author maevious
 */
public class Controller {
    
    Configuration _config;
    DataCenter _dataCenter;
    EventMonitor _eventMonitor;
    Tracker _tracker;
    Sla _globalSla;
  
    ApplianceTierStatistics _appsTierStatistics;
    StatisticsExporter _statisticsExporter;
  
    int _currentLevelID;
    int _counterInStatsExport=0;
    int _counterInConvergenceTest=0;
    
    double[] previusUtil;
    double[] currentUtil;
   
    
    boolean finalConvergence=false;
    
    List<Integer> _applianceNeedsRequest;
   
    int _runningRound=0;
    
    
    public Controller(Configuration _config, DataCenter _dataCenter, EventMonitor _eventMonitor, Tracker _tracker,Sla sla) {
     
        this._config = _config;
        this._dataCenter = _dataCenter;
        this._eventMonitor = _eventMonitor;
        this._tracker = _tracker;
        this._globalSla = sla;
        this._currentLevelID=0;
        this._appsTierStatistics=new ApplianceTierStatistics(_config, _dataCenter,ENodeType.Appliance.toString() );
        
        this._statisticsExporter=new StatisticsExporter(_dataCenter,_config, _tracker, null, _appsTierStatistics);
        this._applianceNeedsRequest=new ArrayList<Integer>();
        
        
        createNecessaryArrays();
    }
     
    private void createNecessaryArrays()
    {
	   //Not Used by all algorithms
	  
	   
	   this.previusUtil=new double[_config.getNumberOfDomains()];
       this.currentUtil=new double[_config.getNumberOfDomains()];
       
	   for (int i = 0; i < _config.getNumberOfDomains(); i++) {
           
            previusUtil[i]=0;
            currentUtil[i]=0;
        }
           
       
           
    }
    
    public void RunControl()
    {   
          updateTierStatistics(ENodeType.Appliance.toString());
          
          try {
               ExportTierStatistics();
        } 
          catch (Exception e) {
      
              System.out.print(e.getStackTrace());
          }
       
       
       
        
    }
    
    
    
    private void updateTierStatistics(String tierType)
    {
    	if(tierType.equals(ENodeType.Appliance.toString())){
    		_appsTierStatistics.UpdateStatistics();
    		_appsTierStatistics.UpdateCpuStatistics();
    	}	
    	
        
    }
 
    
   
     public int findDomainWithMaxUtilizationAboveGoal(String tierType){
        
    	 
        List<Double> _deviationList=new ArrayList<Double>();
        
        double tempDeviation;
        double maxSatisfiedDeviation;
        
    	if(tierType.equals(ENodeType.Appliance.toString())){
        
    		for (int i = 0; i < _config.getNumberOfDomains(); i++) {
    			tempDeviation=_appsTierStatistics.getCpuUtilization_PSD()[i]-_globalSla.getCpuUtilizationSLA()[i];
    			_deviationList.add(tempDeviation);
            } 
        
    		maxSatisfiedDeviation=Collections.max(_deviationList);
        
    		// Find the most suffering and the most satisfied SD
    		for (int i = 0; i < _config.getNumberOfDomains(); i++) {
    			tempDeviation=_appsTierStatistics.getCpuStatistics().getCpuUtilization_PSD()[i]-_globalSla.getCpuUtilizationSLA()[i];  
           
    			if(maxSatisfiedDeviation==tempDeviation){
    				return i;
    			}
    		}
    	}
    	return -1;
     }
    
    public int findDomainWithMinUtilizationBelowGoal(String tierType){
        
        List<Double> _deviationList=new ArrayList<Double>();
        
        double tempDeviation;
        double minSatisfiedDeviation;
        
        if(tierType.equals(ENodeType.Appliance.toString())){
        
        	for (int i = 0; i < _config.getNumberOfDomains(); i++) {
        		tempDeviation=_appsTierStatistics.getCpuStatistics().getCpuUtilization_PSD()[i]-_globalSla.getCpuUtilizationSLA()[i];
        		
        		_deviationList.add(tempDeviation);
            } 
        
        	minSatisfiedDeviation=Collections.min(_deviationList);
        
        	// Find the most suffering and the most satisfied SD
        	for (int i = 0; i < _config.getNumberOfDomains(); i++) {
        		tempDeviation=_appsTierStatistics.getCpuStatistics().getCpuUtilization_PSD()[i]-_globalSla.getCpuUtilizationSLA()[i];  
            
                if(minSatisfiedDeviation==tempDeviation){
                       return i;
                   }
           }
        }
        
        return -1;
        
     }
    
    
    
    public List<Integer> findAllDomainsAboveGoal()
    {
        // satisfaction = {under,over}
        List<Integer> _listOfDomainsAboveGoal=new ArrayList<Integer>();
    
        
        double tempDeviation;
        
        for (int i = 0; i < _config.getNumberOfDomains(); i++) {
            
            tempDeviation=_appsTierStatistics.getCpuStatistics().getCpuUtilization_PSD()[i]-_globalSla.getCpuUtilizationSLA()[i];
            
            if(tempDeviation>0)
               _listOfDomainsAboveGoal.add(i);
          
            } 
        
           return _listOfDomainsAboveGoal;
      
    }
   
    public List<Integer> findAllDomainsBelowGoal()
    {
        // satisfaction = {under,over}
        
        List<Integer> _listOfDomainsBelowGoal=new ArrayList<Integer>();
        
        double tempDeviation;
        
        for (int i = 0; i < _config.getNumberOfDomains(); i++) {
            tempDeviation=_appsTierStatistics.getCpuStatistics().getCpuUtilization_PSD()[i]-_globalSla.getCpuUtilizationSLA()[i];
            if(tempDeviation<0)
               _listOfDomainsBelowGoal.add(i);
            
            } 

           return _listOfDomainsBelowGoal;
    }
    
   
    
   

	public void ExportTierStatistics() throws XMLStreamException {
       
    	if((int)_tracker.getRunningEventTime()%_config.getStatsDensity()==0 & _counterInStatsExport!=(int)_tracker.getRunningEventTime()){
         if(_counterInStatsExport!=(int)_tracker.getRunningEventTime()){   
             _counterInStatsExport=(int)_tracker.getRunningEventTime();
          
            _statisticsExporter.ExportStatistics();

            }
        }
     
    }
	
    
 
 
 
 
 public Sla getGlobalSla() {
     return _globalSla;
 }
    public StatisticsExporter getStatisticsExporter() {
        return _statisticsExporter;
    }

    public DataCenter getDataCenter() {
        return _dataCenter;
    }

    public int getCurrentLevelID() {
        return _currentLevelID;
    }
 
    public void setCurrentLevelID(int _currentLevelID) {
        this._currentLevelID = _currentLevelID;
    }
// </editor-fold>

    public ApplianceTierStatistics getAppsTierStatistics() {
        return _appsTierStatistics;
    }

	public Tracker getTracker() {
		return _tracker;
	}

	public boolean isFinalConvergence() {
		return finalConvergence;
	}

    public EventMonitor getEventMonitor() {
        return _eventMonitor;
    }

    public List<Integer> getApplianceNeedsRequest() {
        return _applianceNeedsRequest;
    }

    public int getRunningRound() {
        return _runningRound;
    }

    public void setRunningRound(int runningRound) {
        this._runningRound = runningRound;
    }

    

   

	
    
	
  
   }   
    
    
