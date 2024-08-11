package com.smartcontact.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartcontact.dao.ContactRepository;
import com.smartcontact.dao.UserRepository;
import com.smartcontact.entities.Contact;
import com.smartcontact.entities.User;

import jakarta.websocket.Session;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) {
		
		String userName = principal.getName();
		System.out.println("username :"+userName);
		
		User user = userRepository.getUserByUserName(userName);
		System.out.println("user :" +user);
		
		model.addAttribute("user",user);
		
	}
	
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal)
	{
		model.addAttribute("title","user dashboard");
		return "normal/user_dashboard";
	}
	
	//open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		
		model.addAttribute("title","add contact");
		model.addAttribute("contact",new Contact());
		return "normal/add-contact-form";
	}
	
	//processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Principal principal) {
		
		try {
		String name=principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		
		if(file.isEmpty()) {
			System.out.println("Not found image");
			contact.setImage("contact.jpeg");
		}else {
			
			contact.setImage(file.getOriginalFilename());
			
			File saveFile = new ClassPathResource("static/image").getFile();
			
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
			System.out.println("image has uploaded");
		}
		
		contact.setUser(user);
		
		user.getContacts().add(contact);
		
		this.userRepository.save(user);
		
		System.out.println("data"+contact);
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("ERROR"+e.getMessage());
			e.printStackTrace();
		}
		
		return "normal/add-contact-form";
	}
	
	//show contact handler
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,Model m,Principal principal) {
		m.addAttribute("title", "show contact page");
		//send list of contacts
		
		String userName = principal.getName();
		
		User user = this.userRepository.getUserByUserName(userName);
		
		Pageable pageable = PageRequest.of(page, 3);
		
		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(),pageable);
		
		m.addAttribute("contacts",contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());
		
		return "normal/show_contacts";
	}
	
	//showing particular contact page
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId,Model model,Principal principal) {
		
		System.out.println("CID"+cId);
		
		Optional<Contact> optional = this.contactRepository.findById(cId);
		Contact contact = optional.get();
		
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		if(user.getId()==contact.getUser().getId())
		{
		 model.addAttribute("contact", contact);
		}
		return "normal/contact_detail";
	}
	
	//delete contact
	
	@RequestMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, Model model,Principal principal) {
		
		Optional<Contact> optionalContact = this.contactRepository.findById(cId);
		
		Contact contact = optionalContact.get();
		
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		 
		user.getContacts().remove(contact);
		
		this.userRepository.save(user);
		
		System.out.println("contact"+contact.getcId());
		
		System.out.println("contact"+contact.getUser().getId());
		
//		if(user.getId()==contact.getUser().getId())	
//		{
//		contact.setUser(null);
//		
//		this.contactRepository.delete(contact);
//		}
		return "redirect:/user/show-contacts/0";
		
		
	}
	
	//open updating contact form
	
	@PostMapping("/update-contact/{cid}")
	public String updateContact(@PathVariable("cid") Integer cid,Model model) {
		
		model.addAttribute("title", "update form");
	 	
	  Contact contact = this.contactRepository.findById(cid).get();	
	  
	  model.addAttribute("contact", contact);
	
		
		return "normal/update_form";
	}
	
	//submit update contact form and save
	
	@PostMapping("/update-contact")
	public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Model model,Principal principal) {
		
		try {
			
			//old contact details
			
			Contact oldContactDetails = this.contactRepository.findById(contact.getcId()).get();
			
			if(!file.isEmpty())
			{
				
				//replace new image with previous one
				
				//delete old picture
				 
				File deleteFile = new ClassPathResource("static/image").getFile();
				File file2=new File(deleteFile,oldContactDetails.getImage());
				file2.delete();
				
				
				
				//update new picture
				
				File saveFile = new ClassPathResource("static/image").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImage(file.getOriginalFilename());
				
				
			}
			else {
				contact.setImage(oldContactDetails.getImage());
			}
			
			User user = this.userRepository.getUserByUserName(principal.getName());
			
			contact.setUser(user);
			
			this.contactRepository.save(contact);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		System.out.println("contact"+contact.getcId());
		System.out.println("contact"+contact.getName());
		return "redirect:/user/"+contact.getcId()+"/contact";
	}
	
	//your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		
		model.addAttribute("title", "profile page");
		
		return "normal/profile";
	}
	
	//setting handler
	@GetMapping("/settings")
	public String openSettings() {
		
		return "normal/settings";
	}
	//change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,Principal principal) {
		
		System.out.println("OLD_PASSWORD"+oldPassword);
		System.out.println("NEW_PASSWORD"+newPassword);
		
		String userName = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(userName);
		System.out.println(currentUser.getPassword());
		
		if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword()))
		{
			//save new password
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			System.out.println("password changed successfully");
		}
		
		return "redirect:/user/index";
	}
	
	
}
