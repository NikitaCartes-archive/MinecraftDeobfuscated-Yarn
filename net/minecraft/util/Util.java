/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.base.Ticker;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.CopyOption;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
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
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.Schemas;
import net.minecraft.state.property.Property;
import net.minecraft.util.CachedMapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.TimeSupplier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.function.CharPredicate;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * A class holding various utility methods.
 */
public class Util {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_PARALLELISM = 255;
    private static final String MAX_BG_THREADS_PROPERTY = "max.bg.threads";
    private static final AtomicInteger NEXT_WORKER_ID = new AtomicInteger(1);
    private static final ExecutorService MAIN_WORKER_EXECUTOR = Util.createWorker("Main");
    private static final ExecutorService IO_WORKER_EXECUTOR = Util.createIoWorker();
    /**
     * A locale-independent datetime formatter that uses {@code yyyy-MM-dd_HH.mm.ss}
     * as the format string. Example: {@code 2022-01-01_00.00.00}
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss", Locale.ROOT);
    public static TimeSupplier.Nanoseconds nanoTimeSupplier = System::nanoTime;
    public static final Ticker TICKER = new Ticker(){

        @Override
        public long read() {
            return nanoTimeSupplier.getAsLong();
        }
    };
    /**
     * The "nil UUID" that represents lack of a UUID.
     */
    public static final UUID NIL_UUID = new UUID(0L, 0L);
    /**
     * The file system provider for handling jar and zip files.
     */
    public static final FileSystemProvider JAR_FILE_SYSTEM_PROVIDER = FileSystemProvider.installedProviders().stream().filter(fileSystemProvider -> fileSystemProvider.getScheme().equalsIgnoreCase("jar")).findFirst().orElseThrow(() -> new IllegalStateException("No jar file system provider found"));
    private static Consumer<String> missingBreakpointHandler = message -> {};

    public static <K, V> Collector<Map.Entry<? extends K, ? extends V>, ?, Map<K, V>> toMap() {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    public static <T extends Comparable<T>> String getValueAsString(Property<T> property, Object value) {
        return property.name((Comparable)value);
    }

    /**
     * {@return the translation key constructed from {@code type} and {@code id}}
     * 
     * <p>If {@code id} is {@code null}, {@code unregistered_sadface} is used as the ID.
     * 
     * @see Identifier#toTranslationKey(String)
     */
    public static String createTranslationKey(String type, @Nullable Identifier id) {
        if (id == null) {
            return type + ".unregistered_sadface";
        }
        return type + "." + id.getNamespace() + "." + id.getPath().replace('/', '.');
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
        return Util.getMeasuringTimeNano() / 1000000L;
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
        int i = MathHelper.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, Util.getMaxBackgroundThreads());
        ExecutorService executorService = i <= 0 ? MoreExecutors.newDirectExecutorService() : new ForkJoinPool(i, forkJoinPool -> {
            ForkJoinWorkerThread forkJoinWorkerThread = new ForkJoinWorkerThread(forkJoinPool){

                @Override
                protected void onTermination(Throwable throwable) {
                    if (throwable != null) {
                        LOGGER.warn("{} died", (Object)this.getName(), (Object)throwable);
                    } else {
                        LOGGER.debug("{} shutdown", (Object)this.getName());
                    }
                    super.onTermination(throwable);
                }
            };
            forkJoinWorkerThread.setName("Worker-" + name + "-" + NEXT_WORKER_ID.getAndIncrement());
            return forkJoinWorkerThread;
        }, Util::uncaughtExceptionHandler, true);
        return executorService;
    }

