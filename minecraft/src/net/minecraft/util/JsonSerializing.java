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
import net.minecraft.registry.Registry;

public class JsonSerializing {
	public static <E, T extends JsonSerializableType<E>> JsonSerializing.SerializerBuilder<E, T> createSerializerBuilder(
		Registry<T> registry, String rootFieldName, String idFieldName, Function<E, T> typeGetter
	) {
		return new JsonSerializing.SerializerBuilder<>(registry, rootFieldName, idFieldName, typeGetter);
	}

	public interface ElementSerializer<T> {
		JsonElement toJson(T object, JsonSerializationContext context);

		T fromJson(JsonElement json, JsonDeserializationContext context);
	}

	static class GsonSerializer<E, T extends JsonSerializableType<E>> implements JsonDeserializer<E>, com.google.gson.JsonSerializer<E> {
		private final Registry<T> registry;
		private final String rootFieldName;
		private final String idFieldName;
		private final Function<E, T> typeGetter;
		@Nullable
		private final T defaultType;
		@Nullable
		private final com.mojang.datafixers.util.Pair<T, JsonSerializing.ElementSerializer<? extends E>> elementSerializer;

		GsonSerializer(
			Registry<T> registry,
			String rootFieldName,
			String idFieldName,
			Function<E, T> typeGetter,
			@Nullable T defaultType,
			@Nullable com.mojang.datafixers.util.Pair<T, JsonSerializing.ElementSerializer<? extends E>> elementSerializer
		) {
			this.registry = registry;
			this.rootFieldName = rootFieldName;
			this.idFieldName = idFieldName;
			this.typeGetter = typeGetter;
			this.defaultType = defaultType;
			this.elementSerializer = elementSerializer;
		}

		@Override
		public E deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
			if (json.isJsonObject()) {
				JsonObject jsonObject = JsonHelper.asObject(json, this.rootFieldName);
				String string = JsonHelper.getString(jsonObject, this.idFieldName, "");
				T jsonSerializableType;
				if (string.isEmpty()) {
					jsonSerializableType = this.defaultType;
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
			T jsonSerializableType = (T)this.typeGetter.apply(object);
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
	 * A builder for serializing types to JSON that can either obtain a type from
	 * a registry to handle JSON conversion or handle with a custom logic bound
	 * to a type.
	 * 
	 * <p>When the root element read is an object, the built serializer obtains the type
	 * from registry to handle reading; otherwise, it falls back to custom
	 * logic.
	 */
	public static class SerializerBuilder<E, T extends JsonSerializableType<E>> {
		private final Registry<T> registry;
		private final String rootFieldName;
		private final String idFieldName;
		private final Function<E, T> typeGetter;
		@Nullable
		private com.mojang.datafixers.util.Pair<T, JsonSerializing.ElementSerializer<? extends E>> elementSerializer;
		@Nullable
		private T defaultType;

		SerializerBuilder(Registry<T> registry, String rootFieldName, String idFieldName, Function<E, T> typeIdentification) {
			this.registry = registry;
			this.rootFieldName = rootFieldName;
			this.idFieldName = idFieldName;
			this.typeGetter = typeIdentification;
		}

		/**
		 * Sets the element serializer and its target type. It can serialize and
		 * deserialize instances of one type to non-{@link com.google.gson.JsonObject}
		 * elements.
		 * 
		 * @apiNote There can only be one element serializer for this builder and
		 * the built serializer. Calling this method replaces any previous serializer.
		 * 
		 * @param type the target type of the element serializer
		 * @param serializer the element serializer
		 */
		public JsonSerializing.SerializerBuilder<E, T> elementSerializer(T type, JsonSerializing.ElementSerializer<? extends E> serializer) {
			this.elementSerializer = com.mojang.datafixers.util.Pair.of(type, serializer);
			return this;
		}

		/**
		 * Sets the default type that is used when there's no ID field.
		 * 
		 * @return this instance
		 * 
		 * @param defaultType the default type
		 */
		public JsonSerializing.SerializerBuilder<E, T> defaultType(T defaultType) {
			this.defaultType = defaultType;
			return this;
		}

		public Object build() {
			return new JsonSerializing.GsonSerializer<>(this.registry, this.rootFieldName, this.idFieldName, this.typeGetter, this.defaultType, this.elementSerializer);
		}
	}
}
