package com.convrt.service;

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
        return playlistRepository.save(playlist);
    }

    @Transactional(readOnly = true)
    public Playlist readPlaylist(User user, String uuid) {
        Playlist playlist = playlistRepository.findOne(uuid);
        if (playlist == null) {
            throw new RuntimeException("No playlist found");
        }
        return playlist;
    }

    @Transactional
    public Playlist updatePlaylist(User user, Playlist playlist) {
        Playlist playlistPersistent = playlistRepository.findOne(user.getUuid());
        if (playlistPersistent == null) {
            throw new RuntimeException("No playlist found to update");
        }
        playlistPersistent.setName(playlist.getName());
        playlistPersistent.setIconColor(playlist.getIconColor());
        playlistPersistent.setVideos(playlist.getVideos());
        return playlistPersistent;
    }

    @Transactional
    public void deletePlaylist(String uuid) {
        if (!playlistRepository.exists(uuid)) {
            throw new RuntimeException("No playlist found to delete");
        }
        playlistRepository.delete(uuid);
    }

}
