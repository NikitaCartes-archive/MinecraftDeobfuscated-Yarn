package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_52 {
	private static final Logger field_946 = LogManager.getLogger();
	public static final class_52 field_948 = new class_52(class_173.field_1175, new class_55[0], new class_117[0]);
	public static final class_176 field_947 = class_173.field_1177;
	private final class_176 field_942;
	private final class_55[] field_943;
	private final class_117[] field_944;
	private final BiFunction<class_1799, class_47, class_1799> field_945;

	private class_52(class_176 arg, class_55[] args, class_117[] args2) {
		this.field_942 = arg;
		this.field_943 = args;
		this.field_944 = args2;
		this.field_945 = class_131.method_594(args2);
	}

	public static Consumer<class_1799> method_332(Consumer<class_1799> consumer) {
		return arg -> {
			if (arg.method_7947() < arg.method_7914()) {
				consumer.accept(arg);
			} else {
				int i = arg.method_7947();

				while (i > 0) {
					class_1799 lv = arg.method_7972();
					lv.method_7939(Math.min(arg.method_7914(), i));
					i -= lv.method_7947();
					consumer.accept(lv);
				}
			}
		};
	}

	public void method_328(class_47 arg, Consumer<class_1799> consumer) {
		if (arg.method_298(this)) {
			Consumer<class_1799> consumer2 = class_117.method_513(this.field_945, consumer, arg);

			for (class_55 lv : this.field_943) {
				lv.method_341(consumer2, arg);
			}

			arg.method_295(this);
		} else {
			field_946.warn("Detected infinite loop in loot tables");
		}
	}

	public void method_320(class_47 arg, Consumer<class_1799> consumer) {
		this.method_328(arg, method_332(consumer));
	}

	public List<class_1799> method_319(class_47 arg) {
		List<class_1799> list = Lists.<class_1799>newArrayList();
		this.method_320(arg, list::add);
		return list;
	}

	public class_176 method_322() {
		return this.field_942;
	}

	public void method_330(class_58 arg, Function<class_2960, class_52> function, Set<class_2960> set, class_176 arg2) {
		for (int i = 0; i < this.field_943.length; i++) {
			this.field_943[i].method_349(arg.method_364(".pools[" + i + "]"), function, set, arg2);
		}

		for (int i = 0; i < this.field_944.length; i++) {
			this.field_944[i].method_292(arg.method_364(".functions[" + i + "]"), function, set, arg2);
		}
	}

	public void method_329(class_1263 arg, class_47 arg2) {
		List<class_1799> list = this.method_319(arg2);
		Random random = arg2.method_294();
		List<Integer> list2 = this.method_321(arg, random);
		this.method_333(list, list2.size(), random);

		for (class_1799 lv : list) {
			if (list2.isEmpty()) {
				field_946.warn("Tried to over-fill a container");
				return;
			}

			if (lv.method_7960()) {
				arg.method_5447((Integer)list2.remove(list2.size() - 1), class_1799.field_8037);
			} else {
				arg.method_5447((Integer)list2.remove(list2.size() - 1), lv);
			}
		}
	}

	private void method_333(List<class_1799> list, int i, Random random) {
		List<class_1799> list2 = Lists.<class_1799>newArrayList();
		Iterator<class_1799> iterator = list.iterator();

		while (iterator.hasNext()) {
			class_1799 lv = (class_1799)iterator.next();
			if (lv.method_7960()) {
				iterator.remove();
			} else if (lv.method_7947() > 1) {
				list2.add(lv);
				iterator.remove();
			}
		}

		while (i - list.size() - list2.size() > 0 && !list2.isEmpty()) {
			class_1799 lv2 = (class_1799)list2.remove(class_3532.method_15395(random, 0, list2.size() - 1));
			int j = class_3532.method_15395(random, 1, lv2.method_7947() / 2);
			class_1799 lv3 = lv2.method_7971(j);
			if (lv2.method_7947() > 1 && random.nextBoolean()) {
				list2.add(lv2);
			} else {
				list.add(lv2);
			}

			if (lv3.method_7947() > 1 && random.nextBoolean()) {
				list2.add(lv3);
			} else {
				list.add(lv3);
			}
		}

		list.addAll(list2);
		Collections.shuffle(list, random);
	}

	private List<Integer> method_321(class_1263 arg, Random random) {
		List<Integer> list = Lists.<Integer>newArrayList();

		for (int i = 0; i < arg.method_5439(); i++) {
			if (arg.method_5438(i).method_7960()) {
				list.add(i);
			}
		}

		Collections.shuffle(list, random);
		return list;
	}

	public static class_52.class_53 method_324() {
		return new class_52.class_53();
	}

	public static class class_53 implements class_116<class_52.class_53> {
		private final List<class_55> field_949 = Lists.<class_55>newArrayList();
		private final List<class_117> field_951 = Lists.<class_117>newArrayList();
		private class_176 field_950 = class_52.field_947;

		public class_52.class_53 method_336(class_55.class_56 arg) {
			this.field_949.add(arg.method_355());
			return this;
		}

		public class_52.class_53 method_334(class_176 arg) {
			this.field_950 = arg;
			return this;
		}

		public class_52.class_53 method_335(class_117.class_118 arg) {
			this.field_951.add(arg.method_515());
			return this;
		}

		public class_52.class_53 method_337() {
			return this;
		}

		public class_52 method_338() {
			return new class_52(this.field_950, (class_55[])this.field_949.toArray(new class_55[0]), (class_117[])this.field_951.toArray(new class_117[0]));
		}
	}

	public static class class_54 implements JsonDeserializer<class_52>, JsonSerializer<class_52> {
		public class_52 method_340(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "loot table");
			class_55[] lvs = class_3518.method_15283(jsonObject, "pools", new class_55[0], jsonDeserializationContext, class_55[].class);
			class_176 lv = null;
			if (jsonObject.has("type")) {
				String string = class_3518.method_15265(jsonObject, "type");
				lv = class_173.method_757(new class_2960(string));
			}

			class_117[] lvs2 = class_3518.method_15283(jsonObject, "functions", new class_117[0], jsonDeserializationContext, class_117[].class);
			return new class_52(lv != null ? lv : class_173.field_1177, lvs, lvs2);
		}

		public JsonElement method_339(class_52 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			if (arg.field_942 != class_52.field_947) {
				class_2960 lv = class_173.method_762(arg.field_942);
				if (lv != null) {
					jsonObject.addProperty("type", lv.toString());
				} else {
					class_52.field_946.warn("Failed to find id for param set " + arg.field_942);
				}
			}

			if (arg.field_943.length > 0) {
				jsonObject.add("pools", jsonSerializationContext.serialize(arg.field_943));
			}

			if (!ArrayUtils.isEmpty((Object[])arg.field_944)) {
				jsonObject.add("functions", jsonSerializationContext.serialize(arg.field_944));
			}

			return jsonObject;
		}
	}
}
