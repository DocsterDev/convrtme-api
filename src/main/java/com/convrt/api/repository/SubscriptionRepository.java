package com.convrt.api.repository;

import com.convrt.api.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

    @Query("SELECT DISTINCT s.channel FROM Subscription s")
    List<String> findDistinctChannel();

    List<Subscription> findSubscriptionsByChannel(String channel);

}
