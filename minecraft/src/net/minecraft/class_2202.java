package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2202 extends class_2248 implements class_2256 {
	protected static final class_265 field_9897 = class_2248.method_9541(4.0, 0.0, 4.0, 12.0, 12.0, 12.0);

	public class_2202(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_2248.class_2250 method_16841() {
		return class_2248.class_2250.field_10657;
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		class_243 lv = arg.method_11599(arg2, arg3);
		return field_9897.method_1096(lv.field_1352, lv.field_1351, lv.field_1350);
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (random.nextInt(3) == 0 && arg2.method_8623(arg3.method_10084()) && arg2.method_8624(arg3.method_10084(), 0) >= 9) {
			this.method_9351(arg2, arg3);
		}
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		return arg2.method_8320(arg3.method_10074()).method_11602(class_3481.field_15497);
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (!arg.method_11591(arg4, arg5)) {
			return class_2246.field_10124.method_9564();
		} else {
			if (arg2 == class_2350.field_11036 && arg3.method_11614() == class_2246.field_10211) {
				arg4.method_8652(arg5, class_2246.field_10211.method_9564(), 2);
			}

			return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_9574(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		return new class_1799(class_1802.field_8648);
	}

	@Override
	public boolean method_9651(class_1922 arg, class_2338 arg2, class_2680 arg3, boolean bl) {
		return arg.method_8320(arg2.method_10084()).method_11588();
	}

	@Override
	public boolean method_9650(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		return true;
	}

	@Override
	public void method_9652(class_1937 arg, Random random, class_2338 arg2, class_2680 arg3) {
		this.method_9351(arg, arg2);
	}

	@Override
	public float method_9594(class_2680 arg, class_1657 arg2, class_1922 arg3, class_2338 arg4) {
		return arg2.method_6047().method_7909() instanceof class_1829 ? 1.0F : super.method_9594(arg, arg2, arg3, arg4);
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	protected void method_9351(class_1937 arg, class_2338 arg2) {
		arg.method_8652(arg2.method_10084(), class_2246.field_10211.method_9564().method_11657(class_2211.field_9917, class_2737.field_12466), 3);
	}
}
