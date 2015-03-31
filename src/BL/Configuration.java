/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL;

import java.io.FileOutputStream;
import Enumerators.*;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
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
public class Configuration {
        
    //**************************
    // A. Basic Configuration
    //**************************
    String fileName;

    String _simulationID;
    Boolean _generateSaturatedConditions;

    double _simulationTime;
    
    int _numberOfDataCenters; 
    int _numberOfLevels;
    int _numberOfDomains;
    int _numberOfRouters;
    int _numberOfAppliances;
    int _numberOfServers;
    int _numberOfWebRequestGenerators;
    String _webRequestGeneratorsType;
    String _linkBandwidth;
    int _maxRequestSize;
    
    int _borderNodeID;
    double cpuPercentageAllShare;
    int _windowSize;
    



	//**************************
    // B. Convergence Criteria
    //**************************
    double _converganceCounterLimit; 
    double _convergencePlusMinCriterion;
    double _convergenceTime;
   
    double _additionalSimulationTime=5000;
       int _statsDensity;
    //**************************
    // C. Control Events
    //**************************
    String _controlOperationType;
    String _finalServiceTier;
    int _numberOfControlEvents;
    int _schedulinDelayPeriod;
    
    HashMap[] _requestArrivalTimeConfiguration;
    
    //**************************
    // D. Node Configuration
    //**************************
    
    HashMap _applianceConfiguration;
    HashMap[] _applianceServiceTimeConfiguration;
   
    HashMap _routerConfiguration;
    HashMap[] _routerServiceTimeConfiguration;

    //**************************
    // D. Burst Configuration (exponential)
    //**************************
    
    HashMap[] _onPeriodConfiguration;
    HashMap[] _offPeriodConfiguration;

    public Configuration() {
        
     _applianceConfiguration=new HashMap();
     _routerConfiguration=new HashMap();
     
    }
    public void CreateHashMaps(){
    
    _routerServiceTimeConfiguration=new HashMap[_numberOfDomains];
    _applianceServiceTimeConfiguration=new HashMap[_numberOfDomains];
    _requestArrivalTimeConfiguration=new HashMap[_numberOfDomains];
    _onPeriodConfiguration=new HashMap[_numberOfDomains];
    _offPeriodConfiguration=new HashMap[_numberOfDomains];
    
        for (int i = 0; i < _numberOfDomains; i++) {
            _requestArrivalTimeConfiguration[i]=new HashMap();
            _routerServiceTimeConfiguration[i]=new HashMap();
            _applianceServiceTimeConfiguration[i]=new HashMap();
            
            _onPeriodConfiguration[i]=new HashMap();
            _offPeriodConfiguration[i]=new HashMap();        
        }
    
    }
    
    private void CheckRouterConfiguration()
    {	    	
    	String _enqueuingAlgorithm=_routerConfiguration.get("EnQueueingAlgorithm").toString();
    	int queuesNumber=-1;
    	
    	if(_enqueuingAlgorithm.equals(EAlgorithmEnQueueing.DomainPerQueue.toString()))
    	{
    		queuesNumber=_numberOfDomains;
    	}
    	else if(_enqueuingAlgorithm.equals(EAlgorithmEnQueueing.Random.toString())||
        	    _enqueuingAlgorithm.equals(EAlgorithmEnQueueing.RoundRobin.toString())||
    	     _enqueuingAlgorithm.equals(EAlgorithmEnQueueing.CentralizedBucket.toString())||
    	    _enqueuingAlgorithm.equals(EAlgorithmEnQueueing.DistributedBucket.toString())){
    		
    		queuesNumber=( ( Integer )_routerConfiguration.get("QueuesNumber")).intValue();
    		 
    	 }
    	else if ( _enqueuingAlgorithm.equals(EAlgorithmEnQueueing.DomainPerQueuePlusBucket.toString()))
    	{
    		queuesNumber=_numberOfDomains+1; 
    	}		 
    	
    	_routerConfiguration.put("QueuesNumber",queuesNumber);
    	
    }
    
