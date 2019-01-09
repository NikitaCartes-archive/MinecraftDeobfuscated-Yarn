package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_466 extends class_465<class_1704> {
	private static final class_2960 field_2808 = new class_2960("textures/gui/container/beacon.png");
	private class_466.class_468 field_2804;
	private boolean field_2805;
	private class_1291 field_17412;
	private class_1291 field_17413;

	public class_466(class_1704 arg, class_1661 arg2, class_2561 arg3) {
		super(arg, arg2, arg3);
		this.field_2792 = 230;
		this.field_2779 = 219;
		arg.method_7596(new class_1712() {
			@Override
			public void method_7634(class_1703 arg, class_2371<class_1799> arg2) {
			}

			@Override
			public void method_7635(class_1703 arg, int i, class_1799 arg2) {
			}

			@Override
			public void method_7633(class_1703 arg, int i, int j) {
				class_466.this.field_17412 = arg.method_17374();
				class_466.this.field_17413 = arg.method_17375();
				class_466.this.field_2805 = true;
			}
		});
	}

	@Override
	protected void method_2224() {
		super.method_2224();
		this.field_2804 = new class_466.class_468(-1, this.field_2776 + 164, this.field_2800 + 107);
		this.method_2219(this.field_2804);
		this.method_2219(new class_466.class_467(-2, this.field_2776 + 190, this.field_2800 + 107));
		this.field_2805 = true;
		this.field_2804.field_2078 = false;
	}

	@Override
	public void method_2225() {
		super.method_2225();
		int i = this.field_2797.method_17373();
		if (this.field_2805 && i >= 0) {
			this.field_2805 = false;
			int j = 100;

			for (int k = 0; k <= 2; k++) {
				int l = class_2580.field_11801[k].length;
				int m = l * 22 + (l - 1) * 2;

				for (int n = 0; n < l; n++) {
					class_1291 lv = class_2580.field_11801[k][n];
					class_466.class_469 lv2 = new class_466.class_469(j++, this.field_2776 + 76 + n * 24 - m / 2, this.field_2800 + 22 + k * 25, lv, true);
					this.method_2219(lv2);
					if (k >= i) {
						lv2.field_2078 = false;
					} else if (lv == this.field_17412) {
						lv2.method_2401(true);
					}
				}
			}

			int k = 3;
			int l = class_2580.field_11801[3].length + 1;
			int m = l * 22 + (l - 1) * 2;

			for (int nx = 0; nx < l - 1; nx++) {
				class_1291 lv = class_2580.field_11801[3][nx];
				class_466.class_469 lv2 = new class_466.class_469(j++, this.field_2776 + 167 + nx * 24 - m / 2, this.field_2800 + 47, lv, false);
				this.method_2219(lv2);
				if (3 >= i) {
					lv2.field_2078 = false;
				} else if (lv == this.field_17413) {
					lv2.method_2401(true);
				}
			}

			if (this.field_17412 != null) {
				class_466.class_469 lv3 = new class_466.class_469(j++, this.field_2776 + 167 + (l - 1) * 24 - m / 2, this.field_2800 + 47, this.field_17412, false);
				this.method_2219(lv3);
				if (3 >= i) {
					lv3.field_2078 = false;
				} else if (this.field_17412 == this.field_17413) {
					lv3.method_2401(true);
				}
			}
		}

		this.field_2804.field_2078 = this.field_2797.method_17376() && this.field_17412 != null;
	}

	@Override
	protected void method_2388(int i, int j) {
		class_308.method_1450();
		this.method_1789(this.field_2554, class_1074.method_4662("block.minecraft.beacon.primary"), 62, 10, 14737632);
		this.method_1789(this.field_2554, class_1074.method_4662("block.minecraft.beacon.secondary"), 169, 10, 14737632);

		for (class_339 lv : this.field_2564) {
			if (lv.method_1828()) {
				lv.method_1823(i - this.field_2776, j - this.field_2800);
				break;
			}
		}

		class_308.method_1453();
	}

	@Override
	protected void method_2389(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_2563.method_1531().method_4618(field_2808);
		int k = (this.field_2561 - this.field_2792) / 2;
		int l = (this.field_2559 - this.field_2779) / 2;
		this.method_1788(k, l, 0, 0, this.field_2792, this.field_2779);
		this.field_2560.field_4730 = 100.0F;
		this.field_2560.method_4023(new class_1799(class_1802.field_8687), k + 42, l + 109);
		this.field_2560.method_4023(new class_1799(class_1802.field_8477), k + 42 + 22, l + 109);
		this.field_2560.method_4023(new class_1799(class_1802.field_8695), k + 42 + 44, l + 109);
		this.field_2560.method_4023(new class_1799(class_1802.field_8620), k + 42 + 66, l + 109);
		this.field_2560.field_4730 = 0.0F;
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		super.method_2214(i, j, f);
		this.method_2380(i, j);
	}

	@Environment(EnvType.CLIENT)
	class class_467 extends class_466.class_470 {
		public class_467(int i, int j, int k) {
			super(i, j, k, class_466.field_2808, 112, 220);
		}

		@Override
		public void method_1826(double d, double e) {
			class_466.this.field_2563.field_1724.field_3944.method_2883(new class_2815(class_466.this.field_2563.field_1724.field_7512.field_7763));
			class_466.this.field_2563.method_1507(null);
		}

		@Override
		public void method_1823(int i, int j) {
			class_466.this.method_2215(class_1074.method_4662("gui.cancel"), i, j);
		}
	}

	@Environment(EnvType.CLIENT)
	class class_468 extends class_466.class_470 {
		public class_468(int i, int j, int k) {
			super(i, j, k, class_466.field_2808, 90, 220);
		}

		@Override
		public void method_1826(double d, double e) {
			class_466.this.field_2563
				.method_1562()
				.method_2883(new class_2866(class_1291.method_5554(class_466.this.field_17412), class_1291.method_5554(class_466.this.field_17413)));
			class_466.this.field_2563.field_1724.field_3944.method_2883(new class_2815(class_466.this.field_2563.field_1724.field_7512.field_7763));
			class_466.this.field_2563.method_1507(null);
		}

		@Override
		public void method_1823(int i, int j) {
			class_466.this.method_2215(class_1074.method_4662("gui.done"), i, j);
		}
	}

	@Environment(EnvType.CLIENT)
	class class_469 extends class_466.class_470 {
		private final class_1291 field_2813;
		private final boolean field_17416;

		public class_469(int i, int j, int k, class_1291 arg2, boolean bl) {
			super(i, j, k, class_465.field_2801, arg2.method_5553() % 12 * 18, 198 + arg2.method_5553() / 12 * 18);
			this.field_2813 = arg2;
			this.field_17416 = bl;
		}

		@Override
		public void method_1826(double d, double e) {
			if (!this.method_2402()) {
				if (this.field_17416) {
					class_466.this.field_17412 = this.field_2813;
				} else {
					class_466.this.field_17413 = this.field_2813;
				}

				class_466.this.field_2564.clear();
				class_466.this.field_2557.clear();
				class_466.this.method_2224();
				class_466.this.method_2225();
			}
		}

		@Override
		public void method_1823(int i, int j) {
			String string = class_1074.method_4662(this.field_2813.method_5567());
			if (!this.field_17416 && this.field_2813 != class_1294.field_5924) {
				string = string + " II";
			}

			class_466.this.method_2215(string, i, j);
		}
	}

	@Environment(EnvType.CLIENT)
	abstract static class class_470 extends class_339 {
		private final class_2960 field_2817;
		private final int field_2816;
		private final int field_2814;
		private boolean field_2815;

		protected class_470(int i, int j, int k, class_2960 arg, int l, int m) {
			super(i, j, k, 22, 22, "");
			this.field_2817 = arg;
			this.field_2816 = l;
			this.field_2814 = m;
		}

		@Override
		public void method_1824(int i, int j, float f) {
			if (this.field_2076) {
				class_310.method_1551().method_1531().method_4618(class_466.field_2808);
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.field_2075 = i >= this.field_2069 && j >= this.field_2068 && i < this.field_2069 + this.field_2071 && j < this.field_2068 + this.field_2070;
				int k = 219;
				int l = 0;
				if (!this.field_2078) {
					l += this.field_2071 * 2;
				} else if (this.field_2815) {
					l += this.field_2071 * 1;
				} else if (this.field_2075) {
					l += this.field_2071 * 3;
				}

				this.method_1788(this.field_2069, this.field_2068, l, 219, this.field_2071, this.field_2070);
				if (!class_466.field_2808.equals(this.field_2817)) {
					class_310.method_1551().method_1531().method_4618(this.field_2817);
				}

				this.method_1788(this.field_2069 + 2, this.field_2068 + 2, this.field_2816, this.field_2814, 18, 18);
			}
		}

		public boolean method_2402() {
			return this.field_2815;
		}

		public void method_2401(boolean bl) {
			this.field_2815 = bl;
		}
	}
}
