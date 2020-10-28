package com.paymenthub.saga.controller;

import java.util.Collections;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.paymenthub.saga.domain.Transaction;
import com.paymenthub.saga.dto.TransactionDto;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;

@RestController
@Log4j2
public class Controller {

	@Autowired
	private KafkaSender<String, Transaction> duplicateCheck;

	private String duplicateCheckTopic = "duplicate-check-topic";

	@Autowired
	private ReceiverOptions<String, String> receiverOptions;

	private String duplicateCheckAckTopic = "duplicate-check-ack-topic";

	@PostMapping
	public Mono<Transaction> doSendMoney(@RequestBody TransactionDto transactionDto) {
		Transaction transaction = new Transaction(transactionDto);
		doDuplicateCheck(transaction);
		receiveDuplicateCheckAck(duplicateCheckAckTopic, receiverOptions);
		return null;
	}

	private void doDuplicateCheck(Transaction transaction) {
		duplicateCheck.createOutbound().send(Flux.just(new ProducerRecord<String, Transaction>(duplicateCheckTopic,
				transaction.getTransactionId(), transaction))).then().doOnError(e -> {
					log.error("Sending failed {}", e);
				}).doOnSuccess(s -> {
					log.info("Msg sent to Kafka successfully {}", s);
				}).subscribe();
	}
	
	private void receiveDuplicateCheckAck(String duplicateCheckAckTopic2,
			ReceiverOptions<String, String> receiverOptions2) {
		createKafkaReceiver(duplicateCheckAckTopic, receiverOptions).receive().doOnNext(ack -> {
			System.out.println(ack.key() + " : " + ack.value());
			ack.receiverOffset().acknowledge();
		}).subscribe();
	}

	private KafkaReceiver<String, String> createKafkaReceiver(String topic,
			ReceiverOptions<String, String> receiverOptions) {
		ReceiverOptions<String, String> options = receiverOptions.subscription(Collections.singleton(topic))
				.addAssignListener(partitions -> log.debug("onPartitionsAssigned {}", partitions))
				.addRevokeListener(partitions -> log.debug("onPartitionsRevoked {}", partitions));

		return KafkaReceiver.create(options);
	}
}
