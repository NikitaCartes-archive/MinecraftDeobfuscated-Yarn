package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_954 extends class_876<class_1667> {
	public static final class_2960 field_4795 = new class_2960("textures/entity/projectiles/arrow.png");
	public static final class_2960 field_4794 = new class_2960("textures/entity/projectiles/tipped_arrow.png");

	public class_954(class_898 arg) {
		super(arg);
	}

	protected class_2960 method_4130(class_1667 arg) {
		return arg.method_7460() > 0 ? field_4794 : field_4795;
	}
}
