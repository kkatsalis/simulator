/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Statistics;

import java.util.ArrayList;

import BL.Configuration;
import BL.DataCenter;
import Enumerators.ENodeType;



public class ApplianceTierStatistics {
   
    Statistics _statistics;
    CpuStatistics _cpuStatistics;
    WindowCpuStatistics _windowCpuStatistics;
    
    Configuration _config; 
    DataCenter _dataCenter; 
    String _tierType;
    
    int levels;
    int domains;
    int numberOfQueues;
    
    // <editor-fold desc="Statistics temp Tables">
    int[] tempRequestArrivals_PL;
    int[] tempRequestsServed_PL;
    int[] tempRequestsDelayed_PL;
    int[] tempRequestsRejected_PL;
    int[] tempRequestsInQueues_PL;
    
    int[] tempRequestArrivals_PSD;
    int[] tempRequestsServed_PSD;
    int[] tempRequestsDelayed_PSD;
    int[] tempRequestsRejected_PSD;
    int[] tempRequestsInQueues_PSD;
    
    int[][] tempRequestArrivals_PLPSD;
    int[][] tempRequestsServed_PLPSD;
    int[][] tempRequestsDelayed_PLPSD;
    int[][] tempRequestsRejected_PLPSD;
    int[][] tempRequestsInQueues_PLPSD;
    
    ArrayList<Integer[][]>  tempRequestsInQueues_PLPQPSD; //[levelID][queueID][sdID]
 
    double   _avgDelayInService;
    double   _avgDelayInQueue;
    double   _avgQueueSize;
    
    double[]   _avgDelayInService_PSD;
    double[]   _avgDelayInQueue_PSD;
    
    double[]   _avgDelayInService_PL;
    double[]   _avgDelayInQueue_PL;
    double[] _avgQueueSize_PL;
    
    double[][] _avgDelayInService_PLPSD; 
    double[][] _avgDelayInQueue_PLPSD;
    
    
    
    
      // </editor-fold>
    
    // <editor-fold desc="Cpu Statistics temp Tables">
        
        double[]  _idleTime_PL;
        double[] _deviationPSD;   
        double[] _cpuTimeOfOperation_PL;     //PSD: per service domain
        double[] _cpuTimeOfOperation_PSD;     //PSD: per service domain
        double[][] _cpuTimeOfOperation_PLPSD;     //PSD: per service domain
        double[] _cpuUtilization_PL;
        double[] _cpuUtilization_PSD;
        double[][] _cpuUtilization_PLPSD;
    
     // </editor-fold>
    
    double[] _windowCpuUtilization_PSD;        
        
        
    public ApplianceTierStatistics(Configuration config, DataCenter dataCenter,String tierType) {
        
        this._tierType=tierType;
        this._config=config;
        this._dataCenter=dataCenter;
        
        this.levels=_config.getNumberOfLevels();
        this.domains=_config.getNumberOfDomains();
        this.numberOfQueues=CheckQueuesNumber(tierType);
        this._windowCpuStatistics=new WindowCpuStatistics(domains,_config.getWindowSize());
        this._windowCpuUtilization_PSD=new double[domains];
        
        _statistics=new Statistics(domains,levels,numberOfQueues);
        _cpuStatistics=new CpuStatistics(domains,levels);
                
        CreateStatisticsNecessaryTables();
        CreateCpuStatisticsNecessaryTables();
        
        
    }
    
    public void UpdateStatistics(){
      
        try {
           
              UpdateStats();                 
           // UpdateStatsPL();
              UpdateStatsPSD();
              UpdateStatsPQPSD();
           // UpdateStatsPLPSD();
           // UpdateStatsPLPQPSD();
            
              UpdateAverageStats();
          //  UpdateAverageStatsPL();
              UpdateAverageStatsPSD();
          //  UpdateAverageStatsPLPSD();
            
         } 
        catch (Exception e) {
            System.out.print("Error in TierStatistics"+ " "+e.getMessage());
        }
    }
    public void UpdateCpuStatistics(){
      
        try {
            
            UpdateCpuStats();                 
            UpdateCpuStatsPL();
            UpdateCpuStatsPSD();
            UpdateCpuStatsPLPSD();
            UpdateWindowCpuStatistics();
         } 
        catch (Exception e) {
            System.out.print("Error in TierStatistics"+ " "+e.getMessage());
        }
    }
    
