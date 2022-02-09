package net.minecraft.world.gen.chunk;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.placement.StructuresConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FillLayerFeatureConfig;
import net.minecraft.world.gen.feature.MiscPlacedFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.feature.StructureFeature;
import org.slf4j.Logger;

public class FlatChunkGeneratorConfig {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Codec<FlatChunkGeneratorConfig> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.biomeRegistry),
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
	private final Registry<Biome> biomeRegistry;
	private final StructuresConfig structuresConfig;
	private final List<FlatChunkGeneratorLayer> layers = Lists.<FlatChunkGeneratorLayer>newArrayList();
	private RegistryEntry<Biome> biome;
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
		Optional<RegistryEntry<Biome>> biome
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
		if (biome.isEmpty()) {
			LOGGER.error("Unknown biome, defaulting to plains");
			this.biome = biomeRegistry.getOrCreateEntry(BiomeKeys.PLAINS);
		} else {
			this.biome = (RegistryEntry<Biome>)biome.get();
		}
	}

	public FlatChunkGeneratorConfig(StructuresConfig structuresConfig, Registry<Biome> biomeRegistry) {
		this.biomeRegistry = biomeRegistry;
		this.structuresConfig = structuresConfig;
		this.biome = biomeRegistry.getOrCreateEntry(BiomeKeys.PLAINS);
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

	public RegistryEntry<Biome> createBiome() {
		Biome biome = this.getBiome().value();
		GenerationSettings generationSettings = biome.getGenerationSettings();
		GenerationSettings.Builder builder = new GenerationSettings.Builder();
		if (this.hasLakes) {
			builder.feature(GenerationStep.Feature.LAKES, MiscPlacedFeatures.LAKE_LAVA_UNDERGROUND);
			builder.feature(GenerationStep.Feature.LAKES, MiscPlacedFeatures.LAKE_LAVA_SURFACE);
		}

		boolean bl = (!this.hasNoTerrain || this.biome.matchesKey(BiomeKeys.THE_VOID)) && this.hasFeatures;
		if (bl) {
			List<RegistryEntryList<PlacedFeature>> list = generationSettings.getFeatures();

			for (int i = 0; i < list.size(); i++) {
				if (i != GenerationStep.Feature.UNDERGROUND_STRUCTURES.ordinal() && i != GenerationStep.Feature.SURFACE_STRUCTURES.ordinal()) {
					for (RegistryEntry<PlacedFeature> registryEntry : (RegistryEntryList)list.get(i)) {
						builder.feature(i, registryEntry);
					}
				}
			}
		}

		List<BlockState> list = this.getLayerBlocks();

		for (int ix = 0; ix < list.size(); ix++) {
			BlockState blockState = (BlockState)list.get(ix);
			if (!Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(blockState)) {
				list.set(ix, null);
				builder.feature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, PlacedFeatures.createEntry(Feature.FILL_LAYER, new FillLayerFeatureConfig(ix, blockState)));
			}
		}

		return RegistryEntry.of(Biome.Builder.method_40137(biome).generationSettings(builder.build()).build());
	}

	public StructuresConfig getStructuresConfig() {
		return this.structuresConfig;
	}

	public RegistryEntry<Biome> getBiome() {
		return this.biome;
	}

	public void setBiome(RegistryEntry<Biome> registryEntry) {
		this.biome = registryEntry;
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

		this.hasNoTerrain = this.layerBlocks.stream().allMatch(state -> state.isOf(Blocks.AIR));
	}

	public static FlatChunkGeneratorConfig getDefaultConfig(Registry<Biome> biomeRegistry) {
		StructuresConfig structuresConfig = new StructuresConfig(
			ImmutableMap.of(
				StructureFeature.STRONGHOLD,
				StructuresConfig.STRONGHOLD_PLACEMENT,
				StructureFeature.VILLAGE,
				StructuresConfig.DEFAULT_PLACEMENTS.get(StructureFeature.VILLAGE)
			)
		);
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(structuresConfig, biomeRegistry);
		flatChunkGeneratorConfig.biome = biomeRegistry.getOrCreateEntry(BiomeKeys.PLAINS);
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(2, Blocks.DIRT));
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK));
		flatChunkGeneratorConfig.updateLayerBlocks();
		return flatChunkGeneratorConfig;
	}
}
