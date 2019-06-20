package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_185 {
	private final class_2561 field_1240;
	private final class_2561 field_1242;
	private final class_1799 field_1241;
	private final class_2960 field_1243;
	private final class_189 field_1237;
	private final boolean field_1239;
	private final boolean field_1238;
	private final boolean field_1236;
	private float field_1245;
	private float field_1244;

	public class_185(class_1799 arg, class_2561 arg2, class_2561 arg3, @Nullable class_2960 arg4, class_189 arg5, boolean bl, boolean bl2, boolean bl3) {
		this.field_1240 = arg2;
		this.field_1242 = arg3;
		this.field_1241 = arg;
		this.field_1243 = arg4;
		this.field_1237 = arg5;
		this.field_1239 = bl;
		this.field_1238 = bl2;
		this.field_1236 = bl3;
	}

	public void method_816(float f, float g) {
		this.field_1245 = f;
		this.field_1244 = g;
	}

	public class_2561 method_811() {
		return this.field_1240;
	}

	public class_2561 method_817() {
		return this.field_1242;
	}

	@Environment(EnvType.CLIENT)
	public class_1799 method_821() {
		return this.field_1241;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_2960 method_812() {
		return this.field_1243;
	}

	public class_189 method_815() {
		return this.field_1237;
	}

	@Environment(EnvType.CLIENT)
	public float method_818() {
		return this.field_1245;
	}

	@Environment(EnvType.CLIENT)
	public float method_819() {
		return this.field_1244;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_823() {
		return this.field_1239;
	}

	public boolean method_808() {
		return this.field_1238;
	}

	public boolean method_824() {
		return this.field_1236;
	}

	public static class_185 method_809(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2561 lv = class_3518.method_15272(jsonObject, "title", jsonDeserializationContext, class_2561.class);
		class_2561 lv2 = class_3518.method_15272(jsonObject, "description", jsonDeserializationContext, class_2561.class);
		if (lv != null && lv2 != null) {
			class_1799 lv3 = method_822(class_3518.method_15296(jsonObject, "icon"));
			class_2960 lv4 = jsonObject.has("background") ? new class_2960(class_3518.method_15265(jsonObject, "background")) : null;
			class_189 lv5 = jsonObject.has("frame") ? class_189.method_833(class_3518.method_15265(jsonObject, "frame")) : class_189.field_1254;
			boolean bl = class_3518.method_15258(jsonObject, "show_toast", true);
			boolean bl2 = class_3518.method_15258(jsonObject, "announce_to_chat", true);
			boolean bl3 = class_3518.method_15258(jsonObject, "hidden", false);
			return new class_185(lv3, lv, lv2, lv4, lv5, bl, bl2, bl3);
		} else {
			throw new JsonSyntaxException("Both title and description must be set");
		}
	}

	private static class_1799 method_822(JsonObject jsonObject) {
		if (!jsonObject.has("item")) {
			throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
		} else {
			class_1792 lv = class_3518.method_15288(jsonObject, "item");
			if (jsonObject.has("data")) {
				throw new JsonParseException("Disallowed data tag found");
			} else {
				class_1799 lv2 = new class_1799(lv);
				if (jsonObject.has("nbt")) {
					try {
						class_2487 lv3 = class_2522.method_10718(class_3518.method_15287(jsonObject.get("nbt"), "nbt"));
						lv2.method_7980(lv3);
					} catch (CommandSyntaxException var4) {
						throw new JsonSyntaxException("Invalid nbt tag: " + var4.getMessage());
					}
				}

				return lv2;
			}
		}
	}

	public void method_813(class_2540 arg) {
		arg.method_10805(this.field_1240);
		arg.method_10805(this.field_1242);
		arg.method_10793(this.field_1241);
		arg.method_10817(this.field_1237);
		int i = 0;
		if (this.field_1243 != null) {
			i |= 1;
		}

		if (this.field_1239) {
			i |= 2;
		}

		if (this.field_1236) {
			i |= 4;
		}

		arg.writeInt(i);
		if (this.field_1243 != null) {
			arg.method_10812(this.field_1243);
		}

		arg.writeFloat(this.field_1245);
		arg.writeFloat(this.field_1244);
	}

	public static class_185 method_820(class_2540 arg) {
		class_2561 lv = arg.method_10808();
		class_2561 lv2 = arg.method_10808();
		class_1799 lv3 = arg.method_10819();
		class_189 lv4 = arg.method_10818(class_189.class);
		int i = arg.readInt();
		class_2960 lv5 = (i & 1) != 0 ? arg.method_10810() : null;
		boolean bl = (i & 2) != 0;
		boolean bl2 = (i & 4) != 0;
		class_185 lv6 = new class_185(lv3, lv, lv2, lv5, lv4, bl, false, bl2);
		lv6.method_816(arg.readFloat(), arg.readFloat());
		return lv6;
	}

	public JsonElement method_814() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("icon", this.method_810());
		jsonObject.add("title", class_2561.class_2562.method_10868(this.field_1240));
		jsonObject.add("description", class_2561.class_2562.method_10868(this.field_1242));
		jsonObject.addProperty("frame", this.field_1237.method_831());
		jsonObject.addProperty("show_toast", this.field_1239);
		jsonObject.addProperty("announce_to_chat", this.field_1238);
		jsonObject.addProperty("hidden", this.field_1236);
		if (this.field_1243 != null) {
			jsonObject.addProperty("background", this.field_1243.toString());
		}

		return jsonObject;
	}

	private JsonObject method_810() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("item", class_2378.field_11142.method_10221(this.field_1241.method_7909()).toString());
		if (this.field_1241.method_7985()) {
			jsonObject.addProperty("nbt", this.field_1241.method_7969().toString());
		}

		return jsonObject;
	}
}
