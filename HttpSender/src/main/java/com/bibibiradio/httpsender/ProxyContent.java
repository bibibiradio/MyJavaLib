package com.bibibiradio.httpsender;

/**
 * 代理内容
 * @author xiaoleixl
 *
 */
public class ProxyContent {
    private String ip;
    private int port;
    public ProxyContent(){
        
    }
    
    public ProxyContent(String ip,int port){
        this.ip = ip;
        this.port = port;
    }
    
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    
    
}
