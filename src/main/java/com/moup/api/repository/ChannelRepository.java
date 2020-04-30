package com.moup.api.repository;

import com.moup.api.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, String> {
    Channel findChannelByName(String name);
    Channel findChannelByChannelId(String channelId);
}
