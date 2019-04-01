package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_944 extends class_3887<class_1606, class_602<class_1606>> {
	public class_944(class_3883<class_1606, class_602<class_1606>> arg) {
		super(arg);
	}

	public void method_4115(class_1606 arg, float f, float g, float h, float i, float j, float k, float l) {
		GlStateManager.pushMatrix();
		switch (arg.method_7119()) {
			case field_11033:
			default:
				break;
			case field_11034:
				GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.translatef(1.0F, -1.0F, 0.0F);
				GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
				break;
			case field_11039:
				GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.translatef(-1.0F, -1.0F, 0.0F);
				GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
				break;
			case field_11043:
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.translatef(0.0F, -1.0F, -1.0F);
				break;
			case field_11035:
				GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.translatef(0.0F, -1.0F, 1.0F);
				break;
			case field_11036:
				GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.translatef(0.0F, -2.0F, 0.0F);
		}

		class_630 lv = this.method_17165().method_2830();
		lv.field_3675 = j * (float) (Math.PI / 180.0);
		lv.field_3654 = k * (float) (Math.PI / 180.0);
		class_1767 lv2 = arg.method_7121();
		if (lv2 == null) {
			this.method_17164(class_943.field_4781);
		} else {
			this.method_17164(class_943.field_4780[lv2.method_7789()]);
		}

		lv.method_2846(l);
		GlStateManager.popMatrix();
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