    // <editor-fold desc="Statistics Private methods">
    private void CreateStatisticsNecessaryTables() {
        tempRequestArrivals_PL=new int[levels];
        tempRequestsServed_PL=new int[levels];
        tempRequestsDelayed_PL=new int[levels];
        tempRequestsRejected_PL=new int[levels];
        tempRequestsInQueues_PL=new int[levels];
        
        tempRequestArrivals_PSD=new int[domains];
        tempRequestsServed_PSD=new int[domains];
        tempRequestsDelayed_PSD=new int[domains];
        tempRequestsRejected_PSD=new int[domains];
        tempRequestsInQueues_PSD=new int[domains];
        
        tempRequestArrivals_PLPSD = new int[levels][domains];
        tempRequestsServed_PLPSD  = new int[levels][domains];
        tempRequestsDelayed_PLPSD = new int[levels][domains];
        tempRequestsRejected_PLPSD= new int[levels][domains];
        tempRequestsInQueues_PLPSD= new int[levels][domains];
        
        tempRequestsInQueues_PLPQPSD=new ArrayList<Integer[][]>();
        
        _avgDelayInService_PL  =new double[levels];
        _avgDelayInQueue_PL =new double[levels];
        _avgQueueSize_PL =new double[levels];
         
        _avgDelayInService_PSD =new double[domains];
        _avgDelayInQueue_PSD =new double[domains];
         
        _avgDelayInService_PLPSD  = new double[levels][domains];
        _avgDelayInQueue_PLPSD= new double[levels][domains]; 
   
    }
  
