/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Events;

import BL.Configuration;

import java.util.ArrayList;
import java.util.List;



/**
 *
 * @author Kostas Katsalis
 */
public class EventMonitor {

    Configuration _simConfig;
    List<Event> _eventList;
   

    public EventMonitor( Configuration _simConfig) {
        
        this._eventList = new ArrayList<Event>();
   
    }

    public void AddInitialArrivalEvents(Event newEvent){
        _eventList.add(newEvent);
    }
    
    public void NewEventEnterList(Event newEvent)
    {
        List<Double> timeEvents=new ArrayList<Double>();
        
        boolean exists = true;
        double tempTime=0;

       
            // Check if there is an event with the same running moment
            if (newEvent != null)
            {
                // Step 1 : Check if there is another event happening at the same time
                while (exists == true)
                {
                    for (int i = 0; i < _eventList.size(); i++)
                    {
                        if (newEvent.getTimeOfOccurance() == _eventList.get(i).getTimeOfOccurance())
                        {
                            exists = true;
                            tempTime=newEvent.getTimeOfOccurance()+0.0000000001;
                            newEvent.setTimeOfOccurance(tempTime);
                        }
                    }

                    exists = false;

                    for (int i = 0; i < _eventList.size(); i++) {
                        if (newEvent.getTimeOfOccurance() == _eventList.get(i).getTimeOfOccurance())
                            exists = true;
                    }

                }

                // Step 2 : Enter the event at the appropriate place in the queue

                
                 if(_eventList.get( _eventList.size()-1).getTimeOfOccurance()<newEvent.getTimeOfOccurance())
                    _eventList.add(newEvent);
                 else{
                
                     int index = 0;

                while (newEvent.getTimeOfOccurance() > _eventList.get(index).getTimeOfOccurance()&index>=0&index< _eventList.size()-1)
                {
                     index += 1;
                   
                     if(newEvent.getTimeOfOccurance() == _eventList.get(index).getTimeOfOccurance())
                       break;
                }
                
               
                _eventList.add(index, newEvent);
                }
            }
        
           if(CheckList()){
               System.out.println(newEvent.getTimeOfOccurance());
               System.out.println(newEvent.getEventType());
           }
                   
       
    }

    public List<Event> getEventList() {
        return _eventList;
    }

    private boolean CheckList() {
        
        for (int i = 0; i < _eventList.size()-1; i++) {
            if(_eventList.get(i).getTimeOfOccurance()>_eventList.get(i+1).getTimeOfOccurance())
                return true;
        }

        return false;
    }


   
    
    
    
}
