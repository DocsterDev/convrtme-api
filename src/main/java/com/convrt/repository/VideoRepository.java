package com.convrt.repository;

import com.convrt.entity.Video;

public interface VideoRepository extends JpaRepository<Video, String> {

	Video findByVideoId(String videoId);

}
