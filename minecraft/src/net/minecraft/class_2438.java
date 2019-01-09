package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2438 implements class_2405 {
	private static final Logger field_11355 = LogManager.getLogger();
	private static final Gson field_11356 = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private final class_2403 field_11353;
	private final List<Pair<Supplier<Consumer<BiConsumer<class_2960, class_52.class_53>>>, class_176>> field_11354 = ImmutableList.of(
		Pair.of(class_2437::new, class_173.field_1176),
		Pair.of(class_2432::new, class_173.field_1179),
		Pair.of(class_2434::new, class_173.field_1173),
		Pair.of(class_2430::new, class_173.field_1172),
		Pair.of(class_3714::new, class_173.field_16235)
	);

	public class_2438(class_2403 arg) {
		this.field_11353 = arg;
	}

	@Override
	public void method_10319(class_2408 arg) {
		Path path = this.field_11353.method_10313();
		Map<class_2960, class_52> map = Maps.<class_2960, class_52>newHashMap();
		this.field_11354.forEach(pair -> ((Consumer)((Supplier)pair.getFirst()).get()).accept((BiConsumer)(argx, arg2) -> {
				if (map.put(argx, arg2.method_334((class_176)pair.getSecond()).method_338()) != null) {
					throw new IllegalStateException("Duplicate loot table " + argx);
				}
			}));
		class_58 lv = new class_58();

		for (class_2960 lv2 : Sets.difference(class_39.method_270(), map.keySet())) {
			lv.method_360("Missing built-in table: " + lv2);
		}

		map.forEach((arg2, arg3) -> class_60.method_369(lv, arg2, arg3, map::get));
		Multimap<String, String> multimap = lv.method_361();
		if (!multimap.isEmpty()) {
			multimap.forEach((string, string2) -> field_11355.warn("Found validation problem in " + string + ": " + string2));
			throw new IllegalStateException("Failed to validate loot tables, see logs");
		} else {
			map.forEach((arg2, arg3) -> {
				Path path2 = method_10409(path, arg2);

				try {
					class_2405.method_10320(field_11356, arg, class_60.method_372(arg3), path2);
				} catch (IOException var6) {
					field_11355.error("Couldn't save loot table {}", path2, var6);
				}
			});
		}
	}

	private static Path method_10409(Path path, class_2960 arg) {
		return path.resolve("data/" + arg.method_12836() + "/loot_tables/" + arg.method_12832() + ".json");
	}

	@Override
	public String method_10321() {
		return "LootTables";
	}
}
