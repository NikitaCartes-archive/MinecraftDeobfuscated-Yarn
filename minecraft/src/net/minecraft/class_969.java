package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_969 extends class_927<class_1493, class_624<class_1493>> {
	private static final class_2960 field_4821 = new class_2960("textures/entity/wolf/wolf.png");
	private static final class_2960 field_4822 = new class_2960("textures/entity/wolf/wolf_tame.png");
	private static final class_2960 field_4823 = new class_2960("textures/entity/wolf/wolf_angry.png");

	public class_969(class_898 arg) {
		super(arg, new class_624<>(), 0.5F);
		this.method_4046(new class_1006(this));
	}

	protected float method_4167(class_1493 arg, float f) {
		return arg.method_6714();
	}

	public void method_4166(class_1493 arg, double d, double e, double f, float g, float h) {
		if (arg.method_6711()) {
			float i = arg.method_5718() * arg.method_6707(h);
			GlStateManager.color3f(i, i, i);
		}

		super.method_4072(arg, d, e, f, g, h);
	}

	protected class_2960 method_4165(class_1493 arg) {
		if (arg.method_6181()) {
			return field_4822;
		} else {
			return arg.method_6709() ? field_4823 : field_4821;
		}
	}
}
