package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_909<T extends class_1308, M extends class_572<T>> extends class_927<T, M> {
	private static final class_2960 field_4713 = new class_2960("textures/entity/steve.png");

	public class_909(class_898 arg, M arg2, float f) {
		super(arg, arg2, f);
		this.method_4046(new class_976<>(this));
		this.method_4046(new class_979<>(this));
		this.method_4046(new class_989<>(this));
	}

	protected class_2960 method_3982(T arg) {
		return field_4713;
	}
}
