package com.convrt.service;

import com.convrt.entity.Playlist;
import com.convrt.entity.User;
import com.convrt.repository.PlaylistRepository;
import com.google.common.collect.Lists;
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
    public Playlist createPlaylist(User user, Playlist playlist) {
        playlist.setUser(user);
        playlist.setVideos(Lists.newArrayList());
        return playlistRepository.save(playlist);
    }

    @Transactional
    public Playlist readPlaylist(User user, String name) {
        Playlist playlist = playlistRepository.findByUuidAndUser(name, user);
        if (playlist != null) {
            playlist.setLastAccessed(Instant.now());
            playlist.setActive(true);
        }
        return playlist;
    }

    @Transactional
    public void setActive(User user, String name) {
        Playlist playlist = playlistRepository.findByUuidAndUser(name, user);
        if (playlist != null) {
            playlist.setLastAccessed(Instant.now());
            playlist.setActive(true);
        }
    }

    @Transactional(readOnly = true)
    public List<Playlist> readPlaylists(User user) {
        //List<Playlist> playlists = playlistRepository.findByUserOrderByNameAsc(user);
        return playlistRepository.findByUser(user);
    }

    @Transactional
    public Playlist updatePlaylist(User user, Playlist playlist) {
        Playlist playlistPersistent = playlistRepository.findByUuidAndUser(playlist.getUuid(), user);
        if (playlistPersistent == null) {
            throw new RuntimeException("No playlist found to update");
        }
        playlistPersistent.setName(playlist.getName());
        playlistPersistent.setIconColor(playlist.getIconColor());
        videoService.createAllVideos(playlist.getVideos());
        playlistPersistent.setVideos(playlist.getVideos());
        return playlistRepository.save(playlistPersistent);
    }

    @Transactional
    public void deletePlaylist(User user, String uuid) {
        if (!playlistRepository.existsByUuidAndUser(uuid, user)) {
            throw new RuntimeException("No playlist found to delete");
        }
        playlistRepository.deleteByUuidAndUser(uuid, user);
    }

}
