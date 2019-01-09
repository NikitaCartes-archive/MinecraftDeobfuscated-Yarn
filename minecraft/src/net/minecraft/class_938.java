package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_938 extends class_927<class_1462, class_599<class_1462>> {
	private static final class_2960 field_4767 = new class_2960("textures/entity/fish/salmon.png");

	public class_938(class_898 arg) {
		super(arg, new class_599<>(), 0.2F);
	}

	@Nullable
	protected class_2960 method_4101(class_1462 arg) {
		return field_4767;
	}

	protected void method_4100(class_1462 arg, float f, float g, float h) {
		super.method_4058(arg, f, g, h);
		float i = 1.0F;
		float j = 1.0F;
		if (!arg.method_5799()) {
			i = 1.3F;
			j = 1.7F;
		}

		float k = i * 4.3F * class_3532.method_15374(j * 0.6F * f);
		GlStateManager.rotatef(k, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(0.0F, 0.0F, -0.4F);
		if (!arg.method_5799()) {
			GlStateManager.translatef(0.2F, 0.1F, 0.0F);
			GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
		}
	}
}
