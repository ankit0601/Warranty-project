package com.warranty.warranty.services;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.*;

@Service
public class PushNotificationService {

	public void sendNotification(String deviceToken, String title, String body, int loopId) throws FirebaseMessagingException {
		try {
			//Notification notification = Notification.builder().setTitle(title).setBody(body).build();

			 Message message = Message.builder()
	                    .setToken(deviceToken) 
	                    .putData("title", title)
	                    .putData("body", body)
	                    .putData("loopId", loopId+"")
	                    .build();
			
			//Message message = Message.builder().setToken(deviceToken).setNotification(notification).build();

			String response = FirebaseMessaging.getInstance().send(message);

		} catch (Exception e) {
			
		}
	}
	
	
}

