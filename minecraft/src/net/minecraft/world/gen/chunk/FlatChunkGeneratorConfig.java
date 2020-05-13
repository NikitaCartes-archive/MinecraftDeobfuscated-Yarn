package net.minecraft.world.gen.chunk;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OptionalDynamic;
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
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.BastionRemnantFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.RuinedPortalFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatChunkGeneratorConfig {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ConfiguredFeature<?, ? extends StructureFeature<?>> MINESHAFT = Feature.MINESHAFT
		.configure(new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL));
	private static final ConfiguredFeature<?, ? extends StructureFeature<?>> VILLAGE = Feature.VILLAGE
		.configure(new StructurePoolFeatureConfig("village/plains/town_centers", 6));
	private static final ConfiguredFeature<?, ? extends StructureFeature<?>> STRONGHOLD = Feature.STRONGHOLD.configure(FeatureConfig.DEFAULT);
	private static final ConfiguredFeature<?, ? extends StructureFeature<?>> SWAMP_HUT = Feature.SWAMP_HUT.configure(FeatureConfig.DEFAULT);
	private static final ConfiguredFeature<?, ? extends StructureFeature<?>> DESERT_PYRAMID = Feature.DESERT_PYRAMID.configure(FeatureConfig.DEFAULT);
	private static final ConfiguredFeature<?, ? extends StructureFeature<?>> JUNGLE_TEMPLE = Feature.JUNGLE_TEMPLE.configure(FeatureConfig.DEFAULT);
	private static final ConfiguredFeature<?, ? extends StructureFeature<?>> IGLOO = Feature.IGLOO.configure(FeatureConfig.DEFAULT);
	private static final ConfiguredFeature<?, ? extends StructureFeature<?>> SHIPWRECK = Feature.SHIPWRECK.configure(new ShipwreckFeatureConfig(false));
	private static final ConfiguredFeature<?, ? extends StructureFeature<?>> OCEAN_MONUMENT = Feature.OCEAN_MONUMENT.configure(FeatureConfig.DEFAULT);
	private static final ConfiguredFeature<?, ? extends StructureFeature<?>> END_CITY = Feature.END_CITY.configure(FeatureConfig.DEFAULT);
	private static final ConfiguredFeature<?, ? extends StructureFeature<?>> WOODLAND_MANSION = Feature.WOODLAND_MANSION.configure(FeatureConfig.DEFAULT);
	private static final ConfiguredFeature<?, ? extends StructureFeature<?>> NETHER_BRIDGE = Feature.NETHER_BRIDGE.configure(FeatureConfig.DEFAULT);
	private static final ConfiguredFeature<?, ? extends StructureFeature<?>> field_24017 = Feature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig());
	private static final ConfiguredFeature<?, ? extends StructureFeature<?>> OCEAN_RUIN = Feature.OCEAN_RUIN
		.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3F, 0.1F));
	private static final ConfiguredFeature<?, ? extends StructureFeature<?>> PILLAGER_OUTPOST = Feature.PILLAGER_OUTPOST.configure(FeatureConfig.DEFAULT);
	private static final ConfiguredFeature<?, ? extends StructureFeature<?>> field_24422 = Feature.BASTION_REMNANT
		.configure(new BastionRemnantFeatureConfig(BastionRemnantGenerator.START_POOLS_TO_SIZES));
	private static final ConfiguredFeature<?, ?> WATER_LAKE = Feature.LAKE
		.configure(new SingleStateFeatureConfig(Blocks.WATER.getDefaultState()))
		.createDecoratedFeature(Decorator.WATER_LAKE.configure(new ChanceDecoratorConfig(4)));
	private static final ConfiguredFeature<?, ?> LAVA_LAKE = Feature.LAKE
		.configure(new SingleStateFeatureConfig(Blocks.LAVA.getDefaultState()))
		.createDecoratedFeature(Decorator.LAVA_LAKE.configure(new ChanceDecoratorConfig(80)));
	public static final Map<ConfiguredFeature<?, ?>, GenerationStep.Feature> FEATURE_TO_GENERATION_STEP = Util.make(
		Maps.<ConfiguredFeature<?, ?>, GenerationStep.Feature>newHashMap(), hashMap -> {
			hashMap.put(MINESHAFT, GenerationStep.Feature.UNDERGROUND_STRUCTURES);
			hashMap.put(VILLAGE, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(STRONGHOLD, GenerationStep.Feature.UNDERGROUND_STRUCTURES);
			hashMap.put(SWAMP_HUT, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(DESERT_PYRAMID, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(JUNGLE_TEMPLE, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(IGLOO, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(field_24017, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(SHIPWRECK, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(OCEAN_RUIN, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(WATER_LAKE, GenerationStep.Feature.LOCAL_MODIFICATIONS);
			hashMap.put(LAVA_LAKE, GenerationStep.Feature.LOCAL_MODIFICATIONS);
			hashMap.put(END_CITY, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(WOODLAND_MANSION, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(NETHER_BRIDGE, GenerationStep.Feature.UNDERGROUND_STRUCTURES);
			hashMap.put(OCEAN_MONUMENT, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(PILLAGER_OUTPOST, GenerationStep.Feature.SURFACE_STRUCTURES);
			hashMap.put(field_24422, GenerationStep.Feature.SURFACE_STRUCTURES);
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
			hashMap.put("ruined_portal", new ConfiguredFeature[]{field_24017});
			hashMap.put("bastion_remnant", new ConfiguredFeature[]{field_24422});
		}
	);
	public static final Map<ConfiguredFeature<?, ? extends StructureFeature<?>>, FeatureConfig> FEATURE_TO_FEATURE_CONFIG = Util.make(
		Maps.<ConfiguredFeature<?, ? extends StructureFeature<?>>, FeatureConfig>newHashMap(), hashMap -> {
			hashMap.put(MINESHAFT, new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL));
			hashMap.put(VILLAGE, new StructurePoolFeatureConfig("village/plains/town_centers", 6));
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
			hashMap.put(field_24422, new BastionRemnantFeatureConfig(BastionRemnantGenerator.START_POOLS_TO_SIZES));
		}
	);
	private final ChunkGeneratorConfig field_24560;
	private final List<FlatChunkGeneratorLayer> layers = Lists.<FlatChunkGeneratorLayer>newArrayList();
	private final Map<String, Map<String, String>> structures = Maps.<String, Map<String, String>>newHashMap();
	private Biome biome;
	private final BlockState[] layerBlocks = new BlockState[256];
	private boolean hasNoTerrain;
	private int groundHeight;

	public FlatChunkGeneratorConfig() {
		this(new ChunkGeneratorConfig());
	}

	public FlatChunkGeneratorConfig(ChunkGeneratorConfig chunkGeneratorConfig) {
		this.field_24560 = chunkGeneratorConfig;
	}

	public ChunkGeneratorConfig method_28051() {
		return this.field_24560;
	}

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
				if (!blockState.isOf(Blocks.AIR)) {
					this.hasNoTerrain = false;
					this.layerBlocks[j] = blockState;
				}
			}

			if (flatChunkGeneratorLayer.getBlockState().isOf(Blocks.AIR)) {
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
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig();
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
				OptionalDynamic<?> optionalDynamic = dynamic.get("biome");
				flatChunkGeneratorConfig.setBiome((Biome)Registry.BIOME.getOrEmpty(new Identifier(optionalDynamic.asString(""))).orElseGet(() -> {
					LOGGER.error("Unknown biome, defaulting to plains: " + optionalDynamic);
					return Biomes.PLAINS;
				}));
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
			FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig();
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
	private void setStructureOption(String string, String string2, String value) {
		((Map)this.structures.get(string)).put(string2, value);
		this.field_24560.method_28000(string, string2, value);
	}

	public static FlatChunkGeneratorConfig getDefaultConfig() {
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig();
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
