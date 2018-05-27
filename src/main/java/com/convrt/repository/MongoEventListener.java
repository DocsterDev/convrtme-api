package com.convrt.repository;

import com.convrt.entity.Metadata;
import org.springframework.stereotype.Component;

@Component
public class MongoEventListener extends AbstractJpaEventListener<Metadata> {

	@Override
	public void onBeforeSave(BeforeSaveEvent<Metadata> event) {
		// � change values, delete them, whatever �
	}

	@Override
	public void onAfterSave(AfterSaveEvent<Metadata> event) {
		System.out.println("BRO THIS IS AFTER THE SAVE: " + event.getSource().getTitle());
	}

}
