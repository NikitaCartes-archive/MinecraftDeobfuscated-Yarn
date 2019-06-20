package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1863 extends class_4309 {
	private static final Gson field_19359 = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final Logger field_9027 = LogManager.getLogger();
	private Map<class_3956<?>, Map<class_2960, class_1860<?>>> field_9023 = ImmutableMap.of();
	private boolean field_9024;

	public class_1863() {
		super(field_19359, "recipes");
	}

	protected void method_20705(Map<class_2960, JsonObject> map, class_3300 arg, class_3695 arg2) {
		this.field_9024 = false;
		Map<class_3956<?>, Builder<class_2960, class_1860<?>>> map2 = Maps.<class_3956<?>, Builder<class_2960, class_1860<?>>>newHashMap();

		for (Entry<class_2960, JsonObject> entry : map.entrySet()) {
			class_2960 lv = (class_2960)entry.getKey();

			try {
				class_1860<?> lv2 = method_17720(lv, (JsonObject)entry.getValue());
				((Builder)map2.computeIfAbsent(lv2.method_17716(), argx -> ImmutableMap.builder())).put(lv, lv2);
			} catch (IllegalArgumentException | JsonParseException var9) {
				field_9027.error("Parsing error loading recipe {}", lv, var9);
			}
		}

		this.field_9023 = (Map<class_3956<?>, Map<class_2960, class_1860<?>>>)map2.entrySet()
			.stream()
			.collect(ImmutableMap.toImmutableMap(Entry::getKey, entryx -> ((Builder)entryx.getValue()).build()));
		field_9027.info("Loaded {} recipes", map2.size());
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
		return (Map<class_2960, class_1860<C>>)this.field_9023.getOrDefault(arg, Collections.emptyMap());
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

	public static class_1860<?> method_17720(class_2960 arg, JsonObject jsonObject) {
		String string = class_3518.method_15265(jsonObject, "type");
		return ((class_1865)class_2378.field_17598
				.method_17966(new class_2960(string))
				.orElseThrow(() -> new JsonSyntaxException("Invalid or unsupported recipe type '" + string + "'")))
			.method_8121(arg, jsonObject);
	}

	@Environment(EnvType.CLIENT)
	public void method_20702(Iterable<class_1860<?>> iterable) {
		this.field_9024 = false;
		Map<class_3956<?>, Map<class_2960, class_1860<?>>> map = Maps.<class_3956<?>, Map<class_2960, class_1860<?>>>newHashMap();
		iterable.forEach(arg -> {
			Map<class_2960, class_1860<?>> map2 = (Map<class_2960, class_1860<?>>)map.computeIfAbsent(arg.method_17716(), argx -> Maps.newHashMap());
			class_1860<?> lv = (class_1860<?>)map2.put(arg.method_8114(), arg);
			if (lv != null) {
				throw new IllegalStateException("Duplicate recipe ignored with ID " + arg.method_8114());
			}
		});
		this.field_9023 = ImmutableMap.copyOf(map);
	}
}
