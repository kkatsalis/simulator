/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Generators;

import java.util.Random;

import BL.Configuration;
import BL.RandomCollection;
import Enumerators.EGeneratorType;
import Enumerators.ENodeType;
import Enumerators.EServiceOperation;
import jsc.distributions.Exponential;
import jsc.distributions.Pareto;



/**
 *
 * @author maevious
 */
public class TimeGenerator {
    
    Configuration _config;
    
    int _numberOfDomains;
    String _nodeType;
    String distributionType;
    // How to service requests per Service Domain
    Exponential[] _exponentialGenerator;
    Pareto[] _paretoGenerator;
    double[] _fixedDuration;
    RandomCollection<Integer> harmonicItems;
    
    String[] _generatorType;
    Random rand;
    
    public TimeGenerator(Configuration config, String nodeType ) {
       
        this._config=config;
        this._nodeType=nodeType;
        this._numberOfDomains = _config.getNumberOfDomains();
        this.rand=new Random(1);
        _generatorType=new String[_numberOfDomains];
        _fixedDuration=new double[_numberOfDomains];
        
         BuildServiceTimeGenerators();
    }

 

    private void BuildServiceTimeGenerators()
    {
        _generatorType=new String[_numberOfDomains];
        
        for (int i = 0; i < _numberOfDomains; i++) {
            _generatorType[i]=_config.getApplianceServiceTimeConfiguration()[i].get("appServiceType").toString();
        }
        
        // 2. Build the array of generators
        double lamda;
        double location;
        double shape;
         
        this._exponentialGenerator=new Exponential[_numberOfDomains];
        this._paretoGenerator=new Pareto[_numberOfDomains];
        
            for (int i = 0; i < _numberOfDomains; i++) {
                
                if(_generatorType[i].equals(EGeneratorType.Exponential.toString())){
                
                    lamda=((Double)_config.getApplianceServiceTimeConfiguration()[i].get("mean")).doubleValue();
                    this._exponentialGenerator[i]=new Exponential(lamda);
                    this._paretoGenerator[i]=null;
                }
                else if(_generatorType[i].equals(EGeneratorType.Pareto.toString())){
                    
                  _exponentialGenerator[i]=null;  
                  location=((Double)_config.getApplianceServiceTimeConfiguration()[i].get("location")).doubleValue();
                  shape =((Double)_config.getApplianceServiceTimeConfiguration()[i].get("shape")).doubleValue();
                  
                  this._paretoGenerator[i]=new Pareto(location, shape);
                }
                else if(_generatorType[i].equals(EGeneratorType.Fixed.toString())){
                    _exponentialGenerator[i]=null;  
                    _paretoGenerator[i]=null;  
                    _fixedDuration[i]=((Double)_config.getApplianceServiceTimeConfiguration()[i].get("duration")).doubleValue();
                }
            }      
        
    }
  
 

    public Exponential[] getExponentialGenerator() {
        return _exponentialGenerator;
    }

    public Pareto[] getParetoGenerator() {
        return _paretoGenerator;
    }

  

    public double[] getFixedDuration() {
        return _fixedDuration;
    }

    public double getRandomDuration(int sdID) {

        double duration=0.000001;
        
        double rangeMin=((Double)_config.getApplianceServiceTimeConfiguration()[sdID].get("min")).doubleValue();
        double rangeMax=((Double)_config.getApplianceServiceTimeConfiguration()[sdID].get("max")).doubleValue();
        
        if(_generatorType[sdID].equals(EGeneratorType.Random.toString())){
           duration = rangeMin + (rangeMax - rangeMin) * rand.nextDouble();
        }
        
        return duration;
    }

    public double getHarmonicDuration(int sdID) {
        
        double duration=0.000001;
        int minSize;
        int maxSize;
        
         
        if(_generatorType[sdID].equals(EGeneratorType.Harmonic.toString())){
            
            minSize=((Integer)_config.getRequestArrivalTimeConfiguration()[sdID].get("harmonicMinSize")).intValue();
            maxSize=((Integer)_config.getRequestArrivalTimeConfiguration()[sdID].get("harmonicMaxSize")).intValue();
        
            harmonicItems = new RandomCollection<Integer>();
        
            // add some stuff, weighted as you like
             for (int i = minSize; i < maxSize; i++) {
                harmonicItems.add(i/1, i);
        }
        
            // get a random item
            duration = harmonicItems.next();
        }
        
        return duration;
    }

    public String[] getGeneratorType() {
        return _generatorType;
    }

   

  

   

   

    
    
    
    

    

}
