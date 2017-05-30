package xm.bibibiradio.monitor.monitor;

import static org.junit.Assert.*;

import org.junit.Test;

import xm.bibibiradio.monitor.listener.TestListener;
import xm.bibibiradio.monitor.listener.TestListener2;
import xm.bibibiradio.monitor.notifier.MonitorNotifier;
import xm.bibibiradio.monitor.notifier.TestNotifier;

public class MonitorTest {

    //@Test
    public void test() {
        Monitor.getMonitor().register(0, new TestListener());
        
        MonitorNotifier notifier = new TestNotifier();
        notifier.run();
    }
    
    @Test
    public void test2() {
        Monitor.getMonitor().register(0, new TestListener2());
        
        MonitorNotifier notifier = new TestNotifier();
        for(int i=0;i<22;i++)
            notifier.run();
    }

}
