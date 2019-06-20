package net.minecraft;

import java.time.Duration;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3689 implements class_3695 {
	private static final Logger field_19286 = LogManager.getLogger();
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
	public void method_15396(String string) {
		this.field_16270.field_16272.method_15396(string);
		this.field_16271.field_16272.method_15396(string);
	}

	@Override
	public void method_15400(Supplier<String> supplier) {
		this.field_16270.field_16272.method_15400(supplier);
		this.field_16271.field_16272.method_15400(supplier);
	}

	@Override
	public void method_15407() {
		this.field_16270.field_16272.method_15407();
		this.field_16271.field_16272.method_15407();
	}

	@Override
	public void method_15405(String string) {
		this.field_16270.field_16272.method_15405(string);
		this.field_16271.field_16272.method_15405(string);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_15403(Supplier<String> supplier) {
		this.field_16270.field_16272.method_15403(supplier);
		this.field_16271.field_16272.method_15403(supplier);
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
				this.field_16272 = new class_3533(class_156.method_648(), class_3689.this.field_16269);
			}
		}
	}
}
