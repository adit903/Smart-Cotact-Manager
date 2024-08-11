package com.smartcontact.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.objenesis.instantiator.basic.NewInstanceInstantiator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smartcontact.dao.UserRepository;
import com.smartcontact.entities.User;
import com.smartcontact.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/")
	public String home(Model model) {
		
		model.addAttribute("title","Home - Smart contact manager");
		
		return "home";
	}
	
	@RequestMapping("/about")
	public String info(Model model) {
		
		model.addAttribute("title","Home - Smart contact manager");
		
		return "about";
	}
	
	@RequestMapping("/signup")
	public String about(Model model) {
		
		model.addAttribute("signup","about - Smart contact manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	
	//handler for registering user
	@RequestMapping(value="/do_register",method = RequestMethod.POST)
	public String register(@Valid @ModelAttribute("user") User user,BindingResult result1,@RequestParam(value="agreement",defaultValue = "false") boolean agreement,Model model,HttpSession session)
	{
		
   
		try {

			if(!agreement)
			{
				System.out.println("You have not agreed to the Terms & Conditions.");
				throw new Exception("You have not agreed to the Terms & Conditions.");
			}
			
			if(result1.hasErrors())
			{
				System.out.println(result1.toString());
				model.addAttribute("user",user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			
			System.out.println("agreement"+agreement);
			System.out.println("user"+user);
			
			User result = this.userRepository.save(user);
			
			model.addAttribute("user",new User());
			
			session.setAttribute("message",new Message("successfully registered !!","alert-success"));
			return "signup";
			
		} catch (Exception e) {
			// TODO: handle exception
			
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message",new Message("something went wrong !!"+e.getMessage(),"alert-danger"));
			return "signup";
			
		}
	
	}
	
	//login controller
	@RequestMapping("/signin")
	public String customLogin(Model model)
	{
		
		model.addAttribute("title", "Login Page");
		return "login";
	}
	
	
}
