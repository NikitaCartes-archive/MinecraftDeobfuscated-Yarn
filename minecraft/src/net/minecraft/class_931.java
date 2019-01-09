package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_931 extends class_927<class_1440, class_586<class_1440>> {
	public class_931(class_898 arg) {
		super(arg, new class_586<>(9, 0.0F), 0.5F);
		this.method_4046(new class_990(this));
	}

	@Nullable
	protected class_2960 method_4083(class_1440 arg) {
		return arg.method_6554().method_6562();
	}

	protected void method_4085(class_1440 arg, float f, float g, float h) {
		super.method_4058(arg, f, g, h);
		if (arg.field_6767 > 0) {
			int i = arg.field_6767;
			int j = i + 1;
			float k = 7.0F;
			float l = arg.method_6109() ? 0.3F : 0.8F;
			if (i < 8) {
				float m = (float)(90 * i) / 7.0F;
				float n = (float)(90 * j) / 7.0F;
				float o = this.method_4086(m, n, j, h, 8.0F);
				GlStateManager.translatef(0.0F, (l + 0.2F) * (o / 90.0F), 0.0F);
				GlStateManager.rotatef(-o, 1.0F, 0.0F, 0.0F);
			} else if (i < 16) {
				float m = ((float)i - 8.0F) / 7.0F;
				float n = 90.0F + 90.0F * m;
				float p = 90.0F + 90.0F * ((float)j - 8.0F) / 7.0F;
				float o = this.method_4086(n, p, j, h, 16.0F);
				GlStateManager.translatef(0.0F, l + 0.2F + (l - 0.2F) * (o - 90.0F) / 90.0F, 0.0F);
				GlStateManager.rotatef(-o, 1.0F, 0.0F, 0.0F);
			} else if ((float)i < 24.0F) {
				float m = ((float)i - 16.0F) / 7.0F;
				float n = 180.0F + 90.0F * m;
				float p = 180.0F + 90.0F * ((float)j - 16.0F) / 7.0F;
				float o = this.method_4086(n, p, j, h, 24.0F);
				GlStateManager.translatef(0.0F, l + l * (270.0F - o) / 90.0F, 0.0F);
				GlStateManager.rotatef(-o, 1.0F, 0.0F, 0.0F);
			} else if (i < 32) {
				float m = ((float)i - 24.0F) / 7.0F;
				float n = 270.0F + 90.0F * m;
				float p = 270.0F + 90.0F * ((float)j - 24.0F) / 7.0F;
				float o = this.method_4086(n, p, j, h, 32.0F);
				GlStateManager.translatef(0.0F, l * ((360.0F - o) / 90.0F), 0.0F);
				GlStateManager.rotatef(-o, 1.0F, 0.0F, 0.0F);
			}
		} else {
			GlStateManager.rotatef(0.0F, 1.0F, 0.0F, 0.0F);
		}

		float q = arg.method_6534(h);
		if (q > 0.0F) {
			GlStateManager.translatef(0.0F, 0.8F * q, 0.0F);
			GlStateManager.rotatef(class_3532.method_16439(q, arg.field_5965, arg.field_5965 + 90.0F), 1.0F, 0.0F, 0.0F);
			GlStateManager.translatef(0.0F, -1.0F * q, 0.0F);
			if (arg.method_6524()) {
				float r = (float)(Math.cos((double)arg.field_6012 * 1.25) * Math.PI * 0.05F);
				GlStateManager.rotatef(r, 0.0F, 1.0F, 0.0F);
				if (arg.method_6109()) {
					GlStateManager.translatef(0.0F, 0.8F, 0.55F);
				}
			}
		}

		float r = arg.method_6555(h);
		if (r > 0.0F) {
			float k = arg.method_6109() ? 0.5F : 1.3F;
			GlStateManager.translatef(0.0F, k * r, 0.0F);
			GlStateManager.rotatef(class_3532.method_16439(r, arg.field_5965, arg.field_5965 + 180.0F), 1.0F, 0.0F, 0.0F);
		}
	}

	private float method_4086(float f, float g, int i, float h, float j) {
		return (float)i < j ? class_3532.method_16439(h, f, g) : f;
	}
}
