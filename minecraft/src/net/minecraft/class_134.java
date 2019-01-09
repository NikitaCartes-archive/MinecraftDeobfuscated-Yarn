package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class class_134 extends class_120 {
	private final List<class_79> field_1103;

	private class_134(class_209[] args, List<class_79> list) {
		super(args);
		this.field_1103 = ImmutableList.copyOf(list);
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		if (arg.method_7960()) {
			return arg;
		} else {
			class_2371<class_1799> lv = class_2371.method_10211();
			this.field_1103.forEach(arg3 -> arg3.expand(arg2, arg3x -> arg3x.method_426(class_52.method_332(lv::add), arg2)));
			class_2487 lv2 = new class_2487();
			class_1262.method_5426(lv2, lv);
			class_2487 lv3 = arg.method_7948();
			lv3.method_10566("BlockEntityTag", lv2.method_10543(lv3.method_10562("BlockEntityTag")));
			return arg;
		}
	}

	@Override
	public void method_292(class_58 arg, Function<class_2960, class_52> function, Set<class_2960> set, class_176 arg2) {
		super.method_292(arg, function, set, arg2);

		for (int i = 0; i < this.field_1103.size(); i++) {
			((class_79)this.field_1103.get(i)).method_415(arg.method_364(".entry[" + i + "]"), function, set, arg2);
		}
	}

	public static class_134.class_135 method_601() {
		return new class_134.class_135();
	}

	public static class class_135 extends class_120.class_121<class_134.class_135> {
		private final List<class_79> field_1104 = Lists.<class_79>newArrayList();

		protected class_134.class_135 method_603() {
			return this;
		}

		public class_134.class_135 method_602(class_79.class_80<?> arg) {
			this.field_1104.add(arg.method_419());
			return this;
		}

		@Override
		public class_117 method_515() {
			return new class_134(this.method_526(), this.field_1104);
		}
	}

	public static class class_136 extends class_120.class_123<class_134> {
		protected class_136() {
			super(new class_2960("set_contents"), class_134.class);
		}

		public void method_604(JsonObject jsonObject, class_134 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, arg, jsonSerializationContext);
			jsonObject.add("entries", jsonSerializationContext.serialize(arg.field_1103));
		}

		public class_134 method_605(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			class_79[] lvs = class_3518.method_15272(jsonObject, "entries", jsonDeserializationContext, class_79[].class);
			return new class_134(args, Arrays.asList(lvs));
		}
	}
}
