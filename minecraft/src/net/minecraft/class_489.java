package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_489<T extends class_1720> extends class_465<T> implements class_518 {
	private static final class_2960 field_2926 = new class_2960("textures/gui/recipe_button.png");
	public final class_517 field_2924;
	private boolean field_2925;
	private final class_2960 field_18975;

	public class_489(T arg, class_517 arg2, class_1661 arg3, class_2561 arg4, class_2960 arg5) {
		super(arg, arg3, arg4);
		this.field_2924 = arg2;
		this.field_18975 = arg5;
	}

	@Override
	public void init() {
		super.init();
		this.field_2925 = this.width < 379;
		this.field_2924.method_2597(this.width, this.height, this.minecraft, this.field_2925, this.field_2797);
		this.field_2776 = this.field_2924.method_2595(this.field_2925, this.width, this.field_2792);
		this.addButton(new class_344(this.field_2776 + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, field_2926, arg -> {
			this.field_2924.method_2579(this.field_2925);
			this.field_2924.method_2591();
			this.field_2776 = this.field_2924.method_2595(this.field_2925, this.width, this.field_2792);
			((class_344)arg).method_1893(this.field_2776 + 20, this.height / 2 - 49);
		}));
	}

	@Override
	public void tick() {
		super.tick();
		this.field_2924.method_2590();
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		if (this.field_2924.method_2605() && this.field_2925) {
			this.method_2389(f, i, j);
			this.field_2924.render(i, j, f);
		} else {
			this.field_2924.render(i, j, f);
			super.render(i, j, f);
			this.field_2924.method_2581(this.field_2776, this.field_2800, true, f);
		}

		this.method_2380(i, j);
		this.field_2924.method_2601(this.field_2776, this.field_2800, i, j);
	}

	@Override
	protected void method_2388(int i, int j) {
		String string = this.title.method_10863();
		this.font.method_1729(string, (float)(this.field_2792 / 2 - this.font.method_1727(string) / 2), 6.0F, 4210752);
		this.font.method_1729(this.field_17410.method_5476().method_10863(), 8.0F, (float)(this.field_2779 - 96 + 2), 4210752);
	}

	@Override
	protected void method_2389(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.method_1531().method_4618(this.field_18975);
		int k = this.field_2776;
		int l = this.field_2800;
		this.blit(k, l, 0, 0, this.field_2792, this.field_2779);
		if (this.field_2797.method_17365()) {
			int m = this.field_2797.method_17364();
			this.blit(k + 56, l + 36 + 12 - m, 176, 12 - m, 14, m + 1);
		}

		int m = this.field_2797.method_17363();
		this.blit(k + 79, l + 34, 176, 14, m + 1, 16);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.field_2924.mouseClicked(d, e, i)) {
			return true;
		} else {
			return this.field_2925 && this.field_2924.method_2605() ? true : super.mouseClicked(d, e, i);
		}
	}

	@Override
	protected void method_2383(class_1735 arg, int i, int j, class_1713 arg2) {
		super.method_2383(arg, i, j, arg2);
		this.field_2924.method_2600(arg);
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return this.field_2924.keyPressed(i, j, k) ? false : super.keyPressed(i, j, k);
	}

	@Override
	protected boolean method_2381(double d, double e, int i, int j, int k) {
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.field_2792) || e >= (double)(j + this.field_2779);
		return this.field_2924.method_2598(d, e, this.field_2776, this.field_2800, this.field_2792, this.field_2779, k) && bl;
	}

	@Override
	public boolean charTyped(char c, int i) {
		return this.field_2924.charTyped(c, i) ? true : super.charTyped(c, i);
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
	public void removed() {
		this.field_2924.method_2607();
		super.removed();
	}
}
