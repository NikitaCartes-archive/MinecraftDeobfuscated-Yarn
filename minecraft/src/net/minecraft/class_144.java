package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Function;

public class class_144 extends class_120 {
	private final class_2960 field_1116;
	private final long field_1117;

	private class_144(class_209[] args, class_2960 arg, long l) {
		super(args);
		this.field_1116 = arg;
		this.field_1117 = l;
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		if (arg.method_7960()) {
			return arg;
		} else {
			class_2487 lv = new class_2487();
			lv.method_10582("LootTable", this.field_1116.toString());
			if (this.field_1117 != 0L) {
				lv.method_10544("LootTableSeed", this.field_1117);
			}

			arg.method_7948().method_10566("BlockEntityTag", lv);
			return arg;
		}
	}

	@Override
	public void method_292(class_58 arg, Function<class_2960, class_52> function, Set<class_2960> set, class_176 arg2) {
		if (set.contains(this.field_1116)) {
			arg.method_360("Table " + this.field_1116 + " is recursively called");
		} else {
			super.method_292(arg, function, set, arg2);
			class_52 lv = (class_52)function.apply(this.field_1116);
			if (lv == null) {
				arg.method_360("Unknown loot table called " + this.field_1116);
			} else {
				Set<class_2960> set2 = ImmutableSet.<class_2960>builder().addAll(set).add(this.field_1116).build();
				lv.method_330(arg.method_364("->{" + this.field_1116 + "}"), function, set2, arg2);
			}
		}
	}

	public static class class_145 extends class_120.class_123<class_144> {
		protected class_145() {
			super(new class_2960("set_loot_table"), class_144.class);
		}

		public void method_626(JsonObject jsonObject, class_144 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, arg, jsonSerializationContext);
			jsonObject.addProperty("name", arg.field_1116.toString());
			if (arg.field_1117 != 0L) {
				jsonObject.addProperty("seed", arg.field_1117);
			}
		}

		public class_144 method_627(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			class_2960 lv = new class_2960(class_3518.method_15265(jsonObject, "name"));
			long l = class_3518.method_15280(jsonObject, "seed", 0L);
			return new class_144(args, lv, l);
		}
	}
}
