package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_939 extends class_927<class_1463, class_596<class_1463>> {
	private static final class_2960 field_4770 = new class_2960("textures/entity/rabbit/brown.png");
	private static final class_2960 field_4773 = new class_2960("textures/entity/rabbit/white.png");
	private static final class_2960 field_4775 = new class_2960("textures/entity/rabbit/black.png");
	private static final class_2960 field_4768 = new class_2960("textures/entity/rabbit/gold.png");
	private static final class_2960 field_4774 = new class_2960("textures/entity/rabbit/salt.png");
	private static final class_2960 field_4772 = new class_2960("textures/entity/rabbit/white_splotched.png");
	private static final class_2960 field_4771 = new class_2960("textures/entity/rabbit/toast.png");
	private static final class_2960 field_4769 = new class_2960("textures/entity/rabbit/caerbannog.png");

	public class_939(class_898 arg) {
		super(arg, new class_596<>(), 0.3F);
	}

	protected class_2960 method_4102(class_1463 arg) {
		String string = class_124.method_539(arg.method_5477().getString());
		if (string != null && "Toast".equals(string)) {
			return field_4771;
		} else {
			switch (arg.method_6610()) {
				case 0:
				default:
					return field_4770;
				case 1:
					return field_4773;
				case 2:
					return field_4775;
				case 3:
					return field_4772;
				case 4:
					return field_4768;
				case 5:
					return field_4774;
				case 99:
					return field_4769;
			}
		}
	}
}
