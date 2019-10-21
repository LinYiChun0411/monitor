package com.aiinspector.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.aiinspector.alert.impl.MailAlertTool;

@Service
public class MailAlertPushServiceImp extends AlertPushServiceImp<MailAlertTool>{	
	@Resource
	private MailAlertTool mailAlertTool;
}
