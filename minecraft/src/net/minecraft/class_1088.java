package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_1088 {
	public static final class_2960 field_5397 = new class_2960("block/fire_0");
	public static final class_2960 field_5370 = new class_2960("block/fire_1");
	public static final class_2960 field_5381 = new class_2960("block/lava_flow");
	public static final class_2960 field_5391 = new class_2960("block/water_flow");
	public static final class_2960 field_5388 = new class_2960("block/water_overlay");
	public static final class_2960 field_5377 = new class_2960("block/destroy_stage_0");
	public static final class_2960 field_5385 = new class_2960("block/destroy_stage_1");
	public static final class_2960 field_5375 = new class_2960("block/destroy_stage_2");
	public static final class_2960 field_5403 = new class_2960("block/destroy_stage_3");
	public static final class_2960 field_5393 = new class_2960("block/destroy_stage_4");
	public static final class_2960 field_5386 = new class_2960("block/destroy_stage_5");
	public static final class_2960 field_5369 = new class_2960("block/destroy_stage_6");
	public static final class_2960 field_5401 = new class_2960("block/destroy_stage_7");
	public static final class_2960 field_5392 = new class_2960("block/destroy_stage_8");
	public static final class_2960 field_5382 = new class_2960("block/destroy_stage_9");
	private static final Set<class_2960> field_5378 = Sets.<class_2960>newHashSet(
		field_5391,
		field_5381,
		field_5388,
		field_5397,
		field_5370,
		field_5377,
		field_5385,
		field_5375,
		field_5403,
		field_5393,
		field_5386,
		field_5369,
		field_5401,
		field_5392,
		field_5382,
		new class_2960("item/empty_armor_slot_helmet"),
		new class_2960("item/empty_armor_slot_chestplate"),
		new class_2960("item/empty_armor_slot_leggings"),
		new class_2960("item/empty_armor_slot_boots"),
		new class_2960("item/empty_armor_slot_shield")
	);
	private static final Logger field_5380 = LogManager.getLogger();
	public static final class_1091 field_5374 = new class_1091("builtin/missing", "missing");
	@VisibleForTesting
	public static final String field_5371 = ("{    'textures': {       'particle': '"
			+ class_1047.method_4541().method_4598().method_12832()
			+ "',       'missingno': '"
			+ class_1047.method_4541().method_4598().method_12832()
			+ "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}")
		.replace('\'', '"');
	private static final Map<String, String> field_5396 = Maps.<String, String>newHashMap(ImmutableMap.of("missing", field_5371));
	private static final Splitter field_5373 = Splitter.on(',');
	private static final Splitter field_5372 = Splitter.on('=').limit(2);
	public static final class_793 field_5400 = class_156.method_654(class_793.method_3430("{}"), arg -> arg.field_4252 = "generation marker");
	public static final class_793 field_5389 = class_156.method_654(class_793.method_3430("{}"), arg -> arg.field_4252 = "block entity marker");
	private static final class_2689<class_2248, class_2680> field_5395 = new class_2689.class_2690<class_2248, class_2680>(class_2246.field_10124)
		.method_11667(class_2746.method_11825("map"))
		.method_11668(class_2680::new);
	private static final class_801 field_5384 = new class_801();
	private static final Map<class_2960, class_2689<class_2248, class_2680>> field_5383 = ImmutableMap.of(new class_2960("item_frame"), field_5395);
	private final class_3300 field_5379;
	private final class_1059 field_5402;
	private final Set<class_2960> field_5390 = Sets.<class_2960>newHashSet();
	private final class_790.class_791 field_5399 = new class_790.class_791();
	private final Map<class_2960, class_1100> field_5376 = Maps.<class_2960, class_1100>newHashMap();
	private final Map<Triple<class_2960, class_1086, Boolean>, class_1087> field_5398 = Maps.<Triple<class_2960, class_1086, Boolean>, class_1087>newHashMap();
	private final Map<class_2960, class_1100> field_5394 = Maps.<class_2960, class_1100>newHashMap();
	private final Map<class_2960, class_1087> field_5387 = Maps.<class_2960, class_1087>newHashMap();

	public class_1088(class_3300 arg, class_1059 arg2) {
		this.field_5379 = arg;
		this.field_5402 = arg2;

		try {
			this.field_5376.put(field_5374, this.method_4718(field_5374));
			this.method_4727(field_5374);
		} catch (IOException var5) {
			field_5380.error("Error loading missing model, should never happen :(", (Throwable)var5);
			throw new RuntimeException(var5);
		}

		field_5383.forEach((argx, arg2x) -> arg2x.method_11662().forEach(arg2xx -> this.method_4727(class_773.method_3336(argx, arg2xx))));

		for (class_2248 lv : class_2378.field_11146) {
			lv.method_9595().method_11662().forEach(argx -> this.method_4727(class_773.method_3340(argx)));
		}

		for (class_2960 lv2 : class_2378.field_11142.method_10235()) {
			this.method_4727(new class_1091(lv2, "inventory"));
		}

		this.method_4727(new class_1091("minecraft:trident_in_hand#inventory"));
		Set<String> set = Sets.<String>newLinkedHashSet();
		Set<class_2960> set2 = (Set<class_2960>)this.field_5394
			.values()
			.stream()
			.flatMap(argx -> argx.method_4754(this::method_4726, set).stream())
			.collect(Collectors.toSet());
		set2.addAll(field_5378);
		set.forEach(string -> field_5380.warn("Unable to resolve texture reference: {}", string));
		this.field_5402.method_4602(this.field_5379, set2);
		this.field_5394.keySet().forEach(argx -> {
			class_1087 lv = null;

			try {
				lv = this.method_15878(argx, class_1086.field_5350);
			} catch (Exception var4x) {
				field_5380.warn("Unable to bake model: '{}': {}", argx, var4x);
			}

			if (lv != null) {
				this.field_5387.put(argx, lv);
			}
		});
	}

	private static Predicate<class_2680> method_4725(class_2689<class_2248, class_2680> arg, String string) {
		Map<class_2769<?>, Comparable<?>> map = Maps.<class_2769<?>, Comparable<?>>newHashMap();

		for (String string2 : field_5373.split(string)) {
			Iterator<String> iterator = field_5372.split(string2).iterator();
			if (iterator.hasNext()) {
				String string3 = (String)iterator.next();
				class_2769<?> lv = arg.method_11663(string3);
				if (lv != null && iterator.hasNext()) {
					String string4 = (String)iterator.next();
					Comparable<?> comparable = method_4724((class_2769<Comparable<?>>)lv, string4);
					if (comparable == null) {
						throw new RuntimeException("Unknown value: '" + string4 + "' for blockstate property: '" + string3 + "' " + lv.method_11898());
					}

					map.put(lv, comparable);
				} else if (!string3.isEmpty()) {
					throw new RuntimeException("Unknown blockstate property: '" + string3 + "'");
				}
			}
		}

		class_2248 lv2 = arg.method_11660();
		return arg2 -> {
			if (arg2 != null && lv2 == arg2.method_11614()) {
				for (Entry<class_2769<?>, Comparable<?>> entry : map.entrySet()) {
					if (!Objects.equals(arg2.method_11654((class_2769)entry.getKey()), entry.getValue())) {
						return false;
					}
				}

				return true;
			} else {
				return false;
			}
		};
	}

	@Nullable
	static <T extends Comparable<T>> T method_4724(class_2769<T> arg, String string) {
		return (T)arg.method_11900(string).orElse(null);
	}

	public class_1100 method_4726(class_2960 arg) {
		if (this.field_5376.containsKey(arg)) {
			return (class_1100)this.field_5376.get(arg);
		} else if (this.field_5390.contains(arg)) {
			throw new IllegalStateException("Circular reference while loading " + arg);
		} else {
			this.field_5390.add(arg);
			class_1100 lv = (class_1100)this.field_5376.get(field_5374);

			while (!this.field_5390.isEmpty()) {
				class_2960 lv2 = (class_2960)this.field_5390.iterator().next();

				try {
					if (!this.field_5376.containsKey(lv2)) {
						this.method_4715(lv2);
					}
				} catch (class_1088.class_1089 var9) {
					field_5380.warn(var9.getMessage());
					this.field_5376.put(lv2, lv);
				} catch (Exception var10) {
					field_5380.warn("Unable to load model: '{}' referenced from: {}: {}", lv2, arg, var10);
					this.field_5376.put(lv2, lv);
				} finally {
					this.field_5390.remove(lv2);
				}
			}

			return (class_1100)this.field_5376.getOrDefault(arg, lv);
		}
	}

	private void method_4715(class_2960 arg) throws Exception {
		if (!(arg instanceof class_1091)) {
			this.method_4729(arg, this.method_4718(arg));
		} else {
			class_1091 lv = (class_1091)arg;
			if (Objects.equals(lv.method_4740(), "inventory")) {
				class_2960 lv2 = new class_2960(arg.method_12836(), "item/" + arg.method_12832());
				class_793 lv3 = this.method_4718(lv2);
				this.method_4729(lv, lv3);
				this.field_5376.put(lv2, lv3);
			} else {
				class_2960 lv2 = new class_2960(arg.method_12836(), arg.method_12832());
				class_2689<class_2248, class_2680> lv4 = (class_2689<class_2248, class_2680>)Optional.ofNullable(field_5383.get(lv2))
					.orElseGet(() -> class_2378.field_11146.method_10223(lv2).method_9595());
				this.field_5399.method_3426(lv4);
				ImmutableList<class_2680> immutableList = lv4.method_11662();
				Map<class_1091, class_2680> map = Maps.<class_1091, class_2680>newHashMap();
				immutableList.forEach(arg2 -> {
					class_2680 var10000 = (class_2680)map.put(class_773.method_3336(lv2, arg2), arg2);
				});
				Map<class_2680, class_1100> map2 = Maps.<class_2680, class_1100>newHashMap();
				class_2960 lv5 = new class_2960(arg.method_12836(), "blockstates/" + arg.method_12832() + ".json");

				try {
					List<Pair<String, class_790>> list;
					try {
						list = (List<Pair<String, class_790>>)this.field_5379
							.method_14489(lv5)
							.stream()
							.map(
								argx -> {
									try {
										InputStream inputStream = argx.method_14482();
										Throwable var3x = null;

										Pair var4x;
										try {
											var4x = Pair.of(argx.method_14480(), class_790.method_3424(this.field_5399, new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
										} catch (Throwable var14x) {
											var3x = var14x;
											throw var14x;
										} finally {
											if (inputStream != null) {
												if (var3x != null) {
													try {
														inputStream.close();
													} catch (Throwable var13x) {
														var3x.addSuppressed(var13x);
													}
												} else {
													inputStream.close();
												}
											}
										}

										return var4x;
									} catch (Exception var16x) {
										throw new class_1088.class_1089(
											String.format(
												"Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", argx.method_14483(), argx.method_14480(), var16x.getMessage()
											)
										);
									}
								}
							)
							.collect(Collectors.toList());
					} catch (IOException var23) {
						field_5380.warn("Exception loading blockstate definition: {}: {}", lv5, var23);
						return;
					}

					for (Pair<String, class_790> pair : list) {
						class_790 lv7 = pair.getSecond();
						Map<class_2680, class_1100> map3 = Maps.<class_2680, class_1100>newIdentityHashMap();
						class_1100 lv8;
						if (lv7.method_3422()) {
							lv8 = lv7.method_3421();
							immutableList.forEach(arg2 -> {
								class_1100 var10000 = (class_1100)map3.put(arg2, lv8);
							});
						} else {
							lv8 = null;
						}

						lv7.method_3423()
							.forEach(
								(string, arg5) -> {
									try {
										immutableList.stream()
											.filter(method_4725(lv4, string))
											.forEach(
												arg4x -> {
													class_1100 lvx = (class_1100)map3.put(arg4x, arg5);
													if (lvx != null && lvx != lv8) {
														map3.put(arg4x, this.field_5376.get(field_5374));
														throw new RuntimeException(
															"Overlapping definition with: "
																+ (String)((Entry)lv7.method_3423().entrySet().stream().filter(entry -> entry.getValue() == lvx).findFirst().get()).getKey()
														);
													}
												}
											);
									} catch (Exception var11x) {
										field_5380.warn(
											"Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}", lv5, pair.getFirst(), string, var11x.getMessage()
										);
									}
								}
							);
						map2.putAll(map3);
					}
				} catch (class_1088.class_1089 var24) {
					throw var24;
				} catch (Exception var25) {
					throw new class_1088.class_1089(String.format("Exception loading blockstate definition: '%s': %s", lv5, var25));
				} finally {
					Iterator var16 = map.entrySet().iterator();

					while (true) {
						if (!var16.hasNext()) {
							;
						} else {
							Entry<class_1091, class_2680> entry3 = (Entry<class_1091, class_2680>)var16.next();
							class_1100 lv11 = (class_1100)map2.get(entry3.getValue());
							if (lv11 == null) {
								field_5380.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", lv5, entry3.getKey());
								lv11 = (class_1100)this.field_5376.get(field_5374);
							}

							this.method_4729((class_2960)entry3.getKey(), lv11);
						}
					}
				}
			}
		}
	}

	private void method_4729(class_2960 arg, class_1100 arg2) {
		this.field_5376.put(arg, arg2);
		this.field_5390.addAll(arg2.method_4755());
	}

	private void method_4727(class_1091 arg) {
		class_1100 lv = this.method_4726(arg);
		this.field_5376.put(arg, lv);
		this.field_5394.put(arg, lv);
	}

	@Nullable
	public class_1087 method_15878(class_2960 arg, class_3665 arg2) {
		Triple<class_2960, class_1086, Boolean> triple = Triple.of(arg, arg2.method_3509(), arg2.method_3512());
		if (this.field_5398.containsKey(triple)) {
			return (class_1087)this.field_5398.get(triple);
		} else {
			class_1100 lv = this.method_4726(arg);
			if (lv instanceof class_793) {
				class_793 lv2 = (class_793)lv;
				if (lv2.method_3431() == field_5400) {
					return field_5384.method_3479(this.field_5402::method_4608, lv2).method_3446(this, lv2, this.field_5402::method_4608, arg2);
				}
			}

			class_1087 lv3 = lv.method_4753(this, this.field_5402::method_4608, arg2);
			this.field_5398.put(triple, lv3);
			return lv3;
		}
	}

	private class_793 method_4718(class_2960 arg) throws IOException {
		Reader reader = null;
		class_3298 lv = null;

		class_793 lv2;
		try {
			String string = arg.method_12832();
			if ("builtin/generated".equals(string)) {
				return field_5400;
			}

			if (!"builtin/entity".equals(string)) {
				if (string.startsWith("builtin/")) {
					String string2 = string.substring("builtin/".length());
					String string3 = (String)field_5396.get(string2);
					if (string3 == null) {
						throw new FileNotFoundException(arg.toString());
					}

					reader = new StringReader(string3);
				} else {
					lv = this.field_5379.method_14486(new class_2960(arg.method_12836(), "models/" + arg.method_12832() + ".json"));
					reader = new InputStreamReader(lv.method_14482(), StandardCharsets.UTF_8);
				}

				lv2 = class_793.method_3437(reader);
				lv2.field_4252 = arg.toString();
				return lv2;
			}

			lv2 = field_5389;
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(lv);
		}

		return lv2;
	}

	public Map<class_2960, class_1087> method_4734() {
		return this.field_5387;
	}

	@Environment(EnvType.CLIENT)
	static class class_1089 extends RuntimeException {
		public class_1089(String string) {
			super(string);
		}
	}
}
