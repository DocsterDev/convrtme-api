package com.convrt.entity;

import com.convrt.view.View;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseEntity {

	@Id
	@JsonView(View.BaseView.class)
	private String uuid;
	private Timestamp createdDate;
	private Timestamp modifiedDate;

}
