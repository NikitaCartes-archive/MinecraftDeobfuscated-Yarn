package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_885 extends class_927<class_1431, class_561<class_1431>> {
	private static final class_2960 field_4652 = new class_2960("textures/entity/fish/cod.png");

	public class_885(class_898 arg) {
		super(arg, new class_561<>(), 0.3F);
	}

	@Nullable
	protected class_2960 method_3897(class_1431 arg) {
		return field_4652;
	}

	protected void method_3896(class_1431 arg, float f, float g, float h) {
		super.method_4058(arg, f, g, h);
		float i = 4.3F * class_3532.method_15374(0.6F * f);
		GlStateManager.rotatef(i, 0.0F, 1.0F, 0.0F);
		if (!arg.method_5799()) {
			GlStateManager.translatef(0.1F, 0.1F, -0.1F);
			GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
		}
	}
}
