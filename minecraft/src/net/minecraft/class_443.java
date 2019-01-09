package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_443 extends class_437 {
	private final class_437 field_2617;
	private final class_315 field_2618;
	protected String field_2615 = "Options";
	private String field_2616;

	public class_443(class_437 arg, class_315 arg2) {
		this.field_2617 = arg;
		this.field_2618 = arg2;
	}

	@Override
	protected void method_2224() {
		this.field_2615 = class_1074.method_4662("options.sounds.title");
		this.field_2616 = class_1074.method_4662("options.off");
		int i = 0;
		this.method_2219(
			new class_443.class_444(
				class_3419.field_15250.ordinal(), this.field_2561 / 2 - 155 + i % 2 * 160, this.field_2559 / 6 - 12 + 24 * (i >> 1), class_3419.field_15250, true
			)
		);
		i += 2;

		for (class_3419 lv : class_3419.values()) {
			if (lv != class_3419.field_15250) {
				this.method_2219(new class_443.class_444(lv.ordinal(), this.field_2561 / 2 - 155 + i % 2 * 160, this.field_2559 / 6 - 12 + 24 * (i >> 1), lv, false));
				i++;
			}
		}

		this.method_2219(
			new class_349(
				201,
				this.field_2561 / 2 - 75,
				this.field_2559 / 6 - 12 + 24 * (++i >> 1),
				class_315.class_316.field_1951,
				this.field_2618.method_1642(class_315.class_316.field_1951)
			) {
				@Override
				public void method_1826(double d, double e) {
					class_443.this.field_2563.field_1690.method_1629(class_315.class_316.field_1951, 1);
					this.field_2074 = class_443.this.field_2563.field_1690.method_1642(class_315.class_316.field_1951);
					class_443.this.field_2563.field_1690.method_1640();
				}
			}
		);
		this.method_2219(new class_339(200, this.field_2561 / 2 - 100, this.field_2559 / 6 + 168, class_1074.method_4662("gui.done")) {
			@Override
			public void method_1826(double d, double e) {
				class_443.this.field_2563.field_1690.method_1640();
				class_443.this.field_2563.method_1507(class_443.this.field_2617);
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
		this.method_1789(this.field_2554, this.field_2615, this.field_2561 / 2, 15, 16777215);
		super.method_2214(i, j, f);
	}

	protected String method_2256(class_3419 arg) {
		float f = this.field_2618.method_1630(arg);
		return f == 0.0F ? this.field_2616 : (int)(f * 100.0F) + "%";
	}

	@Environment(EnvType.CLIENT)
	class class_444 extends class_339 {
		private final class_3419 field_2622;
		private final String field_2621;
		public double field_2620;
		public boolean field_2623;

		public class_444(int i, int j, int k, class_3419 arg2, boolean bl) {
			super(i, j, k, bl ? 310 : 150, 20, "");
			this.field_2622 = arg2;
			this.field_2621 = class_1074.method_4662("soundCategory." + arg2.method_14840());
			this.field_2074 = this.field_2621 + ": " + class_443.this.method_2256(arg2);
			this.field_2620 = (double)class_443.this.field_2618.method_1630(arg2);
		}

		@Override
		protected int method_1830(boolean bl) {
			return 0;
		}

		@Override
		protected void method_1827(class_310 arg, int i, int j) {
			if (this.field_2076) {
				if (this.field_2623) {
					this.field_2620 = (double)((float)(i - (this.field_2069 + 4)) / (float)(this.field_2071 - 8));
					this.field_2620 = class_3532.method_15350(this.field_2620, 0.0, 1.0);
					arg.field_1690.method_1624(this.field_2622, (float)this.field_2620);
					arg.field_1690.method_1640();
					this.field_2074 = this.field_2621 + ": " + class_443.this.method_2256(this.field_2622);
				}

				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.method_1788(this.field_2069 + (int)(this.field_2620 * (double)(this.field_2071 - 8)), this.field_2068, 0, 66, 4, 20);
				this.method_1788(this.field_2069 + (int)(this.field_2620 * (double)(this.field_2071 - 8)) + 4, this.field_2068, 196, 66, 4, 20);
			}
		}

		@Override
		public void method_1826(double d, double e) {
			this.field_2620 = (d - (double)(this.field_2069 + 4)) / (double)(this.field_2071 - 8);
			this.field_2620 = class_3532.method_15350(this.field_2620, 0.0, 1.0);
			class_443.this.field_2563.field_1690.method_1624(this.field_2622, (float)this.field_2620);
			class_443.this.field_2563.field_1690.method_1640();
			this.field_2074 = this.field_2621 + ": " + class_443.this.method_2256(this.field_2622);
			this.field_2623 = true;
		}

		@Override
		public void method_1832(class_1144 arg) {
		}

		@Override
		public void method_1831(double d, double e) {
			if (this.field_2623) {
				class_443.this.field_2563.method_1483().method_4873(class_1109.method_4758(class_3417.field_15015, 1.0F));
			}

			this.field_2623 = false;
		}
	}
}
