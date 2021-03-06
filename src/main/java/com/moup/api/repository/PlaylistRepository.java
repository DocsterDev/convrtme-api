package com.moup.api.repository;

import com.moup.api.entity.Playlist;
import com.moup.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, String> {

	Playlist findByUuidAndUser(String uuid, User user);

	boolean existsByUuidAndUser(String uuid, User user);

	void deleteByUuidAndUser(String uuid, User user);

}
