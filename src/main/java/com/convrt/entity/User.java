package com.convrt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "user", indexes = {@Index(name = "user_email_idx0", columnList = "email"), @Index(name = "user_pin_idx1", columnList = "pin")})
public class User extends BaseEntity {

	@NotNull
	@Email
	private String email;
	private String pin;

	@JsonIgnore
	private String userAgent;

}
