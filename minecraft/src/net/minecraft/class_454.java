package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_454 extends class_332 {
	private final class_310 field_2680;
	private final class_457 field_2687;
	private final class_453 field_2684;
	private final int field_2681;
	private final class_161 field_2682;
	private final class_185 field_2695;
	private final class_1799 field_2697;
	private final String field_2686;
	private final class_456 field_2696;
	private final Map<class_161, class_456> field_2685 = Maps.<class_161, class_456>newLinkedHashMap();
	private double field_2690;
	private double field_2689;
	private int field_2694 = Integer.MAX_VALUE;
	private int field_2693 = Integer.MAX_VALUE;
	private int field_2692 = Integer.MIN_VALUE;
	private int field_2691 = Integer.MIN_VALUE;
	private float field_2688;
	private boolean field_2683;

	public class_454(class_310 arg, class_457 arg2, class_453 arg3, int i, class_161 arg4, class_185 arg5) {
		this.field_2680 = arg;
		this.field_2687 = arg2;
		this.field_2684 = arg3;
		this.field_2681 = i;
		this.field_2682 = arg4;
		this.field_2695 = arg5;
		this.field_2697 = arg5.method_821();
		this.field_2686 = arg5.method_811().method_10863();
		this.field_2696 = new class_456(this, arg, arg4, arg5);
		this.method_2319(this.field_2696, arg4);
	}

	public class_161 method_2307() {
		return this.field_2682;
	}

	public String method_2309() {
		return this.field_2686;
	}

	public void method_2311(int i, int j, boolean bl) {
		this.field_2684.method_2301(this, i, j, bl, this.field_2681);
	}

	public void method_2315(int i, int j, class_918 arg) {
		this.field_2684.method_2306(i, j, this.field_2681, arg, this.field_2697);
	}

	public void method_2310() {
		if (!this.field_2683) {
			this.field_2690 = (double)(117 - (this.field_2692 + this.field_2694) / 2);
			this.field_2689 = (double)(56 - (this.field_2691 + this.field_2693) / 2);
			this.field_2683 = true;
		}

		GlStateManager.depthFunc(518);
		method_1785(0, 0, 234, 113, -16777216);
		GlStateManager.depthFunc(515);
		class_2960 lv = this.field_2695.method_812();
		if (lv != null) {
			this.field_2680.method_1531().method_4618(lv);
		} else {
			this.field_2680.method_1531().method_4618(class_1060.field_5285);
		}

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int i = class_3532.method_15357(this.field_2690);
		int j = class_3532.method_15357(this.field_2689);
		int k = i % 16;
		int l = j % 16;

		for (int m = -1; m <= 15; m++) {
			for (int n = -1; n <= 8; n++) {
				method_1781(k + 16 * m, l + 16 * n, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
			}
		}

		this.field_2696.method_2323(i, j, true);
		this.field_2696.method_2323(i, j, false);
		this.field_2696.method_2325(i, j);
	}

	public void method_2314(int i, int j, int k, int l) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef(0.0F, 0.0F, 200.0F);
		method_1785(0, 0, 234, 113, class_3532.method_15375(this.field_2688 * 255.0F) << 24);
		boolean bl = false;
		int m = class_3532.method_15357(this.field_2690);
		int n = class_3532.method_15357(this.field_2689);
		if (i > 0 && i < 234 && j > 0 && j < 113) {
			for (class_456 lv : this.field_2685.values()) {
				if (lv.method_2329(m, n, i, j)) {
					bl = true;
					lv.method_2331(m, n, this.field_2688, k, l);
					break;
				}
			}
		}

		GlStateManager.popMatrix();
		if (bl) {
			this.field_2688 = class_3532.method_15363(this.field_2688 + 0.02F, 0.0F, 0.3F);
		} else {
			this.field_2688 = class_3532.method_15363(this.field_2688 - 0.04F, 0.0F, 1.0F);
		}
	}

	public boolean method_2316(int i, int j, double d, double e) {
		return this.field_2684.method_2303(i, j, this.field_2681, d, e);
	}

	@Nullable
	public static class_454 method_2317(class_310 arg, class_457 arg2, int i, class_161 arg3) {
		if (arg3.method_686() == null) {
			return null;
		} else {
			for (class_453 lv : class_453.values()) {
				if (i < lv.method_2304()) {
					return new class_454(arg, arg2, lv, i, arg3, arg3.method_686());
				}

				i -= lv.method_2304();
			}

			return null;
		}
	}

	public void method_2313(double d, double e) {
		if (this.field_2692 - this.field_2694 > 234) {
			this.field_2690 = class_3532.method_15350(this.field_2690 + d, (double)(-(this.field_2692 - 234)), 0.0);
		}

		if (this.field_2691 - this.field_2693 > 113) {
			this.field_2689 = class_3532.method_15350(this.field_2689 + e, (double)(-(this.field_2691 - 113)), 0.0);
		}
	}

	public void method_2318(class_161 arg) {
		if (arg.method_686() != null) {
			class_456 lv = new class_456(this, this.field_2680, arg, arg.method_686());
			this.method_2319(lv, arg);
		}
	}

	private void method_2319(class_456 arg, class_161 arg2) {
		this.field_2685.put(arg2, arg);
		int i = arg.method_2327();
		int j = i + 28;
		int k = arg.method_2326();
		int l = k + 27;
		this.field_2694 = Math.min(this.field_2694, i);
		this.field_2692 = Math.max(this.field_2692, j);
		this.field_2693 = Math.min(this.field_2693, k);
		this.field_2691 = Math.max(this.field_2691, l);

		for (class_456 lv : this.field_2685.values()) {
			lv.method_2332();
		}
	}

	@Nullable
	public class_456 method_2308(class_161 arg) {
		return (class_456)this.field_2685.get(arg);
	}

	public class_457 method_2312() {
		return this.field_2687;
	}
}
