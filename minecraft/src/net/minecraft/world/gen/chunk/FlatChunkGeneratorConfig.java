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
import net.minecraft.class_5504;
import net.minecraft.class_5505;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.GenerationSettings;
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
						class_5505.method_31148(Registry.BIOME_KEY).forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.field_26748),
						StructuresConfig.CODEC.fieldOf("structures").forGetter(FlatChunkGeneratorConfig::getConfig),
						FlatChunkGeneratorLayer.CODEC.listOf().fieldOf("layers").forGetter(FlatChunkGeneratorConfig::getLayers),
						Codec.BOOL.fieldOf("lakes").orElse(false).forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.field_24977),
						Codec.BOOL.fieldOf("features").orElse(false).forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.field_24976),
						Biome.REGISTRY_CODEC
							.fieldOf("biome")
							.orElseGet(Util.method_29188("Unknown biome, defaulting to plains", LOGGER::error), () -> () -> class_5504.field_26734)
							.forGetter(flatChunkGeneratorConfig -> flatChunkGeneratorConfig.biome)
					)
					.apply(instance, FlatChunkGeneratorConfig::new)
		)
		.stable();
	private static final Map<StructureFeature<?>, ConfiguredStructureFeature<?, ?>> STRUCTURE_TO_FEATURES = Util.make(
		Maps.<StructureFeature<?>, ConfiguredStructureFeature<?, ?>>newHashMap(), hashMap -> {
			hashMap.put(StructureFeature.field_24844, ConfiguredStructureFeatures.field_26293);
			hashMap.put(StructureFeature.field_24858, ConfiguredStructureFeatures.field_26311);
			hashMap.put(StructureFeature.field_24852, ConfiguredStructureFeatures.field_26302);
			hashMap.put(StructureFeature.SWAMP_HUT, ConfiguredStructureFeatures.field_26301);
			hashMap.put(StructureFeature.field_24847, ConfiguredStructureFeatures.field_26297);
			hashMap.put(StructureFeature.field_24846, ConfiguredStructureFeatures.field_26296);
			hashMap.put(StructureFeature.field_24848, ConfiguredStructureFeatures.field_26298);
			hashMap.put(StructureFeature.field_24854, ConfiguredStructureFeatures.field_26304);
			hashMap.put(StructureFeature.field_24850, ConfiguredStructureFeatures.field_26299);
			hashMap.put(StructureFeature.field_24853, ConfiguredStructureFeatures.field_26303);
			hashMap.put(StructureFeature.field_24856, ConfiguredStructureFeatures.field_26308);
			hashMap.put(StructureFeature.field_24845, ConfiguredStructureFeatures.field_26295);
			hashMap.put(StructureFeature.field_24855, ConfiguredStructureFeatures.field_26306);
			hashMap.put(StructureFeature.field_24843, ConfiguredStructureFeatures.field_26292);
			hashMap.put(StructureFeature.field_24849, ConfiguredStructureFeatures.field_26316);
			hashMap.put(StructureFeature.field_24860, ConfiguredStructureFeatures.field_26310);
		}
	);
	private final Registry<Biome> field_26748;
	private final StructuresConfig config;
	private final List<FlatChunkGeneratorLayer> layers = Lists.<FlatChunkGeneratorLayer>newArrayList();
	private Supplier<Biome> biome = () -> class_5504.field_26734;
	private final BlockState[] layerBlocks = new BlockState[256];
	private boolean hasNoTerrain;
	private boolean field_24976 = false;
	private boolean field_24977 = false;

	public FlatChunkGeneratorConfig(
		Registry<Biome> registry, StructuresConfig structuresConfig, List<FlatChunkGeneratorLayer> list, boolean bl, boolean bl2, Supplier<Biome> supplier
	) {
		this(structuresConfig, registry);
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

	public FlatChunkGeneratorConfig(StructuresConfig structuresConfig, Registry<Biome> registry) {
		this.field_26748 = registry;
		this.config = structuresConfig;
	}

	@Environment(EnvType.CLIENT)
	public FlatChunkGeneratorConfig method_28912(StructuresConfig structuresConfig) {
		return this.method_29965(this.layers, structuresConfig);
	}

	@Environment(EnvType.CLIENT)
	public FlatChunkGeneratorConfig method_29965(List<FlatChunkGeneratorLayer> list, StructuresConfig structuresConfig) {
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(structuresConfig, this.field_26748);

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
		GenerationSettings generationSettings = biome.getGenerationSettings();
		GenerationSettings.Builder builder = new GenerationSettings.Builder().surfaceBuilder(generationSettings.getSurfaceBuilder());
		if (this.field_24977) {
			builder.feature(GenerationStep.Feature.field_25186, ConfiguredFeatures.field_25964);
			builder.feature(GenerationStep.Feature.field_25186, ConfiguredFeatures.field_25965);
		}

		for (Entry<StructureFeature<?>, StructureConfig> entry : this.config.getStructures().entrySet()) {
			builder.structureFeature(generationSettings.method_30978((ConfiguredStructureFeature<?, ?>)STRUCTURE_TO_FEATURES.get(entry.getKey())));
		}

		boolean bl = (!this.hasNoTerrain || this.field_26748.getKey(biome).equals(Optional.of(Biomes.field_9473))) && this.field_24976;
		if (bl) {
			List<List<Supplier<ConfiguredFeature<?, ?>>>> list = generationSettings.getFeatures();

			for (int i = 0; i < list.size(); i++) {
				if (i != GenerationStep.Feature.field_13172.ordinal() && i != GenerationStep.Feature.field_13173.ordinal()) {
					for (Supplier<ConfiguredFeature<?, ?>> supplier : (List)list.get(i)) {
						builder.feature(i, supplier);
					}
				}
			}
		}

		BlockState[] blockStates = this.getLayerBlocks();

		for (int ix = 0; ix < blockStates.length; ix++) {
			BlockState blockState = blockStates[ix];
			if (blockState != null && !Heightmap.Type.field_13197.getBlockPredicate().test(blockState)) {
				this.layerBlocks[ix] = null;
				builder.feature(GenerationStep.Feature.field_13179, Feature.field_19201.configure(new FillLayerFeatureConfig(ix, blockState)));
			}
		}

		return new Biome.Builder()
			.precipitation(biome.getPrecipitation())
			.category(biome.getCategory())
			.depth(biome.getDepth())
			.scale(biome.getScale())
			.temperature(biome.getTemperature())
			.downfall(biome.getDownfall())
			.effects(biome.getEffects())
			.generationSettings(builder.build())
			.spawnSettings(biome.getSpawnSettings())
			.build();
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
				if (!blockState.isOf(Blocks.field_10124)) {
					this.hasNoTerrain = false;
					this.layerBlocks[j] = blockState;
				}
			}
		}
	}

	public static FlatChunkGeneratorConfig getDefaultConfig(Registry<Biome> registry) {
		StructuresConfig structuresConfig = new StructuresConfig(
			Optional.of(StructuresConfig.DEFAULT_STRONGHOLD),
			Maps.<StructureFeature<?>, StructureConfig>newHashMap(
				ImmutableMap.of(StructureFeature.field_24858, StructuresConfig.DEFAULT_STRUCTURES.get(StructureFeature.field_24858))
			)
		);
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(structuresConfig, registry);
		flatChunkGeneratorConfig.setBiome(class_5504.field_26734);
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.field_9987));
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(2, Blocks.field_10566));
		flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.field_10219));
		flatChunkGeneratorConfig.updateLayerBlocks();
		return flatChunkGeneratorConfig;
	}
}
