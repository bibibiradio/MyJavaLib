package com.bibibiradio.commoncache;

public class CacheData {
	private Object rawData;
	private Object key;
	
	private CacheData preCacheData;
	private CacheData nextCacheData;
	
	private long timestamp;
	
	public CacheData(Object key,Object rawData,long timestamp){
		this.key=key;
		this.rawData=rawData;
		this.timestamp=timestamp;
	}
	
	public CacheData(){}

	public Object getRawData() {
		return rawData;
	}

	public void setRawData(Object rawData) {
		this.rawData = rawData;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public CacheData getPreCacheData() {
		return preCacheData;
	}

	public void setPreCacheData(CacheData preCacheData) {
		this.preCacheData = preCacheData;
	}

	public CacheData getNextCacheData() {
		return nextCacheData;
	}

	public void setNextCacheData(CacheData nextCacheData) {
		this.nextCacheData = nextCacheData;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