    private void UpdateStats() {
        
      this._statistics.setRequestArrivals(0);
      this._statistics.setRequestsServed(0);
      this._statistics.setRequestsDelayed(0);
      this._statistics.setRequestsRejected(0);
      this._statistics.setRequestsInQueues(0);
                        
      for (int k = 0; k < _dataCenter.getAppliances().size(); k++) {
              this._statistics.setRequestArrivals(_statistics.getRequestArrivals()+_dataCenter.getAppliances().get(k).getStatistics().getRequestArrivals());
              this._statistics.setRequestsServed(_statistics.getRequestsServed()+_dataCenter.getAppliances().get(k).getStatistics().getRequestsServed());
              this._statistics.setRequestsDelayed(_statistics.getRequestsDelayed()+_dataCenter.getAppliances().get(k).getStatistics().getRequestsDelayed());
              this._statistics.setRequestsRejected(_statistics.getRequestsRejected()+_dataCenter.getAppliances().get(k).getStatistics().getRequestsRejected());
              this._statistics.setRequestsInQueues(_statistics.getRequestsInQueues()+_dataCenter.getAppliances().get(k).getStatistics().getRequestsInQueues());
              
            }
    }
    private void UpdateStatsPL(){
    
        for (int i = 0; i < levels; i++) {
         
         tempRequestArrivals_PL[i]=0;
         tempRequestsServed_PL[i]=0;
         tempRequestsDelayed_PL[i]=0;
         tempRequestsRejected_PL[i]=0;
         tempRequestsInQueues_PL[i]=0;
         
        for (int k = 0; k < _dataCenter.getAppliances().size(); k++) {
              
              tempRequestArrivals_PL[i]+=_dataCenter.getAppliances().get(k).getStatistics().getRequestArrivals_PL()[i];
              tempRequestsServed_PL[i]+=_dataCenter.getAppliances().get(k).getStatistics().getRequestsServed_PL()[i];
              tempRequestsDelayed_PL[i]+=_dataCenter.getAppliances().get(k).getStatistics().getRequestsDelayed_PL()[i];
              tempRequestsRejected_PL[i]+=_dataCenter.getAppliances().get(k).getStatistics().getRequestsRejected_PL()[i];
              tempRequestsInQueues_PL[i]+=_dataCenter.getAppliances().get(k).getStatistics().getRequestsInQueues_PL()[i];
           
          }
        }
        
        _statistics.setRequestArrivals_PL(tempRequestArrivals_PL);
        _statistics.setRequestsServed_PL(tempRequestsServed_PL);
        _statistics.setRequestsDelayed_PL(tempRequestsDelayed_PL);
        _statistics.setRequestsRejected_PL(tempRequestsRejected_PL);
        _statistics.setRequestsInQueues_PL(tempRequestsInQueues_PL);
     
    }
    private void UpdateStatsPSD() {
     
     for (int i = 0; i < domains; i++) {
         
         tempRequestArrivals_PSD[i]=0;
         tempRequestsServed_PSD[i]=0;
         tempRequestsDelayed_PSD[i]=0;
         tempRequestsRejected_PSD[i]=0;
         tempRequestsInQueues_PSD[i]=0;
         
        for (int k = 0; k < _dataCenter.getAppliances().size(); k++) {
            
                  tempRequestArrivals_PSD[i]+=_dataCenter.getAppliances().get(k).getStatistics().getRequestArrivals_PSD()[i];
                  tempRequestsServed_PSD[i]+=_dataCenter.getAppliances().get(k).getStatistics().getRequestsServed_PSD()[i];
                  tempRequestsDelayed_PSD[i]+=_dataCenter.getAppliances().get(k).getStatistics().getRequestsDelayed_PSD()[i];
                  tempRequestsRejected_PSD[i]+=_dataCenter.getAppliances().get(k).getStatistics().getRequestsRejected_PSD()[i];
                  tempRequestsInQueues_PSD[i]+=_dataCenter.getAppliances().get(k).getStatistics().getRequestsInQueues_PSD()[i];
                     
                              
         }
        }
     
        _statistics.setRequestArrivals_PSD(tempRequestArrivals_PSD);
        _statistics.setRequestsServed_PSD(tempRequestsServed_PSD);
        _statistics.setRequestsDelayed_PSD(tempRequestsDelayed_PSD);
        _statistics.setRequestsRejected_PSD(tempRequestsRejected_PSD);
        _statistics.setRequestsInQueues_PSD(tempRequestsInQueues_PSD);
        
      
    }
    private void UpdateStatsPLPSD() {
     
     
     //Initialize the arrays
     for (int i = 0; i < levels; i++) {
       for (int j = 0; j < domains; j++) {
            tempRequestArrivals_PLPSD[i][j] = 0;
            tempRequestsServed_PLPSD[i][j] = 0;
            tempRequestsDelayed_PLPSD[i][j] = 0;
            tempRequestsRejected_PLPSD[i][j] = 0;
            tempRequestsInQueues_PLPSD[i][j] = 0;
        }
     }
     
     for (int i = 0; i < levels; i++) {
         
        for (int k = 0; k < _dataCenter.getAppliances().size(); k++) {
              
              for (int j = 0; j < domains; j++) {
                    tempRequestArrivals_PLPSD[i][j] +=_dataCenter.getAppliances().get(k).getStatistics().getRequestArrivals_PLPSD()[i][j];
                    tempRequestsServed_PLPSD[i][j] += _dataCenter.getAppliances().get(k).getStatistics().getRequestsServed_PLPSD()[i][j];
                    tempRequestsDelayed_PLPSD[i][j] +=_dataCenter.getAppliances().get(k).getStatistics().getRequestsDelayed_PLPSD()[i][j];
                    tempRequestsRejected_PLPSD[i][j]+=_dataCenter.getAppliances().get(k).getStatistics().getRequestsRejected_PLPSD()[i][j];
                    tempRequestsInQueues_PLPSD[i][j]+=_dataCenter.getAppliances().get(k).getStatistics().getRequestsInQueues_PLPSD()[i][j];
              }
                 
               }
                  
         }
        
        _statistics.setRequestArrivals_PLPSD(tempRequestArrivals_PLPSD);
        _statistics.setRequestsServed_PLPSD(tempRequestsServed_PLPSD);
        _statistics.setRequestsDelayed_PLPSD(tempRequestsDelayed_PLPSD);
        _statistics.setRequestsRejected_PLPSD(tempRequestsRejected_PLPSD);
        _statistics.setRequestsInQueues_PLPSD(tempRequestsInQueues_PLPSD);
        
      }

