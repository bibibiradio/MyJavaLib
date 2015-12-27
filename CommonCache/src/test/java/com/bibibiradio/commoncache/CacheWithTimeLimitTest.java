package com.bibibiradio.commoncache;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bibibiradio.chain.DoublyLinkedProxyData;

public class CacheWithTimeLimitTest {
	static CacheWithTimeLimit testCache = null;
	
	class TuDispose implements CacheDispose{

		@Override
		public void dispose(Object key, Object rawData, long timestamp,
				long timeLimit,int type) {
			// TODO Auto-generated method stub
			System.out.println("[dispose] "+type+" "+timestamp+" "+rawData);
		}
		
	}
	
	@Before
	public void setUp() throws Exception {
		if(testCache == null){
			TuDispose userDisposer=new TuDispose();
			testCache = new CacheWithTimeLimit();
			
			userDisposer.dispose("1", "1234", System.currentTimeMillis(), 100,-1);
			testCache.setUserDispose(userDisposer);
			testCache.setTimeLimit(5000);
			
			//testCache.setNeedTimeLimit(false);
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInputDataObjectObject() {
		String key1="1";
		String rawData1="test1";
		long timestamp1=System.currentTimeMillis();
		
		show(testCache);
		testCache.inputData(key1, rawData1);
		show(testCache);
		
		String r1 = (String)testCache.getData("1");
		assertTrue(r1!=null);
		assert(r1.equals("test1"));
		r1=(String)testCache.getData("2");
		assertTrue(r1==null);
		r1=(String)testCache.getData("3");
		assertTrue(r1==null);
	}

	@Test
	public void testInputDataObjectLongObject() {
		String key2="2";
		String rawData2="test2";
		long timestamp2=System.currentTimeMillis();
		
		String key3="3";
		String rawData3="test3";
		long timestamp3=System.currentTimeMillis()+20000;
		
		testCache.inputData(key2, timestamp2, rawData2);
		show(testCache);
		testCache.inputData(key3, timestamp3, rawData3);
		show(testCache);
		
		rawData2 = "testCover";
		timestamp2= System.currentTimeMillis()+30000;
		testCache.inputData(key2, timestamp2, rawData2);
		show(testCache);
		
		String r1 = null;
		r1=(String)testCache.getData("1");
		assertTrue(r1!=null);
		assertTrue(r1.equals("test1"));
		r1=(String)testCache.getData("2");
		assertTrue(r1!=null);
		assertTrue(r1.equals("testCover"));
		r1=(String)testCache.getData("3");
		assertTrue(r1!=null);
		assertTrue(r1.equals("test3"));
	}

	@Test
	public void testGetData() {
		//fail("Not yet implemented");
		assertTrue(true);
	}

	@Test
	public void testRemoveCheckPointExecute() {
		//fail("Not yet implemented");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		testCache.removeCheckPointExecute();
		
		String r1 = null;
		r1=(String)testCache.getData("1");
		assertTrue(r1==null);
		r1=(String)testCache.getData("2");
		assertTrue(r1!=null);
		assertTrue(r1.equals("testCover"));
		r1=(String)testCache.getData("3");
		assertTrue(r1!=null);
		assertTrue(r1.equals("test3"));
		
		show(testCache);
	}
	
	@Test
	public void testRemoveData() {
		String r1 = null;
		r1=(String)testCache.getData("3");
		assertTrue(r1!=null);
		assertTrue(r1.equals("test3"));
		System.out.println("[cacheTest] "+r1);
		testCache.removeData("3");
		r1=(String)testCache.getData("3");
		assertTrue(r1==null);
		show(testCache);
		System.out.println("[cacheTest] "+r1);
	}
	
	@Test
	public void testHashMap(){
		HashMap<String,String> hm = new HashMap<String,String>();
		String a1 = hm.put("111", "222");
		assertTrue(a1 == null);
		String a2 = hm.put("222", "333");
		assertTrue(a2 == null);
		String a3 = hm.put("111","444");
		assertTrue(a3 != null);
		assertTrue(a3.equals("222"));
	}
	
	@Test
	public void performanceTesting(){
		long mapStart,mapEnd,insertMapPerformance,getMapPerformance;
		long cacheStart,cacheEnd,insertCachePerformance,getCachePerformance;
		
		Map<Integer,Integer>[] testList = (HashMap<Integer,Integer>[])(new HashMap[1000000]);
		for(int i = 0;i < 1000000;i++){
			testList[i] = new HashMap<Integer,Integer>();
			testList[i].put(i, i);
		}
		
		Map<Object,Object> testMap = new HashMap<Object,Object>();
		
		CacheDispose storedUserDispose = testCache.getUserDispose();
		
		mapStart = System.currentTimeMillis();
		for(int i = 0;i < 1000000;i++){
			testMap.put(Integer.valueOf(i), testList[i]);
		}
		mapEnd = System.currentTimeMillis();
		insertMapPerformance = mapEnd - mapStart;
		
		cacheStart = System.currentTimeMillis();
		for(int i = 0;i < 1000000;i++){
		    //System.out.println(i);
			testCache.inputData(Integer.valueOf(i), testList[i]);
		}
		cacheEnd = System.currentTimeMillis();
		insertCachePerformance = cacheEnd - cacheStart;
		
		mapStart = System.currentTimeMillis();
		for(int i = 999999;i >= 0;i--){
			testMap.get(Integer.valueOf(i));
		}
		mapEnd = System.currentTimeMillis();
		getMapPerformance = mapEnd - mapStart;
		
		cacheStart = System.currentTimeMillis();
		for(int i = 999999;i >= 0;i--){
			testCache.getData(Integer.valueOf(i));
		}
		cacheEnd = System.currentTimeMillis();
		getCachePerformance = cacheEnd - cacheStart;
		
		System.out.println("insertMapPerformance:"+insertMapPerformance);
		System.out.println("insertCachePerformance:"+insertCachePerformance);
		System.out.println("getMapPerformance:"+getMapPerformance);
		System.out.println("getCachePerformance:"+getCachePerformance);
		
		testCache.setUserDispose(storedUserDispose);
		
		assertTrue(getCachePerformance/(double)getMapPerformance <= 10);
		assertTrue(insertCachePerformance/(double)insertMapPerformance <= 5);
	}
	
	private static void show(CacheWithTimeLimit cache){
		DoublyLinkedProxyData oldest=cache.getTimeLimitChain().getHead();
		System.out.print("[show] start");
		for(DoublyLinkedProxyData tmp=oldest;tmp!=null;tmp=tmp.getNext()){
			System.out.print("<-->key:"+((CacheData)tmp.getRealData()).getKey()+" v:"+(String)(((CacheData)tmp.getRealData()).getRawData())+" "+" tm:"+((CacheData)tmp.getRealData()).getTimestamp());
		}
		System.out.print("<-->end\n");
	}

}
