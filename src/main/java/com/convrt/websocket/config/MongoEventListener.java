package com.convrt.websocket.config;

import com.convrt.data.entity.Metadata;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;

@Component
public class MongoEventListener extends AbstractMongoEventListener<Metadata> {

	@Override
	public void onBeforeSave(BeforeSaveEvent<Metadata> event) {
		// � change values, delete them, whatever �
	}

	@Override
	public void onAfterSave(AfterSaveEvent<Metadata> event) {
		System.out.println("BRO THIS IS AFTER THE SAVE: " + event.getSource().getTitle());
	}

}
