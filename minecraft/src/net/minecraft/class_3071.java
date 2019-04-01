package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class class_3071 extends class_3145<class_3111> {
	public class_3071(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	@Override
	public String method_14019() {
		return "Igloo";
	}

	@Override
	public int method_14021() {
		return 3;
	}

	@Override
	public class_3195.class_3774 method_14016() {
		return class_3071.class_3072::new;
	}

	@Override
	protected int method_13774() {
		return 14357618;
	}

	public static class class_3072 extends class_3449 {
		public class_3072(class_3195<?> arg, int i, int j, class_1959 arg2, class_3341 arg3, int k, long l) {
			super(arg, i, j, arg2, arg3, k, l);
		}

		@Override
		public void method_16655(class_2794<?> arg, class_3485 arg2, int i, int j, class_1959 arg3) {
			class_3111 lv = arg.method_12105(arg3, class_3031.field_13527);
			int k = i * 16;
			int l = j * 16;
			class_2338 lv2 = new class_2338(k, 90, l);
			class_2470 lv3 = class_2470.values()[this.field_16715.nextInt(class_2470.values().length)];
			class_3351.method_14705(arg2, lv2, lv3, this.field_15325, this.field_16715, lv);
			this.method_14969();
		}
	}
}
