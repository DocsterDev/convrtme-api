package com.convrt.repository;

import com.convrt.entity.PlayCount;
import com.convrt.entity.User;
import com.convrt.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayCountRepository extends JpaRepository<PlayCount, String> {

	PlayCount findByVideoId(String videoId);

	PlayCount findByVideoAndUser(Video video, User user);

}
