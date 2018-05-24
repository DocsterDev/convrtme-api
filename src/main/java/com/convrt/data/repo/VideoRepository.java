package com.convrt.data.repo;

import com.convrt.data.entity.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideoRepository extends MongoRepository<Video, String> {

	Video findByVideoId(String videoId);

}
