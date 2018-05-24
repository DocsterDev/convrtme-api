package com.convrt.websocket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

//@EnableScheduling
@Configuration
public class SchedulerConfig {

	@Autowired
	SimpMessagingTemplate template;

//	@Scheduled(fixedDelay = 3000)
//	public void sendAdhocMessage() {
//		template.convertAndSend("/topic/user", "");
//	}

}
