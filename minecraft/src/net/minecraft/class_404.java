package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_404 extends class_437 {
	private static final class_315.class_316[] field_2352 = new class_315.class_316[]{
		class_315.class_316.field_1923,
		class_315.class_316.field_1917,
		class_315.class_316.field_1920,
		class_315.class_316.field_1921,
		class_315.class_316.field_1925,
		class_315.class_316.field_1946,
		class_315.class_316.field_1940,
		class_315.class_316.field_1942,
		class_315.class_316.field_1941,
		class_315.class_316.field_1962,
		class_315.class_316.field_1956,
		class_315.class_316.field_1957
	};
	private final class_437 field_2354;
	private final class_315 field_2356;
	private String field_2353;
	private class_349 field_2355;

	public class_404(class_437 arg, class_315 arg2) {
		this.field_2354 = arg;
		this.field_2356 = arg2;
	}

	@Override
	protected void method_2224() {
		this.field_2353 = class_1074.method_4662("options.chat.title");
		int i = 0;

		for (class_315.class_316 lv : field_2352) {
			if (lv.method_1653()) {
				this.method_2219(new class_357(lv.method_1647(), this.field_2561 / 2 - 155 + i % 2 * 160, this.field_2559 / 6 + 24 * (i >> 1), lv));
			} else {
				class_349 lv2 = new class_349(
					lv.method_1647(), this.field_2561 / 2 - 155 + i % 2 * 160, this.field_2559 / 6 + 24 * (i >> 1), lv, this.field_2356.method_1642(lv)
				) {
					@Override
					public void method_1826(double d, double e) {
						class_404.this.field_2356.method_1629(this.method_1899(), 1);
						this.field_2074 = class_404.this.field_2356.method_1642(class_315.class_316.method_1655(this.field_2077));
					}
				};
				this.method_2219(lv2);
				if (lv == class_315.class_316.field_1956) {
					this.field_2355 = lv2;
					lv2.field_2078 = class_333.field_2054.method_1791();
				}
			}

			i++;
		}

		this.method_2219(new class_339(200, this.field_2561 / 2 - 100, this.field_2559 / 6 + 144, class_1074.method_4662("gui.done")) {
			@Override
			public void method_1826(double d, double e) {
				class_404.this.field_2563.field_1690.method_1640();
				class_404.this.field_2563.method_1507(class_404.this.field_2354);
			}
		});
	}

	@Override
	public void method_2210() {
		this.field_2563.field_1690.method_1640();
		super.method_2210();
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.method_1789(this.field_2554, this.field_2353, this.field_2561 / 2, 20, 16777215);
		super.method_2214(i, j, f);
	}

	public void method_2096() {
		this.field_2355.field_2074 = this.field_2356.method_1642(class_315.class_316.method_1655(this.field_2355.field_2077));
	}
}
