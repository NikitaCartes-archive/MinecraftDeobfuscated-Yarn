package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.registry.Registry;

public class JsonSerializing {
	public static <E, T extends JsonSerializableType<E>> JsonSerializing.TypeHandler<E, T> createTypeHandler(
		Registry<T> registry, String rootFieldName, String idFieldName, Function<E, T> typeIdentification
	) {
		return new JsonSerializing.TypeHandler<>(registry, rootFieldName, idFieldName, typeIdentification);
	}

	public interface CustomSerializer<T> {
		JsonElement toJson(T object, JsonSerializationContext context);

		T fromJson(JsonElement json, JsonDeserializationContext context);
	}

	static class GsonSerializer<E, T extends JsonSerializableType<E>> implements JsonDeserializer<E>, com.google.gson.JsonSerializer<E> {
		private final Registry<T> registry;
		private final String rootFieldName;
		private final String idFieldName;
		private final Function<E, T> typeIdentification;
		@Nullable
		private final T field_28445;
		@Nullable
		private final com.mojang.datafixers.util.Pair<T, JsonSerializing.CustomSerializer<? extends E>> elementSerializer;

		private GsonSerializer(
			Registry<T> registry,
			String rootFieldName,
			String idFieldName,
			Function<E, T> typeIdentification,
			@Nullable T jsonSerializableType,
			@Nullable com.mojang.datafixers.util.Pair<T, JsonSerializing.CustomSerializer<? extends E>> elementSerializer
		) {
			this.registry = registry;
			this.rootFieldName = rootFieldName;
			this.idFieldName = idFieldName;
			this.typeIdentification = typeIdentification;
			this.field_28445 = jsonSerializableType;
			this.elementSerializer = elementSerializer;
		}

		@Override
		public E deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
			if (json.isJsonObject()) {
				JsonObject jsonObject = JsonHelper.asObject(json, this.rootFieldName);
				String string = JsonHelper.getString(jsonObject, this.idFieldName, "");
				T jsonSerializableType;
				if (string.isEmpty()) {
					jsonSerializableType = this.field_28445;
				} else {
					Identifier identifier = new Identifier(string);
					jsonSerializableType = this.registry.get(identifier);
				}

				if (jsonSerializableType == null) {
					throw new JsonSyntaxException("Unknown type '" + string + "'");
				} else {
					return (E)jsonSerializableType.getJsonSerializer().fromJson(jsonObject, context);
				}
			} else if (this.elementSerializer == null) {
				throw new UnsupportedOperationException("Object " + json + " can't be deserialized");
			} else {
				return (E)this.elementSerializer.getSecond().fromJson(json, context);
			}
		}

		@Override
		public JsonElement serialize(E object, Type type, JsonSerializationContext context) {
			T jsonSerializableType = (T)this.typeIdentification.apply(object);
			if (this.elementSerializer != null && this.elementSerializer.getFirst() == jsonSerializableType) {
				return this.elementSerializer.getSecond().toJson(object, context);
			} else if (jsonSerializableType == null) {
				throw new JsonSyntaxException("Unknown type: " + object);
			} else {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty(this.idFieldName, this.registry.getId(jsonSerializableType).toString());
				jsonSerializableType.getJsonSerializer().toJson(jsonObject, object, context);
				return jsonObject;
			}
		}
	}

	/**
	 * A handler of JSON serializable types that can either obtain a type from
	 * a registry to handle JSON conversion or handle with a custom logic bound
	 * to a type.
	 * 
	 * <p>When the root element read is an object, the handler obtains the type
	 * from registry to handle reading; otherwise, it falls back to custom
	 * logic.
	 */
	public static class TypeHandler<E, T extends JsonSerializableType<E>> {
		private final Registry<T> registry;
		private final String rootFieldName;
		private final String idFieldName;
		private final Function<E, T> typeIdentification;
		@Nullable
		private com.mojang.datafixers.util.Pair<T, JsonSerializing.CustomSerializer<? extends E>> customSerializer;
		@Nullable
		private T field_28444;

		private TypeHandler(Registry<T> registry, String rootFieldName, String idFieldName, Function<E, T> typeIdentification) {
			this.registry = registry;
			this.rootFieldName = rootFieldName;
			this.idFieldName = idFieldName;
			this.typeIdentification = typeIdentification;
		}

		public JsonSerializing.TypeHandler<E, T> method_32385(T jsonSerializableType, JsonSerializing.CustomSerializer<? extends E> customSerializer) {
			this.customSerializer = com.mojang.datafixers.util.Pair.of(jsonSerializableType, customSerializer);
			return this;
		}

		public JsonSerializing.TypeHandler<E, T> method_33409(T jsonSerializableType) {
			this.field_28444 = jsonSerializableType;
			return this;
		}

		public Object createGsonSerializer() {
			return new JsonSerializing.GsonSerializer(
				this.registry, this.rootFieldName, this.idFieldName, this.typeIdentification, this.field_28444, this.customSerializer
			);
		}
	}
}
