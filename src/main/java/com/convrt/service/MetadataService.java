package com.convrt.service;

import com.convrt.entity.Metadata;
import com.convrt.repository.MetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MetadataService {

	@Autowired
	private MetadataRepository metadataRepo;

	public Metadata addMetadata(Metadata fileMetadata) {
		if (fileMetadata.getUuid() == null) {
			fileMetadata.setUuid(UUID.randomUUID().toString());
		}
		if (metadataRepo.exists(fileMetadata.getUuid())) {
			throw new RuntimeException(
					"Cannot insert file metadata, resource already exists: uuid=" + fileMetadata.getUuid());
		}
		return metadataRepo.save(fileMetadata);
	}

	public List<Metadata> readAllMetadata(String user) {
		List<Metadata> fileMetadata = metadataRepo.findAll();
		if (fileMetadata == null) {
			throw new RuntimeException("File Metadata Resource not found");
		}
		return fileMetadata;
	}

	public Metadata readMetadata(String uuid) {
		Metadata fileMetadata = metadataRepo.findOne(uuid);
		if (fileMetadata == null) {
			throw new RuntimeException("File Metadata resource not found");
		}
		return fileMetadata;
	}

	public Metadata updateMetadata(String user, String uuid, Metadata fileMetadata) {
		fileMetadata.setUuid(uuid);
		if (!metadataRepo.exists(uuid)) {
			throw new RuntimeException("Cannot update file metadata, resource does not exist: uuid=" + uuid);
		}
		return metadataRepo.save(fileMetadata);
	}

	public void deleteMetadata(String user, String uuid) {
		metadataRepo.delete(uuid);
	}

}
