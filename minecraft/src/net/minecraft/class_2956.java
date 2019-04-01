package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_2956 extends class_3195<class_2959> {
	public class_2956(Function<Dynamic<?>, ? extends class_2959> function) {
		super(function);
	}

	@Override
	public boolean method_14026(class_2794<?> arg, Random random, int i, int j) {
		class_1959 lv = arg.method_12098().method_8758(new class_2338((i << 4) + 9, 0, (j << 4) + 9));
		if (arg.method_12097(lv, class_3031.field_13538)) {
			((class_2919)random).method_12665(arg.method_12101(), i, j, 10387320);
			class_2959 lv2 = arg.method_12105(lv, class_3031.field_13538);
			return random.nextFloat() < lv2.field_13352;
		} else {
			return false;
		}
	}

	@Override
	public class_3195.class_3774 method_14016() {
		return class_2956.class_2957::new;
	}

	@Override
	public String method_14019() {
		return "Buried_Treasure";
	}

	@Override
	public int method_14021() {
		return 1;
	}

	public static class class_2957 extends class_3449 {
		public class_2957(class_3195<?> arg, int i, int j, class_1959 arg2, class_3341 arg3, int k, long l) {
			super(arg, i, j, arg2, arg3, k, l);
		}

		@Override
		public void method_16655(class_2794<?> arg, class_3485 arg2, int i, int j, class_1959 arg3) {
			int k = i * 16;
			int l = j * 16;
			class_2338 lv = new class_2338(k + 9, 90, l + 9);
			this.field_15325.add(new class_3789.class_3339(lv));
			this.method_14969();
		}

		@Override
		public class_2338 method_14962() {
			return new class_2338((this.method_14967() << 4) + 9, 0, (this.method_14966() << 4) + 9);
		}
	}
}
