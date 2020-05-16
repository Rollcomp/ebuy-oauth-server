package org.ebuy.kafka;

import lombok.extern.slf4j.Slf4j;
import org.ebuy.model.TokenMail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Created by Ozgur Ustun on May, 2020
 */
@Service
@Slf4j
public class Sender {

    private final KafkaTemplate<String, TokenMail> kafkaTemplate;

    @Autowired
    public Sender(KafkaTemplate<String, TokenMail> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public ListenableFuture<SendResult<String, TokenMail>> sendMail(TokenMail tokenMail, String topic) {
        log.debug("message sent to topic: " + topic);
        return kafkaTemplate.send(topic, tokenMail);
    }

}
