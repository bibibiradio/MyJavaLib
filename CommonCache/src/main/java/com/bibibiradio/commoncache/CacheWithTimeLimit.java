package com.bibibiradio.commoncache;

import java.util.HashMap;

import com.bibibiradio.chain.DoublyLinkedList;
import com.bibibiradio.chain.DoublyLinkedProxyData;

public class CacheWithTimeLimit {
	private long oldestTimestamp=-1;
	private long newestTimestamp=-1;
	
	private DoublyLinkedList chain;
	
	private CacheDispose userDispose=null;
	
	private HashMap<Object,DoublyLinkedProxyData> innerHashMap=null;
	
	private boolean needTimeLimit=true;
	private boolean autoCheckPoint=true;
	private long timeLimit=-1;
	
	
	
	public CacheWithTimeLimit(){
		innerHashMap=new HashMap<Object,DoublyLinkedProxyData>();
		chain = new DoublyLinkedList();
		oldestTimestamp=-1;
		newestTimestamp=-1;
		needTimeLimit=true;
		timeLimit=30000000;
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
		
		preCData = insertInHashMap(insertInChain(cData));
		
		if(preCData != null){
			chain.removeEleFromChain(preCData);
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
		chain.removeEleFromChain(needRemoveData);
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
		if(chain.getTail() == null){
			ret = chain.inputNextOfTail(cData);
		}else{
			//
			DoublyLinkedProxyData tmp;
			tmp=chain.getTail();
			while(tmp!=null){
				if(((CacheData)(tmp.getRealData())).getTimestamp()<=cData.getTimestamp()){
					ret = chain.inputNextOfEle(cData, tmp);
					break;
				}
				
				tmp=tmp.getPre();
			}
			
			if(tmp == null){
			    ret = chain.inputPreOfEle(cData, chain.getHead());
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
		DoublyLinkedProxyData currentTmp=chain.getHead();
		if(currentTmp==null){
			return;
		}
		
		while(currentTmp!=null){
			if(!isExpire(nowTime,(CacheData)currentTmp.getRealData())){
				break;
			}
			CacheData needRemoveTmp=(CacheData)currentTmp.getRealData();
			currentTmp=currentTmp.getNext();
			
			removeFromHashMap(needRemoveTmp);

			if(userDispose!=null){
				userDispose.dispose(needRemoveTmp.getKey(), needRemoveTmp.getRawData(), needRemoveTmp.getTimestamp(), timeLimit,1);
			}
		}
		
		if(currentTmp != null && currentTmp.getPre() != null){
		    chain.removeElesFromEleAndThePres(currentTmp.getPre());
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
	
//	private void removeFromChain(CacheData cData){
//		if(cData==oldestCacheData){
//			oldestCacheData=cData.getNextCacheData();
//		}
//		if(cData==newestCacheData){
//			newestCacheData=cData.getPreCacheData();
//		}
//		if(cData.getPreCacheData()!=null){
//			cData.getPreCacheData().setNextCacheData(cData.getNextCacheData());
//		}
//		if(cData.getNextCacheData()!=null){
//			cData.getNextCacheData().setPreCacheData(cData.getPreCacheData());
//		}
//		cData.setNextCacheData(null);
//		cData.setPreCacheData(null);
//	}

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

    public DoublyLinkedList getChain() {
        return chain;
    }

    public void setChain(DoublyLinkedList chain) {
        this.chain = chain;
    }
	
    
}
