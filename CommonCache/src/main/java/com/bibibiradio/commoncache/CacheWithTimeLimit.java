package com.bibibiradio.commoncache;

import java.util.HashMap;

public class CacheWithTimeLimit {
	private long oldestTimestamp=-1;
	private long newestTimestamp=-1;
	private CacheData oldestCacheData=null;
	private CacheData newestCacheData=null;
	
	private CacheDispose userDispose=null;
	
	private HashMap<Object,CacheData> innerHashMap=null;
	
	private boolean needTimeLimit=true;
	private long timeLimit=-1;
	
	
	
	public CacheWithTimeLimit(){
		innerHashMap=new HashMap<Object,CacheData>();
		oldestCacheData=null;
		newestCacheData=null;
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
		
		removeCheckPointExecute();
		
		CacheData cData=new CacheData(key,rawData,timestamp);
		CacheData preCData = null;
		
		preCData = insertInHashMap(cData);
		
		if(preCData != null){
			removeFromChain(preCData);
			if(userDispose!=null){
				userDispose.dispose(key, preCData.getRawData(), preCData.getTimestamp(), timeLimit,3);
			}
		}
		
		if(!insertInChain(cData)){
			return false;
		}
		
		return true;
	}
	
	public Object getData(Object key){
		CacheData cData=innerHashMap.get(key);
		if(cData==null){
			return null;
		}
		return cData.getRawData();
	}
	
	public boolean removeData(Object key){
		CacheData needRemoveData=(CacheData) innerHashMap.remove(key);
		if(needRemoveData==null){
			return true;
		}
		removeFromChain(needRemoveData);
		if(userDispose!=null){
			userDispose.dispose(key, needRemoveData.getRawData(), needRemoveData.getTimestamp(), timeLimit,2);
		}
		removeCheckPointExecute();
		return true;
	}
	
	public void removeCheckPointExecute(){
		removeFromChainAndHashMap();
	}
	
	private boolean insertInChain(CacheData cData){
		if(oldestCacheData==null||newestCacheData==null){
			oldestCacheData=cData;
			newestCacheData=cData;
			oldestTimestamp=cData.getTimestamp();
			newestTimestamp=cData.getTimestamp();
			cData.setNextCacheData(null);
			cData.setPreCacheData(null);
		}else{
			//
			CacheData preTmp=null,nextTmp=null;
			preTmp=newestCacheData;
			nextTmp=null;
			while(preTmp!=null){
				if(preTmp.getTimestamp()<=cData.getTimestamp()){
					cData.setPreCacheData(preTmp);
					cData.setNextCacheData(nextTmp);
					if(preTmp!=null){
						preTmp.setNextCacheData(cData);
					}
					if(nextTmp!=null){
						nextTmp.setPreCacheData(cData);
					}
					break;
				}
				nextTmp=preTmp;
				preTmp=preTmp.getPreCacheData();
			}
			if(oldestCacheData.getTimestamp()>cData.getTimestamp()){
				cData.setPreCacheData(null);
				cData.setNextCacheData(oldestCacheData);
				oldestCacheData.setPreCacheData(cData);
				oldestCacheData=cData;
				oldestTimestamp=cData.getTimestamp();
			}else if(newestCacheData.getTimestamp()<=cData.getTimestamp()){
				newestCacheData=cData;
				newestTimestamp=cData.getTimestamp();
			}
		}
		return true;
	}
	
	private CacheData insertInHashMap(CacheData cData){
		return (CacheData) innerHashMap.put(cData.getKey(), cData);
	}
	
	private void removeFromChainAndHashMap(){
		if((!needTimeLimit)||timeLimit<=0){
			return;
		}
		long nowTime=System.currentTimeMillis();
		CacheData currentTmp=oldestCacheData;
		if(currentTmp==null){
			return;
		}
		
		while(currentTmp!=null){
			if(!isExpire(nowTime,currentTmp)){
				break;
			}
			CacheData needRemoveTmp=currentTmp;
			currentTmp=currentTmp.getNextCacheData();
			
			removeFromHashMap(needRemoveTmp);
			removeFromChain(needRemoveTmp);
			if(userDispose!=null){
				userDispose.dispose(needRemoveTmp.getKey(), needRemoveTmp.getRawData(), needRemoveTmp.getTimestamp(), timeLimit,1);
			}
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
	
	private void removeFromChain(CacheData cData){
		if(cData==oldestCacheData){
			oldestCacheData=cData.getNextCacheData();
		}
		if(cData==newestCacheData){
			newestCacheData=cData.getPreCacheData();
		}
		if(cData.getPreCacheData()!=null){
			cData.getPreCacheData().setNextCacheData(cData.getNextCacheData());
		}
		if(cData.getNextCacheData()!=null){
			cData.getNextCacheData().setPreCacheData(cData.getPreCacheData());
		}
		cData.setNextCacheData(null);
		cData.setPreCacheData(null);
	}

	public CacheDispose getUserDispose() {
		return userDispose;
	}

	public void setUserDispose(CacheDispose userDispose) {
		this.userDispose = userDispose;
	}

	public HashMap<Object, CacheData> getInnerHashMap() {
		return innerHashMap;
	}

	public void setInnerHashMap(HashMap<Object, CacheData> innerHashMap) {
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

	public CacheData getOldestCacheData() {
		return oldestCacheData;
	}

	public void setOldestCacheData(CacheData oldestCacheData) {
		this.oldestCacheData = oldestCacheData;
	}

	public CacheData getNewestCacheData() {
		return newestCacheData;
	}

	public void setNewestCacheData(CacheData newestCacheData) {
		this.newestCacheData = newestCacheData;
	}

	public boolean isNeedTimeLimit() {
		return needTimeLimit;
	}

	public void setNeedTimeLimit(boolean needTimeLimit) {
		this.needTimeLimit = needTimeLimit;
	}
	
}
