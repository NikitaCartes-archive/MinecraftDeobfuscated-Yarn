package net.minecraft.util.thread;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface MessageListener<Msg> extends AutoCloseable {
	String getName();

	void send(Msg message);

	default void close() {
	}

	default <Source> CompletableFuture<Source> ask(Function<? super MessageListener<Source>, ? extends Msg> messageProvider) {
		CompletableFuture<Source> completableFuture = new CompletableFuture();
		Msg object = (Msg)messageProvider.apply(create("ask future procesor handle", completableFuture::complete));
		this.send(object);
		return completableFuture;
	}

	static <Msg> MessageListener<Msg> create(String name, Consumer<Msg> action) {
		return new MessageListener<Msg>() {
			@Override
			public String getName() {
				return name;
			}

			@Override
			public void send(Msg message) {
				action.accept(message);
			}

			public String toString() {
				return name;
			}
		};
	}
}
