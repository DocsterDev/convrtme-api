package com.convrt.controller;

import com.convrt.entity.Context;
import com.convrt.entity.Playlist;
import com.convrt.service.ContextService;
import com.convrt.service.PlaylistService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
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
    public Playlist createPlaylist (@RequestHeader(value = "token", required = false) String token, @RequestBody @NonNull Playlist playlist) {
        Context context = contextService.validateContext(token);
        Playlist playlistPersistent = playlistService.readPlaylist(context.getUser(), playlist.getName());
        if (playlistPersistent != null) {
            throw new RuntimeException("Cant create a new playlist that already exists");
        }
        playlist.setUuid(UUID.randomUUID().toString());
        return playlistService.createPlaylist(context.getUser(), playlist);
    }

    @GetMapping("/{uuid}")
    public Playlist getPlaylist(@RequestHeader(value = "token") String token, @PathVariable(value = "uuid") String uuid) {
        Context context = contextService.validateContext(token);
        return playlistService.readPlaylist(context.getUser(), uuid);
    }

    @GetMapping
    public List<Playlist> getPlaylists(@RequestHeader(value = "token") String token) {
        Context context = contextService.validateContext(token);
        return playlistService.readUserPlaylists(context.getUser());
    }

    @PutMapping
    public List<Playlist> updatePlaylists(@RequestHeader(value = "token") String token, @RequestBody @Valid List<Playlist> playlists) {
        Context context = contextService.validateContext(token);
        return playlistService.updatePlaylists(context.getUser(), playlists);
    }

    @PutMapping("/{uuid}/active")
    public void setActive(@RequestHeader(value = "token") String token, @PathVariable(value = "uuid") String uuid) {
        Context context = contextService.validateContext(token);
        playlistService.setActive(context.getUser(), uuid);
    }

    @PutMapping("/{uuid}")
    public Playlist updatePlaylist(@RequestHeader(value = "token") String token, @PathVariable(value = "uuid") String uuid, @RequestBody @Valid Playlist playlist) {
        Context context = contextService.validateContext(token);
        playlist.setUuid(uuid);
        return playlistService.updatePlaylist(context.getUser(), playlist);
    }

    @DeleteMapping("/{uuid}")
    public void deletePlaylist(@RequestHeader(value = "token") String token, @PathVariable("uuid") String uuid) {
        Context context = contextService.validateContext(token);
        playlistService.deletePlaylist(context.getUser(), uuid);
    }

}
