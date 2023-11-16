package net.minecraft.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.lang.reflect.Field;

public class JsonReaderUtils {
	private static final Field POS = Util.make(() -> {
		try {
			Field field = JsonReader.class.getDeclaredField("pos");
			field.setAccessible(true);
			return field;
		} catch (NoSuchFieldException var1) {
			throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", var1);
		}
	});
	private static final Field LINE_START = Util.make(() -> {
		try {
			Field field = JsonReader.class.getDeclaredField("lineStart");
			field.setAccessible(true);
			return field;
		} catch (NoSuchFieldException var1) {
			throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", var1);
		}
	});

	private static int getPos(JsonReader jsonReader) {
		try {
			return POS.getInt(jsonReader) - LINE_START.getInt(jsonReader) + 1;
		} catch (IllegalAccessException var2) {
			throw new IllegalStateException("Couldn't read position of JsonReader", var2);
		}
	}

	public static <T> T parse(StringReader stringReader, Codec<T> codec) {
		JsonReader jsonReader = new JsonReader(new java.io.StringReader(stringReader.getRemaining()));
		jsonReader.setLenient(false);

		Object var4;
		try {
			JsonElement jsonElement = Streams.parse(jsonReader);
			var4 = Util.getResult(codec.parse(JsonOps.INSTANCE, jsonElement), JsonParseException::new);
		} catch (StackOverflowError var8) {
			throw new JsonParseException(var8);
		} finally {
			stringReader.setCursor(stringReader.getCursor() + getPos(jsonReader));
		}

		return (T)var4;
	}
}
