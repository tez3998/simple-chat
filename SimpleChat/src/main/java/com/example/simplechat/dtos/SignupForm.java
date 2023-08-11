package com.example.simplechat.dtos;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.example.simplechat.entities.User;

import lombok.Data;

@Data
public class SignupForm {
	private String name;
	private String mailAddress;
	private String password;
	private MultipartFile icon;
	
	public User toUser() throws IOException {
		return new User(this.name, this.mailAddress, this.password, this.icon.getBytes());
	}
}
