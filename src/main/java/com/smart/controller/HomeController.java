package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
		@Autowired
		private UserRepository userRepository;
	
	
			// This is Home Handler
	
	@RequestMapping("/")
	public String home(Model m)
	{
		m.addAttribute("tittle","Home-Smart Contact Manager");
		return "home";
	}
	
	
		 // This is About Handler
	
	@RequestMapping("/about")
	public String about(Model m)
	{
		m.addAttribute("tittle","About-Smart Contact Manager");
		return "about";
	}
	

	 	// This is Signup Handler
	
	@RequestMapping("/signup")
	public String signup(Model m)
	{
		m.addAttribute("tittle","Register-Smart Contact Manager");
		m.addAttribute("user", new User());
		return "signup";
	}

	
	// This is for user Register
	
	@PostMapping("/do_register")
	
	public String registerUser(@Valid @ModelAttribute("user") User user ,BindingResult resultvalid, @RequestParam(value="agreement",defaultValue="false")
			boolean agreement, Model model ,HttpSession session )
	{
		
try {
			
			
			
			if(!agreement)
			{
				System.out.println("You have not Agree the terms and conditions ");
				throw new Exception("You have not Agree the terms and conditions ");
			}
			
			if(resultvalid.hasErrors())
			{	
				System.out.println("ERROR"+resultvalid.toString());
				model.addAttribute("user", user);
				
				return"signup";
			}
			
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("Agreement "+agreement);
			System.out.println("USER "+user);
				
			 User result= this.userRepository.save(user);
			
			model.addAttribute("user", new User());
			session.setAttribute("message",new Message("Succesfully Registered !!",	"alert-success"));
			return "signup";

			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message",new Message("Something went wrong!", "alert-danger"));
			return "signup";
		}
		

	}
	
	// Handler for login
	@GetMapping("/signin")
	public String customLogin(Model model)
	{
		model.addAttribute("tittle", "Login Page");
		return"login"; 
	}
}
