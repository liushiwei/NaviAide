package com.carit.platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import com.carit.platform.response.CommonRopResponse;
import com.carit.platform.response.LogonResponse;
import com.google.gson.Gson;

/**
 * <p>
 * <b>功能说明：</b>
 * </p>
 * @author <a href="mailto:xiegengcai@gmail.com">Gengcai Xie</a>
 * @date 2012-9-24
 */
public class CaritClient {
	
	public enum MessageFormat{
		xml, json;
	}
	
	public enum Local{
		zh_CN, en;
	}
	
	public static final String SYSTEM_PARAM_APPKEY="appKey";
	public static final String SYSTEM_PARAM_MESSAGE_FORMAT="messageFormat";
	public static final String SYSTEM_PARAM_METHOD="method";
	public static final String SYSTEM_PARAM_VERSION="v";
	public static final String SYSTEM_PARAM_LOCALE="locale";
	public static final String SYSTEM_PARAM_SIGN="sign";
	public static final String SYSTEM_PARAM_SESSION_ID="sessionId";
	
	public static final String HTTP_METHOD_GET="GET";
	public static final String HTTP_METHOD_POST="POST";
	
	//服务地址
    private String serverUrl;

    //应用键
    private String appKey;

    //应用密钥
    private String appSecret;

    private String sessionId;
    
    private String locale = Locale.SIMPLIFIED_CHINESE.toString();
    
    private String messageFormat=MessageFormat.json.toString();
    
    private Timer timer;
    
    private static class ClientHolder {
    	public static CaritClient INSTANCE=new CaritClient();
    }
    
