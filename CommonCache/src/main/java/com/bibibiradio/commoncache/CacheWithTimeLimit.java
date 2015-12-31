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
	
	private HashMap<Object,CacheData> innerHashMap=null;
	
	private boolean needTimeLimit=true;
	private boolean needAccessTimeLimit=true;
	private boolean autoCheckPoint=true;
	private long timeLimit=-1;
	private long accessTimeLimit = -1;
	
	
	
	public CacheWithTimeLimit(){
		innerHashMap=new HashMap<Object,CacheData>();
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
		
		CacheData cData=new CacheData(key,rawData,timestamp,timestamp);
		CacheData preCData = null;
		
		insertInTimeLimitChain(cData);
		insertInAccessChain(cData);
		preCData = insertInHashMap(cData);
		
		if(preCData != null){
		    timeLimitChain.removeEleFromChain(preCData.getTimeLimitChainEleRef());
		    accessChain.removeEleFromChain(preCData.getAccessChainEleRef());
			if(userDispose!=null){
				userDispose.dispose(key, preCData.getRawData(), preCData.getInsertTimestamp(), timeLimit,3);
			}
		}
		
		return true;
	}
	
	public Object getData(Object key){
		CacheData cData=innerHashMap.get(key);
		if(cData==null){
			return null;
		}
		if(System.currentTimeMillis()%7 == 0){
    		accessChain.removeEleFromChain(cData.getAccessChainEleRef());
    		cData.setAccessTimestamp(System.currentTimeMillis());
    		insertInAccessChain(cData);
		}
		return cData.getRawData();
	}
	
	public boolean removeData(Object key){
	    CacheData needRemoveData=innerHashMap.remove(key);
		if(needRemoveData==null){
			return true;
		}
		timeLimitChain.removeEleFromChain(needRemoveData.getTimeLimitChainEleRef());
		accessChain.removeEleFromChain(needRemoveData.getAccessChainEleRef());
		if(userDispose!=null){
			userDispose.dispose(key, needRemoveData.getRawData(), needRemoveData.getInsertTimestamp(), timeLimit,2);
		}
		
		if(autoCheckPoint == true){
		    removeCheckPointExecute();
		}
		return true;
	}
	
	public void removeCheckPointExecute(){
		removeFromChainAndHashMap();
	}
	
	private void insertInTimeLimitChain(CacheData cData){
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
				if(((CacheData)(tmp.getRealData())).getInsertTimestamp()<=cData.getInsertTimestamp()){
					ret = timeLimitChain.inputNextOfEle(cData, tmp);
					isInsert = true;
					break;
				}
			}
			
			if(!isInsert){
			    ret = timeLimitChain.inputPreOfEle(cData, timeLimitChain.getHead());
			}
		}
		cData.setTimeLimitChainEleRef(ret);
	}
	
	private void insertInAccessChain(CacheData cData){
        DoublyLinkedProxyData ret = null;
        if(accessChain.getTail() == null){
            ret = accessChain.inputNextOfTail(cData);
        }else{
            //
            Iterator<DoublyLinkedProxyData> iter = accessChain.getBackIterator();
            DoublyLinkedProxyData tmp = null;
            boolean isInsert = false;
            while(iter.hasNext()){
                tmp = iter.next();
                if(((CacheData)(tmp.getRealData())).getAccessTimestamp()<=cData.getAccessTimestamp()){
                    ret = accessChain.inputNextOfEle(cData, tmp);
                    isInsert = true;
                    break;
                }
            }
            
            if(!isInsert){
                ret = accessChain.inputPreOfEle(cData, accessChain.getHead());
            }
        }
        cData.setAccessChainEleRef(ret);
    }
	
	private CacheData insertInHashMap(CacheData cData){
		return (CacheData) innerHashMap.put(cData.getKey(), cData);
	}
	
	private void removeFromChainAndHashMap(){
	    long nowTime=System.currentTimeMillis();
	    
		if(needTimeLimit && timeLimit > 0){
    		DoublyLinkedProxyData currentTmp = null;
    		
    		Iterator<DoublyLinkedProxyData> iter = timeLimitChain.getForwardIterator();
    		while(iter.hasNext()){
    		    currentTmp = iter.next();
    			if(!isExpire(nowTime,((CacheData)currentTmp.getRealData()).getInsertTimestamp(),timeLimit)){
    				break;
    			}
    			CacheData needRemoveTmp=(CacheData)currentTmp.getRealData();
    			accessChain.removeEleFromChain(needRemoveTmp.getAccessChainEleRef());
    			removeFromHashMap(needRemoveTmp);
    
    			if(userDispose!=null){
    				userDispose.dispose(needRemoveTmp.getKey(), needRemoveTmp.getRawData(), needRemoveTmp.getInsertTimestamp(), timeLimit,1);
    			}
    		}
    		
    		if(currentTmp != null && currentTmp.getPre() != null){
    		    timeLimitChain.removeElesFromEleAndThePres(currentTmp.getPre());
    		}
		}
		
		if(needAccessTimeLimit && accessTimeLimit > 0){
		    DoublyLinkedProxyData currentTmp = null;
            
            Iterator<DoublyLinkedProxyData> iter = accessChain.getForwardIterator();
            while(iter.hasNext()){
                currentTmp = iter.next();
                if(!isExpire(nowTime,((CacheData)currentTmp.getRealData()).getAccessTimestamp(),accessTimeLimit)){
                    break;
                }
                CacheData needRemoveTmp=(CacheData)currentTmp.getRealData();
                timeLimitChain.removeEleFromChain(needRemoveTmp.getTimeLimitChainEleRef());
                removeFromHashMap(needRemoveTmp);
    
                if(userDispose!=null){
                    userDispose.dispose(needRemoveTmp.getKey(), needRemoveTmp.getRawData(), needRemoveTmp.getInsertTimestamp(), timeLimit,0);
                }
            }
            
            if(currentTmp != null && currentTmp.getPre() != null){
                accessChain.removeElesFromEleAndThePres(currentTmp.getPre());
            }
		}
	}
	
	private boolean isExpire(long nowTimestamp,long checkTimestamp,long timeLimit){
		if(timeLimit<=nowTimestamp-checkTimestamp){
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

	public HashMap<Object, CacheData> getInnerHashMap() {
		return innerHashMap;
	}

	public void setInnerHashMap(HashMap<Object, CacheData> innerHashMap) {
		this.innerHashMap = innerHashMap;
	}
	

	public boolean isNeedAccessTimeLimit() {
        return needAccessTimeLimit;
    }

    public void setNeedAccessTimeLimit(boolean needAccessTimeLimit) {
        this.needAccessTimeLimit = needAccessTimeLimit;
    }

    public long getAccessTimeLimit() {
        return accessTimeLimit;
    }

    public void setAccessTimeLimit(long accessTimeLimit) {
        this.accessTimeLimit = accessTimeLimit;
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
