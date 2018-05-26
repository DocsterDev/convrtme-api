package com.convrt.controller;

import com.convrt.entity.Metadata;
import com.convrt.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/")
public class MetadataController {

	@Autowired
	MetadataService metadataService;

	@PostMapping("{user}/metadata")
	public Metadata addMetadata(@PathVariable("user") String user, @RequestBody Metadata fileMetadata) {
		return metadataService.addMetadata(fileMetadata);
	}

	@GetMapping("{user}/metadata")
	public List<Metadata> readMetadata(@PathVariable("user") String user) {
		try {
			Thread.sleep(1750);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return metadataService.readAllMetadata(user);
	}
	
	@PutMapping("{user}/metadata/{uuid}")
	public Metadata updateMetadata(@PathVariable("user") String user, @PathVariable("uuid") String uuid,
			@RequestBody Metadata fileMetadata) {
		return metadataService.updateMetadata(user, uuid, fileMetadata);
	}

	@DeleteMapping("{user}/metadata/{uuid}")
	public void deleteMetadata(@PathVariable("user") String user, @PathVariable("uuid") String uuid) {
		metadataService.deleteMetadata(user, uuid);
	}

}
