package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_935 extends class_909<class_1590, class_623<class_1590>> {
	private static final class_2960 field_4759 = new class_2960("textures/entity/zombie_pigman.png");

	public class_935(class_898 arg) {
		super(arg, new class_623<>(), 0.5F);
		this.method_4046(new class_987<>(this, new class_623(0.5F, true), new class_623(1.0F, true)));
	}

	protected class_2960 method_4093(class_1590 arg) {
		return field_4759;
	}
}
