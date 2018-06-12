package com.convrt.repository;

import com.convrt.entity.VideoPlayCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoPlayCountRepository extends JpaRepository<VideoPlayCount, String> {

	VideoPlayCount findByUserUuidAndVideoId(String userUuid, String videoId);

}
