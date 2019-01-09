package net.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class class_2048 {
	public static final class_2048 field_9599 = new class_2048(
		class_2050.field_9609,
		class_2025.field_9553,
		class_2090.field_9685,
		class_2102.field_9709,
		class_2105.field_9716,
		class_2040.field_9581,
		class_3735.field_16485,
		null
	);
	public static final class_2048[] field_9598 = new class_2048[0];
	private final class_2050 field_9595;
	private final class_2025 field_9601;
	private final class_2090 field_9596;
	private final class_2102 field_9594;
	private final class_2105 field_9600;
	private final class_2040 field_9597;
	private final class_3735 field_16490;
	private final class_2960 field_16317;

	private class_2048(
		class_2050 arg, class_2025 arg2, class_2090 arg3, class_2102 arg4, class_2105 arg5, class_2040 arg6, class_3735 arg7, @Nullable class_2960 arg8
	) {
		this.field_9595 = arg;
		this.field_9601 = arg2;
		this.field_9596 = arg3;
		this.field_9594 = arg4;
		this.field_9600 = arg5;
		this.field_9597 = arg6;
		this.field_16490 = arg7;
		this.field_16317 = arg8;
	}

	public boolean method_8914(class_3222 arg, @Nullable class_1297 arg2) {
		return this.method_8909(arg.method_14220(), new class_243(arg.field_5987, arg.field_6010, arg.field_6035), arg2);
	}

	public boolean method_8909(class_3218 arg, class_243 arg2, @Nullable class_1297 arg3) {
		if (this == field_9599) {
			return true;
		} else if (arg3 == null) {
			return false;
		} else if (!this.field_9595.method_8925(arg3.method_5864())) {
			return false;
		} else if (!this.field_9601.method_8859(arg2.field_1352, arg2.field_1351, arg2.field_1350, arg3.field_5987, arg3.field_6010, arg3.field_6035)) {
			return false;
		} else if (!this.field_9596.method_9018(arg, arg3.field_5987, arg3.field_6010, arg3.field_6035)) {
			return false;
		} else if (!this.field_9594.method_9062(arg3)) {
			return false;
		} else if (!this.field_9600.method_9072(arg3)) {
			return false;
		} else if (!this.field_9597.method_8892(arg3)) {
			return false;
		} else {
			return !this.field_16490.method_16226(arg3)
				? false
				: this.field_16317 == null || arg3 instanceof class_1451 && ((class_1451)arg3).method_16092().equals(this.field_16317);
		}
	}

	public static class_2048 method_8913(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "entity");
			class_2050 lv = class_2050.method_8928(jsonObject.get("type"));
			class_2025 lv2 = class_2025.method_8857(jsonObject.get("distance"));
			class_2090 lv3 = class_2090.method_9021(jsonObject.get("location"));
			class_2102 lv4 = class_2102.method_9064(jsonObject.get("effects"));
			class_2105 lv5 = class_2105.method_9073(jsonObject.get("nbt"));
			class_2040 lv6 = class_2040.method_8893(jsonObject.get("flags"));
			class_3735 lv7 = class_3735.method_16224(jsonObject.get("equipment"));
			class_2960 lv8 = jsonObject.has("catType") ? new class_2960(class_3518.method_15265(jsonObject, "catType")) : null;
			return new class_2048.class_2049()
				.method_8917(lv)
				.method_8924(lv2)
				.method_8918(lv3)
				.method_8923(lv4)
				.method_8915(lv5)
				.method_8919(lv6)
				.method_16227(lv7)
				.method_16112(lv8)
				.method_8920();
		} else {
			return field_9599;
		}
	}

	public static class_2048[] method_8910(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonArray jsonArray = class_3518.method_15252(jsonElement, "entities");
			class_2048[] lvs = new class_2048[jsonArray.size()];

			for (int i = 0; i < jsonArray.size(); i++) {
				lvs[i] = method_8913(jsonArray.get(i));
			}

			return lvs;
		} else {
			return field_9598;
		}
	}

	public JsonElement method_8912() {
		if (this == field_9599) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("type", this.field_9595.method_8927());
			jsonObject.add("distance", this.field_9601.method_8858());
			jsonObject.add("location", this.field_9596.method_9019());
			jsonObject.add("effects", this.field_9594.method_9068());
			jsonObject.add("nbt", this.field_9600.method_9075());
			jsonObject.add("flags", this.field_9597.method_8894());
			jsonObject.add("equipment", this.field_16490.method_16225());
			if (this.field_16317 != null) {
				jsonObject.addProperty("catType", this.field_16317.toString());
			}

			return jsonObject;
		}
	}

	public static JsonElement method_8911(class_2048[] args) {
		if (args == field_9598) {
			return JsonNull.INSTANCE;
		} else {
			JsonArray jsonArray = new JsonArray();

			for (class_2048 lv : args) {
				JsonElement jsonElement = lv.method_8912();
				if (!jsonElement.isJsonNull()) {
					jsonArray.add(jsonElement);
				}
			}

			return jsonArray;
		}
	}

	public static class class_2049 {
		private class_2050 field_9607 = class_2050.field_9609;
		private class_2025 field_9602 = class_2025.field_9553;
		private class_2090 field_9604 = class_2090.field_9685;
		private class_2102 field_9605 = class_2102.field_9709;
		private class_2105 field_9603 = class_2105.field_9716;
		private class_2040 field_9606 = class_2040.field_9581;
		private class_3735 field_16491 = class_3735.field_16485;
		@Nullable
		private class_2960 field_16318;

		public static class_2048.class_2049 method_8916() {
			return new class_2048.class_2049();
		}

		public class_2048.class_2049 method_8921(class_1299<?> arg) {
			this.field_9607 = class_2050.method_8929(arg);
			return this;
		}

		public class_2048.class_2049 method_8922(class_3494<class_1299<?>> arg) {
			this.field_9607 = class_2050.method_8926(arg);
			return this;
		}

		public class_2048.class_2049 method_16113(class_2960 arg) {
			this.field_16318 = arg;
			return this;
		}

		public class_2048.class_2049 method_8917(class_2050 arg) {
			this.field_9607 = arg;
			return this;
		}

		public class_2048.class_2049 method_8924(class_2025 arg) {
			this.field_9602 = arg;
			return this;
		}

		public class_2048.class_2049 method_8918(class_2090 arg) {
			this.field_9604 = arg;
			return this;
		}

		public class_2048.class_2049 method_8923(class_2102 arg) {
			this.field_9605 = arg;
			return this;
		}

		public class_2048.class_2049 method_8915(class_2105 arg) {
			this.field_9603 = arg;
			return this;
		}

		public class_2048.class_2049 method_8919(class_2040 arg) {
			this.field_9606 = arg;
			return this;
		}

		public class_2048.class_2049 method_16227(class_3735 arg) {
			this.field_16491 = arg;
			return this;
		}

		public class_2048.class_2049 method_16112(@Nullable class_2960 arg) {
			this.field_16318 = arg;
			return this;
		}

		public class_2048 method_8920() {
			return new class_2048(
				this.field_9607, this.field_9602, this.field_9604, this.field_9605, this.field_9603, this.field_9606, this.field_16491, this.field_16318
			);
		}
	}
}
