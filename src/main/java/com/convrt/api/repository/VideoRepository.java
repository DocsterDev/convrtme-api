package com.convrt.api.repository;

import com.convrt.api.entity.Channel;
import com.convrt.api.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface VideoRepository extends JpaRepository<Video, String> {
	Video findById(String id);
	List<Video> findVideosByChannelAndSubscriptionScannedDateIsAfter(Channel channel, Instant subscriptionScannedDate);
}
