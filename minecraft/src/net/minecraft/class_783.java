package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_783 {
	public final class_2350 field_4225;
	public final int field_4226;
	public final String field_4224;
	public final class_787 field_4227;

	public class_783(@Nullable class_2350 arg, int i, String string, class_787 arg2) {
		this.field_4225 = arg;
		this.field_4226 = i;
		this.field_4224 = string;
		this.field_4227 = arg2;
	}

	@Environment(EnvType.CLIENT)
	public static class class_784 implements JsonDeserializer<class_783> {
		protected class_784() {
		}

		public class_783 method_3397(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			class_2350 lv = this.method_3398(jsonObject);
			int i = this.method_3400(jsonObject);
			String string = this.method_3399(jsonObject);
			class_787 lv2 = jsonDeserializationContext.deserialize(jsonObject, class_787.class);
			return new class_783(lv, i, string, lv2);
		}

		protected int method_3400(JsonObject jsonObject) {
			return class_3518.method_15282(jsonObject, "tintindex", -1);
		}

		private String method_3399(JsonObject jsonObject) {
			return class_3518.method_15265(jsonObject, "texture");
		}

		@Nullable
		private class_2350 method_3398(JsonObject jsonObject) {
			String string = class_3518.method_15253(jsonObject, "cullface", "");
			return class_2350.method_10168(string);
		}
	}
}
