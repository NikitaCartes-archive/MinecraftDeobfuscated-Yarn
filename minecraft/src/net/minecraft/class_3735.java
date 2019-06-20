package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class class_3735 {
	public static final class_3735 field_16485 = new class_3735(
		class_2073.field_9640, class_2073.field_9640, class_2073.field_9640, class_2073.field_9640, class_2073.field_9640, class_2073.field_9640
	);
	public static final class_3735 field_19240 = new class_3735(
		class_2073.class_2074.method_8973().method_8977(class_1802.field_8539).method_20399(class_3765.method_16515().method_7969()).method_8976(),
		class_2073.field_9640,
		class_2073.field_9640,
		class_2073.field_9640,
		class_2073.field_9640,
		class_2073.field_9640
	);
	private final class_2073 field_16483;
	private final class_2073 field_16487;
	private final class_2073 field_16488;
	private final class_2073 field_16489;
	private final class_2073 field_16486;
	private final class_2073 field_16484;

	public class_3735(class_2073 arg, class_2073 arg2, class_2073 arg3, class_2073 arg4, class_2073 arg5, class_2073 arg6) {
		this.field_16483 = arg;
		this.field_16487 = arg2;
		this.field_16488 = arg3;
		this.field_16489 = arg4;
		this.field_16486 = arg5;
		this.field_16484 = arg6;
	}

	public boolean method_16226(@Nullable class_1297 arg) {
		if (this == field_16485) {
			return true;
		} else if (!(arg instanceof class_1309)) {
			return false;
		} else {
			class_1309 lv = (class_1309)arg;
			if (!this.field_16483.method_8970(lv.method_6118(class_1304.field_6169))) {
				return false;
			} else if (!this.field_16487.method_8970(lv.method_6118(class_1304.field_6174))) {
				return false;
			} else if (!this.field_16488.method_8970(lv.method_6118(class_1304.field_6172))) {
				return false;
			} else if (!this.field_16489.method_8970(lv.method_6118(class_1304.field_6166))) {
				return false;
			} else {
				return !this.field_16486.method_8970(lv.method_6118(class_1304.field_6173)) ? false : this.field_16484.method_8970(lv.method_6118(class_1304.field_6171));
			}
		}
	}

	public static class_3735 method_16224(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "equipment");
			class_2073 lv = class_2073.method_8969(jsonObject.get("head"));
			class_2073 lv2 = class_2073.method_8969(jsonObject.get("chest"));
			class_2073 lv3 = class_2073.method_8969(jsonObject.get("legs"));
			class_2073 lv4 = class_2073.method_8969(jsonObject.get("feet"));
			class_2073 lv5 = class_2073.method_8969(jsonObject.get("mainhand"));
			class_2073 lv6 = class_2073.method_8969(jsonObject.get("offhand"));
			return new class_3735(lv, lv2, lv3, lv4, lv5, lv6);
		} else {
			return field_16485;
		}
	}

	public JsonElement method_16225() {
		if (this == field_16485) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("head", this.field_16483.method_8971());
			jsonObject.add("chest", this.field_16487.method_8971());
			jsonObject.add("legs", this.field_16488.method_8971());
			jsonObject.add("feet", this.field_16489.method_8971());
			jsonObject.add("mainhand", this.field_16486.method_8971());
			jsonObject.add("offhand", this.field_16484.method_8971());
			return jsonObject;
		}
	}
}
