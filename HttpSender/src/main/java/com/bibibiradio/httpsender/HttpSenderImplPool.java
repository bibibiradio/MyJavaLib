package com.bibibiradio.httpsender;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

/**
 * 包含HttpSender代理池的HttpSender<p>
 * 能够平均选择代理池中的代理发包，无效的代理将被失效一段时间重试，可以统一设置每个代理的发包速度等
 * @author xiaoleixl
 * @version 1.2.0
 */
public class HttpSenderImplPool implements HttpSender,HttpSenderPool {
    final static private Logger logger = Logger.getLogger(HttpSenderImplPool.class);
    /**
     * 内部类，代表一个代理的状态
     * @author xiaoleixl
     *
     */
    private class HttpSenderElement {
        private HttpSender httpSender;
        private ProxyContent proxyContent;
        private long lastVisitTime;
        private long errorCount;
        public HttpSender getHttpSender() {
            return httpSender;
        }
        public void setHttpSender(HttpSender httpSender) {
            this.httpSender = httpSender;
        }
        public long getLastVisitTime() {
            return lastVisitTime;
        }
        public void setLastVisitTime(long lastVisitTime) {
            this.lastVisitTime = lastVisitTime;
        }
        public long getErrorCount() {
            return errorCount;
        }
        public void setErrorCount(long errorCount) {
            this.errorCount = errorCount;
        }
        public ProxyContent getProxyContent() {
            return proxyContent;
        }
        public void setProxyContent(ProxyContent proxyContent) {
            this.proxyContent = proxyContent;
        }
        
    }
    
    private ConcurrentLinkedQueue<HttpSenderElement> httpSenderRuningSinglePool;
    private ConcurrentLinkedQueue<HttpSenderElement> httpSenderFailSinglePool;
    private ProxyContent[] proxys;
    private long limitErrorCount;
    private long invalidTime;
    
    private int retryTime = 0;
    private int timeout = -1;
    private int soTimeout = -1;
    private long sendFreq = -1;
    private boolean isCodec = false;
    private boolean isAutoRedirect = true;
    
    @Override
    public boolean start() {
        // TODO Auto-generated method stub
        initProxys(proxys);
        return true;
    }
    
    /**
     * 初始化代理池对应的HttpSender池
     * @param proxys
     * @return 成功?
     */
    private boolean initProxys(ProxyContent[] proxys){
        httpSenderRuningSinglePool = new ConcurrentLinkedQueue<HttpSenderElement>();
        httpSenderFailSinglePool = new ConcurrentLinkedQueue<HttpSenderElement>();
        
        HttpSender httpSender;
        for(ProxyContent proxy: proxys){
            httpSender = initHttpSender(proxy);
            if(httpSender != null){
                HttpSenderElement ele = new HttpSenderElement();
                ele.setHttpSender(httpSender);
                ele.setProxyContent(proxy);
                httpSenderRuningSinglePool.add(ele);
            }
        }
        
        return true;
    }
    
    /**
     * 根据某个代理初始化1个httpSender
     * @param proxy 代理内容
     * @return httpSender实例
     */
    private HttpSender initHttpSender(ProxyContent proxy){
        HttpSender httpSender = new HttpSenderImplV1();
        httpSender.setAutoRedirect(isAutoRedirect);
        httpSender.setCodec(isCodec);
        httpSender.setRetryTime(retryTime);
        httpSender.setSendFreq(sendFreq);
        httpSender.setSoTimeout(soTimeout);
        httpSender.setTimeout(timeout);
        
        if(!proxy.getIp().equals("0.0.0.0")){
            httpSender.setHttpProxy(proxy.getIp(), proxy.getPort());
        }
        
        if(httpSender.start()){
            return httpSender;
        }else{
            return null;
        }
        
    }
    
    @Override
    public void close() {
        // TODO Auto-generated method stub

    }
    
    @Override
    public ResponseData send(String url, int method, Map<String, String> header, byte[] body) {
        // TODO Auto-generated method stub
        validQueue();
        int poolRetryTime = httpSenderRuningSinglePool.size();
        int count = 0;
        ResponseData responseData = null;
        
        do{
            HttpSenderElement ele;
            ele = httpSenderRuningSinglePool.poll();
            if(ele == null){
                return null;
            }
            
            HttpSender httpSender = ele.getHttpSender();
            responseData = httpSender.send(url, method, header, body);
            if(responseData != null){
                ele.setLastVisitTime(System.currentTimeMillis());
                httpSenderRuningSinglePool.add(ele);
            }else{
                ele.setErrorCount(ele.getErrorCount()+1);
                logger.error(ele.getProxyContent().getIp()+":"+ele.getProxyContent().getPort()+" FAIL "+ele.getErrorCount());
                ele.setLastVisitTime(System.currentTimeMillis());
                if(ele.getErrorCount() >= limitErrorCount){
                    logger.error(ele.getProxyContent().getIp()+":"+ele.getProxyContent().getPort()+" PUTTO INVALID QUEUE ");
                    httpSenderFailSinglePool.add(ele);
                }else{
                    httpSenderRuningSinglePool.add(ele);
                }
            }
            count++;
        }while(responseData == null && count < poolRetryTime);
        
        return responseData;
    }
    
    private void validQueue(){
        int failSize = httpSenderFailSinglePool.size();
        if(failSize <= 0){
            return;
        }
        
        HttpSenderElement ele;
        int count = 0;
        while((ele = httpSenderFailSinglePool.poll()) != null && count < failSize){
            if(System.currentTimeMillis() - ele.getLastVisitTime() > invalidTime){
                ele.errorCount = 0;
                httpSenderRuningSinglePool.add(ele);
            }else{
                httpSenderFailSinglePool.add(ele);
            }
            count++;
        }
    }

    @Override
    public boolean setPeerCerts(File[] certs) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean setMyCerts(File[] certs) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setTimeout(long timeout) {
        // TODO Auto-generated method stub
        this.timeout = (int)timeout;
    }

    @Override
    public void setSoTimeout(long soTimeout) {
        // TODO Auto-generated method stub
        this.soTimeout = (int)soTimeout;
    }

    @Override
    public void setRetryTime(long retryTime) {
        // TODO Auto-generated method stub
        this.retryTime = (int)retryTime;
    }

    @Override
    public void setSendFreq(long sendFreq) {
        // TODO Auto-generated method stub
        this.sendFreq = sendFreq;
    }

    @Override
    public void setHttpProxy(String proxyIp, int proxyPort) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setAutoRedirect(boolean isAutoRedirect) {
        // TODO Auto-generated method stub
        this.isAutoRedirect = isAutoRedirect;
    }

    @Override
    public void setCodec(boolean isCodec) {
        // TODO Auto-generated method stub
        this.isCodec = isCodec;
    }

    @Override
    public void setProxys(ProxyContent[] proxys) {
        // TODO Auto-generated method stub
        this.proxys = proxys;
        
    }

    @Override
    public void setLimitErrorCount(long limitErrorCount) {
        // TODO Auto-generated method stub
        this.limitErrorCount = limitErrorCount;
    }

    @Override
    public void setInvalidTime(long invalidTime) {
        // TODO Auto-generated method stub
        this.invalidTime = invalidTime;
    }

}
