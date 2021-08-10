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
		MultiNoiseUtil.createParameterRange(-0.15F, 0.15F),
		MultiNoiseUtil.createParameterRange(0.15F, 0.45F),
		MultiNoiseUtil.createParameterRange(0.45F, 1.0F)
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
		MultiNoiseUtil.createParameterRange(0.05F, 0.51F),
		MultiNoiseUtil.createParameterRange(0.51F, 0.6F),
		MultiNoiseUtil.createParameterRange(0.6F, 1.0F)
	};
	private final MultiNoiseUtil.ParameterRange FROZEN_TEMPERATURE = this.TEMPERATURE_PARAMETERS[0];
	private final MultiNoiseUtil.ParameterRange NON_FROZEN_TEMPERATURE_PARAMETERS = MultiNoiseUtil.combineParameterRange(
		this.TEMPERATURE_PARAMETERS[1], this.TEMPERATURE_PARAMETERS[4]
	);
	private final MultiNoiseUtil.ParameterRange MUSHROOM_FIELDS_CONTINENTALNESS = MultiNoiseUtil.createParameterRange(-1.05F, -1.05F);
	private final MultiNoiseUtil.ParameterRange DEEP_OCEAN_CONTINENTALNESS = MultiNoiseUtil.createParameterRange(-1.05F, -0.455F);
	private final MultiNoiseUtil.ParameterRange OCEAN_CONTINENTALNESS = MultiNoiseUtil.createParameterRange(-0.455F, -0.2F);
	private final MultiNoiseUtil.ParameterRange SHORE_CONTINENTALNESS = MultiNoiseUtil.createParameterRange(-0.2F, -0.11F);
	private final MultiNoiseUtil.ParameterRange RIVER_CONTINENTALNESS = MultiNoiseUtil.createParameterRange(-0.11F, 0.55F);
	private final MultiNoiseUtil.ParameterRange NEXT_TO_SHORE_CONTINENTALNESS = MultiNoiseUtil.createParameterRange(-0.11F, 0.03F);
	private final MultiNoiseUtil.ParameterRange NEAR_SHORE_CONTINENTALNESS = MultiNoiseUtil.createParameterRange(0.03F, 0.55F);
	private final MultiNoiseUtil.ParameterRange FAR_FROM_SHORE_CONTINENTALNESS = MultiNoiseUtil.createParameterRange(0.55F, 1.0F);
	private final RegistryKey<Biome>[][] OCEAN_BIOMES = new RegistryKey[][]{
		{BiomeKeys.DEEP_FROZEN_OCEAN, BiomeKeys.DEEP_COLD_OCEAN, BiomeKeys.DEEP_OCEAN, BiomeKeys.DEEP_LUKEWARM_OCEAN, BiomeKeys.DEEP_WARM_OCEAN},
		{BiomeKeys.FROZEN_OCEAN, BiomeKeys.COLD_OCEAN, BiomeKeys.OCEAN, BiomeKeys.LUKEWARM_OCEAN, BiomeKeys.WARM_OCEAN}
	};
	private final RegistryKey<Biome>[][] COMMON_BIOMES = new RegistryKey[][]{
		{BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TAIGA, BiomeKeys.GIANT_TREE_TAIGA},
		{BiomeKeys.PLAINS, BiomeKeys.PLAINS, BiomeKeys.FOREST, BiomeKeys.TAIGA, BiomeKeys.TAIGA},
		{BiomeKeys.PLAINS, BiomeKeys.PLAINS, BiomeKeys.FOREST, BiomeKeys.BIRCH_FOREST, BiomeKeys.DARK_FOREST},
		{BiomeKeys.DESERT, BiomeKeys.DESERT, BiomeKeys.FOREST, BiomeKeys.FOREST, BiomeKeys.JUNGLE},
		{BiomeKeys.BADLANDS, BiomeKeys.BADLANDS, BiomeKeys.SAVANNA, BiomeKeys.SAVANNA, BiomeKeys.JUNGLE}
	};
	private final RegistryKey<Biome>[][] SPECIAL_BIOMES = new RegistryKey[][]{
		{BiomeKeys.ICE_SPIKES, null, null, BiomeKeys.GIANT_SPRUCE_TAIGA, null},
		{null, null, null, null, null},
		{BiomeKeys.SUNFLOWER_PLAINS, BiomeKeys.SUNFLOWER_PLAINS, BiomeKeys.FLOWER_FOREST, BiomeKeys.TALL_BIRCH_FOREST, null},
		{null, null, null, null, BiomeKeys.BAMBOO_JUNGLE},
		{BiomeKeys.ERODED_BADLANDS, null, null, null, null}
	};
	private final RegistryKey<Biome>[][] PLATEAU_BIOMES = new RegistryKey[][]{
		{null, null, null, null, null},
		{null, null, null, null, null},
		{BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.MEADOW},
		{BiomeKeys.BADLANDS, BiomeKeys.BADLANDS, BiomeKeys.BADLANDS, BiomeKeys.SAVANNA_PLATEAU, BiomeKeys.SAVANNA_PLATEAU},
		{BiomeKeys.BADLANDS, BiomeKeys.BADLANDS, BiomeKeys.BADLANDS, BiomeKeys.WOODED_BADLANDS_PLATEAU, BiomeKeys.WOODED_BADLANDS_PLATEAU}
	};
	private final RegistryKey<Biome>[][] HILL_BIOMES = new RegistryKey[][]{
		{BiomeKeys.GRAVELLY_HILLS, BiomeKeys.GRAVELLY_HILLS, BiomeKeys.EXTREME_HILLS, BiomeKeys.WOODED_MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS},
		{BiomeKeys.GRAVELLY_HILLS, BiomeKeys.GRAVELLY_HILLS, BiomeKeys.EXTREME_HILLS, BiomeKeys.WOODED_MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS},
		{BiomeKeys.EXTREME_HILLS, BiomeKeys.EXTREME_HILLS, BiomeKeys.EXTREME_HILLS, BiomeKeys.EXTREME_HILLS, BiomeKeys.EXTREME_HILLS},
		{BiomeKeys.BADLANDS, BiomeKeys.BADLANDS, BiomeKeys.WOODED_BADLANDS_PLATEAU, BiomeKeys.WOODED_BADLANDS_PLATEAU, BiomeKeys.WOODED_BADLANDS_PLATEAU},
		{BiomeKeys.BADLANDS, BiomeKeys.BADLANDS, BiomeKeys.SAVANNA_PLATEAU, BiomeKeys.SAVANNA_PLATEAU, BiomeKeys.WOODED_BADLANDS_PLATEAU}
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
		float g = 0.06666667F;
		float h = 0.6F;
		float i = 0.73333335F;
		this.writeMixedBiomes(parameters, MultiNoiseUtil.createParameterRange(-1.0F, -0.93333334F));
		this.writePlainBiomes(parameters, MultiNoiseUtil.createParameterRange(-0.93333334F, -0.73333335F));
		this.writeMountainousBiomes(parameters, MultiNoiseUtil.createParameterRange(-0.73333335F, -0.6F));
		this.writePlainBiomes(parameters, MultiNoiseUtil.createParameterRange(-0.6F, -0.4F));
		this.writeMixedBiomes(parameters, MultiNoiseUtil.createParameterRange(-0.4F, -0.26666668F));
		this.writeBiomesNearRivers(parameters, MultiNoiseUtil.createParameterRange(-0.26666668F, -0.05F));
		this.writeRiverBiomes(parameters, MultiNoiseUtil.createParameterRange(-0.05F, 0.05F));
		this.writeBiomesNearRivers(parameters, MultiNoiseUtil.createParameterRange(0.05F, 0.26666668F));
		this.writeMixedBiomes(parameters, MultiNoiseUtil.createParameterRange(0.26666668F, 0.4F));
		this.writePlainBiomes(parameters, MultiNoiseUtil.createParameterRange(0.4F, 0.6F));
		this.writeMountainousBiomes(parameters, MultiNoiseUtil.createParameterRange(0.6F, 0.73333335F));
		this.writePlainBiomes(parameters, MultiNoiseUtil.createParameterRange(0.73333335F, 0.93333334F));
		this.writeMixedBiomes(parameters, MultiNoiseUtil.createParameterRange(0.93333334F, 1.0F));
	}

	private void writeMountainousBiomes(Builder<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness) {
		MultiNoiseUtil.ParameterRange parameterRange = MultiNoiseUtil.combineParameterRange(this.RIVER_CONTINENTALNESS, this.FAR_FROM_SHORE_CONTINENTALNESS);
		MultiNoiseUtil.ParameterRange parameterRange2 = MultiNoiseUtil.combineParameterRange(this.NEAR_SHORE_CONTINENTALNESS, this.FAR_FROM_SHORE_CONTINENTALNESS);
		if (weirdness.getMax() < 0.0F) {
			this.writeBiomeParameters(
				parameters,
				MultiNoiseUtil.combineParameterRange(this.TEMPERATURE_PARAMETERS[0], this.TEMPERATURE_PARAMETERS[2]),
				this.DEFAULT_PARAMETER,
				parameterRange2,
				this.EROSION_PARAMETERS[0],
				weirdness,
				0.0F,
				BiomeKeys.LOFTY_PEAKS
			);
		} else {
			this.writeBiomeParameters(
				parameters,
				MultiNoiseUtil.combineParameterRange(this.TEMPERATURE_PARAMETERS[0], this.TEMPERATURE_PARAMETERS[2]),
				this.DEFAULT_PARAMETER,
				parameterRange2,
				this.EROSION_PARAMETERS[0],
				weirdness,
				0.0F,
				BiomeKeys.SNOWCAPPED_PEAKS
			);
		}

		this.writeBiomeParameters(
			parameters,
			MultiNoiseUtil.combineParameterRange(this.TEMPERATURE_PARAMETERS[3], this.TEMPERATURE_PARAMETERS[4]),
			this.DEFAULT_PARAMETER,
			parameterRange2,
			this.EROSION_PARAMETERS[0],
			weirdness,
			0.0F,
			BiomeKeys.STONY_PEAKS
		);

		for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; i++) {
			MultiNoiseUtil.ParameterRange parameterRange3 = this.TEMPERATURE_PARAMETERS[i];

			for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; j++) {
				MultiNoiseUtil.ParameterRange parameterRange4 = this.HUMIDITY_PARAMETERS[j];
				RegistryKey<Biome> registryKey = this.getPlateauOrFrozenBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey2 = this.getHillBiome(i, j);
				RegistryKey<Biome> registryKey3 = this.getRegularBiome(i, j, weirdness);
				this.writeBiomeParameters(parameters, parameterRange3, parameterRange4, parameterRange2, this.EROSION_PARAMETERS[1], weirdness, 0.0F, registryKey);
				this.writeBiomeParameters(
					parameters, parameterRange3, parameterRange4, this.NEAR_SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[2], weirdness, 0.0F, registryKey3
				);
				this.writeBiomeParameters(
					parameters, parameterRange3, parameterRange4, this.FAR_FROM_SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[2], weirdness, 0.0F, registryKey
				);
				this.writeBiomeParameters(parameters, parameterRange3, parameterRange4, parameterRange, this.EROSION_PARAMETERS[3], weirdness, 0.0F, registryKey2);
				this.writeBiomeParameters(
					parameters, parameterRange3, parameterRange4, this.NEAR_SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[4], weirdness, 0.0F, registryKey2
				);
				this.writeBiomeParameters(parameters, parameterRange3, parameterRange4, parameterRange, this.EROSION_PARAMETERS[5], weirdness, 0.0F, registryKey2);
				this.writeBiomeParameters(
					parameters, parameterRange3, parameterRange4, this.NEXT_TO_SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[0], weirdness, 0.0F, registryKey
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange3,
					parameterRange4,
					this.NEXT_TO_SHORE_CONTINENTALNESS,
					MultiNoiseUtil.combineParameterRange(this.EROSION_PARAMETERS[1], this.EROSION_PARAMETERS[2]),
					weirdness,
					0.0F,
					registryKey3
				);
			}
		}

		for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; i++) {
			MultiNoiseUtil.ParameterRange parameterRange3 = this.TEMPERATURE_PARAMETERS[i];
			RegistryKey<Biome> registryKey4 = this.method_37846(i, BiomeKeys.EXTREME_HILLS);
			this.writeBiomeParameters(
				parameters, parameterRange3, this.DEFAULT_PARAMETER, this.NEXT_TO_SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[4], weirdness, 0.0F, registryKey4
			);
			if (i > 1) {
				this.writeBiomeParameters(
					parameters, parameterRange3, this.DEFAULT_PARAMETER, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[4], weirdness, 0.0F, BiomeKeys.SHATTERED_SAVANNA
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
				RegistryKey<Biome> registryKey2 = this.getPlateauOrFrozenBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey3 = this.method_37846(i, registryKey);
				RegistryKey<Biome> registryKey4 = this.getMountainSlopesOrRegularBiome(i, j, weirdness);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.NEXT_TO_SHORE_CONTINENTALNESS),
					MultiNoiseUtil.combineParameterRange(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[2]),
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.NEAR_SHORE_CONTINENTALNESS, this.FAR_FROM_SHORE_CONTINENTALNESS),
					this.EROSION_PARAMETERS[1],
					weirdness,
					0.0F,
					registryKey2
				);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.NEAR_SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[2], weirdness, 0.0F, registryKey
				);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.FAR_FROM_SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[2], weirdness, 0.0F, registryKey2
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.FAR_FROM_SHORE_CONTINENTALNESS),
					this.EROSION_PARAMETERS[3],
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.NEAR_SHORE_CONTINENTALNESS, this.FAR_FROM_SHORE_CONTINENTALNESS),
					this.EROSION_PARAMETERS[4],
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.FAR_FROM_SHORE_CONTINENTALNESS),
					this.EROSION_PARAMETERS[5],
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.NEXT_TO_SHORE_CONTINENTALNESS),
					this.EROSION_PARAMETERS[4],
					weirdness,
					0.0F,
					registryKey3
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.NEAR_SHORE_CONTINENTALNESS, this.FAR_FROM_SHORE_CONTINENTALNESS),
					this.EROSION_PARAMETERS[0],
					weirdness,
					0.0F,
					registryKey4
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
			MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.RIVER_CONTINENTALNESS),
			this.EROSION_PARAMETERS[5],
			weirdness,
			0.0F,
			BiomeKeys.SWAMP
		);
		this.writeBiomeParameters(
			parameters,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			MultiNoiseUtil.combineParameterRange(this.HUMIDITY_PARAMETERS[1], this.HUMIDITY_PARAMETERS[4]),
			this.NEAR_SHORE_CONTINENTALNESS,
			this.EROSION_PARAMETERS[4],
			weirdness,
			0.0F,
			BiomeKeys.SWAMP
		);

		for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; i++) {
			MultiNoiseUtil.ParameterRange parameterRange = this.TEMPERATURE_PARAMETERS[i];

			for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; j++) {
				MultiNoiseUtil.ParameterRange parameterRange2 = this.HUMIDITY_PARAMETERS[j];
				RegistryKey<Biome> registryKey = this.getPlateauOrFrozenBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey2 = this.getRegularBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey3 = this.getMountainSlopesOrRegularBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey4 = this.method_37846(i, registryKey2);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.FAR_FROM_SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[0], weirdness, 0.0F, registryKey3
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					this.RIVER_CONTINENTALNESS,
					MultiNoiseUtil.combineParameterRange(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]),
					weirdness,
					0.0F,
					registryKey2
				);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.FAR_FROM_SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[1], weirdness, 0.0F, registryKey
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.RIVER_CONTINENTALNESS),
					MultiNoiseUtil.combineParameterRange(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[3]),
					weirdness,
					0.0F,
					registryKey2
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					this.FAR_FROM_SHORE_CONTINENTALNESS,
					MultiNoiseUtil.combineParameterRange(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[5]),
					weirdness,
					0.0F,
					registryKey2
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.NEXT_TO_SHORE_CONTINENTALNESS),
					this.EROSION_PARAMETERS[4],
					weirdness,
					0.0F,
					registryKey4
				);
				if (i == 0 || j == 0) {
					this.writeBiomeParameters(
						parameters, parameterRange, parameterRange2, this.NEAR_SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[4], weirdness, 0.0F, registryKey2
					);
					this.writeBiomeParameters(
						parameters,
						parameterRange,
						parameterRange2,
						MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.RIVER_CONTINENTALNESS),
						this.EROSION_PARAMETERS[5],
						weirdness,
						0.0F,
						registryKey2
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
			this.FROZEN_TEMPERATURE,
			this.DEFAULT_PARAMETER,
			this.SHORE_CONTINENTALNESS,
			MultiNoiseUtil.combineParameterRange(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[3]),
			weirdness,
			0.0F,
			BiomeKeys.SNOWY_BEACH
		);
		this.writeBiomeParameters(
			parameters,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			this.DEFAULT_PARAMETER,
			this.SHORE_CONTINENTALNESS,
			MultiNoiseUtil.combineParameterRange(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[3]),
			weirdness,
			0.0F,
			BiomeKeys.BEACH
		);
		this.writeBiomeParameters(
			parameters,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			MultiNoiseUtil.combineParameterRange(this.HUMIDITY_PARAMETERS[1], this.HUMIDITY_PARAMETERS[4]),
			MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.RIVER_CONTINENTALNESS),
			this.EROSION_PARAMETERS[5],
			weirdness,
			0.0F,
			BiomeKeys.SWAMP
		);
		this.writeBiomeParameters(
			parameters,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			MultiNoiseUtil.combineParameterRange(this.HUMIDITY_PARAMETERS[1], this.HUMIDITY_PARAMETERS[4]),
			this.NEAR_SHORE_CONTINENTALNESS,
			this.EROSION_PARAMETERS[4],
			weirdness,
			0.0F,
			BiomeKeys.SWAMP
		);

		for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; i++) {
			MultiNoiseUtil.ParameterRange parameterRange = this.TEMPERATURE_PARAMETERS[i];

			for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; j++) {
				MultiNoiseUtil.ParameterRange parameterRange2 = this.HUMIDITY_PARAMETERS[j];
				RegistryKey<Biome> registryKey = this.getRegularBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey2 = this.getMountainSlopesOrRegularBiome(i, j, weirdness);
				RegistryKey<Biome> registryKey3 = this.method_37846(i, registryKey);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					this.RIVER_CONTINENTALNESS,
					MultiNoiseUtil.combineParameterRange(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[3]),
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					this.FAR_FROM_SHORE_CONTINENTALNESS,
					MultiNoiseUtil.combineParameterRange(this.EROSION_PARAMETERS[1], this.EROSION_PARAMETERS[5]),
					weirdness,
					0.0F,
					registryKey
				);
				this.writeBiomeParameters(
					parameters,
					parameterRange,
					parameterRange2,
					MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.NEXT_TO_SHORE_CONTINENTALNESS),
					this.EROSION_PARAMETERS[4],
					weirdness,
					0.0F,
					registryKey3
				);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.FAR_FROM_SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[0], weirdness, 0.0F, registryKey2
				);
				if (i == 0 || j == 0) {
					this.writeBiomeParameters(
						parameters, parameterRange, parameterRange2, this.NEAR_SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[4], weirdness, 0.0F, registryKey
					);
					this.writeBiomeParameters(
						parameters,
						parameterRange,
						parameterRange2,
						MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.RIVER_CONTINENTALNESS),
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
			MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.RIVER_CONTINENTALNESS),
			MultiNoiseUtil.combineParameterRange(this.EROSION_PARAMETERS[1], this.EROSION_PARAMETERS[5]),
			weirdness,
			0.0F,
			BiomeKeys.FROZEN_RIVER
		);
		this.writeBiomeParameters(
			parameters,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.combineParameterRange(this.SHORE_CONTINENTALNESS, this.RIVER_CONTINENTALNESS),
			MultiNoiseUtil.combineParameterRange(this.EROSION_PARAMETERS[1], this.EROSION_PARAMETERS[5]),
			weirdness,
			0.0F,
			BiomeKeys.RIVER
		);
		this.writeBiomeParameters(
			parameters,
			this.FROZEN_TEMPERATURE,
			this.DEFAULT_PARAMETER,
			this.NEXT_TO_SHORE_CONTINENTALNESS,
			this.EROSION_PARAMETERS[0],
			weirdness,
			0.0F,
			BiomeKeys.FROZEN_RIVER
		);
		this.writeBiomeParameters(
			parameters,
			this.NON_FROZEN_TEMPERATURE_PARAMETERS,
			this.DEFAULT_PARAMETER,
			this.NEXT_TO_SHORE_CONTINENTALNESS,
			this.EROSION_PARAMETERS[0],
			weirdness,
			0.0F,
			BiomeKeys.RIVER
		);

		for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; i++) {
			MultiNoiseUtil.ParameterRange parameterRange = this.TEMPERATURE_PARAMETERS[i];

			for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; j++) {
				MultiNoiseUtil.ParameterRange parameterRange2 = this.HUMIDITY_PARAMETERS[j];
				RegistryKey<Biome> registryKey = this.getRegularBiome(i, j, weirdness);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.NEAR_SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[0], weirdness, 0.0F, registryKey
				);
				this.writeBiomeParameters(
					parameters, parameterRange, parameterRange2, this.FAR_FROM_SHORE_CONTINENTALNESS, this.DEFAULT_PARAMETER, weirdness, 0.0F, registryKey
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
			RegistryKey<Biome> registryKey = this.SPECIAL_BIOMES[temperature][humidity];
			return registryKey == null ? this.COMMON_BIOMES[temperature][humidity] : registryKey;
		}
	}

	private RegistryKey<Biome> method_37846(int i, RegistryKey<Biome> registryKey) {
		return i > 1 ? BiomeKeys.SHATTERED_SAVANNA : registryKey;
	}

	private RegistryKey<Biome> getPlateauOrFrozenBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
		RegistryKey<Biome> registryKey = this.PLATEAU_BIOMES[temperature][humidity];
		return registryKey == null ? this.getRegularBiome(0, humidity, weirdness) : registryKey;
	}

	private RegistryKey<Biome> getMountainSlopesOrRegularBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
		if (temperature >= 3) {
			return this.getRegularBiome(temperature, humidity, weirdness);
		} else {
			return humidity <= 1 ? BiomeKeys.SNOWY_SLOPES : BiomeKeys.GROVE;
		}
	}

	private RegistryKey<Biome> getHillBiome(int temperature, int humidity) {
		return this.HILL_BIOMES[temperature][humidity];
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
				MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.createParameterRange(0.0F), weirdness, offset), biome
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

	private void method_37847(Builder<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> builder) {
		float f = -0.2F;
		float g = -0.05F;
		float h = -0.15F;
		float i = 0.15F;
		this.writeBiomeParameters(
			builder,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-0.2F, -0.05F),
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.BADLANDS
		);
		this.writeBiomeParameters(
			builder,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-0.05F, 1.0F),
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-0.15F, 0.15F),
			0.0F,
			BiomeKeys.DESERT
		);
		this.writeBiomeParameters(
			builder,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-1.0F, -0.2F),
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			0.0F,
			BiomeKeys.OCEAN
		);
		this.writeBiomeParameters(
			builder,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-0.05F, 1.0F),
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-1.0F, -0.15F),
			0.0F,
			BiomeKeys.PLAINS
		);
		this.writeBiomeParameters(
			builder,
			this.DEFAULT_PARAMETER,
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(-0.05F, 1.0F),
			this.DEFAULT_PARAMETER,
			MultiNoiseUtil.createParameterRange(0.15F, 1.0F),
			0.0F,
			BiomeKeys.PLAINS
		);
	}
}
