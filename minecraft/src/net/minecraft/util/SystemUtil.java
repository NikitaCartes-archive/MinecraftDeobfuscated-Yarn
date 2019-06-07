package net.minecraft.util;

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
import net.minecraft.Bootstrap;
import net.minecraft.state.property.Property;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SystemUtil {
	private static final AtomicInteger field_18034 = new AtomicInteger(1);
	private static final ExecutorService SERVER_WORKER_EXECUTOR = createServerWorkerExecutor();
	public static LongSupplier nanoTimeSupplier = System::nanoTime;
	private static final Logger LOGGER = LogManager.getLogger();

	public static <K, V> Collector<Entry<? extends K, ? extends V>, ?, Map<K, V>> toMap() {
		return Collectors.toMap(Entry::getKey, Entry::getValue);
	}

	public static <T extends Comparable<T>> String getValueAsString(Property<T> property, Object object) {
		return property.getName((T)object);
	}

	public static String createTranslationKey(String string, @Nullable Identifier identifier) {
		return identifier == null ? string + ".unregistered_sadface" : string + '.' + identifier.getNamespace() + '.' + identifier.getPath().replace('/', '.');
	}

	public static long getMeasuringTimeMs() {
		return getMeasuringTimeNano() / 1000000L;
	}

	public static long getMeasuringTimeNano() {
		return nanoTimeSupplier.getAsLong();
	}

	public static long getEpochTimeMs() {
		return Instant.now().toEpochMilli();
	}

	private static ExecutorService createServerWorkerExecutor() {
		int i = MathHelper.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, 7);
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

				if (throwable instanceof CrashException) {
					Bootstrap.println(((CrashException)throwable).getReport().asString());
					System.exit(-1);
				}

				LOGGER.error(String.format("Caught exception in thread %s", thread), throwable);
			}, true);
		}

		return executorService;
	}

	public static Executor getServerWorkerExecutor() {
		return SERVER_WORKER_EXECUTOR;
	}

	public static void shutdownServerWorkerExecutor() {
		SERVER_WORKER_EXECUTOR.shutdown();

		boolean bl;
		try {
			bl = SERVER_WORKER_EXECUTOR.awaitTermination(3L, TimeUnit.SECONDS);
		} catch (InterruptedException var2) {
			bl = false;
		}

		if (!bl) {
			SERVER_WORKER_EXECUTOR.shutdownNow();
		}
	}

	@Environment(EnvType.CLIENT)
	public static <T> CompletableFuture<T> completeExceptionally(Throwable throwable) {
		CompletableFuture<T> completableFuture = new CompletableFuture();
		completableFuture.completeExceptionally(throwable);
		return completableFuture;
	}

	public static SystemUtil.OperatingSystem getOperatingSystem() {
		String string = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		if (string.contains("win")) {
			return SystemUtil.OperatingSystem.WINDOWS;
		} else if (string.contains("mac")) {
			return SystemUtil.OperatingSystem.MAC;
		} else if (string.contains("solaris")) {
			return SystemUtil.OperatingSystem.field_1134;
		} else if (string.contains("sunos")) {
			return SystemUtil.OperatingSystem.field_1134;
		} else if (string.contains("linux")) {
			return SystemUtil.OperatingSystem.field_1135;
		} else {
			return string.contains("unix") ? SystemUtil.OperatingSystem.field_1135 : SystemUtil.OperatingSystem.field_1132;
		}
	}

	public static Stream<String> getJVMFlags() {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		return runtimeMXBean.getInputArguments().stream().filter(string -> string.startsWith("-X"));
	}

	public static <T> T method_20793(List<T> list) {
		return (T)list.get(list.size() - 1);
	}

	public static <T> T next(Iterable<T> iterable, @Nullable T object) {
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

	public static <T> T previous(Iterable<T> iterable, @Nullable T object) {
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

	public static <T> T get(Supplier<T> supplier) {
		return (T)supplier.get();
	}

	public static <T> T consume(T object, Consumer<T> consumer) {
		consumer.accept(object);
		return object;
	}

	public static <K> Strategy<K> identityHashStrategy() {
		return SystemUtil.IdentityHashStrategy.field_1130;
	}

	public static <V> CompletableFuture<List<V>> thenCombine(List<? extends CompletableFuture<? extends V>> list) {
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

	public static <T> Stream<T> stream(Optional<? extends T> optional) {
		return DataFixUtils.orElseGet(optional.map(Stream::of), Stream::empty);
	}

	public static <T> Optional<T> ifPresentOrElse(Optional<T> optional, Consumer<T> consumer, Runnable runnable) {
		if (optional.isPresent()) {
			consumer.accept(optional.get());
		} else {
			runnable.run();
		}

		return optional;
	}

	public static Runnable debugRunnable(Runnable runnable, Supplier<String> supplier) {
		return runnable;
	}

	public static Optional<UUID> readUuid(String string, Dynamic<?> dynamic) {
		return dynamic.get(string + "Most")
			.asNumber()
			.flatMap(number -> dynamic.get(string + "Least").asNumber().map(number2 -> new UUID(number.longValue(), number2.longValue())));
	}

	public static <T> Dynamic<T> writeUuid(String string, UUID uUID, Dynamic<T> dynamic) {
		return dynamic.set(string + "Most", dynamic.createLong(uUID.getMostSignificantBits()))
			.set(string + "Least", dynamic.createLong(uUID.getLeastSignificantBits()));
	}

	static enum IdentityHashStrategy implements Strategy<Object> {
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

	public static enum OperatingSystem {
		field_1135,
		field_1134,
		WINDOWS {
			@Environment(EnvType.CLIENT)
			@Override
			protected String[] getURLOpenCommand(URL uRL) {
				return new String[]{"rundll32", "url.dll,FileProtocolHandler", uRL.toString()};
			}
		},
		MAC {
			@Environment(EnvType.CLIENT)
			@Override
			protected String[] getURLOpenCommand(URL uRL) {
				return new String[]{"open", uRL.toString()};
			}
		},
		field_1132;

		private OperatingSystem() {
		}

		@Environment(EnvType.CLIENT)
		public void open(URL uRL) {
			try {
				Process process = (Process)AccessController.doPrivileged(() -> Runtime.getRuntime().exec(this.getURLOpenCommand(uRL)));

				for (String string : IOUtils.readLines(process.getErrorStream())) {
					SystemUtil.LOGGER.error(string);
				}

				process.getInputStream().close();
				process.getErrorStream().close();
				process.getOutputStream().close();
			} catch (IOException | PrivilegedActionException var5) {
				SystemUtil.LOGGER.error("Couldn't open url '{}'", uRL, var5);
			}
		}

		@Environment(EnvType.CLIENT)
		public void open(URI uRI) {
			try {
				this.open(uRI.toURL());
			} catch (MalformedURLException var3) {
				SystemUtil.LOGGER.error("Couldn't open uri '{}'", uRI, var3);
			}
		}

		@Environment(EnvType.CLIENT)
		public void open(File file) {
			try {
				this.open(file.toURI().toURL());
			} catch (MalformedURLException var3) {
				SystemUtil.LOGGER.error("Couldn't open file '{}'", file, var3);
			}
		}

		@Environment(EnvType.CLIENT)
		protected String[] getURLOpenCommand(URL uRL) {
			String string = uRL.toString();
			if ("file".equals(uRL.getProtocol())) {
				string = string.replace("file:", "file://");
			}

			return new String[]{"xdg-open", string};
		}

		@Environment(EnvType.CLIENT)
		public void open(String string) {
			try {
				this.open(new URI(string).toURL());
			} catch (MalformedURLException | IllegalArgumentException | URISyntaxException var3) {
				SystemUtil.LOGGER.error("Couldn't open uri '{}'", string, var3);
			}
		}
	}
}
