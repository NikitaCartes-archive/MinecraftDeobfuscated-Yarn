package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1775 extends class_1789 {
	public class_1775(int i, float f, boolean bl, class_1792.class_1793 arg) {
		super(i, f, bl, arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_7886(class_1799 arg) {
		return true;
	}

	@Override
	protected void method_7831(class_1799 arg, class_1937 arg2, class_1657 arg3) {
		if (!arg2.field_9236) {
			arg3.method_6092(new class_1293(class_1294.field_5924, 400, 1));
			arg3.method_6092(new class_1293(class_1294.field_5907, 6000, 0));
			arg3.method_6092(new class_1293(class_1294.field_5918, 6000, 0));
			arg3.method_6092(new class_1293(class_1294.field_5898, 2400, 3));
		}
	}
}
