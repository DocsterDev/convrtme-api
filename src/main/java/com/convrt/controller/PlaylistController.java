package com.convrt.controller;

import com.convrt.entity.Playlist;
import com.convrt.entity.User;
import com.convrt.service.PlaylistService;
import com.convrt.service.UserService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/videos/playlists")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;
    @Autowired
    private UserService userService;

    @PostMapping
    public Playlist createPlaylist (@RequestHeader(value = "User", required = false) String userUuid, @RequestBody @NonNull Playlist playlist) {
        User user = null;
        if (userUuid != null) {
            user = userService.readUser(userUuid);
        }
        if (playlist.getUuid() != null) {
            Playlist playlistPersistent = playlistService.readPlaylist(user, playlist.getUuid());
            if (playlistPersistent != null) {
                throw new RuntimeException("Cant create a new playlist that already exists");
            }
        }
        playlist.setUuid(UUID.randomUUID().toString());
        return playlistService.createPlaylist(user, playlist);
    }

//    @GetMapping
//    public Map<String, List<Video>> getAllPlaylists() {
//        return playlistService.getAllPlaylists();
//    }

    @GetMapping("/{uuid}")
    public Playlist getPlaylist(@RequestHeader(value = "User", required = false) String userUuid, @PathVariable(value = "uuid") String uuid) {
        User user = userService.readUser(userUuid);
        return playlistService.readPlaylist(user, uuid);
    }

    @PutMapping("/{uuid}")
    public Playlist updatePlaylist(@RequestHeader(value = "User", required = false) String userUuid, @RequestBody Playlist playlist) {
        User user = userService.readUser(userUuid);
        return playlistService.updatePlaylist(user, playlist);
    }

    @DeleteMapping("/{uuid}")
    public void deletePlaylist(@PathVariable("uuid") String uuid) {
        playlistService.deletePlaylist(uuid);
    }

}
