/**
 * Copyright 2019 Expedia, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.expediagroup.rhapsody.rabbitmq.factory;

import java.util.function.Function;

import org.reactivestreams.Publisher;

import com.expediagroup.rhapsody.api.Acknowledgeable;
import com.expediagroup.rhapsody.rabbitmq.message.RabbitMessageCreator;

import reactor.core.publisher.Flux;
import reactor.rabbitmq.CorrelableOutboundMessage;
import reactor.rabbitmq.OutboundMessageResult;

public class RabbitMQBodySenderFactory<T> extends RabbitMQSenderFactory<T> {

    public RabbitMQBodySenderFactory(RabbitConfigFactory configFactory) {
        super(configFactory);
    }

    public Function<Publisher<Acknowledgeable<T>>, Flux<Acknowledgeable<OutboundMessageResult<CorrelableOutboundMessage<T>>>>>
    sendAcknowledgeableBodies(RabbitMessageCreator<T> rabbitMessageCreator) {
        return bodies -> sendAcknowledgeableBodies(bodies, rabbitMessageCreator);
    }

    public Flux<Acknowledgeable<OutboundMessageResult<CorrelableOutboundMessage<T>>>>
    sendAcknowledgeableBodies(Publisher<Acknowledgeable<T>> bodies, RabbitMessageCreator<T> rabbitMessageCreator) {
        return Flux.from(bodies).map(Acknowledgeable.mapping(rabbitMessageCreator)).transform(this::sendAcknowledgeable);
    }

    public Function<Publisher<T>, Flux<OutboundMessageResult<CorrelableOutboundMessage<T>>>>
    sendBodies(RabbitMessageCreator<T> rabbitMessageCreator) {
        return bodies -> sendBodies(bodies, rabbitMessageCreator);
    }

    public Flux<OutboundMessageResult<CorrelableOutboundMessage<T>>>
    sendBodies(Publisher<T> bodies, RabbitMessageCreator<T> rabbitMessageCreator) {
        return Flux.from(bodies).map(rabbitMessageCreator).transform(this::send);
    }
}
