package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1779 extends class_1792 {
	public class_1779(class_1792.class_1793 arg) {
		super(arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_7886(class_1799 arg) {
		return true;
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = arg2.method_5998(arg3);
		if (!arg2.field_7503.field_7477) {
			lv.method_7934(1);
		}

		arg.method_8465(
			null, arg2.field_5987, arg2.field_6010, arg2.field_6035, class_3417.field_14637, class_3419.field_15254, 0.5F, 0.4F / (field_8005.nextFloat() * 0.4F + 0.8F)
		);
		if (!arg.field_9236) {
			class_1683 lv2 = new class_1683(arg, arg2);
			lv2.method_16940(lv);
			lv2.method_7489(arg2, arg2.field_5965, arg2.field_6031, -20.0F, 0.7F, 1.0F);
			arg.method_8649(lv2);
		}

		arg2.method_7259(class_3468.field_15372.method_14956(this));
		return new class_1271<>(class_1269.field_5812, lv);
	}
}
