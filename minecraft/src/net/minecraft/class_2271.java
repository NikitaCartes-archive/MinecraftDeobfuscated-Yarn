package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2271 extends class_2302 {
	private static final class_265[] field_10737 = new class_265[]{
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 5.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 7.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 9.0, 16.0)
	};

	public class_2271(class_2248.class_2251 arg) {
		super(arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	protected class_1935 method_9832() {
		return class_1802.field_8179;
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return field_10737[arg.method_11654(this.method_9824())];
	}
}
