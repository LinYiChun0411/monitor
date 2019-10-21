package com.aiinspector.config;

import javax.mail.NoSuchProviderException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aiinspector.alert.impl.MailAlertTool;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class AlertServiceConfig {
	
	@Value("${inspector.alertmail.gmail.host}")
	private String gmailHost;

	@Value("${inspector.alertmail.gmail.port}")
	private int gmailPort;
	
	@Value("${inspector.alertmail.gmail.username}")
	private String username;

	@Value("${inspector.alertmail.gmail.password}")
	private String password;
	
	@Value("${inspector.alertmail.gmail.mailto}")
	private String mailTo;
	
	@Bean
	public MailAlertTool mailAlertTool() throws NoSuchProviderException{
		MailAlertTool mailAlertTool = new MailAlertTool(gmailHost, gmailPort);
		mailAlertTool.setUserInf(username, password);		
		mailAlertTool.setMailTo(mailTo);
		
		return mailAlertTool;
	}

}
