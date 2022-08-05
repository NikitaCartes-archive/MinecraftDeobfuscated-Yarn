package net.minecraft.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public class LowercaseEnumTypeAdapterFactory implements TypeAdapterFactory {
	@Nullable
	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
		Class<T> class_ = (Class<T>)typeToken.getRawType();
		if (!class_.isEnum()) {
			return null;
		} else {
			final Map<String, T> map = Maps.<String, T>newHashMap();

			for (T object : class_.getEnumConstants()) {
				map.put(this.getKey(object), object);
			}

			return new TypeAdapter<T>() {
				@Override
				public void write(JsonWriter writer, T o) throws IOException {
					if (o == null) {
						writer.nullValue();
					} else {
						writer.value(LowercaseEnumTypeAdapterFactory.this.getKey(o));
					}
				}

				@Nullable
				@Override
				public T read(JsonReader reader) throws IOException {
					if (reader.peek() == JsonToken.NULL) {
						reader.nextNull();
						return null;
					} else {
						return (T)map.get(reader.nextString());
					}
				}
			};
		}
	}

	String getKey(Object o) {
		return o instanceof Enum ? ((Enum)o).name().toLowerCase(Locale.ROOT) : o.toString().toLowerCase(Locale.ROOT);
	}
}
