/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Actor<Msg>
extends AutoCloseable {
    public String getName();

    public void send(Msg var1);

    @Override
    default public void close() {
    }

    default public <Source> CompletableFuture<Source> createAndSendFutureActor(Function<? super Actor<Source>, ? extends Msg> function) {
        CompletableFuture completableFuture = new CompletableFuture();
        Msg object = function.apply(Actor.createConsumerActor("ask future procesor handle", completableFuture::complete));
        this.send(object);
        return completableFuture;
    }

    public static <Msg> Actor<Msg> createConsumerActor(final String string, final Consumer<Msg> consumer) {
        return new Actor<Msg>(){

            @Override
            public String getName() {
                return string;
            }

            @Override
            public void send(Msg object) {
                consumer.accept(object);
            }

            public String toString() {
                return string;
            }
        };
    }
}

