package net.minecraft.world.biome.source.util;

import com.mojang.datafixers.util.Pair;
import java.util.function.Consumer;
import net.minecraft.SharedConstants;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public final class VanillaBiomeParameters {
	private static final float field_34500 = 0.05F;
	private static final float field_34501 = 0.1F;
	private static final float field_34502 = 0.56666666F;
	private static final float field_34503 = 0.7666667F;
	private final MultiNoiseUtil.ParameterRange DEFAULT_PARAMETER = MultiNoiseUtil.ParameterRange.method_38121(-1.0F, 1.0F);
	private final MultiNoiseUtil.ParameterRange[] TEMPERATURE_PARAMETERS = new MultiNoiseUtil.ParameterRange[]{
		MultiNoiseUtil.ParameterRange.method_38121(-1.0F, -0.45F),
		MultiNoiseUtil.ParameterRange.method_38121(-0.45F, -0.15F),
		MultiNoiseUtil.ParameterRange.method_38121(-0.15F, 0.2F),
		MultiNoiseUtil.ParameterRange.method_38121(0.2F, 0.55F),
		MultiNoiseUtil.ParameterRange.method_38121(0.55F, 1.0F)
	};
	private final MultiNoiseUtil.ParameterRange[] HUMIDITY_PARAMETERS = new MultiNoiseUtil.ParameterRange[]{
		MultiNoiseUtil.ParameterRange.method_38121(-1.0F, -0.3F),
		MultiNoiseUtil.ParameterRange.method_38121(-0.3F, -0.1F),
		MultiNoiseUtil.ParameterRange.method_38121(-0.1F, 0.1F),
		MultiNoiseUtil.ParameterRange.method_38121(0.1F, 0.3F),
		MultiNoiseUtil.ParameterRange.method_38121(0.3F, 1.0F)
	};
	private final MultiNoiseUtil.ParameterRange[] EROSION_PARAMETERS = new MultiNoiseUtil.ParameterRange[]{
		MultiNoiseUtil.ParameterRange.method_38121(-1.0F, -0.78F),
		MultiNoiseUtil.ParameterRange.method_38121(-0.78F, -0.375F),
		MultiNoiseUtil.ParameterRange.method_38121(-0.375F, -0.2225F),
		MultiNoiseUtil.ParameterRange.method_38121(-0.2225F, 0.05F),
		MultiNoiseUtil.ParameterRange.method_38121(0.05F, 0.45F),
		MultiNoiseUtil.ParameterRange.method_38121(0.45F, 0.55F),
		MultiNoiseUtil.ParameterRange.method_38121(0.55F, 1.0F)
	};
	private final MultiNoiseUtil.ParameterRange FROZEN_TEMPERATURE = this.TEMPERATURE_PARAMETERS[0];
	private final MultiNoiseUtil.ParameterRange NON_FROZEN_TEMPERATURE_PARAMETERS = MultiNoiseUtil.ParameterRange.method_38123(
		this.TEMPERATURE_PARAMETERS[1], this.TEMPERATURE_PARAMETERS[4]
	);
	private final MultiNoiseUtil.ParameterRange MUSHROOM_FIELDS_CONTINENTALNESS = MultiNoiseUtil.ParameterRange.method_38121(-1.2F, -1.05F);
	private final MultiNoiseUtil.ParameterRange DEEP_OCEAN_CONTINENTALNESS = MultiNoiseUtil.ParameterRange.method_38121(-1.05F, -0.455F);
	private final MultiNoiseUtil.ParameterRange OCEAN_CONTINENTALNESS = MultiNoiseUtil.ParameterRange.method_38121(-0.455F, -0.19F);
	private final MultiNoiseUtil.ParameterRange SHORE_CONTINENTALNESS = MultiNoiseUtil.ParameterRange.method_38121(-0.19F, -0.11F);
	private final MultiNoiseUtil.ParameterRange RIVER_CONTINENTALNESS = MultiNoiseUtil.ParameterRange.method_38121(-0.11F, 0.55F);
	private final MultiNoiseUtil.ParameterRange NEAR_INLAND_CONTINENTALNESS = MultiNoiseUtil.ParameterRange.method_38121(-0.11F, 0.03F);
	private final MultiNoiseUtil.ParameterRange MID_INLAND_CONTINENTALNESS = MultiNoiseUtil.ParameterRange.method_38121(0.03F, 0.3F);
	private final MultiNoiseUtil.ParameterRange FAR_INLAND_CONTINENTALNESS = MultiNoiseUtil.ParameterRange.method_38121(0.3F, 1.0F);
	private final RegistryKey<Biome>[][] OCEAN_BIOMES = new RegistryKey[][]{
		{BiomeKeys.DEEP_FROZEN_OCEAN, BiomeKeys.DEEP_COLD_OCEAN, BiomeKeys.DEEP_OCEAN, BiomeKeys.DEEP_LUKEWARM_OCEAN, BiomeKeys.DEEP_WARM_OCEAN},
		{BiomeKeys.FROZEN_OCEAN, BiomeKeys.COLD_OCEAN, BiomeKeys.OCEAN, BiomeKeys.LUKEWARM_OCEAN, BiomeKeys.WARM_OCEAN}
	};
	private final RegistryKey<Biome>[][] COMMON_BIOMES = new RegistryKey[][]{
		{BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TAIGA, BiomeKeys.SNOWY_TAIGA},
		{BiomeKeys.PLAINS, BiomeKeys.PLAINS, BiomeKeys.FOREST, BiomeKeys.TAIGA, BiomeKeys.GIANT_SPRUCE_TAIGA},
		{BiomeKeys.PLAINS, BiomeKeys.PLAINS, BiomeKeys.FOREST, BiomeKeys.BIRCH_FOREST, BiomeKeys.DARK_FOREST},
		{BiomeKeys.SAVANNA, BiomeKeys.SAVANNA, BiomeKeys.FOREST, BiomeKeys.JUNGLE, BiomeKeys.JUNGLE},
		{BiomeKeys.DESERT, BiomeKeys.DESERT, BiomeKeys.DESERT, BiomeKeys.JUNGLE_EDGE, BiomeKeys.JUNGLE}
	};
	private final RegistryKey<Biome>[][] UNCOMMON_BIOMES = new RegistryKey[][]{
		{BiomeKeys.ICE_SPIKES, null, BiomeKeys.SNOWY_TAIGA, null, null},
		{null, null, null, null, BiomeKeys.GIANT_TREE_TAIGA},
		{null, BiomeKeys.SUNFLOWER_PLAINS, BiomeKeys.FLOWER_FOREST, BiomeKeys.TALL_BIRCH_FOREST, null},
		{null, null, BiomeKeys.PLAINS, BiomeKeys.PLAINS, null},
		{null, null, null, BiomeKeys.PLAINS, BiomeKeys.BAMBOO_JUNGLE}
	};
	private final RegistryKey<Biome>[][] NEAR_MOUNTAIN_BIOMES = new RegistryKey[][]{
		{BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TAIGA, BiomeKeys.SNOWY_TAIGA},
		{BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.FOREST, BiomeKeys.TAIGA, BiomeKeys.GIANT_SPRUCE_TAIGA},
		{BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.DARK_FOREST},
		{BiomeKeys.SAVANNA_PLATEAU, BiomeKeys.SAVANNA_PLATEAU, BiomeKeys.FOREST, BiomeKeys.FOREST, BiomeKeys.JUNGLE},
		{BiomeKeys.BADLANDS, BiomeKeys.BADLANDS, BiomeKeys.BADLANDS, BiomeKeys.WOODED_BADLANDS_PLATEAU, BiomeKeys.WOODED_BADLANDS_PLATEAU}
	};
	private final RegistryKey<Biome>[][] SPECIAL_NEAR_MOUNTAIN_BIOMES = new RegistryKey[][]{
		{BiomeKeys.ICE_SPIKES, null, null, null, null},
		{null, null, BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.GIANT_TREE_TAIGA},
		{null, null, BiomeKeys.FOREST, BiomeKeys.BIRCH_FOREST, null},
		{null, null, null, null, null},
		{BiomeKeys.ERODED_BADLANDS, BiomeKeys.ERODED_BADLANDS, null, null, null}
	};
	private final RegistryKey<Biome>[][] HILL_BIOMES = new RegistryKey[][]{
		{BiomeKeys.GRAVELLY_MOUNTAINS, BiomeKeys.GRAVELLY_MOUNTAINS, BiomeKeys.MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS},
		{BiomeKeys.GRAVELLY_MOUNTAINS, BiomeKeys.GRAVELLY_MOUNTAINS, BiomeKeys.MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS},
		{BiomeKeys.MOUNTAINS, BiomeKeys.MOUNTAINS, BiomeKeys.MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS},
		{null, null, null, null, null},
		{null, null, null, null, null}
	};

	public void writeVanillaBiomeParameters(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer) {
		if (SharedConstants.DEBUG_BIOME_SOURCE) {
			new VanillaTerrainParameters().method_38215(consumer);
		} else {
			this.writeOceanBiomes(consumer);
			this.writeLandBiomes(consumer);
			this.writeCaveBiomes(consumer);
		}
	}

	private void writeOceanBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer) {
		this.writeBiomeParameters(
			consumer,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			this.MUSHROOM_FIELDS_CONTINENTALNESS,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.MUSHROOM_FIELDS
		);

		for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; i++) {
			MultiNoiseUtil.ParameterRange parameterRange = this.TEMPERATURE_PARAMETERS[i];
			this.writeBiomeParameters(
				consumer,
				parameterRange,
				this.DEFAULT_PARAMETER,
				this.DEEP_OCEAN_CONTINENTALNESS,
				this.DEFAULT_PARAMETER,
				this.DEFAULT_PARAMETER,
				0.0F,
				this.OCEAN_BIOMES[0][i]
			);
			this.writeBiomeParameters(
				consumer, parameterRange, this.DEFAULT_PARAMETER, this.OCEAN_CONTINENTALNESS, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, 0.0F, this.OCEAN_BIOMES[1][i]
			);
		}
	}

	private void writeLandBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer) {
		this.writeMixedBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(-1.0F, -0.93333334F));
		this.writePlainBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(-0.93333334F, -0.7666667F));
		this.writeMountainousBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(-0.7666667F, -0.56666666F));
		this.writePlainBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(-0.56666666F, -0.4F));
		this.writeMixedBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(-0.4F, -0.26666668F));
		this.writeBiomesNearRivers(consumer, MultiNoiseUtil.ParameterRange.method_38121(-0.26666668F, -0.05F));
		this.writeRiverBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(-0.05F, 0.05F));
		this.writeBiomesNearRivers(consumer, MultiNoiseUtil.ParameterRange.method_38121(0.05F, 0.26666668F));
		this.writeMixedBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(0.26666668F, 0.4F));
		this.writePlainBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(0.4F, 0.56666666F));
		this.writeMountainousBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(0.56666666F, 0.7666667F));
		this.writePlainBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(0.7666667F, 0.93333334F));
		this.writeMixedBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(0.93333334F, 1.0F));
	}

	private void writeMountainousBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer, MultiNoiseUtil.ParameterRange weirdness) {
		for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; i++) {
			MultiNoiseUtil.ParameterRange parameterRange = this.TEMPERATURE_PARAMETERS[i];

			for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; j++) {
				MultiNoiseUtil.ParameterRange parameterRange2 = this.HUMIDITY_PARAMETERS[j];
				RegistryKey<Biome> registryKey = this.getRegularBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey2 = this.getBadlandsOrRegularBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey3 = this.method_38194(i, j, weirdness);
				RegistryKey<Biome> registryKey4 = this.getNearMountainBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey5 = this.getHillBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey6 = this.method_38183(i, j, weirdness, registryKey5);
				RegistryKey<Biome> registryKey7 = this.getPeakBiome(i, j, weirdness);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[0],
					weirdness,
					0.0F,
					registryKey7
				);
				this.writeBiomeParameters(
					consumer, parameterRange, parameterRange2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[1], weirdness, 0.0F, registryKey3
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[1],
					weirdness,
					0.0F,
					registryKey7
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					this.NEAR_INLAND_CONTINENTALNESS,
					MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[3]),
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[2],
					weirdness,
					0.0F,
					registryKey4
				);
				this.writeBiomeParameters(
					consumer, parameterRange, parameterRange2, this.MID_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[3], weirdness, 0.0F, registryKey2
				);
				this.writeBiomeParameters(
					consumer, parameterRange, parameterRange2, this.FAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[3], weirdness, 0.0F, registryKey4
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[4],
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.NEAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[5],
					weirdness,
					0.0F,
					registryKey6
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[5],
					weirdness,
					0.0F,
					registryKey5
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[6],
					weirdness,
					0.0F,
					registryKey
				);
			}
		}
	}

	private void writePlainBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer, MultiNoiseUtil.ParameterRange weirdness) {
		for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; i++) {
			MultiNoiseUtil.ParameterRange parameterRange = this.TEMPERATURE_PARAMETERS[i];

			for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; j++) {
				MultiNoiseUtil.ParameterRange parameterRange2 = this.HUMIDITY_PARAMETERS[j];
				RegistryKey<Biome> registryKey = this.getRegularBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey2 = this.getBadlandsOrRegularBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey3 = this.method_38194(i, j, weirdness);
				RegistryKey<Biome> registryKey4 = this.getNearMountainBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey5 = this.getHillBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey6 = this.method_38183(i, j, weirdness, registryKey);
				RegistryKey<Biome> registryKey7 = this.getMountainSlopeBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey8 = this.getPeakBiome(i, j, weirdness);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					this.SHORE_CONTINENTALNESS,
					MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]),
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					consumer, parameterRange, parameterRange2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[0], weirdness, 0.0F, registryKey7
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[0],
					weirdness,
					0.0F,
					registryKey8
				);
				this.writeBiomeParameters(
					consumer, parameterRange, parameterRange2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[1], weirdness, 0.0F, registryKey3
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[1],
					weirdness,
					0.0F,
					registryKey7
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.NEAR_INLAND_CONTINENTALNESS),
					MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[3]),
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[2],
					weirdness,
					0.0F,
					registryKey4
				);
				this.writeBiomeParameters(
					consumer, parameterRange, parameterRange2, this.MID_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[3], weirdness, 0.0F, registryKey2
				);
				this.writeBiomeParameters(
					consumer, parameterRange, parameterRange2, this.FAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[3], weirdness, 0.0F, registryKey4
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[4],
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.NEAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[5],
					weirdness,
					0.0F,
					registryKey6
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[5],
					weirdness,
					0.0F,
					registryKey5
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[6],
					weirdness,
					0.0F,
					registryKey
				);
			}
		}
	}

	private void writeMixedBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer, MultiNoiseUtil.ParameterRange weirdness) {
		this.writeBiomeParameters(
			consumer,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			this.SHORE_CONTINENTALNESS,
			MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[2]),
			weirdness,
			0.0F,
			BiomeKeys.STONE_SHORE
		);
		this.writeBiomeParameters(
			consumer,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.ParameterRange.method_38123(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
			this.EROSION_PARAMETERS[6],
			weirdness,
			0.0F,
			BiomeKeys.SWAMP
		);

		for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; i++) {
			MultiNoiseUtil.ParameterRange parameterRange = this.TEMPERATURE_PARAMETERS[i];

			for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; j++) {
				MultiNoiseUtil.ParameterRange parameterRange2 = this.HUMIDITY_PARAMETERS[j];
				RegistryKey<Biome> registryKey = this.getRegularBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey2 = this.getBadlandsOrRegularBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey3 = this.method_38194(i, j, weirdness);
				RegistryKey<Biome> registryKey4 = this.getHillBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey5 = this.getNearMountainBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey6 = this.method_38181(i, j);
				RegistryKey<Biome> registryKey7 = this.method_38183(i, j, weirdness, registryKey);
				RegistryKey<Biome> registryKey8 = this.method_38198(i, j, weirdness);
				RegistryKey<Biome> registryKey9 = this.getMountainSlopeBiome(i, j, weirdness);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[0],
					weirdness,
					0.0F,
					registryKey9
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.NEAR_INLAND_CONTINENTALNESS, this.MID_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[1],
					weirdness,
					0.0F,
					registryKey3
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					this.FAR_INLAND_CONTINENTALNESS,
					this.EROSION_PARAMETERS[1],
					weirdness,
					0.0F,
					i == 0 ? registryKey9 : registryKey5
				);
				this.writeBiomeParameters(
					consumer, parameterRange, parameterRange2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[2], weirdness, 0.0F, registryKey
				);
				this.writeBiomeParameters(
					consumer, parameterRange, parameterRange2, this.MID_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[2], weirdness, 0.0F, registryKey2
				);
				this.writeBiomeParameters(
					consumer, parameterRange, parameterRange2, this.FAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[2], weirdness, 0.0F, registryKey5
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.NEAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[3],
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[3],
					weirdness,
					0.0F,
					registryKey2
				);
				if (weirdness.getMax() < 0.0F) {
					this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[4], weirdness, 0.0F, registryKey6);
					this.writeBiomeParameters(
						consumer,
						parameterRange,
						parameterRange2,
						MultiNoiseUtil.ParameterRange.method_38123(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
						this.EROSION_PARAMETERS[4],
						weirdness,
						0.0F,
						registryKey
					);
				} else {
					this.writeBiomeParameters(
						consumer,
						parameterRange,
						parameterRange2,
						MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
						this.EROSION_PARAMETERS[4],
						weirdness,
						0.0F,
						registryKey
					);
				}

				this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[5], weirdness, 0.0F, registryKey8);
				this.writeBiomeParameters(
					consumer, parameterRange, parameterRange2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[5], weirdness, 0.0F, registryKey7
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[5],
					weirdness,
					0.0F,
					registryKey4
				);
				if (weirdness.getMax() < 0.0F) {
					this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[6], weirdness, 0.0F, registryKey6);
				} else {
					this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[6], weirdness, 0.0F, registryKey);
				}

				if (i == 0) {
					this.writeBiomeParameters(
						consumer,
						parameterRange,
						parameterRange2,
						MultiNoiseUtil.ParameterRange.method_38123(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
						this.EROSION_PARAMETERS[6],
						weirdness,
						0.0F,
						registryKey
					);
				}
			}
		}
	}

	private void writeBiomesNearRivers(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer, MultiNoiseUtil.ParameterRange weirdness) {
		this.writeBiomeParameters(
			consumer,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			this.SHORE_CONTINENTALNESS,
			MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[2]),
			weirdness,
			0.0F,
			BiomeKeys.STONE_SHORE
		);
		this.writeBiomeParameters(
			consumer,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.ParameterRange.method_38123(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
			this.EROSION_PARAMETERS[6],
			weirdness,
			0.0F,
			BiomeKeys.SWAMP
		);

		for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; i++) {
			MultiNoiseUtil.ParameterRange parameterRange = this.TEMPERATURE_PARAMETERS[i];

			for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; j++) {
				MultiNoiseUtil.ParameterRange parameterRange2 = this.HUMIDITY_PARAMETERS[j];
				RegistryKey<Biome> registryKey = this.getRegularBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey2 = this.getBadlandsOrRegularBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey3 = this.method_38194(i, j, weirdness);
				RegistryKey<Biome> registryKey4 = this.method_38181(i, j);
				RegistryKey<Biome> registryKey5 = this.method_38183(i, j, weirdness, registryKey);
				RegistryKey<Biome> registryKey6 = this.method_38198(i, j, weirdness);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					this.NEAR_INLAND_CONTINENTALNESS,
					MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]),
					weirdness,
					0.0F,
					registryKey2
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]),
					weirdness,
					0.0F,
					registryKey3
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					this.NEAR_INLAND_CONTINENTALNESS,
					MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[3]),
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[3]),
					weirdness,
					0.0F,
					registryKey2
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					this.SHORE_CONTINENTALNESS,
					MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[3], this.EROSION_PARAMETERS[4]),
					weirdness,
					0.0F,
					registryKey4
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[4],
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[5], weirdness, 0.0F, registryKey6);
				this.writeBiomeParameters(
					consumer, parameterRange, parameterRange2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[5], weirdness, 0.0F, registryKey5
				);
				this.writeBiomeParameters(
					consumer,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[5],
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[6], weirdness, 0.0F, registryKey4);
				if (i == 0) {
					this.writeBiomeParameters(
						consumer,
						parameterRange,
						parameterRange2,
						MultiNoiseUtil.ParameterRange.method_38123(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
						this.EROSION_PARAMETERS[6],
						weirdness,
						0.0F,
						registryKey
					);
				}
			}
		}
	}

	private void writeRiverBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer, MultiNoiseUtil.ParameterRange parameterRange) {
		this.writeBiomeParameters(
			consumer,
			this.FROZEN_TEMPERATURE,
			this.DEFAULT_PARAMETER,
			this.SHORE_CONTINENTALNESS,
			MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]),
			parameterRange,
			0.0F,
			parameterRange.getMax() < 0.0F ? BiomeKeys.STONE_SHORE : BiomeKeys.FROZEN_RIVER
		);
		this.writeBiomeParameters(
			consumer,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			this.DEFAULT_PARAMETER,
			this.SHORE_CONTINENTALNESS,
			MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]),
			parameterRange,
			0.0F,
			parameterRange.getMax() < 0.0F ? BiomeKeys.STONE_SHORE : BiomeKeys.RIVER
		);
		this.writeBiomeParameters(
			consumer,
			this.FROZEN_TEMPERATURE,
			this.DEFAULT_PARAMETER,
			this.NEAR_INLAND_CONTINENTALNESS,
			MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]),
			parameterRange,
			0.0F,
			BiomeKeys.FROZEN_RIVER
		);
		this.writeBiomeParameters(
			consumer,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			this.DEFAULT_PARAMETER,
			this.NEAR_INLAND_CONTINENTALNESS,
			MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]),
			parameterRange,
			0.0F,
			BiomeKeys.RIVER
		);
		this.writeBiomeParameters(
			consumer,
			this.FROZEN_TEMPERATURE,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
			MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[5]),
			parameterRange,
			0.0F,
			BiomeKeys.FROZEN_RIVER
		);
		this.writeBiomeParameters(
			consumer,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
			MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[5]),
			parameterRange,
			0.0F,
			BiomeKeys.RIVER
		);
		this.writeBiomeParameters(
			consumer,
			this.FROZEN_TEMPERATURE,
			this.DEFAULT_PARAMETER,
			this.SHORE_CONTINENTALNESS,
			this.EROSION_PARAMETERS[6],
			parameterRange,
			0.0F,
			BiomeKeys.FROZEN_RIVER
		);
		this.writeBiomeParameters(
			consumer,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			this.DEFAULT_PARAMETER,
			this.SHORE_CONTINENTALNESS,
			this.EROSION_PARAMETERS[6],
			parameterRange,
			0.0F,
			BiomeKeys.RIVER
		);
		this.writeBiomeParameters(
			consumer,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.ParameterRange.method_38123(this.RIVER_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
			this.EROSION_PARAMETERS[6],
			parameterRange,
			0.0F,
			BiomeKeys.SWAMP
		);
		this.writeBiomeParameters(
			consumer,
			this.FROZEN_TEMPERATURE,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.ParameterRange.method_38123(this.RIVER_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
			this.EROSION_PARAMETERS[6],
			parameterRange,
			0.0F,
			BiomeKeys.FROZEN_RIVER
		);

		for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; i++) {
			MultiNoiseUtil.ParameterRange parameterRange2 = this.TEMPERATURE_PARAMETERS[i];

			for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; j++) {
				MultiNoiseUtil.ParameterRange parameterRange3 = this.HUMIDITY_PARAMETERS[j];
				RegistryKey<Biome> registryKey = this.getBadlandsOrRegularBiome(i, j, parameterRange);
				this.writeBiomeParameters(
					consumer,
					parameterRange2,
					parameterRange3,
					MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]),
					parameterRange,
					0.0F,
					registryKey
				);
			}
		}
	}

	private void writeCaveBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer) {
		this.writeCaveBiomeParameters(
			consumer,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.ParameterRange.method_38121(0.8F, 1.0F),
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.DRIPSTONE_CAVES
		);
		this.writeCaveBiomeParameters(
			consumer,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.ParameterRange.method_38121(0.7F, 1.0F),
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.LUSH_CAVES
		);
	}

	private RegistryKey<Biome> getRegularBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
		if (weirdness.getMax() < 0.0F) {
			return this.COMMON_BIOMES[temperature][humidity];
		} else {
			RegistryKey<Biome> registryKey = this.UNCOMMON_BIOMES[temperature][humidity];
			return registryKey == null ? this.COMMON_BIOMES[temperature][humidity] : registryKey;
		}
	}

	private RegistryKey<Biome> getBadlandsOrRegularBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
		return temperature == 4 ? this.getBadlandsBiome(humidity, weirdness) : this.getRegularBiome(temperature, humidity, weirdness);
	}

	private RegistryKey<Biome> method_38194(int i, int j, MultiNoiseUtil.ParameterRange parameterRange) {
		return i == 0 ? this.getMountainSlopeBiome(i, j, parameterRange) : this.getBadlandsOrRegularBiome(i, j, parameterRange);
	}

	private RegistryKey<Biome> method_38183(int i, int j, MultiNoiseUtil.ParameterRange parameterRange, RegistryKey<Biome> registryKey) {
		return i > 1 && j < 4 && parameterRange.getMax() >= 0.0F ? BiomeKeys.SHATTERED_SAVANNA : registryKey;
	}

	private RegistryKey<Biome> method_38198(int i, int j, MultiNoiseUtil.ParameterRange parameterRange) {
		RegistryKey<Biome> registryKey = parameterRange.getMax() >= 0.0F ? this.getRegularBiome(i, j, parameterRange) : this.method_38181(i, j);
		return this.method_38183(i, j, parameterRange, registryKey);
	}

	private RegistryKey<Biome> method_38181(int i, int j) {
		if (i == 0) {
			return BiomeKeys.SNOWY_BEACH;
		} else {
			return i == 4 && j < 3 ? BiomeKeys.DESERT : BiomeKeys.BEACH;
		}
	}

	private RegistryKey<Biome> getBadlandsBiome(int humidity, MultiNoiseUtil.ParameterRange weirdness) {
		if (humidity < 2) {
			return weirdness.getMax() < 0.0F ? BiomeKeys.ERODED_BADLANDS : BiomeKeys.BADLANDS;
		} else {
			return humidity < 3 ? BiomeKeys.BADLANDS : BiomeKeys.WOODED_BADLANDS_PLATEAU;
		}
	}

	private RegistryKey<Biome> getNearMountainBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
		if (weirdness.getMax() < 0.0F) {
			return this.NEAR_MOUNTAIN_BIOMES[temperature][humidity];
		} else {
			RegistryKey<Biome> registryKey = this.SPECIAL_NEAR_MOUNTAIN_BIOMES[temperature][humidity];
			return registryKey == null ? this.NEAR_MOUNTAIN_BIOMES[temperature][humidity] : registryKey;
		}
	}

	private RegistryKey<Biome> getPeakBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
		if (temperature <= 2) {
			return weirdness.getMax() < 0.0F ? BiomeKeys.LOFTY_PEAKS : BiomeKeys.SNOWCAPPED_PEAKS;
		} else {
			return temperature == 3 ? BiomeKeys.STONY_PEAKS : this.getBadlandsBiome(humidity, weirdness);
		}
	}

	private RegistryKey<Biome> getMountainSlopeBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
		if (temperature >= 3) {
			return this.getNearMountainBiome(temperature, humidity, weirdness);
		} else {
			return humidity <= 1 ? BiomeKeys.SNOWY_SLOPES : BiomeKeys.GROVE;
		}
	}

	private RegistryKey<Biome> getHillBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
		RegistryKey<Biome> registryKey = this.HILL_BIOMES[temperature][humidity];
		return registryKey == null ? this.getRegularBiome(temperature, humidity, weirdness) : registryKey;
	}

	private void writeBiomeParameters(
		Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer,
		MultiNoiseUtil.ParameterRange temperature,
		MultiNoiseUtil.ParameterRange humidity,
		MultiNoiseUtil.ParameterRange continentalness,
		MultiNoiseUtil.ParameterRange erosion,
		MultiNoiseUtil.ParameterRange weirdness,
		float offset,
		RegistryKey<Biome> biome
	) {
		consumer.accept(
			Pair.of(
				MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.method_38120(0.0F), weirdness, offset),
				biome
			)
		);
		consumer.accept(
			Pair.of(
				MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.method_38120(1.0F), weirdness, offset),
				biome
			)
		);
	}

	private void writeCaveBiomeParameters(
		Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer,
		MultiNoiseUtil.ParameterRange temperature,
		MultiNoiseUtil.ParameterRange humidity,
		MultiNoiseUtil.ParameterRange continentalness,
		MultiNoiseUtil.ParameterRange erosion,
		MultiNoiseUtil.ParameterRange weirdness,
		float offset,
		RegistryKey<Biome> biome
	) {
		consumer.accept(
			Pair.of(
				MultiNoiseUtil.createNoiseHypercube(
					temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.method_38121(0.1F, 0.9F), weirdness, offset
				),
				biome
			)
		);
	}

	public static String getWeirdnessDescription(double weirdness) {
		if (weirdness < (double)VanillaTerrainParameters.getNormalizedWeirdness(0.05F)) {
			return "Valley";
		} else if (weirdness < (double)VanillaTerrainParameters.getNormalizedWeirdness(0.26666668F)) {
			return "Low";
		} else if (weirdness < (double)VanillaTerrainParameters.getNormalizedWeirdness(0.4F)) {
			return "Mid";
		} else {
			return weirdness < (double)VanillaTerrainParameters.getNormalizedWeirdness(0.56666666F) ? "High" : "Peak";
		}
	}

	public String getContinentalnessDescription(double continentalness) {
		if (continentalness < (double)this.MUSHROOM_FIELDS_CONTINENTALNESS.getMax()) {
			return "Mushroom fields";
		} else if (continentalness < (double)this.DEEP_OCEAN_CONTINENTALNESS.getMax()) {
			return "Deep ocean";
		} else if (continentalness < (double)this.OCEAN_CONTINENTALNESS.getMax()) {
			return "Ocean";
		} else if (continentalness < (double)this.SHORE_CONTINENTALNESS.getMax()) {
			return "Coast";
		} else if (continentalness < (double)this.NEAR_INLAND_CONTINENTALNESS.getMax()) {
			return "Near inland";
		} else {
			return continentalness < (double)this.MID_INLAND_CONTINENTALNESS.getMax() ? "Mid inland" : "Far inland";
		}
	}

	public String getErosionDescription(double erosion) {
		return getNoiseRangeIndex(erosion, this.EROSION_PARAMETERS);
	}

	public String getTemperatureDescription(double temperature) {
		return getNoiseRangeIndex(temperature, this.TEMPERATURE_PARAMETERS);
	}

	public String getHumidityDescription(double humidity) {
		return getNoiseRangeIndex(humidity, this.HUMIDITY_PARAMETERS);
	}

	private static String getNoiseRangeIndex(double d, MultiNoiseUtil.ParameterRange[] parameterRanges) {
		for (int i = 0; i < parameterRanges.length; i++) {
			if (d < (double)parameterRanges[i].getMax()) {
				return i + "";
			}
		}

		return "?";
	}
}
