package com.convrt.repository;

import com.convrt.entity.Metadata;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MetadataRepository extends JpaRepository<Metadata, String> {

}
