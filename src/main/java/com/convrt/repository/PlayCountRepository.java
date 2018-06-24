package com.convrt.repository;

import com.convrt.entity.PlayCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayCountRepository extends JpaRepository<PlayCount, String> {

	PlayCount findByUserUuidAndVideoId(String userUuid, String videoId);

}
