package com.bibibiradio.httpsender;

public class HttpSenderFactory {
    public HttpSender provide(String type) throws Exception{
        if(type.equals("implV1")){
            return new HttpSenderImplV1();
        }else if(type.equals("pooledImplV1")){
            return new HttpSenderImplPool();
        }else{
            throw new Exception("NO HTTPSENDER TYPE");
        }
    }
}
