package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2418 extends class_2500 {
	public class_2418(class_2248.class_2251 arg) {
		super(arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		super.method_9496(arg, arg2, arg3, random);
		if (random.nextInt(10) == 0) {
			arg2.method_8406(
				class_2398.field_11219,
				(double)((float)arg3.method_10263() + random.nextFloat()),
				(double)((float)arg3.method_10264() + 1.1F),
				(double)((float)arg3.method_10260() + random.nextFloat()),
				0.0,
				0.0,
				0.0
			);
		}
	}
}
