package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializableType;
import net.minecraft.util.registry.Registry;

public class class_5330 {
	public static <E, T extends JsonSerializableType<E>> class_5330.class_5331<E, T> method_29306(
		Registry<T> registry, String string, String string2, Function<E, T> function
	) {
		return new class_5330.class_5331<>(registry, string, string2, function);
	}

	public static class class_5331<E, T extends JsonSerializableType<E>> {
		private final Registry<T> field_25192;
		private final String field_25193;
		private final String field_25194;
		private final Function<E, T> field_25195;
		@Nullable
		private Pair<T, class_5330.class_5332<? extends E>> field_25196;

		private class_5331(Registry<T> registry, String string, String string2, Function<E, T> function) {
			this.field_25192 = registry;
			this.field_25193 = string;
			this.field_25194 = string2;
			this.field_25195 = function;
		}

		public Object method_29307() {
			return new class_5330.class_5333(this.field_25192, this.field_25193, this.field_25194, this.field_25195, this.field_25196);
		}
	}

	public interface class_5332<T> {
		JsonElement method_29309(T object, JsonSerializationContext jsonSerializationContext);

		T method_29308(JsonElement jsonElement, JsonDeserializationContext jsonDeserializationContext);
	}

	static class class_5333<E, T extends JsonSerializableType<E>> implements JsonDeserializer<E>, JsonSerializer<E> {
		private final Registry<T> field_25197;
		private final String field_25198;
		private final String field_25199;
		private final Function<E, T> field_25200;
		@Nullable
		private final Pair<T, class_5330.class_5332<? extends E>> field_25201;

		private class_5333(Registry<T> registry, String string, String string2, Function<E, T> function, @Nullable Pair<T, class_5330.class_5332<? extends E>> pair) {
			this.field_25197 = registry;
			this.field_25198 = string;
			this.field_25199 = string2;
			this.field_25200 = function;
			this.field_25201 = pair;
		}

		@Override
		public E deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonObject()) {
				JsonObject jsonObject = JsonHelper.asObject(jsonElement, this.field_25198);
				Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, this.field_25199));
				T jsonSerializableType = this.field_25197.get(identifier);
				if (jsonSerializableType == null) {
					throw new JsonSyntaxException("Unknown type '" + identifier + "'");
				} else {
					return (E)jsonSerializableType.getJsonSerializer().fromJson(jsonObject, jsonDeserializationContext);
				}
			} else if (this.field_25201 == null) {
				throw new UnsupportedOperationException("Object " + jsonElement + " can't be deserialized");
			} else {
				return (E)this.field_25201.getSecond().method_29308(jsonElement, jsonDeserializationContext);
			}
		}

		@Override
		public JsonElement serialize(E object, Type type, JsonSerializationContext jsonSerializationContext) {
			T jsonSerializableType = (T)this.field_25200.apply(object);
			if (this.field_25201 != null && this.field_25201.getFirst() == jsonSerializableType) {
				return this.field_25201.getSecond().method_29309(object, jsonSerializationContext);
			} else if (jsonSerializableType == null) {
				throw new JsonSyntaxException("Unknown type: " + object);
			} else {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty(this.field_25199, this.field_25197.getId(jsonSerializableType).toString());
				jsonSerializableType.getJsonSerializer().toJson(jsonObject, object, jsonSerializationContext);
				return jsonObject;
			}
		}
	}
}
