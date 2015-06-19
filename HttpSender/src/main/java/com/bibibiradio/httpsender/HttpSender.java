package com.bibibiradio.httpsender;

import java.io.File;
import java.util.Map;

public interface HttpSender {
	public ResponseData send(String url,int method,Map<String,String> header,byte[] body);
	public boolean setPeerCerts(File[] certs);
	public boolean setMyCerts(File[] certs);
	public void setTimeout(long timeout);
	public void setRetryTime(long retryTime);
	public void setSendFreq(long sendFreq);
}