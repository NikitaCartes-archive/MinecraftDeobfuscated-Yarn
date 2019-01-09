package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1006 extends class_3887<class_1493, class_624<class_1493>> {
	private static final class_2960 field_4913 = new class_2960("textures/entity/wolf/wolf_collar.png");

	public class_1006(class_3883<class_1493, class_624<class_1493>> arg) {
		super(arg);
	}

	public void method_4209(class_1493 arg, float f, float g, float h, float i, float j, float k, float l) {
		if (arg.method_6181() && !arg.method_5767()) {
			this.method_17164(field_4913);
			float[] fs = arg.method_6713().method_7787();
			GlStateManager.color3f(fs[0], fs[1], fs[2]);
			this.method_17165().method_17132(arg, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean method_4200() {
		return true;
	}
}
