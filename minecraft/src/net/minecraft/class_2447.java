package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2447 {
	private static final Logger field_11382 = LogManager.getLogger();
	private final class_1792 field_11380;
	private final int field_11378;
	private final List<String> field_11377 = Lists.<String>newArrayList();
	private final Map<Character, class_1856> field_11376 = Maps.<Character, class_1856>newLinkedHashMap();
	private final class_161.class_162 field_11379 = class_161.class_162.method_707();
	private String field_11381;

	public class_2447(class_1935 arg, int i) {
		this.field_11380 = arg.method_8389();
		this.field_11378 = i;
	}

	public static class_2447 method_10437(class_1935 arg) {
		return method_10436(arg, 1);
	}

	public static class_2447 method_10436(class_1935 arg, int i) {
		return new class_2447(arg, i);
	}

	public class_2447 method_10433(Character character, class_3494<class_1792> arg) {
		return this.method_10428(character, class_1856.method_8106(arg));
	}

	public class_2447 method_10434(Character character, class_1935 arg) {
		return this.method_10428(character, class_1856.method_8091(arg));
	}

	public class_2447 method_10428(Character character, class_1856 arg) {
		if (this.field_11376.containsKey(character)) {
			throw new IllegalArgumentException("Symbol '" + character + "' is already defined!");
		} else if (character == ' ') {
			throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		} else {
			this.field_11376.put(character, arg);
			return this;
		}
	}

	public class_2447 method_10439(String string) {
		if (!this.field_11377.isEmpty() && string.length() != ((String)this.field_11377.get(0)).length()) {
			throw new IllegalArgumentException("Pattern must be the same width on every line!");
		} else {
			this.field_11377.add(string);
			return this;
		}
	}

	public class_2447 method_10429(String string, class_184 arg) {
		this.field_11379.method_709(string, arg);
		return this;
	}

	public class_2447 method_10435(String string) {
		this.field_11381 = string;
		return this;
	}

	public void method_10431(Consumer<class_2444> consumer) {
		this.method_10430(consumer, class_2378.field_11142.method_10221(this.field_11380));
	}

	public void method_10438(Consumer<class_2444> consumer, String string) {
		class_2960 lv = class_2378.field_11142.method_10221(this.field_11380);
		if (new class_2960(string).equals(lv)) {
			throw new IllegalStateException("Shaped Recipe " + string + " should remove its 'save' argument");
		} else {
			this.method_10430(consumer, new class_2960(string));
		}
	}

	public void method_10430(Consumer<class_2444> consumer, class_2960 arg) {
		this.method_10432(arg);
		this.field_11379
			.method_708(new class_2960("recipes/root"))
			.method_709("has_the_recipe", new class_2119.class_2121(arg))
			.method_703(class_170.class_171.method_753(arg))
			.method_704(class_193.field_1257);
		consumer.accept(
			new class_2447.class_2448(
				arg,
				this.field_11380,
				this.field_11378,
				this.field_11381 == null ? "" : this.field_11381,
				this.field_11377,
				this.field_11376,
				this.field_11379,
				new class_2960(arg.method_12836(), "recipes/" + this.field_11380.method_7859().method_7751() + "/" + arg.method_12832())
			)
		);
	}

	private void method_10432(class_2960 arg) {
		if (this.field_11377.isEmpty()) {
			throw new IllegalStateException("No pattern is defined for shaped recipe " + arg + "!");
		} else {
			Set<Character> set = Sets.<Character>newHashSet(this.field_11376.keySet());
			set.remove(' ');

			for (String string : this.field_11377) {
				for (int i = 0; i < string.length(); i++) {
					char c = string.charAt(i);
					if (!this.field_11376.containsKey(c) && c != ' ') {
						throw new IllegalStateException("Pattern in recipe " + arg + " uses undefined symbol '" + c + "'");
					}

					set.remove(c);
				}
			}

			if (!set.isEmpty()) {
				throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + arg);
			} else if (this.field_11377.size() == 1 && ((String)this.field_11377.get(0)).length() == 1) {
				throw new IllegalStateException("Shaped recipe " + arg + " only takes in a single item - should it be a shapeless recipe instead?");
			} else if (this.field_11379.method_710().isEmpty()) {
				throw new IllegalStateException("No way of obtaining recipe " + arg);
			}
		}
	}

	class class_2448 implements class_2444 {
		private final class_2960 field_11385;
		private final class_1792 field_11383;
		private final int field_11386;
		private final String field_11387;
		private final List<String> field_11384;
		private final Map<Character, class_1856> field_11388;
		private final class_161.class_162 field_11389;
		private final class_2960 field_11390;

		public class_2448(
			class_2960 arg2, class_1792 arg3, int i, String string, List<String> list, Map<Character, class_1856> map, class_161.class_162 arg4, class_2960 arg5
		) {
			this.field_11385 = arg2;
			this.field_11383 = arg3;
			this.field_11386 = i;
			this.field_11387 = string;
			this.field_11384 = list;
			this.field_11388 = map;
			this.field_11389 = arg4;
			this.field_11390 = arg5;
		}

		@Override
		public void method_10416(JsonObject jsonObject) {
			if (!this.field_11387.isEmpty()) {
				jsonObject.addProperty("group", this.field_11387);
			}

			JsonArray jsonArray = new JsonArray();

			for (String string : this.field_11384) {
				jsonArray.add(string);
			}

			jsonObject.add("pattern", jsonArray);
			JsonObject jsonObject2 = new JsonObject();

			for (Entry<Character, class_1856> entry : this.field_11388.entrySet()) {
				jsonObject2.add(String.valueOf(entry.getKey()), ((class_1856)entry.getValue()).method_8089());
			}

			jsonObject.add("key", jsonObject2);
			JsonObject jsonObject3 = new JsonObject();
			jsonObject3.addProperty("item", class_2378.field_11142.method_10221(this.field_11383).toString());
			if (this.field_11386 > 1) {
				jsonObject3.addProperty("count", this.field_11386);
			}

			jsonObject.add("result", jsonObject3);
		}

		@Override
		public class_1865<?> method_17800() {
			return class_1865.field_9035;
		}

		@Override
		public class_2960 method_10417() {
			return this.field_11385;
		}

		@Nullable
		@Override
		public JsonObject method_10415() {
			return this.field_11389.method_698();
		}

		@Nullable
		@Override
		public class_2960 method_10418() {
			return this.field_11390;
		}
	}
}
