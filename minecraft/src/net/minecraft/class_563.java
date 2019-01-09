package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_563<T extends class_1309> extends class_583<T> {
	private final class_630 field_3364;
	private final class_630 field_3365 = new class_630(this, 22, 0);

	public class_563() {
		this.field_3365.method_2856(-10.0F, 0.0F, 0.0F, 10, 20, 2, 1.0F);
		this.field_3364 = new class_630(this, 22, 0);
		this.field_3364.field_3666 = true;
		this.field_3364.method_2856(0.0F, 0.0F, 0.0F, 10, 20, 2, 1.0F);
	}

	public void method_17078(T arg, float f, float g, float h, float i, float j, float k) {
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableCull();
		if (arg.method_6109()) {
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 1.5F, -0.1F);
			this.field_3365.method_2846(k);
			this.field_3364.method_2846(k);
			GlStateManager.popMatrix();
		} else {
			this.field_3365.method_2846(k);
			this.field_3364.method_2846(k);
		}
	}

	public void method_17079(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_17080(arg, f, g, h, i, j, k);
		float l = (float) (Math.PI / 12);
		float m = (float) (-Math.PI / 12);
		float n = 0.0F;
		float o = 0.0F;
		if (arg.method_6128()) {
			float p = 1.0F;
			if (arg.field_5984 < 0.0) {
				class_243 lv = new class_243(arg.field_5967, arg.field_5984, arg.field_6006).method_1029();
				p = 1.0F - (float)Math.pow(-lv.field_1351, 1.5);
			}

			l = p * (float) (Math.PI / 9) + (1.0F - p) * l;
			m = p * (float) (-Math.PI / 2) + (1.0F - p) * m;
		} else if (arg.method_5715()) {
			l = (float) (Math.PI * 2.0 / 9.0);
			m = (float) (-Math.PI / 4);
			n = 3.0F;
			o = 0.08726646F;
		}

		this.field_3365.field_3657 = 5.0F;
		this.field_3365.field_3656 = n;
		if (arg instanceof class_742) {
			class_742 lv2 = (class_742)arg;
			lv2.field_3900 = (float)((double)lv2.field_3900 + (double)(l - lv2.field_3900) * 0.1);
			lv2.field_3899 = (float)((double)lv2.field_3899 + (double)(o - lv2.field_3899) * 0.1);
			lv2.field_3898 = (float)((double)lv2.field_3898 + (double)(m - lv2.field_3898) * 0.1);
			this.field_3365.field_3654 = lv2.field_3900;
			this.field_3365.field_3675 = lv2.field_3899;
			this.field_3365.field_3674 = lv2.field_3898;
		} else {
			this.field_3365.field_3654 = l;
			this.field_3365.field_3674 = m;
			this.field_3365.field_3675 = o;
		}

		this.field_3364.field_3657 = -this.field_3365.field_3657;
		this.field_3364.field_3675 = -this.field_3365.field_3675;
		this.field_3364.field_3656 = this.field_3365.field_3656;
		this.field_3364.field_3654 = this.field_3365.field_3654;
		this.field_3364.field_3674 = -this.field_3365.field_3674;
	}
}
