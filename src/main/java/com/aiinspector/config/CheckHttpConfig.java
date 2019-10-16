package com.aiinspector.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.aiinspector.util.HttpUtil;
import com.aiinspector.util.HttpUtil.HttpConfig;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class CheckHttpConfig {
	@Autowired
	RestTemplate restTemplate;
	
	@Bean
	public HttpUtil normalHttp() {
		HttpConfig httpConfig = new HttpConfig();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("user-agent", "CheckRuntime");
		httpConfig.setServer("");
		httpConfig.setHeaders(headers);
		httpConfig.setRestTemplate(restTemplate);
		return new HttpUtil(httpConfig);
	}

}
