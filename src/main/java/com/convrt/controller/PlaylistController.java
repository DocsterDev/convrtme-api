package com.convrt.controller;

import com.convrt.entity.Playlist;
import com.convrt.service.PlaylistService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    @PostMapping
    public Playlist createPlaylist (@RequestBody @NonNull Playlist playlist) {
        if (playlist.getUuid() != null) {
            Playlist playlistPersistent = playlistService.readPlaylist(playlist.getUuid());
            if (playlistPersistent != null) {
                throw new RuntimeException("Cant create a new playlist that already exists");
            }
        }
        playlist.setUuid(UUID.randomUUID().toString());
        return playlistService.createPlaylist(playlist);
    }

//    @GetMapping
//    public Map<String, List<Video>> getAllPlaylists() {
//        return playlistService.getAllPlaylists();
//    }

    @GetMapping("/{uuid}")
    public Playlist getPlaylist(@PathVariable(value = "uuid") String uuid) {
        return playlistService.readPlaylist(uuid);
    }

    @PutMapping("/{uuid}")
    public Playlist updatePlaylist(@PathVariable("uuid") String uuid, @RequestBody Playlist playlist) {
        return playlistService.updatePlaylist(uuid, playlist);
    }

    @DeleteMapping("/{uuid}")
    public void deletePlaylist(@PathVariable("uuid") String uuid) {
        playlistService.deletePlaylist(uuid);
    }

}
