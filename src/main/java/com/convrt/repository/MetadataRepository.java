package com.convrt.repository;

import com.convrt.entity.Metadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetadataRepository extends JpaRepository<Metadata, String> {

}
