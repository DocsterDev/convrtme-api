package com.moup.api.repository;

import com.moup.api.entity.Channel;
import com.moup.api.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface VideoRepository extends JpaRepository<Video, String> {
	List<Video> findVideosByChannelAndSubscriptionScannedDateIsAfter(Channel channel, Instant subscriptionScannedDate);
}
