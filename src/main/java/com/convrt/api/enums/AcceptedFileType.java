package com.convrt.api.enums;

public enum AcceptedFileType {
	MP3, MKV, WAV, WMA, ACC, MP4, AVI, OGG, WEBM;

	public static boolean isAccepted(String fileExtension) {
		for (AcceptedFileType type : AcceptedFileType.values()) {
			if (type.name().equalsIgnoreCase(fileExtension)) {
				return true;
			}
		}
		return false;
	}

}
