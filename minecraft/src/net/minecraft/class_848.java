package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_848 extends class_851 {
	private final int field_4449 = class_311.method_1593(class_1921.values().length);

	public class_848(class_1937 arg, class_761 arg2) {
		super(arg, arg2);
	}

	public int method_3639(class_1921 arg, class_849 arg2) {
		return !arg2.method_3641(arg) ? this.field_4449 + arg.ordinal() : -1;
	}

	@Override
	public void method_3659() {
		super.method_3659();
		class_311.method_1594(this.field_4449, class_1921.values().length);
	}
}
