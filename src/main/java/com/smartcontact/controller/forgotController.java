package com.smartcontact.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontact.dao.UserRepository;
import com.smartcontact.entities.User;
import com.smartcontact.service.EmailService;

import jakarta.mail.Message;
import jakarta.servlet.http.HttpSession;

@Controller
public class forgotController {

	Random random = new Random(100000);
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	//email form open handler
	@RequestMapping("/forgot")
	public String openEmailForm() {
		return "forgot_email_form";
	}
	
	@PostMapping("/send-otp")
	public String openEmailForm(@RequestParam("email") String email,HttpSession session) {
		
		System.out.println("EMAIL"+email);
		
		//Random random = new Random(1000);
		
		int otp = random.nextInt(999999);
		
		System.out.println("OTP="+otp);
		
		String subject="One Time Password (OTP) for your smart contact manager (SCM) ";
		String message="The One Time Password (OTP) for your smart contact manager (SCM) account is ("+otp+"). Please do not share this One Time Password with anyone.";
		String to=email;
		
		boolean flag= this.emailService.sendEmail(subject, message, to);
		
		if(flag) {
			
			session.setAttribute("myotp",otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}
		else {
			
		return "forgot_email_form";
		}
	}
	
	//verify otp
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp,HttpSession session) {
		
		int myOtp=(int)session.getAttribute("myotp");
		String email=(String)session.getAttribute("email");
		if(myOtp==otp) {
			//session.setAttribute("message", "Change Password");
			
			User user = this.userRepository.getUserByUserName(email);
			if(user==null) {
				//send error message
				session.setAttribute("message", "Email does not not exited !");
				return "forgot_email_form";
			}
			else {
				//send password change form
			}
			
			
			
			return "password_change_form";
		}else {
			//session.setAttribute("message", "please check your email !");
			return "verify_otp";
		}
			
	}
	
	//change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword,HttpSession session) {
		
		String email = (String)session.getAttribute("email");
		User user= this.userRepository.getUserByUserName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
		this.userRepository.save(user);
		
		
		return "redirect:/signin";
	}
	
	
	
	
	
	
}
