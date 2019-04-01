package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class class_3411 extends class_3145<class_3114> {
	public class_3411(Function<Dynamic<?>, ? extends class_3114> function) {
		super(function);
	}

	@Override
	public String method_14019() {
		return "Ocean_Ruin";
	}

	@Override
	public int method_14021() {
		return 3;
	}

	@Override
	protected int method_13773(class_2794<?> arg) {
		return arg.method_12109().method_12564();
	}

	@Override
	protected int method_13775(class_2794<?> arg) {
		return arg.method_12109().method_12555();
	}

	@Override
	public class_3195.class_3774 method_14016() {
		return class_3411.class_3412::new;
	}

	@Override
	protected int method_13774() {
		return 14357621;
	}

	public static class class_3412 extends class_3449 {
		public class_3412(class_3195<?> arg, int i, int j, class_1959 arg2, class_3341 arg3, int k, long l) {
			super(arg, i, j, arg2, arg3, k, l);
		}

		@Override
		public void method_16655(class_2794<?> arg, class_3485 arg2, int i, int j, class_1959 arg3) {
			class_3114 lv = arg.method_12105(arg3, class_3031.field_13536);
			int k = i * 16;
			int l = j * 16;
			class_2338 lv2 = new class_2338(k, 90, l);
			class_2470 lv3 = class_2470.values()[this.field_16715.nextInt(class_2470.values().length)];
			class_3409.method_14827(arg2, lv2, lv3, this.field_15325, this.field_16715, lv);
			this.method_14969();
		}
	}

	public static enum class_3413 {
		field_14532("warm"),
		field_14528("cold");

		private static final Map<String, class_3411.class_3413> field_14530 = (Map<String, class_3411.class_3413>)Arrays.stream(values())
			.collect(Collectors.toMap(class_3411.class_3413::method_14831, arg -> arg));
		private final String field_14529;

		private class_3413(String string2) {
			this.field_14529 = string2;
		}

		public String method_14831() {
			return this.field_14529;
		}

		public static class_3411.class_3413 method_14830(String string) {
			return (class_3411.class_3413)field_14530.get(string);
		}
	}
}
