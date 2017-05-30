package xm.bibibiradio.monitor.listener;

import xm.bibibiradio.monitor.monitor.NotifyMessage;

public class TestListener implements MonitorListener {

    @Override
    public void listen(NotifyMessage msg) {
        // TODO Auto-generated method stub
        System.out.println(msg.getLevel()+" "+msg.getHashcode()+" "+msg.getContent().toString());
    }

}