    private void UpdateStatsPLPQPSD() {
    
        tempRequestsInQueues_PLPQPSD.clear();
        
        for (int i = 0; i < levels; i++) {
            tempRequestsInQueues_PLPQPSD.add(new Integer[numberOfQueues][domains]);    
 
           for (int k = 0; k < _dataCenter.getAppliances().size(); k++) {
              
                for (int j = 0; j < numberOfQueues; j++) {
                    for (int l = 0; l < domains; l++) {
                        tempRequestsInQueues_PLPQPSD.get(i)[j][l]+=_dataCenter.getAppliances().get(k).getStatistics().getRequestsInQueues_PLPQPSD().get(i)[j][l];
                    }
                }
                
           
        }
   
        
        }
        
        _statistics.setRequestsInQueues_PLPQPSD(tempRequestsInQueues_PLPQPSD);
    }

    private int CheckQueuesNumber(String _tierType) {
       
        int _numberOfQueues;
        
        if(_tierType.equals(ENodeType.Router.toString())){
            _numberOfQueues=((Integer)_config.getRouterConfiguration().get("QueuesNumber")).intValue();
                    
        }
        else if(_tierType.equals(ENodeType.Appliance.toString())){
            _numberOfQueues=((Integer)_config.getApplianceConfiguration().get("QueuesNumber")).intValue();
                    
        }
        else
            _numberOfQueues=((Integer)_config.getRouterConfiguration().get("QueuesNumber")).intValue();
        
        return _numberOfQueues;
    }

    private void UpdateAverageStats() {
    
   
        double  avgDelayInQueue=0;
        double  avgDelayInService=0;
       
        int nodesNumber=0;
        int  numberOfSchedulingRounds=0;
        for (int k = 0; k < _dataCenter.getAppliances().size(); k++) {
            
                  nodesNumber++;
                  
                  avgDelayInQueue+=_dataCenter.getAppliances().get(k).getStatistics().getAvgDelayInQueue();
                  avgDelayInService+=_dataCenter.getAppliances().get(k).getStatistics().getAvgDelayInService();
                  numberOfSchedulingRounds+=_dataCenter.getAppliances().get(k).getStatistics().getNumberOfSchedulingRounds();
             
         }
        
          avgDelayInQueue = (double)avgDelayInQueue/nodesNumber;
          avgDelayInService = (double)avgDelayInService/nodesNumber;
        
          numberOfSchedulingRounds=(int)numberOfSchedulingRounds/nodesNumber;
        
          _statistics.setAvgDelayInQueue(avgDelayInQueue);
          _statistics.setAvgDelayInService(avgDelayInService);
      
          _statistics.setNumberOfSchedulingRounds(numberOfSchedulingRounds);
    }

    private void UpdateAverageStatsPL() {
    
      int nodesNumber=0;
    
      for (int i = 0; i < levels; i++) {
      
           _avgQueueSize_PL[i]=0;
           _avgDelayInService_PL[i]=0;
           _avgDelayInQueue_PL[i]=0;
                          
           for (int k = 0; k < _dataCenter.getAppliances().size(); k++) {
              
                  nodesNumber++;
                  
            
                  _avgDelayInService_PL[i]+=_dataCenter.getAppliances().get(k).getStatistics().getAvgDelayInSys_PL()[i];  
                  _avgDelayInQueue_PL[i]+=_dataCenter.getAppliances().get(k).getStatistics().getAvgDelayInQueue_PL()[i];
       
             
           }
       }
       
       for (int i = 0; i < levels; i++) {
            _avgQueueSize_PL[i]=(double)_avgQueueSize_PL[i]/nodesNumber;
            _avgDelayInService_PL[i] =(double)_avgDelayInService_PL[i]/nodesNumber;
            _avgDelayInQueue_PL[i]=(double)_avgDelayInQueue_PL[i]/nodesNumber;
       
       }
        
       
       _statistics.setAvgDelayInQueue_PL(_avgDelayInQueue_PL);
       _statistics.setAvgDelayInSys_PL(_avgDelayInService_PL);
      
    }

