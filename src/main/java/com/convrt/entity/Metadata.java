package com.convrt.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Metadata extends BaseEntity {

	String userUuid;
	String title;
	String conversionFrom;
	String conversionTo;
	boolean uploadComplete;
	boolean conversionComplete;
	Date uploadStarted;
	Date uploadCompleted;
	Date conversionStarted;
	Date conversionCompleted;

}
