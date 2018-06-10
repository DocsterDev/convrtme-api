package com.convrt.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name="user")
public class User extends BaseEntity {

	@NotNull
	@Email
	private String email;
	private String name;
	private String pin;
	private String userAgent;

}
