package com.smart.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	@Autowired
	private ContactRepository contactRepository;
	
	//method for adding common data to response
		@ModelAttribute
		public void addCommmonData(Model model, Principal principal)
		{
			String userName=principal.getName();
			System.out.println("USERNAME: "+userName);
	
				//get the user using username(Email)
			User user = userRepository.getUserByUserName(userName);
			System.out.println(" USER : "+user);
	 
	 
				model.addAttribute("user", user);
		}

		//Dashboard Home	
		
		@RequestMapping("/index")
		public String dashboard(Model model )
		{
			model.addAttribute("tittle", "User Dashboard");
		
		return"normal/user_dashboard";
		}
	
		
		//open add form handler
	
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model)
	{
		model.addAttribute("tittle", "Add Contact");
		model.addAttribute("contact",new Contact() );
		return"normal/add_contact_form";
	}

		
	//processing add contact form
	@PostMapping("/process-contact")
	public String  processContact(@ModelAttribute Contact contact , 
			@RequestParam("profileImage") MultipartFile file,
			Principal principal,HttpSession session)
	{
		
	try {
		
		// user ka name leke aya 
				String name=principal.getName();
			

					//get the user using username(Email)
				
				User user = this.userRepository.getUserByUserName(name);
				
				
				// processing and uploading file
				if(file.isEmpty())
				{
						// if file is empty then peint our message
					
					contact.setImage("user.jpg");
					
					
				}else {
					
					// upload the file to the folder AND update the name to contact
					
					contact.setImage(file.getOriginalFilename());// this is the way how we get the original name of the file
					
					File saveFile = new ClassPathResource("static/image").getFile(); // this is the path where we upload the image 
						
					//file ka path nikalna ka tarika
					
			Path path=Paths.get(saveFile.getAbsoluteFile()+File.separator+file.getOriginalFilename());
					
							//yha se data read krna hai,path diya or copy kr diya
					
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
			System.out.println("Image is uploaded");
	
			
				}
				
				
				
				
				//contact ko dena tha user
				
				contact.setUser(user);
				
				// add kr rha hu nye contact ko user ke contact mee
				// or user ko dena bhi contact dena hai 
				// yeh bidirectional mapping thi
				
				user.getContacts().add(contact);
				
				// then save kr rha hu
				this.userRepository.save(user);
				
				System.out.println("DATA : "+contact);
				System.out.println("Added to Database");
				
				
				// message
				session.setAttribute("message", new Message("Your Contact is added !!","success"));
		
	}catch(Exception e) {
		
		System.out.println("ERROR ! "+e.getMessage());
		e.printStackTrace();
		
		
		// Error
		session.setAttribute("message", new Message("Something went Wrong Try again!!","danger"));
	}
	
		return"normal/add_contact_form";
	}
	
	
		
	////Show  contacts handler
	
	
	@GetMapping("/show-contacts")
	private String showContacts(Model m , Principal principal)
	{
		
		m.addAttribute("tittle", "Show User Contacts");
		
	
		
		//get the user using username(Email)
		String userName= principal.getName();
		User user =this.userRepository.getUserByUserName(userName);// from here by using this we can get the user id 
		
		
		
		// contacts ki list 
	List<Contact> contacts= this.contactRepository.findContactsByUser(user.getId());
		
		
		m.addAttribute("contacts", contacts);
		
		return"normal/show_contacts";
	}
		
	
			// showing particular detail
	@RequestMapping("/contact/{cid}")
	public String showContactDetail(@PathVariable("cid") Integer cid , Model model,
			Principal principal)
	{
		System.out.println("Contact Id"+cid);
		
	Optional<Contact> contactOptional =	this.contactRepository.findById(cid);
	Contact contact = contactOptional.get();
	
	//getuser
	 String useraName=principal.getName();	
	 User user =this.userRepository.getUserByUserName(useraName);
	 
	 if(user.getId() == contact.getUser().getId())
	model.addAttribute("contact", contact);
	 model.addAttribute("title", contact.getName());
		
		return"normal/contact_detail";
	}
	
	
				//delete contact Handler
	
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid,  Model model , 
			MultipartFile file ,HttpSession session, Principal principal)
	{
		
		
	//	Optional<Contact> contactOptional =	this.contactRepository.findById(cid);
	//	Contact contact = contactOptional.get();
		
	
	
		
	// image remove
	
	try {
		Contact contact =	this.contactRepository.findById(cid).get();
		
		
			System.out.println("Contact "+contact.getCid());
		
		
		User user= this.userRepository.getUserByUserName(principal.getName());
		
		user.getContacts().remove(contact);
		
		this.userRepository.save(user);
		
	// for delteing image

		File deleteFile = new ClassPathResource("static/image").getFile();
		File file1 = new File(deleteFile,contact.getImage());
		file1.delete();
		
		session.setAttribute("message", new Message("Contact deleted successfully...","success"));
		
		System.out.println("Files Deleted");
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		System.out.println("Files Not Deleted");
	}

	
		return "redirect:/user/show-contacts";
	}
	
	
	// open udate form
	@PostMapping("/update-contact/{cid}")
	public String updateform( @PathVariable("cid") Integer cid, Model m)
	{
		m.addAttribute("tittle","Update Contact");
	Contact contact=	this.contactRepository.findById(cid).get();
	m.addAttribute("contact", contact);
		return"normal/update_form";
	}
	
	
		// update-process contact handler
	@RequestMapping(value="/process-update" , method= RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact , @RequestParam("profileImage") 
									MultipartFile file , Model m , HttpSession session,Principal principal)
	{
		// old Contact detail
		m.addAttribute("tittle","Delete Contact");
		
		Contact oldContactDetail = this.contactRepository.findById(contact.getCid()).get();
		try {
					
			// image
			if(!file.isEmpty())
			{	
				//*****#######***Delete Old Photo***########*****//

				File deleteFile = new ClassPathResource("static/image").getFile();
				File file1 = new File(deleteFile,oldContactDetail.getImage());
				file1.delete();
				
				
				
				
					//*****#######***Update new Photo***########*****//
				
				File saveFile = new ClassPathResource("static/image").getFile(); // this is the path where we upload the image 
				
					//file ka path nikalna ka tarika
				Path path=Paths.get(saveFile.getAbsoluteFile()+File.separator+file.getOriginalFilename());
				
				//yha se data read krna hai,path diya or copy kr diya			
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
			contact.setImage(file.getOriginalFilename());
				
			}else {
				
				contact.setImage(oldContactDetail.getImage());
			}
			
				
		User user =	this.userRepository.getUserByUserName(principal.getName());
		
		contact.setUser(user);
		
		this.contactRepository.save(contact);
		
		session.setAttribute("message", new Message(" Your Contact is Updated !!!","success"));
			
		}catch( Exception e) {
			
			e.printStackTrace();
		}
		
		System.out.println("Contact Name : "+contact.getName());
		return "redirect:/user/contact/"+contact.getCid();
	}
	
	
		//Your profile handler
	
	@GetMapping("/profile")
	public String yourProfile(Model model)
	{
		model.addAttribute("tittle","Profile Page");
		
		return"normal/profile";
	}
		
	
		// open setting handler
	@GetMapping("/settings")
	public String openSettings()
	{
		return"normal/settings";
	}
	
		
	//change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal,HttpSession session )
	{
		System.out.println("OLD-PASSWORD "+oldPassword);
		System.out.println("NEW-PASSWORD "+newPassword);
		
		String userName=principal.getName();
		User currentUser = this.userRepository.getUserByUserName(userName);
		
		String useroldPassword = currentUser.getPassword();
		
		if(this.bCryptPasswordEncoder.matches(oldPassword, useroldPassword))
		{
			// change the password
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			
			session.setAttribute("message", new Message("Your Password is Updated...", "success"));
			
			
			
		}else
		{
			//error
			session.setAttribute("message", new Message("Incorrect old Password Entered...", "danger"));
			return "redirect:/user/settings";
		}
		
		return "redirect:/signin?logout";
	}
	
	
	
}
