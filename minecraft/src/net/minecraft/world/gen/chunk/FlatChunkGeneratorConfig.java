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
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
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
	public static final Codec<FlatChunkGeneratorConfig> CODEC = RecordCodecBuilder.<FlatChunkGeneratorConfig>create(
			instance -> instance.group(
						StructuresConfig.CODEC.fieldOf("structures").forGetter(FlatChunkGeneratorConfig::getConfig),
						FlatChunkGeneratorLayer.CODEC.listOf().fieldOf("layers").forGetter(FlatChunkGeneratorConfig::getLayers),
						Codec.BOOL.fieldOf("lakes").orElse(false).forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.field_24977),
						Codec.BOOL.fieldOf("features").orElse(false).forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.field_24976),
						Biome.REGISTRY_CODEC
							.fieldOf("biome")
							.orElseGet(Util.method_29188("Unknown biome, defaulting to plains", LOGGER::error), () -> () -> Biomes.PLAINS)
							.forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.biome)
					)
					.apply(instance, FlatChunkGeneratorConfig::new)
		)
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
	private final StructuresConfig config;
	private final List<FlatChunkGeneratorLayer> layers = Lists.<FlatChunkGeneratorLayer>newArrayList();
	private Supplier<Biome> biome = () -> Biomes.PLAINS;
	private final BlockState[] layerBlocks = new BlockState[256];
	private boolean hasNoTerrain;
	private boolean field_24976 = false;
	private boolean field_24977 = false;

	public FlatChunkGeneratorConfig(StructuresConfig structuresConfig, List<FlatChunkGeneratorLayer> list, boolean bl, boolean bl2, Supplier<Biome> supplier) {
		this(structuresConfig);
		if (bl) {
			this.method_28916();
		}

		if (bl2) {
			this.method_28911();
		}

		this.layers.addAll(list);
		this.updateLayerBlocks();
		this.biome = supplier;
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

		flatChunkGeneratorConfig.setBiome((Biome)this.biome.get());
		if (this.field_24976) {
			flatChunkGeneratorConfig.method_28911();
		}

		if (this.field_24977) {
			flatChunkGeneratorConfig.method_28916();
		}

		return flatChunkGeneratorConfig;
	}

	public void method_28911() {
		this.field_24976 = true;
	}

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
				.downfall(biome.getDownfall())
				.effects(biome.getEffects())
				.parent(biome.getParent())
		);
		if (this.field_24977) {
			biome2.addFeature(GenerationStep.Feature.LAKES, ConfiguredFeatures.LAKE_WATER);
			biome2.addFeature(GenerationStep.Feature.LAKES, ConfiguredFeatures.LAKE_LAVA);
		}

		for (Entry<StructureFeature<?>, StructureConfig> entry : this.config.getStructures().entrySet()) {
			biome2.addStructureFeature(biome.method_28405((ConfiguredStructureFeature<?, ?>)STRUCTURE_TO_FEATURES.get(entry.getKey())));
		}

		boolean bl = (!this.hasNoTerrain || biome == Biomes.THE_VOID) && this.field_24976;
		if (bl) {
			List<List<Supplier<ConfiguredFeature<?, ?>>>> list = biome.getFeatures();

			for (int i = 0; i < list.size(); i++) {
				if (i != GenerationStep.Feature.UNDERGROUND_STRUCTURES.ordinal() && i != GenerationStep.Feature.SURFACE_STRUCTURES.ordinal()) {
					for (Supplier<ConfiguredFeature<?, ?>> supplier : (List)list.get(i)) {
						biome2.addFeature(i, supplier);
					}
				}
			}
		}

		BlockState[] blockStates = this.getLayerBlocks();

		for (int ix = 0; ix < blockStates.length; ix++) {
			BlockState blockState = blockStates[ix];
			if (blockState != null && !Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(blockState)) {
				this.layerBlocks[ix] = null;
				biome2.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, Feature.FILL_LAYER.configure(new FillLayerFeatureConfig(ix, blockState)));
			}
		}

		return biome2;
	}

	public StructuresConfig getConfig() {
		return this.config;
	}

	public Biome getBiome() {
		return (Biome)this.biome.get();
	}

	public void setBiome(Biome biome) {
		this.biome = () -> biome;
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
