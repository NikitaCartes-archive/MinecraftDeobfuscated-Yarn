package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;

public class class_2105 {
	public static final class_2105 field_9716 = new class_2105(null);
	@Nullable
	private final class_2487 field_9715;

	public class_2105(@Nullable class_2487 arg) {
		this.field_9715 = arg;
	}

	public boolean method_9074(class_1799 arg) {
		return this == field_9716 ? true : this.method_9077(arg.method_7969());
	}

	public boolean method_9072(class_1297 arg) {
		return this == field_9716 ? true : this.method_9077(method_9076(arg));
	}

	public boolean method_9077(@Nullable class_2520 arg) {
		return arg == null ? this == field_9716 : this.field_9715 == null || class_2512.method_10687(this.field_9715, arg, true);
	}

	public JsonElement method_9075() {
		return (JsonElement)(this != field_9716 && this.field_9715 != null ? new JsonPrimitive(this.field_9715.toString()) : JsonNull.INSTANCE);
	}

	public static class_2105 method_9073(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			class_2487 lv;
			try {
				lv = class_2522.method_10718(class_3518.method_15287(jsonElement, "nbt"));
			} catch (CommandSyntaxException var3) {
				throw new JsonSyntaxException("Invalid nbt tag: " + var3.getMessage());
			}

			return new class_2105(lv);
		} else {
			return field_9716;
		}
	}

	public static class_2487 method_9076(class_1297 arg) {
		class_2487 lv = arg.method_5647(new class_2487());
		if (arg instanceof class_1657) {
			class_1799 lv2 = ((class_1657)arg).field_7514.method_7391();
			if (!lv2.method_7960()) {
				lv.method_10566("SelectedItem", lv2.method_7953(new class_2487()));
			}
		}

		return lv;
	}
}
