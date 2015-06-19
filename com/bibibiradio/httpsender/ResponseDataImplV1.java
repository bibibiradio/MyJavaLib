package com.bibibiradio.httpsender;

import java.util.Map;

public class ResponseDataImplV1 implements ResponseData {
	private int statusCode = -1;
	private byte[] responseContent = null;
	private Map<String,String> responseHeader = null;
	
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public byte[] getResponseContent() {
		return responseContent;
	}
	public void setResponseContent(byte[] responseContent) {
		this.responseContent = responseContent;
	}
	public Map<String, String> getResponseHeader() {
		return responseHeader;
	}
	public void setResponseHeader(Map<String, String> responseHeader) {
		this.responseHeader = responseHeader;
	}

	

}
