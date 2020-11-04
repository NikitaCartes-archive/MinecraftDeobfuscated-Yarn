package net.minecraft.util.thread;

import com.mojang.datafixers.util.Either;
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

	default <Source> CompletableFuture<Source> method_27918(Function<? super MessageListener<Either<Source, Exception>>, ? extends Msg> function) {
		CompletableFuture<Source> completableFuture = new CompletableFuture();
		Msg object = (Msg)function.apply(create("ask future procesor handle", either -> {
			either.ifLeft(completableFuture::complete);
			either.ifRight(completableFuture::completeExceptionally);
		}));
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
