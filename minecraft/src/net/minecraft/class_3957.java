package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class class_3957<T extends class_1874> implements class_1865<T> {
	private final int field_17551;
	private final class_3957.class_3958<T> field_17552;

	public class_3957(class_3957.class_3958<T> arg, int i) {
		this.field_17551 = i;
		this.field_17552 = arg;
	}

	public T method_17736(class_2960 arg, JsonObject jsonObject) {
		String string = class_3518.method_15253(jsonObject, "group", "");
		JsonElement jsonElement = (JsonElement)(class_3518.method_15264(jsonObject, "ingredient")
			? class_3518.method_15261(jsonObject, "ingredient")
			: class_3518.method_15296(jsonObject, "ingredient"));
		class_1856 lv = class_1856.method_8102(jsonElement);
		String string2 = class_3518.method_15265(jsonObject, "result");
		class_2960 lv2 = new class_2960(string2);
		class_1799 lv3 = new class_1799(
			(class_1935)class_2378.field_11142.method_17966(lv2).orElseThrow(() -> new IllegalStateException("Item: " + string2 + " does not exist"))
		);
		float f = class_3518.method_15277(jsonObject, "experience", 0.0F);
		int i = class_3518.method_15282(jsonObject, "cookingtime", this.field_17551);
		return this.field_17552.create(arg, string, lv, lv3, f, i);
	}

	public T method_17737(class_2960 arg, class_2540 arg2) {
		String string = arg2.method_10800(32767);
		class_1856 lv = class_1856.method_8086(arg2);
		class_1799 lv2 = arg2.method_10819();
		float f = arg2.readFloat();
		int i = arg2.method_10816();
		return this.field_17552.create(arg, string, lv, lv2, f, i);
	}

	public void method_17735(class_2540 arg, T arg2) {
		arg.method_10814(arg2.field_9062);
		arg2.field_9061.method_8088(arg);
		arg.method_10793(arg2.field_9059);
		arg.writeFloat(arg2.field_9057);
		arg.method_10804(arg2.field_9058);
	}

	interface class_3958<T extends class_1874> {
		T create(class_2960 arg, String string, class_1856 arg2, class_1799 arg3, float f, int i);
	}
}
