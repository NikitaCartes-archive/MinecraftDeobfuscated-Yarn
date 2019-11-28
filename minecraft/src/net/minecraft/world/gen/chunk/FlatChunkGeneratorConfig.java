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
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.LakeDecoratorConfig;
import net.minecraft.world.gen.feature.BushFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.VillageFeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatChunkGeneratorConfig extends ChunkGeneratorConfig {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ConfiguredFeature<?, ?> MINESHAFT = Feature.MINESHAFT
		.configure(new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL))
		.createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
	private static final ConfiguredFeature<?, ?> VILLAGE = Feature.VILLAGE
		.configure(new VillageFeatureConfig("village/plains/town_centers", 6))
		.createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
	private static final ConfiguredFeature<?, ?> STRONGHOLD = Feature.STRONGHOLD
		.configure(FeatureConfig.DEFAULT)
		.createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
	private static final ConfiguredFeature<?, ?> SWAMP_HUT = Feature.SWAMP_HUT
		.configure(FeatureConfig.DEFAULT)
		.createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
	private static final ConfiguredFeature<?, ?> DESERT_PYRAMID = Feature.DESERT_PYRAMID
		.configure(FeatureConfig.DEFAULT)
		.createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
	private static final ConfiguredFeature<?, ?> JUNGLE_TEMPLE = Feature.JUNGLE_TEMPLE
		.configure(FeatureConfig.DEFAULT)
		.createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
	private static final ConfiguredFeature<?, ?> IGLOO = Feature.IGLOO
		.configure(FeatureConfig.DEFAULT)
		.createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
	private static final ConfiguredFeature<?, ?> SHIPWRECK = Feature.SHIPWRECK
		.configure(new ShipwreckFeatureConfig(false))
		.createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
	private static final ConfiguredFeature<?, ?> OCEAN_MONUMENT = Feature.OCEAN_MONUMENT
		.configure(FeatureConfig.DEFAULT)
		.createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
	private static final ConfiguredFeature<?, ?> WATER_LAKE = Feature.LAKE
		.configure(new BushFeatureConfig(Blocks.WATER.getDefaultState()))
		.createDecoratedFeature(Decorator.WATER_LAKE.configure(new LakeDecoratorConfig(4)));
	private static final ConfiguredFeature<?, ?> LAVA_LAKE = Feature.LAKE
		.configure(new BushFeatureConfig(Blocks.LAVA.getDefaultState()))
		.createDecoratedFeature(Decorator.LAVA_LAKE.configure(new LakeDecoratorConfig(80)));
	private static final ConfiguredFeature<?, ?> END_CITY = Feature.END_CITY
		.configure(FeatureConfig.DEFAULT)
		.createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
	private static final ConfiguredFeature<?, ?> WOODLAND_MANSION = Feature.WOODLAND_MANSION
		.configure(FeatureConfig.DEFAULT)
		.createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
	private static final ConfiguredFeature<?, ?> NETHER_BRIDGE = Feature.NETHER_BRIDGE
		.configure(FeatureConfig.DEFAULT)
		.createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
	private static final ConfiguredFeature<?, ?> OCEAN_RUIN = Feature.OCEAN_RUIN
		.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3F, 0.1F))
		.createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
	private static final ConfiguredFeature<?, ?> PILLAGER_OUTPOST = Feature.PILLAGER_OUTPOST
		.configure(FeatureConfig.DEFAULT)
		.createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
	public static final Map<ConfiguredFeature<?, ?>, GenerationStep.Feature> FEATURE_TO_GENERATION_STEP = Util.make(
		Maps.<ConfiguredFeature<?, ?>, GenerationStep.Feature>newHashMap(), hashMap -> {
			hashMap.put(MINESHAFT, GenerationStep.Feature.UNDERGROUND_STRUCTURES);
			hashMap.put(VILLAGE, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(STRONGHOLD, GenerationStep.Feature.UNDERGROUND_STRUCTURES);
			hashMap.put(SWAMP_HUT, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(DESERT_PYRAMID, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(JUNGLE_TEMPLE, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(IGLOO, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(SHIPWRECK, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(OCEAN_RUIN, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(WATER_LAKE, GenerationStep.Feature.LOCAL_MODIFICATIONS);
			hashMap.put(LAVA_LAKE, GenerationStep.Feature.LOCAL_MODIFICATIONS);
			hashMap.put(END_CITY, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(WOODLAND_MANSION, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(NETHER_BRIDGE, GenerationStep.Feature.UNDERGROUND_STRUCTURES);
			hashMap.put(OCEAN_MONUMENT, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(PILLAGER_OUTPOST, GenerationStep.Feature.SURFACE_STRUCTURES);
		}
	);
	public static final Map<String, ConfiguredFeature<?, ?>[]> STRUCTURE_TO_FEATURES = Util.make(
		Maps.<String, ConfiguredFeature<?, ?>[]>newHashMap(), hashMap -> {
			hashMap.put("mineshaft", new ConfiguredFeature[]{MINESHAFT});
			hashMap.put("village", new ConfiguredFeature[]{VILLAGE});
			hashMap.put("stronghold", new ConfiguredFeature[]{STRONGHOLD});
			hashMap.put("biome_1", new ConfiguredFeature[]{SWAMP_HUT, DESERT_PYRAMID, JUNGLE_TEMPLE, IGLOO, OCEAN_RUIN, SHIPWRECK});
			hashMap.put("oceanmonument", new ConfiguredFeature[]{OCEAN_MONUMENT});
			hashMap.put("lake", new ConfiguredFeature[]{WATER_LAKE});
			hashMap.put("lava_lake", new ConfiguredFeature[]{LAVA_LAKE});
			hashMap.put("endcity", new ConfiguredFeature[]{END_CITY});
			hashMap.put("mansion", new ConfiguredFeature[]{WOODLAND_MANSION});
			hashMap.put("fortress", new ConfiguredFeature[]{NETHER_BRIDGE});
			hashMap.put("pillager_outpost", new ConfiguredFeature[]{PILLAGER_OUTPOST});
		}
	);
	public static final Map<ConfiguredFeature<?, ?>, FeatureConfig> FEATURE_TO_FEATURE_CONFIG = Util.make(
		Maps.<ConfiguredFeature<?, ?>, FeatureConfig>newHashMap(), hashMap -> {
			hashMap.put(MINESHAFT, new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL));
			hashMap.put(VILLAGE, new VillageFeatureConfig("village/plains/town_centers", 6));
			hashMap.put(STRONGHOLD, FeatureConfig.DEFAULT);
			hashMap.put(SWAMP_HUT, FeatureConfig.DEFAULT);
			hashMap.put(DESERT_PYRAMID, FeatureConfig.DEFAULT);
			hashMap.put(JUNGLE_TEMPLE, FeatureConfig.DEFAULT);
			hashMap.put(IGLOO, FeatureConfig.DEFAULT);
			hashMap.put(OCEAN_RUIN, new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3F, 0.9F));
			hashMap.put(SHIPWRECK, new ShipwreckFeatureConfig(false));
			hashMap.put(OCEAN_MONUMENT, FeatureConfig.DEFAULT);
			hashMap.put(END_CITY, FeatureConfig.DEFAULT);
			hashMap.put(WOODLAND_MANSION, FeatureConfig.DEFAULT);
			hashMap.put(NETHER_BRIDGE, FeatureConfig.DEFAULT);
			hashMap.put(PILLAGER_OUTPOST, FeatureConfig.DEFAULT);
		}
	);
	private final List<FlatChunkGeneratorLayer> layers = Lists.<FlatChunkGeneratorLayer>newArrayList();
	private final Map<String, Map<String, String>> structures = Maps.<String, Map<String, String>>newHashMap();
	private Biome biome;
	private final BlockState[] layerBlocks = new BlockState[256];
	private boolean hasNoTerrain;
	private int groundHeight;

	@Nullable
	public static Block parseBlock(String string) {
		try {
			Identifier identifier = new Identifier(string);
			return (Block)Registry.BLOCK.getOrEmpty(identifier).orElse(null);
		} catch (IllegalArgumentException var2) {
			LOGGER.warn("Invalid blockstate: {}", string, var2);
			return null;
		}
	}

	public Biome getBiome() {
		return this.biome;
	}

	public void setBiome(Biome biome) {
		this.biome = biome;
	}

	public Map<String, Map<String, String>> getStructures() {
		return this.structures;
	}

	public List<FlatChunkGeneratorLayer> getLayers() {
		return this.layers;
	}

	public void updateLayerBlocks() {
		int i = 0;

		for (FlatChunkGeneratorLayer flatChunkGeneratorLayer : this.layers) {
			flatChunkGeneratorLayer.setStartY(i);
			i += flatChunkGeneratorLayer.getThickness();
		}

		this.groundHeight = 0;
		this.hasNoTerrain = true;
		i = 0;

		for (FlatChunkGeneratorLayer flatChunkGeneratorLayer : this.layers) {
			for (int j = flatChunkGeneratorLayer.getStartY(); j < flatChunkGeneratorLayer.getStartY() + flatChunkGeneratorLayer.getThickness(); j++) {
				BlockState blockState = flatChunkGeneratorLayer.getBlockState();
				if (blockState.getBlock() != Blocks.AIR) {
					this.hasNoTerrain = false;
					this.layerBlocks[j] = blockState;
				}
			}

			if (flatChunkGeneratorLayer.getBlockState().getBlock() == Blocks.AIR) {
				i += flatChunkGeneratorLayer.getThickness();
			} else {
				this.groundHeight = this.groundHeight + flatChunkGeneratorLayer.getThickness() + i;
				i = 0;
			}
		}
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < this.layers.size(); i++) {
			if (i > 0) {
				stringBuilder.append(",");
			}

			stringBuilder.append(this.layers.get(i));
		}

		stringBuilder.append(";");
		stringBuilder.append(Registry.BIOME.getId(this.biome));
		stringBuilder.append(";");
		if (!this.structures.isEmpty()) {
			int i = 0;

			for (Entry<String, Map<String, String>> entry : this.structures.entrySet()) {
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
	private static FlatChunkGeneratorLayer parseLayerString(String string, int startY) {
		String[] strings = string.split("\\*", 2);
		int i;
		if (strings.length == 2) {
			try {
				i = Math.max(Integer.parseInt(strings[0]), 0);
			} catch (NumberFormatException var9) {
				LOGGER.error("Error while parsing flat world string => {}", var9.getMessage());
				return null;
			}
		} else {
			i = 1;
		}

		int j = Math.min(startY + i, 256);
		int k = j - startY;

		Block block;
		try {
			block = parseBlock(strings[strings.length - 1]);
		} catch (Exception var8) {
			LOGGER.error("Error while parsing flat world string => {}", var8.getMessage());
			return null;
		}

		if (block == null) {
			LOGGER.error("Error while parsing flat world string => Unknown block, {}", strings[strings.length - 1]);
			return null;
		} else {
			FlatChunkGeneratorLayer flatChunkGeneratorLayer = new FlatChunkGeneratorLayer(k, block);
			flatChunkGeneratorLayer.setStartY(startY);
			return flatChunkGeneratorLayer;
		}
	}

	@Environment(EnvType.CLIENT)
	private static List<FlatChunkGeneratorLayer> parseLayersString(String string) {
		List<FlatChunkGeneratorLayer> list = Lists.<FlatChunkGeneratorLayer>newArrayList();
		String[] strings = string.split(",");
		int i = 0;

		for (String string2 : strings) {
			FlatChunkGeneratorLayer flatChunkGeneratorLayer = parseLayerString(string2, i);
			if (flatChunkGeneratorLayer == null) {
				return Collections.emptyList();
			}

			list.add(flatChunkGeneratorLayer);
			i += flatChunkGeneratorLayer.getThickness();
		}

		return list;
	}

	@Environment(EnvType.CLIENT)
	public <T> Dynamic<T> toDynamic(DynamicOps<T> dynamicOps) {
		T object = dynamicOps.createList(
			this.layers
				.stream()
				.map(
					flatChunkGeneratorLayer -> dynamicOps.createMap(
							ImmutableMap.of(
								dynamicOps.createString("height"),
								dynamicOps.createInt(flatChunkGeneratorLayer.getThickness()),
								dynamicOps.createString("block"),
								dynamicOps.createString(Registry.BLOCK.getId(flatChunkGeneratorLayer.getBlockState().getBlock()).toString())
							)
						)
				)
		);
		T object2 = dynamicOps.createMap(
			(Map<T, T>)this.structures
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
					dynamicOps.createString(Registry.BIOME.getId(this.biome).toString()),
					dynamicOps.createString("structures"),
					object2
				)
			)
		);
	}

	public static FlatChunkGeneratorConfig fromDynamic(Dynamic<?> dynamic) {
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = ChunkGeneratorType.FLAT.createSettings();
		List<Pair<Integer, Block>> list = dynamic.get("layers")
			.asList(dynamicx -> Pair.of(dynamicx.get("height").asInt(1), parseBlock(dynamicx.get("block").asString(""))));
		if (list.stream().anyMatch(pair -> pair.getSecond() == null)) {
			return getDefaultConfig();
		} else {
			List<FlatChunkGeneratorLayer> list2 = (List<FlatChunkGeneratorLayer>)list.stream()
				.map(pair -> new FlatChunkGeneratorLayer((Integer)pair.getFirst(), (Block)pair.getSecond()))
				.collect(Collectors.toList());
			if (list2.isEmpty()) {
				return getDefaultConfig();
			} else {
				flatChunkGeneratorConfig.getLayers().addAll(list2);
				flatChunkGeneratorConfig.updateLayerBlocks();
				flatChunkGeneratorConfig.setBiome(Registry.BIOME.get(new Identifier(dynamic.get("biome").asString(""))));
				dynamic.get("structures")
					.flatMap(Dynamic::getMapValues)
					.ifPresent(
						map -> map.keySet().forEach(dynamicx -> dynamicx.asString().map(string -> (Map)flatChunkGeneratorConfig.getStructures().put(string, Maps.newHashMap())))
					);
				return flatChunkGeneratorConfig;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static FlatChunkGeneratorConfig fromString(String string) {
		Iterator<String> iterator = Splitter.on(';').split(string).iterator();
		if (!iterator.hasNext()) {
			return getDefaultConfig();
		} else {
			FlatChunkGeneratorConfig flatChunkGeneratorConfig = ChunkGeneratorType.FLAT.createSettings();
			List<FlatChunkGeneratorLayer> list = parseLayersString((String)iterator.next());
			if (list.isEmpty()) {
				return getDefaultConfig();
			} else {
				flatChunkGeneratorConfig.getLayers().addAll(list);
				flatChunkGeneratorConfig.updateLayerBlocks();
				Biome biome = Biomes.PLAINS;
				if (iterator.hasNext()) {
					try {
						Identifier identifier = new Identifier((String)iterator.next());
						biome = (Biome)Registry.BIOME.getOrEmpty(identifier).orElseThrow(() -> new IllegalArgumentException("Invalid Biome: " + identifier));
					} catch (Exception var17) {
						LOGGER.error("Error while parsing flat world string => {}", var17.getMessage());
					}
				}

				flatChunkGeneratorConfig.setBiome(biome);
				if (iterator.hasNext()) {
					String[] strings = ((String)iterator.next()).toLowerCase(Locale.ROOT).split(",");

					for (String string2 : strings) {
						String[] strings2 = string2.split("\\(", 2);
						if (!strings2[0].isEmpty()) {
							flatChunkGeneratorConfig.addStructure(strings2[0]);
							if (strings2.length > 1 && strings2[1].endsWith(")") && strings2[1].length() > 1) {
								String[] strings3 = strings2[1].substring(0, strings2[1].length() - 1).split(" ");

								for (String string3 : strings3) {
									String[] strings4 = string3.split("=", 2);
									if (strings4.length == 2) {
										flatChunkGeneratorConfig.setStructureOption(strings2[0], strings4[0], strings4[1]);
									}
								}
							}
						}
					}
				} else {
					flatChunkGeneratorConfig.getStructures().put("village", Maps.newHashMap());
				}

				return flatChunkGeneratorConfig;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	private void addStructure(String id) {
		Map<String, String> map = Maps.<String, String>newHashMap();
		this.structures.put(id, map);
	}

	@Environment(EnvType.CLIENT)
	private void setStructureOption(String structure, String key, String value) {
		((Map)this.structures.get(structure)).put(key, value);
		if ("village".equals(structure) && "distance".equals(key)) {
			this.villageDistance = MathHelper.parseInt(value, this.villageDistance, 9);
		}

		if ("biome_1".equals(structure) && "distance".equals(key)) {
			this.templeDistance = MathHelper.parseInt(value, this.templeDistance, 9);
		}

		if ("stronghold".equals(structure)) {
			if ("distance".equals(key)) {
				this.strongholdDistance = MathHelper.parseInt(value, this.strongholdDistance, 1);
			} else if ("count".equals(key)) {
				this.strongholdCount = MathHelper.parseInt(value, this.strongholdCount, 1);
			} else if ("spread".equals(key)) {
				this.strongholdSpread = MathHelper.parseInt(value, this.strongholdSpread, 1);
			}
		}

		if ("oceanmonument".equals(structure)) {
			if ("separation".equals(key)) {
				this.oceanMonumentSeparation = MathHelper.parseInt(value, this.oceanMonumentSeparation, 1);
			} else if ("spacing".equals(key)) {
				this.oceanMonumentSpacing = MathHelper.parseInt(value, this.oceanMonumentSpacing, 1);
			}
		}

		if ("endcity".equals(structure) && "distance".equals(key)) {
			this.endCityDistance = MathHelper.parseInt(value, this.endCityDistance, 1);
		}

		if ("mansion".equals(structure) && "distance".equals(key)) {
			this.mansionDistance = MathHelper.parseInt(value, this.mansionDistance, 1);
		}
	}

	public static FlatChunkGeneratorConfig getDefaultConfig() {
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = ChunkGeneratorType.FLAT.createSettings();
		flatChunkGeneratorConfig.setBiome(Biomes.PLAINS);
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(2, Blocks.DIRT));
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK));
		flatChunkGeneratorConfig.updateLayerBlocks();
		flatChunkGeneratorConfig.getStructures().put("village", Maps.newHashMap());
		return flatChunkGeneratorConfig;
	}

	public boolean hasNoTerrain() {
		return this.hasNoTerrain;
	}

	public BlockState[] getLayerBlocks() {
		return this.layerBlocks;
	}

	public void removeLayerBlock(int layer) {
		this.layerBlocks[layer] = null;
	}
}
