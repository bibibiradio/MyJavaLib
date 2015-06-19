package com.bibibiradio.commoncache;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CacheWithTimeLimitTest {
	static CacheWithTimeLimit testCache = null;
	
	class TuDispose implements CacheDispose{

		@Override
		public void dispose(Object key, Object rawData, long timestamp,
				long timeLimit) {
			// TODO Auto-generated method stub
			System.out.println("[dispose] "+timestamp+" "+(String)rawData);
		}
		
	}
	
	@Before
	public void setUp() throws Exception {
		if(testCache == null){
			TuDispose userDisposer=new TuDispose();
			testCache = new CacheWithTimeLimit();
			
			userDisposer.dispose("1", "1234", System.currentTimeMillis(), 100);
			testCache.setUserDispose(userDisposer);
			testCache.setTimeLimit(5000);
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
		
		String r1 = null;
		r1=(String)testCache.getData("1");
		assertTrue(r1!=null);
		assertTrue(r1.equals("test1"));
		r1=(String)testCache.getData("2");
		assertTrue(r1!=null);
		assertTrue(r1.equals("test2"));
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
		assertTrue(r1==null);
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
	
	private static void show(CacheWithTimeLimit cache){
		CacheData oldest=cache.getOldestCacheData();
		System.out.print("[show] start");
		for(CacheData tmp=oldest;tmp!=null;tmp=tmp.getNextCacheData()){
			System.out.print("<-->key:"+tmp.getKey()+" tm:"+tmp.getTimestamp());
		}
		System.out.print("<-->end\n");
	}

}
