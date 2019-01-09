package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class class_94 extends class_120 {
	private static final Map<class_2960, class_94.class_97> field_1010 = Maps.<class_2960, class_94.class_97>newHashMap();
	private final class_1887 field_1011;
	private final class_94.class_96 field_1009;

	private class_94(class_209[] args, class_1887 arg, class_94.class_96 arg2) {
		super(args);
		this.field_1011 = arg;
		this.field_1009 = arg2;
	}

	@Override
	public Set<class_169<?>> method_293() {
		return ImmutableSet.of(class_181.field_1229);
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		class_1799 lv = arg2.method_296(class_181.field_1229);
		if (lv != null) {
			int i = class_1890.method_8225(this.field_1011, lv);
			int j = this.field_1009.method_467(arg2.method_294(), arg.method_7947(), i);
			arg.method_7939(j);
		}

		return arg;
	}

	public static class_120.class_121<?> method_463(class_1887 arg, float f, int i) {
		return method_520(args -> new class_94(args, arg, new class_94.class_95(i, f)));
	}

	public static class_120.class_121<?> method_455(class_1887 arg) {
		return method_520(args -> new class_94(args, arg, new class_94.class_98()));
	}

	public static class_120.class_121<?> method_456(class_1887 arg) {
		return method_520(args -> new class_94(args, arg, new class_94.class_100(1)));
	}

	public static class_120.class_121<?> method_461(class_1887 arg, int i) {
		return method_520(args -> new class_94(args, arg, new class_94.class_100(i)));
	}

	static {
		field_1010.put(class_94.class_95.field_1013, class_94.class_95::method_464);
		field_1010.put(class_94.class_98.field_1015, class_94.class_98::method_468);
		field_1010.put(class_94.class_100.field_1016, class_94.class_100::method_471);
	}

	static final class class_100 implements class_94.class_96 {
		public static final class_2960 field_1016 = new class_2960("uniform_bonus_count");
		private final int field_1017;

		public class_100(int i) {
			this.field_1017 = i;
		}

		@Override
		public int method_467(Random random, int i, int j) {
			return i + random.nextInt(this.field_1017 * j + 1);
		}

		@Override
		public void method_465(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("bonusMultiplier", this.field_1017);
		}

		public static class_94.class_96 method_471(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			int i = class_3518.method_15260(jsonObject, "bonusMultiplier");
			return new class_94.class_100(i);
		}

		@Override
		public class_2960 method_466() {
			return field_1016;
		}
	}

	static final class class_95 implements class_94.class_96 {
		public static final class_2960 field_1013 = new class_2960("binomial_with_bonus_count");
		private final int field_1014;
		private final float field_1012;

		public class_95(int i, float f) {
			this.field_1014 = i;
			this.field_1012 = f;
		}

		@Override
		public int method_467(Random random, int i, int j) {
			for (int k = 0; k < j + this.field_1014; k++) {
				if (random.nextFloat() < this.field_1012) {
					i++;
				}
			}

			return i;
		}

		@Override
		public void method_465(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("extra", this.field_1014);
			jsonObject.addProperty("probability", this.field_1012);
		}

		public static class_94.class_96 method_464(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			int i = class_3518.method_15260(jsonObject, "extra");
			float f = class_3518.method_15259(jsonObject, "probability");
			return new class_94.class_95(i, f);
		}

		@Override
		public class_2960 method_466() {
			return field_1013;
		}
	}

	interface class_96 {
		int method_467(Random random, int i, int j);

		void method_465(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext);

		class_2960 method_466();
	}

	interface class_97 {
		class_94.class_96 deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext);
	}

	static final class class_98 implements class_94.class_96 {
		public static final class_2960 field_1015 = new class_2960("ore_drops");

		private class_98() {
		}

		@Override
		public int method_467(Random random, int i, int j) {
			if (j > 0) {
				int k = random.nextInt(j + 2) - 1;
				if (k < 0) {
					k = 0;
				}

				return i * (k + 1);
			} else {
				return i;
			}
		}

		@Override
		public void method_465(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
		}

		public static class_94.class_96 method_468(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			return new class_94.class_98();
		}

		@Override
		public class_2960 method_466() {
			return field_1015;
		}
	}

	public static class class_99 extends class_120.class_123<class_94> {
		public class_99() {
			super(new class_2960("apply_bonus"), class_94.class);
		}

		public void method_469(JsonObject jsonObject, class_94 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, arg, jsonSerializationContext);
			jsonObject.addProperty("enchantment", class_2378.field_11160.method_10221(arg.field_1011).toString());
			jsonObject.addProperty("formula", arg.field_1009.method_466().toString());
			JsonObject jsonObject2 = new JsonObject();
			arg.field_1009.method_465(jsonObject2, jsonSerializationContext);
			if (jsonObject2.size() > 0) {
				jsonObject.add("parameters", jsonObject2);
			}
		}

		public class_94 method_470(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			class_2960 lv = new class_2960(class_3518.method_15265(jsonObject, "enchantment"));
			class_1887 lv2 = class_2378.field_11160.method_10223(lv);
			if (lv2 == null) {
				throw new JsonParseException("Invalid enchantment id: " + lv);
			} else {
				class_2960 lv3 = new class_2960(class_3518.method_15265(jsonObject, "formula"));
				class_94.class_97 lv4 = (class_94.class_97)class_94.field_1010.get(lv3);
				if (lv4 == null) {
					throw new JsonParseException("Invalid formula id: " + lv3);
				} else {
					class_94.class_96 lv5;
					if (jsonObject.has("parameters")) {
						lv5 = lv4.deserialize(class_3518.method_15296(jsonObject, "parameters"), jsonDeserializationContext);
					} else {
						lv5 = lv4.deserialize(new JsonObject(), jsonDeserializationContext);
					}

					return new class_94(args, lv2, lv5);
				}
			}
		}
	}
}
