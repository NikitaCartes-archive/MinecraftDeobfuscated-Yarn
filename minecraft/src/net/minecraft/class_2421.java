package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2421 extends class_2261 {
	public static final class_2758 field_11306 = class_2741.field_12497;
	private static final class_265[] field_11305 = new class_265[]{
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 5.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 11.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 14.0, 16.0)
	};

	protected class_2421(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11306, Integer.valueOf(0)));
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return field_11305[arg.method_11654(field_11306)];
	}

	@Override
	protected boolean method_9695(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return arg.method_11614() == class_2246.field_10114;
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		int i = (Integer)arg.method_11654(field_11306);
		if (i < 3 && random.nextInt(10) == 0) {
			arg = arg.method_11657(field_11306, Integer.valueOf(i + 1));
			arg2.method_8652(arg3, arg, 2);
		}

		super.method_9588(arg, arg2, arg3, random);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_9574(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		return new class_1799(class_1802.field_8790);
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11306);
	}
}
