package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_926 extends class_927<class_1438, class_560<class_1438>> {
	private static final class_2960 field_4748 = new class_2960("textures/entity/cow/mooshroom.png");

	public class_926(class_898 arg) {
		super(arg, new class_560<>(), 0.7F);
		this.method_4046(new class_991<>(this));
	}

	protected class_2960 method_4066(class_1438 arg) {
		return field_4748;
	}
}
