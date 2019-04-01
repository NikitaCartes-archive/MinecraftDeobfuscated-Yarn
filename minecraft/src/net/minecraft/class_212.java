package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;

public class class_212 implements class_209 {
	private final class_2248 field_1286;
	private final Map<class_2769<?>, Object> field_1288;
	private final Predicate<class_2680> field_1287;

	private class_212(class_2248 arg, Map<class_2769<?>, Object> map) {
		this.field_1286 = arg;
		this.field_1288 = ImmutableMap.copyOf(map);
		this.field_1287 = method_898(arg, map);
	}

	private static Predicate<class_2680> method_898(class_2248 arg, Map<class_2769<?>, Object> map) {
		int i = map.size();
		if (i == 0) {
			return arg2 -> arg2.method_11614() == arg;
		} else if (i == 1) {
			Entry<class_2769<?>, Object> entry = (Entry<class_2769<?>, Object>)map.entrySet().iterator().next();
			class_2769<?> lv = (class_2769<?>)entry.getKey();
			Object object = entry.getValue();
			return arg3 -> arg3.method_11614() == arg && object.equals(arg3.method_11654(lv));
		} else {
			Predicate<class_2680> predicate = arg2 -> arg2.method_11614() == arg;

			for (Entry<class_2769<?>, Object> entry2 : map.entrySet()) {
				class_2769<?> lv2 = (class_2769<?>)entry2.getKey();
				Object object2 = entry2.getValue();
				predicate = predicate.and(arg2 -> object2.equals(arg2.method_11654(lv2)));
			}

			return predicate;
		}
	}

	@Override
	public Set<class_169<?>> method_293() {
		return ImmutableSet.of(class_181.field_1224);
	}

	public boolean method_899(class_47 arg) {
		class_2680 lv = arg.method_296(class_181.field_1224);
		return lv != null && this.field_1287.test(lv);
	}

	public static class_212.class_213 method_900(class_2248 arg) {
		return new class_212.class_213(arg);
	}

	public static class class_213 implements class_209.class_210 {
		private final class_2248 field_1290;
		private final Set<class_2769<?>> field_1289;
		private final Map<class_2769<?>, Object> field_1291 = Maps.<class_2769<?>, Object>newHashMap();

		public class_213(class_2248 arg) {
			this.field_1290 = arg;
			this.field_1289 = Sets.newIdentityHashSet();
			this.field_1289.addAll(arg.method_9595().method_11659());
		}

		public <T extends Comparable<T>> class_212.class_213 method_907(class_2769<T> arg, T comparable) {
			if (!this.field_1289.contains(arg)) {
				throw new IllegalArgumentException("Block " + class_2378.field_11146.method_10221(this.field_1290) + " does not have property '" + arg + "'");
			} else if (!arg.method_11898().contains(comparable)) {
				throw new IllegalArgumentException(
					"Block " + class_2378.field_11146.method_10221(this.field_1290) + " property '" + arg + "' does not have value '" + comparable + "'"
				);
			} else {
				this.field_1291.put(arg, comparable);
				return this;
			}
		}

		@Override
		public class_209 build() {
			return new class_212(this.field_1290, this.field_1291);
		}
	}

	public static class class_214 extends class_209.class_211<class_212> {
		private static <T extends Comparable<T>> String method_908(class_2769<T> arg, Object object) {
			return arg.method_11901((T)object);
		}

		protected class_214() {
			super(new class_2960("block_state_property"), class_212.class);
		}

		public void method_909(JsonObject jsonObject, class_212 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("block", class_2378.field_11146.method_10221(arg.field_1286).toString());
			JsonObject jsonObject2 = new JsonObject();
			arg.field_1288.forEach((argx, object) -> jsonObject2.addProperty(argx.method_11899(), method_908(argx, object)));
			jsonObject.add("properties", jsonObject2);
		}

		public class_212 method_910(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			class_2960 lv = new class_2960(class_3518.method_15265(jsonObject, "block"));
			class_2248 lv2 = (class_2248)class_2378.field_11146.method_17966(lv).orElseThrow(() -> new IllegalArgumentException("Can't find block " + lv));
			class_2689<class_2248, class_2680> lv3 = lv2.method_9595();
			Map<class_2769<?>, Object> map = Maps.<class_2769<?>, Object>newHashMap();
			if (jsonObject.has("properties")) {
				JsonObject jsonObject2 = class_3518.method_15296(jsonObject, "properties");
				jsonObject2.entrySet()
					.forEach(
						entry -> {
							String string = (String)entry.getKey();
							class_2769<?> lvx = lv3.method_11663(string);
							if (lvx == null) {
								throw new IllegalArgumentException("Block " + class_2378.field_11146.method_10221(lv2) + " does not have property '" + string + "'");
							} else {
								String string2 = class_3518.method_15287((JsonElement)entry.getValue(), "value");
								Object object = lvx.method_11900(string2)
									.orElseThrow(
										() -> new IllegalArgumentException(
												"Block " + class_2378.field_11146.method_10221(lv2) + " property '" + string + "' does not have value '" + string2 + "'"
											)
									);
								map.put(lvx, object);
							}
						}
					);
			}

			return new class_212(lv2, map);
		}
	}
}
