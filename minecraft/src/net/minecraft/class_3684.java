package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3684 extends class_3887<class_1451, class_3680<class_1451>> {
	private static final class_2960 field_16260 = new class_2960("textures/entity/cat/cat_collar.png");
	private final class_3680<class_1451> field_16261 = new class_3680<>(0.01F);

	public class_3684(class_3883<class_1451, class_3680<class_1451>> arg) {
		super(arg);
	}

	public void method_16047(class_1451 arg, float f, float g, float h, float i, float j, float k, float l) {
		if (arg.method_6181() && !arg.method_5767()) {
			this.method_17164(field_16260);
			float[] fs = arg.method_16096().method_7787();
			GlStateManager.color3f(fs[0], fs[1], fs[2]);
			this.method_17165().method_17081(this.field_16261);
			this.field_16261.method_17074(arg, f, g, h);
			this.field_16261.method_2819(arg, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean method_4200() {
		return true;
	}
}
