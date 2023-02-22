package net.minecraft.world.gen.chunk;

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
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureSetKeys;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FillLayerFeatureConfig;
import net.minecraft.world.gen.feature.MiscPlacedFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import org.slf4j.Logger;

public class FlatChunkGeneratorConfig {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Codec<FlatChunkGeneratorConfig> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						RegistryCodecs.entryList(RegistryKeys.STRUCTURE_SET).optionalFieldOf("structure_overrides").forGetter(config -> config.structureOverrides),
						FlatChunkGeneratorLayer.CODEC.listOf().fieldOf("layers").forGetter(FlatChunkGeneratorConfig::getLayers),
						Codec.BOOL.fieldOf("lakes").orElse(false).forGetter(config -> config.hasLakes),
						Codec.BOOL.fieldOf("features").orElse(false).forGetter(config -> config.hasFeatures),
						Biome.REGISTRY_CODEC.optionalFieldOf("biome").orElseGet(Optional::empty).forGetter(config -> Optional.of(config.biome)),
						RegistryOps.getEntryCodec(BiomeKeys.PLAINS),
						RegistryOps.getEntryCodec(MiscPlacedFeatures.LAKE_LAVA_UNDERGROUND),
						RegistryOps.getEntryCodec(MiscPlacedFeatures.LAKE_LAVA_SURFACE)
					)
					.apply(instance, FlatChunkGeneratorConfig::new)
		)
		.<FlatChunkGeneratorConfig>comapFlatMap(FlatChunkGeneratorConfig::checkHeight, Function.identity())
		.stable();
	private final Optional<RegistryEntryList<StructureSet>> structureOverrides;
	private final List<FlatChunkGeneratorLayer> layers = Lists.<FlatChunkGeneratorLayer>newArrayList();
	private final RegistryEntry<Biome> biome;
	private final List<BlockState> layerBlocks;
	private boolean hasNoTerrain;
	private boolean hasFeatures;
	private boolean hasLakes;
	private final List<RegistryEntry<PlacedFeature>> features;

	private static DataResult<FlatChunkGeneratorConfig> checkHeight(FlatChunkGeneratorConfig config) {
		int i = config.layers.stream().mapToInt(FlatChunkGeneratorLayer::getThickness).sum();
		return i > DimensionType.MAX_HEIGHT ? DataResult.error(() -> "Sum of layer heights is > " + DimensionType.MAX_HEIGHT, config) : DataResult.success(config);
	}

	private FlatChunkGeneratorConfig(
		Optional<RegistryEntryList<StructureSet>> structureOverrides,
		List<FlatChunkGeneratorLayer> layers,
		boolean lakes,
		boolean features,
		Optional<RegistryEntry<Biome>> biome,
		RegistryEntry.Reference<Biome> fallback,
		RegistryEntry<PlacedFeature> undergroundLavaLakeFeature,
		RegistryEntry<PlacedFeature> surfaceLavaLakeFeature
	) {
		this(structureOverrides, getBiome(biome, fallback), List.of(undergroundLavaLakeFeature, surfaceLavaLakeFeature));
		if (lakes) {
			this.enableLakes();
		}

		if (features) {
			this.enableFeatures();
		}

		this.layers.addAll(layers);
		this.updateLayerBlocks();
	}

	private static RegistryEntry<Biome> getBiome(Optional<? extends RegistryEntry<Biome>> biome, RegistryEntry<Biome> fallback) {
		if (biome.isEmpty()) {
			LOGGER.error("Unknown biome, defaulting to plains");
			return fallback;
		} else {
			return (RegistryEntry<Biome>)biome.get();
		}
	}

	public FlatChunkGeneratorConfig(
		Optional<RegistryEntryList<StructureSet>> structureOverrides, RegistryEntry<Biome> biome, List<RegistryEntry<PlacedFeature>> features
	) {
		this.structureOverrides = structureOverrides;
		this.biome = biome;
		this.layerBlocks = Lists.<BlockState>newArrayList();
		this.features = features;
	}

	public FlatChunkGeneratorConfig with(
		List<FlatChunkGeneratorLayer> layers, Optional<RegistryEntryList<StructureSet>> structureOverrides, RegistryEntry<Biome> biome
	) {
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(structureOverrides, biome, this.features);

		for (FlatChunkGeneratorLayer flatChunkGeneratorLayer : layers) {
			flatChunkGeneratorConfig.layers.add(new FlatChunkGeneratorLayer(flatChunkGeneratorLayer.getThickness(), flatChunkGeneratorLayer.getBlockState().getBlock()));
			flatChunkGeneratorConfig.updateLayerBlocks();
		}

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

	public GenerationSettings createGenerationSettings(RegistryEntry<Biome> biomeEntry) {
		if (!biomeEntry.equals(this.biome)) {
			return biomeEntry.value().getGenerationSettings();
		} else {
			GenerationSettings generationSettings = this.getBiome().value().getGenerationSettings();
			GenerationSettings.Builder builder = new GenerationSettings.Builder();
			if (this.hasLakes) {
				for (RegistryEntry<PlacedFeature> registryEntry : this.features) {
					builder.feature(GenerationStep.Feature.LAKES, registryEntry);
				}
			}

			boolean bl = (!this.hasNoTerrain || biomeEntry.matchesKey(BiomeKeys.THE_VOID)) && this.hasFeatures;
			if (bl) {
				List<RegistryEntryList<PlacedFeature>> list = generationSettings.getFeatures();

				for (int i = 0; i < list.size(); i++) {
					if (i != GenerationStep.Feature.UNDERGROUND_STRUCTURES.ordinal()
						&& i != GenerationStep.Feature.SURFACE_STRUCTURES.ordinal()
						&& (!this.hasLakes || i != GenerationStep.Feature.LAKES.ordinal())) {
						for (RegistryEntry<PlacedFeature> registryEntry2 : (RegistryEntryList)list.get(i)) {
							builder.addFeature(i, registryEntry2);
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

			return builder.build();
		}
	}

	public Optional<RegistryEntryList<StructureSet>> getStructureOverrides() {
		return this.structureOverrides;
	}

	public RegistryEntry<Biome> getBiome() {
		return this.biome;
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

	public static FlatChunkGeneratorConfig getDefaultConfig(
		RegistryEntryLookup<Biome> biomeLookup, RegistryEntryLookup<StructureSet> structureSetLookup, RegistryEntryLookup<PlacedFeature> featureLookup
	) {
		RegistryEntryList<StructureSet> registryEntryList = RegistryEntryList.of(
			structureSetLookup.getOrThrow(StructureSetKeys.STRONGHOLDS), structureSetLookup.getOrThrow(StructureSetKeys.VILLAGES)
		);
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(
			Optional.of(registryEntryList), getPlains(biomeLookup), getLavaLakes(featureLookup)
		);
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(2, Blocks.DIRT));
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK));
		flatChunkGeneratorConfig.updateLayerBlocks();
		return flatChunkGeneratorConfig;
	}

	public static RegistryEntry<Biome> getPlains(RegistryEntryLookup<Biome> biomeLookup) {
		return biomeLookup.getOrThrow(BiomeKeys.PLAINS);
	}

	public static List<RegistryEntry<PlacedFeature>> getLavaLakes(RegistryEntryLookup<PlacedFeature> featureLookup) {
		return List.of(featureLookup.getOrThrow(MiscPlacedFeatures.LAKE_LAVA_UNDERGROUND), featureLookup.getOrThrow(MiscPlacedFeatures.LAKE_LAVA_SURFACE));
	}
}
