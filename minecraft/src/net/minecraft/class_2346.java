package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2346 extends class_2248 {
	public class_2346(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		arg2.method_8397().method_8676(arg3, this, this.method_9563(arg2));
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		arg4.method_8397().method_8676(arg5, this, this.method_9563(arg4));
		return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!arg2.field_9236) {
			this.method_10131(arg2, arg3);
		}
	}

	private void method_10131(class_1937 arg, class_2338 arg2) {
		if (method_10128(arg.method_8320(arg2.method_10074())) && arg2.method_10264() >= 0) {
			if (!arg.field_9236) {
				class_1540 lv = new class_1540(
					arg, (double)arg2.method_10263() + 0.5, (double)arg2.method_10264(), (double)arg2.method_10260() + 0.5, arg.method_8320(arg2)
				);
				this.method_10132(lv);
				arg.method_8649(lv);
			}
		}
	}

	protected void method_10132(class_1540 arg) {
	}

	@Override
	public int method_9563(class_1941 arg) {
		return 2;
	}

	public static boolean method_10128(class_2680 arg) {
		class_2248 lv = arg.method_11614();
		class_3614 lv2 = arg.method_11620();
		return arg.method_11588() || lv == class_2246.field_10036 || lv2.method_15797() || lv2.method_15800();
	}

	public void method_10127(class_1937 arg, class_2338 arg2, class_2680 arg3, class_2680 arg4) {
	}

	public void method_10129(class_1937 arg, class_2338 arg2) {
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (random.nextInt(16) == 0) {
			class_2338 lv = arg3.method_10074();
			if (method_10128(arg2.method_8320(lv))) {
				double d = (double)((float)arg3.method_10263() + random.nextFloat());
				double e = (double)arg3.method_10264() - 0.05;
				double f = (double)((float)arg3.method_10260() + random.nextFloat());
				arg2.method_8406(new class_2388(class_2398.field_11206, arg), d, e, f, 0.0, 0.0, 0.0);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public int method_10130(class_2680 arg) {
		return -16777216;
	}
}
