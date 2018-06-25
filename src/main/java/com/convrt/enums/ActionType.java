package com.convrt.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActionType {
	REGISTER("register"), LOGIN("login"), LOGOUT("logout"), AUTHENTICATE("authenticate");

	private final String name;

}
