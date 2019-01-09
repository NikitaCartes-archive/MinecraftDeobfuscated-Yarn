package net.minecraft;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_1064 {
	protected static final Logger field_5290 = LogManager.getLogger();
	private final Map<String, File> field_5289 = Maps.<String, File>newHashMap();

	protected class_1064() {
	}

	public class_1064(File file, String string) {
		File file2 = new File(file, "objects");
		File file3 = new File(file, "indexes/" + string + ".json");
		BufferedReader bufferedReader = null;

		try {
			bufferedReader = Files.newReader(file3, StandardCharsets.UTF_8);
			JsonObject jsonObject = class_3518.method_15255(bufferedReader);
			JsonObject jsonObject2 = class_3518.method_15281(jsonObject, "objects", null);
			if (jsonObject2 != null) {
				for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
					JsonObject jsonObject3 = (JsonObject)entry.getValue();
					String string2 = (String)entry.getKey();
					String[] strings = string2.split("/", 2);
					String string3 = strings.length == 1 ? strings[0] : strings[0] + ":" + strings[1];
					String string4 = class_3518.method_15265(jsonObject3, "hash");
					File file4 = new File(file2, string4.substring(0, 2) + "/" + string4);
					this.field_5289.put(string3, file4);
				}
			}
		} catch (JsonParseException var20) {
			field_5290.error("Unable to parse resource index file: {}", file3);
		} catch (FileNotFoundException var21) {
			field_5290.error("Can't find the resource index file: {}", file3);
		} finally {
			IOUtils.closeQuietly(bufferedReader);
		}
	}

	@Nullable
	public File method_4630(class_2960 arg) {
		return this.method_4631(arg.toString());
	}

	@Nullable
	public File method_4631(String string) {
		return (File)this.field_5289.get(string);
	}

	public Collection<String> method_4632(String string, int i, Predicate<String> predicate) {
		return (Collection<String>)this.field_5289
			.keySet()
			.stream()
			.filter(stringx -> !stringx.endsWith(".mcmeta"))
			.map(class_2960::new)
			.map(class_2960::method_12832)
			.filter(string2 -> string2.startsWith(string + "/"))
			.filter(predicate)
			.collect(Collectors.toList());
	}
}
