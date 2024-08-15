package net.minecraft.util;

import com.google.common.base.Ticker;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceImmutableList;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.Schemas;
import net.minecraft.registry.Registry;
import net.minecraft.state.property.Property;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ReportType;
import net.minecraft.util.function.CharPredicate;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;

/**
 * A class holding various utility methods.
 */
public class Util {
	static final Logger LOGGER = LogUtils.getLogger();
	private static final int MAX_PARALLELISM = 255;
	private static final int BACKUP_ATTEMPTS = 10;
	private static final String MAX_BG_THREADS_PROPERTY = "max.bg.threads";
	private static final ExecutorService MAIN_WORKER_EXECUTOR = createWorker("Main");
	private static final ExecutorService IO_WORKER_EXECUTOR = createIoWorker("IO-Worker-", false);
	private static final ExecutorService DOWNLOAD_WORKER_EXECUTOR = createIoWorker("Download-", true);
	/**
	 * A locale-independent datetime formatter that uses {@code yyyy-MM-dd_HH.mm.ss}
	 * as the format string. Example: {@code 2022-01-01_00.00.00}
	 */
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss", Locale.ROOT);
	public static final int field_46220 = 8;
	private static final Set<String> SUPPORTED_URI_PROTOCOLS = Set.of("http", "https");
	public static final long field_45714 = 1000000L;
	public static TimeSupplier.Nanoseconds nanoTimeSupplier = System::nanoTime;
	public static final Ticker TICKER = new Ticker() {
		@Override
		public long read() {
			return Util.nanoTimeSupplier.getAsLong();
		}
	};
	/**
	 * The "nil UUID" that represents lack of a UUID.
	 */
	public static final UUID NIL_UUID = new UUID(0L, 0L);
	/**
	 * The file system provider for handling jar and zip files.
	 */
	public static final FileSystemProvider JAR_FILE_SYSTEM_PROVIDER = (FileSystemProvider)FileSystemProvider.installedProviders()
		.stream()
		.filter(fileSystemProvider -> fileSystemProvider.getScheme().equalsIgnoreCase("jar"))
		.findFirst()
		.orElseThrow(() -> new IllegalStateException("No jar file system provider found"));
	private static Consumer<String> missingBreakpointHandler = message -> {
	};

	public static <K, V> Collector<Entry<? extends K, ? extends V>, ?, Map<K, V>> toMap() {
		return Collectors.toMap(Entry::getKey, Entry::getValue);
	}

	public static <T> Collector<T, ?, List<T>> toArrayList() {
		return Collectors.toCollection(Lists::newArrayList);
	}

	public static <T extends Comparable<T>> String getValueAsString(Property<T> property, Object value) {
		return property.name((T)value);
	}

	/**
	 * {@return the translation key constructed from {@code type} and {@code id}}
	 * 
	 * <p>If {@code id} is {@code null}, {@code unregistered_sadface} is used as the ID.
	 * 
	 * @see Identifier#toTranslationKey(String)
	 */
	public static String createTranslationKey(String type, @Nullable Identifier id) {
		return id == null ? type + ".unregistered_sadface" : type + "." + id.getNamespace() + "." + id.getPath().replace('/', '.');
	}

	/**
	 * {@return the current time in milliseconds, to be used for measuring a duration}
	 * 
	 * <p>This is not the Unix epoch time, and can only be used to determine the duration
	 * between two calls of this method.
	 * 
	 * @see #getMeasuringTimeNano
	 * @see #getEpochTimeMs
	 */
	public static long getMeasuringTimeMs() {
		return getMeasuringTimeNano() / 1000000L;
	}

	/**
	 * {@return the current time in nanoseconds, to be used for measuring a duration}
	 * 
	 * <p>This is not the Unix epoch time, and can only be used to determine the duration
	 * between two calls of this method.
	 * 
	 * @see #getMeasuringTimeMs
	 * @see #getEpochTimeMs
	 */
	public static long getMeasuringTimeNano() {
		return nanoTimeSupplier.getAsLong();
	}

	/**
	 * {@return the milliseconds passed since the Unix epoch}
	 * 
	 * <p>This should be used to display or store the current time. {@link #getMeasuringTimeMs}
	 * should be used for determining the duration between two calls.
	 * 
	 * @see #getMeasuringTimeMs
	 * @see #getMeasuringTimeNano
	 */
	public static long getEpochTimeMs() {
		return Instant.now().toEpochMilli();
	}

	/**
	 * {@return the current time formatted using {@link #DATE_TIME_FORMATTER}}
	 */
	public static String getFormattedCurrentTime() {
		return DATE_TIME_FORMATTER.format(ZonedDateTime.now());
	}

	private static ExecutorService createWorker(String name) {
		int i = MathHelper.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, getMaxBackgroundThreads());
		ExecutorService executorService;
		if (i <= 0) {
			executorService = MoreExecutors.newDirectExecutorService();
		} else {
			AtomicInteger atomicInteger = new AtomicInteger(1);
			executorService = new ForkJoinPool(i, pool -> {
				ForkJoinWorkerThread forkJoinWorkerThread = new ForkJoinWorkerThread(pool) {
					protected void onTermination(Throwable throwable) {
						if (throwable != null) {
							Util.LOGGER.warn("{} died", this.getName(), throwable);
						} else {
							Util.LOGGER.debug("{} shutdown", this.getName());
						}

						super.onTermination(throwable);
					}
				};
				forkJoinWorkerThread.setName("Worker-" + name + "-" + atomicInteger.getAndIncrement());
				return forkJoinWorkerThread;
			}, Util::uncaughtExceptionHandler, true);
		}

