package com.convrt.service;

import com.convrt.entity.Playlist;
import com.convrt.entity.Video;
import com.convrt.repository.PlaylistRepository;
import com.convrt.repository.VideoRepository;
import com.convrt.view.VideoIdSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class PlaylistService {


    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private VideoRepository videoRepository;

    public Playlist createPlaylist(Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    public Playlist getPlaylist(String uuid){
        Playlist playlist = playlistRepository.findOne(uuid);
        List<String> videoIdList = playlist.getVideoIdList();
        List<Video> videoList = videoRepository.findVideosByVideoIdIn(videoIdList);
        List<VideoIdSet> videoIdSets = playlist.getVideos();
        videoIdSets.stream().forEach((e) -> {
            videoList.stream().forEach((k) -> {
                if (e.getVideoId().equals(k.getVideoId())) {
                    e.setVideo(k);
                }
            });
        });
        return playlist;
    }

}


//    public Playlist createPlaylist (@RequestBody Playlist videoList) {
//        return playlistService.createPlaylist(videoList);
//    }
//
//    @GetMapping
//    public Map<String, List<Video>> getAllPlaylists() {
//        return playlistService.getAllPlaylists();
//    }
//
//    @GetMapping("/{uuid}")
//    public List<Playlist> getPlaylist(@PathVariable("uuid") String uuid) {
//        return playlistService.getPlaylist(uuid);
//    }
//
//    @PutMapping("/{uuid}")
//    public Playlist updatePlaylist(@PathVariable("uuid") String uuid, @RequestBody List<String> videoList) {
//        return playlistService.updatePlaylist(uuid, videoList);
//    }
//
//    @DeleteMapping("/{uuid}")
//    public void deletePlaylist(@PathVariable("uuid") String uuid) {
//        return playlistService.deletePlaylist(uuid);
//    }
