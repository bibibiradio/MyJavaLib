package com.bibibiradio.httpsender;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

import javax.net.ssl.*;

/**
 * 
 * Http客户端，支持GET,POST,PUT请求，支持设置请求头，设置代理，设置访问频率，设置超时时间，设置重试次数，设置自动跳转开关。
 * @version 1.1.4
 * @author xiaoleixl
 *
 */
public class HttpSenderImplV1 implements HttpSender {
    final private static Logger logger         = Logger.getLogger(HttpSenderImplV1.class);
    private HttpClient          client         = null;
    private String              proxyIp        = null;
    private int                 proxyPort      = -1;
    private int                 retryTime      = 0;
    private int                 timeout        = -1;
    private int                 soTimeout      = -1;
    private long                sendFreq       = -1;
    private long                lastSend       = -1;
    private boolean             isCodec        = false;
    private boolean             isAutoRedirect = true;
    private boolean             isCheckPeerCert = false;
    private String              charSet = "UTF-8";

    public HttpSenderImplV1() {
        //lastSend = System.currentTimeMillis();
    }

    /**
     * 可设置HTTP代理地址
     * @param proxyIp 代理IP，String类型
     * @param proxyPort 代理端口，int类型
     */
    public HttpSenderImplV1(String proxyIp, int proxyPort) {
        this.proxyIp = proxyIp;
        this.proxyPort = proxyPort;
    }

    @Override
    public ResponseData send(String url, int method, Map<String, String> header, byte[] body) throws Exception{
        ResponseData responseData = null;

        //请求失败后的重试次数
        for (int i = 0; i < retryTime + 1; i++) {
            responseData = oriSend(url, method, header, new String(body));
            if (responseData != null) {
                return responseData;
            }
        }

        return null;
    }

    @Override
    public ResponseData send2(String url, int method, Map<String, String> header, String body) throws Exception{
        ResponseData responseData = null;

        //请求失败后的重试次数
        for (int i = 0; i < retryTime + 1; i++) {
            responseData = oriSend(url, method, header, body);
            if (responseData != null) {
                return responseData;
            }
        }

        return null;
    }