    private void UpdateAverageStatsPSD() {
       
       int nodesNumber=0;
 
       for (int i = 0; i < domains; i++) {
           
           _avgDelayInService_PSD[i]=0;
           _avgDelayInQueue_PSD[i]=0;
           
           for (int k = 0; k < _dataCenter.getAppliances().size(); k++) {
              
                  nodesNumber++;
                  
                  _avgDelayInService_PSD[i]+=_dataCenter.getAppliances().get(k).getStatistics().getAvgDelayInService_PSD()[i];
                  _avgDelayInQueue_PSD[i]+=_dataCenter.getAppliances().get(k).getStatistics().getAvgDelayInQueue_PSD()[i];
              
           }
       }
       
         for (int i = 0; i < domains; i++) {
            _avgDelayInService_PSD[i]= (double)_avgDelayInService_PSD[i]/nodesNumber;
            _avgDelayInQueue_PSD[i]=(double)_avgDelayInQueue_PSD[i]/nodesNumber;
         }
         
         
         _statistics.setAvgDelayInQueue_PSD(_avgDelayInQueue_PSD);
         _statistics.setAvgDelayInService_PSD(_avgDelayInService_PSD);
         
    }

    private void UpdateAverageStatsPLPSD() {
        
        int nodesNumber=0;
        
        for (int i = 0; i < levels; i++) {
            for (int j = 0; j < domains; j++) {
                
                _avgDelayInQueue_PLPSD[i][j]=0;
                _avgDelayInService_PLPSD[i][j]=0; 
            }
         }
        
        for (int k = 0; k < _dataCenter.getAppliances().size(); k++) {
              
                  nodesNumber++;
                   
                  for (int i = 0; i < levels; i++) {
                        for (int j = 0; j < domains; j++) {
                            _avgDelayInQueue_PLPSD[i][j]+=_dataCenter.getAppliances().get(k).getStatistics().getAvgDelayInQueue_PLPSD()[i][j];
                            _avgDelayInService_PLPSD[i][j]+=_dataCenter.getAppliances().get(k).getStatistics().getAvgDelayInSys_PLPSD()[i][j];
                        }
                  }
        
        
            
        }

     for (int i = 0; i < levels; i++) {
        for(int j = 0; j < domains; j++) {
              _avgDelayInQueue_PLPSD[i][j]=(double)_avgDelayInQueue_PLPSD[i][j]/nodesNumber;
              _avgDelayInService_PLPSD[i][j]=(double) _avgDelayInService_PLPSD[i][j]/nodesNumber;
        
        }
     }
     
     _statistics.setAvgDelayInQueue_PLPSD(_avgDelayInQueue_PLPSD);
     _statistics.setAvgDelayInSys_PLPSD(_avgDelayInService_PLPSD);

  }
   // </editor-fold>

