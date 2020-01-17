package net.minecraft.util.profiler;

import java.time.Duration;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DisableableProfiler implements Profiler {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final long field_16268 = Duration.ofMillis(300L).toNanos();
	private final IntSupplier tickSupplier;
	private final DisableableProfiler.ControllerImpl controller = new DisableableProfiler.ControllerImpl();
	private final DisableableProfiler.ControllerImpl field_16271 = new DisableableProfiler.ControllerImpl();

	public DisableableProfiler(IntSupplier tickSupplier) {
		this.tickSupplier = tickSupplier;
	}

	public DisableableProfiler.Controller getController() {
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
	public void push(String location) {
		this.controller.profiler.push(location);
		this.field_16271.profiler.push(location);
	}

	@Override
	public void push(Supplier<String> locationGetter) {
		this.controller.profiler.push(locationGetter);
		this.field_16271.profiler.push(locationGetter);
	}

	@Override
	public void pop() {
		this.controller.profiler.pop();
		this.field_16271.profiler.pop();
	}

	@Override
	public void swap(String location) {
		this.controller.profiler.swap(location);
		this.field_16271.profiler.swap(location);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void swap(Supplier<String> locationGetter) {
		this.controller.profiler.swap(locationGetter);
		this.field_16271.profiler.swap(locationGetter);
	}

	@Override
	public void visit(String marker) {
		this.controller.profiler.visit(marker);
		this.field_16271.profiler.visit(marker);
	}

	@Override
	public void visit(Supplier<String> markerGetter) {
		this.controller.profiler.visit(markerGetter);
		this.field_16271.profiler.visit(markerGetter);
	}

	public interface Controller {
		boolean isEnabled();

		ProfileResult disable();

		@Environment(EnvType.CLIENT)
		ProfileResult getResults();

		void enable();
	}

	class ControllerImpl implements DisableableProfiler.Controller {
		protected ReadableProfiler profiler = DummyProfiler.INSTANCE;

		private ControllerImpl() {
		}

		@Override
		public boolean isEnabled() {
			return this.profiler != DummyProfiler.INSTANCE;
		}

		@Override
		public ProfileResult disable() {
			ProfileResult profileResult = this.profiler.getResult();
			this.profiler = DummyProfiler.INSTANCE;
			return profileResult;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public ProfileResult getResults() {
			return this.profiler.getResult();
		}

		@Override
		public void enable() {
			if (this.profiler == DummyProfiler.INSTANCE) {
				this.profiler = new ProfilerSystem(Util.getMeasuringTimeNano(), DisableableProfiler.this.tickSupplier, true);
			}
		}
	}
}
