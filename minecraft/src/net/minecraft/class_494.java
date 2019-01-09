package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_494 extends class_465<class_1726> {
	private static final class_2960 field_2966 = new class_2960("textures/gui/container/loom.png");
	private static final int field_2963 = (class_2582.field_11846 - 4 - 1 + 4 - 1) / 4;
	private static final class_1767 field_2964 = class_1767.field_7944;
	private static final class_1767 field_2956 = class_1767.field_7952;
	private static final List<class_1767> field_2959 = Lists.<class_1767>newArrayList(field_2964, field_2956);
	private class_2960 field_2957;
	private class_1799 field_2955 = class_1799.field_8037;
	private class_1799 field_2954 = class_1799.field_8037;
	private class_1799 field_2967 = class_1799.field_8037;
	private final class_2960[] field_2972 = new class_2960[class_2582.field_11846];
	private boolean field_2965;
	private boolean field_2962;
	private boolean field_2961;
	private float field_2968;
	private boolean field_2958;
	private int field_2970 = 1;
	private int field_2969 = 1;

	public class_494(class_1726 arg, class_1661 arg2, class_2561 arg3) {
		super(arg, arg2, arg3);
		arg.method_17423(this::method_17576);
	}

	@Override
	public void method_2225() {
		super.method_2225();
		if (this.field_2969 < class_2582.field_11846) {
			class_2582 lv = class_2582.values()[this.field_2969];
			String string = "b" + field_2964.method_7789();
			String string2 = lv.method_10945() + field_2956.method_7789();
			this.field_2972[this.field_2969] = class_770.field_4154
				.method_3331(string + string2, Lists.<class_2582>newArrayList(class_2582.field_11834, lv), field_2959);
			this.field_2969++;
		}
	}

	@Override
	public void method_2214(int i, int j, float f) {
		super.method_2214(i, j, f);
		this.method_2380(i, j);
	}

	@Override
	protected void method_2388(int i, int j) {
		this.field_2554.method_1729(this.field_17411.method_10863(), 8.0F, 4.0F, 4210752);
		this.field_2554.method_1729(this.field_17410.method_5476().method_10863(), 8.0F, (float)(this.field_2779 - 96 + 2), 4210752);
	}

	@Override
	protected void method_2389(float f, int i, int j) {
		this.method_2240();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_2563.method_1531().method_4618(field_2966);
		int k = this.field_2776;
		int l = this.field_2800;
		this.method_1788(k, l, 0, 0, this.field_2792, this.field_2779);
		class_1735 lv = this.field_2797.method_17428();
		class_1735 lv2 = this.field_2797.method_17429();
		class_1735 lv3 = this.field_2797.method_17430();
		class_1735 lv4 = this.field_2797.method_17431();
		if (!lv.method_7681()) {
			this.method_1788(k + lv.field_7873, l + lv.field_7872, this.field_2792, 0, 16, 16);
		}

		if (!lv2.method_7681()) {
			this.method_1788(k + lv2.field_7873, l + lv2.field_7872, this.field_2792 + 16, 0, 16, 16);
		}

		if (!lv3.method_7681()) {
			this.method_1788(k + lv3.field_7873, l + lv3.field_7872, this.field_2792 + 32, 0, 16, 16);
		}

		int m = (int)(41.0F * this.field_2968);
		this.method_1788(k + 119, l + 13 + m, 232 + (this.field_2965 ? 0 : 12), 0, 12, 15);
		if (this.field_2957 != null && !this.field_2961) {
			this.field_2563.method_1531().method_4618(this.field_2957);
			method_1786(k + 141, l + 8, 1.0F, 1.0F, 20, 40, 20, 40, 64.0F, 64.0F);
		} else if (this.field_2961) {
			this.method_1788(k + lv4.field_7873 - 2, l + lv4.field_7872 - 2, this.field_2792, 17, 17, 16);
		}

		if (this.field_2965) {
			int n = k + 60;
			int o = l + 13;
			int p = this.field_2970 + 16;

			for (int q = this.field_2970; q < p && q < this.field_2972.length - 4; q++) {
				int r = q - this.field_2970;
				int s = n + r % 4 * 14;
				int t = o + r / 4 * 14;
				this.field_2563.method_1531().method_4618(field_2966);
				int u = this.field_2779;
				if (q == this.field_2797.method_7647()) {
					u += 14;
				} else if (i >= s && j >= t && i < s + 14 && j < t + 14) {
					u += 28;
				}

				this.method_1788(s, t, 0, u, 14, 14);
				if (this.field_2972[q] != null) {
					this.field_2563.method_1531().method_4618(this.field_2972[q]);
					method_1786(s + 4, t + 2, 1.0F, 1.0F, 20, 40, 5, 10, 64.0F, 64.0F);
				}
			}
		} else if (this.field_2962) {
			int n = k + 60;
			int o = l + 13;
			this.field_2563.method_1531().method_4618(field_2966);
			this.method_1788(n, o, 0, this.field_2779, 14, 14);
			int p = this.field_2797.method_7647();
			if (this.field_2972[p] != null) {
				this.field_2563.method_1531().method_4618(this.field_2972[p]);
				method_1786(n + 4, o + 2, 1.0F, 1.0F, 20, 40, 5, 10, 64.0F, 64.0F);
			}
		}
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		this.field_2958 = false;
		if (this.field_2965) {
			int j = this.field_2776 + 60;
			int k = this.field_2800 + 13;
			int l = this.field_2970 + 16;

			for (int m = this.field_2970; m < l; m++) {
				int n = m - this.field_2970;
				double f = d - (double)(j + n % 4 * 14);
				double g = e - (double)(k + n / 4 * 14);
				if (f >= 0.0 && g >= 0.0 && f < 14.0 && g < 14.0 && this.field_2797.method_7604(this.field_2563.field_1724, m)) {
					class_310.method_1551().method_1483().method_4873(class_1109.method_4758(class_3417.field_14920, 1.0F));
					this.field_2563.field_1761.method_2900(this.field_2797.field_7763, m);
					return true;
				}
			}

			j = this.field_2776 + 119;
			k = this.field_2800 + 9;
			if (d >= (double)j && d < (double)(j + 12) && e >= (double)k && e < (double)(k + 56)) {
				this.field_2958 = true;
			}
		}

		return super.method_16807(d, e, i);
	}

	@Override
	public boolean method_16801(double d, double e, int i, double f, double g) {
		if (this.field_2958 && this.field_2965) {
			int j = this.field_2800 + 13;
			int k = j + 56;
			this.field_2968 = ((float)e - (float)j - 7.5F) / ((float)(k - j) - 15.0F);
			this.field_2968 = class_3532.method_15363(this.field_2968, 0.0F, 1.0F);
			int l = field_2963 - 4;
			int m = (int)((double)(this.field_2968 * (float)l) + 0.5);
			if (m < 0) {
				m = 0;
			}

			this.field_2970 = 1 + m * 4;
			return true;
		} else {
			return super.method_16801(d, e, i, f, g);
		}
	}

	@Override
	public boolean method_16802(double d) {
		if (this.field_2965) {
			int i = field_2963 - 4;
			this.field_2968 = (float)((double)this.field_2968 - d / (double)i);
			this.field_2968 = class_3532.method_15363(this.field_2968, 0.0F, 1.0F);
			this.field_2970 = 1 + (int)((double)(this.field_2968 * (float)i) + 0.5) * 4;
		}

		return true;
	}

	@Override
	protected boolean method_2381(double d, double e, int i, int j, int k) {
		return d < (double)i || e < (double)j || d >= (double)(i + this.field_2792) || e >= (double)(j + this.field_2779);
	}

	private void method_17576() {
		class_1799 lv = this.field_2797.method_17431().method_7677();
		if (lv.method_7960()) {
			this.field_2957 = null;
		} else {
			class_2573 lv2 = new class_2573();
			lv2.method_10913(lv, ((class_1746)lv.method_7909()).method_7706());
			this.field_2957 = class_770.field_4154.method_3331(lv2.method_10915(), lv2.method_10911(), lv2.method_10909());
		}

		class_1799 lv3 = this.field_2797.method_17428().method_7677();
		class_1799 lv4 = this.field_2797.method_17429().method_7677();
		class_1799 lv5 = this.field_2797.method_17430().method_7677();
		class_2487 lv6 = lv3.method_7911("BlockEntityTag");
		this.field_2961 = lv6.method_10573("Patterns", 9) && !lv3.method_7960() && lv6.method_10554("Patterns", 10).size() >= 6;
		if (this.field_2961) {
			this.field_2957 = null;
		}

		if (!class_1799.method_7973(lv3, this.field_2955) || !class_1799.method_7973(lv4, this.field_2954) || !class_1799.method_7973(lv5, this.field_2967)) {
			this.field_2965 = !lv3.method_7960() && !lv4.method_7960() && lv5.method_7960() && !this.field_2961;
			this.field_2962 = !lv5.method_7960() && !this.field_2961;
		}

		this.field_2955 = lv3.method_7972();
		this.field_2954 = lv4.method_7972();
		this.field_2967 = lv5.method_7972();
	}
}
