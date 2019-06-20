package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2242 extends class_2302 {
	public static final class_2758 field_9962 = class_2741.field_12497;
	private static final class_265[] field_9961 = new class_265[]{
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 8.0, 16.0)
	};

	public class_2242(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_2758 method_9824() {
		return field_9962;
	}

	@Override
	public int method_9827() {
		return 3;
	}

	@Environment(EnvType.CLIENT)
	@Override
	protected class_1935 method_9832() {
		return class_1802.field_8309;
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (random.nextInt(3) != 0) {
			super.method_9588(arg, arg2, arg3, random);
		}
	}

	@Override
	protected int method_9831(class_1937 arg) {
		return super.method_9831(arg) / 3;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_9962);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_9961[arg.method_11654(this.method_9824())];
	}
}
