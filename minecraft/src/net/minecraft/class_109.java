package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_109 extends class_120 {
	private static final Logger field_1031 = LogManager.getLogger();
	private final List<class_1887> field_1030;

	private class_109(class_209[] args, Collection<class_1887> collection) {
		super(args);
		this.field_1030 = ImmutableList.copyOf(collection);
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		Random random = arg2.method_294();
		class_1887 lv2;
		if (this.field_1030.isEmpty()) {
			List<class_1887> list = Lists.<class_1887>newArrayList();

			for (class_1887 lv : class_2378.field_11160) {
				if (arg.method_7909() == class_1802.field_8529 || lv.method_8192(arg)) {
					list.add(lv);
				}
			}

			if (list.isEmpty()) {
				field_1031.warn("Couldn't find a compatible enchantment for {}", arg);
				return arg;
			}

			lv2 = (class_1887)list.get(random.nextInt(list.size()));
		} else {
			lv2 = (class_1887)this.field_1030.get(random.nextInt(this.field_1030.size()));
		}

		int i = class_3532.method_15395(random, lv2.method_8187(), lv2.method_8183());
		if (arg.method_7909() == class_1802.field_8529) {
			arg = new class_1799(class_1802.field_8598);
			class_1772.method_7807(arg, new class_1889(lv2, i));
		} else {
			arg.method_7978(lv2, i);
		}

		return arg;
	}

	public static class_120.class_121<?> method_489() {
		return method_520(args -> new class_109(args, ImmutableList.<class_1887>of()));
	}

	public static class class_110 extends class_120.class_123<class_109> {
		public class_110() {
			super(new class_2960("enchant_randomly"), class_109.class);
		}

		public void method_491(JsonObject jsonObject, class_109 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, arg, jsonSerializationContext);
			if (!arg.field_1030.isEmpty()) {
				JsonArray jsonArray = new JsonArray();

				for (class_1887 lv : arg.field_1030) {
					class_2960 lv2 = class_2378.field_11160.method_10221(lv);
					if (lv2 == null) {
						throw new IllegalArgumentException("Don't know how to serialize enchantment " + lv);
					}

					jsonArray.add(new JsonPrimitive(lv2.toString()));
				}

				jsonObject.add("enchantments", jsonArray);
			}
		}

		public class_109 method_490(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			List<class_1887> list = Lists.<class_1887>newArrayList();
			if (jsonObject.has("enchantments")) {
				for (JsonElement jsonElement : class_3518.method_15261(jsonObject, "enchantments")) {
					String string = class_3518.method_15287(jsonElement, "enchantment");
					class_1887 lv = class_2378.field_11160.method_10223(new class_2960(string));
					if (lv == null) {
						throw new JsonSyntaxException("Unknown enchantment '" + string + "'");
					}

					list.add(lv);
				}
			}

			return new class_109(args, list);
		}
	}
}
