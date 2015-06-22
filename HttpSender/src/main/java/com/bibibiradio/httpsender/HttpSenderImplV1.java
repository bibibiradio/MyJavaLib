package com.bibibiradio.httpsender;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class HttpSenderImplV1 implements HttpSender {
	private static HttpClient client = null;
	private String proxyIp = null;
	private int proxyPort = -1;
	private int retryTime = 0;
	private int timeout = -1;
	private int soTimeout = -1;
	private long sendFreq = -1;
	private long lastSend = -1;
	
	public HttpSenderImplV1(){
		//lastSend = System.currentTimeMillis();
	}
	
	public HttpSenderImplV1(String proxyIp,int proxyPort){
		this.proxyIp = proxyIp;
		this.proxyPort = proxyPort;
	}
	
	@Override
	public ResponseData send(String url, int method,
			Map<String, String> header, byte[] body) {
		ResponseData responseData = null;
		
		//请求失败后的重试次数
		for(int i=0;i<retryTime+1;i++){
			responseData = oriSend(url,method,header,body);
			if(responseData != null){
				return responseData;
			}
		}
		
		return null;
	}
	
	public ResponseData oriSend(String url, int method,
			Map<String, String> header, byte[] body) {
		// TODO Auto-generated method stub
		HttpRequestBase httpMethod = null;
		HttpResponse reponse = null;
		Set<Entry<String, String>> headers = null;
		Iterator iter = null;
		byte[] content = null;
		HttpResponse response = null;
		ResponseDataImplV1 retData = new ResponseDataImplV1();
		
		//两次请求的间隔大于sendFreq ms
		long duTime = System.currentTimeMillis() - lastSend;
		if(duTime < sendFreq){
			try {
				Thread.sleep(sendFreq - duTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		lastSend = System.currentTimeMillis();
		
		if(client == null){
			client = newHttpClient();
		}
		
		//处理GET POST PUT 请求
		if(method == 0){
			httpMethod = new HttpGet(url);
		}else if(method == 1){
			HttpPost httpPost = new HttpPost(url);
			if(body != null){
				try {
					httpPost.setEntity(new StringEntity(new String(body)));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			httpMethod = httpPost;
			
		}else if(method == 2){
			HttpPut httpPut = new HttpPut(url);
			if(body != null){
				try {
					httpPut.setEntity(new StringEntity(new String(body)));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			httpMethod = httpPut;
		}
		
		//设置请求头
		if(header != null){
			headers = header.entrySet();
			iter = headers.iterator();
			Entry<String, String> entry = null;
			if(iter != null){
				while(iter.hasNext()){
					entry = (Entry<String, String>) iter.next();
					httpMethod.addHeader(entry.getKey(), entry.getValue());
				}
			}
		}
		
		//执行请求
		try {
			response = client.execute(httpMethod);
			if(response == null){
				return null;
			}
			
			HttpEntity entity = response.getEntity();
			
			//获取相应body
			content = readAllFromInputStream(entity.getContent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		//获取响应头
		Header[] allHeader = response.getAllHeaders();
		Map<String,String> mapHeader = new HashMap<String,String>();
		for(int i = 0;i < allHeader.length;i++){
			Header rHead = allHeader[i];
			mapHeader.put(rHead.getName(), rHead.getValue());
		}
		retData.setResponseHeader(mapHeader);
		retData.setResponseContent(content);
		
		//获取响应状态
		retData.setStatusCode(response.getStatusLine().getStatusCode());
		
		return retData;
	}
	
	
	
	public String getProxyIp() {
		return proxyIp;
	}



	public void setProxyIp(String proxyIp) {
		this.proxyIp = proxyIp;
	}



	public int getProxyPort() {
		return proxyPort;
	}



	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}
	
	@Override
	public void setHttpProxy(String proxyIp, int proxyPort) {
		// TODO Auto-generated method stub
		this.proxyIp = proxyIp;
		this.proxyPort = proxyPort;
		
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
		this.timeout = (int) timeout;
	}

	@Override
	public void setRetryTime(long retryTime) {
		// TODO Auto-generated method stub
		this.retryTime = (int) retryTime;
	}

	@Override
	public void setSendFreq(long sendFreq) {
		// TODO Auto-generated method stub
		this.sendFreq = sendFreq;
		
	}
	
	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		if(client != null){
			client.getConnectionManager().shutdown();
		}
	}
	
	@Override
	public void setSoTimeout(long soTimeout) {
		// TODO Auto-generated method stub
		this.soTimeout = (int) soTimeout;
	}
	
	private byte[] readAllFromInputStream(InputStream inputStream){
		StringBuilder sb1 = new StringBuilder();      
        byte[] bytes = new byte[4096];    
        int size = 0;    
          
        try {      
            while ((size = inputStream.read(bytes)) > 0) {    
                String str = new String(bytes, 0, size);    
                sb1.append(str);    
            }    
        } catch (IOException e) {
            e.printStackTrace();      
        } finally {
            try {      
            	inputStream.close();      
            } catch (IOException e) {      
            	e.printStackTrace();      
            } 
        }  
        return sb1.toString().getBytes();
	}
	
	private HttpClient newHttpClient(){
		HttpParams params =new BasicHttpParams();
		
		//设置连接超时时间
		if(timeout != -1){
			HttpConnectionParams.setConnectionTimeout(params, timeout);
		}
		
		//设置无数据超时时间
		if(soTimeout != -1){
			HttpConnectionParams.setSoTimeout(params, soTimeout);
		}
		
		//设置http代理地址
		if(proxyIp != null && proxyPort != -1){
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxyIp, proxyPort));
		}
		
		//处理连接超时时间
		ClientConnectionManager conMgr =new ThreadSafeClientConnManager();
		
		return new DefaultHttpClient(conMgr,params);
	}
}
