package net.minecraft.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.function.ToIntFunction;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public interface DataProvider {
	ToIntFunction<String> JSON_KEY_SORT_ORDER = Util.make(new Object2IntOpenHashMap<>(), map -> {
		map.put("type", 0);
		map.put("parent", 1);
		map.defaultReturnValue(2);
	});
	Comparator<String> JSON_KEY_SORTING_COMPARATOR = Comparator.comparingInt(JSON_KEY_SORT_ORDER).thenComparing(key -> key);
	Logger LOGGER = LogUtils.getLogger();

	CompletableFuture<?> run(DataWriter writer);

	String getName();

	static CompletableFuture<?> writeToPath(DataWriter writer, JsonElement json, Path path) {
		return CompletableFuture.runAsync(() -> {
			try {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), byteArrayOutputStream);
				JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(hashingOutputStream, StandardCharsets.UTF_8));

				try {
					jsonWriter.setSerializeNulls(false);
					jsonWriter.setIndent("  ");
					JsonHelper.writeSorted(jsonWriter, json, JSON_KEY_SORTING_COMPARATOR);
				} catch (Throwable var9) {
					try {
						jsonWriter.close();
					} catch (Throwable var8) {
						var9.addSuppressed(var8);
					}

					throw var9;
				}

				jsonWriter.close();
				writer.write(path, byteArrayOutputStream.toByteArray(), hashingOutputStream.hash());
			} catch (IOException var10) {
				LOGGER.error("Failed to save file to {}", path, var10);
			}
		}, Util.getMainWorkerExecutor());
	}

	@FunctionalInterface
	public interface Factory<T extends DataProvider> {
		T create(DataOutput output);
	}
}
