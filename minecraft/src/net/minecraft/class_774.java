package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_774 {
	public void method_3342(class_2248 arg, float f) {
		GlStateManager.color4f(f, f, f, 1.0F);
		GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
		class_756.field_3986.method_3166(new class_1799(arg));
	}
}
