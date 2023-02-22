/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.util;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Backoff;
import net.minecraft.util.TimeSupplier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class PeriodicRunnerFactory {
    static final Logger LOGGER = LogUtils.getLogger();
    final Executor executor;
    final TimeUnit timeUnit;
    final TimeSupplier timeSupplier;

    public PeriodicRunnerFactory(Executor executor, TimeUnit timeUnit, TimeSupplier timeSupplier) {
        this.executor = executor;
        this.timeUnit = timeUnit;
        this.timeSupplier = timeSupplier;
    }

    public <T> PeriodicRunner<T> create(String name, Callable<T> task, Duration cycle, Backoff backoff) {
        long l = this.timeUnit.convert(cycle);
        if (l == 0L) {
            throw new IllegalArgumentException("Period of " + cycle + " too short for selected resolution of " + this.timeUnit);
        }
        return new PeriodicRunner<T>(name, task, l, backoff);
    }

    public RunnersManager create() {
        return new RunnersManager();
    }

    @Environment(value=EnvType.CLIENT)
    public class PeriodicRunner<T> {
        private final String name;
        private final Callable<T> task;
        private final long unitDuration;
        private final Backoff backoff;
        @Nullable
        private CompletableFuture<TimedErrableResult<T>> resultFuture;
        @Nullable
        TimedResult<T> lastResult;
        private long nextTime = -1L;

        PeriodicRunner(String name, Callable<T> task, long unitDuration, Backoff backoff) {
            this.name = name;
            this.task = task;
            this.unitDuration = unitDuration;
            this.backoff = backoff;
        }

        void run(long currentTime) {
            if (this.resultFuture != null) {
                TimedErrableResult timedErrableResult = this.resultFuture.getNow(null);
                if (timedErrableResult == null) {
                    return;
                }
                this.resultFuture = null;
                long l = timedErrableResult.time;
                timedErrableResult.value().ifLeft(value -> {
                    this.lastResult = new TimedResult<Object>(value, l);
                    this.nextTime = l + this.unitDuration * this.backoff.success();
                }).ifRight(exception -> {
                    long m = this.backoff.fail();
                    LOGGER.warn("Failed to process task {}, will repeat after {} cycles", this.name, m, exception);
                    this.nextTime = l + this.unitDuration * m;
                });
            }
            if (this.nextTime <= currentTime) {
                this.resultFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        T object = this.task.call();
                        long l = PeriodicRunnerFactory.this.timeSupplier.get(PeriodicRunnerFactory.this.timeUnit);
                        return new TimedErrableResult<T>(Either.left(object), l);
                    } catch (Exception exception) {
                        long l = PeriodicRunnerFactory.this.timeSupplier.get(PeriodicRunnerFactory.this.timeUnit);
                        return new TimedErrableResult(Either.right(exception), l);
                    }
                }, PeriodicRunnerFactory.this.executor);
            }
        }

        public void reset() {
            this.resultFuture = null;
            this.lastResult = null;
            this.nextTime = -1L;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class RunnersManager {
        private final List<ResultListenableRunner<?>> runners = new ArrayList();

        public <T> void add(PeriodicRunner<T> runner, Consumer<T> resultListener) {
            ResultListenableRunner<T> resultListenableRunner = new ResultListenableRunner<T>(runner, resultListener);
            this.runners.add(resultListenableRunner);
            resultListenableRunner.runListener();
        }

        public void forceRunListeners() {
            for (ResultListenableRunner<?> resultListenableRunner : this.runners) {
                resultListenableRunner.forceRunListener();
            }
        }

        public void runAll() {
            for (ResultListenableRunner<?> resultListenableRunner : this.runners) {
                resultListenableRunner.run(PeriodicRunnerFactory.this.timeSupplier.get(PeriodicRunnerFactory.this.timeUnit));
            }
        }

        public void resetAll() {
            for (ResultListenableRunner<?> resultListenableRunner : this.runners) {
                resultListenableRunner.reset();
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ResultListenableRunner<T> {
        private final PeriodicRunner<T> runner;
        private final Consumer<T> resultListener;
        private long lastRunTime = -1L;

        ResultListenableRunner(PeriodicRunner<T> runner, Consumer<T> resultListener) {
            this.runner = runner;
            this.resultListener = resultListener;
        }

        void run(long currentTime) {
            this.runner.run(currentTime);
            this.runListener();
        }

        void runListener() {
            TimedResult timedResult = this.runner.lastResult;
            if (timedResult != null && this.lastRunTime < timedResult.time) {
                this.resultListener.accept(timedResult.value);
                this.lastRunTime = timedResult.time;
            }
        }

        void forceRunListener() {
            TimedResult timedResult = this.runner.lastResult;
            if (timedResult != null) {
                this.resultListener.accept(timedResult.value);
                this.lastRunTime = timedResult.time;
            }
        }

        void reset() {
            this.runner.reset();
            this.lastRunTime = -1L;
        }
    }

    @Environment(value=EnvType.CLIENT)
    record TimedResult<T>(T value, long time) {
    }

    @Environment(value=EnvType.CLIENT)
    record TimedErrableResult<T>(Either<T, Exception> value, long time) {
    }
}

