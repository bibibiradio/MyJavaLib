package xm.bibibiradio.monitor.monitor;

public class NotifyMessage {
    private int hashcode;
    private int level;
    private Object content;
    public int getHashcode() {
        return hashcode;
    }
    public void setHashcode(int hashcode) {
        this.hashcode = hashcode;
    }
    
    public Object getContent() {
        return content;
    }
    public void setContent(Object content) {
        this.content = content;
    }
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    
    
}
