package com.aiinspector.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${inspector.check.check-gamelist-server}")
	private String gameListServerString;

	@Value("${inspector.check.check-epgs-server}")
	private String epgsServerString;

	@Bean
	public HttpUtil gameListHttp() {
		HttpConfig httpConfig = new HttpConfig();
		HttpHeaders headers = new HttpHeaders();
		httpConfig.setServer(gameListServerString);
		httpConfig.setHeaders(headers);
		httpConfig.setRestTemplate(restTemplate);
		return new HttpUtil(httpConfig);
	}

	@Bean
	public HttpUtil epgsHttp() {
		HttpConfig httpConfig = new HttpConfig();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("user-agent", "CheckRuntime");
		httpConfig.setServer(epgsServerString);
		httpConfig.setHeaders(headers);
		httpConfig.setRestTemplate(restTemplate);
		return new HttpUtil(httpConfig);
	}

}
