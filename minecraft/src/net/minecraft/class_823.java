package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_823 extends class_827<class_2573> {
	private final class_550 field_4339 = new class_550();

	public void method_3546(class_2573 arg, double d, double e, double f, float g, int i) {
		float h = 0.6666667F;
		boolean bl = arg.method_10997() == null;
		GlStateManager.pushMatrix();
		class_630 lv = this.field_4339.method_2791();
		long l;
		if (bl) {
			l = 0L;
			GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
			lv.field_3665 = true;
		} else {
			l = arg.method_10997().method_8510();
			class_2680 lv2 = arg.method_11010();
			if (lv2.method_11614() instanceof class_2215) {
				GlStateManager.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
				GlStateManager.rotatef((float)(-(Integer)lv2.method_11654(class_2215.field_9924) * 360) / 16.0F, 0.0F, 1.0F, 0.0F);
				lv.field_3665 = true;
			} else {
				GlStateManager.translatef((float)d + 0.5F, (float)e - 0.16666667F, (float)f + 0.5F);
				GlStateManager.rotatef(-((class_2350)lv2.method_11654(class_2546.field_11722)).method_10144(), 0.0F, 1.0F, 0.0F);
				GlStateManager.translatef(0.0F, -0.3125F, -0.4375F);
				lv.field_3665 = false;
			}
		}

		class_2338 lv3 = arg.method_11016();
		float j = (float)((long)(lv3.method_10263() * 7 + lv3.method_10264() * 9 + lv3.method_10260() * 13) + l) + g;
		this.field_4339.method_2792().field_3654 = (-0.0125F + 0.01F * class_3532.method_15362(j * (float) Math.PI * 0.02F)) * (float) Math.PI;
		GlStateManager.enableRescaleNormal();
		class_2960 lv4 = this.method_3547(arg);
		if (lv4 != null) {
			this.method_3566(lv4);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.6666667F, -0.6666667F, -0.6666667F);
			this.field_4339.method_2793();
			GlStateManager.popMatrix();
		}

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	@Nullable
	private class_2960 method_3547(class_2573 arg) {
		return class_770.field_4154.method_3331(arg.method_10915(), arg.method_10911(), arg.method_10909());
	}
}