    private static int getMaxBackgroundThreads() {
        String string = System.getProperty(MAX_BG_THREADS_PROPERTY);
        if (string != null) {
            try {
                int i = Integer.parseInt(string);
                if (i >= 1 && i <= 255) {
                    return i;
                }
                LOGGER.error("Wrong {} property value '{}'. Should be an integer value between 1 and {}.", MAX_BG_THREADS_PROPERTY, string, 255);
            } catch (NumberFormatException numberFormatException) {
                LOGGER.error("Could not parse {} property value '{}'. Should be an integer value between 1 and {}.", MAX_BG_THREADS_PROPERTY, string, 255);
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

    public static void shutdownExecutors() {
        Util.attemptShutdown(MAIN_WORKER_EXECUTOR);
        Util.attemptShutdown(IO_WORKER_EXECUTOR);
    }

    private static void attemptShutdown(ExecutorService service) {
        boolean bl;
        service.shutdown();
        try {
            bl = service.awaitTermination(3L, TimeUnit.SECONDS);
        } catch (InterruptedException interruptedException) {
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
        Util.throwOrPause(t);
        if (t instanceof CompletionException) {
            t = t.getCause();
        }
        if (t instanceof CrashException) {
            Bootstrap.println(((CrashException)t).getReport().asString());
            System.exit(-1);
        }
        LOGGER.error(String.format(Locale.ROOT, "Caught exception in thread %s", thread), t);
    }

    @Nullable
    public static Type<?> getChoiceType(DSL.TypeReference typeReference, String id) {
        if (!SharedConstants.useChoiceTypeRegistrations) {
            return null;
        }
        return Util.getChoiceTypeInternal(typeReference, id);
    }

    @Nullable
    private static Type<?> getChoiceTypeInternal(DSL.TypeReference typeReference, String id) {
        Type<?> type;
        block2: {
            type = null;
            try {
                type = Schemas.getFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getSaveVersion().getId())).getChoiceType(typeReference, id);
            } catch (IllegalArgumentException illegalArgumentException) {
                LOGGER.error("No data fixer registered for {}", (Object)id);
                if (!SharedConstants.isDevelopment) break block2;
                throw illegalArgumentException;
            }
        }
        return type;
    }

    public static Runnable debugRunnable(String activeThreadName, Runnable task) {
        if (SharedConstants.isDevelopment) {
            return () -> {
                Thread thread = Thread.currentThread();
                String string2 = thread.getName();
                thread.setName(activeThreadName);
                try {
                    task.run();
                } finally {
                    thread.setName(string2);
                }
            };
        }
        return task;
    }

    public static <V> Supplier<V> debugSupplier(String activeThreadName, Supplier<V> supplier) {
        if (SharedConstants.isDevelopment) {
            return () -> {
                Thread thread = Thread.currentThread();
                String string2 = thread.getName();
                thread.setName(activeThreadName);
                try {
                    Object t = supplier.get();
                    return t;
                } finally {
                    thread.setName(string2);
                }
            };
        }
        return supplier;
    }

    /**
     * {@return the operating system instance for the current platform}
     * 
     * @implNote This uses the {@code os.name} system property to determine the operating system.
     * @apiNote This is used for opening links.
     */
    public static OperatingSystem getOperatingSystem() {
        String string = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (string.contains("win")) {
            return OperatingSystem.WINDOWS;
        }
        if (string.contains("mac")) {
            return OperatingSystem.OSX;
        }
        if (string.contains("solaris")) {
            return OperatingSystem.SOLARIS;
        }
        if (string.contains("sunos")) {
            return OperatingSystem.SOLARIS;
        }
        if (string.contains("linux")) {
            return OperatingSystem.LINUX;
        }
        if (string.contains("unix")) {
            return OperatingSystem.LINUX;
        }
        return OperatingSystem.UNKNOWN;
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
        return list.get(list.size() - 1);
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
        T object2 = iterator.next();
        if (object != null) {
            T object3 = object2;
            while (true) {
                if (object3 == object) {
                    if (!iterator.hasNext()) break;
                    return iterator.next();
                }
                if (!iterator.hasNext()) continue;
                object3 = iterator.next();
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
            T object3 = iterator.next();
            if (object3 == object) {
                if (object2 != null) break;
                object2 = iterator.hasNext() ? Iterators.getLast(iterator) : object;
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
        return factory.get();
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
    public static <T> T make(T object, Consumer<T> initializer) {
        initializer.accept(object);
        return object;
    }

    /**
     * {@return the {@link Hash.Strategy} that uses identity comparison}
     * 
     * <p>fastutil's "reference" object types should be used instead in most cases.
     */
    public static <K> Hash.Strategy<K> identityHashStrategy() {
        return IdentityHashStrategy.INSTANCE;
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
        }
        if (futures.size() == 1) {
            return futures.get(0).thenApply(List::of);
        }
        CompletableFuture<Void> completableFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        return completableFuture.thenApply(void_ -> futures.stream().map(CompletableFuture::join).toList());
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
        CompletableFuture completableFuture = new CompletableFuture();
        return Util.combine(futures, completableFuture::completeExceptionally).applyToEither((CompletionStage)completableFuture, Function.identity());
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
        CompletableFuture completableFuture = new CompletableFuture();
        return Util.combine(futures, throwable -> {
            if (completableFuture.completeExceptionally((Throwable)throwable)) {
                for (CompletableFuture completableFuture2 : futures) {
                    completableFuture2.cancel(true);
                }
            }
        }).applyToEither((CompletionStage)completableFuture, Function.identity());
    }

    private static <V> CompletableFuture<List<V>> combine(List<? extends CompletableFuture<? extends V>> futures, Consumer<Throwable> exceptionHandler) {
        ArrayList list = Lists.newArrayListWithCapacity(futures.size());
        CompletableFuture[] completableFutures = new CompletableFuture[futures.size()];
        futures.forEach(future -> {
            int i = list.size();
            list.add(null);
            completableFutures[i] = future.whenComplete((value, throwable) -> {
                if (throwable != null) {
                    exceptionHandler.accept((Throwable)throwable);
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

    public static void error(String message) {
        LOGGER.error(message);
        if (SharedConstants.isDevelopment) {
            Util.pause(message);
        }
    }

    public static void error(String message, Throwable throwable) {
        LOGGER.error(message, throwable);
        if (SharedConstants.isDevelopment) {
            Util.pause(message);
        }
    }

    public static <T extends Throwable> T throwOrPause(T t) {
        if (SharedConstants.isDevelopment) {
            LOGGER.error("Trying to throw a fatal exception, pausing in IDE", t);
            Util.pause(t.getMessage());
        }
        return t;
    }

    public static void setMissingBreakpointHandler(Consumer<String> missingBreakpointHandler) {
        Util.missingBreakpointHandler = missingBreakpointHandler;
    }

    private static void pause(String message) {
        boolean bl;
        Instant instant = Instant.now();
        LOGGER.warn("Did you remember to set a breakpoint here?");
        boolean bl2 = bl = Duration.between(instant, Instant.now()).toMillis() > 500L;
        if (!bl) {
            missingBreakpointHandler.accept(message);
        }
    }

    public static String getInnermostMessage(Throwable t) {
        if (t.getCause() != null) {
            return Util.getInnermostMessage(t.getCause());
        }
        if (t.getMessage() != null) {
            return t.getMessage();
        }
        return t.toString();
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
        return list.get(random.nextInt(list.size()));
    }

    /**
     * {@return an {@link Optional} of a random item from {@code list}, or an empty optional
     * if the list is empty}
     * 
     * @see #getRandom(List, Random)
     */
    public static <T> Optional<T> getRandomOrEmpty(List<T> list, Random random) {
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Util.getRandom(list, random));
    }

    private static BooleanSupplier renameTask(final Path src, final Path dest) {
        return new BooleanSupplier(){

            @Override
            public boolean getAsBoolean() {
                try {
                    Files.move(src, dest, new CopyOption[0]);
                    return true;
                } catch (IOException iOException) {
                    LOGGER.error("Failed to rename", iOException);
                    return false;
                }
            }

            public String toString() {
                return "rename " + src + " to " + dest;
            }
        };
    }

    private static BooleanSupplier deleteTask(final Path path) {
        return new BooleanSupplier(){

            @Override
            public boolean getAsBoolean() {
                try {
                    Files.deleteIfExists(path);
                    return true;
                } catch (IOException iOException) {
                    LOGGER.warn("Failed to delete", iOException);
                    return false;
                }
            }

            public String toString() {
                return "delete old " + path;
            }
        };
    }

    private static BooleanSupplier deletionVerifyTask(final Path path) {
        return new BooleanSupplier(){

            @Override
            public boolean getAsBoolean() {
                return !Files.exists(path, new LinkOption[0]);
            }

            public String toString() {
                return "verify that " + path + " is deleted";
            }
        };
    }

    private static BooleanSupplier existenceCheckTask(final Path path) {
        return new BooleanSupplier(){

            @Override
            public boolean getAsBoolean() {
                return Files.isRegularFile(path, new LinkOption[0]);
            }

            public String toString() {
                return "verify that " + path + " is present";
            }
        };
    }

    private static boolean attemptTasks(BooleanSupplier ... tasks) {
        for (BooleanSupplier booleanSupplier : tasks) {
            if (booleanSupplier.getAsBoolean()) continue;
            LOGGER.warn("Failed to execute {}", (Object)booleanSupplier);
            return false;
        }
        return true;
    }

    private static boolean attemptTasks(int retries, String taskName, BooleanSupplier ... tasks) {
        for (int i = 0; i < retries; ++i) {
            if (Util.attemptTasks(tasks)) {
                return true;
            }
            LOGGER.error("Failed to {}, retrying {}/{}", taskName, i, retries);
        }
        LOGGER.error("Failed to {}, aborting, progress might be lost", (Object)taskName);
        return false;
    }

    /**
     * Copies {@code current} to {@code backup} and then replaces {@code current} with {@code newPath}.
     */
    public static void backupAndReplace(File current, File newFile, File backup) {
        Util.backupAndReplace(current.toPath(), newFile.toPath(), backup.toPath());
    }

    /**
     * Copies {@code current} to {@code backup} and then replaces {@code current} with {@code newPath}.
     */
    public static void backupAndReplace(Path current, Path newPath, Path backup) {
        Util.backupAndReplace(current, newPath, backup, false);
    }

    /**
     * Copies {@code current} to {@code backup} and then replaces {@code current} with {@code newPath}.
     * 
     * @param noRestoreOnFail if {@code true}, does not restore the current file when replacing fails
     */
    public static void backupAndReplace(File current, File newPath, File backup, boolean noRestoreOnFail) {
        Util.backupAndReplace(current.toPath(), newPath.toPath(), backup.toPath(), noRestoreOnFail);
    }

    /**
     * Copies {@code current} to {@code backup} and then replaces {@code current} with {@code newPath}.
     * 
     * @param noRestoreOnFail if {@code true}, does not restore the current file when replacing fails
     */
    public static void backupAndReplace(Path current, Path newPath, Path backup, boolean noRestoreOnFail) {
        int i = 10;
        if (Files.exists(current, new LinkOption[0]) && !Util.attemptTasks(10, "create backup " + backup, Util.deleteTask(backup), Util.renameTask(current, backup), Util.existenceCheckTask(backup))) {
            return;
        }
        if (!Util.attemptTasks(10, "remove old " + current, Util.deleteTask(current), Util.deletionVerifyTask(current))) {
            return;
        }
        if (!Util.attemptTasks(10, "replace " + current + " with " + newPath, Util.renameTask(newPath, current), Util.existenceCheckTask(current)) && !noRestoreOnFail) {
            Util.attemptTasks(10, "restore " + current + " from " + backup, Util.renameTask(backup, current), Util.existenceCheckTask(current));
        }
    }

    /**
     * Moves the {@code cursor} in the {@code string} by a {@code delta} amount.
     * Skips surrogate characters.
     */
    public static int moveCursor(String string, int cursor, int delta) {
        int i = string.length();
        if (delta >= 0) {
            for (int j = 0; cursor < i && j < delta; ++j) {
                if (!Character.isHighSurrogate(string.charAt(cursor++)) || cursor >= i || !Character.isLowSurrogate(string.charAt(cursor))) continue;
                ++cursor;
            }
        } else {
            for (int j = delta; cursor > 0 && j < 0; ++j) {
                if (!Character.isLowSurrogate(string.charAt(--cursor)) || cursor <= 0 || !Character.isHighSurrogate(string.charAt(cursor - 1))) continue;
                --cursor;
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

    public static DataResult<int[]> toArray(IntStream stream, int length) {
        int[] is = stream.limit(length + 1).toArray();
        if (is.length != length) {
            Supplier<String> supplier = () -> "Input is not a list of " + length + " ints";
            if (is.length >= length) {
                return DataResult.error(supplier, Arrays.copyOf(is, length));
            }
            return DataResult.error(supplier);
        }
        return DataResult.success(is);
    }

    public static <T> DataResult<List<T>> toArray(List<T> list, int length) {
        if (list.size() != length) {
            Supplier<String> supplier = () -> "Input is not a list of " + length + " elements";
            if (list.size() >= length) {
                return DataResult.error(supplier, list.subList(0, length));
            }
            return DataResult.error(supplier);
        }
        return DataResult.success(list);
    }

    public static void startTimerHack() {
        Thread thread = new Thread("Timer hack thread"){

            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(Integer.MAX_VALUE);
                    }
                } catch (InterruptedException interruptedException) {
                    LOGGER.warn("Timer hack thread interrupted, that really should not happen");
                    return;
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
        Files.copy(toCopy, path2, new CopyOption[0]);
    }

    public static String replaceInvalidChars(String string, CharPredicate predicate) {
        return string.toLowerCase(Locale.ROOT).chars().mapToObj(charCode -> predicate.test((char)charCode) ? Character.toString((char)charCode) : "_").collect(Collectors.joining());
    }

    public static <K, V> CachedMapper<K, V> cachedMapper(Function<K, V> mapper) {
        return new CachedMapper<K, V>(mapper);
    }

    public static <T, R> Function<T, R> memoize(final Function<T, R> function) {
        return new Function<T, R>(){
            private final Map<T, R> cache = new ConcurrentHashMap();

            @Override
            public R apply(T object) {
                return this.cache.computeIfAbsent(object, function);
            }

            public String toString() {
                return "memoize/1[function=" + function + ", size=" + this.cache.size() + "]";
            }
        };
    }

    public static <T, U, R> BiFunction<T, U, R> memoize(final BiFunction<T, U, R> biFunction) {
        return new BiFunction<T, U, R>(){
            private final Map<Pair<T, U>, R> cache = new ConcurrentHashMap();

            @Override
            public R apply(T a, U b) {
                return this.cache.computeIfAbsent(Pair.of(a, b), pair -> biFunction.apply(pair.getFirst(), pair.getSecond()));
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
        ObjectArrayList objectArrayList = stream.collect(ObjectArrayList.toList());
        Util.shuffle(objectArrayList, random);
        return objectArrayList;
    }

    /**
     * {@return the contents of {@code stream} copied to a list and then shuffled}
     */
    public static IntArrayList shuffle(IntStream stream, Random random) {
        int i;
        IntArrayList intArrayList = IntArrayList.wrap(stream.toArray());
        for (int j = i = intArrayList.size(); j > 1; --j) {
            int k = random.nextInt(j);
            intArrayList.set(j - 1, intArrayList.set(k, intArrayList.getInt(j - 1)));
        }
        return intArrayList;
    }

    /**
     * {@return the contents of {@code array} copied to a list and then shuffled}
     */
    public static <T> List<T> copyShuffled(T[] array, Random random) {
        ObjectArrayList<T> objectArrayList = new ObjectArrayList<T>(array);
        Util.shuffle(objectArrayList, random);
        return objectArrayList;
    }

    /**
     * {@return the contents of {@code stream} copied to a list and then shuffled}
     */
    public static <T> List<T> copyShuffled(ObjectArrayList<T> list, Random random) {
        ObjectArrayList<T> objectArrayList = new ObjectArrayList<T>(list);
        Util.shuffle(objectArrayList, random);
        return objectArrayList;
    }

    /**
     * Shuffles {@code list}, modifying the passed list in place.
     */
    public static <T> void shuffle(ObjectArrayList<T> list, Random random) {
        int i;
        for (int j = i = list.size(); j > 1; --j) {
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
        return Util.waitAndApply(resultFactory, CompletableFuture::isDone);
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
        int i;
        LinkedBlockingQueue blockingQueue = new LinkedBlockingQueue();
        T object = resultFactory.apply(blockingQueue::add);
        while (!donePredicate.test(object)) {
            try {
                Runnable runnable = (Runnable)blockingQueue.poll(100L, TimeUnit.MILLISECONDS);
                if (runnable == null) continue;
                runnable.run();
            } catch (InterruptedException interruptedException) {
                LOGGER.warn("Interrupted wait");
                break;
            }
        }
        if ((i = blockingQueue.size()) > 0) {
            LOGGER.warn("Tasks left in queue: {}", (Object)i);
        }
        return object;
    }

    /**
     * {@return a function that, when given a value in {@code values}, returns the last
     * index of the value in the list}
     * 
     * @implNote Unlike {@link java.util.List#lastIndexOf}, the returned function will
     * return {@code 0} when given values not in the passed list.
     */
    public static <T> ToIntFunction<T> lastIndexGetter(List<T> values) {
        return Util.lastIndexGetter(values, Object2IntOpenHashMap::new);
    }

    /**
     * {@return a function that, when given a value in {@code values}, returns the last
     * index of the value in the list}
     * 
     * @implNote Unlike {@link java.util.List#lastIndexOf}, the returned function will
     * return {@code 0} when given values not in the passed list.
     * 
     * @param mapCreator a function that, when given the size of {@code values},
     * returns a map for storing the indices of the values
     */
    public static <T> ToIntFunction<T> lastIndexGetter(List<T> values, IntFunction<Object2IntMap<T>> mapCreator) {
        Object2IntMap<T> object2IntMap = mapCreator.apply(values.size());
        for (int i = 0; i < values.size(); ++i) {
            object2IntMap.put(values.get(i), i);
        }
        return object2IntMap;
    }

    /**
     * {@return the result wrapped in {@code result}}
     */
    public static <T, E extends Exception> T getResult(DataResult<T> result, Function<String, E> exceptionGetter) throws E {
        Optional<DataResult.PartialResult<T>> optional = result.error();
        if (optional.isPresent()) {
            throw (Exception)exceptionGetter.apply(optional.get().message());
        }
        return result.result().orElseThrow();
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    public static enum OperatingSystem {
        LINUX("linux"),
        SOLARIS("solaris"),
        WINDOWS("windows"){

            @Override
            protected String[] getURLOpenCommand(URL url) {
                return new String[]{"rundll32", "url.dll,FileProtocolHandler", url.toString()};
            }
        }
        ,
        OSX("mac"){

            @Override
            protected String[] getURLOpenCommand(URL url) {
                return new String[]{"open", url.toString()};
            }
        }
        ,
        UNKNOWN("unknown");

        private final String name;

        OperatingSystem(String name) {
            this.name = name;
        }

        public void open(URL url) {
            try {
                Process process = AccessController.doPrivileged(() -> Runtime.getRuntime().exec(this.getURLOpenCommand(url)));
                process.getInputStream().close();
                process.getErrorStream().close();
                process.getOutputStream().close();
            } catch (IOException | PrivilegedActionException exception) {
                LOGGER.error("Couldn't open url '{}'", (Object)url, (Object)exception);
            }
        }

        public void open(URI uri) {
            try {
                this.open(uri.toURL());
            } catch (MalformedURLException malformedURLException) {
                LOGGER.error("Couldn't open uri '{}'", (Object)uri, (Object)malformedURLException);
            }
        }

        public void open(File file) {
            try {
                this.open(file.toURI().toURL());
            } catch (MalformedURLException malformedURLException) {
                LOGGER.error("Couldn't open file '{}'", (Object)file, (Object)malformedURLException);
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
            } catch (IllegalArgumentException | MalformedURLException | URISyntaxException exception) {
                LOGGER.error("Couldn't open uri '{}'", (Object)uri, (Object)exception);
            }
        }

        public String getName() {
            return this.name;
        }
    }

    static enum IdentityHashStrategy implements Hash.Strategy<Object>
    {
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
}

