package com.convrt.api.repository;

import com.convrt.api.entity.Metadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetadataRepository extends JpaRepository<Metadata, String> {

}
