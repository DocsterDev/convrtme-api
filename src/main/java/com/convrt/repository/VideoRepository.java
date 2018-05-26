package com.convrt.repository;

import com.convrt.entity.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideoRepository extends MongoRepository<Video, String> {

	Video findByVideoId(String videoId);

}
