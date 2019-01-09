package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_492 extends class_465<class_1728> {
	private static final class_2960 field_2950 = new class_2960("textures/gui/container/villager.png");
	private class_492.class_493 field_2946;
	private class_492.class_493 field_2944;
	private int field_2947;

	public class_492(class_1728 arg, class_1661 arg2, class_2561 arg3) {
		super(arg, arg2, arg3);
	}

	private void method_2496() {
		this.field_2797.method_7650(this.field_2947);
		this.field_2563.method_1562().method_2883(new class_2863(this.field_2947));
	}

	@Override
	protected void method_2224() {
		super.method_2224();
		int i = (this.field_2561 - this.field_2792) / 2;
		int j = (this.field_2559 - this.field_2779) / 2;
		this.field_2946 = this.method_2219(new class_492.class_493(1, i + 120 + 27, j + 24 - 1, true) {
			@Override
			public void method_1826(double d, double e) {
				class_492.this.field_2947++;
				class_1916 lv = class_492.this.field_2797.method_17438();
				if (lv != null && class_492.this.field_2947 >= lv.size()) {
					class_492.this.field_2947 = lv.size() - 1;
				}

				class_492.this.method_2496();
			}
		});
		this.field_2944 = this.method_2219(new class_492.class_493(2, i + 36 - 19, j + 24 - 1, false) {
			@Override
			public void method_1826(double d, double e) {
				class_492.this.field_2947--;
				if (class_492.this.field_2947 < 0) {
					class_492.this.field_2947 = 0;
				}

				class_492.this.method_2496();
			}
		});
		this.field_2946.field_2078 = false;
		this.field_2944.field_2078 = false;
	}

	@Override
	protected void method_2388(int i, int j) {
		String string = this.field_17411.method_10863();
		this.field_2554.method_1729(string, (float)(this.field_2792 / 2 - this.field_2554.method_1727(string) / 2), 6.0F, 4210752);
		this.field_2554.method_1729(this.field_17410.method_5476().method_10863(), 8.0F, (float)(this.field_2779 - 96 + 2), 4210752);
	}

	@Override
	public void method_2225() {
		super.method_2225();
		class_1916 lv = this.field_2797.method_17438();
		this.field_2946.field_2078 = this.field_2947 < lv.size() - 1;
		this.field_2944.field_2078 = this.field_2947 > 0;
	}

	@Override
	protected void method_2389(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_2563.method_1531().method_4618(field_2950);
		int k = (this.field_2561 - this.field_2792) / 2;
		int l = (this.field_2559 - this.field_2779) / 2;
		this.method_1788(k, l, 0, 0, this.field_2792, this.field_2779);
		class_1916 lv = this.field_2797.method_17438();
		if (!lv.isEmpty()) {
			int m = this.field_2947;
			if (m < 0 || m >= lv.size()) {
				return;
			}

			class_1914 lv2 = (class_1914)lv.get(m);
			if (lv2.method_8255()) {
				this.field_2563.method_1531().method_4618(field_2950);
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableLighting();
				this.method_1788(this.field_2776 + 83, this.field_2800 + 21, 212, 0, 28, 21);
				this.method_1788(this.field_2776 + 83, this.field_2800 + 51, 212, 0, 28, 21);
			}
		}
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		super.method_2214(i, j, f);
		class_1916 lv = this.field_2797.method_17438();
		if (!lv.isEmpty()) {
			int k = (this.field_2561 - this.field_2792) / 2;
			int l = (this.field_2559 - this.field_2779) / 2;
			int m = this.field_2947;
			class_1914 lv2 = (class_1914)lv.get(m);
			class_1799 lv3 = lv2.method_8246();
			class_1799 lv4 = lv2.method_8247();
			class_1799 lv5 = lv2.method_8250();
			GlStateManager.pushMatrix();
			class_308.method_1453();
			GlStateManager.disableLighting();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableColorMaterial();
			GlStateManager.enableLighting();
			this.field_2560.field_4730 = 100.0F;
			this.field_2560.method_4023(lv3, k + 36, l + 24);
			this.field_2560.method_4025(this.field_2554, lv3, k + 36, l + 24);
			if (!lv4.method_7960()) {
				this.field_2560.method_4023(lv4, k + 62, l + 24);
				this.field_2560.method_4025(this.field_2554, lv4, k + 62, l + 24);
			}

			this.field_2560.method_4023(lv5, k + 120, l + 24);
			this.field_2560.method_4025(this.field_2554, lv5, k + 120, l + 24);
			this.field_2560.field_4730 = 0.0F;
			GlStateManager.disableLighting();
			if (this.method_2378(36, 24, 16, 16, (double)i, (double)j) && !lv3.method_7960()) {
				this.method_2218(lv3, i, j);
			} else if (!lv4.method_7960() && this.method_2378(62, 24, 16, 16, (double)i, (double)j)) {
				this.method_2218(lv4, i, j);
			} else if (!lv5.method_7960() && this.method_2378(120, 24, 16, 16, (double)i, (double)j)) {
				this.method_2218(lv5, i, j);
			} else if (lv2.method_8255() && (this.method_2378(83, 21, 28, 21, (double)i, (double)j) || this.method_2378(83, 51, 28, 21, (double)i, (double)j))) {
				this.method_2215(class_1074.method_4662("merchant.deprecated"), i, j);
			}

			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
			GlStateManager.enableDepthTest();
			class_308.method_1452();
		}

		this.method_2380(i, j);
	}

	@Environment(EnvType.CLIENT)
	abstract static class class_493 extends class_339 {
		private final boolean field_2952;

		public class_493(int i, int j, int k, boolean bl) {
			super(i, j, k, 12, 19, "");
			this.field_2952 = bl;
		}

		@Override
		public void method_1824(int i, int j, float f) {
			if (this.field_2076) {
				class_310.method_1551().method_1531().method_4618(class_492.field_2950);
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				boolean bl = i >= this.field_2069 && j >= this.field_2068 && i < this.field_2069 + this.field_2071 && j < this.field_2068 + this.field_2070;
				int k = 0;
				int l = 176;
				if (!this.field_2078) {
					l += this.field_2071 * 2;
				} else if (bl) {
					l += this.field_2071;
				}

				if (!this.field_2952) {
					k += this.field_2070;
				}

				this.method_1788(this.field_2069, this.field_2068, l, k, this.field_2071, this.field_2070);
			}
		}
	}
}
