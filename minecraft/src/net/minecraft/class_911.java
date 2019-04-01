package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_911 extends class_927<class_1584, class_571> {
	private static final class_2960 field_4715 = new class_2960("textures/entity/illager/ravager.png");

	public class_911(class_898 arg) {
		super(arg, new class_571(), 1.1F);
	}

	protected class_2960 method_3984(class_1584 arg) {
		return field_4715;
	}
}
