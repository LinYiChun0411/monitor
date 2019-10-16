package com.aiinspector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@EnableConfigurationProperties
@Data
public class YAMLConfig {
	
	@Value("${name}")
	private String name;
	
	@Value("${environment}")
    private String environment;
}
