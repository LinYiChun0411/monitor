package com.aiinspector.alert;

import javax.mail.MessagingException;

public abstract class AlertTool {
	
	public abstract void sendMessage(String title ,String content) throws MessagingException;

}
