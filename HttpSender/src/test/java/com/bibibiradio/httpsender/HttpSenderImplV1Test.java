package com.bibibiradio.httpsender;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bibibiradio.httpsender.HttpSender;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpSenderImplV1Test {
	private HttpSender httpSender = null;
	@Before
	public void setUp() throws Exception {
		if(httpSender == null){
			//httpSender = new HttpSenderImplV1("127.0.0.1",8080);
			httpSender = new HttpSenderImplV1();
			httpSender.setSendFreq(10000);
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSend() {
		ResponseData response = httpSender.send("https://www.baidu.com", 0, null, "123".getBytes());
		assertTrue(response != null);
		Set<Entry<String, String>> responseSet = response.getResponseHeader().entrySet();
		Iterator<Entry<String, String>> iter = responseSet.iterator();
		System.out.println(response.getStatusCode());
		while(iter.hasNext()){
			Entry<String,String> entry = iter.next();
			System.out.println(entry.getKey()+":"+entry.getValue());
		}
		System.out.println(new String(response.getResponseContent()));
		
		response = httpSender.send("https://www.baidu.com", 0, null, "123".getBytes());
		assertTrue(response != null);
	}

}
