package com.convrt.service;

import com.convrt.entity.Context;
import com.convrt.entity.Playlist;
import com.convrt.entity.User;
import com.convrt.repository.PlaylistRepository;
import com.convrt.repository.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private VideoRepository videoRepository;

    @Transactional
    public Playlist createPlaylist(User user, Playlist playlist) {
        playlist.setUser(user);
        return playlistRepository.save(playlist);
    }

    @Transactional(readOnly = true)
    public Playlist readPlaylist(User user, String uuid) {
        Playlist playlist = playlistRepository.findByUuidAndUser(uuid, user);
        if (playlist == null) {
            throw new RuntimeException("No playlist found with uuid " + uuid);
        }
        return playlist;
    }

    @Transactional
    public Playlist updatePlaylist(User user, Playlist playlist) {
        Playlist playlistPersistent = playlistRepository.findByUuidAndUser(playlist.getUuid(), user);
        if (playlistPersistent == null) {
            throw new RuntimeException("No playlist found to update");
        }
        playlistPersistent.setName(playlist.getName());
        playlistPersistent.setIconColor(playlist.getIconColor());
        // playlistPersistent.setVideos(playlist.getVideos());
        return playlistPersistent;
    }

    @Transactional
    public void deletePlaylist(User user, String uuid) {
        if (!playlistRepository.existsByUuidAndUser(uuid, user)) {
            throw new RuntimeException("No playlist found to delete");
        }
        playlistRepository.deleteByUuidAndUser(uuid, user);
    }

}
