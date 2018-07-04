package com.convrt.repository;

import com.convrt.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.stream.Stream;

public interface VideoRepository extends JpaRepository<Video, String> {

	Video findByIdAndStreamUrlExpireDateNotNull(String id);

	List<Video> findVideosByIdIn(List<String> videosIds);

}
