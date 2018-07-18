package com.convrt.service;

import com.convrt.entity.Playlist;
import com.convrt.entity.User;
import com.convrt.entity.Video;
import com.convrt.repository.PlaylistRepository;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PreRemove;
import java.time.Instant;
import java.util.List;
import java.util.Set;

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
    public List<Video> readPlaylistVideos(User user, String name) {
        Playlist playlist = playlistRepository.findByUuidAndUser(name, user);
        if (playlist != null) {
            playlist.setLastAccessed(Instant.now());
        }
        return playlist.getVideos();
    }

    @Transactional
    public Playlist updateVideos(String playlistUuid, User user, List<Video> videos) {
        Playlist playlist = playlistRepository.findByUuidAndUser(playlistUuid, user);
        if (playlist == null) {
            throw new RuntimeException("No playlist found to update videos");
        }
        Set<String> duplicates = findDuplicates(videos);
        if (!duplicates.isEmpty()) {
            throw new RuntimeException(String.format("Cannot have the same video added to the same playlist. Found %s", StringUtils.join(duplicates, ",")));
        }
        videoService.createAllVideos(videos);
        playlist.getVideos().clear();
        videos.forEach((e) -> {
            playlist.getVideos().add(e);
        });
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

    @Transactional
    public List<Video> deleteVideo(User user, String uuid, String videoId) {
        Playlist playlistPersistent = playlistRepository.findByUuidAndUser(uuid, user);
        if (playlistPersistent == null) {
            throw new RuntimeException("No playlist found to update");
        }
        Integer removeIndex = null;
        int i = 0;
        for (Video video: playlistPersistent.getVideos()) {
            if (video.getId().equals(videoId)) {
                removeIndex = i;
                log.info("Deleting: " + removeIndex);
                break;
            }
            i++;
        }
        if (removeIndex != null) {
            playlistPersistent.getVideos().remove(removeIndex);
            playlistPersistent.setVideos(Lists.newArrayList(playlistPersistent.getVideos()));
        }
        return playlistPersistent.getVideos();
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

    public Set<String> findDuplicates(List<Video> listContainingDuplicates) {
        final Set<String> setToReturn = Sets.newHashSet();
        final Set<String> set1 = Sets.newHashSet();
        for (Video video : listContainingDuplicates) {
            if (!set1.add(video.getTitle())) {
                setToReturn.add(video.getTitle());
            }
        }
        return setToReturn;
    }

}
