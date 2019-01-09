package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_479 extends class_465<class_1714> implements class_518 {
	private static final class_2960 field_2878 = new class_2960("textures/gui/container/crafting_table.png");
	private static final class_2960 field_2881 = new class_2960("textures/gui/recipe_button.png");
	private final class_507 field_2880 = new class_507();
	private boolean field_2877;

	public class_479(class_1714 arg, class_1661 arg2, class_2561 arg3) {
		super(arg, arg2, arg3);
	}

	@Override
	protected void method_2224() {
		super.method_2224();
		this.field_2877 = this.field_2561 < 379;
		this.field_2880.method_2597(this.field_2561, this.field_2559, this.field_2563, this.field_2877, this.field_2797);
		this.field_2776 = this.field_2880.method_2595(this.field_2877, this.field_2561, this.field_2792);
		this.field_2557.add(this.field_2880);
		this.method_2219(new class_344(10, this.field_2776 + 5, this.field_2559 / 2 - 49, 20, 18, 0, 0, 19, field_2881) {
			@Override
			public void method_1826(double d, double e) {
				class_479.this.field_2880.method_2579(class_479.this.field_2877);
				class_479.this.field_2880.method_2591();
				class_479.this.field_2776 = class_479.this.field_2880.method_2595(class_479.this.field_2877, class_479.this.field_2561, class_479.this.field_2792);
				this.method_1893(class_479.this.field_2776 + 5, class_479.this.field_2559 / 2 - 49);
			}
		});
	}

	@Nullable
	@Override
	public class_364 getFocused() {
		return this.field_2880;
	}

	@Override
	public void method_2225() {
		super.method_2225();
		this.field_2880.method_2590();
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		if (this.field_2880.method_2605() && this.field_2877) {
			this.method_2389(f, i, j);
			this.field_2880.method_2578(i, j, f);
		} else {
			this.field_2880.method_2578(i, j, f);
			super.method_2214(i, j, f);
			this.field_2880.method_2581(this.field_2776, this.field_2800, true, f);
		}

		this.method_2380(i, j);
		this.field_2880.method_2601(this.field_2776, this.field_2800, i, j);
	}

	@Override
	protected void method_2388(int i, int j) {
		this.field_2554.method_1729(this.field_17411.method_10863(), 28.0F, 6.0F, 4210752);
		this.field_2554.method_1729(this.field_17410.method_5476().method_10863(), 8.0F, (float)(this.field_2779 - 96 + 2), 4210752);
	}

	@Override
	protected void method_2389(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_2563.method_1531().method_4618(field_2878);
		int k = this.field_2776;
		int l = (this.field_2559 - this.field_2779) / 2;
		this.method_1788(k, l, 0, 0, this.field_2792, this.field_2779);
	}

	@Override
	protected boolean method_2378(int i, int j, int k, int l, double d, double e) {
		return (!this.field_2877 || !this.field_2880.method_2605()) && super.method_2378(i, j, k, l, d, e);
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		if (this.field_2880.method_16807(d, e, i)) {
			return true;
		} else {
			return this.field_2877 && this.field_2880.method_2605() ? true : super.method_16807(d, e, i);
		}
	}

	@Override
	protected boolean method_2381(double d, double e, int i, int j, int k) {
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.field_2792) || e >= (double)(j + this.field_2779);
		return this.field_2880.method_2598(d, e, this.field_2776, this.field_2800, this.field_2792, this.field_2779, k) && bl;
	}

	@Override
	protected void method_2383(class_1735 arg, int i, int j, class_1713 arg2) {
		super.method_2383(arg, i, j, arg2);
		this.field_2880.method_2600(arg);
	}

	@Override
	public void method_16891() {
		this.field_2880.method_2592();
	}

	@Override
	public void method_2234() {
		this.field_2880.method_2607();
		super.method_2234();
	}

	@Override
	public class_507 method_2659() {
		return this.field_2880;
	}
}
