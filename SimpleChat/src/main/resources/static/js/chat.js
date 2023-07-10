"use strict";

const my_id = $("#my_id").text();
const peer_id = $("#peer_id").text();
//const baseUrl = $(location).attr("protocol") + "://" + $(location).attr("host");
const baseUrl = "http://" + $(location).attr("host");

function sendMessage() {
	const message = JSON.stringify({
		senderId: my_id,
		receiverId: peer_id,
		message: $("#message-area").val()
	});
	
	$.ajax({
		method: "POST",
		url: baseUrl + "/send",
		data: message,
		contentType: "application/json",
		dataType: "json" // TODO: write success case and error case
	});
	
	console.log("send: " + $("#message-area").val());
	
	$("#message-area").val("");
}

function showMessages(messages) {
	for (let i = 0; i < messages.length; i++) {
		const m = messages[i];
		if (m.senderId == peer_id) {
			$("#message-list").append("<div class=\"message\"><img class=\"peer_icon\" src=\"/image/" + my_id + "\" width=\"45\" height=\"45\" loading=\"lazy\" decoding=\"async\"/><p class=\"content peer_side\">" + m.message + "</p></div><span class=\"sent_datetime\" hidden>" + m.sentDateTime + "</span>");
		} else {
			$("#message-list").append("<div class=\"message\"><p class=\"content my_side\">" + m.message + "</p></div><span class=\"sent_datetime\" hidden>" + m.sentDateTime + "</span>")
		}
	}
	
//	for (const m of messages) {
//		console.log(m);
//		if (m.senderId == peer_id) {
//			$("#message-list").append("<div class=\"message\"><img class=\"peer_icon\" src=\"/image/" + my_id + "\" width=\"45\" height=\"45\" loading=\"lazy\" decoding=\"async\"/><p class=\"content peer_side\">" + m.message + "</p></div><span class=\"sent_datetime\" hidden>" + m.sentDateTime + "</span>");
//		} else {
//			$("#message-list").append("<div class=\"message\"><p class=\"content my_side\">" + m.message + "</p></div><span class=\"sent_datetime\" hidden>" + m.sentDateTime + "</span>")
//		}
//	}
}

function receiveMessage() {
	const messageItem = $(".message");
	
	messageItem.ready(function() {
		const lastMessageDataTime = messageItem.last().children(".sent_datetime").text();
	
		console.log(lastMessageDataTime);
		
		if(!lastMessageDataTime) {
			return;
		}
		
		const messageRequest = JSON.stringify({
			senderId: my_id,
			receiverId: peer_id,
			lastMessageDateTime: lastMessageDataTime
		});
		
		$.ajax({
			method: "POST",
			url: baseUrl + "/receive",
			data: messageRequest,
			contentType: "application/json",
			dataType: "json"
		}).done( function (messages) {
			console.log("request message");
			
			console.log(messages);
			
			if (messages.length == 0) {
				return;
			}
			
			showMessages(messages);
		}); // TODO: write error case
	});
	
//	const lastMessageDataTime = $(".message").last().children(".sent_datetime").text();
//	
//	console.log(lastMessageDataTime);
//	
//	if(!lastMessageDataTime) {
//		return;
//	}
//	
//	const messageRequest = JSON.stringify({
//		senderId: my_id,
//		receiverId: peer_id,
//		lastMessageDateTime: lastMessageDataTime
//	});
//	
//	$.ajax({
//		method: "POST",
//		url: baseUrl + "/receive",
//		data: messageRequest,
//		contentType: "application/json",
//		dataType: "json"
//	}).done( function (messages) {
//		console.log("request message");
//		
//		console.log(messages);
//		
//		if (messages.length == 0) {
//			return;
//		}
//		
//		showMessages(messages);
//	}); // TODO: write error case
}

$(function() {
	$("form").on("submit", function(e){
		e.preventDefault();
	});
	
	$("#submit-button").click(function(){
		sendMessage();
	});
	
	setInterval(receiveMessage, 1000);
});