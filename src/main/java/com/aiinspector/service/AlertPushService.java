package com.aiinspector.service;

import javax.mail.MessagingException;

public interface AlertPushService {
	
	public void sendAlertMessage(String title, String content) throws MessagingException;

}
