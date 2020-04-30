package com.moup.api.repository;

import com.moup.api.entity.Channel;
import com.moup.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, String> {
    Channel findChannelByName(String name);
    Channel findChannelByChannelId(String channelId);
}
