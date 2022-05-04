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
	ToIntFunction<String> field_39212 = Util.make(new Object2IntOpenHashMap<>(), object2IntOpenHashMap -> {
		object2IntOpenHashMap.put("type", 0);
		object2IntOpenHashMap.put("parent", 1);
		object2IntOpenHashMap.defaultReturnValue(2);
	});
	Comparator<String> field_39213 = Comparator.comparingInt(field_39212).thenComparing(string -> string);

	void run(DataWriter cache) throws IOException;

	String getName();

	static void writeToPath(DataWriter dataWriter, JsonElement jsonElement, Path path) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), byteArrayOutputStream);
		Writer writer = new OutputStreamWriter(hashingOutputStream, StandardCharsets.UTF_8);
		JsonWriter jsonWriter = new JsonWriter(writer);
		jsonWriter.setSerializeNulls(false);
		jsonWriter.setIndent("  ");
		JsonHelper.writeSorted(jsonWriter, jsonElement, field_39213);
		jsonWriter.close();
		dataWriter.write(path, byteArrayOutputStream.toByteArray(), hashingOutputStream.hash());
	}
}
