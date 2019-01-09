package net.minecraft;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface class_3906<Msg> extends AutoCloseable {
	String method_16898();

	void method_16901(Msg object);

	default void close() {
	}

	default <Source> CompletableFuture<Source> method_17345(Function<? super class_3906<Source>, ? extends Msg> function) {
		CompletableFuture<Source> completableFuture = new CompletableFuture();
		Msg object = (Msg)function.apply(method_17344("ask future procesor handle", completableFuture::complete));
		this.method_16901(object);
		return completableFuture;
	}

	static <Msg> class_3906<Msg> method_17344(String string, Consumer<Msg> consumer) {
		return new class_3906<Msg>() {
			@Override
			public String method_16898() {
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
