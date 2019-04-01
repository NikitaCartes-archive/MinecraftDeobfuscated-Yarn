package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2989 implements class_4013 {
	private static final Logger field_13406 = LogManager.getLogger();
	private static final Gson field_13405 = new GsonBuilder()
		.registerTypeHierarchyAdapter(class_161.class_162.class, (JsonDeserializer<class_161.class_162>)(jsonElement, type, jsonDeserializationContext) -> {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "advancement");
			return class_161.class_162.method_692(jsonObject, jsonDeserializationContext);
		})
		.registerTypeAdapter(class_170.class, new class_170.class_172())
		.registerTypeHierarchyAdapter(class_2561.class, new class_2561.class_2562())
		.registerTypeHierarchyAdapter(class_2583.class, new class_2583.class_2584())
		.registerTypeAdapterFactory(new class_3530())
		.create();
	private static final class_163 field_13404 = new class_163();
	public static final int field_13403 = "advancements/".length();
	public static final int field_13402 = ".json".length();
	private boolean field_13401;

	private Map<class_2960, class_161.class_162> method_12894(class_3300 arg) {
		Map<class_2960, class_161.class_162> map = Maps.<class_2960, class_161.class_162>newHashMap();

		for (class_2960 lv : arg.method_14488("advancements", stringx -> stringx.endsWith(".json"))) {
			String string = lv.method_12832();
			class_2960 lv2 = new class_2960(lv.method_12836(), string.substring(field_13403, string.length() - field_13402));

			try {
				class_3298 lv3 = arg.method_14486(lv);
				Throwable var8 = null;

				try {
					class_161.class_162 lv4 = class_3518.method_15284(field_13405, IOUtils.toString(lv3.method_14482(), StandardCharsets.UTF_8), class_161.class_162.class);
					if (lv4 == null) {
						field_13406.error("Couldn't load custom advancement {} from {} as it's empty or null", lv2, lv);
					} else {
						map.put(lv2, lv4);
					}
				} catch (Throwable var19) {
					var8 = var19;
					throw var19;
				} finally {
					if (lv3 != null) {
						if (var8 != null) {
							try {
								lv3.close();
							} catch (Throwable var18) {
								var8.addSuppressed(var18);
							}
						} else {
							lv3.close();
						}
					}
				}
			} catch (IllegalArgumentException | class_151 | JsonParseException var21) {
				field_13406.error("Parsing error loading custom advancement {}: {}", lv2, var21.getMessage());
				this.field_13401 = true;
			} catch (IOException var22) {
				field_13406.error("Couldn't read custom advancement {} from {}", lv2, lv, var22);
				this.field_13401 = true;
			}
		}

		return map;
	}

	@Nullable
	public class_161 method_12896(class_2960 arg) {
		return field_13404.method_716(arg);
	}

	public Collection<class_161> method_12893() {
		return field_13404.method_712();
	}

	@Override
	public void method_14491(class_3300 arg) {
		this.field_13401 = false;
		field_13404.method_714();
		Map<class_2960, class_161.class_162> map = this.method_12894(arg);
		field_13404.method_711(map);

		for (class_161 lv : field_13404.method_715()) {
			if (lv.method_686() != null) {
				class_194.method_852(lv);
			}
		}
	}
}
