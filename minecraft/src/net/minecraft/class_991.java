package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_991<T extends class_1438> extends class_3887<T, class_560<T>> {
	public class_991(class_3883<T, class_560<T>> arg) {
		super(arg);
	}

	public void method_4195(T arg, float f, float g, float h, float i, float j, float k, float l) {
		if (!arg.method_6109() && !arg.method_5767()) {
			class_2680 lv = arg.method_18435().method_18437();
			this.method_17164(class_1059.field_5275);
			GlStateManager.enableCull();
			GlStateManager.cullFace(GlStateManager.class_1024.field_5068);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(1.0F, -1.0F, 1.0F);
			GlStateManager.translatef(0.2F, 0.35F, 0.5F);
			GlStateManager.rotatef(42.0F, 0.0F, 1.0F, 0.0F);
			class_776 lv2 = class_310.method_1551().method_1541();
			GlStateManager.pushMatrix();
			GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
			lv2.method_3353(lv, 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.1F, 0.0F, -0.6F);
			GlStateManager.rotatef(42.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
			lv2.method_3353(lv, 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			this.method_17165().method_2800().method_2847(0.0625F);
			GlStateManager.scalef(1.0F, -1.0F, 1.0F);
			GlStateManager.translatef(0.0F, 0.7F, -0.2F);
			GlStateManager.rotatef(12.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
			lv2.method_3353(lv, 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.cullFace(GlStateManager.class_1024.field_5070);
			GlStateManager.disableCull();
		}
	}

	@Override
	public boolean method_4200() {
		return true;
	}
}
