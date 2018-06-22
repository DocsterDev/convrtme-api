package com.convrt.repository;

import com.convrt.entity.Playlist;
import com.convrt.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, String> {

	Playlist findByUserUuid(String userUuid);

}
