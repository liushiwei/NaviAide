package com.carit.platform.response;


/**
 * <pre>
 * 功能说明：登录响应模型
 * </pre>
 * @author <a href="mailto:xiegengcai@gmail.com">Gengcai Xie</a>
 * 2012-9-21
 */
public class LogonResponse{

    private String sessionId;
    
    
	public LogonResponse() {
	}

	public LogonResponse(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}

