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
			httpSender.setCodec(true);
			httpSender.setSendFreq(10000);
			//httpSender.setHttpProxy("127.0.0.1", 8080);
			httpSender.setAutoRedirect(false);
			httpSender.setCheckPeerCert(false);
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSend() throws Exception {
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
	
	//@Test
	public void testSend2() throws Exception {
		Map<String,String> header = new HashMap<String,String>();
		//header.put("Accept-Encoding", "compress;q=0,gzip;q=0");
		ResponseData response = httpSender.send("http://www.bilibili.com/video/av2893535/", 0, header, null);
		assertTrue(response != null);
		Set<Entry<String, String>> responseSet = response.getResponseHeader().entrySet();
		Iterator<Entry<String, String>> iter = responseSet.iterator();
		System.out.println(response.getStatusCode());
		while(iter.hasNext()){
			Entry<String,String> entry = iter.next();
			System.out.println(entry.getKey()+":"+entry.getValue());
		}
		System.out.println(new String(response.getResponseContent()));

	}
	
	//@Test
    public void testSend3() throws Exception {
        Map<String,String> header = new HashMap<String,String>();
        //header.put("Accept-Encoding", "compress;q=0,gzip;q=0");
        ResponseData response = httpSender.send("http://www.test.checkhtml.com/urljump_yes?param=http://xlxlxl/path", 0, header, null);
        assertTrue(response != null);
        Set<Entry<String, String>> responseSet = response.getResponseHeader().entrySet();
        Iterator<Entry<String, String>> iter = responseSet.iterator();
        System.out.println(response.getStatusCode());
        while(iter.hasNext()){
            Entry<String,String> entry = iter.next();
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
        System.out.println(new String(response.getResponseContent()));

    }

	//@Test
	public void testSen4() throws Exception {
		Map<String,String> header = new HashMap<String,String>();
		//header.put("Accept-Encoding", "compress;q=0,gzip;q=0");
		ResponseData response = httpSender.send("https://cdn.nhentai.xxx/g/1756709/1.jpg", 0, header, null);
		assertTrue(response != null);
		Set<Entry<String, String>> responseSet = response.getResponseHeader().entrySet();
		Iterator<Entry<String, String>> iter = responseSet.iterator();
		System.out.println(response.getStatusCode());
		while(iter.hasNext()){
			Entry<String,String> entry = iter.next();
			System.out.println(entry.getKey()+":"+entry.getValue());
		}
		System.out.println(new String(response.getResponseContent()));
	}

}
