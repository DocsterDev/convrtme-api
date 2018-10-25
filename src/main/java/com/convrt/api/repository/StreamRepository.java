package com.convrt.api.repository;

import com.convrt.api.entity.Stream;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StreamRepository extends JpaRepository<Stream, String> {

    Stream findByVideoIdAndExtension(String videoId, String extension);
}
