package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_1078 {
	private static final Gson field_5333 = new Gson();
	private static final Logger field_5332 = LogManager.getLogger();
	private static final Pattern field_5331 = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
	protected final Map<String, String> field_5330 = Maps.<String, String>newHashMap();

	public synchronized void method_4675(class_3300 arg, List<String> list) {
		this.field_5330.clear();

		for (String string : list) {
			String string2 = String.format("lang/%s.json", string);

			for (String string3 : arg.method_14487()) {
				try {
					class_2960 lv = new class_2960(string3, string2);
					this.method_4676(arg.method_14489(lv));
				} catch (FileNotFoundException var9) {
				} catch (Exception var10) {
					field_5332.warn("Skipped language file: {}:{} ({})", string3, string2, var10.toString());
				}
			}
		}
	}

	private void method_4676(List<class_3298> list) {
		for (class_3298 lv : list) {
			InputStream inputStream = lv.method_14482();

			try {
				this.method_4674(inputStream);
			} finally {
				IOUtils.closeQuietly(inputStream);
			}
		}
	}

	private void method_4674(InputStream inputStream) {
		JsonElement jsonElement = field_5333.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonElement.class);
		JsonObject jsonObject = class_3518.method_15295(jsonElement, "strings");

		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String string = field_5331.matcher(class_3518.method_15287((JsonElement)entry.getValue(), (String)entry.getKey())).replaceAll("%$1s");
			this.field_5330.put(entry.getKey(), string);
		}
	}

	private String method_4679(String string) {
		String string2 = (String)this.field_5330.get(string);
		return string2 == null ? string : string2;
	}

	public String method_4677(String string, Object[] objects) {
		String string2 = this.method_4679(string);

		try {
			return String.format(string2, objects);
		} catch (IllegalFormatException var5) {
			return "Format error: " + string2;
		}
	}

	public boolean method_4678(String string) {
		return this.field_5330.containsKey(string);
	}
}