    /**
     *
     * 实际发送Http请求
     * @param url url
     * @param method 0,Get;1,POST;2,PUT
     * @param header http头
     * @param body http的body
     * @return 请求返回结果
     */
    public ResponseData oriSend(String url, int method, Map<String, String> header, String body) throws Exception {
        // TODO Auto-generated method stub
        HttpRequestBase httpMethod = null;
        //HttpResponse reponse = null;
        Set<Entry<String, String>> headers = null;
        Iterator iter = null;
        byte[] content = null;
        HttpResponse response = null;
        ResponseDataImplV1 retData = new ResponseDataImplV1();

        //两次请求的间隔大于sendFreq ms
        long duTime = System.currentTimeMillis() - lastSend;
        if (duTime < sendFreq) {
            try {
                Thread.sleep(sendFreq - duTime);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                logger.error("error message", e);
                return null;
            }
        }
        lastSend = System.currentTimeMillis();

        if (client == null) {
            client = newHttpClient();
        }

        //处理GET POST PUT 请求
        if (method == 0) {
            httpMethod = new HttpGet(url);
        } else if (method == 1) {
            HttpPost httpPost = new HttpPost(url);
            if (body != null) {
                try {
                    httpPost.setEntity(new StringEntity(new String(body),charSet));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    logger.error("error message", e);
                }
            }
            httpMethod = httpPost;

        } else if (method == 2) {
            HttpPut httpPut = new HttpPut(url);
            if (body != null) {
                try {
                    httpPut.setEntity(new StringEntity(new String(body),charSet));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            httpMethod = httpPut;
        }

        //设置请求头
        if (header != null) {
            headers = header.entrySet();
            iter = headers.iterator();
            Entry<String, String> entry = null;
            if (iter != null) {
                while (iter.hasNext()) {
                    entry = (Entry<String, String>) iter.next();
                    httpMethod.addHeader(entry.getKey(), entry.getValue());
                }
            }
        }

        //执行请求
        try {
            response = client.execute(httpMethod);
            if (response == null) {
                return null;
            }

            HttpEntity entity = response.getEntity();

            //获取相应body
            content = readAllFromInputStream(entity.getContent());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("error message", e);
            return null;
        }

        //获取响应头
        Header[] allHeader = response.getAllHeaders();
        Map<String, String> mapHeader = new HashMap<String, String>();
        for (int i = 0; i < allHeader.length; i++) {
            Header rHead = allHeader[i];
            mapHeader.put(rHead.getName(), rHead.getValue());
        }
        retData.setResponseHeader(mapHeader);
        retData.setResponseContent(content);

        //获取响应状态
        retData.setStatusCode(response.getStatusLine().getStatusCode());

        return retData;
    }

    public boolean isCodec() {
        return isCodec;
    }

    public void setCodec(boolean isCodec) {
        this.isCodec = isCodec;
    }

    public String getProxyIp() {
        return proxyIp;
    }

    public void setProxyIp(String proxyIp) {
        this.proxyIp = proxyIp;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    @Override
    public void setHttpProxy(String proxyIp, int proxyPort) {
        // TODO Auto-generated method stub
        this.proxyIp = proxyIp;
        this.proxyPort = proxyPort;

    }

    @Override
    public boolean setPeerCerts(File[] certs) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean setMyCerts(File[] certs) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setTimeout(long timeout) {
        // TODO Auto-generated method stub
        this.timeout = (int) timeout;
    }

    @Override
    public void setRetryTime(long retryTime) {
        // TODO Auto-generated method stub
        this.retryTime = (int) retryTime;
    }

    @Override
    public void setSendFreq(long sendFreq) {
        // TODO Auto-generated method stub
        this.sendFreq = sendFreq;

    }

    public boolean isAutoRedirect() {
        return isAutoRedirect;
    }

    public void setAutoRedirect(boolean isAutoRedirect) {
        this.isAutoRedirect = isAutoRedirect;
    }

    @Override
    public boolean start() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        if (client != null) {
            client.getConnectionManager().shutdown();
        }
    }

    @Override
    public void setSoTimeout(long soTimeout) {
        // TODO Auto-generated method stub
        this.soTimeout = (int) soTimeout;
    }

    /**
     * 将返回数据流读出成byte[]
     * @param inputStream 请求返回的流
     * @return 请求返回数据
     */
    private byte[] readAllFromInputStream(InputStream inputStream) throws Exception {
        byte[] bytes = new byte[4096];
        int size = 0;
        ByteArrayOutputStream ba = new ByteArrayOutputStream();

        try {
            while ((size = inputStream.read(bytes)) > 0) {
                ba.write(bytes, 0, size);
            }
        } finally {
            inputStream.close();
        }
        return ba.toByteArray();
    }

    /**
     * 底层生成HttpClient实例
     * @return HttpClient客户端
     */
    private HttpClient newHttpClient() throws Exception {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        RequestConfig.Builder rc = RequestConfig.custom();
        // 设置连接超时时间
        if (timeout != -1) {
            rc.setConnectTimeout(timeout);
        }

        // 设置无数据超时时间
        if (soTimeout != -1) {
            rc.setSocketTimeout(soTimeout);
        }

        // 设置http代理地址
        if (proxyIp != null && proxyPort != -1) {
            rc.setProxy(new HttpHost(
                    proxyIp, proxyPort));
        }

        if (!isAutoRedirect) {
            rc.setRedirectsEnabled(isAutoRedirect);
        }

        httpClientBuilder.setDefaultRequestConfig(rc.build());

        if (!isCheckPeerCert) {
            try {
                SSLContext ctx = SSLContext.getInstance("SSL");
                // Implementation of a trust manager for X509 certificates
                X509TrustManager tm = new X509TrustManager() {

                    public void checkClientTrusted(X509Certificate[] xcs,
                                                   String string) throws CertificateException {

                    }

                    public void checkServerTrusted(X509Certificate[] xcs,
                                                   String string) throws CertificateException {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                };
                ctx.init(null, new TrustManager[] { tm }, null);

                httpClientBuilder.setSSLContext(ctx);
                httpClientBuilder.setSSLHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        // TODO Auto-generated method stub
                        return true;
                    }
                });
            } catch (Exception ex) {
                throw ex;
            }
        }

        if (isCodec) {
            httpClientBuilder.addInterceptorLast(new HttpResponseInterceptor() {
                @Override
                public void process(HttpResponse response, HttpContext context)
                        throws HttpException, IOException {
                    HttpEntity entity = response.getEntity();
                    Header ceheader = entity.getContentEncoding();
                    if (ceheader != null) {
                        HeaderElement[] codecs = ceheader.getElements();
                        for (int i = 0; i < codecs.length; i++) {
                            if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                                response.setEntity(new GzipDecompressingEntity(
                                        response.getEntity()));
                                return;
                            }
                        }
                    }
                }
            });
        }

        CloseableHttpClient httpClient = httpClientBuilder.build();

        return httpClient;
    }

    public boolean isCheckPeerCert() {
        return isCheckPeerCert;
    }

    public void setCheckPeerCert(boolean checkPeerCert) {
        isCheckPeerCert = checkPeerCert;
    }

    @Override
    public void setCharSet(String charSet) {
        this.charSet=charSet;
    }
}
