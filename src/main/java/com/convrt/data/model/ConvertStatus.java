package com.convrt.data.model;

import java.math.BigDecimal;

public class ConvertStatus {

	private String uuid;
	private BigDecimal progress;
	private String action;

	public ConvertStatus() {
	}

	public ConvertStatus(String uuid, BigDecimal progress, String action) {
		this.uuid = uuid;
		this.progress = progress;
		this.action = action;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public BigDecimal getProgress() {
		return progress;
	}

	public void setProgress(BigDecimal progress) {
		this.progress = progress;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
