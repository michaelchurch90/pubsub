package com.bc.hackathon.pubsub;

import com.google.cloud.pubsub.v1.Subscriber;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class SubscriberInitializer {
    private final Subscriber subscriber;

    public SubscriberInitializer(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    @PostConstruct
    public void onStart() {
        subscriber.startAsync().awaitRunning();
    }
}
