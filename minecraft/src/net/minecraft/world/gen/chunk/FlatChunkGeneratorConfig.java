package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5311;
import net.minecraft.class_5314;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
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
						class_5311.field_24821.fieldOf("structures").forGetter(FlatChunkGeneratorConfig::getConfig),
						FlatChunkGeneratorLayer.CODEC.listOf().fieldOf("layers").forGetter(FlatChunkGeneratorConfig::getLayers),
						Registry.BIOME.fieldOf("biome").withDefault((Supplier<? extends Biome>)(() -> {
							LOGGER.error("Unknown biome, defaulting to plains");
							return Biomes.PLAINS;
						})).forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.biome)
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
			hashMap.put(StructureFeature.MINESHAFT, DefaultBiomeFeatures.field_24688);
			hashMap.put(StructureFeature.VILLAGE, DefaultBiomeFeatures.field_24706);
			hashMap.put(StructureFeature.STRONGHOLD, DefaultBiomeFeatures.field_24697);
			hashMap.put(StructureFeature.field_24851, DefaultBiomeFeatures.field_24696);
			hashMap.put(StructureFeature.DESERT_PYRAMID, DefaultBiomeFeatures.field_24692);
			hashMap.put(StructureFeature.JUNGLE_PYRAMID, DefaultBiomeFeatures.field_24691);
			hashMap.put(StructureFeature.IGLOO, DefaultBiomeFeatures.field_24693);
			hashMap.put(StructureFeature.OCEAN_RUIN, DefaultBiomeFeatures.field_24699);
			hashMap.put(StructureFeature.SHIPWRECK, DefaultBiomeFeatures.field_24694);
			hashMap.put(StructureFeature.MONUMENT, DefaultBiomeFeatures.field_24698);
			hashMap.put(StructureFeature.END_CITY, DefaultBiomeFeatures.field_24703);
			hashMap.put(StructureFeature.MANSION, DefaultBiomeFeatures.field_24690);
			hashMap.put(StructureFeature.FORTRESS, DefaultBiomeFeatures.field_24701);
			hashMap.put(StructureFeature.PILLAGER_OUTPOST, DefaultBiomeFeatures.field_24687);
			hashMap.put(StructureFeature.RUINED_PORTAL, DefaultBiomeFeatures.field_24711);
			hashMap.put(StructureFeature.BASTION_REMNANT, DefaultBiomeFeatures.field_24705);
		}
	);
	private final class_5311 config;
	private final List<FlatChunkGeneratorLayer> layers = Lists.<FlatChunkGeneratorLayer>newArrayList();
	private Biome biome;
	private final BlockState[] layerBlocks = new BlockState[256];
	private boolean hasNoTerrain;
	private boolean field_24976 = false;
	private boolean field_24977 = false;

	public FlatChunkGeneratorConfig(class_5311 arg, List<FlatChunkGeneratorLayer> list, Biome biome) {
		this(arg);
		this.layers.addAll(list);
		this.updateLayerBlocks();
		this.biome = biome;
	}

	public FlatChunkGeneratorConfig(class_5311 config) {
		this.config = config;
	}

	@Environment(EnvType.CLIENT)
	public FlatChunkGeneratorConfig method_28912(class_5311 arg) {
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(arg);

		for (FlatChunkGeneratorLayer flatChunkGeneratorLayer : this.getLayers()) {
			flatChunkGeneratorConfig.getLayers()
				.add(new FlatChunkGeneratorLayer(flatChunkGeneratorLayer.getThickness(), flatChunkGeneratorLayer.getBlockState().getBlock()));
			flatChunkGeneratorConfig.updateLayerBlocks();
		}

		flatChunkGeneratorConfig.setBiome(this.biome);
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
			biome2.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, WATER_LAKE);
			biome2.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, LAVA_LAKE);
		}

		for (Entry<StructureFeature<?>, class_5314> entry : this.config.method_28598().entrySet()) {
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

	public class_5311 getConfig() {
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

	public void addStructure(StructureFeature<?> structureFeature) {
		this.config.method_28598().put(structureFeature, class_5311.field_24822.get(structureFeature));
	}

	public BlockState[] getLayerBlocks() {
		return this.layerBlocks;
	}

	public void updateLayerBlocks() {
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
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(
			new class_5311(Optional.of(class_5311.field_24823), Maps.<StructureFeature<?>, class_5314>newHashMap())
		);
		flatChunkGeneratorConfig.setBiome(Biomes.PLAINS);
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(2, Blocks.DIRT));
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK));
		flatChunkGeneratorConfig.updateLayerBlocks();
		flatChunkGeneratorConfig.addStructure(StructureFeature.VILLAGE);
		return flatChunkGeneratorConfig;
	}
}
