/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
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
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.class_5462;
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
    private static final AtomicInteger NEXT_SERVER_WORKER_ID = new AtomicInteger(1);
    private static final ExecutorService BOOTSTRAP = Util.method_28122("Bootstrap");
    private static final ExecutorService SERVER_WORKER_EXECUTOR = Util.method_28122("Main");
    private static final ExecutorService field_24477 = Util.method_27959();
    public static LongSupplier nanoTimeSupplier = System::nanoTime;
    public static final UUID NIL_UUID = new UUID(0L, 0L);
    private static final Logger LOGGER = LogManager.getLogger();

    public static <K, V> Collector<Map.Entry<? extends K, ? extends V>, ?, Map<K, V>> toMap() {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    public static <T extends Comparable<T>> String getValueAsString(Property<T> property, Object object) {
        return property.name((Comparable)object);
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

    private static ExecutorService method_28122(String string) {
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
            forkJoinWorkerThread.setName("Worker-" + string + "-" + NEXT_SERVER_WORKER_ID.getAndIncrement());
            return forkJoinWorkerThread;
        }, Util::method_18347, true);
        return executorService;
    }

    public static Executor method_28124() {
        return BOOTSTRAP;
    }

    public static Executor getServerWorkerExecutor() {
        return SERVER_WORKER_EXECUTOR;
    }

    public static Executor method_27958() {
        return field_24477;
    }

    public static void shutdownServerWorkerExecutor() {
        Util.method_27957(SERVER_WORKER_EXECUTOR);
        Util.method_27957(field_24477);
    }

    private static void method_27957(ExecutorService executorService) {
        boolean bl;
        executorService.shutdown();
        try {
            bl = executorService.awaitTermination(3L, TimeUnit.SECONDS);
        } catch (InterruptedException interruptedException) {
            bl = false;
        }
        if (!bl) {
            executorService.shutdownNow();
        }
    }

    private static ExecutorService method_27959() {
        return Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("IO-Worker-" + NEXT_SERVER_WORKER_ID.getAndIncrement());
            thread.setUncaughtExceptionHandler(Util::method_18347);
            return thread;
        });
    }

    @Environment(value=EnvType.CLIENT)
    public static <T> CompletableFuture<T> completeExceptionally(Throwable throwable) {
        CompletableFuture completableFuture = new CompletableFuture();
        completableFuture.completeExceptionally(throwable);
        return completableFuture;
    }

    @Environment(value=EnvType.CLIENT)
    public static void throwUnchecked(Throwable throwable) {
        throw throwable instanceof RuntimeException ? (RuntimeException)throwable : new RuntimeException(throwable);
    }

    private static void method_18347(Thread thread, Throwable throwable) {
        Util.throwOrPause(throwable);
        if (throwable instanceof CompletionException) {
            throwable = throwable.getCause();
        }
        if (throwable instanceof CrashException) {
            Bootstrap.println(((CrashException)throwable).getReport().asString());
            System.exit(-1);
        }
        LOGGER.error(String.format("Caught exception in thread %s", thread), throwable);
    }

    @Nullable
    public static Type<?> method_29187(DSL.TypeReference typeReference, String string) {
        if (!SharedConstants.field_25135) {
            return null;
        }
        return Util.method_29191(typeReference, string);
    }

    @Nullable
    private static Type<?> method_29191(DSL.TypeReference typeReference, String string) {
        Type<?> type;
        block2: {
            type = null;
            try {
                type = Schemas.getFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion())).getChoiceType(typeReference, string);
            } catch (IllegalArgumentException illegalArgumentException) {
                LOGGER.error("No data fixer registered for {}", (Object)string);
                if (!SharedConstants.isDevelopment) break block2;
                throw illegalArgumentException;
            }
        }
        return type;
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
        return runtimeMXBean.getInputArguments().stream().filter(string -> string.startsWith("-X"));
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

    public static <V> CompletableFuture<List<V>> combine(List<? extends CompletableFuture<? extends V>> futures) {
        ArrayList list = Lists.newArrayListWithCapacity(futures.size());
        CompletableFuture[] completableFutures = new CompletableFuture[futures.size()];
        CompletableFuture completableFuture = new CompletableFuture();
        futures.forEach(completableFuture2 -> {
            int i = list.size();
            list.add(null);
            completableFutures[i] = completableFuture2.whenComplete((object, throwable) -> {
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

    public static <T> Optional<T> ifPresentOrElse(Optional<T> optional, Consumer<T> consumer, Runnable runnable) {
        if (optional.isPresent()) {
            consumer.accept(optional.get());
        } else {
            runnable.run();
        }
        return optional;
    }

    public static Runnable debugRunnable(Runnable runnable, Supplier<String> messageSupplier) {
        return runnable;
    }

    public static <T extends Throwable> T throwOrPause(T t) {
        if (SharedConstants.isDevelopment) {
            LOGGER.error("Trying to throw a fatal exception, pausing in IDE", t);
            try {
                while (true) {
                    Thread.sleep(1000L);
                    LOGGER.error("paused");
                }
            } catch (InterruptedException interruptedException) {
                return t;
            }
        }
        return t;
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

    private static BooleanSupplier method_30625(final Path path, final Path path2) {
        return new BooleanSupplier(){

            @Override
            public boolean getAsBoolean() {
                try {
                    Files.move(path, path2, new CopyOption[0]);
                    return true;
                } catch (IOException iOException) {
                    LOGGER.error("Failed to rename", (Throwable)iOException);
                    return false;
                }
            }

            public String toString() {
                return "rename " + path + " to " + path2;
            }
        };
    }

    private static BooleanSupplier method_30624(final Path path) {
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

    private static BooleanSupplier method_30628(final Path path) {
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

    private static BooleanSupplier method_30629(final Path path) {
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

    private static boolean method_30627(BooleanSupplier ... booleanSuppliers) {
        for (BooleanSupplier booleanSupplier : booleanSuppliers) {
            if (booleanSupplier.getAsBoolean()) continue;
            LOGGER.warn("Failed to execute {}", (Object)booleanSupplier);
            return false;
        }
        return true;
    }

    private static boolean method_30622(int i, String string, BooleanSupplier ... booleanSuppliers) {
        for (int j = 0; j < i; ++j) {
            if (Util.method_30627(booleanSuppliers)) {
                return true;
            }
            LOGGER.error("Failed to {}, retrying {}/{}", (Object)string, (Object)j, (Object)i);
        }
        LOGGER.error("Failed to {}, aborting, progress might be lost", (Object)string);
        return false;
    }

    public static void method_27760(File file, File file2, File file3) {
        Util.method_30626(file.toPath(), file2.toPath(), file3.toPath());
    }

    public static void method_30626(Path path, Path path2, Path path3) {
        int i = 10;
        if (Files.exists(path, new LinkOption[0]) && !Util.method_30622(10, "create backup " + path3, Util.method_30624(path3), Util.method_30625(path, path3), Util.method_30629(path3))) {
            return;
        }
        if (!Util.method_30622(10, "remove old " + path, Util.method_30624(path), Util.method_30628(path))) {
            return;
        }
        if (!Util.method_30622(10, "replace " + path + " with " + path2, Util.method_30625(path2, path), Util.method_30629(path))) {
            Util.method_30622(10, "restore " + path + " from " + path3, Util.method_30625(path3, path), Util.method_30629(path));
        }
    }

    /**
     * Moves the {@code cursor} in the {@code string} by a {@code delta} amount.
     * Skips surrogate characters.
     */
    @Environment(value=EnvType.CLIENT)
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

    public static Consumer<String> method_29188(String string, Consumer<String> consumer) {
        return string2 -> consumer.accept(string + string2);
    }

    public static DataResult<int[]> toIntArray(IntStream intStream, int length) {
        int[] is = intStream.limit(length + 1).toArray();
        if (is.length != length) {
            String string = "Input is not a list of " + length + " ints";
            if (is.length >= length) {
                return DataResult.error(string, Arrays.copyOf(is, length));
            }
            return DataResult.error(string);
        }
        return DataResult.success(is);
    }

    public static void method_29476() {
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

    @Environment(value=EnvType.CLIENT)
    public static void method_29775(Path path, Path path2, Path path3) throws IOException {
        Path path4 = path.relativize(path3);
        Path path5 = path2.resolve(path4);
        Files.copy(path3, path5, new CopyOption[0]);
    }

    @Environment(value=EnvType.CLIENT)
    public static String method_30309(String string, class_5462 arg) {
        return string.toLowerCase(Locale.ROOT).chars().mapToObj(i -> arg.test((char)i) ? Character.toString((char)i) : "_").collect(Collectors.joining());
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
            @Environment(value=EnvType.CLIENT)
            protected String[] getURLOpenCommand(URL url) {
                return new String[]{"rundll32", "url.dll,FileProtocolHandler", url.toString()};
            }
        }
        ,
        OSX{

            @Override
            @Environment(value=EnvType.CLIENT)
            protected String[] getURLOpenCommand(URL url) {
                return new String[]{"open", url.toString()};
            }
        }
        ,
        UNKNOWN;


        @Environment(value=EnvType.CLIENT)
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

        @Environment(value=EnvType.CLIENT)
        public void open(URI uRI) {
            try {
                this.open(uRI.toURL());
            } catch (MalformedURLException malformedURLException) {
                LOGGER.error("Couldn't open uri '{}'", (Object)uRI, (Object)malformedURLException);
            }
        }

        @Environment(value=EnvType.CLIENT)
        public void open(File file) {
            try {
                this.open(file.toURI().toURL());
            } catch (MalformedURLException malformedURLException) {
                LOGGER.error("Couldn't open file '{}'", (Object)file, (Object)malformedURLException);
            }
        }

        @Environment(value=EnvType.CLIENT)
        protected String[] getURLOpenCommand(URL url) {
            String string = url.toString();
            if ("file".equals(url.getProtocol())) {
                string = string.replace("file:", "file://");
            }
            return new String[]{"xdg-open", string};
        }

        @Environment(value=EnvType.CLIENT)
        public void open(String string) {
            try {
                this.open(new URI(string).toURL());
            } catch (IllegalArgumentException | MalformedURLException | URISyntaxException exception) {
                LOGGER.error("Couldn't open uri '{}'", (Object)string, (Object)exception);
            }
        }
    }
}

