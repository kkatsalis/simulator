/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package BL;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;



/**
 *
 * @author Kostas Katsalis
 */

public class Sla {

    int _serviceDomainsNumber;
    double[] _cpuUtilization;
  
    
    Configuration _config;
            
    public Sla(Configuration _config) {
        
        this._config=_config;
        
        this._serviceDomainsNumber=_config.getNumberOfDomains();
       
        this._cpuUtilization=new double[_config.getNumberOfDomains()];
        
        
        for (int i = 0; i < _config.getNumberOfDomains(); i++) {
        	
            _cpuUtilization[i]=(double)1/_config.getNumberOfDomains();
            
          
        }
    }
    
     
     public void SLAConfiguration(int serviceDomainID, double value)
     {
        _cpuUtilization[serviceDomainID]=value;
     }
 
     public double[] getCpuUtilizationSLA() {
        return _cpuUtilization;
    }

     
   
     public void SLAConfiguration()
     {
         
      
        Properties property = new Properties();
	InputStream input = null;    
            
        try {
 
            String filename = "simulation.properties";
            input = Configuration.class.getClassLoader().getResourceAsStream(filename);
            
        
            // load a properties file
            property.load(input);

            String parameter="";
            double value=-1;
        
            // SLA
            for (int i = 0; i < _serviceDomainsNumber; i++) {
                parameter="sla"+i;
                value=Double.valueOf((String)property.getProperty(parameter));
               
                
                _cpuUtilization[i]=value;
         }
       
//        int  temp=_config.getNumberOfDomains();
//        double value=(double)1/temp;
//        
//        for (int i = 0; i <_config.getNumberOfDomains();  i++) {
//          _simulator.getDataCenter().getController().getGlobalSla().SLAConfiguration(i,(double)value);
//        }
            
      
         } catch (IOException ex) {
		ex.printStackTrace();
	} finally {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
        

        
        
     }
    
}
