package com.moup.api.repository;

import com.moup.api.entity.Stream;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StreamRepository extends JpaRepository<Stream, String> {

    Stream findByVideoIdAndExtension(String videoId, String extension);
}
