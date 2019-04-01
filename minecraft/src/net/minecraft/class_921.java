package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_921 extends class_927<class_1501, class_578<class_1501>> {
	private static final class_2960[] field_4736 = new class_2960[]{
		new class_2960("textures/entity/llama/creamy.png"),
		new class_2960("textures/entity/llama/white.png"),
		new class_2960("textures/entity/llama/brown.png"),
		new class_2960("textures/entity/llama/gray.png")
	};

	public class_921(class_898 arg) {
		super(arg, new class_578<>(0.0F), 0.7F);
		this.method_4046(new class_988(this));
	}

	protected class_2960 method_4037(class_1501 arg) {
		return field_4736[arg.method_6809()];
	}
}
