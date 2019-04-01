package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1863 implements class_4013 {
	private static final Logger field_9027 = LogManager.getLogger();
	public static final int field_9026 = "recipes/".length();
	public static final int field_9025 = ".json".length();
	private final Map<class_3956<?>, Map<class_2960, class_1860<?>>> field_9023 = class_156.method_654(
		Maps.<class_3956<?>, Map<class_2960, class_1860<?>>>newHashMap(), class_1863::method_17719
	);
	private boolean field_9024;

	@Override
	public void method_14491(class_3300 arg) {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		this.field_9024 = false;
		method_17719(this.field_9023);

		for (class_2960 lv : arg.method_14488("recipes", stringx -> stringx.endsWith(".json"))) {
			String string = lv.method_12832();
			class_2960 lv2 = new class_2960(lv.method_12836(), string.substring(field_9026, string.length() - field_9025));

			try {
				class_3298 lv3 = arg.method_14486(lv);
				Throwable var8 = null;

				try {
					JsonObject jsonObject = class_3518.method_15284(gson, IOUtils.toString(lv3.method_14482(), StandardCharsets.UTF_8), JsonObject.class);
					if (jsonObject == null) {
						field_9027.error("Couldn't load recipe {} as it's null or empty", lv2);
					} else {
						this.method_8125(method_17720(lv2, jsonObject));
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
			} catch (IllegalArgumentException | JsonParseException var21) {
				field_9027.error("Parsing error loading recipe {}", lv2, var21);
				this.field_9024 = true;
			} catch (IOException var22) {
				field_9027.error("Couldn't read custom advancement {} from {}", lv2, lv, var22);
				this.field_9024 = true;
			}
		}

		field_9027.info("Loaded {} recipes", this.field_9023.size());
	}

	public void method_8125(class_1860<?> arg) {
		Map<class_2960, class_1860<?>> map = (Map<class_2960, class_1860<?>>)this.field_9023.get(arg.method_17716());
		if (map.containsKey(arg.method_8114())) {
			throw new IllegalStateException("Duplicate recipe ignored with ID " + arg.method_8114());
		} else {
			map.put(arg.method_8114(), arg);
		}
	}

	public <C extends class_1263, T extends class_1860<C>> Optional<T> method_8132(class_3956<T> arg, C arg2, class_1937 arg3) {
		return this.method_17717(arg).values().stream().flatMap(arg4 -> class_156.method_17815(arg.method_17725(arg4, arg3, arg2))).findFirst();
	}

	public <C extends class_1263, T extends class_1860<C>> List<T> method_17877(class_3956<T> arg, C arg2, class_1937 arg3) {
		return (List<T>)this.method_17717(arg)
			.values()
			.stream()
			.flatMap(arg4 -> class_156.method_17815(arg.method_17725(arg4, arg3, arg2)))
			.sorted(Comparator.comparing(argx -> argx.method_8110().method_7922()))
			.collect(Collectors.toList());
	}

	private <C extends class_1263, T extends class_1860<C>> Map<class_2960, class_1860<C>> method_17717(class_3956<T> arg) {
		return (Map<class_2960, class_1860<C>>)this.field_9023.getOrDefault(arg, Maps.newHashMap());
	}

	public <C extends class_1263, T extends class_1860<C>> class_2371<class_1799> method_8128(class_3956<T> arg, C arg2, class_1937 arg3) {
		Optional<T> optional = this.method_8132(arg, arg2, arg3);
		if (optional.isPresent()) {
			return ((class_1860)optional.get()).method_8111(arg2);
		} else {
			class_2371<class_1799> lv = class_2371.method_10213(arg2.method_5439(), class_1799.field_8037);

			for (int i = 0; i < lv.size(); i++) {
				lv.set(i, arg2.method_5438(i));
			}

			return lv;
		}
	}

	public Optional<? extends class_1860<?>> method_8130(class_2960 arg) {
		return this.field_9023.values().stream().map(map -> (class_1860)map.get(arg)).filter(Objects::nonNull).findFirst();
	}

	public Collection<class_1860<?>> method_8126() {
		return (Collection<class_1860<?>>)this.field_9023.values().stream().flatMap(map -> map.values().stream()).collect(Collectors.toSet());
	}

	public Stream<class_2960> method_8127() {
		return this.field_9023.values().stream().flatMap(map -> map.keySet().stream());
	}

	@Environment(EnvType.CLIENT)
	public void method_8133() {
		method_17719(this.field_9023);
	}

	public static class_1860<?> method_17720(class_2960 arg, JsonObject jsonObject) {
		String string = class_3518.method_15265(jsonObject, "type");
		return ((class_1865)class_2378.field_17598
				.method_17966(new class_2960(string))
				.orElseThrow(() -> new JsonSyntaxException("Invalid or unsupported recipe type '" + string + "'")))
			.method_8121(arg, jsonObject);
	}

	private static void method_17719(Map<class_3956<?>, Map<class_2960, class_1860<?>>> map) {
		map.clear();

		for (class_3956<?> lv : class_2378.field_17597) {
			map.put(lv, Maps.newHashMap());
		}
	}
}
