package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
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
	protected void init() {
		super.init();
		this.field_2877 = this.width < 379;
		this.field_2880.method_2597(this.width, this.height, this.minecraft, this.field_2877, this.field_2797);
		this.field_2776 = this.field_2880.method_2595(this.field_2877, this.width, this.field_2792);
		this.children.add(this.field_2880);
		this.method_20085(this.field_2880);
		this.addButton(new class_344(this.field_2776 + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, field_2881, arg -> {
			this.field_2880.method_2579(this.field_2877);
			this.field_2880.method_2591();
			this.field_2776 = this.field_2880.method_2595(this.field_2877, this.width, this.field_2792);
			((class_344)arg).method_1893(this.field_2776 + 5, this.height / 2 - 49);
		}));
	}

	@Override
	public void tick() {
		super.tick();
		this.field_2880.method_2590();
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		if (this.field_2880.method_2605() && this.field_2877) {
			this.method_2389(f, i, j);
			this.field_2880.render(i, j, f);
		} else {
			this.field_2880.render(i, j, f);
			super.render(i, j, f);
			this.field_2880.method_2581(this.field_2776, this.field_2800, true, f);
		}

		this.method_2380(i, j);
		this.field_2880.method_2601(this.field_2776, this.field_2800, i, j);
		this.method_20086(this.field_2880);
	}

	@Override
	protected void method_2388(int i, int j) {
		this.font.method_1729(this.title.method_10863(), 28.0F, 6.0F, 4210752);
		this.font.method_1729(this.field_17410.method_5476().method_10863(), 8.0F, (float)(this.field_2779 - 96 + 2), 4210752);
	}

	@Override
	protected void method_2389(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.method_1531().method_4618(field_2878);
		int k = this.field_2776;
		int l = (this.height - this.field_2779) / 2;
		this.blit(k, l, 0, 0, this.field_2792, this.field_2779);
	}

	@Override
	protected boolean method_2378(int i, int j, int k, int l, double d, double e) {
		return (!this.field_2877 || !this.field_2880.method_2605()) && super.method_2378(i, j, k, l, d, e);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.field_2880.mouseClicked(d, e, i)) {
			return true;
		} else {
			return this.field_2877 && this.field_2880.method_2605() ? true : super.mouseClicked(d, e, i);
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
	public void removed() {
		this.field_2880.method_2607();
		super.removed();
	}

	@Override
	public class_507 method_2659() {
		return this.field_2880;
	}
}
