package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_932 extends class_927<class_1452, class_587<class_1452>> {
	private static final class_2960 field_4755 = new class_2960("textures/entity/pig/pig.png");

	public class_932(class_898 arg) {
		super(arg, new class_587<>(), 0.7F);
		this.method_4046(new class_992(this));
	}

	protected class_2960 method_4087(class_1452 arg) {
		return field_4755;
	}
}
