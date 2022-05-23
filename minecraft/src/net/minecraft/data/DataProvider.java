package net.minecraft.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.ToIntFunction;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;

public interface DataProvider {
	ToIntFunction<String> JSON_KEY_SORT_ORDER = Util.make(new Object2IntOpenHashMap<>(), map -> {
		map.put("type", 0);
		map.put("parent", 1);
		map.defaultReturnValue(2);
	});
	Comparator<String> JSON_KEY_SORTING_COMPARATOR = Comparator.comparingInt(JSON_KEY_SORT_ORDER).thenComparing(key -> key);

	void run(DataWriter writer) throws IOException;

	String getName();

	static void writeToPath(DataWriter writer, JsonElement json, Path path) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), byteArrayOutputStream);
		Writer writer2 = new OutputStreamWriter(hashingOutputStream, StandardCharsets.UTF_8);
		JsonWriter jsonWriter = new JsonWriter(writer2);
		jsonWriter.setSerializeNulls(false);
		jsonWriter.setIndent("  ");
		JsonHelper.writeSorted(jsonWriter, json, JSON_KEY_SORTING_COMPARATOR);
		jsonWriter.close();
		writer.write(path, byteArrayOutputStream.toByteArray(), hashingOutputStream.hash());
	}
}
