package com.convrt.service;

import com.convrt.model.ConvertStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ConvertStatusWebsocketService {

	@Autowired
	private SimpMessagingTemplate template;

	public void sendStatusUpdate(String uuid, BigDecimal progress, String status) {
		template.convertAndSend("/topic/user", new ConvertStatus(uuid, progress, status));
	}

}
