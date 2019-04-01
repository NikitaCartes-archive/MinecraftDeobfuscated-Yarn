package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_308 {
	private static final FloatBuffer field_1675 = class_311.method_1597(4);
	private static final class_1160 field_1677 = method_15980(0.2F, 1.0F, -0.7F);
	private static final class_1160 field_1676 = method_15980(-0.2F, 1.0F, 0.7F);

	private static class_1160 method_15980(float f, float g, float h) {
		class_1160 lv = new class_1160(f, g, h);
		lv.method_4952();
		return lv;
	}

	public static void method_1450() {
		GlStateManager.disableLighting();
		GlStateManager.disableLight(0);
		GlStateManager.disableLight(1);
		GlStateManager.disableColorMaterial();
	}

	public static void method_1452() {
		GlStateManager.enableLighting();
		GlStateManager.enableLight(0);
		GlStateManager.enableLight(1);
		GlStateManager.enableColorMaterial();
		GlStateManager.colorMaterial(1032, 5634);
		GlStateManager.light(16384, 4611, method_1451(field_1677.method_4943(), field_1677.method_4945(), field_1677.method_4947(), 0.0F));
		float f = 0.6F;
		GlStateManager.light(16384, 4609, method_1451(0.6F, 0.6F, 0.6F, 1.0F));
		GlStateManager.light(16384, 4608, method_1451(0.0F, 0.0F, 0.0F, 1.0F));
		GlStateManager.light(16384, 4610, method_1451(0.0F, 0.0F, 0.0F, 1.0F));
		GlStateManager.light(16385, 4611, method_1451(field_1676.method_4943(), field_1676.method_4945(), field_1676.method_4947(), 0.0F));
		GlStateManager.light(16385, 4609, method_1451(0.6F, 0.6F, 0.6F, 1.0F));
		GlStateManager.light(16385, 4608, method_1451(0.0F, 0.0F, 0.0F, 1.0F));
		GlStateManager.light(16385, 4610, method_1451(0.0F, 0.0F, 0.0F, 1.0F));
		GlStateManager.shadeModel(7424);
		float g = 0.4F;
		GlStateManager.lightModel(2899, method_1451(0.4F, 0.4F, 0.4F, 1.0F));
	}

	public static FloatBuffer method_1451(float f, float g, float h, float i) {
		field_1675.clear();
		field_1675.put(f).put(g).put(h).put(i);
		field_1675.flip();
		return field_1675;
	}

	public static void method_1453() {
		GlStateManager.pushMatrix();
		GlStateManager.rotatef(-30.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(165.0F, 1.0F, 0.0F, 0.0F);
		method_1452();
		GlStateManager.popMatrix();
	}
}
