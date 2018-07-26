package com.convrt.api.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConvertStatus {

	private String uuid;
	private BigDecimal progress;
	private String action;

}
