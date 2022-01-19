package net.minecraft.client.resource;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ResourceIndex {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Map<String, File> rootIndex = Maps.<String, File>newHashMap();
	private final Map<Identifier, File> namespacedIndex = Maps.<Identifier, File>newHashMap();

	protected ResourceIndex() {
	}

	public ResourceIndex(File directory, String indexName) {
		File file = new File(directory, "objects");
		File file2 = new File(directory, "indexes/" + indexName + ".json");
		BufferedReader bufferedReader = null;

		try {
			bufferedReader = Files.newReader(file2, StandardCharsets.UTF_8);
			JsonObject jsonObject = JsonHelper.deserialize(bufferedReader);
			JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "objects", null);
			if (jsonObject2 != null) {
				for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
					JsonObject jsonObject3 = (JsonObject)entry.getValue();
					String string = (String)entry.getKey();
					String[] strings = string.split("/", 2);
					String string2 = JsonHelper.getString(jsonObject3, "hash");
					File file3 = new File(file, string2.substring(0, 2) + "/" + string2);
					if (strings.length == 1) {
						this.rootIndex.put(strings[0], file3);
					} else {
						this.namespacedIndex.put(new Identifier(strings[0], strings[1]), file3);
					}
				}
			}
		} catch (JsonParseException var19) {
			LOGGER.error("Unable to parse resource index file: {}", file2);
		} catch (FileNotFoundException var20) {
			LOGGER.error("Can't find the resource index file: {}", file2);
		} finally {
			IOUtils.closeQuietly(bufferedReader);
		}
	}

	@Nullable
	public File getResource(Identifier identifier) {
		return (File)this.namespacedIndex.get(identifier);
	}

	@Nullable
	public File findFile(String path) {
		return (File)this.rootIndex.get(path);
	}

	public Collection<Identifier> getFilesRecursively(String prefix, String namespace, int maxDepth, Predicate<String> pathFilter) {
		return (Collection<Identifier>)this.namespacedIndex.keySet().stream().filter(id -> {
			String string3 = id.getPath();
			return id.getNamespace().equals(namespace) && !string3.endsWith(".mcmeta") && string3.startsWith(prefix + "/") && pathFilter.test(string3);
		}).collect(Collectors.toList());
	}
}
