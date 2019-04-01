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
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;

public class class_55 {
	private final class_79[] field_953;
	private final class_209[] field_954;
	private final Predicate<class_47> field_955;
	private final class_117[] field_956;
	private final BiFunction<class_1799, class_47, class_1799> field_952;
	private final class_59 field_957;
	private final class_61 field_958;

	private class_55(class_79[] args, class_209[] args2, class_117[] args3, class_59 arg, class_61 arg2) {
		this.field_953 = args;
		this.field_954 = args2;
		this.field_955 = class_217.method_924(args2);
		this.field_956 = args3;
		this.field_952 = class_131.method_594(args3);
		this.field_957 = arg;
		this.field_958 = arg2;
	}

	private void method_345(Consumer<class_1799> consumer, class_47 arg) {
		Random random = arg.method_294();
		List<class_82> list = Lists.<class_82>newArrayList();
		MutableInt mutableInt = new MutableInt();

		for (class_79 lv : this.field_953) {
			lv.expand(arg, arg2 -> {
				int i = arg2.method_427(arg.method_302());
				if (i > 0) {
					list.add(arg2);
					mutableInt.add(i);
				}
			});
		}

		int i = list.size();
		if (mutableInt.intValue() != 0 && i != 0) {
			if (i == 1) {
				((class_82)list.get(0)).method_426(consumer, arg);
			} else {
				int j = random.nextInt(mutableInt.intValue());

				for (class_82 lv2 : list) {
					j -= lv2.method_427(arg.method_302());
					if (j < 0) {
						lv2.method_426(consumer, arg);
						return;
					}
				}
			}
		}
	}

	public void method_341(Consumer<class_1799> consumer, class_47 arg) {
		if (this.field_955.test(arg)) {
			Consumer<class_1799> consumer2 = class_117.method_513(this.field_952, consumer, arg);
			Random random = arg.method_294();
			int i = this.field_957.method_366(random) + class_3532.method_15375(this.field_958.method_374(random) * arg.method_302());

			for (int j = 0; j < i; j++) {
				this.method_345(consumer2, arg);
			}
		}
	}

	public void method_349(class_58 arg, Function<class_2960, class_52> function, Set<class_2960> set, class_176 arg2) {
		for (int i = 0; i < this.field_954.length; i++) {
			this.field_954[i].method_292(arg.method_364(".condition[" + i + "]"), function, set, arg2);
		}

		for (int i = 0; i < this.field_956.length; i++) {
			this.field_956[i].method_292(arg.method_364(".functions[" + i + "]"), function, set, arg2);
		}

		for (int i = 0; i < this.field_953.length; i++) {
			this.field_953[i].method_415(arg.method_364(".entries[" + i + "]"), function, set, arg2);
		}
	}

	public static class_55.class_56 method_347() {
		return new class_55.class_56();
	}

	public static class class_56 implements class_116<class_55.class_56>, class_192<class_55.class_56> {
		private final List<class_79> field_960 = Lists.<class_79>newArrayList();
		private final List<class_209> field_963 = Lists.<class_209>newArrayList();
		private final List<class_117> field_961 = Lists.<class_117>newArrayList();
		private class_59 field_959 = new class_61(1.0F);
		private class_61 field_962 = new class_61(0.0F, 0.0F);

		public class_55.class_56 method_352(class_59 arg) {
			this.field_959 = arg;
			return this;
		}

		public class_55.class_56 method_354() {
			return this;
		}

		public class_55.class_56 method_351(class_79.class_80<?> arg) {
			this.field_960.add(arg.method_419());
			return this;
		}

		public class_55.class_56 method_356(class_209.class_210 arg) {
			this.field_963.add(arg.build());
			return this;
		}

		public class_55.class_56 method_353(class_117.class_118 arg) {
			this.field_961.add(arg.method_515());
			return this;
		}

		public class_55 method_355() {
			if (this.field_959 == null) {
				throw new IllegalArgumentException("Rolls not set");
			} else {
				return new class_55(
					(class_79[])this.field_960.toArray(new class_79[0]),
					(class_209[])this.field_963.toArray(new class_209[0]),
					(class_117[])this.field_961.toArray(new class_117[0]),
					this.field_959,
					this.field_962
				);
			}
		}
	}

	public static class class_57 implements JsonDeserializer<class_55>, JsonSerializer<class_55> {
		public class_55 method_358(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "loot pool");
			class_79[] lvs = class_3518.method_15272(jsonObject, "entries", jsonDeserializationContext, class_79[].class);
			class_209[] lvs2 = class_3518.method_15283(jsonObject, "conditions", new class_209[0], jsonDeserializationContext, class_209[].class);
			class_117[] lvs3 = class_3518.method_15283(jsonObject, "functions", new class_117[0], jsonDeserializationContext, class_117[].class);
			class_59 lv = class_63.method_383(jsonObject.get("rolls"), jsonDeserializationContext);
			class_61 lv2 = class_3518.method_15283(jsonObject, "bonus_rolls", new class_61(0.0F, 0.0F), jsonDeserializationContext, class_61.class);
			return new class_55(lvs, lvs2, lvs3, lv, lv2);
		}

		public JsonElement method_357(class_55 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("rolls", class_63.method_384(arg.field_957, jsonSerializationContext));
			jsonObject.add("entries", jsonSerializationContext.serialize(arg.field_953));
			if (arg.field_958.method_378() != 0.0F && arg.field_958.method_380() != 0.0F) {
				jsonObject.add("bonus_rolls", jsonSerializationContext.serialize(arg.field_958));
			}

			if (!ArrayUtils.isEmpty((Object[])arg.field_954)) {
				jsonObject.add("conditions", jsonSerializationContext.serialize(arg.field_954));
			}

			if (!ArrayUtils.isEmpty((Object[])arg.field_956)) {
				jsonObject.add("functions", jsonSerializationContext.serialize(arg.field_956));
			}

			return jsonObject;
		}
	}
}
