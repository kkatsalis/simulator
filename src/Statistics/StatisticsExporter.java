/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Statistics;

import java.io.FileOutputStream;
import java.io.FileWriter;

import java.io.IOException;
import java.util.Date;

import BL.Configuration;
import BL.DataCenter;
import BL.Tracker;
import Enumerators.ENodeConfig;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
/**
 *
 * @author maevious
 */
public class StatisticsExporter {
    

    
   FileWriter appTierCpuUtilization;
   
    XMLOutputFactory outputFactory;
    XMLEventWriter eventWriter;
    XMLEventFactory eventFactory;
    
    DataCenter _dataCenter;
    Configuration _config;
    ApplianceTierStatistics _appsTierStatistics;
    
    Tracker _tracker;
     
    
    public StatisticsExporter(DataCenter _dataCenter,Configuration _config, Tracker _tracker,ApplianceTierStatistics _rtrTierStatistics,ApplianceTierStatistics _appsTierStatistics) {

        this._dataCenter=_dataCenter;
        this._config = _config;
        this._tracker=_tracker;
        this._appsTierStatistics=_appsTierStatistics;
        
    }
    
    public void CreateExportedFiles()
    {
  
     try {
 
         LoadAppsTierStatistics();
       
       //   System.out.println("Exported Files: Done");
         //     LoadXMLFile();
        } 
        catch (Exception e){ 
            System.out.print(e.getStackTrace());
        }
    
    }
       
   
     private void LoadAppsTierStatistics() {
       
       String fileName=_config.getFileName();      
                
       try {
          
            appTierCpuUtilization = new FileWriter(fileName);
           
            appTierCpuUtilization.append("Time");
            
            for (int i = 0; i < _config.getNumberOfDomains(); i++) {
             appTierCpuUtilization.append("&Domain"+String.valueOf(i));
           }
            appTierCpuUtilization.append("&Deviation");
            
        } catch (Exception e) {
        }
    }
  
   
    private void OutputApplianceTierStatistics(){

        double utilization=0;
        double deviation=0;
        double goal=0;
    	
    	try {
            
            appTierCpuUtilization.append('\n');
            appTierCpuUtilization.append(String.valueOf(_tracker.getRunningEventTime()));appTierCpuUtilization.append('&');
            
            for (int i = 0; i <_config.getNumberOfDomains(); i++) {
              System.out.print("-"+_dataCenter.getRouters().get(0).getQueue()[i].size());
                
              utilization=(_appsTierStatistics.getCpuTimeOfOperation_PSD()[i]/(_config.getNumberOfAppliances()*_tracker.getRunningEventTime()));
                goal=_dataCenter.getController().getGlobalSla().getCpuUtilizationSLA()[i];
                appTierCpuUtilization.append(Double.toString(utilization));appTierCpuUtilization.append('&');
                deviation+=Math.abs(utilization-goal);
                
            }
            System.out.println();
            appTierCpuUtilization.append(Double.toString(deviation));appTierCpuUtilization.append('&');
            appTierCpuUtilization.flush();
          //  clusterPerDomainWriter.append("\n\n");
        } catch (Exception e) {
        }
    }
   
  

   
    public void ExportStatistics() {
        try {
            
          OutputApplianceTierStatistics();
          
        } catch (Exception e) {
        }
       
     

    }
     
   public void CloseFiles()
   {
        try {
          appTierCpuUtilization.close();
           
                
       } 
       
        catch (Exception e) {
       }
           
   } 
     
    
}
