package com.paymenthub.saga.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class TransactionDto {

	@NonNull
	private String fromAccount;
	@NonNull
	private String toAccount;
	@NonNull
	private Double amount;
}
