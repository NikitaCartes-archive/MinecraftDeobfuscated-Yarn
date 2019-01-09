package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_353 extends class_350<class_353.class_354> {
	public class_353(class_310 arg, int i, int j, int k, int l, int m, class_315.class_316... args) {
		super(arg, i, j, k, l, m);
		this.field_2173 = false;
		this.method_1901(new class_353.class_354(i, class_315.class_316.field_1931));

		for (int n = 0; n < args.length; n += 2) {
			class_315.class_316 lv = args[n];
			class_315.class_316 lv2 = n < args.length - 1 ? args[n + 1] : null;
			this.method_1901(new class_353.class_354(i, lv, lv2));
		}
	}

	@Nullable
	private static class_339 method_1914(class_310 arg, int i, int j, int k, @Nullable class_315.class_316 arg2) {
		if (arg2 == null) {
			return null;
		} else {
			int l = arg2.method_1647();
			return (class_339)(arg2.method_1653()
				? new class_357(l, i, j, k, 20, arg2, 0.0, 1.0)
				: new class_349(l, i, j, k, 20, arg2, arg.field_1690.method_1642(arg2)) {
					@Override
					public void method_1826(double d, double e) {
						arg.field_1690.method_1629(arg2, 1);
						this.field_2074 = arg.field_1690.method_1642(class_315.class_316.method_1655(this.field_2077));
					}
				});
		}
	}

	@Override
	public int method_1932() {
		return 400;
	}

	@Override
	protected int method_1948() {
		return super.method_1948() + 32;
	}

	@Environment(EnvType.CLIENT)
	public final class class_354 extends class_350.class_351<class_353.class_354> {
		@Nullable
		private final class_339 field_2149;
		@Nullable
		private final class_339 field_2150;

		public class_354(@Nullable class_339 arg2, @Nullable class_339 arg3) {
			this.field_2149 = arg2;
			this.field_2150 = arg3;
		}

		public class_354(int i, class_315.class_316 arg2) {
			this(class_353.method_1914(class_353.this.field_2164, i / 2 - 155, 0, 310, arg2), null);
		}

		public class_354(int i, class_315.class_316 arg2, @Nullable class_315.class_316 arg3) {
			this(
				class_353.method_1914(class_353.this.field_2164, i / 2 - 155, 0, 150, arg2),
				class_353.method_1914(class_353.this.field_2164, i / 2 - 155 + 160, 0, 150, arg3)
			);
		}

		@Override
		public void method_1903(int i, int j, int k, int l, boolean bl, float f) {
			if (this.field_2149 != null) {
				this.field_2149.field_2068 = this.method_1906();
				this.field_2149.method_1824(k, l, f);
			}

			if (this.field_2150 != null) {
				this.field_2150.field_2068 = this.method_1906();
				this.field_2150.method_1824(k, l, f);
			}
		}

		@Override
		public boolean method_16807(double d, double e, int i) {
			return this.field_2149.method_16807(d, e, i) ? true : this.field_2150 != null && this.field_2150.method_16807(d, e, i);
		}

		@Override
		public boolean method_16804(double d, double e, int i) {
			boolean bl = this.field_2149 != null && this.field_2149.method_16804(d, e, i);
			boolean bl2 = this.field_2150 != null && this.field_2150.method_16804(d, e, i);
			return bl || bl2;
		}

		@Override
		public void method_1904(float f) {
		}
	}
}
