package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_440 extends class_437 {
	private final class_437 field_2577;
	private String field_2578;

	public class_440(class_437 arg) {
		this.field_2577 = arg;
	}

	@Override
	protected void method_2224() {
		int i = 0;
		this.field_2578 = class_1074.method_4662("options.skinCustomisation.title");

		for (class_1664 lv : class_1664.values()) {
			this.method_2219(new class_440.class_441(lv.method_7431(), this.field_2561 / 2 - 155 + i % 2 * 160, this.field_2559 / 6 + 24 * (i >> 1), 150, 20, lv));
			i++;
		}

		this.method_2219(
			new class_349(
				199,
				this.field_2561 / 2 - 155 + i % 2 * 160,
				this.field_2559 / 6 + 24 * (i >> 1),
				class_315.class_316.field_1955,
				this.field_2563.field_1690.method_1642(class_315.class_316.field_1955)
			) {
				@Override
				public void method_1826(double d, double e) {
					class_440.this.field_2563.field_1690.method_1629(class_315.class_316.field_1955, 1);
					this.field_2074 = class_440.this.field_2563.field_1690.method_1642(class_315.class_316.field_1955);
					class_440.this.field_2563.field_1690.method_1643();
				}
			}
		);
		if (++i % 2 == 1) {
			i++;
		}

		this.method_2219(new class_339(200, this.field_2561 / 2 - 100, this.field_2559 / 6 + 24 * (i >> 1), class_1074.method_4662("gui.done")) {
			@Override
			public void method_1826(double d, double e) {
				class_440.this.field_2563.field_1690.method_1640();
				class_440.this.field_2563.method_1507(class_440.this.field_2577);
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
		this.method_1789(this.field_2554, this.field_2578, this.field_2561 / 2, 20, 16777215);
		super.method_2214(i, j, f);
	}

	private String method_2248(class_1664 arg) {
		String string;
		if (this.field_2563.field_1690.method_1633().contains(arg)) {
			string = class_1074.method_4662("options.on");
		} else {
			string = class_1074.method_4662("options.off");
		}

		return arg.method_7428().method_10863() + ": " + string;
	}

	@Environment(EnvType.CLIENT)
	class class_441 extends class_339 {
		private final class_1664 field_2579;

		private class_441(int i, int j, int k, int l, int m, class_1664 arg2) {
			super(i, j, k, l, m, class_440.this.method_2248(arg2));
			this.field_2579 = arg2;
		}

		@Override
		public void method_1826(double d, double e) {
			class_440.this.field_2563.field_1690.method_1631(this.field_2579);
			this.field_2074 = class_440.this.method_2248(this.field_2579);
		}
	}
}