 // <editor-fold desc="CPU Statistics Private methods">  
    private void CreateCpuStatisticsNecessaryTables() {
        
        _idleTime_PL=new double[levels];
        _deviationPSD=new double[domains]; 
        _cpuTimeOfOperation_PL=new double[levels];
        _cpuTimeOfOperation_PSD=new double[domains];
        _cpuTimeOfOperation_PLPSD=new double[levels][domains];
        _cpuUtilization_PL=new double[levels];
        _cpuUtilization_PSD=new double[domains];
        _cpuUtilization_PLPSD=new double[levels][domains];
    }  
    private void UpdateCpuStats() {
       
    	double nodetimeOfOperation;
    	double tierTimeOfOperation;
    	
      _cpuStatistics.setIdleTime(0);
      _cpuStatistics.setCpuTimeOfOperation(0);
       
      for (int k = 0; k < _dataCenter.getAppliances().size(); k++) {
            
    	  
            	  nodetimeOfOperation= _dataCenter.getAppliances().get(k).getCpuStatistics().getCpuTimeOfOperation();
            	  tierTimeOfOperation=_cpuStatistics.getCpuTimeOfOperation();
            	  _cpuStatistics.setIdleTime(_cpuStatistics.getIdleTime()+_dataCenter.getAppliances().get(k).getCpuStatistics().getIdleTime());
                _cpuStatistics.setCpuTimeOfOperation(tierTimeOfOperation+nodetimeOfOperation);
          
      }
    } 
    private void UpdateCpuStatsPL() {

        
     for (int i = 0; i < levels; i++) {
         
         _idleTime_PL[i]=0;
         _cpuTimeOfOperation_PL[i]=0;
         _cpuUtilization_PL[i]=0;
         
        for (int k = 0; k < _dataCenter.getAppliances().size(); k++) {
              
                  _idleTime_PL[i]+=_dataCenter.getAppliances().get(k).getCpuStatistics().getIdleTime_PL()[i];
                  _cpuTimeOfOperation_PL[i]+=_dataCenter.getAppliances().get(k).getCpuStatistics().getCpuTimeOfOperation_PL()[i];
                  _cpuUtilization_PL[i]+=_dataCenter.getAppliances().get(k).getCpuStatistics().getCpuUtilization_PL()[i];
                     
               
                  
         }
        }
     
       for (int i = 0; i < levels; i++) {
         _cpuUtilization_PL[i]=(double)_cpuTimeOfOperation_PL[i]/(_cpuTimeOfOperation_PL[i]+_idleTime_PL[i]);
         }
     
        _cpuStatistics.setIdleTime_PL(_idleTime_PL);
        _cpuStatistics.setCpuTimeOfOperation_PL(_cpuTimeOfOperation_PL);
        _cpuStatistics.setCpuUtilization_PL(_cpuUtilization_PL);
     
    }
    private void UpdateCpuStatsPSD() {
     
     for (int i = 0; i < domains; i++) {
 
         _cpuTimeOfOperation_PSD[i]=0;
         
         for (int k = 0; k < _dataCenter.getAppliances().size(); k++) {
             
                   _cpuTimeOfOperation_PSD[i]+=_dataCenter.getAppliances().get(k).getCpuStatistics().getCpuTimeOfOperation_PSD()[i];     
                 
            
          }
       
        } 
     
       	for (int i = 0; i < domains; i++) {
    	   
    	   		if((_cpuStatistics.getCpuTimeOfOperation()+_cpuStatistics.getIdleTime())==0)
    	   			_cpuUtilization_PSD[i]=0;
    	   		else
    	   		{
    	   			_cpuUtilization_PSD[i]=(double)_cpuTimeOfOperation_PSD[i]/(_cpuStatistics.getCpuTimeOfOperation()+_cpuStatistics.getIdleTime());
    	   		}
    	 }
       
         _cpuStatistics.setCpuTimeOfOperation_PSD(_cpuTimeOfOperation_PSD);
         _cpuStatistics.setCpuUtilization_PSD(_cpuUtilization_PSD);
      
    }
    
    private void UpdateCpuStatsPLPSD() {
        
       //Initialize the arrays
     for (int i = 0; i < levels; i++) {
       for (int j = 0; j < domains; j++) {
           

         _cpuTimeOfOperation_PLPSD[i][j] = 0; 
         _cpuUtilization_PLPSD[i][j]=0;
                  
        }
     }
     
     for (int i = 0; i < levels; i++) {
         
        for (int k = 0; k < _dataCenter.getAppliances().size(); k++) {
              
              for (int j = 0; j < domains; j++) {
                   _cpuTimeOfOperation_PLPSD[i][j] +=_dataCenter.getAppliances().get(k).getCpuStatistics().getCpuTimeOfOperation_PLPSD()[i][j]; 
              }
                  
        }
                  
      }
        
        for (int i = 0; i < levels; i++) {
            for (int j = 0; j < domains; j++) {
            _cpuUtilization_PLPSD[i][j]=(double)_cpuTimeOfOperation_PLPSD[i][j]/_cpuTimeOfOperation_PL[i];
            }
        }
        
        _cpuStatistics.setCpuTimeOfOperation_PLPSD(_cpuTimeOfOperation_PLPSD);
        _cpuStatistics.setCpuUtilization_PLPSD(_cpuUtilization_PLPSD);
    }
  // </editor-fold>

    public double getAvgDelayInQueue() {
        return _avgDelayInQueue;
    }

    public double[] getAvgDelayInQueue_PL() {
        return _avgDelayInQueue_PL;
    }

    public double[][] getAvgDelayInQueue_PLPSD() {
        return _avgDelayInQueue_PLPSD;
    }

    public double[] getAvgDelayInQueue_PSD() {
        return _avgDelayInQueue_PSD;
    }

    public double getAvgDelayInService() {
        return _avgDelayInService;
    }

    public double[] getAvgDelayInService_PL() {
        return _avgDelayInService_PL;
    }

    public double[][] getAvgDelayInService_PLPSD() {
        return _avgDelayInService_PLPSD;
    }

