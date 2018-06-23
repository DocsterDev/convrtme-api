package com.convrt.service;

import com.convrt.entity.Playlist;
import com.convrt.entity.Video;
import com.convrt.repository.PlaylistRepository;
import com.convrt.repository.VideoRepository;
import com.convrt.view.VideoIdSet;
import lombok.extern.slf4j.Slf4j;
import org.omg.SendingContext.RunTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class PlaylistService {


    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private VideoRepository videoRepository;

    @Transactional
    public Playlist createPlaylist(Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    @Transactional(readOnly = true)
    public Playlist getPlaylist(String uuid){
        Playlist playlist = playlistRepository.findOne(uuid);
        if (playlist == null){
            throw new RuntimeException("Playlist not found for uuid=" + uuid);
        }
        // TODO: Get stream from the database to test that functionality / was getting "stream has already been operated upon or closed"
        Iterator<Video> videos = videoRepository.findVideosByVideoIdIn(playlist.getVideoIdList()).iterator();
        List<VideoIdSet> videoIds = playlist.getVideos();
        videos.forEachRemaining((v) -> {
            videoIds.stream().forEach((id) -> {
                if (id.getVideoId().equals(v.getVideoId()))
                    id.setVideo(v);
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
