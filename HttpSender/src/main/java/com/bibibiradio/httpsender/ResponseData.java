package com.bibibiradio.httpsender;

import java.util.Map;

public interface ResponseData {
    /**
     * Http返回码
     * @return 返回码
     */
	public int getStatusCode();
	
	/**
	 * Http返回数据
	 * @return 返回数据
	 */
	public byte[] getResponseContent();
	
	/**
	 * Http返回头
	 * @return 返回头Map
	 */
	public Map<String,String> getResponseHeader();
}
