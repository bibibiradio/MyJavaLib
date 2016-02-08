package com.bibibiradio.httpsender;

public class ProxysParse {
    static public ProxyContent[] parse(String proxys){
        String[] proxyStrs = proxys.split(",");
        ProxyContent[] proxyContents = new ProxyContent[proxyStrs.length];
        
        int i=0;
        for(String proxyOne : proxyStrs){
            String[] proxy = proxyOne.split(":");
            ProxyContent proxyContent = new ProxyContent();
            proxyContent.setIp(proxy[0]);
            proxyContent.setPort(Integer.valueOf(proxy[1]));
            proxyContents[i] = proxyContent;
            i++;
        }
        return proxyContents;
    }
}
