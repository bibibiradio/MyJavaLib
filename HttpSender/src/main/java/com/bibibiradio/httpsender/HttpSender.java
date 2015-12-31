package com.bibibiradio.httpsender;

import java.io.File;
import java.util.Map;

public interface HttpSender {
    /**
     * 发送Http请求
     * @param url url
     * @param method 0,Get;1,POST;2,PUT
     * @param header http头
     * @param body http的body
     * @return 请求返回结果
     */
	public ResponseData send(String url,int method,Map<String,String> header,byte[] body);
	
	/**
	 * 设置对方根及中间证书
	 * @param certs 证书文件列表
	 * @return 成功？
	 */
	public boolean setPeerCerts(File[] certs);
	
	/**
	 * 设置自己的客户端证书
	 * @param certs 自己的证书列表
	 * @return 成功？
	 */
	public boolean setMyCerts(File[] certs);
	
	/**
	 * 设置连接超时时间
	 * @param timeout 超时时间
	 */
	public void setTimeout(long timeout);
	
	/**
	 * 设置socket超时时间
	 * @param soTimeout 超时时间
	 */
	public void setSoTimeout(long soTimeout);
	
	/**
	 * 设置重试次数
	 * @param retryTime 重试次数
	 */
	public void setRetryTime(long retryTime);
	
	/**
	 * 设置发送频率
	 * @param sendFreq 两次http请求发送间隔，ms
	 */
	public void setSendFreq(long sendFreq);
	
	/**
	 * 设置http代理
	 * @param proxyIp 代理ip
	 * @param proxyPort 代理端口
	 */
	public void setHttpProxy(String proxyIp,int proxyPort);
	
	/**
	 * 设置是否自动跳转
	 * @param isAutoRedirect true，自动；false，不跳转
	 */
	public void setAutoRedirect(boolean isAutoRedirect);
	
	/**
	 * 初始化／启动httpSender
	 * @return 启动成功？
	 */
	public boolean start();
	
	/**
	 * 根据返回的content-type自动解压
	 * @param isCodec true，自动解压；false，返回原始数据
	 */
	public void setCodec(boolean isCodec);
	
	/**
	 * 关闭httpSender
	 */
	public void close();
}
