package net.minecraft.world.gen.chunk;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FillLayerFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatChunkGeneratorConfig {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Codec<FlatChunkGeneratorConfig> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.biomeRegistry),
						StructuresConfig.CODEC.fieldOf("structures").forGetter(FlatChunkGeneratorConfig::getStructuresConfig),
						FlatChunkGeneratorLayer.CODEC.listOf().fieldOf("layers").forGetter(FlatChunkGeneratorConfig::getLayers),
						Codec.BOOL.fieldOf("lakes").orElse(false).forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.hasLakes),
						Codec.BOOL.fieldOf("features").orElse(false).forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.hasFeatures),
						Biome.REGISTRY_CODEC
							.optionalFieldOf("biome")
							.orElseGet(Optional::empty)
							.forGetter(flatChunkGeneratorConfig -> Optional.of(flatChunkGeneratorConfig.biome))
					)
					.apply(instance, FlatChunkGeneratorConfig::new)
		)
		.<FlatChunkGeneratorConfig>comapFlatMap(FlatChunkGeneratorConfig::checkHeight, Function.identity())
		.stable();
	private static final Map<StructureFeature<?>, ConfiguredStructureFeature<?, ?>> STRUCTURE_TO_FEATURES = Util.make(
		Maps.<StructureFeature<?>, ConfiguredStructureFeature<?, ?>>newHashMap(), hashMap -> {
			hashMap.put(StructureFeature.MINESHAFT, ConfiguredStructureFeatures.MINESHAFT);
			hashMap.put(StructureFeature.VILLAGE, ConfiguredStructureFeatures.VILLAGE_PLAINS);
			hashMap.put(StructureFeature.STRONGHOLD, ConfiguredStructureFeatures.STRONGHOLD);
			hashMap.put(StructureFeature.SWAMP_HUT, ConfiguredStructureFeatures.SWAMP_HUT);
			hashMap.put(StructureFeature.DESERT_PYRAMID, ConfiguredStructureFeatures.DESERT_PYRAMID);
			hashMap.put(StructureFeature.JUNGLE_PYRAMID, ConfiguredStructureFeatures.JUNGLE_PYRAMID);
			hashMap.put(StructureFeature.IGLOO, ConfiguredStructureFeatures.IGLOO);
			hashMap.put(StructureFeature.OCEAN_RUIN, ConfiguredStructureFeatures.OCEAN_RUIN_COLD);
			hashMap.put(StructureFeature.SHIPWRECK, ConfiguredStructureFeatures.SHIPWRECK);
			hashMap.put(StructureFeature.MONUMENT, ConfiguredStructureFeatures.MONUMENT);
			hashMap.put(StructureFeature.END_CITY, ConfiguredStructureFeatures.END_CITY);
			hashMap.put(StructureFeature.MANSION, ConfiguredStructureFeatures.MANSION);
			hashMap.put(StructureFeature.FORTRESS, ConfiguredStructureFeatures.FORTRESS);
			hashMap.put(StructureFeature.PILLAGER_OUTPOST, ConfiguredStructureFeatures.PILLAGER_OUTPOST);
			hashMap.put(StructureFeature.RUINED_PORTAL, ConfiguredStructureFeatures.RUINED_PORTAL);
			hashMap.put(StructureFeature.BASTION_REMNANT, ConfiguredStructureFeatures.BASTION_REMNANT);
		}
	);
	private final Registry<Biome> biomeRegistry;
	private final StructuresConfig structuresConfig;
	private final List<FlatChunkGeneratorLayer> layers = Lists.<FlatChunkGeneratorLayer>newArrayList();
	private Supplier<Biome> biome;
	private final List<BlockState> layerBlocks;
	private boolean hasNoTerrain;
	private boolean hasFeatures;
	private boolean hasLakes;

	private static DataResult<FlatChunkGeneratorConfig> checkHeight(FlatChunkGeneratorConfig config) {
		int i = config.layers.stream().mapToInt(FlatChunkGeneratorLayer::getThickness).sum();
		return i > DimensionType.MAX_HEIGHT ? DataResult.error("Sum of layer heights is > " + DimensionType.MAX_HEIGHT, config) : DataResult.success(config);
	}

	private FlatChunkGeneratorConfig(
		Registry<Biome> biomeRegistry,
		StructuresConfig structuresConfig,
		List<FlatChunkGeneratorLayer> layers,
		boolean hasLakes,
		boolean hasFeatures,
		Optional<Supplier<Biome>> biome
	) {
		this(structuresConfig, biomeRegistry);
		if (hasLakes) {
			this.enableLakes();
		}

		if (hasFeatures) {
			this.enableFeatures();
		}

		this.layers.addAll(layers);
		this.updateLayerBlocks();
		if (!biome.isPresent()) {
			LOGGER.error("Unknown biome, defaulting to plains");
			this.biome = () -> biomeRegistry.getOrThrow(BiomeKeys.PLAINS);
		} else {
			this.biome = (Supplier<Biome>)biome.get();
		}
	}

	public FlatChunkGeneratorConfig(StructuresConfig structuresConfig, Registry<Biome> biomeRegistry) {
		this.biomeRegistry = biomeRegistry;
		this.structuresConfig = structuresConfig;
		this.biome = () -> biomeRegistry.getOrThrow(BiomeKeys.PLAINS);
		this.layerBlocks = Lists.<BlockState>newArrayList();
	}

	public FlatChunkGeneratorConfig withStructuresConfig(StructuresConfig structuresConfig) {
		return this.withLayers(this.layers, structuresConfig);
	}

	public FlatChunkGeneratorConfig withLayers(List<FlatChunkGeneratorLayer> layers, StructuresConfig structuresConfig) {
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(structuresConfig, this.biomeRegistry);

		for (FlatChunkGeneratorLayer flatChunkGeneratorLayer : layers) {
			flatChunkGeneratorConfig.layers.add(new FlatChunkGeneratorLayer(flatChunkGeneratorLayer.getThickness(), flatChunkGeneratorLayer.getBlockState().getBlock()));
			flatChunkGeneratorConfig.updateLayerBlocks();
		}

		flatChunkGeneratorConfig.setBiome(this.biome);
		if (this.hasFeatures) {
			flatChunkGeneratorConfig.enableFeatures();
		}

		if (this.hasLakes) {
			flatChunkGeneratorConfig.enableLakes();
		}

		return flatChunkGeneratorConfig;
	}

	public void enableFeatures() {
		this.hasFeatures = true;
	}

	public void enableLakes() {
		this.hasLakes = true;
	}

	public Biome createBiome() {
		Biome biome = this.getBiome();
		GenerationSettings generationSettings = biome.getGenerationSettings();
		GenerationSettings.Builder builder = new GenerationSettings.Builder().surfaceBuilder(generationSettings.getSurfaceBuilder());
		if (this.hasLakes) {
			builder.feature(GenerationStep.Feature.LAKES, ConfiguredFeatures.LAKE_WATER);
			builder.feature(GenerationStep.Feature.LAKES, ConfiguredFeatures.LAKE_LAVA);
		}

		for (Entry<StructureFeature<?>, StructureConfig> entry : this.structuresConfig.getStructures().entrySet()) {
			builder.structureFeature(generationSettings.method_30978((ConfiguredStructureFeature<?, ?>)STRUCTURE_TO_FEATURES.get(entry.getKey())));
		}

		boolean bl = (!this.hasNoTerrain || this.biomeRegistry.getKey(biome).equals(Optional.of(BiomeKeys.THE_VOID))) && this.hasFeatures;
		if (bl) {
			List<List<Supplier<ConfiguredFeature<?, ?>>>> list = generationSettings.getFeatures();

			for (int i = 0; i < list.size(); i++) {
				if (i != GenerationStep.Feature.UNDERGROUND_STRUCTURES.ordinal() && i != GenerationStep.Feature.SURFACE_STRUCTURES.ordinal()) {
					for (Supplier<ConfiguredFeature<?, ?>> supplier : (List)list.get(i)) {
						builder.feature(i, supplier);
					}
				}
			}
		}

		List<BlockState> list = this.getLayerBlocks();

		for (int ix = 0; ix < list.size(); ix++) {
			BlockState blockState = (BlockState)list.get(ix);
			if (!Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(blockState)) {
				list.set(ix, null);
				builder.feature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, Feature.FILL_LAYER.configure(new FillLayerFeatureConfig(ix, blockState)));
			}
		}

		return new Biome.Builder()
			.precipitation(biome.getPrecipitation())
			.category(biome.getCategory())
			.temperature(biome.getTemperature())
			.downfall(biome.getDownfall())
			.effects(biome.getEffects())
			.generationSettings(builder.build())
			.spawnSettings(biome.getSpawnSettings())
			.build();
	}

	public StructuresConfig getStructuresConfig() {
		return this.structuresConfig;
	}

	public Biome getBiome() {
		return (Biome)this.biome.get();
	}

	public void setBiome(Supplier<Biome> biome) {
		this.biome = biome;
	}

	public List<FlatChunkGeneratorLayer> getLayers() {
		return this.layers;
	}

	public List<BlockState> getLayerBlocks() {
		return this.layerBlocks;
	}

	public void updateLayerBlocks() {
		this.layerBlocks.clear();

		for (FlatChunkGeneratorLayer flatChunkGeneratorLayer : this.layers) {
			for (int i = 0; i < flatChunkGeneratorLayer.getThickness(); i++) {
				this.layerBlocks.add(flatChunkGeneratorLayer.getBlockState());
			}
		}

		this.hasNoTerrain = this.layerBlocks.stream().allMatch(blockState -> blockState.isOf(Blocks.AIR));
	}

	public static FlatChunkGeneratorConfig getDefaultConfig(Registry<Biome> biomeRegistry) {
		StructuresConfig structuresConfig = new StructuresConfig(
			Optional.of(StructuresConfig.DEFAULT_STRONGHOLD),
			Maps.<StructureFeature<?>, StructureConfig>newHashMap(
				ImmutableMap.of(StructureFeature.VILLAGE, StructuresConfig.DEFAULT_STRUCTURES.get(StructureFeature.VILLAGE))
			)
		);
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(structuresConfig, biomeRegistry);
		flatChunkGeneratorConfig.biome = () -> biomeRegistry.getOrThrow(BiomeKeys.PLAINS);
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(2, Blocks.DIRT));
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK));
		flatChunkGeneratorConfig.updateLayerBlocks();
		return flatChunkGeneratorConfig;
	}
}
