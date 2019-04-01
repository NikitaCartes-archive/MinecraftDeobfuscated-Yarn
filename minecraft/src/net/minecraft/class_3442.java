package net.minecraft;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3442 extends class_3469 {
	private static final Logger field_15309 = LogManager.getLogger();
	private final MinecraftServer field_15308;
	private final File field_15305;
	private final Set<class_3445<?>> field_15307 = Sets.<class_3445<?>>newHashSet();
	private int field_15306 = -300;

	public class_3442(MinecraftServer minecraftServer, File file) {
		this.field_15308 = minecraftServer;
		this.field_15305 = file;
		if (file.isFile()) {
			try {
				this.method_14906(minecraftServer.method_3855(), FileUtils.readFileToString(file));
			} catch (IOException var4) {
				field_15309.error("Couldn't read statistics file {}", file, var4);
			} catch (JsonParseException var5) {
				field_15309.error("Couldn't parse statistics file {}", file, var5);
			}
		}
	}

	public void method_14912() {
		try {
			FileUtils.writeStringToFile(this.field_15305, this.method_14911());
		} catch (IOException var2) {
			field_15309.error("Couldn't save stats", (Throwable)var2);
		}
	}

	@Override
	public void method_15023(class_1657 arg, class_3445<?> arg2, int i) {
		super.method_15023(arg, arg2, i);
		this.field_15307.add(arg2);
	}

	private Set<class_3445<?>> method_14909() {
		Set<class_3445<?>> set = Sets.<class_3445<?>>newHashSet(this.field_15307);
		this.field_15307.clear();
		return set;
	}

	public void method_14906(DataFixer dataFixer, String string) {
		try {
			JsonReader jsonReader = new JsonReader(new StringReader(string));
			Throwable var4 = null;

			try {
				jsonReader.setLenient(false);
				JsonElement jsonElement = Streams.parse(jsonReader);
				if (!jsonElement.isJsonNull()) {
					class_2487 lv = method_14908(jsonElement.getAsJsonObject());
					if (!lv.method_10573("DataVersion", 99)) {
						lv.method_10569("DataVersion", 1343);
					}

					lv = class_2512.method_10688(dataFixer, DataFixTypes.STATS, lv, lv.method_10550("DataVersion"));
					if (lv.method_10573("stats", 10)) {
						class_2487 lv2 = lv.method_10562("stats");

						for (String string2 : lv2.method_10541()) {
							if (lv2.method_10573(string2, 10)) {
								class_156.method_17974(
									class_2378.field_11152.method_17966(new class_2960(string2)),
									arg2 -> {
										class_2487 lvx = lv2.method_10562(string2);

										for (String string2x : lvx.method_10541()) {
											if (lvx.method_10573(string2x, 99)) {
												class_156.method_17974(
													this.method_14905(arg2, string2x),
													arg2x -> this.field_15431.put(arg2x, lvx.method_10550(string2x)),
													() -> field_15309.warn("Invalid statistic in {}: Don't know what {} is", this.field_15305, string2x)
												);
											} else {
												field_15309.warn("Invalid statistic value in {}: Don't know what {} is for key {}", this.field_15305, lvx.method_10580(string2x), string2x);
											}
										}
									},
									() -> field_15309.warn("Invalid statistic type in {}: Don't know what {} is", this.field_15305, string2)
								);
							}
						}
					}

					return;
				}

				field_15309.error("Unable to parse Stat data from {}", this.field_15305);
			} catch (Throwable var19) {
				var4 = var19;
				throw var19;
			} finally {
				if (jsonReader != null) {
					if (var4 != null) {
						try {
							jsonReader.close();
						} catch (Throwable var18) {
							var4.addSuppressed(var18);
						}
					} else {
						jsonReader.close();
					}
				}
			}
		} catch (IOException | JsonParseException var21) {
			field_15309.error("Unable to parse Stat data from {}", this.field_15305, var21);
		}
	}

	private <T> Optional<class_3445<T>> method_14905(class_3448<T> arg, String string) {
		return Optional.ofNullable(class_2960.method_12829(string)).flatMap(arg.method_14959()::method_17966).map(arg::method_14956);
	}

	private static class_2487 method_14908(JsonObject jsonObject) {
		class_2487 lv = new class_2487();

		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			JsonElement jsonElement = (JsonElement)entry.getValue();
			if (jsonElement.isJsonObject()) {
				lv.method_10566((String)entry.getKey(), method_14908(jsonElement.getAsJsonObject()));
			} else if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					lv.method_10569((String)entry.getKey(), jsonPrimitive.getAsInt());
				}
			}
		}

		return lv;
	}

	protected String method_14911() {
		Map<class_3448<?>, JsonObject> map = Maps.<class_3448<?>, JsonObject>newHashMap();

		for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<class_3445<?>> entry : this.field_15431.object2IntEntrySet()) {
			class_3445<?> lv = (class_3445<?>)entry.getKey();
			((JsonObject)map.computeIfAbsent(lv.method_14949(), arg -> new JsonObject())).addProperty(method_14907(lv).toString(), entry.getIntValue());
		}

		JsonObject jsonObject = new JsonObject();

		for (Entry<class_3448<?>, JsonObject> entry2 : map.entrySet()) {
			jsonObject.add(class_2378.field_11152.method_10221((class_3448<?>)entry2.getKey()).toString(), (JsonElement)entry2.getValue());
		}

		JsonObject jsonObject2 = new JsonObject();
		jsonObject2.add("stats", jsonObject);
		jsonObject2.addProperty("DataVersion", class_155.method_16673().getWorldVersion());
		return jsonObject2.toString();
	}

	private static <T> class_2960 method_14907(class_3445<T> arg) {
		return arg.method_14949().method_14959().method_10221(arg.method_14951());
	}

	public void method_14914() {
		this.field_15307.addAll(this.field_15431.keySet());
	}

	public void method_14910(class_3222 arg) {
		int i = this.field_15308.method_3780();
		Object2IntMap<class_3445<?>> object2IntMap = new Object2IntOpenHashMap<>();
		if (i - this.field_15306 > 300) {
			this.field_15306 = i;

			for (class_3445<?> lv : this.method_14909()) {
				object2IntMap.put(lv, this.method_15025(lv));
			}
		}

		arg.field_13987.method_14364(new class_2617(object2IntMap));
	}
}
