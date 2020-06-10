package net.minecraft.world.gen.chunk;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.NumberCodecs;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FillLayerFeatureConfig;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatChunkGeneratorConfig {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Codec<FlatChunkGeneratorConfig> CODEC = RecordCodecBuilder.<FlatChunkGeneratorConfig>create(
			instance -> instance.group(
						StructuresConfig.CODEC.fieldOf("structures").forGetter(FlatChunkGeneratorConfig::getConfig),
						FlatChunkGeneratorLayer.CODEC.listOf().fieldOf("layers").forGetter(FlatChunkGeneratorConfig::getLayers),
						NumberCodecs.method_29905(Registry.BIOME.fieldOf("biome"), Util.method_29188("Unknown biome, defaulting to plains", LOGGER::error), () -> Biomes.PLAINS)
							.forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.biome)
					)
					.apply(instance, FlatChunkGeneratorConfig::new)
		)
		.stable();
	private static final ConfiguredFeature<?, ?> WATER_LAKE = Feature.LAKE
		.configure(new SingleStateFeatureConfig(Blocks.WATER.getDefaultState()))
		.createDecoratedFeature(Decorator.WATER_LAKE.configure(new ChanceDecoratorConfig(4)));
	private static final ConfiguredFeature<?, ?> LAVA_LAKE = Feature.LAKE
		.configure(new SingleStateFeatureConfig(Blocks.LAVA.getDefaultState()))
		.createDecoratedFeature(Decorator.LAVA_LAKE.configure(new ChanceDecoratorConfig(80)));
	private static final Map<StructureFeature<?>, ConfiguredStructureFeature<?, ?>> STRUCTURE_TO_FEATURES = Util.make(
		Maps.<StructureFeature<?>, ConfiguredStructureFeature<?, ?>>newHashMap(), hashMap -> {
			hashMap.put(StructureFeature.MINESHAFT, DefaultBiomeFeatures.NORMAL_MINESHAFT);
			hashMap.put(StructureFeature.VILLAGE, DefaultBiomeFeatures.PLAINS_VILLAGE);
			hashMap.put(StructureFeature.STRONGHOLD, DefaultBiomeFeatures.STRONGHOLD);
			hashMap.put(StructureFeature.SWAMP_HUT, DefaultBiomeFeatures.SWAMP_HUT);
			hashMap.put(StructureFeature.DESERT_PYRAMID, DefaultBiomeFeatures.DESERT_PYRAMID);
			hashMap.put(StructureFeature.JUNGLE_PYRAMID, DefaultBiomeFeatures.JUNGLE_PYRAMID);
			hashMap.put(StructureFeature.IGLOO, DefaultBiomeFeatures.IGLOO);
			hashMap.put(StructureFeature.OCEAN_RUIN, DefaultBiomeFeatures.COLD_OCEAN_RUIN);
			hashMap.put(StructureFeature.SHIPWRECK, DefaultBiomeFeatures.SUNKEN_SHIPWRECK);
			hashMap.put(StructureFeature.MONUMENT, DefaultBiomeFeatures.MONUMENT);
			hashMap.put(StructureFeature.END_CITY, DefaultBiomeFeatures.END_CITY);
			hashMap.put(StructureFeature.MANSION, DefaultBiomeFeatures.MANSION);
			hashMap.put(StructureFeature.FORTRESS, DefaultBiomeFeatures.FORTRESS);
			hashMap.put(StructureFeature.PILLAGER_OUTPOST, DefaultBiomeFeatures.PILLAGER_OUTPOST);
			hashMap.put(StructureFeature.RUINED_PORTAL, DefaultBiomeFeatures.STANDARD_RUINED_PORTAL);
			hashMap.put(StructureFeature.BASTION_REMNANT, DefaultBiomeFeatures.BASTION_REMNANT);
		}
	);
	private final StructuresConfig config;
	private final List<FlatChunkGeneratorLayer> layers = Lists.<FlatChunkGeneratorLayer>newArrayList();
	private Biome biome;
	private final BlockState[] layerBlocks = new BlockState[256];
	private boolean hasNoTerrain;
	private boolean field_24976 = false;
	private boolean field_24977 = false;

	public FlatChunkGeneratorConfig(StructuresConfig structuresConfig, List<FlatChunkGeneratorLayer> list, Biome biome) {
		this(structuresConfig);
		this.layers.addAll(list);
		this.updateLayerBlocks();
		this.biome = biome;
	}

	public FlatChunkGeneratorConfig(StructuresConfig config) {
		this.config = config;
	}

	@Environment(EnvType.CLIENT)
	public FlatChunkGeneratorConfig method_28912(StructuresConfig structuresConfig) {
		return this.method_29965(this.layers, structuresConfig);
	}

	@Environment(EnvType.CLIENT)
	public FlatChunkGeneratorConfig method_29965(List<FlatChunkGeneratorLayer> list, StructuresConfig structuresConfig) {
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(structuresConfig);

		for (FlatChunkGeneratorLayer flatChunkGeneratorLayer : list) {
			flatChunkGeneratorConfig.layers.add(new FlatChunkGeneratorLayer(flatChunkGeneratorLayer.getThickness(), flatChunkGeneratorLayer.getBlockState().getBlock()));
			flatChunkGeneratorConfig.updateLayerBlocks();
		}

		flatChunkGeneratorConfig.setBiome(this.biome);
		if (this.field_24976) {
			flatChunkGeneratorConfig.method_28911();
		}

		if (this.field_24977) {
			flatChunkGeneratorConfig.method_28916();
		}

		return flatChunkGeneratorConfig;
	}

	@Environment(EnvType.CLIENT)
	public void method_28911() {
		this.field_24976 = true;
	}

	@Environment(EnvType.CLIENT)
	public void method_28916() {
		this.field_24977 = true;
	}

	public Biome method_28917() {
		Biome biome = this.getBiome();
		Biome biome2 = new Biome(
			new Biome.Settings()
				.surfaceBuilder(biome.getSurfaceBuilder())
				.precipitation(biome.getPrecipitation())
				.category(biome.getCategory())
				.depth(biome.getDepth())
				.scale(biome.getScale())
				.temperature(biome.getTemperature())
				.downfall(biome.getRainfall())
				.effects(biome.getEffects())
				.parent(biome.getParent())
		) {
		};
		if (this.field_24977) {
			biome2.addFeature(GenerationStep.Feature.LAKES, WATER_LAKE);
			biome2.addFeature(GenerationStep.Feature.LAKES, LAVA_LAKE);
		}

		for (Entry<StructureFeature<?>, StructureConfig> entry : this.config.getStructures().entrySet()) {
			biome2.addStructureFeature(biome.method_28405((ConfiguredStructureFeature<?, ?>)STRUCTURE_TO_FEATURES.get(entry.getKey())));
		}

		boolean bl = (!this.hasNoTerrain || biome == Biomes.THE_VOID) && this.field_24976;
		if (bl) {
			List<GenerationStep.Feature> list = Lists.<GenerationStep.Feature>newArrayList();
			list.add(GenerationStep.Feature.UNDERGROUND_STRUCTURES);
			list.add(GenerationStep.Feature.SURFACE_STRUCTURES);

			for (GenerationStep.Feature feature : GenerationStep.Feature.values()) {
				if (!list.contains(feature)) {
					for (ConfiguredFeature<?, ?> configuredFeature : biome.getFeaturesForStep(feature)) {
						biome2.addFeature(feature, configuredFeature);
					}
				}
			}
		}

		BlockState[] blockStates = this.getLayerBlocks();

		for (int i = 0; i < blockStates.length; i++) {
			BlockState blockState = blockStates[i];
			if (blockState != null && !Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(blockState)) {
				this.layerBlocks[i] = null;
				biome2.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, Feature.FILL_LAYER.configure(new FillLayerFeatureConfig(i, blockState)));
			}
		}

		return biome2;
	}

	public StructuresConfig getConfig() {
		return this.config;
	}

	public Biome getBiome() {
		return this.biome;
	}

	public void setBiome(Biome biome) {
		this.biome = biome;
	}

	public List<FlatChunkGeneratorLayer> getLayers() {
		return this.layers;
	}

	public BlockState[] getLayerBlocks() {
		return this.layerBlocks;
	}

	public void updateLayerBlocks() {
		Arrays.fill(this.layerBlocks, 0, this.layerBlocks.length, null);
		int i = 0;

		for (FlatChunkGeneratorLayer flatChunkGeneratorLayer : this.layers) {
			flatChunkGeneratorLayer.setStartY(i);
			i += flatChunkGeneratorLayer.getThickness();
		}

		this.hasNoTerrain = true;

		for (FlatChunkGeneratorLayer flatChunkGeneratorLayer2 : this.layers) {
			for (int j = flatChunkGeneratorLayer2.getStartY(); j < flatChunkGeneratorLayer2.getStartY() + flatChunkGeneratorLayer2.getThickness(); j++) {
				BlockState blockState = flatChunkGeneratorLayer2.getBlockState();
				if (!blockState.isOf(Blocks.AIR)) {
					this.hasNoTerrain = false;
					this.layerBlocks[j] = blockState;
				}
			}
		}
	}

	public static FlatChunkGeneratorConfig getDefaultConfig() {
		StructuresConfig structuresConfig = new StructuresConfig(
			Optional.of(StructuresConfig.DEFAULT_STRONGHOLD),
			Maps.<StructureFeature<?>, StructureConfig>newHashMap(
				ImmutableMap.of(StructureFeature.VILLAGE, StructuresConfig.DEFAULT_STRUCTURES.get(StructureFeature.VILLAGE))
			)
		);
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(structuresConfig);
		flatChunkGeneratorConfig.setBiome(Biomes.PLAINS);
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(2, Blocks.DIRT));
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK));
		flatChunkGeneratorConfig.updateLayerBlocks();
		return flatChunkGeneratorConfig;
	}
}
