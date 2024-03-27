package net.minecraft.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.lang.reflect.Field;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.function.CharPredicate;

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
			return POS.getInt(jsonReader) - LINE_START.getInt(jsonReader);
		} catch (IllegalAccessException var2) {
			throw new IllegalStateException("Couldn't read position of JsonReader", var2);
		}
	}

	public static <T> T parse(RegistryWrapper.WrapperLookup registryLookup, StringReader stringReader, Codec<T> codec) {
		JsonReader jsonReader = new JsonReader(new java.io.StringReader(stringReader.getRemaining()));
		jsonReader.setLenient(false);

		Object var5;
		try {
			JsonElement jsonElement = Streams.parse(jsonReader);
			var5 = codec.parse(registryLookup.getOps(JsonOps.INSTANCE), jsonElement).getOrThrow(JsonParseException::new);
		} catch (StackOverflowError var9) {
			throw new JsonParseException(var9);
		} finally {
			stringReader.setCursor(stringReader.getCursor() + getPos(jsonReader));
		}

		return (T)var5;
	}

	public static String readWhileMatching(StringReader stringReader, CharPredicate predicate) {
		int i = stringReader.getCursor();

		while (stringReader.canRead() && predicate.test(stringReader.peek())) {
			stringReader.skip();
		}

		return stringReader.getString().substring(i, stringReader.getCursor());
	}
}
