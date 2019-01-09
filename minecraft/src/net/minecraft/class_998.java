package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_998<T extends class_1309> extends class_3887<T, class_591<T>> {
	public static final class_2960 field_4898 = new class_2960("textures/entity/trident_riptide.png");
	private final class_998.class_999 field_4897 = new class_998.class_999();

	public class_998(class_3883<T, class_591<T>> arg) {
		super(arg);
	}

	public void method_4203(T arg, float f, float g, float h, float i, float j, float k, float l) {
		if (arg.method_6123()) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.method_17164(field_4898);

			for (int m = 0; m < 3; m++) {
				GlStateManager.pushMatrix();
				GlStateManager.rotatef(i * (float)(-(45 + m * 5)), 0.0F, 1.0F, 0.0F);
				float n = 0.75F * (float)m;
				GlStateManager.scalef(n, n, n);
				GlStateManager.translatef(0.0F, -0.2F + 0.6F * (float)m, 0.0F);
				this.field_4897.method_17166(f, g, i, j, k, l);
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	static class class_999 extends class_3879 {
		private final class_630 field_4900;

		public class_999() {
			this.field_17138 = 64;
			this.field_17139 = 64;
			this.field_4900 = new class_630(this, 0, 0);
			this.field_4900.method_2844(-8.0F, -16.0F, -8.0F, 16, 32, 16);
		}

		public void method_17166(float f, float g, float h, float i, float j, float k) {
			this.field_4900.method_2846(k);
		}
	}
}
