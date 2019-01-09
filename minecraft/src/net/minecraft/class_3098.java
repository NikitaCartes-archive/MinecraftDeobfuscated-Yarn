package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class class_3098 extends class_3195<class_3101> {
	public class_3098(Function<Dynamic<?>, ? extends class_3101> function) {
		super(function);
	}

	@Override
	public boolean method_14026(class_2794<?> arg, Random random, int i, int j) {
		((class_2919)random).method_12663(arg.method_12101(), i, j);
		class_1959 lv = arg.method_12098().method_8758(new class_2338((i << 4) + 9, 0, (j << 4) + 9));
		if (arg.method_12097(lv, class_3031.field_13547)) {
			class_3101 lv2 = arg.method_12105(lv, class_3031.field_13547);
			double d = lv2.field_13693;
			return random.nextDouble() < d;
		} else {
			return false;
		}
	}

	@Override
	public class_3195.class_3774 method_14016() {
		return class_3098.class_3099::new;
	}

	@Override
	public String method_14019() {
		return "Mineshaft";
	}

	@Override
	public int method_14021() {
		return 8;
	}

	public static class class_3099 extends class_3449 {
		public class_3099(class_3195<?> arg, int i, int j, class_1959 arg2, class_3341 arg3, int k, long l) {
			super(arg, i, j, arg2, arg3, k, l);
		}

		@Override
		public void method_16655(class_2794<?> arg, class_3485 arg2, int i, int j, class_1959 arg3) {
			class_3101 lv = arg.method_12105(arg3, class_3031.field_13547);
			class_3353.class_3357 lv2 = new class_3353.class_3357(0, this.field_16715, (i << 4) + 2, (j << 4) + 2, lv.field_13694);
			this.field_15325.add(lv2);
			lv2.method_14918(lv2, this.field_15325, this.field_16715);
			this.method_14969();
			if (lv.field_13694 == class_3098.class_3100.field_13691) {
				int k = -5;
				int l = arg.method_16398() - this.field_15330.field_14377 + this.field_15330.method_14663() / 2 - -5;
				this.field_15330.method_14661(0, l, 0);

				for (class_3443 lv3 : this.field_15325) {
					lv3.method_14922(0, l, 0);
				}
			} else {
				this.method_14978(arg.method_16398(), this.field_16715, 10);
			}
		}
	}

	public static enum class_3100 {
		field_13692("normal"),
		field_13691("mesa");

		private static final Map<String, class_3098.class_3100> field_13690 = (Map<String, class_3098.class_3100>)Arrays.stream(values())
			.collect(Collectors.toMap(class_3098.class_3100::method_13534, arg -> arg));
		private final String field_13689;

		private class_3100(String string2) {
			this.field_13689 = string2;
		}

		public String method_13534() {
			return this.field_13689;
		}

		public static class_3098.class_3100 method_13532(String string) {
			return (class_3098.class_3100)field_13690.get(string);
		}

		public static class_3098.class_3100 method_13535(int i) {
			return i >= 0 && i < values().length ? values()[i] : field_13692;
		}
	}
}
