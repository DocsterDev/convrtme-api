package com.convrt.repository;

import com.convrt.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Video, String> {

	Video findByUserId(String userId);

}
