package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2496 extends class_2237 {
	protected class_2496(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2636();
	}

	@Override
	public void method_9565(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1799 arg4) {
		super.method_9565(arg, arg2, arg3, arg4);
		int i = 15 + arg2.field_9229.nextInt(15) + arg2.field_9229.nextInt(15);
		this.method_9583(arg2, arg3, i);
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11458;
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_9574(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		return class_1799.field_8037;
	}
}
