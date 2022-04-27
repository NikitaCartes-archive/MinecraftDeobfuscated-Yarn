package net.minecraft.util;

import com.google.common.base.Ticker;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
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
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.client.util.CharPredicate;
import net.minecraft.datafixer.Schemas;
import net.minecraft.state.property.Property;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class Util {
	static final Logger LOGGER = LogUtils.getLogger();
	private static final int MAX_PARALLELISM = 255;
	private static final String MAX_BG_THREADS_PROPERTY = "max.bg.threads";
	private static final AtomicInteger NEXT_WORKER_ID = new AtomicInteger(1);
	private static final ExecutorService BOOTSTRAP_EXECUTOR = createWorker("Bootstrap");
	private static final ExecutorService MAIN_WORKER_EXECUTOR = createWorker("Main");
	private static final ExecutorService IO_WORKER_EXECUTOR = createIoWorker();
	public static LongSupplier nanoTimeSupplier = System::nanoTime;
	public static final Ticker TICKER = new Ticker() {
		@Override
		public long read() {
			return Util.nanoTimeSupplier.getAsLong();
		}
	};
	public static final UUID NIL_UUID = new UUID(0L, 0L);
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

	public static <T extends Comparable<T>> String getValueAsString(Property<T> property, Object value) {
		return property.name((T)value);
	}

	public static String createTranslationKey(String type, @Nullable Identifier id) {
		return id == null ? type + ".unregistered_sadface" : type + "." + id.getNamespace() + "." + id.getPath().replace('/', '.');
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

	private static ExecutorService createWorker(String name) {
		int i = MathHelper.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, getMaxBackgroundThreads());
		ExecutorService executorService;
		if (i <= 0) {
			executorService = MoreExecutors.newDirectExecutorService();
		} else {
			executorService = new ForkJoinPool(i, forkJoinPool -> {
				ForkJoinWorkerThread forkJoinWorkerThread = new ForkJoinWorkerThread(forkJoinPool) {
					protected void onTermination(Throwable throwable) {
						if (throwable != null) {
							Util.LOGGER.warn("{} died", this.getName(), throwable);
						} else {
							Util.LOGGER.debug("{} shutdown", this.getName());
						}

						super.onTermination(throwable);
					}
				};
				forkJoinWorkerThread.setName("Worker-" + name + "-" + NEXT_WORKER_ID.getAndIncrement());
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

	public static ExecutorService getBootstrapExecutor() {
		return BOOTSTRAP_EXECUTOR;
	}

	public static ExecutorService getMainWorkerExecutor() {
		return MAIN_WORKER_EXECUTOR;
	}

	public static ExecutorService getIoWorkerExecutor() {
		return IO_WORKER_EXECUTOR;
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

	private static ExecutorService createIoWorker() {
		return Executors.newCachedThreadPool(runnable -> {
			Thread thread = new Thread(runnable);
			thread.setName("IO-Worker-" + NEXT_WORKER_ID.getAndIncrement());
			thread.setUncaughtExceptionHandler(Util::uncaughtExceptionHandler);
			return thread;
		});
	}

	public static <T> CompletableFuture<T> completeExceptionally(Throwable throwable) {
		CompletableFuture<T> completableFuture = new CompletableFuture();
		completableFuture.completeExceptionally(throwable);
		return completableFuture;
	}

	public static void throwUnchecked(Throwable t) {
		throw t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
	}

	private static void uncaughtExceptionHandler(Thread thread, Throwable t) {
		throwOrPause(t);
		if (t instanceof CompletionException) {
			t = t.getCause();
		}

		if (t instanceof CrashException) {
			Bootstrap.println(((CrashException)t).getReport().asString());
			System.exit(-1);
		}

		LOGGER.error(String.format("Caught exception in thread %s", thread), t);
	}

	@Nullable
	public static Type<?> getChoiceType(TypeReference typeReference, String id) {
		return !SharedConstants.useChoiceTypeRegistrations ? null : getChoiceTypeInternal(typeReference, id);
	}

	@Nullable
	private static Type<?> getChoiceTypeInternal(TypeReference typeReference, String id) {
		Type<?> type = null;

		try {
			type = Schemas.getFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion())).getChoiceType(typeReference, id);
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

	public static Stream<String> getJVMFlags() {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		return runtimeMXBean.getInputArguments().stream().filter(runtimeArg -> runtimeArg.startsWith("-X"));
	}

	public static <T> T getLast(List<T> list) {
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

	public static <T> T make(Supplier<T> factory) {
		return (T)factory.get();
	}

	public static <T> T make(T object, Consumer<T> initializer) {
		initializer.accept(object);
		return object;
	}

	public static <K> Strategy<K> identityHashStrategy() {
		return Util.IdentityHashStrategy.INSTANCE;
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
			for (CompletableFuture<? extends V> completableFuture2 : futures) {
				completableFuture2.cancel(true);
			}

			completableFuture.completeExceptionally(throwable);
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

	public static void error(String message) {
		LOGGER.error(message);
		if (SharedConstants.isDevelopment) {
			pause(message);
		}
	}

	public static void error(String message, Throwable throwable) {
		LOGGER.error(message, throwable);
		if (SharedConstants.isDevelopment) {
			pause(message);
		}
	}

	public static <T extends Throwable> T throwOrPause(T t) {
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

	public static <T> T getRandom(T[] array, AbstractRandom random) {
		return array[random.nextInt(array.length)];
	}

	public static int getRandom(int[] array, AbstractRandom random) {
		return array[random.nextInt(array.length)];
	}

	public static <T> T getRandom(List<T> list, AbstractRandom random) {
		return (T)list.get(random.nextInt(list.size()));
	}

	public static <T> Optional<T> getRandomOrEmpty(List<T> list, AbstractRandom random) {
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

	public static void backupAndReplace(File current, File newFile, File backup) {
		backupAndReplace(current.toPath(), newFile.toPath(), backup.toPath());
	}

	/**
	 * Copies {@code current} to {@code backup} and then replaces {@code current} with {@code newPath}
	 */
	public static void backupAndReplace(Path current, Path newPath, Path backup) {
		backupAndReplace(current, newPath, backup, false);
	}

	public static void backupAndReplace(File current, File newPath, File backup, boolean noRestoreOnFail) {
		backupAndReplace(current.toPath(), newPath.toPath(), backup.toPath(), noRestoreOnFail);
	}

	public static void backupAndReplace(Path current, Path newPath, Path backup, boolean noRestoreOnFail) {
		int i = 10;
		if (!Files.exists(current, new LinkOption[0])
			|| attemptTasks(10, "create backup " + backup, deleteTask(backup), renameTask(current, backup), existenceCheckTask(backup))) {
			if (attemptTasks(10, "remove old " + current, deleteTask(current), deletionVerifyTask(current))) {
				if (!attemptTasks(10, "replace " + current + " with " + newPath, renameTask(newPath, current), existenceCheckTask(current)) && !noRestoreOnFail) {
					attemptTasks(10, "restore " + current + " from " + backup, renameTask(backup, current), existenceCheckTask(current));
				}
			}
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

	public static Consumer<String> addPrefix(String prefix, Consumer<String> consumer) {
		return string -> consumer.accept(prefix + string);
	}

	public static DataResult<int[]> toArray(IntStream stream, int length) {
		int[] is = stream.limit((long)(length + 1)).toArray();
		if (is.length != length) {
			String string = "Input is not a list of " + length + " ints";
			return is.length >= length ? DataResult.error(string, Arrays.copyOf(is, length)) : DataResult.error(string);
		} else {
			return DataResult.success(is);
		}
	}

	public static <T> DataResult<List<T>> toArray(List<T> list, int length) {
		if (list.size() != length) {
			String string = "Input is not a list of " + length + " elements";
			return list.size() >= length ? DataResult.error(string, list.subList(0, length)) : DataResult.error(string);
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

	public static <T, R> Function<T, R> memoize(Function<T, R> function) {
		return new Function<T, R>() {
			private final Map<T, R> cache = Maps.<T, R>newHashMap();

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
			private final Map<com.mojang.datafixers.util.Pair<T, U>, R> cache = Maps.<com.mojang.datafixers.util.Pair<T, U>, R>newHashMap();

			public R apply(T a, U b) {
				return (R)this.cache.computeIfAbsent(com.mojang.datafixers.util.Pair.of(a, b), pair -> biFunction.apply(pair.getFirst(), pair.getSecond()));
			}

			public String toString() {
				return "memoize/2[function=" + biFunction + ", size=" + this.cache.size() + "]";
			}
		};
	}

	public static <T> List<T> copyShuffled(Stream<T> stream, AbstractRandom random) {
		ObjectArrayList<T> objectArrayList = (ObjectArrayList<T>)stream.collect(ObjectArrayList.toList());
		shuffle(objectArrayList, random);
		return objectArrayList;
	}

	public static IntArrayList shuffle(IntStream stream, AbstractRandom random) {
		IntArrayList intArrayList = IntArrayList.wrap(stream.toArray());
		int i = intArrayList.size();

		for (int j = i; j > 1; j--) {
			int k = random.nextInt(j);
			intArrayList.set(j - 1, intArrayList.set(k, intArrayList.getInt(j - 1)));
		}

		return intArrayList;
	}

	public static <T> List<T> copyShuffled(T[] array, AbstractRandom random) {
		ObjectArrayList<T> objectArrayList = new ObjectArrayList<>(array);
		shuffle(objectArrayList, random);
		return objectArrayList;
	}

	public static <T> List<T> copyShuffled(ObjectArrayList<T> list, AbstractRandom random) {
		ObjectArrayList<T> objectArrayList = new ObjectArrayList<>(list);
		shuffle(objectArrayList, random);
		return objectArrayList;
	}

	public static <T> void shuffle(ObjectArrayList<T> list, AbstractRandom random) {
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
	 * @param resultFactory a function that takes the apply-stage executor and returns the preliminary result
	 * @param donePredicate a predicate that, given the result, checks whether applying has finished
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

	static enum IdentityHashStrategy implements Strategy<Object> {
		INSTANCE;

		@Override
		public int hashCode(Object o) {
			return System.identityHashCode(o);
		}

		@Override
		public boolean equals(Object o, Object o2) {
			return o == o2;
		}
	}

	public static enum OperatingSystem {
		LINUX("linux"),
		SOLARIS("solaris"),
		WINDOWS("windows") {
			@Override
			protected String[] getURLOpenCommand(URL url) {
				return new String[]{"rundll32", "url.dll,FileProtocolHandler", url.toString()};
			}
		},
		OSX("mac") {
			@Override
			protected String[] getURLOpenCommand(URL url) {
				return new String[]{"open", url.toString()};
			}
		},
		UNKNOWN("unknown");

		private final String name;

		OperatingSystem(String name) {
			this.name = name;
		}

		public void open(URL url) {
			try {
				Process process = (Process)AccessController.doPrivileged(() -> Runtime.getRuntime().exec(this.getURLOpenCommand(url)));

				for (String string : IOUtils.readLines(process.getErrorStream())) {
					Util.LOGGER.error(string);
				}

				process.getInputStream().close();
				process.getErrorStream().close();
				process.getOutputStream().close();
			} catch (IOException | PrivilegedActionException var5) {
				Util.LOGGER.error("Couldn't open url '{}'", url, var5);
			}
		}

		public void open(URI uri) {
			try {
				this.open(uri.toURL());
			} catch (MalformedURLException var3) {
				Util.LOGGER.error("Couldn't open uri '{}'", uri, var3);
			}
		}

		public void open(File file) {
			try {
				this.open(file.toURI().toURL());
			} catch (MalformedURLException var3) {
				Util.LOGGER.error("Couldn't open file '{}'", file, var3);
			}
		}

		protected String[] getURLOpenCommand(URL url) {
			String string = url.toString();
			if ("file".equals(url.getProtocol())) {
				string = string.replace("file:", "file://");
			}

			return new String[]{"xdg-open", string};
		}

		public void open(String uri) {
			try {
				this.open(new URI(uri).toURL());
			} catch (MalformedURLException | IllegalArgumentException | URISyntaxException var3) {
				Util.LOGGER.error("Couldn't open uri '{}'", uri, var3);
			}
		}

		public String getName() {
			return this.name;
		}
	}
}
