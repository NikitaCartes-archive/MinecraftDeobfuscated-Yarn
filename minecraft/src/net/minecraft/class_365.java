package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_365 extends class_332 implements class_536 {
	private static final class_2960 field_2197 = new class_2960("textures/gui/widgets.png");
	public static final class_2960 field_2199 = new class_2960("textures/gui/spectator_widgets.png");
	private final class_310 field_2201;
	private long field_2198;
	private class_531 field_2200;

	public class_365(class_310 arg) {
		this.field_2201 = arg;
	}

	public void method_1977(int i) {
		this.field_2198 = class_156.method_658();
		if (this.field_2200 != null) {
			this.field_2200.method_2771(i);
		} else {
			this.field_2200 = new class_531(this);
		}
	}

	private float method_1981() {
		long l = this.field_2198 - class_156.method_658() + 5000L;
		return class_3532.method_15363((float)l / 2000.0F, 0.0F, 1.0F);
	}

	public void method_1978(float f) {
		if (this.field_2200 != null) {
			float g = this.method_1981();
			if (g <= 0.0F) {
				this.field_2200.method_2779();
			} else {
				int i = this.field_2201.field_1704.method_4486() / 2;
				float h = this.field_2050;
				this.field_2050 = -90.0F;
				float j = (float)this.field_2201.field_1704.method_4502() - 22.0F * g;
				class_539 lv = this.field_2200.method_2772();
				this.method_1975(g, i, j, lv);
				this.field_2050 = h;
			}
		}
	}

	protected void method_1975(float f, int i, float g, class_539 arg) {
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
		);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, f);
		this.field_2201.method_1531().method_4618(field_2197);
		this.method_1784((float)(i - 91), g, 0, 0, 182, 22);
		if (arg.method_2787() >= 0) {
			this.method_1784((float)(i - 91 - 1 + arg.method_2787() * 20), g - 1.0F, 0, 22, 24, 22);
		}

		class_308.method_1453();

		for (int j = 0; j < 9; j++) {
			this.method_1982(j, this.field_2201.field_1704.method_4486() / 2 - 90 + j * 20 + 2, g + 3.0F, f, arg.method_2786(j));
		}

		class_308.method_1450();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
	}

	private void method_1982(int i, int j, float f, float g, class_537 arg) {
		this.field_2201.method_1531().method_4618(field_2199);
		if (arg != class_531.field_3260) {
			int k = (int)(g * 255.0F);
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)j, f, 0.0F);
			float h = arg.method_16893() ? 1.0F : 0.25F;
			GlStateManager.color4f(h, h, h, g);
			arg.method_2784(h, k);
			GlStateManager.popMatrix();
			String string = String.valueOf(this.field_2201.field_1690.field_1852[i].method_16007());
			if (k > 3 && arg.method_16893()) {
				this.field_2201.field_1772.method_1720(string, (float)(j + 19 - 2 - this.field_2201.field_1772.method_1727(string)), f + 6.0F + 3.0F, 16777215 + (k << 24));
			}
		}
	}

	public void method_1979() {
		int i = (int)(this.method_1981() * 255.0F);
		if (i > 3 && this.field_2200 != null) {
			class_537 lv = this.field_2200.method_2774();
			String string = lv == class_531.field_3260 ? this.field_2200.method_2776().method_2781().method_10863() : lv.method_16892().method_10863();
			if (string != null) {
				int j = (this.field_2201.field_1704.method_4486() - this.field_2201.field_1772.method_1727(string)) / 2;
				int k = this.field_2201.field_1704.method_4502() - 35;
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				GlStateManager.blendFuncSeparate(
					GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
				);
				this.field_2201.field_1772.method_1720(string, (float)j, (float)k, 16777215 + (i << 24));
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public void method_2782(class_531 arg) {
		this.field_2200 = null;
		this.field_2198 = 0L;
	}

	public boolean method_1980() {
		return this.field_2200 != null;
	}

	public void method_1976(double d) {
		int i = this.field_2200.method_2773() + (int)d;

		while (i >= 0 && i <= 8 && (this.field_2200.method_2777(i) == class_531.field_3260 || !this.field_2200.method_2777(i).method_16893())) {
			i = (int)((double)i + d);
		}

		if (i >= 0 && i <= 8) {
			this.field_2200.method_2771(i);
			this.field_2198 = class_156.method_658();
		}
	}

	public void method_1983() {
		this.field_2198 = class_156.method_658();
		if (this.method_1980()) {
			int i = this.field_2200.method_2773();
			if (i != -1) {
				this.field_2200.method_2771(i);
			}
		} else {
			this.field_2200 = new class_531(this);
		}
	}
}
