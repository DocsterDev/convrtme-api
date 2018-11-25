package com.convrt.api.repository;

import com.convrt.api.entity.User;
import com.convrt.api.entity.UserVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserVideoRepository extends JpaRepository<UserVideo, String> {
    UserVideo findFirstByUserOrderByVideosOrderDesc(User user);
    List<String> findUserVideosByUserUuid(String userUuid);
    List<UserVideo> findDistinctByUserUuid(String userUuid);
}
