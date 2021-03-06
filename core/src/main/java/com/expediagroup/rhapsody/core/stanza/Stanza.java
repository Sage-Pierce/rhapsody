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
package com.expediagroup.rhapsody.core.stanza;

import java.util.concurrent.atomic.AtomicReference;

import com.expediagroup.rhapsody.util.Throwing;

import reactor.core.Disposable;

/**
 * A Stanza is a stream process that can be started and stopped
 *
 * @param <C> The type modeling configuration information for this Stanza
 */
public abstract class Stanza<C extends StanzaConfig> {
    
    public enum State { STOPPED, STARTING, STARTED }

    private static final Disposable EMPTY = () -> {};

    private static final Disposable STARTING = () -> {};

    private final AtomicReference<Disposable> disposableReference = new AtomicReference<>(EMPTY);

    public final synchronized void start(C config) {
        if (!disposableReference.compareAndSet(EMPTY, STARTING) && !disposableReference.get().isDisposed()) {
            throw new UnsupportedOperationException("Cannot start Stanza that is already starting/started");
        }

        try {
            disposableReference.set(startDisposable(config));
        } catch (Throwable error) {
            disposableReference.set(EMPTY);
            throw Throwing.propagate(error);
        }
    }

    public final synchronized void stop() {
        disposableReference.getAndSet(EMPTY).dispose();
    }

    public final State state() {
        Disposable disposable = disposableReference.get();
        if (disposable == STARTING) {
            return State.STARTING;
        } else if (disposable == EMPTY || disposable.isDisposed()) {
            return State.STOPPED;
        } else {
            return State.STARTED;
        }
    }

    protected abstract Disposable startDisposable(C config);
}
