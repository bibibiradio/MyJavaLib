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
    private String proxys="0.0.0.0:0,"
            + "101.66.253.22:8080,"
            + "101.80.52.192:9999,"
            + "183.13.45.244:3128,"
            + "123.7.78.157:9999,"
            + "159.226.224.180:3128,"
            + "220.164.109.38:80,"
            + "112.74.25.121:3128,"
            + "114.215.150.13:3128,"
            + "123.7.115.141:9797,"
            + "101.200.149.140:8888,"
            + "115.28.101.22:3128,"
            + "123.139.59.85:9999,"
            +"58.247.125.205:80,"
            +"123.56.28.196:8888,"
            +"122.72.18.160:80";
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
        httpSenderPool.setLimitErrorCount(1);
        
        httpSenderPool.setCodec(true);
        httpSenderPool.setSendFreq(1000);
        httpSenderPool.setAutoRedirect(false);
        httpSenderPool.setTimeout(5000);
        httpSenderPool.setSoTimeout(10000);
        httpSenderPool.setRetryTime(1);
        
        httpSenderPool.start();
        Map<String,String> header = new HashMap<String,String>();
        for(int i=0;i<100;i++){
            ResponseData response = httpSenderPool.send("http://www.bilibili.com/video/av2893535/", 0, header, null);
            //assertTrue(response != null);
            if(response == null){
                //System.out.println("timeout "+i);
                continue;
            }
            
            //System.out.println(response.getStatusCode()+" "+i);
            
        }
        
    }
    
    //@Test
    public void test2(){
        HttpSender hp;
        Map<String,String> header = new HashMap<String,String>();
        
        System.out.print("101.66.253.22:8080 ");
        hp = new HttpSenderImplV1();
        hp.setCodec(true);
        hp.setSendFreq(1000);
        hp.setAutoRedirect(false);
        hp.setTimeout(5000);
        hp.setSoTimeout(5000);
        hp.setRetryTime(3);
        hp.setHttpProxy("101.66.253.22", 8080);
        ResponseData response = hp.send("http://www.bilibili.com/video/av2893535/", 0, header, null);
        if(response == null){
            System.out.println("valid");
        }
    }

}
