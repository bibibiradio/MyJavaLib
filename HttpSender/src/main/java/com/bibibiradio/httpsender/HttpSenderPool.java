package com.bibibiradio.httpsender;

/**
 * HttpSender池接口，用于代理池
 * @author xiaoleixl
 * @version 1.2.0
 */
public interface HttpSenderPool {
    /**
     * 设置代理池的代理
     * @param proxys 代理列表
     */
    public void setProxys(ProxyContent[] proxys);
    
    /**
     * 设置某个代理连接失败多少次后将被加入失效代理列表
     * @param limitErrorCount 失败次数阀值
     */
    public void setLimitErrorCount(long limitErrorCount);
    
    /**
     * 设置失效代理列表重试时间
     * @param invalidTime 失效时间
     */
    public void setInvalidTime(long invalidTime);
}
