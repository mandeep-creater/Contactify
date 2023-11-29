package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.service.EmailService;

@Controller
public class ForgotController {
	

	 Random random = new Random(1000);
	 
	 @Autowired
	  private EmailService emailService;
	 
	 @Autowired
	 private UserRepository userRepository;
	 
	 @Autowired
	 private BCryptPasswordEncoder bcrypt;
	  
	 
	//Email id form open Controller
	@RequestMapping("/forgot")
	public String openEmailForm()
	{
		
		return"forgot_email_form";
	}
	
	// OTP handler
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email , HttpSession session)
	{
		System.out.println("Email : "+email);
		
		//generating OTP of 4 numbers
		
		 
		int otp= random.nextInt(999999);
		
		 System.out.println("OTP :"+otp);
		 
		 //Code for send the OTP to the email.....
		 
		 String  subject = "OTP From SCM";
		 String message=""+"<div style ='border:1px solid #e2e2e2; padding:20px'>"
				 		  +"<h1>"
				 		  +"OTP is "
				 		  +otp
				 		 +"</n> "
				 		 +"</h1> "
				 		  +"</div>";
		 String to=email;
		 
		boolean flag = this.emailService.sendEmail(subject, message, to);
		
		if(flag) {
			
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return"verify_otp";
			
		}else {
				
			session.setAttribute("message","Check your mail id !!");
			return"forgot_email_form";
		}
			
			
		
	}
	
	// verify OTp
	@PostMapping("/verify-otp")
	public String verifyotp(@RequestParam("otp") int  otp, HttpSession session)
	{
		int   myotp = (int)session.getAttribute("myotp");
		String email = (String) session.getAttribute("email");
		
		if(myotp == otp)
		{
			
			// password change
		User user =	this.userRepository.getUserByUserName(email);
		
		if(user==null)
		{
			// send error message
			
			session.setAttribute("message","User does not exits with the above given mail Id !!");
			return "forgot_email_form";
		}else {
			
			// send change password form
			
		}
			
			
			
			 return "password_change_form";
		}else {
			
			session.setAttribute("message", "You have entered wrong OTP !!");
			return "verify_otp";
		}
	}
	
	
	// Change Password
	
	@PostMapping("/change-password")
	public  String changePassword(@RequestParam("newpassword") String newpassword,HttpSession session)
	{
		String email = (String) session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		user.setPassword(this.bcrypt.encode(newpassword));
		this.userRepository.save(user);
		
		//session.setAttribute("message", "You have entered wrong OTP !!");
		return "redirect:/signin?change=password changed successfully....";
		
	}
}
