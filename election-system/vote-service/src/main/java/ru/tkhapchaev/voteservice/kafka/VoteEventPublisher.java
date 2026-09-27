package ru.tkhapchaev.voteservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VoteEventPublisher {

    private final KafkaTemplate<String, VoteCreatedEvent> kafkaTemplate;

    public void publishVoteCreated(VoteCreatedEvent event, String topic) {
        kafkaTemplate.send(topic, event.voteId().toString(), event);
    }
}
