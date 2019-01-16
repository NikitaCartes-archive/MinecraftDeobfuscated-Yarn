package net.minecraft.util;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Actor<Msg> extends AutoCloseable {
	String getName();

	void method_16901(Msg object);

	default void close() {
	}

	default <Source> CompletableFuture<Source> createAndSendFutureActor(Function<? super Actor<Source>, ? extends Msg> function) {
		CompletableFuture<Source> completableFuture = new CompletableFuture();
		Msg object = (Msg)function.apply(createConsumerActor("ask future procesor handle", completableFuture::complete));
		this.method_16901(object);
		return completableFuture;
	}

	static <Msg> Actor<Msg> createConsumerActor(String string, Consumer<Msg> consumer) {
		return new Actor<Msg>() {
			@Override
			public String getName() {
				return string;
			}

			@Override
			public void method_16901(Msg object) {
				consumer.accept(object);
			}

			public String toString() {
				return string;
			}
		};
	}
}
