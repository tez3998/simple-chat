package com.example.simplechat.beans;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.example.simplechat.entities.LoginForm;
import com.example.simplechat.entities.User;
import com.example.simplechat.repositories.UserRepository;

@Component
@SessionScope
public class SessionControl implements Serializable {
	@Autowired
	private UserRepository userRepository;
	private User user;
	private Logger logger = LoggerFactory.getLogger(SessionControl.class);
	
	public boolean login(LoginForm loginForm) {
		this.user = this.userRepository.findByEmailAndPassword(loginForm.getMailAddress(), loginForm.getPassword());
		if (user == null) {
			return false;
		} else {
			this.logger.info("log in(" + this.user + ")");
			return true;
		}
	}
	
	public void logout() {
		this.user = null;
	}
	
	public boolean isLogin() {
		if (user == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public User getUser() {
		return this.user;
	}
}
