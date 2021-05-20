package com.revature.autosurvey.users.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
public class SqsSender {
	
	private final SqsAsyncClient sqsAsyncClient;
	
	@Autowired
	public SqsSender(SqsAsyncClient sqsAsyncClient) {
        this.sqsAsyncClient = sqsAsyncClient;
    }
	
    @PostConstruct
    public void sendMessageRequest(String toQueue, String key, MessageAttributeValue value) throws Exception {
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        messageAttributes.put(key, value);
    	CompletableFuture<GetQueueUrlResponse> msg = sqsAsyncClient
        		.getQueueUrl(
        				GetQueueUrlRequest
        				.builder()
        				.queueName(toQueue)
        				.build());
        GetQueueUrlResponse getQueueUrlResponse = msg.get();

        Mono.fromFuture(() -> sqsAsyncClient.sendMessage(
                SendMessageRequest.builder()
                        .queueUrl(getQueueUrlResponse.queueUrl())
                        .messageAttributes(messageAttributes)
                        .build()
            ))
                .retryWhen(Retry.max(3))
                .repeat(5)
                .subscribe();
    }
}
