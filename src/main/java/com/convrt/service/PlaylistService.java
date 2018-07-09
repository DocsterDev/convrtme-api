package com.convrt.service;

import com.convrt.entity.Playlist;
import com.convrt.entity.User;
import com.convrt.entity.Video;
import com.convrt.repository.PlaylistRepository;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private VideoService videoService;

    @Transactional
    public Playlist readPlaylist(User user, String name) {
        Playlist playlist = playlistRepository.findByUuidAndUser(name, user);
        if (playlist != null) {
            playlist.setLastAccessed(Instant.now());
        }
        return playlist;
    }

    @Transactional
    public Playlist updateVideos(String playlistUuid, User user, List<Video> videos) {
        Playlist playlist = playlistRepository.findByUuidAndUser(playlistUuid, user);
        if (playlist == null) {
            throw new RuntimeException("No playlist found to update videos");
        }
        videoService.createAllVideos(videos);
        playlist.getVideos().clear();
        videos.forEach((e) -> playlist.getVideos().add(e));
        return playlistRepository.save(playlist);
    }

    @Transactional
    public Playlist updatePlaylist(User user, Playlist playlist) {
        Playlist playlistPersistent = playlistRepository.findByUuidAndUser(playlist.getUuid(), user);
        if (playlistPersistent == null) {
            throw new RuntimeException("No playlist found to update");
        }
        playlistPersistent.setName(playlist.getName());
        playlistPersistent.setIconColor(playlist.getIconColor());
        return playlistRepository.save(playlistPersistent);
    }

    @Transactional
    public void deletePlaylist(User user, String uuid) {
        if (!playlistRepository.existsByUuidAndUser(uuid, user)) {
            throw new RuntimeException("No playlist found to delete");
        }
        playlistRepository.deleteByUuidAndUser(uuid, user);
    }

    public List<Playlist> generatePlaylists(User user) {
       return ImmutableList.of(
               new Playlist("playlist_0", "F36262", user),
               new Playlist("playlist_1", "F39E62", user),
               new Playlist("playlist_2", "F7EE37", user),
               new Playlist("playlist_3", "8FDB49", user),
               new Playlist("playlist_4", "62BCF3", user),
               new Playlist("playlist_5", "668CDE", user)
       );
    }

}
