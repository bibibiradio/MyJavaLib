package com.bibibiradio.commoncache;


public class CacheData {
	private Object key;
	private Object rawData;
	private long timestamp;
	
	public CacheData(Object key,Object rawData,long timestamp){
		this.key=key;
		this.rawData = rawData;
		this.timestamp=timestamp;
	}
	
	public CacheData(){}


	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

    public Object getRawData() {
        return rawData;
    }

    public void setRawData(Object rawData) {
        this.rawData = rawData;
    }
	
}
