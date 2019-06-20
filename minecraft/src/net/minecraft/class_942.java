package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_942 extends class_927<class_1614, class_604<class_1614>> {
	private static final class_2960 field_4779 = new class_2960("textures/entity/silverfish.png");

	public class_942(class_898 arg) {
		super(arg, new class_604<>(), 0.3F);
	}

	protected float method_4107(class_1614 arg) {
		return 180.0F;
	}

	protected class_2960 method_4108(class_1614 arg) {
		return field_4779;
	}
}
