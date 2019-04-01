package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_950 extends class_946 {
	private static final class_2960 field_4790 = new class_2960("textures/entity/skeleton/stray.png");

	public class_950(class_898 arg) {
		super(arg);
		this.method_4046(new class_1002<>(this));
	}

	@Override
	protected class_2960 method_4119(class_1547 arg) {
		return field_4790;
	}
}
