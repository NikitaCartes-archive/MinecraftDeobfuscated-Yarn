package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_977 extends class_3887<class_1433, class_889<class_1433>> {
	private final class_918 field_4847 = class_310.method_1551().method_1480();

	public class_977(class_3883<class_1433, class_889<class_1433>> arg) {
		super(arg);
	}

	public void method_17160(class_1433 arg, float f, float g, float h, float i, float j, float k, float l) {
		boolean bl = arg.method_6068() == class_1306.field_6183;
		class_1799 lv = bl ? arg.method_6079() : arg.method_6047();
		class_1799 lv2 = bl ? arg.method_6047() : arg.method_6079();
		if (!lv.method_7960() || !lv2.method_7960()) {
			this.method_4180(arg, lv2);
		}
	}

	private void method_4180(class_1309 arg, class_1799 arg2) {
		if (!arg2.method_7960()) {
			class_1792 lv = arg2.method_7909();
			class_2248 lv2 = class_2248.method_9503(lv);
			GlStateManager.pushMatrix();
			boolean bl = this.field_4847.method_4014(arg2) && lv2.method_9551() == class_1921.field_9179;
			if (bl) {
				GlStateManager.depthMask(false);
			}

			float f = 1.0F;
			float g = -1.0F;
			float h = class_3532.method_15379(arg.field_5965) / 60.0F;
			if (arg.field_5965 < 0.0F) {
				GlStateManager.translatef(0.0F, 1.0F - h * 0.5F, -1.0F + h * 0.5F);
			} else {
				GlStateManager.translatef(0.0F, 1.0F + h * 0.8F, -1.0F + h * 0.2F);
			}

			this.field_4847.method_4016(arg2, arg, class_809.class_811.field_4318, false);
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
