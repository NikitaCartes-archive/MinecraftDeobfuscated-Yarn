package net.minecraft;

import java.time.Duration;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Profiler;
import net.minecraft.util.ProfilerSystem;
import net.minecraft.util.SystemUtil;

public class class_3689 implements Profiler {
	private static final long field_16268 = Duration.ofMillis(300L).toNanos();
	private final IntSupplier field_16269;
	private final class_3689.class_3691 field_16270 = new class_3689.class_3691();
	private final class_3689.class_3691 field_16271 = new class_3689.class_3691();

	public class_3689(IntSupplier intSupplier) {
		this.field_16269 = intSupplier;
	}

	public class_3689.class_3690 method_16055() {
		return this.field_16270;
	}

	@Override
	public void method_16065() {
		this.field_16270.field_16272.method_16065();
		this.field_16271.field_16272.method_16065();
	}

	@Override
	public void method_16066() {
		this.field_16270.field_16272.method_16066();
		this.field_16271.field_16272.method_16066();
	}

	@Override
	public void begin(String string) {
		this.field_16270.field_16272.begin(string);
		this.field_16271.field_16272.begin(string);
	}

	@Override
	public void begin(Supplier<String> supplier) {
		this.field_16270.field_16272.begin(supplier);
		this.field_16271.field_16272.begin(supplier);
	}

	@Override
	public void end() {
		this.field_16270.field_16272.end();
		this.field_16271.field_16272.end();
	}

	@Override
	public void endBegin(String string) {
		this.field_16270.field_16272.endBegin(string);
		this.field_16271.field_16272.endBegin(string);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void endBegin(Supplier<String> supplier) {
		this.field_16270.field_16272.endBegin(supplier);
		this.field_16271.field_16272.endBegin(supplier);
	}

	public interface class_3690 {
		boolean method_16057();

		class_3696 method_16058();

		@Environment(EnvType.CLIENT)
		class_3696 method_16059();

		void method_16060();
	}

	class class_3691 implements class_3689.class_3690 {
		protected class_3693 field_16272 = class_3694.field_16280;

		private class_3691() {
		}

		@Override
		public boolean method_16057() {
			return this.field_16272 != class_3694.field_16280;
		}

		@Override
		public class_3696 method_16058() {
			class_3696 lv = this.field_16272.method_16064();
			this.field_16272 = class_3694.field_16280;
			return lv;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public class_3696 method_16059() {
			return this.field_16272.method_16064();
		}

		@Override
		public void method_16060() {
			if (this.field_16272 == class_3694.field_16280) {
				this.field_16272 = new ProfilerSystem(SystemUtil.getMeasuringTimeNano(), class_3689.this.field_16269);
			}
		}
	}
}
