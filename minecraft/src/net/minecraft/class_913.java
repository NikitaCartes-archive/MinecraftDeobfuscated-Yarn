package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_913 extends class_927<class_1439, class_574<class_1439>> {
	private static final class_2960 field_4717 = new class_2960("textures/entity/iron_golem.png");

	public class_913(class_898 arg) {
		super(arg, new class_574<>(), 0.5F);
		this.method_4046(new class_986(this));
	}

	protected class_2960 method_3987(class_1439 arg) {
		return field_4717;
	}

	protected void method_3986(class_1439 arg, float f, float g, float h) {
		super.method_4058(arg, f, g, h);
		if (!((double)arg.field_6225 < 0.01)) {
			float i = 13.0F;
			float j = arg.field_6249 - arg.field_6225 * (1.0F - h) + 6.0F;
			float k = (Math.abs(j % 13.0F - 6.5F) - 3.25F) / 3.25F;
			GlStateManager.rotatef(6.5F * k, 0.0F, 0.0F, 1.0F);
		}
	}
}
