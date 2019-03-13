package net.minecraft.util.profiler;

import java.time.Duration;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.SystemUtil;

public class DisableableProfiler implements Profiler {
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
		this.controller.field_16272.startTick();
		this.field_16271.field_16272.startTick();
	}

	@Override
	public void endTick() {
		this.controller.field_16272.endTick();
		this.field_16271.field_16272.endTick();
	}

	@Override
	public void push(String string) {
		this.controller.field_16272.push(string);
		this.field_16271.field_16272.push(string);
	}

	@Override
	public void push(Supplier<String> supplier) {
		this.controller.field_16272.push(supplier);
		this.field_16271.field_16272.push(supplier);
	}

	@Override
	public void pop() {
		this.controller.field_16272.pop();
		this.field_16271.field_16272.pop();
	}

	@Override
	public void swap(String string) {
		this.controller.field_16272.swap(string);
		this.field_16271.field_16272.swap(string);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void swap(Supplier<String> supplier) {
		this.controller.field_16272.swap(supplier);
		this.field_16271.field_16272.swap(supplier);
	}

	public interface ProfilerController {
		boolean isEnabled();

		ProfileResult method_16058();

		@Environment(EnvType.CLIENT)
		ProfileResult method_16059();

		void enable();
	}

	class ProfilerControllerImpl implements DisableableProfiler.ProfilerController {
		protected ReadableProfiler field_16272 = DummyProfiler.INSTANCE;

		private ProfilerControllerImpl() {
		}

		@Override
		public boolean isEnabled() {
			return this.field_16272 != DummyProfiler.INSTANCE;
		}

		@Override
		public ProfileResult method_16058() {
			ProfileResult profileResult = this.field_16272.method_16064();
			this.field_16272 = DummyProfiler.INSTANCE;
			return profileResult;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public ProfileResult method_16059() {
			return this.field_16272.method_16064();
		}

		@Override
		public void enable() {
			if (this.field_16272 == DummyProfiler.INSTANCE) {
				this.field_16272 = new ProfilerSystem(SystemUtil.getMeasuringTimeNano(), DisableableProfiler.this.tickSupplier);
			}
		}
	}
}
