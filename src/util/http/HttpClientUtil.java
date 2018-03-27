package util.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class HttpClientUtil {
	private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);      
	private HttpClient httpClient = null;
	private long startTime = 0L;
	private long endTime = 0L;
	private int status = 0;
	
	 /** 
     * 0.成功 1.执行方法失败 2.协议错误 3.网络错误 
     *  
     * @return the status 
     */  
    public int getStatus() {  
        return status;  
    }  
  
    /** 
     * @param status 
     * the status to set 
     */  
    public void setStatus(int status) {  
        this.status = status;  
    }  
  
    /** 
     * @return the startTime 
     */  
    public long getStartTime() {  
        return startTime;  
    }  
  
    /** 
     * @return the endTime 
     */  
    public long getEndTime() {  
        return endTime;  
    } 
    
    
    public  HttpClientUtil(String url) {  
        if (StringUtils.isNotBlank(url)) {  
        	httpClient = new DefaultHttpClient();  
        }  
    } 
  
  
    /** 
     * 调用 API 
     *  
     * @param parameters 
     * @return 
     */  
    private String post(String url,String parameters) {  
        String body = null;  
        log.info("parameters:" + parameters);  
        HttpPost method = new HttpPost(url); 
        if (method != null & StringUtils.isNotBlank(parameters)) {  
            try {  
                // 建立一个NameValuePair数组，用于存储欲传送的参数  
                method.addHeader("Content-type","application/json; charset=utf-8");  
                method.setHeader("Accept", "application/json");  
                method.setEntity(new StringEntity(parameters, Charset.forName("UTF-8")));  
                startTime = System.currentTimeMillis();  
                HttpResponse response = httpClient.execute(method);  
                endTime = System.currentTimeMillis();  
                int statusCode = response.getStatusLine().getStatusCode();  
                log.info("statusCode:" + statusCode);  
                log.info("调用API 花费时间(单位：毫秒)：" + (endTime - startTime));  
                if (statusCode != HttpStatus.SC_OK) {  
                    log.error("Method failed:" + response.getStatusLine());  
                    status = 1;  
                }  
                // Read the response body  
                body = EntityUtils.toString(response.getEntity());  
  
            } catch (IOException e) {  
                // 网络错误  
                status = 3;  
                e.printStackTrace();
            } finally {  
                log.info("调用接口状态：" + status);  
            }  
  
        }  
        return body;  
    }  
    
    
    
    
    private String put(String url,String parameters){  
        String body = null;  
        log.info("parameters:" + parameters);  
        HttpPut method = new HttpPut(url);
        if (method != null & StringUtils.isNotBlank(parameters)) {  
            try {  
                // 建立一个NameValuePair数组，用于存储欲传送的参数  
                method.addHeader("Content-type","application/json; charset=utf-8");  
                method.setHeader("Accept", "application/json");  
                method.setEntity(new StringEntity(parameters, Charset.forName("UTF-8")));  
                startTime = System.currentTimeMillis();  
                HttpResponse response = httpClient.execute(method);  
                endTime = System.currentTimeMillis();  
                int statusCode = response.getStatusLine().getStatusCode();  
                log.info("statusCode:" + statusCode);  
                log.info("调用API 花费时间(单位：毫秒)：" + (endTime - startTime));  
                if (statusCode != HttpStatus.SC_OK) {  
                    log.error("Method failed:" + response.getStatusLine());  
                    status = 1;  
                }  
                // Read the response body  
                body = EntityUtils.toString(response.getEntity());  
  
            } catch (IOException e) {  
                // 网络错误  
                status = 3;  
                e.printStackTrace();
            } finally {  
                log.info("调用接口状态：" + status);  
            }  
  
        }  
        return body;  
    } 
  
    /**
     * 删除
     * @param url
     * @return
     */
    private String delete(String url){  
        String body = null;  
        HttpDelete method = new HttpDelete(url);
        if (method != null ) {  
            try {  
                // 建立一个NameValuePair数组，用于存储欲传送的参数  
                method.addHeader("Content-type","application/json; charset=utf-8");  
                method.setHeader("Accept", "application/json");  
                startTime = System.currentTimeMillis();  
                HttpResponse response = httpClient.execute(method);  
                endTime = System.currentTimeMillis();  
                int statusCode = response.getStatusLine().getStatusCode();  
                log.info("statusCode:" + statusCode);  
                log.info("调用API 花费时间(单位：毫秒)：" + (endTime - startTime));  
                if (statusCode != HttpStatus.SC_OK) {  
                    log.error("Method failed:" + response.getStatusLine());  
                    status = 1;  
                }  
                // Read the response body  
                body = EntityUtils.toString(response.getEntity());  
  
            } catch (IOException e) {  
                // 网络错误  
                status = 3;  
                e.printStackTrace();
            } finally {  
                log.info("调用接口状态：" + status);  
            }  
  
        }  
        return body;  
    } 
    
      
    private static String invoke(DefaultHttpClient httpclient,  
            HttpUriRequest httpost) {  
        HttpResponse response = sendRequest(httpclient, httpost);  
        String body = paseResponse(response);  
        return body;  
    }  
  
    private static String paseResponse(HttpResponse response) {  
        HttpEntity entity = response.getEntity();  
        log.info("response status: " + response.getStatusLine());  
        String charset = EntityUtils.getContentCharSet(entity);  
        log.info(charset);  
        String body = null;  
        try {  
            body = EntityUtils.toString(entity);  
            log.info(body);  
        } catch (ParseException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
          
        return body;  
    }  
  
    private static HttpResponse sendRequest(DefaultHttpClient httpclient,  
            HttpUriRequest httpost) {  
        HttpResponse response = null;  
        try {  
            response = httpclient.execute(httpost);  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return response;  
    }  
  
    private static HttpPost postForm(String url, Map<String, String> params){  
        HttpPost httpost = new HttpPost(url);  
        List<NameValuePair> nvps = new ArrayList <NameValuePair>();  
        Set<String> keySet = params.keySet();  
        for(String key : keySet) {  
            nvps.add(new BasicNameValuePair(key, params.get(key)));  
        }  
        try {  
            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
        return httpost;  
    }  
    
    
    /**
     * post map 请求
     * @param url
     * @param params
     * @return
     */
    public static String post(String url, Map<String, String> params) {  
        DefaultHttpClient httpclient = new DefaultHttpClient();  
        String body = null;  
        log.info("create httppost:" + url);  
        HttpPost post = postForm(url, params);  
        body = invoke(httpclient, post);  
        httpclient.getConnectionManager().shutdown();  
        return body;  
    }  
      
    /**
     * get 请求
     * @param url
     * @return
     */
    public static String get(String url) {  
    	log.info("create httpget:" + url);  
        DefaultHttpClient httpclient = new DefaultHttpClient();  
        String body = null;  
        HttpGet get = new HttpGet(url);  
        body = invoke(httpclient, get);  
        httpclient.getConnectionManager().shutdown();  
        return body;  
    }  
          
    /** 
     * post  json  
     * @param url 
     */  
    public static String  postJson(String url,JSONObject obj) {  
    	if(StringUtils.isNotBlank(url)){
            return new HttpClientUtil(url).post(url,obj.toString());
        }  
        return "";
    }  
    
    /**
     * put json
     * @param url
     * @param obj
     * @return
     */
    public static String  putJson(String url,JSONObject obj) {  
    	if(StringUtils.isNotBlank(url)){
            return new HttpClientUtil(url).put(url,obj.toString());
        }  
        return "";
    }  
    
    
    /**
     * delete
     * @param url
     * @return
     */
    public static String doDelete(String url){
    	if(StringUtils.isNotBlank(url)){
    		return new HttpClientUtil(url).delete(url);
    	}
    	return "";
    }
    
	public static void main(String[] args) {
		JSONObject obj = new JSONObject();
		obj.put("email", "112210@qq.com"); 
		obj.put("password", "123456");
		//CUD 用post请求
		//R 用get 请求
//		HttpClientUtil.postJson("http://127.0.0.1:8080/dmpcore/api/customer", obj);//insert
//		HttpClientUtil.postJson("http://127.0.0.1:8080/dmpcore/api/customer", obj);//update
//		HttpClientUtil.postJson("http://127.0.0.1:8080/dmpcore/api/delCustomer", obj);//post 方式 delete
//		HttpClientUtil.postJson("http://127.0.0.1:8080/dmpcore/api/customer", obj);//update 调用post请求发送
//		HttpClientUtil.doDelete("http://127.0.0.1:8080/dmpcore/api/customer/67");//delete
//		HttpClientUtil.postJson("http://127.0.0.1:8080/dmpcore/api/delCustomer/24", obj);//post 方式 delete
//		HttpClientUtil.postJson("http://127.0.0.1:8080/dmpcore/api/customer/login", obj);//login
		Map<String, String> params = new HashMap<String, String>();
		params.put("account", "112210@qq.com"); 
		params.put("password", "123456");
		params.put("token", "dae3cdd3dc3b4e82a1f2c2a2696e15212");
//		HttpClientUtil.post("http://127.0.0.1:8080/dmpcore/api/web/register", params);
//		HttpClientUtil.post("http://127.0.0.1:8080/dmpcore/api/web/login", params);//login
		HttpClientUtil.post("http://127.0.0.1:8080/dmpcore/api/web/customer/postTest", params);//login
		
		
	}
    
    
}
