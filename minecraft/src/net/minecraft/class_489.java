package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_489<T extends class_1720<T>> extends class_465<T> implements class_518 {
	private static final class_2960 field_2926 = new class_2960("textures/gui/recipe_button.png");
	public final class_517 field_2924;
	private boolean field_2925;

	public class_489(T arg, class_517 arg2, class_1661 arg3, class_2561 arg4) {
		super(arg, arg3, arg4);
		this.field_2924 = arg2;
	}

	@Override
	public void method_2224() {
		super.method_2224();
		this.field_2925 = this.field_2561 < 379;
		this.field_2924.method_2597(this.field_2561, this.field_2559, this.field_2563, this.field_2925, this.field_2797);
		this.field_2776 = this.field_2924.method_2595(this.field_2925, this.field_2561, this.field_2792);
		this.method_2219(new class_344(10, this.field_2776 + 20, this.field_2559 / 2 - 49, 20, 18, 0, 0, 19, field_2926) {
			@Override
			public void method_1826(double d, double e) {
				class_489.this.field_2924.method_2579(class_489.this.field_2925);
				class_489.this.field_2924.method_2591();
				class_489.this.field_2776 = class_489.this.field_2924.method_2595(class_489.this.field_2925, class_489.this.field_2561, class_489.this.field_2792);
				this.method_1893(class_489.this.field_2776 + 20, class_489.this.field_2559 / 2 - 49);
			}
		});
	}

	protected abstract class_2960 method_17045();

	@Override
	public void method_2225() {
		super.method_2225();
		this.field_2924.method_2590();
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		if (this.field_2924.method_2605() && this.field_2925) {
			this.method_2389(f, i, j);
			this.field_2924.method_2578(i, j, f);
		} else {
			this.field_2924.method_2578(i, j, f);
			super.method_2214(i, j, f);
			this.field_2924.method_2581(this.field_2776, this.field_2800, true, f);
		}

		this.method_2380(i, j);
		this.field_2924.method_2601(this.field_2776, this.field_2800, i, j);
	}

	@Override
	protected void method_2388(int i, int j) {
		String string = this.field_17411.method_10863();
		this.field_2554.method_1729(string, (float)(this.field_2792 / 2 - this.field_2554.method_1727(string) / 2), 6.0F, 4210752);
		this.field_2554.method_1729(this.field_17410.method_5476().method_10863(), 8.0F, (float)(this.field_2779 - 96 + 2), 4210752);
	}

	@Override
	protected void method_2389(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_2563.method_1531().method_4618(this.method_17045());
		int k = this.field_2776;
		int l = this.field_2800;
		this.method_1788(k, l, 0, 0, this.field_2792, this.field_2779);
		if (this.field_2797.method_17365()) {
			int m = this.field_2797.method_17364();
			this.method_1788(k + 56, l + 36 + 12 - m, 176, 12 - m, 14, m + 1);
		}

		int m = this.field_2797.method_17363();
		this.method_1788(k + 79, l + 34, 176, 14, m + 1, 16);
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		if (this.field_2924.method_16807(d, e, i)) {
			return true;
		} else {
			return this.field_2925 && this.field_2924.method_2605() ? true : super.method_16807(d, e, i);
		}
	}

	@Override
	protected void method_2383(class_1735 arg, int i, int j, class_1713 arg2) {
		super.method_2383(arg, i, j, arg2);
		this.field_2924.method_2600(arg);
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		return this.field_2924.method_16805(i, j, k) ? false : super.method_16805(i, j, k);
	}

	@Override
	protected boolean method_2381(double d, double e, int i, int j, int k) {
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.field_2792) || e >= (double)(j + this.field_2779);
		return this.field_2924.method_2598(d, e, this.field_2776, this.field_2800, this.field_2792, this.field_2779, k) && bl;
	}

	@Override
	public boolean method_16806(char c, int i) {
		return this.field_2924.method_16806(c, i) ? true : super.method_16806(c, i);
	}

	@Override
	public void method_16891() {
		this.field_2924.method_2592();
	}

	@Override
	public class_507 method_2659() {
		return this.field_2924;
	}

	@Override
	public void method_2234() {
		this.field_2924.method_2607();
		super.method_2234();
	}
}
