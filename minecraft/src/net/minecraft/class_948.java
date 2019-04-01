package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_948 extends class_927<class_1473, class_608<class_1473>> {
	private static final class_2960 field_4788 = new class_2960("textures/entity/snow_golem.png");

	public class_948(class_898 arg) {
		super(arg, new class_608<>(), 0.5F);
		this.method_4046(new class_996(this));
	}

	protected class_2960 method_4122(class_1473 arg) {
		return field_4788;
	}
}
