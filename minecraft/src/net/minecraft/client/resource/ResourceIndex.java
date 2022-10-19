package net.minecraft.client.resource;

import com.google.common.base.Splitter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.fs.ResourceFileSystem;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ResourceIndex {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Splitter SEPARATOR_SPLITTER = Splitter.on('/');

	/**
	 * Builds the resource file system from the index.
	 * 
	 * @return the root path of the resource file system
	 */
	public static Path buildFileSystem(Path assetsDir, String indexName) {
		Path path = assetsDir.resolve("objects");
		ResourceFileSystem.Builder builder = ResourceFileSystem.builder();
		Path path2 = assetsDir.resolve("indexes/" + indexName + ".json");

		try {
			BufferedReader bufferedReader = Files.newBufferedReader(path2, StandardCharsets.UTF_8);

			try {
				JsonObject jsonObject = JsonHelper.deserialize(bufferedReader);
				JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "objects", null);
				if (jsonObject2 != null) {
					for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
						JsonObject jsonObject3 = (JsonObject)entry.getValue();
						String string = (String)entry.getKey();
						List<String> list = SEPARATOR_SPLITTER.splitToList(string);
						String string2 = JsonHelper.getString(jsonObject3, "hash");
						Path path3 = path.resolve(string2.substring(0, 2) + "/" + string2);
						builder.withFile(list, path3);
					}
				}
			} catch (Throwable var16) {
				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (Throwable var15) {
						var16.addSuppressed(var15);
					}
				}

				throw var16;
			}

			if (bufferedReader != null) {
				bufferedReader.close();
			}
		} catch (JsonParseException var17) {
			LOGGER.error("Unable to parse resource index file: {}", path2);
		} catch (IOException var18) {
			LOGGER.error("Can't open the resource index file: {}", path2);
		}

		return builder.build("index-" + indexName).getPath("/");
	}
}