    public double[] getAvgDelayInService_PSD() {
        return _avgDelayInService_PSD;
    }

    public double getAvgQueueSize() {
        return _avgQueueSize;
    }

    public double[] getAvgQueueSize_PL() {
        return _avgQueueSize_PL;
    }

    public Configuration getConfig() {
        return _config;
    }

    public CpuStatistics getCpuStatistics() {
        return _cpuStatistics;
    }

    public double[] getCpuTimeOfOperation_PL() {
        return _cpuTimeOfOperation_PL;
    }

    public double[][] getCpuTimeOfOperation_PLPSD() {
        return _cpuTimeOfOperation_PLPSD;
    }

    public double[] getCpuTimeOfOperation_PSD() {
        return _cpuTimeOfOperation_PSD;
    }

    public double[] getCpuUtilization_PL() {
        return _cpuUtilization_PL;
    }

    public double[][] getCpuUtilization_PLPSD() {
        return _cpuUtilization_PLPSD;
    }

    public double[] getCpuUtilization_PSD() {
        return _cpuUtilization_PSD;
    }

    public DataCenter getDataCenter() {
        return _dataCenter;
    }

    public double[] getDeviationPSD() {
        return _deviationPSD;
    }

    public double[] getIdleTime_PL() {
        return _idleTime_PL;
    }

    public Statistics getStatistics() {
        return _statistics;
    }

    public String getTierType() {
        return _tierType;
    }

    public int getDomains() {
        return domains;
    }

    public int getLevels() {
        return levels;
    }

    public int getNumberOfQueues() {
        return numberOfQueues;
    }

    public int[] getTempRequestArrivals_PL() {
        return tempRequestArrivals_PL;
    }

    public int[][] getTempRequestArrivals_PLPSD() {
        return tempRequestArrivals_PLPSD;
    }

    public int[] getTempRequestArrivals_PSD() {
        return tempRequestArrivals_PSD;
    }

    public int[] getTempRequestsDelayed_PL() {
        return tempRequestsDelayed_PL;
    }

    public int[][] getTempRequestsDelayed_PLPSD() {
        return tempRequestsDelayed_PLPSD;
    }

    public int[] getTempRequestsDelayed_PSD() {
        return tempRequestsDelayed_PSD;
    }

    public int[] getTempRequestsInQueues_PL() {
        return tempRequestsInQueues_PL;
    }

    public ArrayList<Integer[][]> getTempRequestsInQueues_PLPQPSD() {
        return tempRequestsInQueues_PLPQPSD;
    }

    public int[][] getTempRequestsInQueues_PLPSD() {
        return tempRequestsInQueues_PLPSD;
    }

    public int[] getTempRequestsInQueues_PSD() {
        return tempRequestsInQueues_PSD;
    }

    public int[] getTempRequestsRejected_PL() {
        return tempRequestsRejected_PL;
    }

    public int[][] getTempRequestsRejected_PLPSD() {
        return tempRequestsRejected_PLPSD;
    }

    public int[] getTempRequestsRejected_PSD() {
        return tempRequestsRejected_PSD;
    }

    public int[] getTempRequestsServed_PL() {
        return tempRequestsServed_PL;
    }

    public int[][] getTempRequestsServed_PLPSD() {
        return tempRequestsServed_PLPSD;
    }

    public int[] getTempRequestsServed_PSD() {
        return tempRequestsServed_PSD;
    }

    private void UpdateStatsPQPSD() {
      long req=0; 
     
      for (int k = 0; k < _dataCenter.getAppliances().size(); k++) {
       
            for (int i = 0; i < numberOfQueues; i++) {
                for (int j = 0; j < _config.getNumberOfDomains(); j++) {
                    
                    req= _dataCenter.getAppliances().get(k).getStatistics().getRequestsEnqueued_PQPSD()[i][j];
                   
                    _statistics.getRequestsEnqueued_PQPSD()[i][j]+=req;
                           
                            
                }
            }
        
      
    }
    
    
    }

    private void UpdateWindowCpuStatistics() {
        
        for (int k = 0; k < _dataCenter.getAppliances().size(); k++) {
              
              for (int j = 0; j < domains; j++) {
              
              }
        
        }
    }



  
   
  
}