package com.convrt.data.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

@Data
public class User extends BaseEntity {

	@NotNull
	@Email
	private String email;
	private String name;

}