    private CaritClient(){
    	try {
			serverUrl="http://www.android-in-car.com:8088/router?";
			appKey="1";
			appSecret="abcdeabcdeabcdeabcdeabcde";
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public static CaritClient getInstance(){
    	return ClientHolder.INSTANCE;
    }
    
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
	
    public String getMessageFormat() {
		return messageFormat;
	}

	public void setMessageFormat(String messageFormat) {
		this.messageFormat = messageFormat;
	}

	public Map<String, String> buildParamValues(String method, String version) {
		Map<String, String> paramValues=new HashMap<String, String>();
		paramValues.put(SYSTEM_PARAM_METHOD, method);
		paramValues.put(SYSTEM_PARAM_APPKEY, ClientHolder.INSTANCE.appKey);
		paramValues.put(SYSTEM_PARAM_VERSION, version);
		paramValues.put(SYSTEM_PARAM_LOCALE, Local.zh_CN.toString());
		paramValues.put(SYSTEM_PARAM_MESSAGE_FORMAT, CaritClient.MessageFormat.json.toString());
		return paramValues;
	}
	
	public Map<String, String> buildParamValues(String method, String version, String sessionId) {
		Map<String, String> paramValues=new HashMap<String, String>();
		paramValues.put(SYSTEM_PARAM_METHOD, method);
		paramValues.put(SYSTEM_PARAM_APPKEY, ClientHolder.INSTANCE.appKey);
		paramValues.put(SYSTEM_PARAM_VERSION, version);
		paramValues.put(SYSTEM_PARAM_LOCALE, Local.zh_CN.toString());
		paramValues.put(SYSTEM_PARAM_MESSAGE_FORMAT, CaritClient.MessageFormat.json.toString());
		paramValues.put(SYSTEM_PARAM_SESSION_ID, sessionId);
		return paramValues;
	}
	
	/**
	 * 获取session
	 * @throws Exception
	 */
	public void getSession() throws Exception{
		if(getInstance().getSessionId()==null){
			Map<String, String> paramValues=getInstance().buildParamValues("platform.getSession", "1.0");
			// 生成签名
			String sign=ClientUtils.sign(paramValues, getInstance().appSecret);
			// 不需要签名的参数放后面
			paramValues.put(SYSTEM_PARAM_SIGN, sign);
			// 获取响应
			String resonse=getHttpResponse(ClientUtils.buildRequestUrl(getInstance().serverUrl, paramValues), "POST");
			try{
			    Gson gson = new Gson();
				LogonResponse response=(LogonResponse) gson.fromJson(resonse, LogonResponse.class);
				getInstance().setSessionId(response.getSessionId());
				// 开启一个延时5分钟每间隔5分钟执行一次的定时器
				timer=new Timer("keep connection timer", false);
				timer.schedule(new TimerTask() {
					
					@Override
					public void run() {
						Map<String, String> paramValues=getInstance().buildParamValues("platform.heartbeat", "1.0", getInstance().getSessionId());
						// 生成签名
						String sign=ClientUtils.sign(paramValues, getInstance().appSecret);
						// 不需要签名的参数放后面
						paramValues.put(SYSTEM_PARAM_SIGN, sign);
						// 获取响应
						getHttpResponse(ClientUtils.buildRequestUrl(getInstance().serverUrl, paramValues));
					}
				}, 5*60*1000, 5*60*1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 删除session
	 * @throws Exception
	 */
	public void removeSession() throws Exception{
		if(getInstance().getSessionId()!=null){
			Map<String, String> paramValues=getInstance().buildParamValues("platform.removeSession", "1.0", getInstance().getSessionId());
			// 生成签名
			String sign=ClientUtils.sign(paramValues, getInstance().appSecret);
			// 不需要签名的参数放后面
			paramValues.put(SYSTEM_PARAM_SIGN, sign);
			// 获取响应
			String resonse=getHttpResponse(ClientUtils.buildRequestUrl(getInstance().serverUrl, paramValues), HTTP_METHOD_POST);
			try{
				//JsonUtil.jsonToObject(resonse, CommonRopResponse.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param url
	 * @param method
	 * @return
	 */
	public static String getHttpResponse(String url, String method) {
		String response = null;
		HttpURLConnection conn = null;
		BufferedReader reader = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod(method);
			int responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				reader = new BufferedReader(new InputStreamReader(
						conn.getInputStream(), "UTF-8"));
				// 防止多行响应数据，变更读取方法
				char [] cbuf=new char[2048];
				StringBuilder sb=new StringBuilder();
				int n=reader.read(cbuf);
				while (n!=-1) {
					sb.append(cbuf);
					n=reader.read(cbuf);
				}
//				response = reader.readLine();
				response=sb.toString();
			} else {
				response = "{\"httpError\":" + responseCode + "}";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	public String getHttpResponse(String url) {
		return getHttpResponse(url, HTTP_METHOD_GET);
	}
	
	public static void main(String[] args) throws Exception {
//		getInstance().getSession();
//		// 登录
//		Map<String, String> paramValues=getInstance().buildParamValues("account.logon", "1.0", getInstance().getSessionId());
//		paramValues.put("email", "xiegc@carit.com.cn");
//		
//		String sign=ClientUtils.sign(paramValues, ClientHolder.INSTANCE.appSecret);
//		// 不需要签名的参数放后面
//		paramValues.put("sign", sign);
//		paramValues.put("password", "123456");
//		String resonse=getHttpResponse(ClientUtils.buildRequestUrl(getInstance().serverUrl, paramValues), HTTP_METHOD_POST);
//		try {
//			AccountResponse account=(AccountResponse) JsonUtil.jsonToObject(resonse, AccountResponse.class);
//			System.out.println(account);
//		} catch (Exception e) {
//			try {
//				ErrorResponse error=(ErrorResponse) JsonUtil.jsonToObject(resonse, ErrorResponse.class);
//				System.out.println(error);
//			} catch (Exception e2) {
//				e.printStackTrace();
//			}
//		}
		
		Map<String, String> param=getInstance().buildParamValues("platform.location.search", "1.0");
		param.put("deviceId", "BD0321CE");
		param.put("type", "0");
		
		String sign=ClientUtils.sign(param, getInstance().appSecret);
		
		param.put(SYSTEM_PARAM_SIGN, sign);
		
		System.out.println(ClientUtils.buildRequestUrl(getInstance().getServerUrl(), param));
		
	}
}
