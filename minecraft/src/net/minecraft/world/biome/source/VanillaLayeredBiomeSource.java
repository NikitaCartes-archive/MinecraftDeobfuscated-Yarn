package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.BiomeLayers;

public class VanillaLayeredBiomeSource extends BiomeSource {
	public static final Codec<VanillaLayeredBiomeSource> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.LONG.fieldOf("seed").stable().forGetter(vanillaLayeredBiomeSource -> vanillaLayeredBiomeSource.field_24728),
					Codec.BOOL
						.optionalFieldOf("legacy_biome_init_layer", Boolean.valueOf(false), Lifecycle.stable())
						.forGetter(vanillaLayeredBiomeSource -> vanillaLayeredBiomeSource.field_24498),
					Codec.BOOL.fieldOf("large_biomes").withDefault(false).stable().forGetter(vanillaLayeredBiomeSource -> vanillaLayeredBiomeSource.field_24729)
				)
				.apply(instance, instance.stable(VanillaLayeredBiomeSource::new))
	);
	private final BiomeLayerSampler biomeSampler;
	private static final List<Biome> BIOMES = ImmutableList.of(
		Biomes.OCEAN,
		Biomes.PLAINS,
		Biomes.DESERT,
		Biomes.MOUNTAINS,
		Biomes.FOREST,
		Biomes.TAIGA,
		Biomes.SWAMP,
		Biomes.RIVER,
		Biomes.FROZEN_OCEAN,
		Biomes.FROZEN_RIVER,
		Biomes.SNOWY_TUNDRA,
		Biomes.SNOWY_MOUNTAINS,
		Biomes.MUSHROOM_FIELDS,
		Biomes.MUSHROOM_FIELD_SHORE,
		Biomes.BEACH,
		Biomes.DESERT_HILLS,
		Biomes.WOODED_HILLS,
		Biomes.TAIGA_HILLS,
		Biomes.MOUNTAIN_EDGE,
		Biomes.JUNGLE,
		Biomes.JUNGLE_HILLS,
		Biomes.JUNGLE_EDGE,
		Biomes.DEEP_OCEAN,
		Biomes.STONE_SHORE,
		Biomes.SNOWY_BEACH,
		Biomes.BIRCH_FOREST,
		Biomes.BIRCH_FOREST_HILLS,
		Biomes.DARK_FOREST,
		Biomes.SNOWY_TAIGA,
		Biomes.SNOWY_TAIGA_HILLS,
		Biomes.GIANT_TREE_TAIGA,
		Biomes.GIANT_TREE_TAIGA_HILLS,
		Biomes.WOODED_MOUNTAINS,
		Biomes.SAVANNA,
		Biomes.SAVANNA_PLATEAU,
		Biomes.BADLANDS,
		Biomes.WOODED_BADLANDS_PLATEAU,
		Biomes.BADLANDS_PLATEAU,
		Biomes.WARM_OCEAN,
		Biomes.LUKEWARM_OCEAN,
		Biomes.COLD_OCEAN,
		Biomes.DEEP_WARM_OCEAN,
		Biomes.DEEP_LUKEWARM_OCEAN,
		Biomes.DEEP_COLD_OCEAN,
		Biomes.DEEP_FROZEN_OCEAN,
		Biomes.SUNFLOWER_PLAINS,
		Biomes.DESERT_LAKES,
		Biomes.GRAVELLY_MOUNTAINS,
		Biomes.FLOWER_FOREST,
		Biomes.TAIGA_MOUNTAINS,
		Biomes.SWAMP_HILLS,
		Biomes.ICE_SPIKES,
		Biomes.MODIFIED_JUNGLE,
		Biomes.MODIFIED_JUNGLE_EDGE,
		Biomes.TALL_BIRCH_FOREST,
		Biomes.TALL_BIRCH_HILLS,
		Biomes.DARK_FOREST_HILLS,
		Biomes.SNOWY_TAIGA_MOUNTAINS,
		Biomes.GIANT_SPRUCE_TAIGA,
		Biomes.GIANT_SPRUCE_TAIGA_HILLS,
		Biomes.MODIFIED_GRAVELLY_MOUNTAINS,
		Biomes.SHATTERED_SAVANNA,
		Biomes.SHATTERED_SAVANNA_PLATEAU,
		Biomes.ERODED_BADLANDS,
		Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU,
		Biomes.MODIFIED_BADLANDS_PLATEAU
	);
	private final long field_24728;
	private final boolean field_24498;
	private final boolean field_24729;

	public VanillaLayeredBiomeSource(long l, boolean bl, boolean bl2) {
		super(BIOMES);
		this.field_24728 = l;
		this.field_24498 = bl;
		this.field_24729 = bl2;
		this.biomeSampler = BiomeLayers.build(l, bl, bl2 ? 6 : 4, 4);
	}

	@Override
	protected Codec<? extends BiomeSource> method_28442() {
		return CODEC;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public BiomeSource withSeed(long seed) {
		return new VanillaLayeredBiomeSource(seed, this.field_24498, this.field_24729);
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return this.biomeSampler.sample(biomeX, biomeZ);
	}
}
