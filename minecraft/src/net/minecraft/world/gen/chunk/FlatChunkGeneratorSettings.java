package net.minecraft.world.gen.chunk;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3229;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.sortme.structures.VillageGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.config.decorator.DecoratorConfig;
import net.minecraft.world.gen.config.decorator.LakeDecoratorConfig;
import net.minecraft.world.gen.config.feature.FeatureConfig;
import net.minecraft.world.gen.config.feature.LakeFeatureConfig;
import net.minecraft.world.gen.config.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.config.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.config.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.config.feature.VillageFeatureConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatChunkGeneratorSettings extends ChunkGeneratorSettings {
	private static final Logger field_14064 = LogManager.getLogger();
	private static final ConfiguredFeature<?> field_14075 = Biome.configureFeature(
		Feature.MINESHAFT, new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL), Decorator.NOPE, DecoratorConfig.DEFAULT
	);
	private static final ConfiguredFeature<?> field_14079 = Biome.configureFeature(
		Feature.VILLAGE, new VillageFeatureConfig(0, VillageGenerator.Type.OAK), Decorator.NOPE, DecoratorConfig.DEFAULT
	);
	private static final ConfiguredFeature<?> field_14063 = Biome.configureFeature(
		Feature.STRONGHOLD, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT
	);
	private static final ConfiguredFeature<?> field_14067 = Biome.configureFeature(
		Feature.SWAMP_HUT, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT
	);
	private static final ConfiguredFeature<?> field_14061 = Biome.configureFeature(
		Feature.DESERT_PYRAMID, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT
	);
	private static final ConfiguredFeature<?> field_14078 = Biome.configureFeature(
		Feature.JUNGLE_TEMPLE, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT
	);
	private static final ConfiguredFeature<?> field_14070 = Biome.configureFeature(Feature.IGLOO, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT);
	private static final ConfiguredFeature<?> field_14062 = Biome.configureFeature(
		Feature.SHIPWRECK, new ShipwreckFeatureConfig(false), Decorator.NOPE, DecoratorConfig.DEFAULT
	);
	private static final ConfiguredFeature<?> field_14076 = Biome.configureFeature(
		Feature.OCEAN_MONUMENT, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT
	);
	private static final ConfiguredFeature<?> field_14071 = Biome.configureFeature(
		Feature.field_13573, new LakeFeatureConfig(Blocks.field_10382.getDefaultState()), Decorator.field_14242, new LakeDecoratorConfig(4)
	);
	private static final ConfiguredFeature<?> field_14066 = Biome.configureFeature(
		Feature.field_13573, new LakeFeatureConfig(Blocks.field_10164.getDefaultState()), Decorator.field_14237, new LakeDecoratorConfig(80)
	);
	private static final ConfiguredFeature<?> field_14084 = Biome.configureFeature(
		Feature.END_CITY, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT
	);
	private static final ConfiguredFeature<?> field_14068 = Biome.configureFeature(
		Feature.WOODLAND_MANSION, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT
	);
	private static final ConfiguredFeature<?> field_14065 = Biome.configureFeature(
		Feature.NETHER_BRIDGE, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT
	);
	private static final ConfiguredFeature<?> field_14085 = Biome.configureFeature(
		Feature.OCEAN_RUIN, new OceanRuinFeatureConfig(OceanRuinFeature.BiomeTemperature.COLD, 0.3F, 0.1F), Decorator.NOPE, DecoratorConfig.DEFAULT
	);
	public static final Map<ConfiguredFeature<?>, GenerationStep.Feature> field_14069 = SystemUtil.consume(
		Maps.<ConfiguredFeature<?>, GenerationStep.Feature>newHashMap(), hashMap -> {
			hashMap.put(field_14075, GenerationStep.Feature.field_13172);
			hashMap.put(field_14079, GenerationStep.Feature.field_13173);
			hashMap.put(field_14063, GenerationStep.Feature.field_13172);
			hashMap.put(field_14067, GenerationStep.Feature.field_13173);
			hashMap.put(field_14061, GenerationStep.Feature.field_13173);
			hashMap.put(field_14078, GenerationStep.Feature.field_13173);
			hashMap.put(field_14070, GenerationStep.Feature.field_13173);
			hashMap.put(field_14062, GenerationStep.Feature.field_13173);
			hashMap.put(field_14085, GenerationStep.Feature.field_13173);
			hashMap.put(field_14071, GenerationStep.Feature.field_13171);
			hashMap.put(field_14066, GenerationStep.Feature.field_13171);
			hashMap.put(field_14084, GenerationStep.Feature.field_13173);
			hashMap.put(field_14068, GenerationStep.Feature.field_13173);
			hashMap.put(field_14065, GenerationStep.Feature.field_13172);
			hashMap.put(field_14076, GenerationStep.Feature.field_13173);
		}
	);
	public static final Map<String, ConfiguredFeature<?>[]> field_14073 = SystemUtil.consume(Maps.<String, ConfiguredFeature<?>[]>newHashMap(), hashMap -> {
		hashMap.put("mineshaft", new ConfiguredFeature[]{field_14075});
		hashMap.put("village", new ConfiguredFeature[]{field_14079});
		hashMap.put("stronghold", new ConfiguredFeature[]{field_14063});
		hashMap.put("biome_1", new ConfiguredFeature[]{field_14067, field_14061, field_14078, field_14070, field_14085, field_14062});
		hashMap.put("oceanmonument", new ConfiguredFeature[]{field_14076});
		hashMap.put("lake", new ConfiguredFeature[]{field_14071});
		hashMap.put("lava_lake", new ConfiguredFeature[]{field_14066});
		hashMap.put("endcity", new ConfiguredFeature[]{field_14084});
		hashMap.put("mansion", new ConfiguredFeature[]{field_14068});
		hashMap.put("fortress", new ConfiguredFeature[]{field_14065});
	});
	public static final Map<ConfiguredFeature<?>, FeatureConfig> field_14080 = SystemUtil.consume(
		Maps.<ConfiguredFeature<?>, FeatureConfig>newHashMap(), hashMap -> {
			hashMap.put(field_14075, new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL));
			hashMap.put(field_14079, new VillageFeatureConfig(0, VillageGenerator.Type.OAK));
			hashMap.put(field_14063, FeatureConfig.DEFAULT);
			hashMap.put(field_14067, FeatureConfig.DEFAULT);
			hashMap.put(field_14061, FeatureConfig.DEFAULT);
			hashMap.put(field_14078, FeatureConfig.DEFAULT);
			hashMap.put(field_14070, FeatureConfig.DEFAULT);
			hashMap.put(field_14085, new OceanRuinFeatureConfig(OceanRuinFeature.BiomeTemperature.COLD, 0.3F, 0.9F));
			hashMap.put(field_14062, new ShipwreckFeatureConfig(false));
			hashMap.put(field_14076, FeatureConfig.DEFAULT);
			hashMap.put(field_14084, FeatureConfig.DEFAULT);
			hashMap.put(field_14068, FeatureConfig.DEFAULT);
			hashMap.put(field_14065, FeatureConfig.DEFAULT);
		}
	);
	private final List<class_3229> field_14072 = Lists.<class_3229>newArrayList();
	private final Map<String, Map<String, String>> field_14074 = Maps.<String, Map<String, String>>newHashMap();
	private Biome field_14081;
	private final BlockState[] field_14082 = new BlockState[256];
	private boolean field_14077;
	private int field_14083;

	@Nullable
	public static Block method_14334(String string) {
		try {
			Identifier identifier = new Identifier(string);
			if (Registry.BLOCK.contains(identifier)) {
				return Registry.BLOCK.get(identifier);
			}
		} catch (IllegalArgumentException var2) {
			field_14064.warn("Invalid blockstate: {}", string, var2);
		}

		return null;
	}

	public Biome method_14326() {
		return this.field_14081;
	}

	public void method_14325(Biome biome) {
		this.field_14081 = biome;
	}

	public Map<String, Map<String, String>> method_14333() {
		return this.field_14074;
	}

	public List<class_3229> method_14327() {
		return this.field_14072;
	}

	public void method_14330() {
		int i = 0;

		for (class_3229 lv : this.field_14072) {
			lv.method_14287(i);
			i += lv.method_14289();
		}

		this.field_14083 = 0;
		this.field_14077 = true;
		i = 0;

		for (class_3229 lv : this.field_14072) {
			for (int j = lv.method_14288(); j < lv.method_14288() + lv.method_14289(); j++) {
				BlockState blockState = lv.method_14286();
				if (blockState.getBlock() != Blocks.field_10124) {
					this.field_14077 = false;
					this.field_14082[j] = blockState;
				}
			}

			if (lv.method_14286().getBlock() == Blocks.field_10124) {
				i += lv.method_14289();
			} else {
				this.field_14083 = this.field_14083 + lv.method_14289() + i;
				i = 0;
			}
		}
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < this.field_14072.size(); i++) {
			if (i > 0) {
				stringBuilder.append(",");
			}

			stringBuilder.append(this.field_14072.get(i));
		}

		stringBuilder.append(";");
		stringBuilder.append(Registry.BIOME.getId(this.field_14081));
		stringBuilder.append(";");
		if (!this.field_14074.isEmpty()) {
			int i = 0;

			for (Entry<String, Map<String, String>> entry : this.field_14074.entrySet()) {
				if (i++ > 0) {
					stringBuilder.append(",");
				}

				stringBuilder.append(((String)entry.getKey()).toLowerCase(Locale.ROOT));
				Map<String, String> map = (Map<String, String>)entry.getValue();
				if (!map.isEmpty()) {
					stringBuilder.append("(");
					int j = 0;

					for (Entry<String, String> entry2 : map.entrySet()) {
						if (j++ > 0) {
							stringBuilder.append(" ");
						}

						stringBuilder.append((String)entry2.getKey());
						stringBuilder.append("=");
						stringBuilder.append((String)entry2.getValue());
					}

					stringBuilder.append(")");
				}
			}
		}

		return stringBuilder.toString();
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	private static class_3229 method_14315(String string, int i) {
		String[] strings = string.split("\\*", 2);
		int j;
		if (strings.length == 2) {
			try {
				j = MathHelper.clamp(Integer.parseInt(strings[0]), 0, 256 - i);
			} catch (NumberFormatException var7) {
				field_14064.error("Error while parsing flat world string => {}", var7.getMessage());
				return null;
			}
		} else {
			j = 1;
		}

		Block block;
		try {
			block = method_14334(strings[strings.length - 1]);
		} catch (Exception var6) {
			field_14064.error("Error while parsing flat world string => {}", var6.getMessage());
			return null;
		}

		if (block == null) {
			field_14064.error("Error while parsing flat world string => Unknown block, {}", strings[strings.length - 1]);
			return null;
		} else {
			class_3229 lv = new class_3229(j, block);
			lv.method_14287(i);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	private static List<class_3229> method_14328(String string) {
		List<class_3229> list = Lists.<class_3229>newArrayList();
		String[] strings = string.split(",");
		int i = 0;

		for (String string2 : strings) {
			class_3229 lv = method_14315(string2, i);
			if (lv == null) {
				return Collections.emptyList();
			}

			list.add(lv);
			i += lv.method_14289();
		}

		return list;
	}

	@Environment(EnvType.CLIENT)
	public <T> Dynamic<T> method_14313(DynamicOps<T> dynamicOps) {
		T object = dynamicOps.createList(
			this.field_14072
				.stream()
				.map(
					arg -> dynamicOps.createMap(
							ImmutableMap.of(
								dynamicOps.createString("height"),
								dynamicOps.createInt(arg.method_14289()),
								dynamicOps.createString("block"),
								dynamicOps.createString(Registry.BLOCK.getId(arg.method_14286().getBlock()).toString())
							)
						)
				)
		);
		T object2 = dynamicOps.createMap(
			(Map<T, T>)this.field_14074
				.entrySet()
				.stream()
				.map(
					entry -> Pair.of(
							dynamicOps.createString(((String)entry.getKey()).toLowerCase(Locale.ROOT)),
							dynamicOps.createMap(
								(Map<T, T>)((Map)entry.getValue())
									.entrySet()
									.stream()
									.map(entryx -> Pair.of(dynamicOps.createString((String)entryx.getKey()), dynamicOps.createString((String)entryx.getValue())))
									.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
							)
						)
				)
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
		);
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("layers"),
					object,
					dynamicOps.createString("biome"),
					dynamicOps.createString(Registry.BIOME.getId(this.field_14081).toString()),
					dynamicOps.createString("structures"),
					object2
				)
			)
		);
	}

	public static FlatChunkGeneratorSettings method_14323(Dynamic<?> dynamic) {
		FlatChunkGeneratorSettings flatChunkGeneratorSettings = ChunkGeneratorType.field_12766.createSettings();
		List<Pair<Integer, Block>> list = (List<Pair<Integer, Block>>)((Stream)dynamic.get("layers").flatMap(Dynamic::getStream).orElse(Stream.empty()))
			.map(dynamicx -> Pair.of(dynamicx.getInt("height", 1), method_14334(dynamicx.getString("block"))))
			.collect(Collectors.toList());
		if (list.stream().anyMatch(pair -> pair.getSecond() == null)) {
			return method_14309();
		} else {
			List<class_3229> list2 = (List<class_3229>)list.stream()
				.map(pair -> new class_3229((Integer)pair.getFirst(), (Block)pair.getSecond()))
				.collect(Collectors.toList());
			if (list2.isEmpty()) {
				return method_14309();
			} else {
				flatChunkGeneratorSettings.method_14327().addAll(list2);
				flatChunkGeneratorSettings.method_14330();
				flatChunkGeneratorSettings.method_14325(Registry.BIOME.get(new Identifier(dynamic.getString("biome"))));
				dynamic.get("structures")
					.flatMap(Dynamic::getMapValues)
					.ifPresent(
						map -> map.keySet()
								.forEach(dynamicx -> dynamicx.getStringValue().map(string -> (Map)flatChunkGeneratorSettings.method_14333().put(string, Maps.newHashMap())))
					);
				return flatChunkGeneratorSettings;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static FlatChunkGeneratorSettings method_14319(String string) {
		Iterator<String> iterator = Splitter.on(';').split(string).iterator();
		if (!iterator.hasNext()) {
			return method_14309();
		} else {
			FlatChunkGeneratorSettings flatChunkGeneratorSettings = ChunkGeneratorType.field_12766.createSettings();
			List<class_3229> list = method_14328((String)iterator.next());
			if (list.isEmpty()) {
				return method_14309();
			} else {
				flatChunkGeneratorSettings.method_14327().addAll(list);
				flatChunkGeneratorSettings.method_14330();
				Biome biome = iterator.hasNext() ? Registry.BIOME.get(new Identifier((String)iterator.next())) : null;
				flatChunkGeneratorSettings.method_14325(biome == null ? Biomes.field_9451 : biome);
				if (iterator.hasNext()) {
					String[] strings = ((String)iterator.next()).toLowerCase(Locale.ROOT).split(",");

					for (String string2 : strings) {
						String[] strings2 = string2.split("\\(", 2);
						if (!strings2[0].isEmpty()) {
							flatChunkGeneratorSettings.method_14314(strings2[0]);
							if (strings2.length > 1 && strings2[1].endsWith(")") && strings2[1].length() > 1) {
								String[] strings3 = strings2[1].substring(0, strings2[1].length() - 1).split(" ");

								for (String string3 : strings3) {
									String[] strings4 = string3.split("=", 2);
									if (strings4.length == 2) {
										flatChunkGeneratorSettings.method_14324(strings2[0], strings4[0], strings4[1]);
									}
								}
							}
						}
					}
				} else {
					flatChunkGeneratorSettings.method_14333().put("village", Maps.newHashMap());
				}

				return flatChunkGeneratorSettings;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	private void method_14314(String string) {
		Map<String, String> map = Maps.<String, String>newHashMap();
		this.field_14074.put(string, map);
	}

	@Environment(EnvType.CLIENT)
	private void method_14324(String string, String string2, String string3) {
		((Map)this.field_14074.get(string)).put(string2, string3);
		if ("village".equals(string) && "distance".equals(string2)) {
			this.field_13146 = MathHelper.parseInt(string3, this.field_13146, 9);
		}

		if ("biome_1".equals(string) && "distance".equals(string2)) {
			this.field_13139 = MathHelper.parseInt(string3, this.field_13139, 9);
		}

		if ("stronghold".equals(string)) {
			if ("distance".equals(string2)) {
				this.field_13142 = MathHelper.parseInt(string3, this.field_13142, 1);
			} else if ("count".equals(string2)) {
				this.field_13141 = MathHelper.parseInt(string3, this.field_13141, 1);
			} else if ("spread".equals(string2)) {
				this.field_13140 = MathHelper.parseInt(string3, this.field_13140, 1);
			}
		}

		if ("oceanmonument".equals(string)) {
			if ("separation".equals(string2)) {
				this.field_13143 = MathHelper.parseInt(string3, this.field_13143, 1);
			} else if ("spacing".equals(string2)) {
				this.field_13144 = MathHelper.parseInt(string3, this.field_13144, 1);
			}
		}

		if ("endcity".equals(string) && "distance".equals(string2)) {
			this.field_13152 = MathHelper.parseInt(string3, this.field_13152, 1);
		}

		if ("mansion".equals(string) && "distance".equals(string2)) {
			this.field_13148 = MathHelper.parseInt(string3, this.field_13148, 1);
		}
	}

	public static FlatChunkGeneratorSettings method_14309() {
		FlatChunkGeneratorSettings flatChunkGeneratorSettings = ChunkGeneratorType.field_12766.createSettings();
		flatChunkGeneratorSettings.method_14325(Biomes.field_9451);
		flatChunkGeneratorSettings.method_14327().add(new class_3229(1, Blocks.field_9987));
		flatChunkGeneratorSettings.method_14327().add(new class_3229(2, Blocks.field_10566));
		flatChunkGeneratorSettings.method_14327().add(new class_3229(1, Blocks.field_10219));
		flatChunkGeneratorSettings.method_14330();
		flatChunkGeneratorSettings.method_14333().put("village", Maps.newHashMap());
		return flatChunkGeneratorSettings;
	}

	public boolean method_14320() {
		return this.field_14077;
	}

	public BlockState[] method_14312() {
		return this.field_14082;
	}
}
