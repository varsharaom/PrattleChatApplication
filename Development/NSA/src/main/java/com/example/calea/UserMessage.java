package com.example.calea;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserMessage {

	private final MessageRepository repository;
	
	public UserMessage() {
		this.repository = new MessageRepository();
	}
	
	@PostMapping("/messages/sent/{id}")
	public List<Message> messagesSentByUser(@RequestBody RequestMessage requestBody, @PathVariable long id) {
		return repository.getMessagesSentByUser(requestBody, id);
	}
	
	@PostMapping("/messages/received/{id}")
	public List<Message> messagesReceivedByUser(@RequestBody RequestMessage requestBody,  @PathVariable long id) {
		return repository.getMessagesReceivedByUser(requestBody, id);
	}
	
}
