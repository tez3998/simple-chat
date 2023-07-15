"use strict";

const myId = $("#my_id").text();
const peerId = $("#peer_id").text();
const baseUrl = "http://" + $(location).attr("host");

function moveToBottom() {
	const bottom = document.documentElement.scrollHeight - document.documentElement.clientHeight;
	window.scroll(0, bottom);
}

function sendMessage() {
	const message = JSON.stringify({
		senderId: myId,
		receiverId: peerId,
		message: $("#message-area").val()
	});
	
	$.ajax({
		method: "POST",
		url: baseUrl + "/send",
		data: message,
		contentType: "application/json",
		dataType: "json" // TODO: write success case and error case
	});
	
	console.log("send: " + message);
	
	$("#message-area").val("");
}

function generateMessageClassTag(innerElement) {
	const openingTag = `<div class="message">`;
	const closingTag = `</div>`;
	return openingTag + innerElement + closingTag;
}

function generatePeerIconClassTag(id) {
	const openingTag = `<img class="peer_icon" src="/image/`;
	const closingTag = `" width="45" height="45" loading="lazy" decoding="async"/>`;
	return openingTag + id + closingTag;
}

function generateMessageContentClassTag(sideName, content) {
	const openingTag = `<p class="content ` + sideName + `">`;
	const closingTag = `</p>`;
	return openingTag + content + closingTag;
}

function generateSentDateTimeTag(sentDateTime) {
	const openingTag = `<span class="sent_datetime" hidden>`;
	const closingTag = `</span>`;
	return openingTag + sentDateTime + closingTag;
}

function showMessages(messages) {
	for (let i = 0; i < messages.length; i++) {
		const m = messages[i];
		const sentDateTimeTag = generateSentDateTimeTag(m.sentDateTime);
		let appendingElement;
		
		if (m.senderId == myId) {
			const messageContentClassTag = generateMessageContentClassTag("my_side", m.message);
			appendingElement = generateMessageClassTag(messageContentClassTag + sentDateTimeTag);	
		} else {
			const peerIconClassTag = generatePeerIconClassTag(peerId);
			const messageContentTag = generateMessageContentClassTag("peer_side", m.message);
			appendingElement = generateMessageClassTag(peerIconClassTag + messageContentTag + sentDateTimeTag);	
		}
		$("#message-list").append(appendingElement);
	}
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
			senderId: myId,
			receiverId: peerId,
			lastMessageDateTime: lastMessageDataTime
		});
		
		$.ajax({
			method: "POST",
			url: baseUrl + "/receive",
			data: messageRequest,
			contentType: "application/json",
			dataType: "json"
		}).done( function (messages) {
			//console.log("request message");
			
			if (messages.length == 0) {
				return;
			}
			
			console.log(messages);
			
			const windowHight = $(window).height();
			const bottomSpaceTop = $('#bottom-space').offset().top;
			const scrollTop = $(window).scrollTop();
			
			showMessages(messages);
			
			if (scrollTop >= bottomSpaceTop - windowHight) {
				moveToBottom();
			}
		}); // TODO: write error case
	});
}

$(function() {
	$("form").on("submit", function(e){
		e.preventDefault();
	});
	
	$("#submit-button").click(function(){
		sendMessage();
	});
	
	setInterval(receiveMessage, 1000);
	
	moveToBottom();
});