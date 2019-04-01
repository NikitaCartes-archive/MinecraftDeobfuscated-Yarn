package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_813 implements class_3665 {
	private final class_2960 field_4326;
	private final class_1086 field_4328;
	private final boolean field_4327;
	private final int field_4325;

	public class_813(class_2960 arg, class_1086 arg2, boolean bl, int i) {
		this.field_4326 = arg;
		this.field_4328 = arg2;
		this.field_4327 = bl;
		this.field_4325 = i;
	}

	public class_2960 method_3510() {
		return this.field_4326;
	}

	@Override
	public class_1086 method_3509() {
		return this.field_4328;
	}

	@Override
	public boolean method_3512() {
		return this.field_4327;
	}

	public int method_3511() {
		return this.field_4325;
	}

	public String toString() {
		return "Variant{modelLocation=" + this.field_4326 + ", rotation=" + this.field_4328 + ", uvLock=" + this.field_4327 + ", weight=" + this.field_4325 + '}';
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_813)) {
			return false;
		} else {
			class_813 lv = (class_813)object;
			return this.field_4326.equals(lv.field_4326) && this.field_4328 == lv.field_4328 && this.field_4327 == lv.field_4327 && this.field_4325 == lv.field_4325;
		}
	}

	public int hashCode() {
		int i = this.field_4326.hashCode();
		i = 31 * i + this.field_4328.hashCode();
		i = 31 * i + Boolean.valueOf(this.field_4327).hashCode();
		return 31 * i + this.field_4325;
	}

	@Environment(EnvType.CLIENT)
	public static class class_814 implements JsonDeserializer<class_813> {
		public class_813 method_3513(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			class_2960 lv = this.method_3514(jsonObject);
			class_1086 lv2 = this.method_3515(jsonObject);
			boolean bl = this.method_3516(jsonObject);
			int i = this.method_3517(jsonObject);
			return new class_813(lv, lv2, bl, i);
		}

		private boolean method_3516(JsonObject jsonObject) {
			return class_3518.method_15258(jsonObject, "uvlock", false);
		}

		protected class_1086 method_3515(JsonObject jsonObject) {
			int i = class_3518.method_15282(jsonObject, "x", 0);
			int j = class_3518.method_15282(jsonObject, "y", 0);
			class_1086 lv = class_1086.method_4699(i, j);
			if (lv == null) {
				throw new JsonParseException("Invalid BlockModelRotation x: " + i + ", y: " + j);
			} else {
				return lv;
			}
		}

		protected class_2960 method_3514(JsonObject jsonObject) {
			return new class_2960(class_3518.method_15265(jsonObject, "model"));
		}

		protected int method_3517(JsonObject jsonObject) {
			int i = class_3518.method_15282(jsonObject, "weight", 1);
			if (i < 1) {
				throw new JsonParseException("Invalid weight " + i + " found, expected integer >= 1");
			} else {
				return i;
			}
		}
	}
}
