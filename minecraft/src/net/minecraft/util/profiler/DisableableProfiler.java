package net.minecraft.util.profiler;

import java.time.Duration;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.SystemUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DisableableProfiler implements Profiler {
	private static final Logger field_19286 = LogManager.getLogger();
	private static final long field_16268 = Duration.ofMillis(300L).toNanos();
	private final IntSupplier tickSupplier;
	private final DisableableProfiler.ProfilerControllerImpl controller = new DisableableProfiler.ProfilerControllerImpl();
	private final DisableableProfiler.ProfilerControllerImpl field_16271 = new DisableableProfiler.ProfilerControllerImpl();

	public DisableableProfiler(IntSupplier intSupplier) {
		this.tickSupplier = intSupplier;
	}

	public DisableableProfiler.ProfilerController getController() {
		return this.controller;
	}

	@Override
	public void startTick() {
		this.controller.profiler.startTick();
		this.field_16271.profiler.startTick();
	}

	@Override
	public void endTick() {
		this.controller.profiler.endTick();
		this.field_16271.profiler.endTick();
	}

	@Override
	public void push(String string) {
		this.controller.profiler.push(string);
		this.field_16271.profiler.push(string);
	}

	@Override
	public void push(Supplier<String> supplier) {
		this.controller.profiler.push(supplier);
		this.field_16271.profiler.push(supplier);
	}

	@Override
	public void pop() {
		this.controller.profiler.pop();
		this.field_16271.profiler.pop();
	}

	@Override
	public void swap(String string) {
		this.controller.profiler.swap(string);
		this.field_16271.profiler.swap(string);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void swap(Supplier<String> supplier) {
		this.controller.profiler.swap(supplier);
		this.field_16271.profiler.swap(supplier);
	}

	public interface ProfilerController {
		boolean isEnabled();

		ProfileResult disable();

		@Environment(EnvType.CLIENT)
		ProfileResult getResults();

		void enable();
	}

	class ProfilerControllerImpl implements DisableableProfiler.ProfilerController {
		protected ReadableProfiler profiler = DummyProfiler.INSTANCE;

		private ProfilerControllerImpl() {
		}

		@Override
		public boolean isEnabled() {
			return this.profiler != DummyProfiler.INSTANCE;
		}

		@Override
		public ProfileResult disable() {
			ProfileResult profileResult = this.profiler.getResults();
			this.profiler = DummyProfiler.INSTANCE;
			return profileResult;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public ProfileResult getResults() {
			return this.profiler.getResults();
		}

		@Override
		public void enable() {
			if (this.profiler == DummyProfiler.INSTANCE) {
				this.profiler = new ProfilerSystem(SystemUtil.getMeasuringTimeNano(), DisableableProfiler.this.tickSupplier, true);
			}
		}
	}
}
