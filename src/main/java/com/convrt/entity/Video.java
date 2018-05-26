package com.convrt.entity;

import lombok.Data;

@Data
public class Video extends BaseEntity {

	private String videoId;
	private String searchQuery;
	private String status;
	private String title;
	private String owner;
	private String lastPlayed;
	private String filePath;

}
