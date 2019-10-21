package com.aiinspector.alert.impl;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.aiinspector.alert.AlertTool;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MailAlertTool extends AlertTool{
	
	private Properties props = new Properties();
	private MimeMessage message;
	private MimeBodyPart textPart = null;
	private MimeBodyPart picturePart = null;
	private MimeBodyPart filePart = null;
	private String mailTo = "";
	
	public MailAlertTool(String host, int protocol) throws NoSuchProviderException{
		this.setProperties(host, protocol);

	}
	
	public void setProperties(String host, int protocol) throws NoSuchProviderException{
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", protocol);
	}
	
	public void setUserInf(String name, String password) throws NoSuchProviderException{
        this.createMessage(name,password);
	}
	
	private void createMessage(String name, String password) throws NoSuchProviderException{
		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(name, password);
			}
		});
		this.message = new MimeMessage(session);
	}
	
	public void setSubject(String subject) throws MessagingException{
		this.message.setSubject(subject);
	}
	
	public void setText(String text) throws MessagingException{
        this.textPart = new MimeBodyPart();
        this.textPart.setContent(text, "text/html; charset=UTF-8");
	}
	
	public void setPicture(String path, String cid) throws MessagingException{
        this.picturePart = new MimeBodyPart();
        this.picturePart.setDataHandler(new DataHandler(new FileDataSource(path)));
        this.picturePart.setFileName(path);
        this.picturePart.setHeader("Content-ID", cid);
	}
	
	public void setAttFile(String path) throws MessagingException{
		if(path != null && path.length() > 0 && new File(path).exists()){
			this.filePart = new MimeBodyPart();
		    this.filePart.setDataHandler(new DataHandler(new FileDataSource(path)));
			this.filePart.setFileName(path);
		}
	}
	
	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}
	
	public void send(String mailTo) throws MessagingException{
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(this.textPart);
		if(this.picturePart != null) multipart.addBodyPart(this.picturePart);
		if(this.filePart != null) multipart.addBodyPart(this.filePart);

		this.message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(this.mailTo));
        this.message.setContent(multipart);
        log.info("Mail to:{}...",mailTo);
        Transport.send(message, message.getRecipients(Message.RecipientType.TO));        
       
        log.info("OK");
	}

	@Override
	public void sendMessage(String title, String content) throws MessagingException{
		Multipart multipart = new MimeMultipart();
		setSubject(title);
		setText(content);
		multipart.addBodyPart(this.textPart);
		if(this.picturePart != null) multipart.addBodyPart(this.picturePart);
		if(this.filePart != null) multipart.addBodyPart(this.filePart);

		this.message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(this.mailTo));
        this.message.setContent(multipart);
        log.info("Mail to:{}...",mailTo);
        Transport.send(message, message.getRecipients(Message.RecipientType.TO));
        log.info("OK");
	}		

}
