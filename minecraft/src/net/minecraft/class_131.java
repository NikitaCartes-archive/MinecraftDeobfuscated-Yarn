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
import java.util.function.BiFunction;

public class class_131 {
	private static final Map<class_2960, class_117.class_119<?>> field_1100 = Maps.<class_2960, class_117.class_119<?>>newHashMap();
	private static final Map<Class<? extends class_117>, class_117.class_119<?>> field_1101 = Maps.<Class<? extends class_117>, class_117.class_119<?>>newHashMap();
	public static final BiFunction<class_1799, class_47, class_1799> field_1102 = (arg, arg2) -> arg;

	public static <T extends class_117> void method_589(class_117.class_119<? extends T> arg) {
		class_2960 lv = arg.method_518();
		Class<T> class_ = (Class<T>)arg.method_519();
		if (field_1100.containsKey(lv)) {
			throw new IllegalArgumentException("Can't re-register item function name " + lv);
		} else if (field_1101.containsKey(class_)) {
			throw new IllegalArgumentException("Can't re-register item function class " + class_.getName());
		} else {
			field_1100.put(lv, arg);
			field_1101.put(class_, arg);
		}
	}

	public static class_117.class_119<?> method_593(class_2960 arg) {
		class_117.class_119<?> lv = (class_117.class_119<?>)field_1100.get(arg);
		if (lv == null) {
			throw new IllegalArgumentException("Unknown loot item function '" + arg + "'");
		} else {
			return lv;
		}
	}

	public static <T extends class_117> class_117.class_119<T> method_590(T arg) {
		class_117.class_119<T> lv = (class_117.class_119<T>)field_1101.get(arg.getClass());
		if (lv == null) {
			throw new IllegalArgumentException("Unknown loot item function " + arg);
		} else {
			return lv;
		}
	}

	public static BiFunction<class_1799, class_47, class_1799> method_594(BiFunction<class_1799, class_47, class_1799>[] biFunctions) {
		switch (biFunctions.length) {
			case 0:
				return field_1102;
			case 1:
				return biFunctions[0];
			case 2:
				BiFunction<class_1799, class_47, class_1799> biFunction = biFunctions[0];
				BiFunction<class_1799, class_47, class_1799> biFunction2 = biFunctions[1];
				return (arg, arg2) -> (class_1799)biFunction2.apply(biFunction.apply(arg, arg2), arg2);
			default:
				return (arg, arg2) -> {
					for (BiFunction<class_1799, class_47, class_1799> biFunctionx : biFunctions) {
						arg = (class_1799)biFunctionx.apply(arg, arg2);
					}

					return arg;
				};
		}
	}

	static {
		method_589(new class_141.class_142());
		method_589(new class_106.class_108());
		method_589(new class_109.class_110());
		method_589(new class_159.class_160());
		method_589(new class_165.class_166());
		method_589(new class_125.class_127());
		method_589(new class_149.class_150());
		method_589(new class_137.class_139());
		method_589(new class_3670.class_147());
		method_589(new class_111.class_113());
		method_589(new class_152.class_154());
		method_589(new class_101.class_103());
		method_589(new class_134.class_136());
		method_589(new class_114.class_115());
		method_589(new class_94.class_99());
		method_589(new class_144.class_145());
		method_589(new class_104.class_105());
		method_589(new class_3671.class_3672());
		method_589(new class_3668.class_3669());
		method_589(new class_3837.class_3842());
	}

	public static class class_132 implements JsonDeserializer<class_117>, JsonSerializer<class_117> {
		public class_117 method_596(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "function");
			class_2960 lv = new class_2960(class_3518.method_15265(jsonObject, "function"));

			class_117.class_119<?> lv2;
			try {
				lv2 = class_131.method_593(lv);
			} catch (IllegalArgumentException var8) {
				throw new JsonSyntaxException("Unknown function '" + lv + "'");
			}

			return lv2.method_517(jsonObject, jsonDeserializationContext);
		}

		public JsonElement method_597(class_117 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			class_117.class_119<class_117> lv = class_131.method_590(arg);
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("function", lv.method_518().toString());
			lv.method_516(jsonObject, arg, jsonSerializationContext);
			return jsonObject;
		}
	}
}
