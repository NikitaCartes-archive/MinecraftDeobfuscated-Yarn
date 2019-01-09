package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2329 extends class_2237 {
	protected class_2329(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2643();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		class_2586 lv = arg2.method_8321(arg3);
		if (lv instanceof class_2643) {
			int i = ((class_2643)lv).method_11415();

			for (int j = 0; j < i; j++) {
				double d = (double)((float)arg3.method_10263() + random.nextFloat());
				double e = (double)((float)arg3.method_10264() + random.nextFloat());
				double f = (double)((float)arg3.method_10260() + random.nextFloat());
				double g = ((double)random.nextFloat() - 0.5) * 0.5;
				double h = ((double)random.nextFloat() - 0.5) * 0.5;
				double k = ((double)random.nextFloat() - 0.5) * 0.5;
				int l = random.nextInt(2) * 2 - 1;
				if (random.nextBoolean()) {
					f = (double)arg3.method_10260() + 0.5 + 0.25 * (double)l;
					k = (double)(random.nextFloat() * 2.0F * (float)l);
				} else {
					d = (double)arg3.method_10263() + 0.5 + 0.25 * (double)l;
					g = (double)(random.nextFloat() * 2.0F * (float)l);
				}

				arg2.method_8406(class_2398.field_11214, d, e, f, g, h, k);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_9574(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		return class_1799.field_8037;
	}
}
