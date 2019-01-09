package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_896 extends class_927<class_1559, class_565<class_1559>> {
	private static final class_2960 field_4671 = new class_2960("textures/entity/endermite.png");

	public class_896(class_898 arg) {
		super(arg, new class_565<>(), 0.3F);
	}

	protected float method_3919(class_1559 arg) {
		return 180.0F;
	}

	protected class_2960 method_3920(class_1559 arg) {
		return field_4671;
	}
}
