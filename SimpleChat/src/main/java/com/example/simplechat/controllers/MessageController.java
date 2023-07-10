package com.example.simplechat.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.example.simplechat.beans.SessionControl;
import com.example.simplechat.entities.Message;
import com.example.simplechat.entities.MessageJson;
import com.example.simplechat.entities.MessageRequestJson;
import com.example.simplechat.entities.User;
import com.example.simplechat.repositories.MessageRepository;
import com.example.simplechat.repositories.UserRepository;

@Controller
public class MessageController {
	@Autowired
	SessionControl sessionControl;
	@Autowired
	MessageRepository messageRepository;
	@Autowired
	UserRepository userRepository;

	private Logger logger = LoggerFactory.getLogger(MessageController.class);
	
	@GetMapping("/chat/{peerId}")
	public ModelAndView showChatForm(@PathVariable("peerId") Integer peerId, ModelAndView mav) {
		mav.setViewName("chat");
		
		User loginUser = sessionControl.getUser();
		mav.addObject("me", loginUser);
		Optional<User> peerOptional = this.userRepository.findById(peerId);
		if (peerOptional.isEmpty()) {
			mav.setViewName("redirect:/users");
			return mav;
		}
		User peer = peerOptional.get();
		mav.addObject("peer", peer);
		List<Message> messages = this.messageRepository.findByUserIdsOrderByTimestamp(loginUser.getId(), peerId);
		mav.addObject("messages", messages);
		
		return mav;
	}
	
	@PostMapping("/send")
	@ResponseBody
	@Transactional
	public MessageJson send(@RequestBody MessageJson messageJson) {
		User sender = this.sessionControl.getUser();
		Optional<User> receiverOptional = this.userRepository.findById(messageJson.getReceiverId());
		if (receiverOptional.isEmpty()) {
			return messageJson;
		}
		
		User receiver = receiverOptional.get();
		String messageContent = messageJson.getMessage();
		Message sentMessage = new Message(sender, receiver, messageContent);
		this.messageRepository.saveAndFlush(sentMessage);
		logger.info(String.format("send(sender:%s, reciver:%s, message:%s)", sender, receiver, messageContent));
		return messageJson;
	}
	
	@PostMapping("/receive")
	@ResponseBody
	public List<MessageJson> receive(@RequestBody MessageRequestJson messageRequestJson) {
		//logger.info(messageRequestJson.getLastMessageDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")).toString());
		//String timestampStr = messageRequestJson.getLastMessageDateTime().toString().replace("T", " ");
		logger.info(messageRequestJson.getLastMessageDateTime().toString());
		//DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssssss");
		//logger.info(messageRequestJson.getLastMessageDateTime().format(dateTimeFormatter).toString());
		
		//List<Message> messages = this.messageRepository.findFromTimestamp(messageRequestJson.getMyId(), messageRequestJson.getPeerId(), messageRequestJson.getLastMessageDateTime());
		List<Message> messages = this.messageRepository.findFromTimestamp(messageRequestJson.getLastMessageDateTime());
		logger.info(Integer.valueOf(messages.size()).toString());
		List<MessageJson> messageJsons = new ArrayList<>();
		for (Message m : messages) {
			messageJsons.add(m.toMessageJson());
		}
		return messageJsons;
	}
}