		return executorService;
	}

	private static int getMaxBackgroundThreads() {
		String string = System.getProperty("max.bg.threads");
		if (string != null) {
			try {
				int i = Integer.parseInt(string);
				if (i >= 1 && i <= 255) {
					return i;
				}

				LOGGER.error("Wrong {} property value '{}'. Should be an integer value between 1 and {}.", "max.bg.threads", string, 255);
			} catch (NumberFormatException var2) {
				LOGGER.error("Could not parse {} property value '{}'. Should be an integer value between 1 and {}.", "max.bg.threads", string, 255);
			}
		}

		return 255;
	}

	/**
	 * {@return the main worker executor for miscellaneous asynchronous tasks}
	 */
	public static ExecutorService getMainWorkerExecutor() {
		return MAIN_WORKER_EXECUTOR;
	}

	/**
	 * {@return the executor for disk or network IO tasks}
	 */
	public static ExecutorService getIoWorkerExecutor() {
		return IO_WORKER_EXECUTOR;
	}

	/**
	 * {@return the executor for download tasks}
	 */
	public static ExecutorService getDownloadWorkerExecutor() {
		return DOWNLOAD_WORKER_EXECUTOR;
	}

	public static void shutdownExecutors() {
		attemptShutdown(MAIN_WORKER_EXECUTOR);
		attemptShutdown(IO_WORKER_EXECUTOR);
	}

	private static void attemptShutdown(ExecutorService service) {
		service.shutdown();

		boolean bl;
		try {
			bl = service.awaitTermination(3L, TimeUnit.SECONDS);
		} catch (InterruptedException var3) {
			bl = false;
		}

		if (!bl) {
			service.shutdownNow();
		}
	}

	private static ExecutorService createIoWorker(String namePrefix, boolean daemon) {
		AtomicInteger atomicInteger = new AtomicInteger(1);
		return Executors.newCachedThreadPool(runnable -> {
			Thread thread = new Thread(runnable);
			thread.setName(namePrefix + atomicInteger.getAndIncrement());
			thread.setDaemon(daemon);
			thread.setUncaughtExceptionHandler(Util::uncaughtExceptionHandler);
			return thread;
		});
	}

	/**
	 * Throws {@code t} if it's a {@link RuntimeException} (or any of its subclass), otherwise
	 * {@code t} wrapped in a RuntimeException.
	 * 
	 * <p>{@link Error} is wrapped as well, despite being unchecked.
	 */
	public static void throwUnchecked(Throwable t) {
		throw t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
	}

	private static void uncaughtExceptionHandler(Thread thread, Throwable t) {
		getFatalOrPause(t);
		if (t instanceof CompletionException) {
			t = t.getCause();
		}

		if (t instanceof CrashException crashException) {
			Bootstrap.println(crashException.getReport().asString(ReportType.MINECRAFT_CRASH_REPORT));
			System.exit(-1);
		}

		LOGGER.error(String.format(Locale.ROOT, "Caught exception in thread %s", thread), t);
	}

	@Nullable
	public static Type<?> getChoiceType(TypeReference typeReference, String id) {
		return !SharedConstants.useChoiceTypeRegistrations ? null : getChoiceTypeInternal(typeReference, id);
	}

	@Nullable
	private static Type<?> getChoiceTypeInternal(TypeReference typeReference, String id) {
		Type<?> type = null;

		try {
			type = Schemas.getFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getSaveVersion().getId())).getChoiceType(typeReference, id);
		} catch (IllegalArgumentException var4) {
			LOGGER.error("No data fixer registered for {}", id);
			if (SharedConstants.isDevelopment) {
				throw var4;
			}
		}

		return type;
	}

	public static Runnable debugRunnable(String activeThreadName, Runnable task) {
		return SharedConstants.isDevelopment ? () -> {
			Thread thread = Thread.currentThread();
			String string2 = thread.getName();
			thread.setName(activeThreadName);

			try {
				task.run();
			} finally {
				thread.setName(string2);
			}
		} : task;
	}

	public static <V> Supplier<V> debugSupplier(String activeThreadName, Supplier<V> supplier) {
		return SharedConstants.isDevelopment ? () -> {
			Thread thread = Thread.currentThread();
			String string2 = thread.getName();
			thread.setName(activeThreadName);

			Object var4;
			try {
				var4 = supplier.get();
			} finally {
				thread.setName(string2);
			}

			return var4;
		} : supplier;
	}

	public static <T> String registryValueToString(Registry<T> registry, T value) {
		Identifier identifier = registry.getId(value);
		return identifier == null ? "[unregistered]" : identifier.toString();
	}

	public static <T> Predicate<T> and() {
		return o -> true;
	}

	public static <T> Predicate<T> and(Predicate<? super T> a) {
		return (Predicate<T>)a;
	}

	public static <T> Predicate<T> and(Predicate<? super T> a, Predicate<? super T> b) {
		return o -> a.test(o) && b.test(o);
	}

	public static <T> Predicate<T> and(Predicate<? super T> a, Predicate<? super T> b, Predicate<? super T> c) {
		return o -> a.test(o) && b.test(o) && c.test(o);
	}

	public static <T> Predicate<T> and(Predicate<? super T> a, Predicate<? super T> b, Predicate<? super T> c, Predicate<? super T> d) {
		return o -> a.test(o) && b.test(o) && c.test(o) && d.test(o);
	}

	public static <T> Predicate<T> and(Predicate<? super T> a, Predicate<? super T> b, Predicate<? super T> c, Predicate<? super T> d, Predicate<? super T> e) {
		return o -> a.test(o) && b.test(o) && c.test(o) && d.test(o) && e.test(o);
	}

	@SafeVarargs
	public static <T> Predicate<T> and(Predicate<? super T>... predicates) {
		return o -> {
			for (Predicate<? super T> predicate : predicates) {
				if (!predicate.test(o)) {
					return false;
				}
			}

			return true;
		};
	}

	public static <T> Predicate<T> allOf(List<? extends Predicate<? super T>> predicates) {
		return switch (predicates.size()) {
			case 0 -> and();
			case 1 -> and((Predicate<? super T>)predicates.get(0));
			case 2 -> and((Predicate<? super T>)predicates.get(0), (Predicate<? super T>)predicates.get(1));
			case 3 -> and((Predicate<? super T>)predicates.get(0), (Predicate<? super T>)predicates.get(1), (Predicate<? super T>)predicates.get(2));
			case 4 -> and(
			(Predicate<? super T>)predicates.get(0),
			(Predicate<? super T>)predicates.get(1),
			(Predicate<? super T>)predicates.get(2),
			(Predicate<? super T>)predicates.get(3)
		);
			case 5 -> and(
			(Predicate<? super T>)predicates.get(0),
			(Predicate<? super T>)predicates.get(1),
			(Predicate<? super T>)predicates.get(2),
			(Predicate<? super T>)predicates.get(3),
			(Predicate<? super T>)predicates.get(4)
		);
			default -> {
				Predicate<? super T>[] predicates2 = (Predicate<? super T>[])predicates.toArray(Predicate[]::new);
				yield and(predicates2);
			}
		};
	}

	public static <T> Predicate<T> or() {
		return o -> false;
	}

	public static <T> Predicate<T> or(Predicate<? super T> a) {
		return (Predicate<T>)a;
	}

	public static <T> Predicate<T> or(Predicate<? super T> a, Predicate<? super T> b) {
		return o -> a.test(o) || b.test(o);
	}

	public static <T> Predicate<T> or(Predicate<? super T> a, Predicate<? super T> b, Predicate<? super T> c) {
		return o -> a.test(o) || b.test(o) || c.test(o);
	}

	public static <T> Predicate<T> or(Predicate<? super T> a, Predicate<? super T> b, Predicate<? super T> c, Predicate<? super T> d) {
		return o -> a.test(o) || b.test(o) || c.test(o) || d.test(o);
	}

	public static <T> Predicate<T> or(Predicate<? super T> a, Predicate<? super T> b, Predicate<? super T> c, Predicate<? super T> d, Predicate<? super T> e) {
		return o -> a.test(o) || b.test(o) || c.test(o) || d.test(o) || e.test(o);
	}

	@SafeVarargs
	public static <T> Predicate<T> or(Predicate<? super T>... predicates) {
		return o -> {
			for (Predicate<? super T> predicate : predicates) {
				if (predicate.test(o)) {
					return true;
				}
			}

			return false;
		};
	}

	public static <T> Predicate<T> anyOf(List<? extends Predicate<? super T>> predicates) {
		return switch (predicates.size()) {
			case 0 -> or();
			case 1 -> or((Predicate<? super T>)predicates.get(0));
			case 2 -> or((Predicate<? super T>)predicates.get(0), (Predicate<? super T>)predicates.get(1));
			case 3 -> or((Predicate<? super T>)predicates.get(0), (Predicate<? super T>)predicates.get(1), (Predicate<? super T>)predicates.get(2));
			case 4 -> or(
			(Predicate<? super T>)predicates.get(0),
			(Predicate<? super T>)predicates.get(1),
			(Predicate<? super T>)predicates.get(2),
			(Predicate<? super T>)predicates.get(3)
		);
			case 5 -> or(
			(Predicate<? super T>)predicates.get(0),
			(Predicate<? super T>)predicates.get(1),
			(Predicate<? super T>)predicates.get(2),
			(Predicate<? super T>)predicates.get(3),
			(Predicate<? super T>)predicates.get(4)
		);
			default -> {
				Predicate<? super T>[] predicates2 = (Predicate<? super T>[])predicates.toArray(Predicate[]::new);
				yield or(predicates2);
			}
		};
	}

	public static <T> boolean isSymmetrical(int width, int height, List<T> list) {
		if (width == 1) {
			return true;
		} else {
			int i = width / 2;

			for (int j = 0; j < height; j++) {
				for (int k = 0; k < i; k++) {
					int l = width - 1 - k;
					T object = (T)list.get(k + j * width);
					T object2 = (T)list.get(l + j * width);
					if (!object.equals(object2)) {
						return false;
					}
				}
			}

			return true;
		}
	}

	/**
	 * {@return the operating system instance for the current platform}
	 * 
	 * @implNote This uses the {@code os.name} system property to determine the operating system.
	 * @apiNote This is used for opening links.
	 */
	public static Util.OperatingSystem getOperatingSystem() {
		String string = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		if (string.contains("win")) {
			return Util.OperatingSystem.WINDOWS;
		} else if (string.contains("mac")) {
			return Util.OperatingSystem.OSX;
		} else if (string.contains("solaris")) {
			return Util.OperatingSystem.SOLARIS;
		} else if (string.contains("sunos")) {
			return Util.OperatingSystem.SOLARIS;
		} else if (string.contains("linux")) {
			return Util.OperatingSystem.LINUX;
		} else {
			return string.contains("unix") ? Util.OperatingSystem.LINUX : Util.OperatingSystem.UNKNOWN;
		}
	}

	public static URI validateUri(String uri) throws URISyntaxException {
		URI uRI = new URI(uri);
		String string = uRI.getScheme();
		if (string == null) {
			throw new URISyntaxException(uri, "Missing protocol in URI: " + uri);
		} else {
			String string2 = string.toLowerCase(Locale.ROOT);
			if (!SUPPORTED_URI_PROTOCOLS.contains(string2)) {
				throw new URISyntaxException(uri, "Unsupported protocol in URI: " + uri);
			} else {
				return uRI;
			}
		}
	}

	/**
	 * {@return a stream of JVM flags passed when launching}
	 * 
	 * <p>The streamed strings include the {@code -X} prefix.
	 */
	public static Stream<String> getJVMFlags() {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		return runtimeMXBean.getInputArguments().stream().filter(runtimeArg -> runtimeArg.startsWith("-X"));
	}

	/**
	 * {@return the last item of {@code list}}
	 * 
	 * @throws IndexOutOfBoundsException if {@code list} is empty
	 */
	public static <T> T getLast(List<T> list) {
		return (T)list.get(list.size() - 1);
	}

	/**
	 * {@return the item succeeding {@code object} in {@code iterable}}
	 * 
	 * @implNote If {@code object} is {@code null}, this returns the first item of the iterable.
	 * If {@code object} is not in {@code iterable}, this enters into an infinite loop.
	 * {@code object} is compared using the {@code ==} operator.
	 */
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

	/**
	 * {@return the item preceding {@code object} in {@code iterable}}
	 * 
	 * <p>If {@code object} is not in {@code iterable}, this returns the last item of the iterable.
	 * {@code object} is compared using the {@code ==} operator.
	 */
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

	/**
	 * {@return the value supplied from {@code factory}}
	 * 
	 * <p>This is useful when initializing static fields:
	 * <pre>{@code
	 * private static final Map<String, String> MAP = Util.make(() -> {
	 *     Map<String, String> map = new HashMap<>();
	 *     map.put("example", "hello");
	 *     return map;
	 * });
	 * }</pre>
	 */
	public static <T> T make(Supplier<T> factory) {
		return (T)factory.get();
	}

	/**
	 * {@return {@code object} initialized with {@code initializer}}
	 * 
	 * <p>This is useful when initializing static fields:
	 * <pre>{@code
	 * private static final Map<String, String> MAP = Util.make(new HashMap<>(), (map) -> {
	 *     map.put("example", "hello");
	 * });
	 * }</pre>
	 */
	public static <T> T make(T object, Consumer<? super T> initializer) {
		initializer.accept(object);
		return object;
	}

	/**
	 * Combines a list of {@code futures} into one future that holds a list
	 * of their results.
	 * 
	 * <p>This version expects all futures to complete successfully and is not
	 * optimized in case any of the input futures throws.
	 * 
	 * @return the combined future
	 * @see #combine(List)
	 * 
	 * @param futures the completable futures to combine
	 */
	public static <V> CompletableFuture<List<V>> combineSafe(List<? extends CompletableFuture<V>> futures) {
		if (futures.isEmpty()) {
			return CompletableFuture.completedFuture(List.of());
		} else if (futures.size() == 1) {
			return ((CompletableFuture)futures.get(0)).thenApply(List::of);
		} else {
			CompletableFuture<Void> completableFuture = CompletableFuture.allOf((CompletableFuture[])futures.toArray(new CompletableFuture[0]));
			return completableFuture.thenApply(void_ -> futures.stream().map(CompletableFuture::join).toList());
		}
	}

	/**
	 * Combines a list of {@code futures} into one future that holds a list
	 * of their results.
	 * 
	 * <p>The returned future is fail-fast; if any of the input futures fails,
	 * this returned future will be immediately completed exceptionally than
	 * waiting for other input futures.
	 * 
	 * @return the combined future
	 * @see #combineCancellable(List)
	 * @see #combineSafe(List)
	 * 
	 * @param futures the completable futures to combine
	 */
	public static <V> CompletableFuture<List<V>> combine(List<? extends CompletableFuture<? extends V>> futures) {
		CompletableFuture<List<V>> completableFuture = new CompletableFuture();
		return combine(futures, completableFuture::completeExceptionally).applyToEither(completableFuture, Function.identity());
	}

	/**
	 * Combines a list of {@code futures} into one future that holds a list
	 * of their results.
	 * 
	 * <p>The returned future is fail-fast; if any of the input futures fails,
	 * this returned future will be immediately completed exceptionally than
	 * waiting for other input futures. Additionally, all other futures will
	 * be canceled.
	 * 
	 * @return the combined future
	 * @see #combine(List)
	 * @see #combineSafe(List)
	 */
	public static <V> CompletableFuture<List<V>> combineCancellable(List<? extends CompletableFuture<? extends V>> futures) {
		CompletableFuture<List<V>> completableFuture = new CompletableFuture();
		return combine(futures, throwable -> {
			if (completableFuture.completeExceptionally(throwable)) {
				for (CompletableFuture<? extends V> completableFuture2 : futures) {
					completableFuture2.cancel(true);
				}
			}
		}).applyToEither(completableFuture, Function.identity());
	}

	private static <V> CompletableFuture<List<V>> combine(List<? extends CompletableFuture<? extends V>> futures, Consumer<Throwable> exceptionHandler) {
		List<V> list = Lists.<V>newArrayListWithCapacity(futures.size());
		CompletableFuture<?>[] completableFutures = new CompletableFuture[futures.size()];
		futures.forEach(future -> {
			int i = list.size();
			list.add(null);
			completableFutures[i] = future.whenComplete((value, throwable) -> {
				if (throwable != null) {
					exceptionHandler.accept(throwable);
				} else {
					list.set(i, value);
				}
			});
		});
		return CompletableFuture.allOf(completableFutures).thenApply(void_ -> list);
	}

	/**
	 * If {@code optional} has value, calls {@code presentAction} with the value,
	 * otherwise calls {@code elseAction}.
	 * 
	 * @return the passed {@code optional}
	 */
	public static <T> Optional<T> ifPresentOrElse(Optional<T> optional, Consumer<T> presentAction, Runnable elseAction) {
		if (optional.isPresent()) {
			presentAction.accept(optional.get());
		} else {
			elseAction.run();
		}

		return optional;
	}

	public static <T> Supplier<T> debugSupplier(Supplier<T> supplier, Supplier<String> messageSupplier) {
		return supplier;
	}

	public static Runnable debugRunnable(Runnable runnable, Supplier<String> messageSupplier) {
		return runnable;
	}

	/**
	 * Logs an error-level message and pauses the game if
	 * {@link net.minecraft.SharedConstants#isDevelopment SharedConstants.isDevelopment}
	 * is {@code true}.
	 * 
	 * @param message the log message
	 */
	public static void logErrorOrPause(String message) {
		LOGGER.error(message);
		if (SharedConstants.isDevelopment) {
			pause(message);
		}
	}

	/**
	 * Logs an error-level message and pauses the game if
	 * {@link net.minecraft.SharedConstants#isDevelopment SharedConstants.isDevelopment}
	 * is {@code true}.
	 * 
	 * @param throwable a throwable related to the log message
	 * @param message the log message
	 */
	public static void logErrorOrPause(String message, Throwable throwable) {
		LOGGER.error(message, throwable);
		if (SharedConstants.isDevelopment) {
			pause(message);
		}
	}

	/**
	 * Returns the provided fatal throwable, or pauses the game if
	 * {@link net.minecraft.SharedConstants#isDevelopment SharedConstants.isDevelopment}
	 * is {@code true}.
	 * 
	 * <p>This method is usually chained with a {@code throw} statement:
	 * {@snippet :
	 * throw Util.getFatalOrPause(theFatalException);
	 * }
	 * 
	 * @return the provided throwable
	 * 
	 * @param t the throwable
	 */
	public static <T extends Throwable> T getFatalOrPause(T t) {
		if (SharedConstants.isDevelopment) {
			LOGGER.error("Trying to throw a fatal exception, pausing in IDE", t);
			pause(t.getMessage());
		}

		return t;
	}

	public static void setMissingBreakpointHandler(Consumer<String> missingBreakpointHandler) {
		Util.missingBreakpointHandler = missingBreakpointHandler;
	}

	private static void pause(String message) {
		Instant instant = Instant.now();
		LOGGER.warn("Did you remember to set a breakpoint here?");
		boolean bl = Duration.between(instant, Instant.now()).toMillis() > 500L;
		if (!bl) {
			missingBreakpointHandler.accept(message);
		}
	}

	public static String getInnermostMessage(Throwable t) {
		if (t.getCause() != null) {
			return getInnermostMessage(t.getCause());
		} else {
			return t.getMessage() != null ? t.getMessage() : t.toString();
		}
	}

	/**
	 * {@return a random item from {@code array}}
	 * 
	 * @throws IllegalArgumentException if {@code array} is empty
	 */
	public static <T> T getRandom(T[] array, Random random) {
		return array[random.nextInt(array.length)];
	}

	/**
	 * {@return a random integer from {@code array}}
	 * 
	 * @throws IllegalArgumentException if {@code array} is empty
	 */
	public static int getRandom(int[] array, Random random) {
		return array[random.nextInt(array.length)];
	}

	/**
	 * {@return a random item from {@code list}}
	 * 
	 * @throws IllegalArgumentException if {@code list} is empty
	 * 
	 * @see #getRandomOrEmpty
	 */
	public static <T> T getRandom(List<T> list, Random random) {
		return (T)list.get(random.nextInt(list.size()));
	}

	/**
	 * {@return an {@link Optional} of a random item from {@code list}, or an empty optional
	 * if the list is empty}
	 * 
	 * @see #getRandom(List, Random)
	 */
	public static <T> Optional<T> getRandomOrEmpty(List<T> list, Random random) {
		return list.isEmpty() ? Optional.empty() : Optional.of(getRandom(list, random));
	}

	private static BooleanSupplier renameTask(Path src, Path dest) {
		return new BooleanSupplier() {
			public boolean getAsBoolean() {
				try {
					Files.move(src, dest);
					return true;
				} catch (IOException var2) {
					Util.LOGGER.error("Failed to rename", (Throwable)var2);
					return false;
				}
			}

			public String toString() {
				return "rename " + src + " to " + dest;
			}
		};
	}

	private static BooleanSupplier deleteTask(Path path) {
		return new BooleanSupplier() {
			public boolean getAsBoolean() {
				try {
					Files.deleteIfExists(path);
					return true;
				} catch (IOException var2) {
					Util.LOGGER.warn("Failed to delete", (Throwable)var2);
					return false;
				}
			}

			public String toString() {
				return "delete old " + path;
			}
		};
	}

	private static BooleanSupplier deletionVerifyTask(Path path) {
		return new BooleanSupplier() {
			public boolean getAsBoolean() {
				return !Files.exists(path, new LinkOption[0]);
			}

			public String toString() {
				return "verify that " + path + " is deleted";
			}
		};
	}

	private static BooleanSupplier existenceCheckTask(Path path) {
		return new BooleanSupplier() {
			public boolean getAsBoolean() {
				return Files.isRegularFile(path, new LinkOption[0]);
			}

			public String toString() {
				return "verify that " + path + " is present";
			}
		};
	}

	private static boolean attemptTasks(BooleanSupplier... tasks) {
		for (BooleanSupplier booleanSupplier : tasks) {
			if (!booleanSupplier.getAsBoolean()) {
				LOGGER.warn("Failed to execute {}", booleanSupplier);
				return false;
			}
		}

		return true;
	}

	private static boolean attemptTasks(int retries, String taskName, BooleanSupplier... tasks) {
		for (int i = 0; i < retries; i++) {
			if (attemptTasks(tasks)) {
				return true;
			}

			LOGGER.error("Failed to {}, retrying {}/{}", taskName, i, retries);
		}

		LOGGER.error("Failed to {}, aborting, progress might be lost", taskName);
		return false;
	}

	/**
	 * Copies {@code current} to {@code backup} and then replaces {@code current} with {@code newPath}.
	 */
	public static void backupAndReplace(Path current, Path newPath, Path backup) {
		backupAndReplace(current, newPath, backup, false);
	}

	/**
	 * Copies {@code current} to {@code backup} and then replaces {@code current} with {@code newPath}.
	 * 
	 * @param noRestoreOnFail if {@code true}, does not restore the current file when replacing fails
	 */
	public static boolean backupAndReplace(Path current, Path newPath, Path backup, boolean noRestoreOnFail) {
		if (Files.exists(current, new LinkOption[0])
			&& !attemptTasks(10, "create backup " + backup, deleteTask(backup), renameTask(current, backup), existenceCheckTask(backup))) {
			return false;
		} else if (!attemptTasks(10, "remove old " + current, deleteTask(current), deletionVerifyTask(current))) {
			return false;
		} else if (!attemptTasks(10, "replace " + current + " with " + newPath, renameTask(newPath, current), existenceCheckTask(current)) && !noRestoreOnFail) {
			attemptTasks(10, "restore " + current + " from " + backup, renameTask(backup, current), existenceCheckTask(current));
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Moves the {@code cursor} in the {@code string} by a {@code delta} amount.
	 * Skips surrogate characters.
	 */
	public static int moveCursor(String string, int cursor, int delta) {
		int i = string.length();
		if (delta >= 0) {
			for (int j = 0; cursor < i && j < delta; j++) {
				if (Character.isHighSurrogate(string.charAt(cursor++)) && cursor < i && Character.isLowSurrogate(string.charAt(cursor))) {
					cursor++;
				}
			}
		} else {
			for (int jx = delta; cursor > 0 && jx < 0; jx++) {
				cursor--;
				if (Character.isLowSurrogate(string.charAt(cursor)) && cursor > 0 && Character.isHighSurrogate(string.charAt(cursor - 1))) {
					cursor--;
				}
			}
		}

		return cursor;
	}

	/**
	 * {@return a consumer that first prepends {@code prefix} to its input
	 * string and passes the result to {@code consumer}}
	 * 
	 * @apiNote This is useful in codec-based deserialization when passing the
	 * error consumer to some methods, e.g. {@code
	 * Util.addPrefix("Could not parse Example", LOGGER::error)}.
	 */
	public static Consumer<String> addPrefix(String prefix, Consumer<String> consumer) {
		return string -> consumer.accept(prefix + string);
	}

	public static DataResult<int[]> decodeFixedLengthArray(IntStream stream, int length) {
		int[] is = stream.limit((long)(length + 1)).toArray();
		if (is.length != length) {
			Supplier<String> supplier = () -> "Input is not a list of " + length + " ints";
			return is.length >= length ? DataResult.error(supplier, Arrays.copyOf(is, length)) : DataResult.error(supplier);
		} else {
			return DataResult.success(is);
		}
	}

	public static DataResult<long[]> decodeFixedLengthArray(LongStream stream, int length) {
		long[] ls = stream.limit((long)(length + 1)).toArray();
		if (ls.length != length) {
			Supplier<String> supplier = () -> "Input is not a list of " + length + " longs";
			return ls.length >= length ? DataResult.error(supplier, Arrays.copyOf(ls, length)) : DataResult.error(supplier);
		} else {
			return DataResult.success(ls);
		}
	}

	public static <T> DataResult<List<T>> decodeFixedLengthList(List<T> list, int length) {
		if (list.size() != length) {
			Supplier<String> supplier = () -> "Input is not a list of " + length + " elements";
			return list.size() >= length ? DataResult.error(supplier, list.subList(0, length)) : DataResult.error(supplier);
		} else {
			return DataResult.success(list);
		}
	}

	public static void startTimerHack() {
		Thread thread = new Thread("Timer hack thread") {
			public void run() {
				while (true) {
					try {
						Thread.sleep(2147483647L);
					} catch (InterruptedException var2) {
						Util.LOGGER.warn("Timer hack thread interrupted, that really should not happen");
						return;
					}
				}
			}
		};
		thread.setDaemon(true);
		thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
		thread.start();
	}

	/**
	 * Copies a file contained in the folder {@code src} to the folder {@code dest}.
	 * This will replicate any path structure that may exist between {@code src} and {@code toCopy}.
	 */
	public static void relativeCopy(Path src, Path dest, Path toCopy) throws IOException {
		Path path = src.relativize(toCopy);
		Path path2 = dest.resolve(path);
		Files.copy(toCopy, path2);
	}

	public static String replaceInvalidChars(String string, CharPredicate predicate) {
		return (String)string.toLowerCase(Locale.ROOT)
			.chars()
			.mapToObj(charCode -> predicate.test((char)charCode) ? Character.toString((char)charCode) : "_")
			.collect(Collectors.joining());
	}

	public static <K, V> CachedMapper<K, V> cachedMapper(Function<K, V> mapper) {
		return new CachedMapper<>(mapper);
	}

	public static <T, R> Function<T, R> memoize(Function<T, R> function) {
		return new Function<T, R>() {
			private final Map<T, R> cache = new ConcurrentHashMap();

			public R apply(T object) {
				return (R)this.cache.computeIfAbsent(object, function);
			}

			public String toString() {
				return "memoize/1[function=" + function + ", size=" + this.cache.size() + "]";
			}
		};
	}

	public static <T, U, R> BiFunction<T, U, R> memoize(BiFunction<T, U, R> biFunction) {
		return new BiFunction<T, U, R>() {
			private final Map<com.mojang.datafixers.util.Pair<T, U>, R> cache = new ConcurrentHashMap();

			public R apply(T a, U b) {
				return (R)this.cache.computeIfAbsent(com.mojang.datafixers.util.Pair.of(a, b), pair -> biFunction.apply(pair.getFirst(), pair.getSecond()));
			}

			public String toString() {
				return "memoize/2[function=" + biFunction + ", size=" + this.cache.size() + "]";
			}
		};
	}

	/**
	 * {@return the contents of {@code stream} copied to a list and then shuffled}
	 */
	public static <T> List<T> copyShuffled(Stream<T> stream, Random random) {
		ObjectArrayList<T> objectArrayList = (ObjectArrayList<T>)stream.collect(ObjectArrayList.toList());
		shuffle(objectArrayList, random);
		return objectArrayList;
	}

	/**
	 * {@return the contents of {@code stream} copied to a list and then shuffled}
	 */
	public static IntArrayList shuffle(IntStream stream, Random random) {
		IntArrayList intArrayList = IntArrayList.wrap(stream.toArray());
		int i = intArrayList.size();

		for (int j = i; j > 1; j--) {
			int k = random.nextInt(j);
			intArrayList.set(j - 1, intArrayList.set(k, intArrayList.getInt(j - 1)));
		}

		return intArrayList;
	}

	/**
	 * {@return the contents of {@code array} copied to a list and then shuffled}
	 */
	public static <T> List<T> copyShuffled(T[] array, Random random) {
		ObjectArrayList<T> objectArrayList = new ObjectArrayList<>(array);
		shuffle(objectArrayList, random);
		return objectArrayList;
	}

	/**
	 * {@return the contents of {@code stream} copied to a list and then shuffled}
	 */
	public static <T> List<T> copyShuffled(ObjectArrayList<T> list, Random random) {
		ObjectArrayList<T> objectArrayList = new ObjectArrayList<>(list);
		shuffle(objectArrayList, random);
		return objectArrayList;
	}

	/**
	 * Shuffles {@code list}, modifying the passed list in place.
	 */
	public static <T> void shuffle(List<T> list, Random random) {
		int i = list.size();

		for (int j = i; j > 1; j--) {
			int k = random.nextInt(j);
			list.set(j - 1, list.set(k, list.get(j - 1)));
		}
	}

	/**
	 * Runs tasks using the prepare-apply model, such as creation of a {@link
	 * net.minecraft.server.SaveLoader}.
	 * 
	 * @apiNote This method takes a function that supplies an executor to use in the
	 * apply stage. Inside the function, callers should run the preparation,
	 * and use the passed executor for applying.
	 * 
	 * @param resultFactory a function that takes the apply-stage executor and returns the future
	 */
	public static <T> CompletableFuture<T> waitAndApply(Function<Executor, CompletableFuture<T>> resultFactory) {
		return waitAndApply(resultFactory, CompletableFuture::isDone);
	}

	/**
	 * Runs tasks using the prepare-apply model.
	 * 
	 * @apiNote This method takes a function that supplies an executor to use in the
	 * apply stage. Inside the function, callers should run the preparation,
	 * and use the passed executor for applying.
	 * 
	 * @param donePredicate a predicate that, given the result, checks whether applying has finished
	 * @param resultFactory a function that takes the apply-stage executor and returns the preliminary result
	 */
	public static <T> T waitAndApply(Function<Executor, T> resultFactory, Predicate<T> donePredicate) {
		BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue();
		T object = (T)resultFactory.apply(blockingQueue::add);

		while (!donePredicate.test(object)) {
			try {
				Runnable runnable = (Runnable)blockingQueue.poll(100L, TimeUnit.MILLISECONDS);
				if (runnable != null) {
					runnable.run();
				}
			} catch (InterruptedException var5) {
				LOGGER.warn("Interrupted wait");
				break;
			}
		}

		int i = blockingQueue.size();
		if (i > 0) {
			LOGGER.warn("Tasks left in queue: {}", i);
		}

		return object;
	}

	/**
	 * {@return a function that, when given a value in {@code values}, returns the last
	 * index of the value in the list}
	 * 
	 * @implNote Unlike {@link java.util.List#lastIndexOf}, the returned function will
	 * return {@code 0} when given values not in the passed list.
	 * 
	 * @see #lastIdentityIndexGetter
	 */
	public static <T> ToIntFunction<T> lastIndexGetter(List<T> values) {
		int i = values.size();
		if (i < 8) {
			return values::indexOf;
		} else {
			Object2IntMap<T> object2IntMap = new Object2IntOpenHashMap<>(i);
			object2IntMap.defaultReturnValue(-1);

			for (int j = 0; j < i; j++) {
				object2IntMap.put((T)values.get(j), j);
			}

			return object2IntMap;
		}
	}

	/**
	 * {@return a function that, when given a value in {@code values}, returns the last
	 * index of the value in the list using identity comparison}
	 * 
	 * @implNote Unlike {@link java.util.List#lastIndexOf}, the returned function will
	 * return {@code 0} when given values not in the passed list.
	 * 
	 * @see #lastIndexGetter
	 */
	public static <T> ToIntFunction<T> lastIdentityIndexGetter(List<T> values) {
		int i = values.size();
		if (i < 8) {
			ReferenceList<T> referenceList = new ReferenceImmutableList<>(values);
			return referenceList::indexOf;
		} else {
			Reference2IntMap<T> reference2IntMap = new Reference2IntOpenHashMap<>(i);
			reference2IntMap.defaultReturnValue(-1);

			for (int j = 0; j < i; j++) {
				reference2IntMap.put((T)values.get(j), j);
			}

			return reference2IntMap;
		}
	}

	public static <A, B> Typed<B> apply(Typed<A> typed, Type<B> type, UnaryOperator<Dynamic<?>> modifier) {
		Dynamic<?> dynamic = (Dynamic<?>)typed.write().getOrThrow();
		return readTyped(type, (Dynamic<?>)modifier.apply(dynamic), true);
	}

	public static <T> Typed<T> readTyped(Type<T> type, Dynamic<?> value) {
		return readTyped(type, value, false);
	}

	public static <T> Typed<T> readTyped(Type<T> type, Dynamic<?> value, boolean allowPartial) {
		DataResult<Typed<T>> dataResult = type.readTyped(value).map(com.mojang.datafixers.util.Pair::getFirst);

		try {
			return allowPartial ? dataResult.getPartialOrThrow(IllegalStateException::new) : dataResult.getOrThrow(IllegalStateException::new);
		} catch (IllegalStateException var7) {
			CrashReport crashReport = CrashReport.create(var7, "Reading type");
			CrashReportSection crashReportSection = crashReport.addElement("Info");
			crashReportSection.add("Data", value);
			crashReportSection.add("Type", type);
			throw new CrashException(crashReport);
		}
	}

	public static <T> List<T> withAppended(List<T> list, T valueToAppend) {
		return ImmutableList.<T>builderWithExpectedSize(list.size() + 1).addAll(list).add(valueToAppend).build();
	}

	public static <T> List<T> withPrepended(T valueToPrepend, List<T> list) {
		return ImmutableList.<T>builderWithExpectedSize(list.size() + 1).add(valueToPrepend).addAll(list).build();
	}

	public static <K, V> Map<K, V> mapWith(Map<K, V> map, K keyToAppend, V valueToAppend) {
		return ImmutableMap.<K, V>builderWithExpectedSize(map.size() + 1).putAll(map).put(keyToAppend, valueToAppend).buildKeepingLast();
	}

	/**
	 * An enum representing the operating system of the current platform.
	 * This defines the behavior for opening links.
	 * The current one can be obtained via {@link Util#getOperatingSystem}.
	 */
	public static enum OperatingSystem {
		LINUX("linux"),
		SOLARIS("solaris"),
		WINDOWS("windows") {
			@Override
			protected String[] getURIOpenCommand(URI uri) {
				return new String[]{"rundll32", "url.dll,FileProtocolHandler", uri.toString()};
			}
		},
		OSX("mac") {
			@Override
			protected String[] getURIOpenCommand(URI uri) {
				return new String[]{"open", uri.toString()};
			}
		},
		UNKNOWN("unknown");

		private final String name;

		OperatingSystem(final String name) {
			this.name = name;
		}

		/**
		 * Opens {@code uri}. If this points to an HTTP(S) URI, it is usually opened using
		 * the system's default browser. Otherwise, it is opened directly.
		 * 
		 * <p><strong>Always validate the passed URI's schema</strong> as some values can
		 * execute code.
		 */
		public void open(URI uri) {
			try {
				Process process = (Process)AccessController.doPrivileged(() -> Runtime.getRuntime().exec(this.getURIOpenCommand(uri)));
				process.getInputStream().close();
				process.getErrorStream().close();
				process.getOutputStream().close();
			} catch (IOException | PrivilegedActionException var3) {
				Util.LOGGER.error("Couldn't open location '{}'", uri, var3);
			}
		}

		/**
		 * Opens {@code file}.
		 * 
		 * <p><strong>Do not pass untrusted file to this method</strong> as some values can
		 * execute code.
		 */
		public void open(File file) {
			this.open(file.toURI());
		}

		public void open(Path path) {
			this.open(path.toUri());
		}

		protected String[] getURIOpenCommand(URI uri) {
			String string = uri.toString();
			if ("file".equals(uri.getScheme())) {
				string = string.replace("file:", "file://");
			}

			return new String[]{"xdg-open", string};
		}

		/**
		 * Opens {@code uri}. If this points to an HTTP(S) URI, it is usually opened using
		 * the system's default browser. Otherwise, it is opened directly.
		 * 
		 * <p><strong>Always validate the passed URI's schema</strong> as some values can
		 * execute code.
		 */
		public void open(String uri) {
			try {
				this.open(new URI(uri));
			} catch (IllegalArgumentException | URISyntaxException var3) {
				Util.LOGGER.error("Couldn't open uri '{}'", uri, var3);
			}
		}

		public String getName() {
			return this.name;
		}
	}
}
