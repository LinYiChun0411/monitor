package com.aiinspector.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.Data;

@Data
public class HttpUtil {
	private HttpConfig httpConfig;	

	public HttpUtil(HttpConfig httpConfig) {
		this.httpConfig = httpConfig;
	}

	public ResponseEntity getHttp(String uri) {
		HttpEntity<String> requestEntity = new HttpEntity<String>("", httpConfig.getHeaders());
		return httpConfig.getRestTemplate().exchange(httpConfig.getServer() + uri, HttpMethod.GET, requestEntity, String.class);

	}

	public ResponseEntity postHttp(String uri, MultiValueMap<String, String> bodyMap) {
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(bodyMap,
				httpConfig.getHeaders());
		return httpConfig.getRestTemplate().postForEntity(httpConfig.getServer() + uri, requestEntity, String.class);
	}

	@Data
	public static class HttpConfig {
		private String server;
		private HttpHeaders headers;
		private RestTemplate restTemplate;
	}

}
