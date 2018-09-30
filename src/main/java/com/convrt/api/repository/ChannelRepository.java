package com.convrt.api.repository;

import com.convrt.api.entity.Channel;
import com.convrt.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, String> {

    List<Channel> findChannelsBy(User user);

}
