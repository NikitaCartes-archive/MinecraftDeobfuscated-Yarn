package net.minecraft.world.biome.source.util;

import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.SharedConstants;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public final class VanillaBiomeParameters {
	private final MultiNoiseUtil.ParameterRange DEFAULT_PARAMETER = MultiNoiseUtil.createParameterRange(-1.0F, 1.0F);
	private final MultiNoiseUtil.ParameterRange[] TEMPERATURE_PARAMETERS = new MultiNoiseUtil.ParameterRange[]{
		MultiNoiseUtil.createParameterRange(-1.0F, -0.45F),
		MultiNoiseUtil.createParameterRange(-0.45F, -0.15F),
		MultiNoiseUtil.createParameterRange(-0.15F, 0.2F),
		MultiNoiseUtil.createParameterRange(0.2F, 0.55F),
		MultiNoiseUtil.createParameterRange(0.55F, 1.0F)
	};
	private final MultiNoiseUtil.ParameterRange[] HUMIDITY_PARAMETERS = new MultiNoiseUtil.ParameterRange[]{
		MultiNoiseUtil.createParameterRange(-1.0F, -0.3F),
		MultiNoiseUtil.createParameterRange(-0.3F, -0.1F),
		MultiNoiseUtil.createParameterRange(-0.1F, 0.1F),
		MultiNoiseUtil.createParameterRange(0.1F, 0.3F),
		MultiNoiseUtil.createParameterRange(0.3F, 1.0F)
	};
	private final MultiNoiseUtil.ParameterRange[] EROSION_PARAMETERS = new MultiNoiseUtil.ParameterRange[]{
		MultiNoiseUtil.createParameterRange(-1.0F, -0.375F),
		MultiNoiseUtil.createParameterRange(-0.375F, -0.2225F),
		MultiNoiseUtil.createParameterRange(-0.2225F, 0.05F),
		MultiNoiseUtil.createParameterRange(0.05F, 0.45F),
		MultiNoiseUtil.createParameterRange(0.45F, 0.55F),
		MultiNoiseUtil.createParameterRange(0.55F, 1.0F)
	};
	private final MultiNoiseUtil.ParameterRange FROZEN_TEMPERATURE = this.TEMPERATURE_PARAMETERS[0];
	private final MultiNoiseUtil.ParameterRange NON_FROZEN_TEMPERATURE_PARAMETERS = MultiNoiseUtil.combineParameterRange(
		this.TEMPERATURE_PARAMETERS[1], this.TEMPERATURE_PARAMETERS[4]
	);
	private final MultiNoiseUtil.ParameterRange MUSHROOM_FIELDS_CONTINENTALNESS = MultiNoiseUtil.createParameterRange(-1.2F, -1.05F);
	private final MultiNoiseUtil.ParameterRange DEEP_OCEAN_CONTINENTALNESS = MultiNoiseUtil.createParameterRange(-1.05F, -0.455F);
	private final MultiNoiseUtil.ParameterRange OCEAN_CONTINENTALNESS = MultiNoiseUtil.createParameterRange(-0.455F, -0.19F);
	private final MultiNoiseUtil.ParameterRange SHORE_CONTINENTALNESS = MultiNoiseUtil.createParameterRange(-0.19F, -0.11F);
	private final MultiNoiseUtil.ParameterRange RIVER_CONTINENTALNESS = MultiNoiseUtil.createParameterRange(-0.11F, 0.55F);
	private final MultiNoiseUtil.ParameterRange NEAR_INLAND_CONTINENTALNESS = MultiNoiseUtil.createParameterRange(-0.11F, 0.03F);
	private final MultiNoiseUtil.ParameterRange MID_INLAND_CONTINENTALNESS = MultiNoiseUtil.createParameterRange(0.03F, 0.55F);
	private final MultiNoiseUtil.ParameterRange FAR_INLAND_CONTINENTALNESS = MultiNoiseUtil.createParameterRange(0.55F, 1.0F);
	private final RegistryKey<Biome>[][] OCEAN_BIOMES = new RegistryKey[][]{
		{BiomeKeys.DEEP_FROZEN_OCEAN, BiomeKeys.DEEP_COLD_OCEAN, BiomeKeys.DEEP_OCEAN, BiomeKeys.DEEP_LUKEWARM_OCEAN, BiomeKeys.DEEP_WARM_OCEAN},
		{BiomeKeys.FROZEN_OCEAN, BiomeKeys.COLD_OCEAN, BiomeKeys.OCEAN, BiomeKeys.LUKEWARM_OCEAN, BiomeKeys.WARM_OCEAN}
	};
	private final RegistryKey<Biome>[][] COMMON_BIOMES = new RegistryKey[][]{
		{BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TAIGA, BiomeKeys.GIANT_TREE_TAIGA},
		{BiomeKeys.PLAINS, BiomeKeys.PLAINS, BiomeKeys.FOREST, BiomeKeys.TAIGA, BiomeKeys.TAIGA},
		{BiomeKeys.PLAINS, BiomeKeys.PLAINS, BiomeKeys.FOREST, BiomeKeys.BIRCH_FOREST, BiomeKeys.DARK_FOREST},
		{BiomeKeys.SAVANNA, BiomeKeys.SAVANNA, BiomeKeys.FOREST, BiomeKeys.FOREST, BiomeKeys.JUNGLE},
		{BiomeKeys.DESERT, BiomeKeys.DESERT, BiomeKeys.DESERT, BiomeKeys.JUNGLE, BiomeKeys.JUNGLE}
	};
	private final RegistryKey<Biome>[][] UNCOMMON_BIOMES = new RegistryKey[][]{
		{BiomeKeys.ICE_SPIKES, null, null, BiomeKeys.GIANT_SPRUCE_TAIGA, null},
		{null, null, null, null, null},
		{null, BiomeKeys.SUNFLOWER_PLAINS, BiomeKeys.FLOWER_FOREST, BiomeKeys.TALL_BIRCH_FOREST, null},
		{null, null, BiomeKeys.PLAINS, BiomeKeys.PLAINS, null},
		{null, null, null, BiomeKeys.JUNGLE_EDGE, BiomeKeys.BAMBOO_JUNGLE}
	};
	private final RegistryKey<Biome>[][] NEAR_MOUNTAIN_BIOMES = new RegistryKey[][]{
		{BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TAIGA, BiomeKeys.GIANT_TREE_TAIGA},
		{BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TAIGA, BiomeKeys.SNOWY_TAIGA},
		{BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.MEADOW},
		{BiomeKeys.SAVANNA_PLATEAU, BiomeKeys.SAVANNA_PLATEAU, BiomeKeys.FOREST, BiomeKeys.FOREST, BiomeKeys.JUNGLE},
		{BiomeKeys.BADLANDS, BiomeKeys.BADLANDS, BiomeKeys.BADLANDS, BiomeKeys.WOODED_BADLANDS_PLATEAU, BiomeKeys.WOODED_BADLANDS_PLATEAU}
	};
	private final RegistryKey<Biome>[][] SPECIAL_NEAR_MOUNTAIN_BIOMES = new RegistryKey[][]{
		{BiomeKeys.ICE_SPIKES, null, null, BiomeKeys.GIANT_SPRUCE_TAIGA, null},
		{null, null, null, null, null},
		{null, null, null, null, null},
		{null, null, null, null, null},
		{BiomeKeys.ERODED_BADLANDS, BiomeKeys.ERODED_BADLANDS, null, null, null}
	};
	private final RegistryKey<Biome>[][] HILL_BIOMES = new RegistryKey[][]{
		{BiomeKeys.GRAVELLY_HILLS, BiomeKeys.GRAVELLY_HILLS, BiomeKeys.EXTREME_HILLS, BiomeKeys.WOODED_MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS},
		{BiomeKeys.GRAVELLY_HILLS, BiomeKeys.GRAVELLY_HILLS, BiomeKeys.EXTREME_HILLS, BiomeKeys.WOODED_MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS},
		{BiomeKeys.EXTREME_HILLS, BiomeKeys.EXTREME_HILLS, BiomeKeys.EXTREME_HILLS, BiomeKeys.WOODED_MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS},
		{null, null, null, null, null},
		{null, null, null, null, null}
	};

	public void writeVanillaBiomeParameters(Builder<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters) {
		if (SharedConstants.DEBUG_BIOME_SOURCE) {
			this.writeDebugBiomeParameters(parameters);
		} else {
			this.writeOceanBiomes(parameters);
			this.writeLandBiomes(parameters);
			this.writeCaveBiomes(parameters);
		}
	}

	private void writeOceanBiomes(Builder<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters) {
		this.writeBiomeParameters(
			parameters,
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
				parameters,
				parameterRange,
				this.DEFAULT_PARAMETER,
				this.DEEP_OCEAN_CONTINENTALNESS,
				this.DEFAULT_PARAMETER,
				this.DEFAULT_PARAMETER,
				0.0F,
				this.OCEAN_BIOMES[0][i]
			);
			this.writeBiomeParameters(
				parameters,
				parameterRange,
				this.DEFAULT_PARAMETER,
				this.OCEAN_CONTINENTALNESS,
				this.DEFAULT_PARAMETER,
				this.DEFAULT_PARAMETER,
				0.0F,
				this.OCEAN_BIOMES[1][i]
			);
		}
	}

	private void writeLandBiomes(Builder<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters) {
		float f = 0.05F;
		float g = 0.1F;
		float h = 0.56666666F;
		float i = 0.7666667F;
		this.writeMixedBiomes(parameters, MultiNoiseUtil.createParameterRange(-1.0F, -0.93333334F));
		this.writePlainBiomes(parameters, MultiNoiseUtil.createParameterRange(-0.93333334F, -0.7666667F));
		this.writeMountainousBiomes(parameters, MultiNoiseUtil.createParameterRange(-0.7666667F, -0.56666666F));
		this.writePlainBiomes(parameters, MultiNoiseUtil.createParameterRange(-0.56666666F, -0.4F));
		this.writeMixedBiomes(parameters, MultiNoiseUtil.createParameterRange(-0.4F, -0.26666668F));
		this.writeBiomesNearRivers(parameters, MultiNoiseUtil.createParameterRange(-0.26666668F, -0.05F));
		this.writeRiverBiomes(parameters, MultiNoiseUtil.createParameterRange(-0.05F, 0.05F));
		this.writeBiomesNearRivers(parameters, MultiNoiseUtil.createParameterRange(0.05F, 0.26666668F));
		this.writeMixedBiomes(parameters, MultiNoiseUtil.createParameterRange(0.26666668F, 0.4F));
		this.writePlainBiomes(parameters, MultiNoiseUtil.createParameterRange(0.4F, 0.56666666F));
		this.writeMountainousBiomes(parameters, MultiNoiseUtil.createParameterRange(0.56666666F, 0.7666667F));
		this.writePlainBiomes(parameters, MultiNoiseUtil.createParameterRange(0.7666667F, 0.93333334F));
		this.writeMixedBiomes(parameters, MultiNoiseUtil.createParameterRange(0.93333334F, 1.0F));
	}

	private void writeMountainousBiomes(Builder<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness) {
		for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; i++) {
			MultiNoiseUtil.ParameterRange parameterRange = this.TEMPERATURE_PARAMETERS[i];

			for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; j++) {
				MultiNoiseUtil.ParameterRange parameterRange2 = this.HUMIDITY_PARAMETERS[j];
				RegistryKey<Biome> registryKey = this.getRegularBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey2 = this.getBadlandsOrRegularBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey3 = this.getNearMountainBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey4 = this.getHillBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey5 = this.method_37846(i, j, weirdness, registryKey4);
				RegistryKey<Biome> registryKey6 = this.getPeakBiome(i, j, weirdness);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[0], weirdness, 0.0F, registryKey3
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[0],
					weirdness,
					0.0F,
					registryKey6
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					this.NEAR_INLAND_CONTINENTALNESS,
					MultiNoiseUtil.combineParameterRange(this.EROSION_PARAMETERS[1], this.EROSION_PARAMETERS[2]),
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[1],
					weirdness,
					0.0F,
					registryKey3
				);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.MID_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[2], weirdness, 0.0F, registryKey2
				);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.FAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[2], weirdness, 0.0F, registryKey3
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[3],
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.NEAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[4],
					weirdness,
					0.0F,
					registryKey5
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[4],
					weirdness,
					0.0F,
					registryKey4
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[5],
					weirdness,
					0.0F,
					registryKey
				);
			}
		}
	}

	private void writePlainBiomes(Builder<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness) {
		for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; i++) {
			MultiNoiseUtil.ParameterRange parameterRange = this.TEMPERATURE_PARAMETERS[i];

			for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; j++) {
				MultiNoiseUtil.ParameterRange parameterRange2 = this.HUMIDITY_PARAMETERS[j];
				RegistryKey<Biome> registryKey = this.getRegularBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey2 = this.getBadlandsOrRegularBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey3 = this.getNearMountainBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey4 = this.getHillBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey5 = this.method_37846(i, j, weirdness, registryKey);
				RegistryKey<Biome> registryKey6 = this.getMountainSlopeBiome(i, j, weirdness);
				this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[0], weirdness, 0.0F, registryKey);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[0], weirdness, 0.0F, registryKey2
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[0],
					weirdness,
					0.0F,
					registryKey6
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.NEAR_INLAND_CONTINENTALNESS),
					MultiNoiseUtil.combineParameterRange(this.EROSION_PARAMETERS[1], this.EROSION_PARAMETERS[2]),
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[1],
					weirdness,
					0.0F,
					registryKey3
				);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.MID_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[2], weirdness, 0.0F, registryKey2
				);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.FAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[2], weirdness, 0.0F, registryKey3
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[3],
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.NEAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[4],
					weirdness,
					0.0F,
					registryKey5
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[4],
					weirdness,
					0.0F,
					registryKey4
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[5],
					weirdness,
					0.0F,
					registryKey
				);
			}
		}
	}

	private void writeMixedBiomes(Builder<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness) {
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			this.SHORE_CONTINENTALNESS,
			MultiNoiseUtil.combineParameterRange(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]),
			weirdness,
			0.0F,
			BiomeKeys.STONE_SHORE
		);
		this.writeBiomeParameters(
			parameters,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			MultiNoiseUtil.combineParameterRange(this.HUMIDITY_PARAMETERS[1], this.HUMIDITY_PARAMETERS[4]),
			MultiNoiseUtil.combineParameterRange(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
			this.EROSION_PARAMETERS[5],
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
				RegistryKey<Biome> registryKey3 = this.getHillBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey4 = this.getNearMountainBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey5 = this.method_37869(i, j);
				RegistryKey<Biome> registryKey6 = this.method_37846(i, j, weirdness, registryKey);
				RegistryKey<Biome> registryKey7 = this.method_37870(i, j, weirdness, registryKey5);
				RegistryKey<Biome> registryKey8 = this.getMountainSlopeBiome(i, j, weirdness);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.NEAR_INLAND_CONTINENTALNESS, this.MID_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[0],
					weirdness,
					0.0F,
					registryKey2
				);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.FAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[0], weirdness, 0.0F, registryKey8
				);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[1], weirdness, 0.0F, registryKey
				);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.MID_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[1], weirdness, 0.0F, registryKey2
				);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.FAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[1], weirdness, 0.0F, registryKey4
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.NEAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[2],
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[2],
					weirdness,
					0.0F,
					registryKey2
				);
				if (weirdness.getMax() < 0.0F) {
					this.writeBiomeParameters(
						parameters, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[3], weirdness, 0.0F, registryKey5
					);
					this.writeBiomeParameters(
						parameters,
						parameterRange,
						parameterRange2,
						MultiNoiseUtil.combineParameterRange(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
						this.EROSION_PARAMETERS[3],
						weirdness,
						0.0F,
						registryKey
					);
				} else {
					this.writeBiomeParameters(
						parameters,
						parameterRange,
						parameterRange2,
						MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
						this.EROSION_PARAMETERS[3],
						weirdness,
						0.0F,
						registryKey
					);
				}

				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[4], weirdness, 0.0F, registryKey7
				);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[4], weirdness, 0.0F, registryKey6
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[4],
					weirdness,
					0.0F,
					registryKey3
				);
				if (weirdness.getMax() < 0.0F) {
					this.writeBiomeParameters(
						parameters, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[5], weirdness, 0.0F, registryKey5
					);
				} else {
					this.writeBiomeParameters(
						parameters, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[5], weirdness, 0.0F, registryKey
					);
				}

				if (i == 0 || j == 0) {
					this.writeBiomeParameters(
						parameters,
						parameterRange,
						parameterRange2,
						MultiNoiseUtil.combineParameterRange(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
						this.EROSION_PARAMETERS[5],
						weirdness,
						0.0F,
						registryKey
					);
				}
			}
		}
	}

	private void writeBiomesNearRivers(Builder<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness) {
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			this.SHORE_CONTINENTALNESS,
			MultiNoiseUtil.combineParameterRange(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]),
			weirdness,
			0.0F,
			BiomeKeys.STONE_SHORE
		);
		this.writeBiomeParameters(
			parameters,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			MultiNoiseUtil.combineParameterRange(this.HUMIDITY_PARAMETERS[1], this.HUMIDITY_PARAMETERS[4]),
			MultiNoiseUtil.combineParameterRange(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
			this.EROSION_PARAMETERS[5],
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
				RegistryKey<Biome> registryKey3 = this.getMountainSlopeBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey4 = this.method_37869(i, j);
				RegistryKey<Biome> registryKey5 = this.method_37846(i, j, weirdness, registryKey);
				RegistryKey<Biome> registryKey6 = this.method_37870(i, j, weirdness, registryKey4);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.NEAR_INLAND_CONTINENTALNESS, this.MID_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[0],
					weirdness,
					0.0F,
					registryKey2
				);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.FAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[0], weirdness, 0.0F, registryKey3
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					this.NEAR_INLAND_CONTINENTALNESS,
					MultiNoiseUtil.combineParameterRange(this.EROSION_PARAMETERS[1], this.EROSION_PARAMETERS[2]),
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					MultiNoiseUtil.combineParameterRange(this.EROSION_PARAMETERS[1], this.EROSION_PARAMETERS[2]),
					weirdness,
					0.0F,
					registryKey2
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					this.SHORE_CONTINENTALNESS,
					MultiNoiseUtil.combineParameterRange(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[3]),
					weirdness,
					0.0F,
					registryKey4
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[3],
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[4], weirdness, 0.0F, registryKey6
				);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[4], weirdness, 0.0F, registryKey5
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[4],
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[5], weirdness, 0.0F, registryKey4
				);
				if (i == 0 || j == 0) {
					this.writeBiomeParameters(
						parameters,
						parameterRange,
						parameterRange2,
						MultiNoiseUtil.combineParameterRange(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
						this.EROSION_PARAMETERS[5],
						weirdness,
						0.0F,
						registryKey
					);
				}
			}
		}
	}

	private void writeRiverBiomes(Builder<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness) {
		this.writeBiomeParameters(
			parameters,
			this.FROZEN_TEMPERATURE,
			this.DEFAULT_PARAMETER,
			this.SHORE_CONTINENTALNESS,
			this.EROSION_PARAMETERS[0],
			weirdness,
			0.0F,
			weirdness.getMax() < 0.0F ? BiomeKeys.STONE_SHORE : BiomeKeys.FROZEN_RIVER
		);
		this.writeBiomeParameters(
			parameters,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			this.DEFAULT_PARAMETER,
			this.SHORE_CONTINENTALNESS,
			this.EROSION_PARAMETERS[0],
			weirdness,
			0.0F,
			weirdness.getMax() < 0.0F ? BiomeKeys.STONE_SHORE : BiomeKeys.RIVER
		);
		this.writeBiomeParameters(
			parameters,
			this.FROZEN_TEMPERATURE,
			this.DEFAULT_PARAMETER,
			this.NEAR_INLAND_CONTINENTALNESS,
			this.EROSION_PARAMETERS[0],
			weirdness,
			0.0F,
			BiomeKeys.FROZEN_RIVER
		);
		this.writeBiomeParameters(
			parameters,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			this.DEFAULT_PARAMETER,
			this.NEAR_INLAND_CONTINENTALNESS,
			this.EROSION_PARAMETERS[0],
			weirdness,
			0.0F,
			BiomeKeys.RIVER
		);
		this.writeBiomeParameters(
			parameters,
			this.FROZEN_TEMPERATURE,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
			MultiNoiseUtil.combineParameterRange(this.EROSION_PARAMETERS[1], this.EROSION_PARAMETERS[4]),
			weirdness,
			0.0F,
			BiomeKeys.FROZEN_RIVER
		);
		this.writeBiomeParameters(
			parameters,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
			MultiNoiseUtil.combineParameterRange(this.EROSION_PARAMETERS[1], this.EROSION_PARAMETERS[4]),
			weirdness,
			0.0F,
			BiomeKeys.RIVER
		);
		this.writeBiomeParameters(
			parameters, this.FROZEN_TEMPERATURE, this.DEFAULT_PARAMETER, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[5], weirdness, 0.0F, BiomeKeys.FROZEN_RIVER
		);
		this.writeBiomeParameters(
			parameters,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			this.DEFAULT_PARAMETER,
			this.SHORE_CONTINENTALNESS,
			this.EROSION_PARAMETERS[5],
			weirdness,
			0.0F,
			BiomeKeys.RIVER
		);
		this.writeBiomeParameters(
			parameters,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			MultiNoiseUtil.combineParameterRange(this.HUMIDITY_PARAMETERS[1], this.HUMIDITY_PARAMETERS[4]),
			MultiNoiseUtil.combineParameterRange(this.RIVER_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
			this.EROSION_PARAMETERS[5],
			weirdness,
			0.0F,
			BiomeKeys.SWAMP
		);
		this.writeBiomeParameters(
			parameters,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			this.HUMIDITY_PARAMETERS[0],
			MultiNoiseUtil.combineParameterRange(this.RIVER_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
			this.EROSION_PARAMETERS[5],
			weirdness,
			0.0F,
			BiomeKeys.RIVER
		);
		this.writeBiomeParameters(
			parameters,
			this.FROZEN_TEMPERATURE,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.combineParameterRange(this.RIVER_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
			this.EROSION_PARAMETERS[5],
			weirdness,
			0.0F,
			BiomeKeys.FROZEN_RIVER
		);

		for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; i++) {
			MultiNoiseUtil.ParameterRange parameterRange = this.TEMPERATURE_PARAMETERS[i];

			for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; j++) {
				MultiNoiseUtil.ParameterRange parameterRange2 = this.HUMIDITY_PARAMETERS[j];
				RegistryKey<Biome> registryKey = this.getRegularBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey2 = this.getBadlandsBiome(j, weirdness);
				RegistryKey<Biome> registryKey3 = i == 4 ? registryKey2 : registryKey;
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS),
					this.EROSION_PARAMETERS[0],
					weirdness,
					0.0F,
					registryKey3
				);
			}
		}
	}

	private void writeCaveBiomes(Builder<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters) {
		this.writeCaveBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(0.8F, 1.0F),
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.DRIPSTONE_CAVES
		);
		this.writeCaveBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(0.7F, 1.0F),
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

	private RegistryKey<Biome> method_37846(int i, int j, MultiNoiseUtil.ParameterRange parameterRange, RegistryKey<Biome> registryKey) {
		return i > 1 && j < 4 && parameterRange.getMax() >= 0.0F ? BiomeKeys.SHATTERED_SAVANNA : registryKey;
	}

	private RegistryKey<Biome> method_37870(int i, int j, MultiNoiseUtil.ParameterRange parameterRange, RegistryKey<Biome> registryKey) {
		if (i > 1 && j < 4 && parameterRange.getMax() >= 0.0F) {
			return BiomeKeys.SHATTERED_SAVANNA;
		} else {
			return parameterRange.getMax() >= 0.0F ? this.getRegularBiome(i, j, parameterRange) : this.method_37869(i, j);
		}
	}

	private RegistryKey<Biome> method_37869(int i, int j) {
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
		Builder<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters,
		MultiNoiseUtil.ParameterRange temperature,
		MultiNoiseUtil.ParameterRange humidity,
		MultiNoiseUtil.ParameterRange continentalness,
		MultiNoiseUtil.ParameterRange erosion,
		MultiNoiseUtil.ParameterRange weirdness,
		float offset,
		RegistryKey<Biome> biome
	) {
		parameters.add(
			Pair.of(
				MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.createParameterRange(0.1F), weirdness, offset), biome
			)
		);
		parameters.add(
			Pair.of(
				MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.createParameterRange(1.0F), weirdness, offset), biome
			)
		);
	}

	private void writeCaveBiomeParameters(
		Builder<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters,
		MultiNoiseUtil.ParameterRange temperature,
		MultiNoiseUtil.ParameterRange humidity,
		MultiNoiseUtil.ParameterRange continentalness,
		MultiNoiseUtil.ParameterRange erosion,
		MultiNoiseUtil.ParameterRange weirdness,
		float offset,
		RegistryKey<Biome> biome
	) {
		parameters.add(
			Pair.of(
				MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.createParameterRange(0.2F, 0.9F), weirdness, offset),
				biome
			)
		);
	}

	private void writeDebugBiomeParameters(Builder<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters) {
		this.writeBiomeParameters(
			parameters, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, 0.01F, BiomeKeys.PLAINS
		);
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-0.9F),
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.DESERT
		);
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-0.4F),
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.BADLANDS
		);
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-0.35F),
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.DESERT
		);
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-0.1F),
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.BADLANDS
		);
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(0.2F),
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.DESERT
		);
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(1.0F),
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.BADLANDS
		);
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-1.1F),
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.SNOWY_TAIGA
		);
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-1.005F),
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.SNOWY_TAIGA
		);
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-0.51F),
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.SNOWY_TAIGA
		);
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-0.44F),
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.SNOWY_TAIGA
		);
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-0.18F),
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.SNOWY_TAIGA
		);
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-0.16F),
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.SNOWY_TAIGA
		);
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-0.15F),
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.SNOWY_TAIGA
		);
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-0.1F),
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.SNOWY_TAIGA
		);
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(0.25F),
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.SNOWY_TAIGA
		);
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(1.0F),
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.SNOWY_TAIGA
		);
	}

	private void method_37847(Builder<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters) {
		float f = -0.2F;
		float g = -0.05F;
		float h = -0.15F;
		float i = 0.15F;
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-0.2F, -0.05F),
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.BADLANDS
		);
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-0.05F, 1.0F),
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-0.15F, 0.15F),
			0.0F,
			BiomeKeys.DESERT
		);
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-1.0F, -0.2F),
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.OCEAN
		);
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-0.05F, 1.0F),
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-1.0F, -0.15F),
			0.0F,
			BiomeKeys.PLAINS
		);
		this.writeBiomeParameters(
			parameters,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-0.05F, 1.0F),
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(0.15F, 1.0F),
			0.0F,
			BiomeKeys.PLAINS
		);
	}

	public static String getWeirdnessDescription(double weirdness) {
		if (weirdness < -0.85) {
			return "Valley";
		} else if (weirdness < -0.19) {
			return "Low";
		} else if (weirdness < 0.21) {
			return "Mid";
		} else {
			return weirdness < 0.81 ? "High" : "Peak";
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
		return this.getNoiseRangeIndex(erosion, this.EROSION_PARAMETERS);
	}

	public String getTemperatureDescription(double temperature) {
		return this.getNoiseRangeIndex(temperature, this.TEMPERATURE_PARAMETERS);
	}

	public String getHumidityDescription(double humidity) {
		return this.getNoiseRangeIndex(humidity, this.HUMIDITY_PARAMETERS);
	}

	private String getNoiseRangeIndex(double noise, MultiNoiseUtil.ParameterRange[] ranges) {
		for (int i = 0; i < ranges.length; i++) {
			if (noise < (double)ranges[i].getMax()) {
				return i + "";
			}
		}

		return "?";
	}
}
