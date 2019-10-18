package com.aiinspector;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.aiinspector")
@MapperScan("com.aiinspector.dao.mapper")
public class AIInspectorApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(AIInspectorApplication.class, args);
	}

}
