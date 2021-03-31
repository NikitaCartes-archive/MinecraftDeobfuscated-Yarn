/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.Hash;
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
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.client.util.CharPredicate;
import net.minecraft.datafixer.Schemas;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class Util {
    private static final AtomicInteger NEXT_WORKER_ID = new AtomicInteger(1);
    private static final ExecutorService BOOTSTRAP_EXECUTOR = Util.createWorker("Bootstrap");
    private static final ExecutorService MAIN_WORKER_EXECUTOR = Util.createWorker("Main");
    private static final ExecutorService IO_WORKER_EXECUTOR = Util.createIoWorker();
    public static LongSupplier nanoTimeSupplier = System::nanoTime;
    public static final UUID NIL_UUID = new UUID(0L, 0L);
    private static final Logger LOGGER = LogManager.getLogger();

    public static <K, V> Collector<Map.Entry<? extends K, ? extends V>, ?, Map<K, V>> toMap() {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    public static <T extends Comparable<T>> String getValueAsString(Property<T> property, Object value) {
        return property.name((Comparable)value);
    }

    public static String createTranslationKey(String type, @Nullable Identifier id) {
        if (id == null) {
            return type + ".unregistered_sadface";
        }
        return type + '.' + id.getNamespace() + '.' + id.getPath().replace('/', '.');
    }

    public static long getMeasuringTimeMs() {
        return Util.getMeasuringTimeNano() / 1000000L;
    }

    public static long getMeasuringTimeNano() {
        return nanoTimeSupplier.getAsLong();
    }

    public static long getEpochTimeMs() {
        return Instant.now().toEpochMilli();
    }

    private static ExecutorService createWorker(String name) {
        int i = MathHelper.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, 7);
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

    public static Executor getBootstrapExecutor() {
        return BOOTSTRAP_EXECUTOR;
    }

    public static Executor getMainWorkerExecutor() {
        return MAIN_WORKER_EXECUTOR;
    }

    public static Executor getIoWorkerExecutor() {
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

    public static <T> CompletableFuture<T> completeExceptionally(Throwable throwable) {
        CompletableFuture completableFuture = new CompletableFuture();
        completableFuture.completeExceptionally(throwable);
        return completableFuture;
    }

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
        LOGGER.error(String.format("Caught exception in thread %s", thread), t);
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
                type = Schemas.getFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion())).getChoiceType(typeReference, id);
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

    public static Stream<String> getJVMFlags() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return runtimeMXBean.getInputArguments().stream().filter(runtimeArg -> runtimeArg.startsWith("-X"));
    }

    public static <T> T getLast(List<T> list) {
        return list.get(list.size() - 1);
    }

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

    public static <T> T make(Supplier<T> factory) {
        return factory.get();
    }

    public static <T> T make(T object, Consumer<T> initializer) {
        initializer.accept(object);
        return object;
    }

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
    public static <V> CompletableFuture<List<V>> combineSafe(List<? extends CompletableFuture<? extends V>> futures) {
        return futures.stream().reduce(CompletableFuture.completedFuture(Lists.newArrayList()), (collected, each) -> each.thenCombine((CompletionStage)collected, (eachResult, collectedResults) -> {
            ArrayList<Object> list = Lists.newArrayListWithCapacity(collectedResults.size() + 1);
            list.addAll((Collection<Object>)collectedResults);
            list.add(eachResult);
            return list;
        }), (firstCollected, secondCollected) -> firstCollected.thenCombine((CompletionStage)secondCollected, (firstResults, secondResults) -> {
            ArrayList list = Lists.newArrayListWithCapacity(firstResults.size() + secondResults.size());
            list.addAll(firstResults);
            list.addAll(secondResults);
            return list;
        }));
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
     * @see #combineSafe(List)
     * 
     * @param futures the completable futures to combine
     */
    public static <V> CompletableFuture<List<V>> combine(List<? extends CompletableFuture<? extends V>> futures) {
        ArrayList list = Lists.newArrayListWithCapacity(futures.size());
        CompletableFuture[] completableFutures = new CompletableFuture[futures.size()];
        CompletableFuture completableFuture = new CompletableFuture();
        futures.forEach(future -> {
            int i = list.size();
            list.add(null);
            completableFutures[i] = future.whenComplete((object, throwable) -> {
                if (throwable != null) {
                    completableFuture.completeExceptionally((Throwable)throwable);
                } else {
                    list.set(i, object);
                }
            });
        });
        return CompletableFuture.allOf(completableFutures).applyToEither((CompletionStage)completableFuture, void_ -> list);
    }

    public static <T> Stream<T> stream(Optional<? extends T> optional) {
        return DataFixUtils.orElseGet(optional.map(Stream::of), Stream::empty);
    }

    public static <T> Optional<T> ifPresentOrElse(Optional<T> optional, Consumer<T> presentAction, Runnable elseAction) {
        if (optional.isPresent()) {
            presentAction.accept(optional.get());
        } else {
            elseAction.run();
        }
        return optional;
    }

    public static Runnable debugRunnable(Runnable runnable, Supplier<String> messageSupplier) {
        return runnable;
    }

    public static final void error(String message) {
        LOGGER.error(message);
        if (SharedConstants.isDevelopment) {
            Util.pause();
        }
    }

    public static <T extends Throwable> T throwOrPause(T t) {
        if (SharedConstants.isDevelopment) {
            LOGGER.error("Trying to throw a fatal exception, pausing in IDE", t);
            Util.pause();
        }
        return t;
    }

    private static void pause() {
        try {
            while (true) {
                Thread.sleep(1000L);
                LOGGER.error("paused");
            }
        } catch (InterruptedException interruptedException) {
            return;
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

    public static <T> T getRandom(T[] array, Random random) {
        return array[random.nextInt(array.length)];
    }

    public static int getRandom(int[] array, Random random) {
        return array[random.nextInt(array.length)];
    }

    public static <T> T getRandom(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }

    private static BooleanSupplier renameTask(final Path src, final Path dest) {
        return new BooleanSupplier(){

            @Override
            public boolean getAsBoolean() {
                try {
                    Files.move(src, dest, new CopyOption[0]);
                    return true;
                } catch (IOException iOException) {
                    LOGGER.error("Failed to rename", (Throwable)iOException);
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
                    LOGGER.warn("Failed to delete", (Throwable)iOException);
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
            LOGGER.error("Failed to {}, retrying {}/{}", (Object)taskName, (Object)i, (Object)retries);
        }
        LOGGER.error("Failed to {}, aborting, progress might be lost", (Object)taskName);
        return false;
    }

    public static void backupAndReplace(File current, File newFile, File backup) {
        Util.backupAndReplace(current.toPath(), newFile.toPath(), backup.toPath());
    }

    /**
     * Copies {@code current} to {@code backup} and then replaces {@code current} with {@code newPath}
     */
    public static void backupAndReplace(Path current, Path newPath, Path backup) {
        int i = 10;
        if (Files.exists(current, new LinkOption[0]) && !Util.attemptTasks(10, "create backup " + backup, Util.deleteTask(backup), Util.renameTask(current, backup), Util.existenceCheckTask(backup))) {
            return;
        }
        if (!Util.attemptTasks(10, "remove old " + current, Util.deleteTask(current), Util.deletionVerifyTask(current))) {
            return;
        }
        if (!Util.attemptTasks(10, "replace " + current + " with " + newPath, Util.renameTask(newPath, current), Util.existenceCheckTask(current))) {
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

    public static Consumer<String> addPrefix(String prefix, Consumer<String> consumer) {
        return string -> consumer.accept(prefix + string);
    }

    public static DataResult<int[]> toArray(IntStream stream, int length) {
        int[] is = stream.limit(length + 1).toArray();
        if (is.length != length) {
            String string = "Input is not a list of " + length + " ints";
            if (is.length >= length) {
                return DataResult.error(string, Arrays.copyOf(is, length));
            }
            return DataResult.error(string);
        }
        return DataResult.success(is);
    }

    public static <T> DataResult<List<T>> toArray(List<T> list, int length) {
        if (list.size() != length) {
            String string = "Input is not a list of " + length + " elements";
            if (list.size() >= length) {
                return DataResult.error(string, list.subList(0, length));
            }
            return DataResult.error(string);
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

    public static <T, R> Function<T, R> memoize(final Function<T, R> function) {
        return new Function<T, R>(){
            private final Map<T, R> cache = Maps.newHashMap();

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
            private final Map<Pair<T, U>, R> cache = Maps.newHashMap();

            @Override
            public R apply(T object, U object2) {
                return this.cache.computeIfAbsent(Pair.of(object, object2), pair -> biFunction.apply(pair.getFirst(), pair.getSecond()));
            }

            public String toString() {
                return "memoize/2[function=" + biFunction + ", size=" + this.cache.size() + "]";
            }
        };
    }

    static enum IdentityHashStrategy implements Hash.Strategy<Object>
    {
        INSTANCE;


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
        LINUX,
        SOLARIS,
        WINDOWS{

            @Override
            protected String[] getURLOpenCommand(URL url) {
                return new String[]{"rundll32", "url.dll,FileProtocolHandler", url.toString()};
            }
        }
        ,
        OSX{

            @Override
            protected String[] getURLOpenCommand(URL url) {
                return new String[]{"open", url.toString()};
            }
        }
        ,
        UNKNOWN;


        public void open(URL url) {
            try {
                Process process = AccessController.doPrivileged(() -> Runtime.getRuntime().exec(this.getURLOpenCommand(url)));
                for (String string : IOUtils.readLines(process.getErrorStream())) {
                    LOGGER.error(string);
                }
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
    }
}

