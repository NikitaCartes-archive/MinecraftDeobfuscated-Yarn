package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_989<T extends class_1309, M extends class_583<T> & class_3881> extends class_3887<T, M> {
	public class_989(class_3883<T, M> arg) {
		super(arg);
	}

	public void method_17162(T arg, float f, float g, float h, float i, float j, float k, float l) {
		boolean bl = arg.method_6068() == class_1306.field_6183;
		class_1799 lv = bl ? arg.method_6079() : arg.method_6047();
		class_1799 lv2 = bl ? arg.method_6047() : arg.method_6079();
		if (!lv.method_7960() || !lv2.method_7960()) {
			GlStateManager.pushMatrix();
			if (this.method_17165().field_3448) {
				float m = 0.5F;
				GlStateManager.translatef(0.0F, 0.75F, 0.0F);
				GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			}

			this.method_4192(arg, lv2, class_809.class_811.field_4320, class_1306.field_6183);
			this.method_4192(arg, lv, class_809.class_811.field_4323, class_1306.field_6182);
			GlStateManager.popMatrix();
		}
	}

	private void method_4192(class_1309 arg, class_1799 arg2, class_809.class_811 arg3, class_1306 arg4) {
		if (!arg2.method_7960()) {
			GlStateManager.pushMatrix();
			this.method_4193(arg4);
			if (arg.method_5715()) {
				GlStateManager.translatef(0.0F, 0.2F, 0.0F);
			}

			GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
			boolean bl = arg4 == class_1306.field_6182;
			GlStateManager.translatef((float)(bl ? -1 : 1) / 16.0F, 0.125F, -0.625F);
			class_310.method_1551().method_1489().method_3234(arg, arg2, arg3, bl);
			GlStateManager.popMatrix();
		}
	}

	protected void method_4193(class_1306 arg) {
		this.method_17165().method_2803(0.0625F, arg);
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
