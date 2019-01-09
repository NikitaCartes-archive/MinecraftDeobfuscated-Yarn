package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_60 implements class_3302 {
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
	private final Map<class_2960, class_52> field_970 = Maps.<class_2960, class_52>newHashMap();
	private final Set<class_2960> field_971 = Collections.unmodifiableSet(this.field_970.keySet());
	public static final int field_973 = "loot_tables/".length();
	public static final int field_972 = ".json".length();

	public class_52 method_367(class_2960 arg) {
		return (class_52)this.field_970.getOrDefault(arg, class_52.field_948);
	}

	@Override
	public void method_14491(class_3300 arg) {
		this.field_970.clear();

		for (class_2960 lv : arg.method_14488("loot_tables", stringx -> stringx.endsWith(".json"))) {
			String string = lv.method_12832();
			class_2960 lv2 = new class_2960(lv.method_12836(), string.substring(field_973, string.length() - field_972));

			try {
				class_3298 lv3 = arg.method_14486(lv);
				Throwable var7 = null;

				try {
					class_52 lv4 = class_3518.method_15284(field_974, IOUtils.toString(lv3.method_14482(), StandardCharsets.UTF_8), class_52.class);
					if (lv4 != null) {
						this.field_970.put(lv2, lv4);
					}
				} catch (Throwable var17) {
					var7 = var17;
					throw var17;
				} finally {
					if (lv3 != null) {
						if (var7 != null) {
							try {
								lv3.close();
							} catch (Throwable var16) {
								var7.addSuppressed(var16);
							}
						} else {
							lv3.close();
						}
					}
				}
			} catch (Throwable var19) {
				field_975.error("Couldn't read loot table {} from {}", lv2, lv, var19);
			}
		}

		this.field_970.put(class_39.field_844, class_52.field_948);
		class_58 lv5 = new class_58();
		this.field_970.forEach((arg2, arg3) -> method_369(lv5, arg2, arg3, this.field_970::get));
		lv5.method_361().forEach((stringx, string2) -> field_975.warn("Found validation problem in " + stringx + ": " + string2));
	}

	public static void method_369(class_58 arg, class_2960 arg2, class_52 arg3, Function<class_2960, class_52> function) {
		Set<class_2960> set = ImmutableSet.of(arg2);
		arg3.method_330(arg.method_364("{" + arg2.toString() + "}"), function, set, arg3.method_322());
	}

	public static JsonElement method_372(class_52 arg) {
		return field_974.toJsonTree(arg);
	}

	public Set<class_2960> method_370() {
		return this.field_971;
	}
}
