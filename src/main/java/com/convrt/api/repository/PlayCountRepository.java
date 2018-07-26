package com.convrt.api.repository;

import com.convrt.api.entity.PlayCount;
import com.convrt.api.entity.User;
import com.convrt.api.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayCountRepository extends JpaRepository<PlayCount, String> {

	PlayCount findByVideoId(String videoId);

	PlayCount findByVideoAndUser(Video video, User user);

}
