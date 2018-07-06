package com.convrt.repository;

import com.convrt.entity.Playlist;
import com.convrt.entity.User;
import com.convrt.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, String> {

	Playlist findByUuidAndUser(String uuid, User user);

	List<Playlist> findByUserOrderByNameAsc(User user);

	Playlist findByNameAndUser(String name, User user);

	boolean existsByUuidAndUser(String uuid, User user);

	void deleteByUuidAndUser(String uuid, User user);

}