    private void CheckApplianceConfiguration()
    {	    	
    	String _enqueuingAlgorithm=_applianceConfiguration.get("EnQueueingAlgorithm").toString();
    	int queuesNumber=-1;
    	
    	if(_enqueuingAlgorithm.equals(EAlgorithmEnQueueing.DomainPerQueue.toString()))
    	{
    		queuesNumber=_numberOfDomains;
    	}
    	else if(_enqueuingAlgorithm.equals(EAlgorithmEnQueueing.Random.toString())||
        	    _enqueuingAlgorithm.equals(EAlgorithmEnQueueing.RoundRobin.toString())||
        	    _enqueuingAlgorithm.equals(EAlgorithmEnQueueing.CentralizedBucket.toString())||
        	    _enqueuingAlgorithm.equals(EAlgorithmEnQueueing.DistributedBucket.toString())){
    		
    		queuesNumber=( ( Integer )_applianceConfiguration.get("QueuesNumber")).intValue();
    		 
    	 }
    	else if ( _enqueuingAlgorithm.equals(EAlgorithmEnQueueing.DomainPerQueuePlusBucket.toString()))
    	{
    		queuesNumber=_numberOfDomains+1; 
    	}	
    	
    	_applianceConfiguration.put("QueuesNumber",queuesNumber);
    	
    }
    
    public void CheckConfiguration(){
    	
    	CheckRouterConfiguration();
  //  	CheckApplianceConfiguration();
    	
    }
    
    
    
    //**************************
    // B. Output properties
    //**************************
   
    XMLOutputFactory outputFactory;
    XMLEventWriter eventWriter;
    XMLEventFactory eventFactory;
    

    // <editor-fold desc="Methods">
    
     
    public void XmlConfigFileClose() throws Exception
    {
        XMLEvent end = eventFactory.createDTD("\n");
        
        eventWriter.add(eventFactory.createEndElement("", "", "Configuration"));
	eventWriter.add(end);
	eventWriter.add(eventFactory.createEndDocument());
	eventWriter.close();
    }
    
    public void AddConfigToXML(Double time,double[] values) throws XMLStreamException {

                int _time=time.intValue();
        
		XMLEventFactory localEventFactory = XMLEventFactory.newInstance();
		XMLEvent end = localEventFactory.createDTD("\n");
		XMLEvent tab = localEventFactory.createDTD("\t");
                
                // Create Start node
		StartElement sElement = localEventFactory.createStartElement("", "", "Time");
		XMLEvent attributes = localEventFactory.createAttribute("moment",String.valueOf(_time));
                
                eventWriter.add(tab);
		eventWriter.add(sElement);
                eventWriter.add(attributes);
                
                StartElement sElementSub = localEventFactory.createStartElement("", "", "CPU");
		XMLEvent attributesSub; 
                EndElement eElement = localEventFactory.createEndElement("", "", "CPU");
                
		eventWriter.add(end);
                for (int i = 0; i < values.length; i++) {
                    attributesSub = localEventFactory.createAttribute("sd",String.valueOf(i));
                    
                   
                    eventWriter.add(tab);
                    eventWriter.add(tab);
                    eventWriter.add(sElementSub);
                    eventWriter.add(attributesSub);
                    
                    // Create Content
                    Characters characters = localEventFactory.createCharacters(String.valueOf(values[i]));
                    eventWriter.add(characters);
                      // Create End node
		
                    eventWriter.add(eElement);
                    eventWriter.add(end);
                }
                
                
		
                // Create End node
		EndElement eElementTime = localEventFactory.createEndElement("", "", "Time");
		eventWriter.add(tab);
                eventWriter.add(eElementTime);
		eventWriter.add(end);

                
                
                 
	}
    
   

