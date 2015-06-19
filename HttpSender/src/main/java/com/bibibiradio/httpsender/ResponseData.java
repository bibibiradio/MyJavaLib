package com.bibibiradio.httpsender;

import java.util.Map;

public interface ResponseData {
	public int getStatusCode();
	public byte[] getResponseContent();
	public Map<String,String> getResponseHeader();
}
