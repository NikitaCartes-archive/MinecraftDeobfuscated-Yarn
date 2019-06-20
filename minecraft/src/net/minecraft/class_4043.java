package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4043 extends class_3887<class_4019, class_4041<class_4019>> {
	public class_4043(class_3883<class_4019, class_4041<class_4019>> arg) {
		super(arg);
	}

	public void method_18335(class_4019 arg, float f, float g, float h, float i, float j, float k, float l) {
		class_1799 lv = arg.method_6118(class_1304.field_6173);
		if (!lv.method_7960()) {
			boolean bl = arg.method_6113();
			boolean bl2 = arg.method_6109();
			GlStateManager.pushMatrix();
			if (bl2) {
				float m = 0.75F;
				GlStateManager.scalef(0.75F, 0.75F, 0.75F);
				GlStateManager.translatef(0.0F, 8.0F * l, 3.35F * l);
			}

			GlStateManager.translatef(
				this.method_17165().field_18015.field_3657 / 16.0F, this.method_17165().field_18015.field_3656 / 16.0F, this.method_17165().field_18015.field_3655 / 16.0F
			);
			float m = arg.method_18298(h) * (180.0F / (float)Math.PI);
			GlStateManager.rotatef(m, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotatef(j, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(k, 1.0F, 0.0F, 0.0F);
			if (arg.method_6109()) {
				if (bl) {
					GlStateManager.translatef(0.4F, 0.26F, 0.15F);
				} else {
					GlStateManager.translatef(0.06F, 0.26F, -0.5F);
				}
			} else if (bl) {
				GlStateManager.translatef(0.46F, 0.26F, 0.22F);
			} else {
				GlStateManager.translatef(0.06F, 0.27F, -0.5F);
			}

			GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
			if (bl) {
				GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
			}

			class_310.method_1551().method_1480().method_4016(lv, arg, class_809.class_811.field_4318, false);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
