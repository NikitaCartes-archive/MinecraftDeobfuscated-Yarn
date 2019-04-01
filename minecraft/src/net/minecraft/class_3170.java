package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class class_3170 extends class_3145<class_3172> {
	public class_3170(Function<Dynamic<?>, ? extends class_3172> function) {
		super(function);
	}

	@Override
	public String method_14019() {
		return "Shipwreck";
	}

	@Override
	public int method_14021() {
		return 3;
	}

	@Override
	public class_3195.class_3774 method_14016() {
		return class_3170.class_3171::new;
	}

	@Override
	protected int method_13774() {
		return 165745295;
	}

	@Override
	protected int method_13773(class_2794<?> arg) {
		return arg.method_12109().method_12566();
	}

	@Override
	protected int method_13775(class_2794<?> arg) {
		return arg.method_12109().method_12562();
	}

	public static class class_3171 extends class_3449 {
		public class_3171(class_3195<?> arg, int i, int j, class_1959 arg2, class_3341 arg3, int k, long l) {
			super(arg, i, j, arg2, arg3, k, l);
		}

		@Override
		public void method_16655(class_2794<?> arg, class_3485 arg2, int i, int j, class_1959 arg3) {
			class_3172 lv = arg.method_12105(arg3, class_3031.field_13589);
			class_2470 lv2 = class_2470.values()[this.field_16715.nextInt(class_2470.values().length)];
			class_2338 lv3 = new class_2338(i * 16, 90, j * 16);
			class_3415.method_14834(arg2, lv3, lv2, this.field_15325, this.field_16715, lv);
			this.method_14969();
		}
	}
}
