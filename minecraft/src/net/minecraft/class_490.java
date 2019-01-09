package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_490 extends class_485<class_1723> implements class_518 {
	private static final class_2960 field_2933 = new class_2960("textures/gui/recipe_button.png");
	private float field_2935;
	private float field_2934;
	private final class_507 field_2929 = new class_507();
	private boolean field_2932;
	private boolean field_2931;
	private boolean field_2930;

	public class_490(class_1657 arg) {
		super(arg.field_7498, arg.field_7514, new class_2588("container.crafting"));
		this.field_2558 = true;
	}

	@Override
	public void method_2225() {
		if (this.field_2563.field_1761.method_2914()) {
			this.field_2563.method_1507(new class_481(this.field_2563.field_1724));
		} else {
			this.field_2929.method_2590();
		}
	}

	@Override
	protected void method_2224() {
		if (this.field_2563.field_1761.method_2914()) {
			this.field_2563.method_1507(new class_481(this.field_2563.field_1724));
		} else {
			super.method_2224();
			this.field_2931 = this.field_2561 < 379;
			this.field_2929.method_2597(this.field_2561, this.field_2559, this.field_2563, this.field_2931, this.field_2797);
			this.field_2932 = true;
			this.field_2776 = this.field_2929.method_2595(this.field_2931, this.field_2561, this.field_2792);
			this.field_2557.add(this.field_2929);
			this.method_2219(new class_344(10, this.field_2776 + 104, this.field_2559 / 2 - 22, 20, 18, 0, 0, 19, field_2933) {
				@Override
				public void method_1826(double d, double e) {
					class_490.this.field_2929.method_2579(class_490.this.field_2931);
					class_490.this.field_2929.method_2591();
					class_490.this.field_2776 = class_490.this.field_2929.method_2595(class_490.this.field_2931, class_490.this.field_2561, class_490.this.field_2792);
					this.method_1893(class_490.this.field_2776 + 104, class_490.this.field_2559 / 2 - 22);
					class_490.this.field_2930 = true;
				}
			});
		}
	}

	@Nullable
	@Override
	public class_364 getFocused() {
		return this.field_2929;
	}

	@Override
	protected void method_2388(int i, int j) {
		this.field_2554.method_1729(this.field_17411.method_10863(), 97.0F, 8.0F, 4210752);
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.field_2900 = !this.field_2929.method_2605();
		if (this.field_2929.method_2605() && this.field_2931) {
			this.method_2389(f, i, j);
			this.field_2929.method_2578(i, j, f);
		} else {
			this.field_2929.method_2578(i, j, f);
			super.method_2214(i, j, f);
			this.field_2929.method_2581(this.field_2776, this.field_2800, false, f);
		}

		this.method_2380(i, j);
		this.field_2929.method_2601(this.field_2776, this.field_2800, i, j);
		this.field_2935 = (float)i;
		this.field_2934 = (float)j;
	}

	@Override
	protected void method_2389(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_2563.method_1531().method_4618(field_2801);
		int k = this.field_2776;
		int l = this.field_2800;
		this.method_1788(k, l, 0, 0, this.field_2792, this.field_2779);
		method_2486(k + 51, l + 75, 30, (float)(k + 51) - this.field_2935, (float)(l + 75 - 50) - this.field_2934, this.field_2563.field_1724);
	}

	public static void method_2486(int i, int j, int k, float f, float g, class_1309 arg) {
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)i, (float)j, 50.0F);
		GlStateManager.scalef((float)(-k), (float)k, (float)k);
		GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
		float h = arg.field_6283;
		float l = arg.field_6031;
		float m = arg.field_5965;
		float n = arg.field_6259;
		float o = arg.field_6241;
		GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
		class_308.method_1452();
		GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(-((float)Math.atan((double)(g / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
		arg.field_6283 = (float)Math.atan((double)(f / 40.0F)) * 20.0F;
		arg.field_6031 = (float)Math.atan((double)(f / 40.0F)) * 40.0F;
		arg.field_5965 = -((float)Math.atan((double)(g / 40.0F))) * 20.0F;
		arg.field_6241 = arg.field_6031;
		arg.field_6259 = arg.field_6031;
		GlStateManager.translatef(0.0F, 0.0F, 0.0F);
		class_898 lv = class_310.method_1551().method_1561();
		lv.method_3945(180.0F);
		lv.method_3948(false);
		lv.method_3954(arg, 0.0, 0.0, 0.0, 0.0F, 1.0F, false);
		lv.method_3948(true);
		arg.field_6283 = h;
		arg.field_6031 = l;
		arg.field_5965 = m;
		arg.field_6259 = n;
		arg.field_6241 = o;
		GlStateManager.popMatrix();
		class_308.method_1450();
		GlStateManager.disableRescaleNormal();
		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		GlStateManager.disableTexture();
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
	}

	@Override
	protected boolean method_2378(int i, int j, int k, int l, double d, double e) {
		return (!this.field_2931 || !this.field_2929.method_2605()) && super.method_2378(i, j, k, l, d, e);
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		if (this.field_2929.method_16807(d, e, i)) {
			return true;
		} else {
			return this.field_2931 && this.field_2929.method_2605() ? false : super.method_16807(d, e, i);
		}
	}

	@Override
	public boolean method_16804(double d, double e, int i) {
		if (this.field_2930) {
			this.field_2930 = false;
			return true;
		} else {
			return super.method_16804(d, e, i);
		}
	}

	@Override
	protected boolean method_2381(double d, double e, int i, int j, int k) {
		boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.field_2792) || e >= (double)(j + this.field_2779);
		return this.field_2929.method_2598(d, e, this.field_2776, this.field_2800, this.field_2792, this.field_2779, k) && bl;
	}

	@Override
	protected void method_2383(class_1735 arg, int i, int j, class_1713 arg2) {
		super.method_2383(arg, i, j, arg2);
		this.field_2929.method_2600(arg);
	}

	@Override
	public void method_16891() {
		this.field_2929.method_2592();
	}

	@Override
	public void method_2234() {
		if (this.field_2932) {
			this.field_2929.method_2607();
		}

		super.method_2234();
	}

	@Override
	public class_507 method_2659() {
		return this.field_2929;
	}
}
