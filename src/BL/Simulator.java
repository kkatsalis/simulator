/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package BL;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import Events.*;


/**
 *
 * @author Kostas Katsalis
 */
public class Simulator {

   Configuration _config;
   
    Tracker _tracker;
    EventMonitor _eventMonitor;
    EventHandler _eventHandler;
    DataCenter _dataCenter;
   
    Sla _sla;
              
    List<Double> templistChecker;

    
    public Simulator(Configuration simConfiguration) {
    
        this._config=simConfiguration;
        this._sla=new Sla(_config);
        this._tracker=new Tracker();
        this._eventMonitor=new EventMonitor(_config);
     
        this._dataCenter=new DataCenter(0,_config,_tracker,_eventMonitor,_sla);
        this._eventHandler=new EventHandler(_dataCenter, _eventMonitor, _config, _tracker);
        this.templistChecker=new ArrayList<Double>();
    }

   
     public void ConfigureSimulator() throws Exception
     {
         _dataCenter.createNetworkController(_sla);
        
         _dataCenter.CreateNetwork();
        
         _dataCenter.createInitialRouterArrivalEvents();
         
         _dataCenter.createInitialServiceEvents();
         
         // Create exported Files
         _dataCenter.getController().getStatisticsExporter().CreateExportedFiles();
         //   _config.CreateXMLConfigFile();
     }

     public void Run() throws IOException{
    
        Event eventPointer;
       
          while (_tracker.runningEventTime < _config.getSimulationTime())
          {
            if(CheckList())
            {
               
                eventPointer=_eventMonitor.getEventList().get(0);

                _eventHandler.EventHandingMethod(eventPointer);      
                _eventMonitor.getEventList().remove(eventPointer);
               
               _tracker.runningEventTime = _eventMonitor.getEventList().get(0).getTimeOfOccurance();
               
            //  _dataCenter.getController().CheckConvergence();
              
              }
            else
            {
                System.out.println("No ordered event list");
            }
          }
         
        
    
        try 
        {
           _dataCenter.getController().getStatisticsExporter().CloseFiles();
        } 
        catch (Exception e) {
            
        }
       
    }
   
     private boolean CheckList() {
       
        templistChecker.clear();
        
       for (int i = 0; i < _eventMonitor.getEventList().size(); i++) {
         templistChecker.add(_eventMonitor.getEventList().get(i).getTimeOfOccurance());
         }
      
        boolean sorted = isSorted(templistChecker);

        return sorted;
       
    }
    
    public static <T extends Comparable> boolean isSorted(List<T> listOfT) {
    T previous = null;
    for (T t: listOfT) {
        if (previous != null && t.compareTo(previous) < 0) return false;
        previous = t;
    }
    return true;
}

    

    public DataCenter getDataCenter() {
        return _dataCenter;
    }

    public Sla getSla() {
        return _sla;
    }

    


}

