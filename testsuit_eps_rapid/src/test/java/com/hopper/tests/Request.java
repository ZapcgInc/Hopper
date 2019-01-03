package com.hopper.tests;

import java.util.HashMap;
import java.util.Map;

public class Request {
	String url;
	String version;
	String uri;
	String authKey;
	Map<String, String> headers;
	Map<String, String> params;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getAuthKey() {
		return authKey;
	}
	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = new HashMap<String, String>(headers);
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = new HashMap<String, String>(params);;
	}
	
	public String getEndPoint() {
		StringBuffer endPoint = new StringBuffer(url);
		
		if(version != null) {
			endPoint.append("/");
			endPoint.append(version);
		}
		
		if(uri != null) {
			endPoint.append("/");
			endPoint.append(uri);
		}
		return endPoint.toString();
	}
}
