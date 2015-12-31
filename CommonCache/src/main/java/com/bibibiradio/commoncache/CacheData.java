package com.bibibiradio.commoncache;

import com.bibibiradio.chain.DoublyLinkedProxyData;


public class CacheData {
	private Object key;
	private Object rawData;
	private long InsertTimestamp;
	private long AccessTimestamp;
	private DoublyLinkedProxyData timeLimitChainEleRef;
	private DoublyLinkedProxyData accessChainEleRef;
	
	public CacheData(Object key,Object rawData,long InsertTimestamp,long AccessTimestamp){
		this.key=key;
		this.rawData = rawData;
		this.InsertTimestamp=InsertTimestamp;
		this.AccessTimestamp = AccessTimestamp;
	}
	
	public CacheData(){}


	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

    public long getInsertTimestamp() {
        return InsertTimestamp;
    }

    public void setInsertTimestamp(long insertTimestamp) {
        InsertTimestamp = insertTimestamp;
    }

    public long getAccessTimestamp() {
        return AccessTimestamp;
    }

    public void setAccessTimestamp(long accessTimestamp) {
        AccessTimestamp = accessTimestamp;
    }

    public Object getRawData() {
        return rawData;
    }

    public void setRawData(Object rawData) {
        this.rawData = rawData;
    }

    public DoublyLinkedProxyData getTimeLimitChainEleRef() {
        return timeLimitChainEleRef;
    }

    public void setTimeLimitChainEleRef(DoublyLinkedProxyData timeLimitChainEleRef) {
        this.timeLimitChainEleRef = timeLimitChainEleRef;
    }

    public DoublyLinkedProxyData getAccessChainEleRef() {
        return accessChainEleRef;
    }

    public void setAccessChainEleRef(DoublyLinkedProxyData accessChainEleRef) {
        this.accessChainEleRef = accessChainEleRef;
    }
	
}
