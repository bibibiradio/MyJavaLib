package xm.bibibiradio.monitor.monitor;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import xm.bibibiradio.monitor.listener.MonitorListener;

public class Monitor {
    final static private Logger LOGGER = Logger.getLogger(Monitor.class);
    
    final static public int LEVEL0 = 0;
    final static public int LEVEL1 = 1;
    final static public int LEVEL2 = 2;
    
    private static Monitor monitor;
    
    private List<MonitorListener> level0Listener;
    private List<MonitorListener> level1Listener;
    private List<MonitorListener> level2Listener;
    
    private Monitor(){
        level0Listener = new ArrayList<MonitorListener>();
        level1Listener = new ArrayList<MonitorListener>();
        level2Listener = new ArrayList<MonitorListener>();
    }
    
    static public Monitor getMonitor(){
        if(monitor == null){
            synchronized (Monitor.class){
                if(monitor == null){
                    monitor = new Monitor();
                }
            }
        }
        return monitor;
    }
    
    public void notify(NotifyMessage msg){
        List<MonitorListener> listeners;
        if(msg.getLevel() == LEVEL0)
            listeners = level0Listener;
        else if(msg.getLevel() == LEVEL1)
            listeners = level1Listener;
        else if(msg.getLevel() == LEVEL2)
            listeners = level2Listener;
        else{
            LOGGER.error("error",new Exception("msg level not exists"));
            return;
        }
        
        for(MonitorListener listener : listeners){
            listener.listen(msg);
        }
    }
    
    public void register(int level,MonitorListener listener){
        List<MonitorListener> listeners;
        if(level == LEVEL0)
            listeners = level0Listener;
        else if(level == LEVEL1)
            listeners = level1Listener;
        else if(level == LEVEL2)
            listeners = level2Listener;
        else
            return;
        
        if(listeners != null){
            listeners.add(listener);
        }
    }
}
