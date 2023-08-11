package com.example.simplechat.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.example.simplechat.beans.SessionControl;
import com.example.simplechat.dtos.LoginForm;
import com.example.simplechat.dtos.SignupForm;
import com.example.simplechat.entities.User;
import com.example.simplechat.repositories.UserRepository;


@Controller
public class UserController {
	@Autowired
	UserRepository userRepository;
	@Autowired
	SessionControl sessionControl;
	private Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@GetMapping("/")
	public String index() {
		logger.info(LocalDateTime.now().toString());
		return "index";
	}
	
	@GetMapping("/signup")
	public ModelAndView showSignUpPage(@ModelAttribute("signupForm") SignupForm signupForm, ModelAndView mav) {
		mav.setViewName("signup");
		mav.addObject("signupForm", signupForm);
		
		return mav;
	}
	
	@PostMapping("/signup")
	@Transactional
	public ModelAndView signup(@ModelAttribute("signupForm") SignupForm signupForm, ModelAndView mav) throws IOException {		
		User existingUser = this.userRepository.findByEmail(signupForm.getMailAddress());
		if (existingUser != null) {
			mav.setViewName("signup");
			return mav;
		}
		
		User signupUser =  signupForm.toUser();
		this.userRepository.saveAndFlush(signupUser);
		this.logger.info("sign up(" + signupUser + ")");
		
		mav.setViewName("redirect:/users");
		this.sessionControl.login(signupUser.toLoginForm());
		
		return mav;
	}
	
	@GetMapping("/login")
	public ModelAndView showLoginPage(@ModelAttribute("loginForm") LoginForm loginForm, ModelAndView mav) {
		mav.setViewName("login");
		return mav;
	}
	
	@PostMapping("/login")
	public ModelAndView login(@ModelAttribute("loginForm") LoginForm loginForm, ModelAndView mav) {
		if (this.sessionControl.login(loginForm)) {
			mav.setViewName("redirect:/users");
		} else {
			mav.setViewName("/login");
		}
		
		return mav;
	}
	
	@GetMapping("/users")
	public ModelAndView listUsers(ModelAndView mav) {
		mav.setViewName("users");
		User loginUser = this.sessionControl.getUser();
		mav.addObject("user", loginUser);
		List<User> peers = this.userRepository.findAllExcept(loginUser.getId());
		mav.addObject("peers", peers);
		
		return mav;
	}
	
	@GetMapping(value = "/image/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody byte[] getIcon(@PathVariable("id") Integer id) {
		byte[] icon = this.userRepository.findIconById(id);
		return icon;
	}
}
