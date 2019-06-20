package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2331 extends class_2237 {
	protected static final class_265 field_10951 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

	protected class_2331(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public boolean method_9526(class_2680 arg) {
		return true;
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_10951;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		super.method_9496(arg, arg2, arg3, random);

		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if (i > -2 && i < 2 && j == -1) {
					j = 2;
				}

				if (random.nextInt(16) == 0) {
					for (int k = 0; k <= 1; k++) {
						class_2338 lv = arg3.method_10069(i, k, j);
						if (arg2.method_8320(lv).method_11614() == class_2246.field_10504) {
							if (!arg2.method_8623(arg3.method_10069(i / 2, 0, j / 2))) {
								break;
							}

							arg2.method_8406(
								class_2398.field_11215,
								(double)arg3.method_10263() + 0.5,
								(double)arg3.method_10264() + 2.0,
								(double)arg3.method_10260() + 0.5,
								(double)((float)i + random.nextFloat()) - 0.5,
								(double)((float)k - random.nextFloat() - 1.0F),
								(double)((float)j + random.nextFloat()) - 0.5
							);
						}
					}
				}
			}
		}
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11458;
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2605();
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		if (arg2.field_9236) {
			return true;
		} else {
			arg4.method_17355(arg.method_17526(arg2, arg3));
			return true;
		}
	}

	@Nullable
	@Override
	public class_3908 method_17454(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		class_2586 lv = arg2.method_8321(arg3);
		if (lv instanceof class_2605) {
			class_2561 lv2 = ((class_1275)lv).method_5476();
			return new class_747((i, arg3x, arg4) -> new class_1718(i, arg3x, class_3914.method_17392(arg2, arg3)), lv2);
		} else {
			return null;
		}
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1309 arg4, class_1799 arg5) {
		if (arg5.method_7938()) {
			class_2586 lv = arg.method_8321(arg2);
			if (lv instanceof class_2605) {
				((class_2605)lv).method_11179(arg5.method_7964());
			}
		}
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
