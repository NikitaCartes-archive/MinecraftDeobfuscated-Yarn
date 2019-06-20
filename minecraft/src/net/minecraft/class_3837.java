package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class class_3837 extends class_120 {
	private final class_3837.class_3840 field_17013;
	private final List<class_3837.class_3839> field_17014;
	private static final Function<class_1297, class_2520> field_17015 = class_2105::method_9076;
	private static final Function<class_2586, class_2520> field_17016 = arg -> arg.method_11007(new class_2487());

	private class_3837(class_209[] args, class_3837.class_3840 arg, List<class_3837.class_3839> list) {
		super(args);
		this.field_17013 = arg;
		this.field_17014 = ImmutableList.copyOf(list);
	}

	private static class_2203.class_2209 method_16853(String string) {
		try {
			return new class_2203().method_9362(new StringReader(string));
		} catch (CommandSyntaxException var2) {
			throw new IllegalArgumentException("Failed to parse path " + string, var2);
		}
	}

	@Override
	public Set<class_169<?>> method_293() {
		return ImmutableSet.of(this.field_17013.field_17029);
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		class_2520 lv = (class_2520)this.field_17013.field_17030.apply(arg2);
		if (lv != null) {
			this.field_17014.forEach(arg3 -> arg3.method_16860(arg::method_7948, lv));
		}

		return arg;
	}

	public static class_3837.class_3838 method_16848(class_3837.class_3840 arg) {
		return new class_3837.class_3838(arg);
	}

	public static class class_3838 extends class_120.class_121<class_3837.class_3838> {
		private final class_3837.class_3840 field_17017;
		private final List<class_3837.class_3839> field_17018 = Lists.<class_3837.class_3839>newArrayList();

		private class_3838(class_3837.class_3840 arg) {
			this.field_17017 = arg;
		}

		public class_3837.class_3838 method_16857(String string, String string2, class_3837.class_3841 arg) {
			this.field_17018.add(new class_3837.class_3839(string, string2, arg));
			return this;
		}

		public class_3837.class_3838 method_16856(String string, String string2) {
			return this.method_16857(string, string2, class_3837.class_3841.field_17032);
		}

		protected class_3837.class_3838 method_16855() {
			return this;
		}

		@Override
		public class_117 method_515() {
			return new class_3837(this.method_526(), this.field_17017, this.field_17018);
		}
	}

	static class class_3839 {
		private final String field_17019;
		private final class_2203.class_2209 field_17020;
		private final String field_17021;
		private final class_2203.class_2209 field_17022;
		private final class_3837.class_3841 field_17023;

		private class_3839(String string, String string2, class_3837.class_3841 arg) {
			this.field_17019 = string;
			this.field_17020 = class_3837.method_16853(string);
			this.field_17021 = string2;
			this.field_17022 = class_3837.method_16853(string2);
			this.field_17023 = arg;
		}

		public void method_16860(Supplier<class_2520> supplier, class_2520 arg) {
			try {
				List<class_2520> list = this.field_17020.method_9366(arg);
				if (!list.isEmpty()) {
					this.field_17023.method_16864((class_2520)supplier.get(), this.field_17022, list);
				}
			} catch (CommandSyntaxException var4) {
			}
		}

		public JsonObject method_16858() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("source", this.field_17019);
			jsonObject.addProperty("target", this.field_17021);
			jsonObject.addProperty("op", this.field_17023.field_17035);
			return jsonObject;
		}

		public static class_3837.class_3839 method_16859(JsonObject jsonObject) {
			String string = class_3518.method_15265(jsonObject, "source");
			String string2 = class_3518.method_15265(jsonObject, "target");
			class_3837.class_3841 lv = class_3837.class_3841.method_16865(class_3518.method_15265(jsonObject, "op"));
			return new class_3837.class_3839(string, string2, lv);
		}
	}

	public static enum class_3840 {
		field_17024("this", class_181.field_1226, class_3837.field_17015),
		field_17025("killer", class_181.field_1230, class_3837.field_17015),
		field_17026("killer_player", class_181.field_1233, class_3837.field_17015),
		field_17027("block_entity", class_181.field_1228, class_3837.field_17016);

		public final String field_17028;
		public final class_169<?> field_17029;
		public final Function<class_47, class_2520> field_17030;

		private <T> class_3840(String string2, class_169<T> arg, Function<? super T, class_2520> function) {
			this.field_17028 = string2;
			this.field_17029 = arg;
			this.field_17030 = arg2 -> {
				T object = arg2.method_296(arg);
				return object != null ? (class_2520)function.apply(object) : null;
			};
		}

		public static class_3837.class_3840 method_16862(String string) {
			for (class_3837.class_3840 lv : values()) {
				if (lv.field_17028.equals(string)) {
					return lv;
				}
			}

			throw new IllegalArgumentException("Invalid tag source " + string);
		}
	}

	public static enum class_3841 {
		field_17032("replace") {
			@Override
			public void method_16864(class_2520 arg, class_2203.class_2209 arg2, List<class_2520> list) throws CommandSyntaxException {
				arg2.method_9368(arg, Iterables.getLast(list)::method_10707);
			}
		},
		field_17033("append") {
			@Override
			public void method_16864(class_2520 arg, class_2203.class_2209 arg2, List<class_2520> list) throws CommandSyntaxException {
				List<class_2520> list2 = arg2.method_9367(arg, class_2499::new);
				list2.forEach(argx -> {
					if (argx instanceof class_2499) {
						list.forEach(arg2x -> ((class_2499)argx).add(arg2x.method_10707()));
					}
				});
			}
		},
		field_17034("merge") {
			@Override
			public void method_16864(class_2520 arg, class_2203.class_2209 arg2, List<class_2520> list) throws CommandSyntaxException {
				List<class_2520> list2 = arg2.method_9367(arg, class_2487::new);
				list2.forEach(argx -> {
					if (argx instanceof class_2487) {
						list.forEach(arg2x -> {
							if (arg2x instanceof class_2487) {
								((class_2487)argx).method_10543((class_2487)arg2x);
							}
						});
					}
				});
			}
		};

		private final String field_17035;

		public abstract void method_16864(class_2520 arg, class_2203.class_2209 arg2, List<class_2520> list) throws CommandSyntaxException;

		private class_3841(String string2) {
			this.field_17035 = string2;
		}

		public static class_3837.class_3841 method_16865(String string) {
			for (class_3837.class_3841 lv : values()) {
				if (lv.field_17035.equals(string)) {
					return lv;
				}
			}

			throw new IllegalArgumentException("Invalid merge strategy" + string);
		}
	}

	public static class class_3842 extends class_120.class_123<class_3837> {
		public class_3842() {
			super(new class_2960("copy_nbt"), class_3837.class);
		}

		public void method_16870(JsonObject jsonObject, class_3837 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, arg, jsonSerializationContext);
			jsonObject.addProperty("source", arg.field_17013.field_17028);
			JsonArray jsonArray = new JsonArray();
			arg.field_17014.stream().map(class_3837.class_3839::method_16858).forEach(jsonArray::add);
			jsonObject.add("ops", jsonArray);
		}

		public class_3837 method_16871(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			class_3837.class_3840 lv = class_3837.class_3840.method_16862(class_3518.method_15265(jsonObject, "source"));
			List<class_3837.class_3839> list = Lists.<class_3837.class_3839>newArrayList();

			for (JsonElement jsonElement : class_3518.method_15261(jsonObject, "ops")) {
				JsonObject jsonObject2 = class_3518.method_15295(jsonElement, "op");
				list.add(class_3837.class_3839.method_16859(jsonObject2));
			}

			return new class_3837(args, lv, list);
		}
	}
}
