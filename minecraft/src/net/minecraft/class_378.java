package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_378 implements class_3302 {
	private static final Logger field_2261 = LogManager.getLogger();
	private final Map<class_2960, class_327> field_2259 = Maps.<class_2960, class_327>newHashMap();
	private final class_1060 field_2260;
	private boolean field_2258;

	public class_378(class_1060 arg, boolean bl) {
		this.field_2260 = arg;
		this.field_2258 = bl;
	}

	@Override
	public void method_14491(class_3300 arg) {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		Map<class_2960, List<class_390>> map = Maps.<class_2960, List<class_390>>newHashMap();

		for (class_2960 lv : arg.method_14488("font", stringx -> stringx.endsWith(".json"))) {
			String string = lv.method_12832();
			class_2960 lv2 = new class_2960(lv.method_12836(), string.substring("font/".length(), string.length() - ".json".length()));
			List<class_390> list = (List<class_390>)map.computeIfAbsent(lv2, argx -> Lists.<class_390>newArrayList(new class_376()));

			try {
				for (class_3298 lv3 : arg.method_14489(lv)) {
					try {
						InputStream inputStream = lv3.method_14482();
						Throwable var12 = null;

						try {
							JsonArray jsonArray = class_3518.method_15261(
								class_3518.method_15284(gson, IOUtils.toString(inputStream, StandardCharsets.UTF_8), JsonObject.class), "providers"
							);

							for (int i = jsonArray.size() - 1; i >= 0; i--) {
								JsonObject jsonObject = class_3518.method_15295(jsonArray.get(i), "providers[" + i + "]");

								try {
									class_394 lv4 = class_394.method_2048(class_3518.method_15265(jsonObject, "type"));
									if (!this.field_2258 || lv4 == class_394.field_2313 || !lv2.equals(class_310.field_1740)) {
										class_390 lv5 = lv4.method_2047(jsonObject).method_2039(arg);
										if (lv5 != null) {
											list.add(lv5);
										}
									}
								} catch (RuntimeException var28) {
									field_2261.warn("Unable to read definition '{}' in fonts.json in resourcepack: '{}': {}", lv2, lv3.method_14480(), var28.getMessage());
								}
							}
						} catch (Throwable var29) {
							var12 = var29;
							throw var29;
						} finally {
							if (inputStream != null) {
								if (var12 != null) {
									try {
										inputStream.close();
									} catch (Throwable var27) {
										var12.addSuppressed(var27);
									}
								} else {
									inputStream.close();
								}
							}
						}
					} catch (RuntimeException var31) {
						field_2261.warn("Unable to load font '{}' in fonts.json in resourcepack: '{}': {}", lv2, lv3.method_14480(), var31.getMessage());
					}
				}
			} catch (IOException var32) {
				field_2261.warn("Unable to load font '{}' in fonts.json: {}", lv2, var32.getMessage());
			}
		}

		Stream.concat(this.field_2259.keySet().stream(), map.keySet().stream()).distinct().forEach(argx -> {
			List<class_390> listx = (List<class_390>)map.getOrDefault(argx, Collections.emptyList());
			Collections.reverse(listx);
			((class_327)this.field_2259.computeIfAbsent(argx, argxx -> new class_327(this.field_2260, new class_377(this.field_2260, argxx)))).method_1715(listx);
		});
	}

	@Nullable
	public class_327 method_2019(class_2960 arg) {
		return (class_327)this.field_2259.computeIfAbsent(arg, argx -> {
			class_327 lv = new class_327(this.field_2260, new class_377(this.field_2260, argx));
			lv.method_1715(Lists.<class_390>newArrayList(new class_376()));
			return lv;
		});
	}

	public void method_2018(boolean bl) {
		if (bl != this.field_2258) {
			this.field_2258 = bl;
			this.method_14491(class_310.method_1551().method_1478());
		}
	}
}
