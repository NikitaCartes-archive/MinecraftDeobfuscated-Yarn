package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_60 extends class_4309 {
	private static final Logger field_975 = LogManager.getLogger();
	private static final Gson field_974 = new GsonBuilder()
		.registerTypeAdapter(class_61.class, new class_61.class_62())
		.registerTypeAdapter(class_40.class, new class_40.class_41())
		.registerTypeAdapter(class_44.class, new class_44.class_45())
		.registerTypeAdapter(class_42.class, new class_42.class_43())
		.registerTypeAdapter(class_55.class, new class_55.class_57())
		.registerTypeAdapter(class_52.class, new class_52.class_54())
		.registerTypeHierarchyAdapter(class_79.class, new class_75.class_76())
		.registerTypeHierarchyAdapter(class_117.class, new class_131.class_132())
		.registerTypeHierarchyAdapter(class_209.class, new class_217.class_218())
		.registerTypeHierarchyAdapter(class_47.class_50.class, new class_47.class_50.class_51())
		.create();
	private Map<class_2960, class_52> field_970 = ImmutableMap.of();

	public class_60() {
		super(field_974, "loot_tables");
	}

	public class_52 method_367(class_2960 arg) {
		return (class_52)this.field_970.getOrDefault(arg, class_52.field_948);
	}

	protected void method_20712(Map<class_2960, JsonObject> map, class_3300 arg, class_3695 arg2) {
		Builder<class_2960, class_52> builder = ImmutableMap.builder();
		JsonObject jsonObject = (JsonObject)map.remove(class_39.field_844);
		if (jsonObject != null) {
			field_975.warn("Datapack tried to redefine {} loot table, ignoring", class_39.field_844);
		}

		map.forEach((argx, jsonObjectx) -> {
			try {
				class_52 lvx = field_974.fromJson(jsonObjectx, class_52.class);
				builder.put(argx, lvx);
			} catch (Exception var4x) {
				field_975.error("Couldn't parse loot table {}", argx, var4x);
			}
		});
		builder.put(class_39.field_844, class_52.field_948);
		ImmutableMap<class_2960, class_52> immutableMap = builder.build();
		class_58 lv = new class_58();
		immutableMap.forEach((arg2x, arg3) -> method_369(lv, arg2x, arg3, immutableMap::get));
		lv.method_361().forEach((string, string2) -> field_975.warn("Found validation problem in " + string + ": " + string2));
		this.field_970 = immutableMap;
	}

	public static void method_369(class_58 arg, class_2960 arg2, class_52 arg3, Function<class_2960, class_52> function) {
		Set<class_2960> set = ImmutableSet.of(arg2);
		arg3.method_330(arg.method_364("{" + arg2.toString() + "}"), function, set, arg3.method_322());
	}

	public static JsonElement method_372(class_52 arg) {
		return field_974.toJsonTree(arg);
	}

	public Set<class_2960> method_370() {
		return this.field_970.keySet();
	}
}
