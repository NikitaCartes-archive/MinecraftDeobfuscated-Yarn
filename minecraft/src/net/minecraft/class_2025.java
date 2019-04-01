package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class class_2025 {
	public static final class_2025 field_9553 = new class_2025(
		class_2096.class_2099.field_9705,
		class_2096.class_2099.field_9705,
		class_2096.class_2099.field_9705,
		class_2096.class_2099.field_9705,
		class_2096.class_2099.field_9705
	);
	private final class_2096.class_2099 field_9554;
	private final class_2096.class_2099 field_9555;
	private final class_2096.class_2099 field_9552;
	private final class_2096.class_2099 field_9557;
	private final class_2096.class_2099 field_9556;

	public class_2025(class_2096.class_2099 arg, class_2096.class_2099 arg2, class_2096.class_2099 arg3, class_2096.class_2099 arg4, class_2096.class_2099 arg5) {
		this.field_9554 = arg;
		this.field_9555 = arg2;
		this.field_9552 = arg3;
		this.field_9557 = arg4;
		this.field_9556 = arg5;
	}

	public static class_2025 method_8860(class_2096.class_2099 arg) {
		return new class_2025(
			class_2096.class_2099.field_9705, class_2096.class_2099.field_9705, class_2096.class_2099.field_9705, arg, class_2096.class_2099.field_9705
		);
	}

	public static class_2025 method_8856(class_2096.class_2099 arg) {
		return new class_2025(
			class_2096.class_2099.field_9705, arg, class_2096.class_2099.field_9705, class_2096.class_2099.field_9705, class_2096.class_2099.field_9705
		);
	}

	public boolean method_8859(double d, double e, double f, double g, double h, double i) {
		float j = (float)(d - g);
		float k = (float)(e - h);
		float l = (float)(f - i);
		if (!this.field_9554.method_9047(class_3532.method_15379(j))
			|| !this.field_9555.method_9047(class_3532.method_15379(k))
			|| !this.field_9552.method_9047(class_3532.method_15379(l))) {
			return false;
		} else {
			return !this.field_9557.method_9045((double)(j * j + l * l)) ? false : this.field_9556.method_9045((double)(j * j + k * k + l * l));
		}
	}

	public static class_2025 method_8857(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "distance");
			class_2096.class_2099 lv = class_2096.class_2099.method_9051(jsonObject.get("x"));
			class_2096.class_2099 lv2 = class_2096.class_2099.method_9051(jsonObject.get("y"));
			class_2096.class_2099 lv3 = class_2096.class_2099.method_9051(jsonObject.get("z"));
			class_2096.class_2099 lv4 = class_2096.class_2099.method_9051(jsonObject.get("horizontal"));
			class_2096.class_2099 lv5 = class_2096.class_2099.method_9051(jsonObject.get("absolute"));
			return new class_2025(lv, lv2, lv3, lv4, lv5);
		} else {
			return field_9553;
		}
	}

	public JsonElement method_8858() {
		if (this == field_9553) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("x", this.field_9554.method_9036());
			jsonObject.add("y", this.field_9555.method_9036());
			jsonObject.add("z", this.field_9552.method_9036());
			jsonObject.add("horizontal", this.field_9557.method_9036());
			jsonObject.add("absolute", this.field_9556.method_9036());
			return jsonObject;
		}
	}
}
