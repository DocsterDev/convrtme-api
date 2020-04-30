package com.convrt.api.repository;

import com.convrt.api.entity.Channel;
import com.convrt.api.entity.Subscription;
import com.convrt.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

    @Query("SELECT DISTINCT s.channel FROM Subscription s")
    List<Channel> findDistinctChannel();

    List<Subscription> findSubscriptionsByChannel(String channel);

    boolean existsByChannelAndUser(Channel channel, User user);

    void deleteByUuidAndUser(String uuid, User user);

    List<Subscription> findByUser(User user);

    Long countByChannelUuid(String channelUuid);

}
