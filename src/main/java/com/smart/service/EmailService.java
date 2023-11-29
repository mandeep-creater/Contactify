package com.smart.service;


import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

	 public boolean  sendEmail( String subject , String  message , String to)
	    {

	        boolean f= false;
	         String from="mandeep160902@gmail.com";
	      
	                // variable for gmail
	            
	            String host = "smtp.gmail.com";
	            
	            //  get the Sysetm Properties
	            
	         Properties properties =	System.getProperties();
	         
	         System.out.println("PROPERTIES "+properties);
	         
	         // setting important information to properties
	         
	         //host set
	         
	         properties.put("mail.smtp.host" , host);
	         properties.put("mail.smtp.port" , "465");
	         properties.put("mail.smtp.ssl.enable" , "true");
	         properties.put("mail.smtp.auth" , "true");
	         
	         
	         
	         // step 1: to get the session Object
	         
	         Session session=Session.getInstance(properties , new Authenticator() {
	    
	            @Override
	            protected PasswordAuthentication getPasswordAuthentication() {
	                // TODO Auto-generated method stub
	                return new  PasswordAuthentication("mandeep160902@gmail.com","uhkinjkesmzxlgew");
	            } 
	             
	         });
	            
	            session.setDebug(true);
	            
	            
	             // Step 2: Compose the message [text, multi media]
	            MimeMessage m = new MimeMessage(session);
	            
	            
	    
	            try {
	                // from email 
	              //  m.setFrom(from);
	                
	                //adding recipient  to message
	                m.addRecipient(Message.RecipientType.TO ,  new InternetAddress(to));
	                
	                //adding  Subject   to message
	                
	                m.setSubject(subject);
	                
	                //adding Text  to message
	                
	              //  m.setText(message);
	                
	                m.setContent(message, "text/html");
	                
	                
	                
	                
	                // send
	                
	                
	                // step 3 : Send the message using  Transport Class
	                
	                Transport.send(m);
	                
	                System.out.println("Sent Success.........................");
	                f =true;
	                
	                
	            } catch (MessagingException e) {
	            
	                
	                e.printStackTrace();
	            }
	            return f;
	    }
}
