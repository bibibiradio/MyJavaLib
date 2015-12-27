package com.bibibiradio.commoncache;

import java.util.HashMap;
import java.util.Iterator;

import com.bibibiradio.chain.DoublyLinkedList;
import com.bibibiradio.chain.DoublyLinkedProxyData;

public class CacheWithTimeLimit {
	private long oldestTimestamp=-1;
	private long newestTimestamp=-1;
	
	private DoublyLinkedList timeLimitChain;
	private DoublyLinkedList accessChain;
	
	private CacheDispose userDispose=null;
	
	private HashMap<Object,DoublyLinkedProxyData> innerHashMap=null;
	
	private boolean needTimeLimit=true;
	private boolean needAccessTimeLimit=true;
	private boolean autoCheckPoint=true;
	private long timeLimit=-1;
	private long accessTimeLimit = -1;
	
	
	
	public CacheWithTimeLimit(){
		innerHashMap=new HashMap<Object,DoublyLinkedProxyData>();
		timeLimitChain = new DoublyLinkedList();
		accessChain = new DoublyLinkedList();
		needTimeLimit=true;
		timeLimit=30000000;
		accessTimeLimit = 500000;
	}
	
	public boolean inputData(Object key,Object rawData){
		return inputData(key,System.currentTimeMillis(),rawData);
	}
	
	public boolean inputData(Object key,long timestamp,Object rawData){
		if(rawData==null||key==null){
			return false;
		}
		
		if(autoCheckPoint == true){
		    removeCheckPointExecute();
		}
		
		CacheData cData=new CacheData(key,rawData,timestamp);
		DoublyLinkedProxyData preCData = null;
		DoublyLinkedProxyData linkCDate = null;
		
		linkCDate = insertInChain(cData);
		preCData = insertInHashMap(linkCDate);
		
		if(preCData != null){
		    timeLimitChain.removeEleFromChain(preCData);
			if(userDispose!=null){
				userDispose.dispose(key, ((CacheData)preCData.getRealData()).getRawData(), ((CacheData)preCData.getRealData()).getTimestamp(), timeLimit,3);
			}
		}
		
		return true;
	}
	
	public Object getData(Object key){
		DoublyLinkedProxyData cData=innerHashMap.get(key);
		if(cData==null){
			return null;
		}
		return ((CacheData)cData.getRealData()).getRawData();
	}
	
	public boolean removeData(Object key){
	    DoublyLinkedProxyData needRemoveData=innerHashMap.remove(key);
		if(needRemoveData==null){
			return true;
		}
		timeLimitChain.removeEleFromChain(needRemoveData);
		if(userDispose!=null){
			userDispose.dispose(key, ((CacheData)needRemoveData.getRealData()).getRawData(), ((CacheData)needRemoveData.getRealData()).getTimestamp(), timeLimit,2);
		}
		
		if(autoCheckPoint == true){
		    removeCheckPointExecute();
		}
		return true;
	}
	
	public void removeCheckPointExecute(){
		removeFromChainAndHashMap();
	}
	
	private DoublyLinkedProxyData insertInChain(CacheData cData){
	    DoublyLinkedProxyData ret = null;
		if(timeLimitChain.getTail() == null){
			ret = timeLimitChain.inputNextOfTail(cData);
		}else{
			//
		    Iterator<DoublyLinkedProxyData> iter = timeLimitChain.getBackIterator();
			DoublyLinkedProxyData tmp = null;
			boolean isInsert = false;
			while(iter.hasNext()){
			    tmp = iter.next();
				if(((CacheData)(tmp.getRealData())).getTimestamp()<=cData.getTimestamp()){
					ret = timeLimitChain.inputNextOfEle(cData, tmp);
					isInsert = true;
					break;
				}
			}
			
			if(!isInsert){
			    ret = timeLimitChain.inputPreOfEle(cData, timeLimitChain.getHead());
			}
		}
		return ret;
	}
	
	private DoublyLinkedProxyData insertInHashMap(DoublyLinkedProxyData cData){
		return (DoublyLinkedProxyData) innerHashMap.put(((CacheData)(cData.getRealData())).getKey(), cData);
	}
	
	private void removeFromChainAndHashMap(){
		if((!needTimeLimit)||timeLimit<=0){
			return;
		}
		long nowTime=System.currentTimeMillis();
		DoublyLinkedProxyData currentTmp = null;
		
		Iterator<DoublyLinkedProxyData> iter = timeLimitChain.getForwardIterator();
		while(iter.hasNext()){
		    currentTmp = iter.next();
			if(!isExpire(nowTime,(CacheData)currentTmp.getRealData())){
				break;
			}
			CacheData needRemoveTmp=(CacheData)currentTmp.getRealData();
			removeFromHashMap(needRemoveTmp);

			if(userDispose!=null){
				userDispose.dispose(needRemoveTmp.getKey(), needRemoveTmp.getRawData(), needRemoveTmp.getTimestamp(), timeLimit,1);
			}
		}
		
		if(currentTmp != null && currentTmp.getPre() != null){
		    timeLimitChain.removeElesFromEleAndThePres(currentTmp.getPre());
		}
	}
	
	private boolean isExpire(long nowTimestamp,CacheData cData){
		if(timeLimit<=nowTimestamp-cData.getTimestamp()){
			return true;
		}
		return false;
	}
	
	private void removeFromHashMap(CacheData cData){
		Object key=cData.getKey();
		innerHashMap.remove(key);
	}

	public CacheDispose getUserDispose() {
		return userDispose;
	}

	public void setUserDispose(CacheDispose userDispose) {
		this.userDispose = userDispose;
	}

	public HashMap<Object, DoublyLinkedProxyData> getInnerHashMap() {
		return innerHashMap;
	}

	public void setInnerHashMap(HashMap<Object, DoublyLinkedProxyData> innerHashMap) {
		this.innerHashMap = innerHashMap;
	}

	public long getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(long timeLimit) {
		this.timeLimit = timeLimit;
	}

	public long getOldestTimestamp() {
		return oldestTimestamp;
	}

	public void setOldestTimestamp(long oldestTimestamp) {
		this.oldestTimestamp = oldestTimestamp;
	}

	public long getNewestTimestamp() {
		return newestTimestamp;
	}

	public void setNewestTimestamp(long newestTimestamp) {
		this.newestTimestamp = newestTimestamp;
	}

//	public CacheData getOldestCacheData() {
//		return oldestCacheData;
//	}
//
//	public void setOldestCacheData(CacheData oldestCacheData) {
//		this.oldestCacheData = oldestCacheData;
//	}
//
//	public CacheData getNewestCacheData() {
//		return newestCacheData;
//	}
//
//	public void setNewestCacheData(CacheData newestCacheData) {
//		this.newestCacheData = newestCacheData;
//	}

	public boolean isNeedTimeLimit() {
		return needTimeLimit;
	}

	public void setNeedTimeLimit(boolean needTimeLimit) {
		this.needTimeLimit = needTimeLimit;
	}

    public boolean isAutoCheckPoint() {
        return autoCheckPoint;
    }

    public void setAutoCheckPoint(boolean autoCheckPoint) {
        this.autoCheckPoint = autoCheckPoint;
    }

    public DoublyLinkedList getTimeLimitChain() {
        return timeLimitChain;
    }

    public void setTimeLimitChain(DoublyLinkedList timeLimitChain) {
        this.timeLimitChain = timeLimitChain;
    }

    public DoublyLinkedList getAccessChain() {
        return accessChain;
    }

    public void setAccessChain(DoublyLinkedList accessChain) {
        this.accessChain = accessChain;
    }

    
	
    
}
