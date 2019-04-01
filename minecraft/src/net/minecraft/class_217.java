package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Predicate;

public class class_217 {
	private static final Map<class_2960, class_209.class_211<?>> field_1294 = Maps.<class_2960, class_209.class_211<?>>newHashMap();
	private static final Map<Class<? extends class_209>, class_209.class_211<?>> field_1295 = Maps.<Class<? extends class_209>, class_209.class_211<?>>newHashMap();

	public static <T extends class_209> void method_926(class_209.class_211<? extends T> arg) {
		class_2960 lv = arg.method_894();
		Class<T> class_ = (Class<T>)arg.method_897();
		if (field_1294.containsKey(lv)) {
			throw new IllegalArgumentException("Can't re-register item condition name " + lv);
		} else if (field_1295.containsKey(class_)) {
			throw new IllegalArgumentException("Can't re-register item condition class " + class_.getName());
		} else {
			field_1294.put(lv, arg);
			field_1295.put(class_, arg);
		}
	}

	public static class_209.class_211<?> method_922(class_2960 arg) {
		class_209.class_211<?> lv = (class_209.class_211<?>)field_1294.get(arg);
		if (lv == null) {
			throw new IllegalArgumentException("Unknown loot item condition '" + arg + "'");
		} else {
			return lv;
		}
	}

	public static <T extends class_209> class_209.class_211<T> method_923(T arg) {
		class_209.class_211<T> lv = (class_209.class_211<T>)field_1295.get(arg.getClass());
		if (lv == null) {
			throw new IllegalArgumentException("Unknown loot item condition " + arg);
		} else {
			return lv;
		}
	}

	public static <T> Predicate<T> method_924(Predicate<T>[] predicates) {
		switch (predicates.length) {
			case 0:
				return object -> true;
			case 1:
				return predicates[0];
			case 2:
				return predicates[0].and(predicates[1]);
			default:
				return object -> {
					for (Predicate<T> predicate : predicates) {
						if (!predicate.test(object)) {
							return false;
						}
					}

					return true;
				};
		}
	}

	public static <T> Predicate<T> method_925(Predicate<T>[] predicates) {
		switch (predicates.length) {
			case 0:
				return object -> false;
			case 1:
				return predicates[0];
			case 2:
				return predicates[0].or(predicates[1]);
			default:
				return object -> {
					for (Predicate<T> predicate : predicates) {
						if (predicate.test(object)) {
							return true;
						}
					}

					return false;
				};
		}
	}

	static {
		method_926(new class_207.class_208());
		method_926(new class_186.class_188());
		method_926(new class_219.class_220());
		method_926(new class_225.class_226());
		method_926(new class_215.class_216());
		method_926(new class_221.class_222());
		method_926(new class_199.class_200());
		method_926(new class_212.class_214());
		method_926(new class_223.class_224());
		method_926(new class_182.class_183());
		method_926(new class_201.class_202());
		method_926(new class_190.class_191());
		method_926(new class_205.class_206());
		method_926(new class_227.class_228());
	}

	public static class class_218 implements JsonDeserializer<class_209>, JsonSerializer<class_209> {
		public class_209 method_930(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "condition");
			class_2960 lv = new class_2960(class_3518.method_15265(jsonObject, "condition"));

			class_209.class_211<?> lv2;
			try {
				lv2 = class_217.method_922(lv);
			} catch (IllegalArgumentException var8) {
				throw new JsonSyntaxException("Unknown condition '" + lv + "'");
			}

			return lv2.method_896(jsonObject, jsonDeserializationContext);
		}

		public JsonElement method_931(class_209 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			class_209.class_211<class_209> lv = class_217.method_923(arg);
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("condition", lv.method_894().toString());
			lv.method_895(jsonObject, arg, jsonSerializationContext);
			return jsonObject;
		}
	}
}
