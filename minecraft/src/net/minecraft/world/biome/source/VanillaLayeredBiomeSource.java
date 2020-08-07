package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltInBiomes;
import net.minecraft.world.biome.layer.BiomeLayers;

public class VanillaLayeredBiomeSource extends BiomeSource {
	public static final Codec<VanillaLayeredBiomeSource> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.LONG.fieldOf("seed").stable().forGetter(vanillaLayeredBiomeSource -> vanillaLayeredBiomeSource.seed),
					Codec.BOOL
						.optionalFieldOf("legacy_biome_init_layer", Boolean.valueOf(false), Lifecycle.stable())
						.forGetter(vanillaLayeredBiomeSource -> vanillaLayeredBiomeSource.legacyBiomeInitLayer),
					Codec.BOOL.fieldOf("large_biomes").orElse(false).stable().forGetter(vanillaLayeredBiomeSource -> vanillaLayeredBiomeSource.largeBiomes),
					RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(vanillaLayeredBiomeSource -> vanillaLayeredBiomeSource.biomeRegistry)
				)
				.apply(instance, instance.stable(VanillaLayeredBiomeSource::new))
	);
	private final BiomeLayerSampler biomeSampler;
	private static final List<RegistryKey<Biome>> BIOMES = ImmutableList.of(
		BuiltInBiomes.OCEAN,
		BuiltInBiomes.PLAINS,
		BuiltInBiomes.DESERT,
		BuiltInBiomes.MOUNTAINS,
		BuiltInBiomes.FOREST,
		BuiltInBiomes.TAIGA,
		BuiltInBiomes.SWAMP,
		BuiltInBiomes.RIVER,
		BuiltInBiomes.FROZEN_OCEAN,
		BuiltInBiomes.FROZEN_RIVER,
		BuiltInBiomes.SNOWY_TUNDRA,
		BuiltInBiomes.SNOWY_MOUNTAINS,
		BuiltInBiomes.MUSHROOM_FIELDS,
		BuiltInBiomes.MUSHROOM_FIELD_SHORE,
		BuiltInBiomes.BEACH,
		BuiltInBiomes.DESERT_HILLS,
		BuiltInBiomes.WOODED_HILLS,
		BuiltInBiomes.TAIGA_HILLS,
		BuiltInBiomes.MOUNTAIN_EDGE,
		BuiltInBiomes.JUNGLE,
		BuiltInBiomes.JUNGLE_HILLS,
		BuiltInBiomes.JUNGLE_EDGE,
		BuiltInBiomes.DEEP_OCEAN,
		BuiltInBiomes.STONE_SHORE,
		BuiltInBiomes.SNOWY_BEACH,
		BuiltInBiomes.BIRCH_FOREST,
		BuiltInBiomes.BIRCH_FOREST_HILLS,
		BuiltInBiomes.DARK_FOREST,
		BuiltInBiomes.SNOWY_TAIGA,
		BuiltInBiomes.SNOWY_TAIGA_HILLS,
		BuiltInBiomes.GIANT_TREE_TAIGA,
		BuiltInBiomes.GIANT_TREE_TAIGA_HILLS,
		BuiltInBiomes.WOODED_MOUNTAINS,
		BuiltInBiomes.SAVANNA,
		BuiltInBiomes.SAVANNA_PLATEAU,
		BuiltInBiomes.BADLANDS,
		BuiltInBiomes.WOODED_BADLANDS_PLATEAU,
		BuiltInBiomes.BADLANDS_PLATEAU,
		BuiltInBiomes.WARM_OCEAN,
		BuiltInBiomes.LUKEWARM_OCEAN,
		BuiltInBiomes.COLD_OCEAN,
		BuiltInBiomes.DEEP_WARM_OCEAN,
		BuiltInBiomes.DEEP_LUKEWARM_OCEAN,
		BuiltInBiomes.DEEP_COLD_OCEAN,
		BuiltInBiomes.DEEP_FROZEN_OCEAN,
		BuiltInBiomes.SUNFLOWER_PLAINS,
		BuiltInBiomes.DESERT_LAKES,
		BuiltInBiomes.GRAVELLY_MOUNTAINS,
		BuiltInBiomes.FLOWER_FOREST,
		BuiltInBiomes.TAIGA_MOUNTAINS,
		BuiltInBiomes.SWAMP_HILLS,
		BuiltInBiomes.ICE_SPIKES,
		BuiltInBiomes.MODIFIED_JUNGLE,
		BuiltInBiomes.MODIFIED_JUNGLE_EDGE,
		BuiltInBiomes.TALL_BIRCH_FOREST,
		BuiltInBiomes.TALL_BIRCH_HILLS,
		BuiltInBiomes.DARK_FOREST_HILLS,
		BuiltInBiomes.SNOWY_TAIGA_MOUNTAINS,
		BuiltInBiomes.GIANT_SPRUCE_TAIGA,
		BuiltInBiomes.GIANT_SPRUCE_TAIGA_HILLS,
		BuiltInBiomes.MODIFIED_GRAVELLY_MOUNTAINS,
		BuiltInBiomes.SHATTERED_SAVANNA,
		BuiltInBiomes.SHATTERED_SAVANNA_PLATEAU,
		BuiltInBiomes.ERODED_BADLANDS,
		BuiltInBiomes.MODIFIED_WOODED_BADLANDS_PLATEAU,
		BuiltInBiomes.MODIFIED_BADLANDS_PLATEAU
	);
	private final long seed;
	private final boolean legacyBiomeInitLayer;
	private final boolean largeBiomes;
	private final Registry<Biome> biomeRegistry;

	public VanillaLayeredBiomeSource(long seed, boolean legacyBiomeInitLayer, boolean largeBiomes, Registry<Biome> biomeRegistry) {
		super(BIOMES.stream().map(registryKey -> () -> biomeRegistry.method_31140(registryKey)));
		this.seed = seed;
		this.legacyBiomeInitLayer = legacyBiomeInitLayer;
		this.largeBiomes = largeBiomes;
		this.biomeRegistry = biomeRegistry;
		this.biomeSampler = BiomeLayers.build(seed, legacyBiomeInitLayer, largeBiomes ? 6 : 4, 4);
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() {
		return CODEC;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public BiomeSource withSeed(long seed) {
		return new VanillaLayeredBiomeSource(seed, this.legacyBiomeInitLayer, this.largeBiomes, this.biomeRegistry);
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return this.biomeSampler.sample(this.biomeRegistry, biomeX, biomeZ);
	}
}
