package com.bibibiradio.httpsender;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HttpSenderImplPoolTest {
    private String proxys="0.0.0.0:0,115.218.124.80:9000,218.20.242.134:8090,119.7.90.166:9000,120.198.236.12:80";
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        HttpSenderImplPool httpSenderPool = new HttpSenderImplPool();
        httpSenderPool.setProxys(ProxysParse.parse(proxys));
        httpSenderPool.setInvalidTime(100000);
        httpSenderPool.setLimitErrorCount(2);
        
        httpSenderPool.setCodec(true);
        httpSenderPool.setSendFreq(1000);
        httpSenderPool.setAutoRedirect(false);
        httpSenderPool.setTimeout(5000);
        //httpSenderPool.setSoTimeout(5000);
        
        httpSenderPool.start();
        Map<String,String> header = new HashMap<String,String>();
        for(int i=0;i<100;i++){
            ResponseData response = httpSenderPool.send("http://www.bilibili.com/video/av2893535/", 0, header, null);
            //assertTrue(response != null);
            if(response == null){
                System.out.println("timeout "+i);
                continue;
            }
            
            System.out.println(response.getStatusCode()+" "+i);
            
        }
        
    }

}
