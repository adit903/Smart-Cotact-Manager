package com.smartcontact.service;

import java.util.Properties;

import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	
	//this method is responsible for sending mail
		public boolean sendEmail(String subject,String message,String to) {
			
			boolean b=false;
			
			//from
			String from="aadibajpai75@gmail.com";
			
			//host variable for gmail
			String host="smtp.gmail.com";
			
			
			//get the system properties
			Properties properties = System.getProperties();
			System.out.println("SYSTEM PROPERTIES"+properties);
			
			
			//setting important information for properties object
		    properties.put("mail.smtp.host", host);
		    properties.put("mail.smtp.port", "465");
		    properties.put("mail.smtp.ssl.enable", "true");
		    properties.put("mail.smtp.auth", "true");
		
			//step 1. get the session object
		    jakarta.mail.Session session = jakarta.mail.Session.getInstance(properties, new Authenticator() {

				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					// TODO Auto-generated method stub
					return new PasswordAuthentication("aadibajpai75@gmail.com","sqdbknoqfofafoiu");
				}
			});
		    
		    session.setDebug(true);
		    
		    //step 2. compose the message
		    MimeMessage m=new MimeMessage(session);
		    
		    try {
				
		    	
		    	//from email
		    	m.setFrom(from);
		    	
		    	//sending to 
		    	m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		    	
		    	//subjects
		    	m.setSubject(subject);
		    	
		    	//message body
		    	m.setText(message);
		    	
		    	//step 3. send email using transport class
		    	
		    	Transport.send(m);
		    	System.out.println("send successfully.........");
		    	
		    	
		    	b=true;
		    	
		    	return b;
		    	
		    	
			} catch (Exception e) {
				// TODO: handle exception
			}
		    
		    return b;
			
		}

}
