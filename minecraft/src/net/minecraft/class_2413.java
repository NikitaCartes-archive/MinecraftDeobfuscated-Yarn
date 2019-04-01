package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2413 extends class_2248 {
	public class_2413(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public void method_9591(class_1937 arg, class_2338 arg2, class_1297 arg3) {
		if (!arg3.method_5753() && arg3 instanceof class_1309 && !class_1890.method_8216((class_1309)arg3)) {
			arg3.method_5643(class_1282.field_5858, 1.0F);
		}

		super.method_9591(arg, arg2, arg3);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int method_9546(class_2680 arg, class_1920 arg2, class_2338 arg3) {
		return 15728880;
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		class_2258.method_9657(arg2, arg3.method_10084(), true);
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (arg2 == class_2350.field_11036 && arg3.method_11614() == class_2246.field_10382) {
			arg4.method_8397().method_8676(arg5, this, this.method_9563(arg4));
		}

		return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public void method_9514(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		class_2338 lv = arg3.method_10084();
		if (arg2.method_8316(arg3).method_15767(class_3486.field_15517)) {
			arg2.method_8396(null, arg3, class_3417.field_15102, class_3419.field_15245, 0.5F, 2.6F + (arg2.field_9229.nextFloat() - arg2.field_9229.nextFloat()) * 0.8F);
			if (arg2 instanceof class_3218) {
				((class_3218)arg2)
					.method_14199(
						class_2398.field_11237, (double)lv.method_10263() + 0.5, (double)lv.method_10264() + 0.25, (double)lv.method_10260() + 0.5, 8, 0.5, 0.25, 0.5, 0.0
					);
			}
		}
	}

	@Override
	public int method_9563(class_1941 arg) {
		return 20;
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		arg2.method_8397().method_8676(arg3, this, this.method_9563(arg2));
	}

	@Override
	public boolean method_9523(class_2680 arg, class_1922 arg2, class_2338 arg3, class_1299<?> arg4) {
		return arg4.method_19946();
	}

	@Override
	public boolean method_9552(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return true;
	}
}
