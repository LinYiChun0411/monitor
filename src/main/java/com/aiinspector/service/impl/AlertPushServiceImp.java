package com.aiinspector.service.impl;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;

import com.aiinspector.alert.AlertTool;
import com.aiinspector.service.AlertPushService;

public class AlertPushServiceImp<A extends AlertTool> implements AlertPushService{

	@Autowired
	private A alertTool;
	
	public A getAlertTool() {
		return alertTool;
	}
	
	@Override
	public void sendAlertMessage(String title, String content) throws MessagingException {
		alertTool.sendMessage(title, content);
	}

}
