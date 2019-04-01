package net.minecraft;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.Hash.Strategy;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_156 {
	private static final AtomicInteger field_18034 = new AtomicInteger(1);
	private static final ExecutorService field_18035 = method_18351();
	public static LongSupplier field_1128 = System::nanoTime;
	private static final Logger field_1129 = LogManager.getLogger();

	public static <K, V> Collector<Entry<? extends K, ? extends V>, ?, Map<K, V>> method_664() {
		return Collectors.toMap(Entry::getKey, Entry::getValue);
	}

	public static <T extends Comparable<T>> String method_650(class_2769<T> arg, Object object) {
		return arg.method_11901((T)object);
	}

	public static String method_646(String string, @Nullable class_2960 arg) {
		return arg == null ? string + ".unregistered_sadface" : string + '.' + arg.method_12836() + '.' + arg.method_12832().replace('/', '.');
	}

	public static long method_658() {
		return method_648() / 1000000L;
	}

	public static long method_648() {
		return field_1128.getAsLong();
	}

	public static long method_659() {
		return Instant.now().toEpochMilli();
	}

	private static ExecutorService method_18351() {
		int i = class_3532.method_15340(Runtime.getRuntime().availableProcessors() - 1, 1, 7);
		ExecutorService executorService;
		if (i <= 0) {
			executorService = MoreExecutors.newDirectExecutorService();
		} else {
			executorService = new ForkJoinPool(i, forkJoinPool -> {
				ForkJoinWorkerThread forkJoinWorkerThread = new ForkJoinWorkerThread(forkJoinPool) {
				};
				forkJoinWorkerThread.setName("Server-Worker-" + field_18034.getAndIncrement());
				return forkJoinWorkerThread;
			}, (thread, throwable) -> {
				if (throwable instanceof CompletionException) {
					throwable = throwable.getCause();
				}

				if (throwable instanceof class_148) {
					class_2966.method_12847(((class_148)throwable).method_631().method_568());
					System.exit(-1);
				}

				field_1129.error(String.format("Caught exception in thread %s", thread), throwable);
			}, true);
		}

		return executorService;
	}

	public static Executor method_18349() {
		return field_18035;
	}

	public static void method_18350() {
		field_18035.shutdown();

		boolean bl;
		try {
			bl = field_18035.awaitTermination(3L, TimeUnit.SECONDS);
		} catch (InterruptedException var2) {
			bl = false;
		}

		if (!bl) {
			field_18035.shutdownNow();
		}
	}

	@Environment(EnvType.CLIENT)
	public static <T> CompletableFuture<T> method_19483(Throwable throwable) {
		CompletableFuture<T> completableFuture = new CompletableFuture();
		completableFuture.completeExceptionally(throwable);
		return completableFuture;
	}

	public static class_156.class_158 method_668() {
		String string = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		if (string.contains("win")) {
			return class_156.class_158.field_1133;
		} else if (string.contains("mac")) {
			return class_156.class_158.field_1137;
		} else if (string.contains("solaris")) {
			return class_156.class_158.field_1134;
		} else if (string.contains("sunos")) {
			return class_156.class_158.field_1134;
		} else if (string.contains("linux")) {
			return class_156.class_158.field_1135;
		} else {
			return string.contains("unix") ? class_156.class_158.field_1135 : class_156.class_158.field_1132;
		}
	}

	public static Stream<String> method_651() {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		return runtimeMXBean.getInputArguments().stream().filter(string -> string.startsWith("-X"));
	}

	public static <T> T method_660(Iterable<T> iterable, @Nullable T object) {
		Iterator<T> iterator = iterable.iterator();
		T object2 = (T)iterator.next();
		if (object != null) {
			T object3 = object2;

			while (object3 != object) {
				if (iterator.hasNext()) {
					object3 = (T)iterator.next();
				}
			}

			if (iterator.hasNext()) {
				return (T)iterator.next();
			}
		}

		return object2;
	}

	public static <T> T method_645(Iterable<T> iterable, @Nullable T object) {
		Iterator<T> iterator = iterable.iterator();
		T object2 = null;

		while (iterator.hasNext()) {
			T object3 = (T)iterator.next();
			if (object3 == object) {
				if (object2 == null) {
					object2 = iterator.hasNext() ? Iterators.getLast(iterator) : object;
				}
				break;
			}

			object2 = object3;
		}

		return object2;
	}

	public static <T> T method_656(Supplier<T> supplier) {
		return (T)supplier.get();
	}

	public static <T> T method_654(T object, Consumer<T> consumer) {
		consumer.accept(object);
		return object;
	}

	public static <K> Strategy<K> method_655() {
		return class_156.class_157.field_1130;
	}

	public static <V> CompletableFuture<List<V>> method_652(List<? extends CompletableFuture<? extends V>> list) {
		List<V> list2 = Lists.<V>newArrayListWithCapacity(list.size());
		CompletableFuture<?>[] completableFutures = new CompletableFuture[list.size()];
		CompletableFuture<Void> completableFuture = new CompletableFuture();
		list.forEach(completableFuture2 -> {
			int i = list2.size();
			list2.add(null);
			completableFutures[i] = completableFuture2.whenComplete((object, throwable) -> {
				if (throwable != null) {
					completableFuture.completeExceptionally(throwable);
				} else {
					list2.set(i, object);
				}
			});
		});
		return CompletableFuture.allOf(completableFutures).applyToEither(completableFuture, void_ -> list2);
	}

	public static <T> Stream<T> method_17815(Optional<? extends T> optional) {
		return DataFixUtils.orElseGet(optional.map(Stream::of), Stream::empty);
	}

	public static <T> Optional<T> method_17974(Optional<T> optional, Consumer<T> consumer, Runnable runnable) {
		if (optional.isPresent()) {
			consumer.accept(optional.get());
		} else {
			runnable.run();
		}

		return optional;
	}

	public static Runnable method_18839(Runnable runnable, Supplier<String> supplier) {
		return runnable;
	}

	public static Optional<UUID> method_19481(String string, Dynamic<?> dynamic) {
		return dynamic.get(string + "Most")
			.asNumber()
			.flatMap(number -> dynamic.get(string + "Least").asNumber().map(number2 -> new UUID(number.longValue(), number2.longValue())));
	}

	public static <T> Dynamic<T> method_19482(String string, UUID uUID, Dynamic<T> dynamic) {
		return dynamic.set(string + "Most", dynamic.createLong(uUID.getMostSignificantBits()))
			.set(string + "Least", dynamic.createLong(uUID.getLeastSignificantBits()));
	}

	static enum class_157 implements Strategy<Object> {
		field_1130;

		@Override
		public int hashCode(Object object) {
			return System.identityHashCode(object);
		}

		@Override
		public boolean equals(Object object, Object object2) {
			return object == object2;
		}
	}

	public static enum class_158 {
		field_1135,
		field_1134,
		field_1133 {
			@Environment(EnvType.CLIENT)
			@Override
			protected String[] method_674(URL uRL) {
				return new String[]{"rundll32", "url.dll,FileProtocolHandler", uRL.toString()};
			}
		},
		field_1137 {
			@Environment(EnvType.CLIENT)
			@Override
			protected String[] method_674(URL uRL) {
				return new String[]{"open", uRL.toString()};
			}
		},
		field_1132;

		private class_158() {
		}

		@Environment(EnvType.CLIENT)
		public void method_669(URL uRL) {
			try {
				Process process = (Process)AccessController.doPrivileged(() -> Runtime.getRuntime().exec(this.method_674(uRL)));

				for (String string : IOUtils.readLines(process.getErrorStream())) {
					class_156.field_1129.error(string);
				}

				process.getInputStream().close();
				process.getErrorStream().close();
				process.getOutputStream().close();
			} catch (IOException | PrivilegedActionException var5) {
				class_156.field_1129.error("Couldn't open url '{}'", uRL, var5);
			}
		}

		@Environment(EnvType.CLIENT)
		public void method_673(URI uRI) {
			try {
				this.method_669(uRI.toURL());
			} catch (MalformedURLException var3) {
				class_156.field_1129.error("Couldn't open uri '{}'", uRI, var3);
			}
		}

		@Environment(EnvType.CLIENT)
		public void method_672(File file) {
			try {
				this.method_669(file.toURI().toURL());
			} catch (MalformedURLException var3) {
				class_156.field_1129.error("Couldn't open file '{}'", file, var3);
			}
		}

		@Environment(EnvType.CLIENT)
		protected String[] method_674(URL uRL) {
			String string = uRL.toString();
			if ("file".equals(uRL.getProtocol())) {
				string = string.replace("file:", "file://");
			}

			return new String[]{"xdg-open", string};
		}

		@Environment(EnvType.CLIENT)
		public void method_670(String string) {
			try {
				this.method_669(new URI(string).toURL());
			} catch (MalformedURLException | IllegalArgumentException | URISyntaxException var3) {
				class_156.field_1129.error("Couldn't open uri '{}'", string, var3);
			}
		}
	}
}
