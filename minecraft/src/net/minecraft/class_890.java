package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_890 extends class_968<class_1551, class_564<class_1551>> {
	private static final class_2960 field_4659 = new class_2960("textures/entity/zombie/drowned.png");

	public class_890(class_898 arg) {
		super(arg, new class_564<>(0.0F, 0.0F, 64, 64), new class_564<>(0.5F, true), new class_564<>(1.0F, true));
		this.method_4046(new class_980<>(this));
	}

	@Nullable
	@Override
	protected class_2960 method_4163(class_1642 arg) {
		return field_4659;
	}

	protected void method_4164(class_1551 arg, float f, float g, float h) {
		float i = arg.method_6024(h);
		super.method_17144(arg, f, g, h);
		if (i > 0.0F) {
			GlStateManager.rotatef(class_3532.method_16439(i, arg.field_5965, -10.0F - arg.field_5965), 1.0F, 0.0F, 0.0F);
		}
	}
}
