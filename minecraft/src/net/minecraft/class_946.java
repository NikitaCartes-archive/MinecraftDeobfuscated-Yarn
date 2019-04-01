package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_946 extends class_909<class_1547, class_606<class_1547>> {
	private static final class_2960 field_4785 = new class_2960("textures/entity/skeleton/skeleton.png");

	public class_946(class_898 arg) {
		super(arg, new class_606<>(), 0.5F);
		this.method_4046(new class_989<>(this));
		this.method_4046(new class_987<>(this, new class_606(0.5F, true), new class_606(1.0F, true)));
	}

	protected class_2960 method_4119(class_1547 arg) {
		return field_4785;
	}
}
