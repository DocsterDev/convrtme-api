package com.convrt.controller;

import com.convrt.entity.Video;
import com.convrt.service.PlaylistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    @GetMapping()
    public Map<String, List<Video>> getAllPlaylists() {
        return playlistService.getSearch(query);
    }

    @GetMapping("/{uuid}")
    public Map<String, List<Video>> getPlaylist(@PathVariable("uuid") String uuid) {
        return playlistService.getSearch(query);
    }

    @PostMapping
    public Map<String, > createPlaylist () {

    }

}
