package com.paymenthub.saga.domain;

import java.sql.Timestamp;
import java.util.UUID;

import com.paymenthub.saga.dto.TransactionDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Transaction {

	@NonNull
	private String fromAccount;
	@NonNull
	private String toAccount;
	@NonNull
	private Double amount;
	@NonNull
	private String status;
	@NonNull
	private Timestamp timestamp;
	@NonNull
	private String transactionId;
	
	public Transaction(TransactionDto dto) {
		super();
		this.fromAccount = dto.getFromAccount();
		this.toAccount = dto.getToAccount();
		this.amount = dto.getAmount();
		this.status = "Initiated";
		this.timestamp = new Timestamp(System.currentTimeMillis());
		this.transactionId = String.valueOf(Math.abs(UUID.randomUUID().getLeastSignificantBits()));
	}
}
