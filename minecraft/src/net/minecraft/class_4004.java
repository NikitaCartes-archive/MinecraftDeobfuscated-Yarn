package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4004<T extends class_1309> extends class_3887<T, class_620<T>> {
	private final class_918 field_17893 = class_310.method_1551().method_1480();

	public class_4004(class_3883<T, class_620<T>> arg) {
		super(arg);
	}

	public void method_18147(T arg, float f, float g, float h, float i, float j, float k, float l) {
		class_1799 lv = arg.method_6118(class_1304.field_6173);
		if (!lv.method_7960()) {
			class_1792 lv2 = lv.method_7909();
			class_2248 lv3 = class_2248.method_9503(lv2);
			GlStateManager.pushMatrix();
			boolean bl = this.field_17893.method_4014(lv) && lv3.method_9551() == class_1921.field_9179;
			if (bl) {
				GlStateManager.depthMask(false);
			}

			GlStateManager.translatef(0.0F, 0.4F, -0.4F);
			GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
			this.field_17893.method_4016(lv, arg, class_809.class_811.field_4318, false);
			if (bl) {
				GlStateManager.depthMask(true);
			}

			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
