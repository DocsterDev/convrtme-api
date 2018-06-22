package com.convrt.controller;

import com.convrt.entity.Playlist;
import com.convrt.entity.Video;
import com.convrt.service.PlaylistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    @PostMapping
    public Playlist createPlaylist (@RequestBody Playlist playlist) {
        playlist.setUuid(UUID.randomUUID().toString());
        return playlistService.createPlaylist(playlist);
    }

//    @GetMapping
//    public Map<String, List<Video>> getAllPlaylists() {
//        return playlistService.getAllPlaylists();
//    }

    @GetMapping("/{uuid}")
    public Playlist getPlaylist(@PathVariable("uuid") String uuid) {
        return playlistService.getPlaylist(uuid);
    }

//    @PutMapping("/{uuid}")
//    public Playlist updatePlaylist(@PathVariable("uuid") String uuid, @RequestBody List<String> videoList) {
//        return playlistService.updatePlaylist(uuid, videoList);
//    }
//
//    @DeleteMapping("/{uuid}")
//    public void deletePlaylist(@PathVariable("uuid") String uuid) {
//        return playlistService.deletePlaylist(uuid);
//    }

}
