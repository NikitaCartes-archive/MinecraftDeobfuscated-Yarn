package net.minecraft;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3900 implements AutoCloseable, class_3193.class_3896 {
	private static final Logger field_17248 = LogManager.getLogger();
	private final Map<class_3906<?>, class_3899<? extends Function<class_3906<class_3902>, ?>>> field_17249;
	private final Set<class_3906<?>> field_17250;
	private final class_3846<class_3847.class_3907> field_17251;

	public class_3900(List<class_3906<?>> list, Executor executor, int i) {
		this.field_17249 = (Map<class_3906<?>, class_3899<? extends Function<class_3906<class_3902>, ?>>>)list.stream()
			.collect(Collectors.toMap(Function.identity(), arg -> new class_3899(arg.method_16898() + "_queue", i)));
		this.field_17250 = Sets.<class_3906<?>>newHashSet(list);
		this.field_17251 = new class_3846<>(new class_3847.class_3848(4), executor, "sorter");
	}

	public static class_3900.class_3946<Runnable> method_17626(Runnable runnable, long l, IntSupplier intSupplier) {
		return new class_3900.class_3946<>(arg -> () -> {
				runnable.run();
				arg.method_16901(class_3902.field_17274);
			}, l, intSupplier);
	}

	public static class_3900.class_3946<Runnable> method_17629(class_3193 arg, Runnable runnable) {
		return method_17626(runnable, arg.method_13994().method_8324(), arg::method_17208);
	}

	public static class_3900.class_3947 method_17627(Runnable runnable, long l, boolean bl) {
		return new class_3900.class_3947(runnable, l, bl);
	}

	public <T> class_3906<class_3900.class_3946<T>> method_17622(class_3906<T> arg, boolean bl) {
		return (class_3906<class_3900.class_3946<T>>)this.field_17251
			.method_17345(
				arg2 -> new class_3847.class_3907(
						0,
						() -> {
							this.method_17632(arg);
							arg2.method_16901(
								class_3906.method_17344(
									"chunk priority sorter around " + arg.method_16898(), arg2xx -> this.method_17282(arg, arg2xx.field_17446, arg2xx.field_17447, arg2xx.field_17448, bl)
								)
							);
						}
					)
			)
			.join();
	}

	public class_3906<class_3900.class_3947> method_17614(class_3906<Runnable> arg) {
		return (class_3906<class_3900.class_3947>)this.field_17251
			.method_17345(
				arg2 -> new class_3847.class_3907(
						0,
						() -> arg2.method_16901(
								class_3906.method_17344(
									"chunk priority sorter around " + arg.method_16898(), arg2xx -> this.method_17615(arg, arg2xx.field_17450, arg2xx.field_17449, arg2xx.field_17451)
								)
							)
					)
			)
			.join();
	}

	@Override
	public void method_17209(class_1923 arg, IntSupplier intSupplier, int i, IntConsumer intConsumer) {
		this.field_17251.method_16901(new class_3847.class_3907(0, () -> {
			int j = intSupplier.getAsInt();
			this.field_17249.values().forEach(arg2 -> arg2.method_17272(j, arg, i));
			intConsumer.accept(i);
		}));
	}

	private <T> void method_17615(class_3906<T> arg, long l, Runnable runnable, boolean bl) {
		this.field_17251.method_16901(new class_3847.class_3907(1, () -> {
			class_3899<Function<class_3906<class_3902>, T>> lv = this.method_17632(arg);
			lv.method_17609(l, bl);
			if (this.field_17250.remove(arg)) {
				this.method_17630(lv, arg);
			}

			runnable.run();
		}));
	}

	private <T> void method_17282(class_3906<T> arg, Function<class_3906<class_3902>, T> function, long l, IntSupplier intSupplier, boolean bl) {
		this.field_17251.method_16901(new class_3847.class_3907(2, () -> {
			class_3899<Function<class_3906<class_3902>, T>> lv = this.method_17632(arg);
			int i = intSupplier.getAsInt();
			lv.method_17274(Optional.of(function), l, i);
			if (bl) {
				lv.method_17274(Optional.empty(), l, i);
			}

			if (this.field_17250.remove(arg)) {
				this.method_17630(lv, arg);
			}
		}));
	}

	private <T> void method_17630(class_3899<Function<class_3906<class_3902>, T>> arg, class_3906<T> arg2) {
		this.field_17251.method_16901(new class_3847.class_3907(3, () -> {
			Stream<Either<Function<class_3906<class_3902>, T>, Runnable>> stream = arg.method_17606();
			if (stream == null) {
				this.field_17250.add(arg2);
			} else {
				class_156.method_652((List)stream.map(either -> either.map(arg2::method_17345, runnable -> {
						runnable.run();
						return CompletableFuture.completedFuture(class_3902.field_17274);
					})).collect(Collectors.toList())).thenAccept(list -> this.method_17630(arg, arg2));
			}
		}));
	}

	private <T> class_3899<Function<class_3906<class_3902>, T>> method_17632(class_3906<T> arg) {
		class_3899<? extends Function<class_3906<class_3902>, ?>> lv = (class_3899<? extends Function<class_3906<class_3902>, ?>>)this.field_17249.get(arg);
		if (lv == null) {
			throw new IllegalArgumentException("No queue for: " + arg);
		} else {
			return (class_3899<Function<class_3906<class_3902>, T>>)lv;
		}
	}

	public void close() {
		this.field_17249.keySet().forEach(class_3906::close);
	}

	public static final class class_3946<T> {
		private final Function<class_3906<class_3902>, T> field_17446;
		private final long field_17447;
		private final IntSupplier field_17448;

		private class_3946(Function<class_3906<class_3902>, T> function, long l, IntSupplier intSupplier) {
			this.field_17446 = function;
			this.field_17447 = l;
			this.field_17448 = intSupplier;
		}
	}

	public static final class class_3947 {
		private final Runnable field_17449;
		private final long field_17450;
		private final boolean field_17451;

		private class_3947(Runnable runnable, long l, boolean bl) {
			this.field_17449 = runnable;
			this.field_17450 = l;
			this.field_17451 = bl;
		}
	}
}
