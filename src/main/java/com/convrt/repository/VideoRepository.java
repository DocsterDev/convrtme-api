package com.convrt.repository;

import com.convrt.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, String> {

	Video findByVideoId(String videoId);

}
