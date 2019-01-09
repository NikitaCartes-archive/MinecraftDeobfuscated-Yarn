package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;

public class class_170 {
	public static final class_170 field_1167 = new class_170(0, new class_2960[0], new class_2960[0], class_2158.class_2159.field_9809);
	private final int field_1165;
	private final class_2960[] field_1164;
	private final class_2960[] field_1166;
	private final class_2158.class_2159 field_1163;

	public class_170(int i, class_2960[] args, class_2960[] args2, class_2158.class_2159 arg) {
		this.field_1165 = i;
		this.field_1164 = args;
		this.field_1166 = args2;
		this.field_1163 = arg;
	}

	public void method_748(class_3222 arg) {
		arg.method_7255(this.field_1165);
		class_47 lv = new class_47.class_48(arg.method_14220())
			.method_312(class_181.field_1226, arg)
			.method_312(class_181.field_1232, new class_2338(arg))
			.method_311(arg.method_6051())
			.method_309(class_173.field_1174);
		boolean bl = false;

		for (class_2960 lv2 : this.field_1164) {
			for (class_1799 lv3 : arg.field_13995.method_3857().method_367(lv2).method_319(lv)) {
				if (arg.method_7270(lv3)) {
					arg.field_6002
						.method_8465(
							null,
							arg.field_5987,
							arg.field_6010,
							arg.field_6035,
							class_3417.field_15197,
							class_3419.field_15248,
							0.2F,
							((arg.method_6051().nextFloat() - arg.method_6051().nextFloat()) * 0.7F + 1.0F) * 2.0F
						);
					bl = true;
				} else {
					class_1542 lv4 = arg.method_7328(lv3, false);
					if (lv4 != null) {
						lv4.method_6975();
						lv4.method_6984(arg.method_5667());
					}
				}
			}
		}

		if (bl) {
			arg.field_7498.method_7623();
		}

		if (this.field_1166.length > 0) {
			arg.method_7335(this.field_1166);
		}

		MinecraftServer minecraftServer = arg.field_13995;
		class_2158 lv5 = this.field_1163.method_9196(minecraftServer.method_3740());
		if (lv5 != null) {
			minecraftServer.method_3740().method_12904(lv5, arg.method_5671().method_9217().method_9206(2));
		}
	}

	public String toString() {
		return "AdvancementRewards{experience="
			+ this.field_1165
			+ ", loot="
			+ Arrays.toString(this.field_1164)
			+ ", recipes="
			+ Arrays.toString(this.field_1166)
			+ ", function="
			+ this.field_1163
			+ '}';
	}

	public JsonElement method_747() {
		if (this == field_1167) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			if (this.field_1165 != 0) {
				jsonObject.addProperty("experience", this.field_1165);
			}

			if (this.field_1164.length > 0) {
				JsonArray jsonArray = new JsonArray();

				for (class_2960 lv : this.field_1164) {
					jsonArray.add(lv.toString());
				}

				jsonObject.add("loot", jsonArray);
			}

			if (this.field_1166.length > 0) {
				JsonArray jsonArray = new JsonArray();

				for (class_2960 lv : this.field_1166) {
					jsonArray.add(lv.toString());
				}

				jsonObject.add("recipes", jsonArray);
			}

			if (this.field_1163.method_9197() != null) {
				jsonObject.addProperty("function", this.field_1163.method_9197().toString());
			}

			return jsonObject;
		}
	}

	public static class class_171 {
		private int field_1169;
		private final List<class_2960> field_1171 = Lists.<class_2960>newArrayList();
		private final List<class_2960> field_1168 = Lists.<class_2960>newArrayList();
		@Nullable
		private class_2960 field_1170;

		public static class_170.class_171 method_750(int i) {
			return new class_170.class_171().method_749(i);
		}

		public class_170.class_171 method_749(int i) {
			this.field_1169 += i;
			return this;
		}

		public static class_170.class_171 method_753(class_2960 arg) {
			return new class_170.class_171().method_752(arg);
		}

		public class_170.class_171 method_752(class_2960 arg) {
			this.field_1168.add(arg);
			return this;
		}

		public class_170 method_751() {
			return new class_170(
				this.field_1169,
				(class_2960[])this.field_1171.toArray(new class_2960[0]),
				(class_2960[])this.field_1168.toArray(new class_2960[0]),
				this.field_1170 == null ? class_2158.class_2159.field_9809 : new class_2158.class_2159(this.field_1170)
			);
		}
	}

	public static class class_172 implements JsonDeserializer<class_170> {
		public class_170 method_754(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "rewards");
			int i = class_3518.method_15282(jsonObject, "experience", 0);
			JsonArray jsonArray = class_3518.method_15292(jsonObject, "loot", new JsonArray());
			class_2960[] lvs = new class_2960[jsonArray.size()];

			for (int j = 0; j < lvs.length; j++) {
				lvs[j] = new class_2960(class_3518.method_15287(jsonArray.get(j), "loot[" + j + "]"));
			}

			JsonArray jsonArray2 = class_3518.method_15292(jsonObject, "recipes", new JsonArray());
			class_2960[] lvs2 = new class_2960[jsonArray2.size()];

			for (int k = 0; k < lvs2.length; k++) {
				lvs2[k] = new class_2960(class_3518.method_15287(jsonArray2.get(k), "recipes[" + k + "]"));
			}

			class_2158.class_2159 lv;
			if (jsonObject.has("function")) {
				lv = new class_2158.class_2159(new class_2960(class_3518.method_15265(jsonObject, "function")));
			} else {
				lv = class_2158.class_2159.field_9809;
			}

			return new class_170(i, lvs, lvs2, lv);
		}
	}
}
