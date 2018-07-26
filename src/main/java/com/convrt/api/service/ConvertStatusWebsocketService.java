package com.convrt.api.service;

import com.convrt.api.view.ConvertStatus;
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
