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
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Backoff;
import net.minecraft.util.TimeSupplier;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
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

	public <T> PeriodicRunnerFactory.PeriodicRunner<T> create(String name, Callable<T> task, Duration cycle, Backoff backoff) {
		long l = this.timeUnit.convert(cycle);
		if (l == 0L) {
			throw new IllegalArgumentException("Period of " + cycle + " too short for selected resolution of " + this.timeUnit);
		} else {
			return new PeriodicRunnerFactory.PeriodicRunner<>(name, task, l, backoff);
		}
	}

	public PeriodicRunnerFactory.RunnersManager create() {
		return new PeriodicRunnerFactory.RunnersManager();
	}

	@Environment(EnvType.CLIENT)
	public class PeriodicRunner<T> {
		private final String name;
		private final Callable<T> task;
		private final long unitDuration;
		private final Backoff backoff;
		@Nullable
		private CompletableFuture<PeriodicRunnerFactory.TimedErrableResult<T>> resultFuture;
		@Nullable
		PeriodicRunnerFactory.TimedResult<T> lastResult;
		private long nextTime = -1L;

		PeriodicRunner(final String name, final Callable<T> task, final long unitDuration, final Backoff backoff) {
			this.name = name;
			this.task = task;
			this.unitDuration = unitDuration;
			this.backoff = backoff;
		}

		void run(long currentTime) {
			if (this.resultFuture != null) {
				PeriodicRunnerFactory.TimedErrableResult<T> timedErrableResult = (PeriodicRunnerFactory.TimedErrableResult<T>)this.resultFuture.getNow(null);
				if (timedErrableResult == null) {
					return;
				}

				this.resultFuture = null;
				long l = timedErrableResult.time;
				timedErrableResult.value().ifLeft(value -> {
					this.lastResult = new PeriodicRunnerFactory.TimedResult<>((T)value, l);
					this.nextTime = l + this.unitDuration * this.backoff.success();
				}).ifRight(exception -> {
					long m = this.backoff.fail();
					PeriodicRunnerFactory.LOGGER.warn("Failed to process task {}, will repeat after {} cycles", this.name, m, exception);
					this.nextTime = l + this.unitDuration * m;
				});
			}

			if (this.nextTime <= currentTime) {
				this.resultFuture = CompletableFuture.supplyAsync(() -> {
					try {
						T object = (T)this.task.call();
						long lx = PeriodicRunnerFactory.this.timeSupplier.get(PeriodicRunnerFactory.this.timeUnit);
						return new PeriodicRunnerFactory.TimedErrableResult<>(Either.left(object), lx);
					} catch (Exception var4x) {
						long lx = PeriodicRunnerFactory.this.timeSupplier.get(PeriodicRunnerFactory.this.timeUnit);
						return new PeriodicRunnerFactory.TimedErrableResult(Either.right(var4x), lx);
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

	@Environment(EnvType.CLIENT)
	class ResultListenableRunner<T> {
		private final PeriodicRunnerFactory.PeriodicRunner<T> runner;
		private final Consumer<T> resultListener;
		private long lastRunTime = -1L;

		ResultListenableRunner(final PeriodicRunnerFactory.PeriodicRunner<T> runner, final Consumer<T> resultListener) {
			this.runner = runner;
			this.resultListener = resultListener;
		}

		void run(long currentTime) {
			this.runner.run(currentTime);
			this.runListener();
		}

		void runListener() {
			PeriodicRunnerFactory.TimedResult<T> timedResult = this.runner.lastResult;
			if (timedResult != null && this.lastRunTime < timedResult.time) {
				this.resultListener.accept(timedResult.value);
				this.lastRunTime = timedResult.time;
			}
		}

		void forceRunListener() {
			PeriodicRunnerFactory.TimedResult<T> timedResult = this.runner.lastResult;
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

	@Environment(EnvType.CLIENT)
	public class RunnersManager {
		private final List<PeriodicRunnerFactory.ResultListenableRunner<?>> runners = new ArrayList();

		public <T> void add(PeriodicRunnerFactory.PeriodicRunner<T> runner, Consumer<T> resultListener) {
			PeriodicRunnerFactory.ResultListenableRunner<T> resultListenableRunner = PeriodicRunnerFactory.this.new ResultListenableRunner<>(runner, resultListener);
			this.runners.add(resultListenableRunner);
			resultListenableRunner.runListener();
		}

		public void forceRunListeners() {
			for (PeriodicRunnerFactory.ResultListenableRunner<?> resultListenableRunner : this.runners) {
				resultListenableRunner.forceRunListener();
			}
		}

		public void runAll() {
			for (PeriodicRunnerFactory.ResultListenableRunner<?> resultListenableRunner : this.runners) {
				resultListenableRunner.run(PeriodicRunnerFactory.this.timeSupplier.get(PeriodicRunnerFactory.this.timeUnit));
			}
		}

		public void resetAll() {
			for (PeriodicRunnerFactory.ResultListenableRunner<?> resultListenableRunner : this.runners) {
				resultListenableRunner.reset();
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static record TimedErrableResult<T>(Either<T, Exception> value, long time) {
	}

	@Environment(EnvType.CLIENT)
	static record TimedResult<T>(T value, long time) {
	}
}
