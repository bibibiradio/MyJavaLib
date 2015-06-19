package com.bibibiradio.commoncache;

import java.util.HashMap;

public class CacheWithTimeLimit {
	private long oldestTimestamp=-1;
	private long newestTimestamp=-1;
	private CacheData oldestCacheData=null;
	private CacheData newestCacheData=null;
	
	private CacheDispose userDispose=null;
	
	private HashMap<Object,Object> innerHashMap=null;
	
	private boolean needTimeLimit=true;
	private long timeLimit=-1;
	
	
	
	public CacheWithTimeLimit(){
		innerHashMap=new HashMap<Object,Object>();
		oldestCacheData=null;
		newestCacheData=null;
		oldestTimestamp=-1;
		newestTimestamp=-1;
		needTimeLimit=true;
		timeLimit=30000000;
	}
	
	/*
	 * 插入数据
	 * @key 缓存的索引
	 * @rawData 需要缓存的数据
	 * @返回 插入数据是否成功
	 */
	public boolean inputData(Object key,Object rawData){
		return inputData(key,System.currentTimeMillis(),rawData);
	}
	
	/*
	 * 插入数据
	 * @key 缓存的索引
	 * @rawData 需要缓存的数据
	 * @timestamp 时间戳
	 * @返回 插入数据是否成功
	 */
	public boolean inputData(Object key,long timestamp,Object rawData){
		if(rawData==null||key==null){
			return false;
		}
		
		removeCheckPointExecute();
		
		CacheData cData=new CacheData(key,rawData,timestamp);
		if(!insertInChain(cData)){
			return false;
		}
		
		if(!insertInHashMap(cData)){
			return false;
		}
		
		return true;
	}
	
	/*
	 * 获取数据
	 * @key 缓存的索引
	 * @返回 索引值所对应的数据，如果数据为空，返回null
	 */
	public Object getData(Object key){
		CacheData cData=(CacheData)innerHashMap.get(key);
		if(cData==null){
			return null;
		}
		return cData.getRawData();
	}
	
	/*
	 * 删除数据
	 * @key 需删除数据的索引
	 * @返回 删除是否成功
	 */
	public boolean removeData(Object key){
		CacheData needRemoveData=(CacheData) innerHashMap.remove(key);
		if(needRemoveData==null){
			return true;
		}
		removeFromChain(needRemoveData);
		/*if(userDispose!=null){
			userDispose.dispose(key, needRemoveData.getRawData(), needRemoveData.getTimestamp(), timeLimit);
		}*/
		removeCheckPointExecute();
		return true;
	}
	
	/*
	 * 检查点
	 */
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
	
	private boolean insertInHashMap(CacheData cData){
		innerHashMap.put(cData.getKey(), cData);
		return true;
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
				userDispose.dispose(needRemoveTmp.getKey(), needRemoveTmp.getRawData(), needRemoveTmp.getTimestamp(), timeLimit);
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

	public HashMap<Object, Object> getInnerHashMap() {
		return innerHashMap;
	}

	public void setInnerHashMap(HashMap<Object, Object> innerHashMap) {
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
