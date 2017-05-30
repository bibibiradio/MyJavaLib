package xm.bibibiradio.monitor.notifier;

import xm.bibibiradio.monitor.monitor.Monitor;
import xm.bibibiradio.monitor.monitor.NotifyMessage;

public class TestNotifier implements MonitorNotifier {

    @Override
    public void run() {
        // TODO Auto-generated method stub
        NotifyMessage msg = new NotifyMessage();
        msg.setHashcode("TESTTEST123".hashCode());
        msg.setLevel(Monitor.LEVEL0);
        msg.setContent("TESTTEST123");
        
        Monitor.getMonitor().notify(msg);
    }

}
