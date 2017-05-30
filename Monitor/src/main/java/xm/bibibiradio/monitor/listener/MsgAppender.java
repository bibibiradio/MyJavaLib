package xm.bibibiradio.monitor.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import xm.bibibiradio.monitor.monitor.NotifyMessage;

public class MsgAppender {
    private Map<Integer, List<NotifyMessage>> mapMsgs           = new HashMap<Integer, List<NotifyMessage>>();

    private int                               NotifyMessageNum  = 0;
    private long                              lastDealTimestamp = System.currentTimeMillis();

    private int                               buffSize;
    private long                              interval;
    private int                               mode;

    public MsgAppender() {

    }

    public String append(NotifyMessage msg) {
        List<NotifyMessage> msgs = mapMsgs.get(msg.getHashcode());
        if (msgs == null) {
            msgs = new ArrayList<NotifyMessage>();
            mapMsgs.put(msg.getHashcode(), msgs);
        }

        msgs.add(msg);
        NotifyMessageNum++;

        if (NotifyMessageNum > 0) {
            if (NotifyMessageNum > buffSize
                || System.currentTimeMillis() - lastDealTimestamp > interval) {
                String content = doDeal();

                mapMsgs.clear();
                NotifyMessageNum = 0;
                lastDealTimestamp = System.currentTimeMillis();
                return content;
            } else {
                return null;
            }
        }else{
            return null;
        }

    }

    private String doDeal() {
        StringBuilder sb = new StringBuilder().append("");
        for (Entry<Integer, List<NotifyMessage>> msgsEntry : mapMsgs.entrySet()) {
            List<NotifyMessage> msgs = msgsEntry.getValue();

            if (mode == 0) {
                NotifyMessage msg = msgs.get(0);
                sb.append("\n\n");
                sb.append(msg.getContent() + " " + msg.getHashcode() + " " + msgs.size());
            } else if (mode == 1) {
                for (NotifyMessage msg : msgs) {
                    sb.append("\n");
                    sb.append(msg.getContent() + " " + msg.getHashcode());
                }
            }
        }

        return sb.toString();
    }

    public Map<Integer, List<NotifyMessage>> getMapMsgs() {
        return mapMsgs;
    }

    public void setMapMsgs(Map<Integer, List<NotifyMessage>> mapMsgs) {
        this.mapMsgs = mapMsgs;
    }

    public int getNotifyMessageNum() {
        return NotifyMessageNum;
    }

    public void setNotifyMessageNum(int notifyMessageNum) {
        NotifyMessageNum = notifyMessageNum;
    }

    public long getLastDealTimestamp() {
        return lastDealTimestamp;
    }

    public void setLastDealTimestamp(long lastDealTimestamp) {
        this.lastDealTimestamp = lastDealTimestamp;
    }

    public int getBuffSize() {
        return buffSize;
    }

    public void setBuffSize(int buffSize) {
        this.buffSize = buffSize;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
    
    
}
