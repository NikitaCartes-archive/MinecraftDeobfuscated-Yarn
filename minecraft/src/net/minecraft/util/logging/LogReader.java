package net.minecraft.util.logging;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import javax.annotation.Nullable;
import net.minecraft.util.Util;

public interface LogReader<T> extends Closeable {
	static <T> LogReader<T> create(Codec<T> codec, Reader reader) {
		final JsonReader jsonReader = new JsonReader(reader);
		jsonReader.setLenient(true);
		return new LogReader<T>() {
			@Nullable
			@Override
			public T read() throws IOException {
				try {
					if (!jsonReader.hasNext()) {
						return null;
					} else {
						JsonElement jsonElement = JsonParser.parseReader(jsonReader);
						return Util.getResult(codec.parse(JsonOps.INSTANCE, jsonElement), IOException::new);
					}
				} catch (JsonParseException var2) {
					throw new IOException(var2);
				} catch (EOFException var3) {
					return null;
				}
			}

			public void close() throws IOException {
				jsonReader.close();
			}
		};
	}

	@Nullable
	T read() throws IOException;
}
