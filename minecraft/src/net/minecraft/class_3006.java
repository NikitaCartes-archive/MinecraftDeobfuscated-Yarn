package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class class_3006 extends class_3145<class_3111> {
	public class_3006(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	@Override
	public String method_14019() {
		return "Desert_Pyramid";
	}

	@Override
	public int method_14021() {
		return 3;
	}

	@Override
	public class_3195.class_3774 method_14016() {
		return class_3006.class_3007::new;
	}

	@Override
	protected int method_13774() {
		return 14357617;
	}

	public static class class_3007 extends class_3449 {
		public class_3007(class_3195<?> arg, int i, int j, class_1959 arg2, class_3341 arg3, int k, long l) {
			super(arg, i, j, arg2, arg3, k, l);
		}

		@Override
		public void method_16655(class_2794<?> arg, class_3485 arg2, int i, int j, class_1959 arg3) {
			class_3346 lv = new class_3346(this.field_16715, i * 16, j * 16);
			this.field_15325.add(lv);
			this.method_14969();
		}
	}
}
