package com.bc.hackathon.pubsub;

import com.google.api.gax.batching.FlowControlSettings;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PullRequest;
import com.google.pubsub.v1.PullResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.threeten.bp.Duration;

import java.io.IOException;

@Configuration
public class SubscriberConfig {
    private static final String PROJECT_ID = "mcpubsub-1533048840623";
    private static final String SUBSCRIPTION_ID = "my-sub";

    private ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(PROJECT_ID, SUBSCRIPTION_ID);

    @Bean
    public Subscriber subscriber(MessageReceiver messageReceiver) {
        FlowControlSettings flowControlSettings = FlowControlSettings.newBuilder()
                .setMaxOutstandingElementCount(10000L)
                .build();
        return Subscriber.newBuilder(subscriptionName, messageReceiver)
                .setMaxAckExtensionPeriod(Duration.ofMinutes(5L))
                .setFlowControlSettings(flowControlSettings)
                .build();
    }

    public void test() throws IOException {
//        SubscriberStubSettings stubSettings = SubscriberStubSettings.newBuilder()
//                .build();
//        SubscriberStub subscriber = GrpcSubscriberStub.create(stubSettings);
//
//        PullRequest pullRequest = PullRequest.newBuilder()
//                .setMaxMessages(10000)
//                .setReturnImmediately(false)
//                .setSubscription(subscriptionName.getSubscription())
//                .build();
//
//        PullResponse pullResponse = subscriber.pullCallable().call(pullRequest);
//        pullResponse.
    }
}
