package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_949<T extends class_1628> extends class_927<T, class_611<T>> {
	private static final class_2960 field_4789 = new class_2960("textures/entity/spider/spider.png");

	public class_949(class_898 arg) {
		super(arg, new class_611<>(), 1.0F);
		this.method_4046(new class_1000<>(this));
	}

	protected float method_4124(T arg) {
		return 180.0F;
	}

	protected class_2960 method_4123(T arg) {
		return field_4789;
	}
}