    public void loadConfigurationParameters() {

        Configuration _config=this;
      
        Properties property = new Properties();
	InputStream input = null;    
            
        try {
 
    	String filename = "simulation.properties";
    	input = Configuration.class.getClassLoader().getResourceAsStream(filename);
            
        
        // load a properties file
        property.load(input);

        //String.valueOf((String)property.getProperty("controlOperationType"))
                
        _config.setSimulationID(Utilities.getDateTime());
        _config.setSimulationTime(Double.valueOf((String)property.getProperty("simulationTime")));
        _config.setAdditionalSimulationTime( Double.valueOf((String)property.getProperty("additionalSimulationTime")));
        _config.set_generateSaturatedConditions(Boolean.valueOf((String)property.getProperty("generateSaturatedConditions")));
       
        _config.setBorderNodeID(Integer.valueOf((String)property.getProperty("borderNodeID")));
        _config.setLinkBandwidth(EEdgeBadwidth.Gigabit.toString());
        _config.setNumberOfDataCenters(Integer.valueOf((String)property.getProperty("numberOfDataCenters")));
        _config.setNumberOfDomains( Integer.valueOf((String)property.getProperty("numberOfDomains")));
        _config.setNumberOfRouters(Integer.valueOf((String)property.getProperty("numberOfRouters")));
        _config.setNumberOfAppliances( Integer.valueOf((String)property.getProperty("numberOfAppliances")));
        _config.setNumberOfServers(Integer.valueOf((String)property.getProperty("numberOfServers")));
        
        _config.setControlOperationType(null);
        _config.setConvergencePlusMinCriterion(Double.valueOf((String)property.getProperty("convergencePlusMinCriterion")));
        _config.setConverganceCounterLimit(Double.valueOf((String)property.getProperty("converganceCounterLimit")));
        _config.setFinalServiceTier(String.valueOf((String)property.getProperty("finalServiceTier")));
        _config.setNumberOfControlEvents(Integer.valueOf((String)property.getProperty("numberOfControlEvents")));
        _config.setNumberOfLevels( Integer.valueOf((String)property.getProperty("numberOfLevels")));

         _config.setMaxRequestSize(Integer.valueOf((String)property.getProperty("maxRequestSize")));
         
        _config.setNumberOfWebRequestGenerators(Integer.valueOf((String)property.getProperty("numberOfWebRequestGenerators")));
        _config.setStatsDensity(Integer.valueOf((String)property.getProperty("statsDensity")));
        _config.setDelayBetweenSchedulingPeriods(Integer.valueOf((String)property.getProperty("delayBetweenSchedulingPeriods")));
        _config.setCpuPercentageAllShare(Double.valueOf((String)property.getProperty("cpuPercentageAllShare")));
 
        _config.setWindowSize(Integer.valueOf((String)property.getProperty("windowSize")));
        
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
    
    public void loadNodeConfigurationParameters(String schedulingAlgorithm){
    
        Configuration _config=this;
      
        Properties property = new Properties();
	InputStream input = null;    
            
        try {
 
    	String filename = "nodes.properties";
    	input = Configuration.class.getClassLoader().getResourceAsStream(filename);
            
        
        // load a properties file
        property.load(input);
    
           //////////////////////////////////////////////////
         //             Router Configuration
         //////////////////////////////////////////////////
        if(schedulingAlgorithm==null)
            _routerConfiguration.put(ENodeConfig.SchedulingAlgorithm.toString(), EAlgorithmScheduling.NonSaturatedBGP.toString());
        else 
            _routerConfiguration.put(ENodeConfig.SchedulingAlgorithm.toString(), schedulingAlgorithm);
     //     System.out.println("Scheduling Algorithm: "+_routerConfiguration.get(ENodeConfig.SchedulingAlgorithm.toString()));
         _routerConfiguration.put(ENodeConfig.NodeType.toString(),ENodeType.Router.toString()); 
         _routerConfiguration.put(ENodeConfig.EnQueueingAlgorithm.toString(), EAlgorithmEnQueueing.DomainPerQueue.toString());   
         _routerConfiguration.put(ENodeConfig.RoutingAlgorithm.toString(), EAlgorithmRouting.RoundRobin.toString());         
         _routerConfiguration.put(ENodeConfig.ServiceDistribution.toString(), EGeneratorType.Exponential.toString());
          
         _routerConfiguration.put(EQueueConfig.QueuesNumber.toString(),Integer.valueOf((String)property.getProperty("routerQueuesNumber")));
         _routerConfiguration.put(EQueueConfig.NodeMaxBuffering.toString(),Integer.valueOf((String)property.getProperty("routerMaxBufferSize")));
         _routerConfiguration.put(EQueueConfig.QueuesLimit.toString(),Integer.valueOf(property.getProperty("routerQueueLimit")));
               
        
         //////////////////////////////////////////////////
         //             Appliance Configuration
         //////////////////////////////////////////////////
         _config.getApplianceConfiguration().put(ENodeConfig.NodeType.toString(),ENodeType.Appliance.toString());
         _config.getApplianceConfiguration().put(ENodeConfig.EnQueueingAlgorithm.toString(), EAlgorithmEnQueueing.FIFO.toString()); 
         _config.getApplianceConfiguration().put(ENodeConfig.SchedulingAlgorithm.toString(), EAlgorithmScheduling.FIFO.toString());
         _config.getApplianceConfiguration().put(ENodeConfig.RoutingAlgorithm.toString(), EAlgorithmRouting.Random.toString());         
       
       
         _config.getApplianceConfiguration().put(EQueueConfig.QueuesNumber.toString(),Integer.valueOf((String)property.getProperty("applianceQueuesNumber")));
         _config.getApplianceConfiguration().put(EQueueConfig.NodeMaxBuffering.toString(),Integer.valueOf((String)property.getProperty("applianceQueuesNumber")));
         _config.getApplianceConfiguration().put(EQueueConfig.QueuesLimit.toString(),Integer.valueOf((String)property.getProperty("applianceQueuesNumber")));
        
        
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
    
    public void loadRouterRatesParameters() {

      
        Properties property = new Properties();
	InputStream input = null;    
            
        try {
 
    	String filename = "simulation.properties";
    	input = Configuration.class.getClassLoader().getResourceAsStream(filename);
            
        
        // load a properties file
        property.load(input);

        String parameter="";
        String arrType="";
        double value=-1;
         
        // InterArrival Time
        for (int i = 0; i < _numberOfDomains; i++) {
            parameter="arrType_"+i;
            arrType=String.valueOf((String)property.getProperty(parameter));
            
            if(EGeneratorType.Exponential.toString().equals(arrType)){
                parameter="lamda"+i;
                value=Double.valueOf((String)property.getProperty(parameter));
                value=(double)1/value;
                _requestArrivalTimeConfiguration[i].put("arrType",EGeneratorType.Exponential.toString());
                _requestArrivalTimeConfiguration[i].put("mean",value);
            }
            else if(EGeneratorType.Pareto.toString().equals(arrType)){
                _requestArrivalTimeConfiguration[i].put("arrType",EGeneratorType.Pareto.toString());
                
                parameter="location"+i;
                value=Double.valueOf((String)property.getProperty(parameter));
                _requestArrivalTimeConfiguration[i].put("location",value);
                
                parameter="shape"+i;
                value=Double.valueOf((String)property.getProperty(parameter));
                _requestArrivalTimeConfiguration[i].put("shape",value);
            
            }
            else if(EGeneratorType.Traces.toString().equals(arrType)){
                  _requestArrivalTimeConfiguration[i].put("arrType",EGeneratorType.Traces.toString());
             }
            
         }
         
         // Router Service Time
         for (int i = 0; i < _numberOfDomains; i++) {
            parameter="r_mu"+i;
            value=Double.valueOf((String)property.getProperty(parameter));
            value=(double)1/value;
            _routerServiceTimeConfiguration[i].put("mean",value);
         }
        
      
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
    
    public void loadApplianceRatesParameters() {

      
        Properties property = new Properties();
	InputStream input = null;    
            
        try {
 
    	String filename = "simulation.properties";
    	input = Configuration.class.getClassLoader().getResourceAsStream(filename);
            
        
        // load a properties file
        property.load(input);

        String appServiceType;
        String parameter="";
        double value=-1;
         
       for (int i = 0; i < _numberOfDomains; i++) {
            parameter="appServiceType"+i;
            appServiceType=String.valueOf((String)property.getProperty(parameter));
            
            if(EGeneratorType.Exponential.toString().equals(appServiceType)){
                parameter="app_mu"+i;
                value=Double.valueOf((String)property.getProperty(parameter));
                _applianceServiceTimeConfiguration[i].put("appServiceType",EGeneratorType.Exponential.toString());
                _applianceServiceTimeConfiguration[i].put("mean",value);
            }
            else if(EGeneratorType.Pareto.toString().equals(appServiceType)){
                _applianceServiceTimeConfiguration[i].put("appServiceType",EGeneratorType.Pareto.toString());
                
                parameter="appService_location"+i;
                value=Double.valueOf((String)property.getProperty(parameter));
                _applianceServiceTimeConfiguration[i].put("location",value);
                
                parameter="appService_shape"+i;
                value=Double.valueOf((String)property.getProperty(parameter));
                _applianceServiceTimeConfiguration[i].put("shape",value);
            
            }
            else if(EGeneratorType.Random.toString().equals(appServiceType)){
                _applianceServiceTimeConfiguration[i].put("appServiceType",EGeneratorType.Random.toString());
                
                 parameter="appService_Min"+i;
                value=Double.valueOf((String)property.getProperty(parameter));
                _applianceServiceTimeConfiguration[i].put("min",value);
                
                parameter="appService_Max"+i;
                value=Double.valueOf((String)property.getProperty(parameter));
                _applianceServiceTimeConfiguration[i].put("max",value);
            }
            else if(EGeneratorType.Fixed.toString().equals(appServiceType)){
                _applianceServiceTimeConfiguration[i].put("appServiceType",EGeneratorType.Fixed.toString());
                
                 parameter="fixedDuration"+i;
                value=Double.valueOf((String)property.getProperty(parameter));
                _applianceServiceTimeConfiguration[i].put("duration",value);
              
            }
             else if(EGeneratorType.Harmonic.toString().equals(appServiceType)){
                _applianceServiceTimeConfiguration[i].put("appServiceType",EGeneratorType.Harmonic.toString());
                
                 parameter="harmonicMinSize"+i;
                value=Double.valueOf((String)property.getProperty(parameter));
                _applianceServiceTimeConfiguration[i].put("harmonicMinSize",value);
                
                 parameter="harmonicMaxSize"+i;
                value=Double.valueOf((String)property.getProperty(parameter));
                _applianceServiceTimeConfiguration[i].put("harmonicMaxSize",value);
              
            }
             else if(EGeneratorType.Traces.toString().equals(appServiceType)){
                 _applianceServiceTimeConfiguration[i].put("appServiceType",EGeneratorType.Traces.toString());
             }
         }
         
         
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
    
    
    
    public void loadBurstConfigurationParameters() {

      // To DO add Pareto
        Properties property = new Properties();
	InputStream input = null;    
            
        try {
 
    	String filename = "simulation.properties";
    	input = Configuration.class.getClassLoader().getResourceAsStream(filename);
            
        
        // load a properties file
        property.load(input);

        String parameter="";
        double value=-1;
        String onBurstType;
        String offBurstType;
        
         // Burst ON periods
       for (int i = 0; i < _numberOfDomains; i++) {
            parameter="onBurstType"+i;
            onBurstType=String.valueOf((String)property.getProperty(parameter));
            
            if(EGeneratorType.Exponential.toString().equals(onBurstType)){
                parameter="meanOn_"+i;
                value=Double.valueOf((String)property.getProperty(parameter));
                _onPeriodConfiguration[i].put("onBurstType",EGeneratorType.Exponential.toString());
                _onPeriodConfiguration[i].put("mean",value);
            }
            else if(EGeneratorType.Pareto.toString().equals(onBurstType)){
                _onPeriodConfiguration[i].put("onBurstType",EGeneratorType.Pareto.toString());
                
                parameter="burstON_location"+i;
                value=Double.valueOf((String)property.getProperty(parameter));
                _onPeriodConfiguration[i].put("location",value);
                
                parameter="burstON_shape"+i;
                value=Double.valueOf((String)property.getProperty(parameter));
                _onPeriodConfiguration[i].put("shape",value);
            
            }
            
         }
                 
          // Burst OFF periods
        for (int i = 0; i < _numberOfDomains; i++) {
            parameter="offBurstType"+i;
            onBurstType=String.valueOf((String)property.getProperty(parameter));
            
            if(EGeneratorType.Exponential.toString().equals(onBurstType)){
                parameter="meanOff_"+i;
                value=Double.valueOf((String)property.getProperty(parameter));
                _offPeriodConfiguration[i].put("offBurstType",EGeneratorType.Exponential.toString());
                _offPeriodConfiguration[i].put("mean",value);
            }
            else if(EGeneratorType.Pareto.toString().equals(onBurstType)){
                _offPeriodConfiguration[i].put("offBurstType",EGeneratorType.Pareto.toString());
                
                parameter="burstOFF_location"+i;
                value=Double.valueOf((String)property.getProperty(parameter));
                _offPeriodConfiguration[i].put("location",value);
                
                parameter="burstOFF_shape"+i;
                value=Double.valueOf((String)property.getProperty(parameter));
                _offPeriodConfiguration[i].put("shape",value);
            
            }
            
         }
      
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

           
         
      
        
   
    public int getMaxRequestSize() {
        return _maxRequestSize;
    }

    public void setMaxRequestSize(int _maxRequestSize) {
        this._maxRequestSize = _maxRequestSize;
    }

    public void loadWebRequestGeneratorsType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public HashMap[] getOnPeriodConfiguration() {
        return _onPeriodConfiguration;
    }

    public HashMap[] getOffPeriodConfiguration() {
        return _offPeriodConfiguration;
    }
    
     public double getCpuPercentageAllShare() {
		return cpuPercentageAllShare;
    }
    
    public void setCpuPercentageAllShare(double cpuPercentageAllShare) {
		this.cpuPercentageAllShare = cpuPercentageAllShare;
    }

    public int getSchedulingDelayPeriod() {
        return _schedulinDelayPeriod;
    }

    public void setDelayBetweenSchedulingPeriods(int _TdelayedPeriods) {
        this._schedulinDelayPeriod = _TdelayedPeriods;
    }

    public double getAdditionalSimulationTime() {
        return _additionalSimulationTime;
    }

    public void setAdditionalSimulationTime(double _additionalSimulationTime) {
        this._additionalSimulationTime = _additionalSimulationTime;
    }

    public HashMap getApplianceConfiguration() {
        return _applianceConfiguration;
    }

    public void setApplianceConfiguration(HashMap _applianceConfiguration) {
        this._applianceConfiguration = _applianceConfiguration;
    }

    public String getControlOperationType() {
        return _controlOperationType;
    }

    public void setControlOperationType(String _controlOperationType) {
        this._controlOperationType = _controlOperationType;
    }

    public double getConverganceCounterLimit() {
        return _converganceCounterLimit;
    }

    public void setConverganceCounterLimit(double _converganceLimit) {
        this._converganceCounterLimit = _converganceLimit;
    }

    public String getFinalServiceTier() {
        return _finalServiceTier;
    }

    public void setFinalServiceTier(String _finalServiceTier) {
        this._finalServiceTier = _finalServiceTier;
    }

    public String getLinkBandwidth() {
        return _linkBandwidth;
    }

    public void setLinkBandwidth(String _linkBandwidth) {
        this._linkBandwidth = _linkBandwidth;
    }

    public int getNumberOfAppliances() {
        return _numberOfAppliances;
    }

    public void setNumberOfAppliances(int _numberOfAppliances) {
        this._numberOfAppliances = _numberOfAppliances;
    }

    public int getNumberOfControlEvents() {
        return _numberOfControlEvents;
    }

    public void setNumberOfControlEvents(int _numberOfControlEvents) {
        this._numberOfControlEvents = _numberOfControlEvents;
    }

    public int getNumberOfDataCenters() {
        return _numberOfDataCenters;
    }

    public void setNumberOfDataCenters(int _numberOfDataCenters) {
        this._numberOfDataCenters = _numberOfDataCenters;
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

    public int getNumberOfServers() {
        return _numberOfServers;
    }

    public void setNumberOfServers(int _numberOfServers) {
        this._numberOfServers = _numberOfServers;
    }

    public HashMap getRouterConfiguration() {
        return _routerConfiguration;
    }

    public void setRouterConfiguration(HashMap _routerConfiguration) {
        this._routerConfiguration = _routerConfiguration;
    }

   

    public String getSimulationID() {
        return _simulationID;
    }

    public void setSimulationID(String _simulationID) {
        this._simulationID = _simulationID;
    }

    public double getSimulationTime() {
        return _simulationTime;
    }

    public void setSimulationTime(double _simulationTime) {
        this._simulationTime = _simulationTime;
    }

    public int getStatsDensity() {
        return _statsDensity;
    }

    public void setStatsDensity(int _statsDensity) {
        this._statsDensity = _statsDensity;
    }

    public XMLEventFactory getEventFactory() {
        return eventFactory;
    }

    public void setEventFactory(XMLEventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

    public XMLEventWriter getEventWriter() {
        return eventWriter;
    }

    public void setEventWriter(XMLEventWriter eventWriter) {
        this.eventWriter = eventWriter;
    }

    public XMLOutputFactory getOutputFactory() {
        return outputFactory;
    }

    public void setOutputFactory(XMLOutputFactory outputFactory) {
        this.outputFactory = outputFactory;
    }

    public int getNumberOfRouters() {
        return _numberOfRouters;
    }

    public void setNumberOfRouters(int _numberOfRouters) {
        this._numberOfRouters = _numberOfRouters;
    }

    public HashMap[] getApplianceServiceTimeConfiguration() {
        return _applianceServiceTimeConfiguration;
    }

    public HashMap[] getRequestArrivalTimeConfiguration() {
        return _requestArrivalTimeConfiguration;
    }

    public HashMap[] getRouterServiceTimeConfiguration() {
        return _routerServiceTimeConfiguration;
    }

    public int getBorderNodeID() {
        return _borderNodeID;
    }

    public void setBorderNodeID(int _borderNodeID) {
        this._borderNodeID = _borderNodeID;
    }

    public int getNumberOfWebRequestGenerators() {
        return _numberOfWebRequestGenerators;
    }

    public void setNumberOfWebRequestGenerators(int _webRequestGenerators) {
        this._numberOfWebRequestGenerators = _webRequestGenerators;
    }

    public String getWebRequestGeneratorsType() {
        return _webRequestGeneratorsType;
    }

    public void setWebRequestGeneratorsType(String _webRequestGeneratorsType) {
        this._webRequestGeneratorsType = _webRequestGeneratorsType;
    }

  public double getConvergencePlusMinCriterion() {
        return _convergencePlusMinCriterion;
    }

    public void setConvergencePlusMinCriterion(double _convergencePlusMinCriterion) {
        this._convergencePlusMinCriterion = _convergencePlusMinCriterion;
    }

    public double getConvergenceTime() {
        return _convergenceTime;
    }

    public void setConvergenceTime(double _convergenceTime) {
        this._convergenceTime = _convergenceTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
	public Boolean get_generateSaturatedConditions() {
		return _generateSaturatedConditions;
	}
	public void set_generateSaturatedConditions(Boolean _generateSaturatedConditions) {
		this._generateSaturatedConditions = _generateSaturatedConditions;
	}

    public int getWindowSize() {
        return _windowSize;
    }

    public void setWindowSize(int _windowSize) {
        this._windowSize = _windowSize;
    }
    
   
}
