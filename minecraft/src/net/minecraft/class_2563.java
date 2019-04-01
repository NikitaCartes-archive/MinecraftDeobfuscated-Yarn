package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2563 extends class_2356 {
	public class_2563(class_1291 arg, class_2248.class_2251 arg2) {
		super(arg, 8, arg2);
	}

	@Override
	protected boolean method_9695(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		class_2248 lv = arg.method_11614();
		return super.method_9695(arg, arg2, arg3) || lv == class_2246.field_10515 || lv == class_2246.field_10114;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		class_265 lv = this.method_9530(arg, arg2, arg3, class_3726.method_16194());
		class_243 lv2 = lv.method_1107().method_1005();
		double d = (double)arg3.method_10263() + lv2.field_1352;
		double e = (double)arg3.method_10260() + lv2.field_1350;

		for (int i = 0; i < 3; i++) {
			if (random.nextBoolean()) {
				arg2.method_8406(
					class_2398.field_11251,
					d + (double)(random.nextFloat() / 5.0F),
					(double)arg3.method_10264() + (0.5 - (double)random.nextFloat()),
					e + (double)(random.nextFloat() / 5.0F),
					0.0,
					0.0,
					0.0
				);
			}
		}
	}

	@Override
	public void method_9548(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1297 arg4) {
		if (!arg2.field_9236 && arg2.method_8407() != class_1267.field_5801) {
			if (arg4 instanceof class_1309) {
				class_1309 lv = (class_1309)arg4;
				if (!lv.method_5679(class_1282.field_5850)) {
					lv.method_6092(new class_1293(class_1294.field_5920, 40));
				}
			}
		}
	}
}
