package com.bc.hackathon.pubsub;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.pubsub.v1.PubsubMessage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.FluxSink;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Optional;

@Service
public class NonBlockingReceiver implements MessageReceiver {
    private static final Logger log = LoggerFactory.getLogger(NonBlockingReceiver.class.getCanonicalName());

    private final WebClient webClient;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final EmitterProcessor<Tuple2<PubsubMessage, AckReplyConsumer>> processor;
    private final FluxSink<Tuple2<PubsubMessage, AckReplyConsumer>> sink;

    public NonBlockingReceiver(WebClient.Builder builder, KafkaTemplate<String, String> kafkaTemplate) {
        this.webClient = builder.build();
        this.kafkaTemplate = kafkaTemplate;

        this.processor = EmitterProcessor.create(16384);
        processor.flatMap(t2 -> {
            String data = t2.getT1().getData().toStringUtf8();
            return webClient.get().uri("http://localhost:8079?value=" + data).retrieve()
                    .bodyToMono(String.class)
                    .retry(3)
                    .map(result -> Tuples.of(result, t2.getT2()));
        }, 2048, 1024)
                .subscribe(t2 -> kafkaTemplate.send("output", t2.getT1())
                        .addCallback(sendResult -> {
                                    t2.getT2().ack();
                                    log.info("finished: {}", Optional.ofNullable(sendResult).map(SendResult::getProducerRecord).map(ProducerRecord::value).orElse(null));
                                },
                                error -> t2.getT2().ack()));
        sink = processor.sink();

    }

    @Override
    public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
        sink.next(Tuples.of(message, consumer));
    }
}
