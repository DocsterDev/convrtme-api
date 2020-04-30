package com.convrt.api.repository;

import com.convrt.api.entity.UserVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserVideoRepository extends JpaRepository<UserVideo, String> {
    UserVideo findFirstByUserUuidOrderByVideosOrderDesc(String userUuid);
    UserVideo findFirstByUserUuidAndVideoIdOrderByVideosOrderDesc(String userUuid, String videoId);
    List<UserVideo> findDistinctByUserUuid(String userUuid);
}
