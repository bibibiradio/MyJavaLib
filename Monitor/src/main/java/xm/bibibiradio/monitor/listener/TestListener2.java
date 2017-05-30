package xm.bibibiradio.monitor.listener;

import xm.bibibiradio.monitor.monitor.NotifyMessage;

public class TestListener2 implements MonitorListener {
    private MsgAppender appender;
    
    public TestListener2(){
        appender = new MsgAppender();
        appender.setBuffSize(10);
        appender.setInterval(1000*10);
        appender.setMode(0);
    }
    @Override
    public void listen(NotifyMessage msg) {
        // TODO Auto-generated method stub
        String content = appender.append(msg);
        if(content != null){
            System.out.println(content);
        }
    }

}
