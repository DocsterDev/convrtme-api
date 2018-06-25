package com.convrt.controller;

import com.convrt.entity.Context;
import com.convrt.entity.Playlist;
import com.convrt.entity.User;
import com.convrt.service.ContextService;
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
    private ContextService contextService;

    @PostMapping
    public Playlist createPlaylist (@RequestHeader(value = "user-token") String token, @RequestBody @NonNull Playlist playlist) {
        Context context = contextService.validateContext(token);
        if (playlist.getUuid() != null) {
            Playlist playlistPersistent = playlistService.readPlaylist(context.getUser(), playlist.getUuid());
            if (playlistPersistent != null) {
                throw new RuntimeException("Cant create a new playlist that already exists");
            }
        }
        playlist.setUuid(UUID.randomUUID().toString());
        return playlistService.createPlaylist(context.getUser(), playlist);
    }

//    @GetMapping
//    public Map<String, List<Video>> getAllPlaylists() {
//        return playlistService.getAllPlaylists();
//    }

    @GetMapping("/{uuid}")
    public Playlist getPlaylist(@RequestHeader(value = "user-token") String token, @PathVariable(value = "uuid") String uuid) {
        Context context = contextService.validateContext(token);
        return playlistService.readPlaylist(context.getUser(), uuid);
    }

    @PutMapping("/{uuid}")
    public Playlist updatePlaylist(@RequestHeader(value = "user-token") String token, @RequestBody Playlist playlist) {
        Context context = contextService.validateContext(token);
        return playlistService.updatePlaylist(context.getUser(), playlist);
    }

    @DeleteMapping("/{uuid}")
    public void deletePlaylist(@RequestHeader(value = "user-token") String token, @PathVariable("uuid") String uuid) {
        Context context = contextService.validateContext(token);
        playlistService.deletePlaylist(uuid);
    }

}
