/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source.util;

import com.mojang.datafixers.util.Pair;
import java.util.function.Consumer;
import net.minecraft.SharedConstants;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.VanillaTerrainParameters;

public final class VanillaBiomeParameters {
    private static final float field_34500 = 0.05f;
    private static final float field_34501 = 0.1f;
    private static final float field_34502 = 0.56666666f;
    private static final float field_34503 = 0.7666667f;
    private final MultiNoiseUtil.ParameterRange DEFAULT_PARAMETER = MultiNoiseUtil.ParameterRange.method_38121(-1.0f, 1.0f);
    private final MultiNoiseUtil.ParameterRange[] TEMPERATURE_PARAMETERS = new MultiNoiseUtil.ParameterRange[]{MultiNoiseUtil.ParameterRange.method_38121(-1.0f, -0.45f), MultiNoiseUtil.ParameterRange.method_38121(-0.45f, -0.15f), MultiNoiseUtil.ParameterRange.method_38121(-0.15f, 0.2f), MultiNoiseUtil.ParameterRange.method_38121(0.2f, 0.55f), MultiNoiseUtil.ParameterRange.method_38121(0.55f, 1.0f)};
    private final MultiNoiseUtil.ParameterRange[] HUMIDITY_PARAMETERS = new MultiNoiseUtil.ParameterRange[]{MultiNoiseUtil.ParameterRange.method_38121(-1.0f, -0.3f), MultiNoiseUtil.ParameterRange.method_38121(-0.3f, -0.1f), MultiNoiseUtil.ParameterRange.method_38121(-0.1f, 0.1f), MultiNoiseUtil.ParameterRange.method_38121(0.1f, 0.3f), MultiNoiseUtil.ParameterRange.method_38121(0.3f, 1.0f)};
    private final MultiNoiseUtil.ParameterRange[] EROSION_PARAMETERS = new MultiNoiseUtil.ParameterRange[]{MultiNoiseUtil.ParameterRange.method_38121(-1.0f, -0.78f), MultiNoiseUtil.ParameterRange.method_38121(-0.78f, -0.375f), MultiNoiseUtil.ParameterRange.method_38121(-0.375f, -0.2225f), MultiNoiseUtil.ParameterRange.method_38121(-0.2225f, 0.05f), MultiNoiseUtil.ParameterRange.method_38121(0.05f, 0.45f), MultiNoiseUtil.ParameterRange.method_38121(0.45f, 0.55f), MultiNoiseUtil.ParameterRange.method_38121(0.55f, 1.0f)};
    private final MultiNoiseUtil.ParameterRange FROZEN_TEMPERATURE = this.TEMPERATURE_PARAMETERS[0];
    private final MultiNoiseUtil.ParameterRange NON_FROZEN_TEMPERATURE_PARAMETERS = MultiNoiseUtil.ParameterRange.method_38123(this.TEMPERATURE_PARAMETERS[1], this.TEMPERATURE_PARAMETERS[4]);
    private final MultiNoiseUtil.ParameterRange MUSHROOM_FIELDS_CONTINENTALNESS = MultiNoiseUtil.ParameterRange.method_38121(-1.2f, -1.05f);
    private final MultiNoiseUtil.ParameterRange DEEP_OCEAN_CONTINENTALNESS = MultiNoiseUtil.ParameterRange.method_38121(-1.05f, -0.455f);
    private final MultiNoiseUtil.ParameterRange OCEAN_CONTINENTALNESS = MultiNoiseUtil.ParameterRange.method_38121(-0.455f, -0.19f);
    private final MultiNoiseUtil.ParameterRange SHORE_CONTINENTALNESS = MultiNoiseUtil.ParameterRange.method_38121(-0.19f, -0.11f);
    private final MultiNoiseUtil.ParameterRange RIVER_CONTINENTALNESS = MultiNoiseUtil.ParameterRange.method_38121(-0.11f, 0.55f);
    private final MultiNoiseUtil.ParameterRange NEAR_INLAND_CONTINENTALNESS = MultiNoiseUtil.ParameterRange.method_38121(-0.11f, 0.03f);
    private final MultiNoiseUtil.ParameterRange MID_INLAND_CONTINENTALNESS = MultiNoiseUtil.ParameterRange.method_38121(0.03f, 0.3f);
    private final MultiNoiseUtil.ParameterRange FAR_INLAND_CONTINENTALNESS = MultiNoiseUtil.ParameterRange.method_38121(0.3f, 1.0f);
    private final RegistryKey<Biome>[][] OCEAN_BIOMES = new RegistryKey[][]{{BiomeKeys.DEEP_FROZEN_OCEAN, BiomeKeys.DEEP_COLD_OCEAN, BiomeKeys.DEEP_OCEAN, BiomeKeys.DEEP_LUKEWARM_OCEAN, BiomeKeys.DEEP_WARM_OCEAN}, {BiomeKeys.FROZEN_OCEAN, BiomeKeys.COLD_OCEAN, BiomeKeys.OCEAN, BiomeKeys.LUKEWARM_OCEAN, BiomeKeys.WARM_OCEAN}};
    private final RegistryKey<Biome>[][] COMMON_BIOMES = new RegistryKey[][]{{BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TAIGA, BiomeKeys.SNOWY_TAIGA}, {BiomeKeys.PLAINS, BiomeKeys.PLAINS, BiomeKeys.FOREST, BiomeKeys.TAIGA, BiomeKeys.GIANT_SPRUCE_TAIGA}, {BiomeKeys.PLAINS, BiomeKeys.PLAINS, BiomeKeys.FOREST, BiomeKeys.BIRCH_FOREST, BiomeKeys.DARK_FOREST}, {BiomeKeys.SAVANNA, BiomeKeys.SAVANNA, BiomeKeys.FOREST, BiomeKeys.JUNGLE, BiomeKeys.JUNGLE}, {BiomeKeys.DESERT, BiomeKeys.DESERT, BiomeKeys.DESERT, BiomeKeys.JUNGLE_EDGE, BiomeKeys.JUNGLE}};
    private final RegistryKey<Biome>[][] UNCOMMON_BIOMES = new RegistryKey[][]{{BiomeKeys.ICE_SPIKES, null, BiomeKeys.SNOWY_TAIGA, null, null}, {null, null, null, null, BiomeKeys.GIANT_TREE_TAIGA}, {null, BiomeKeys.SUNFLOWER_PLAINS, BiomeKeys.FLOWER_FOREST, BiomeKeys.TALL_BIRCH_FOREST, null}, {null, null, BiomeKeys.PLAINS, BiomeKeys.PLAINS, null}, {null, null, null, BiomeKeys.PLAINS, BiomeKeys.BAMBOO_JUNGLE}};
    private final RegistryKey<Biome>[][] NEAR_MOUNTAIN_BIOMES = new RegistryKey[][]{{BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_TAIGA, BiomeKeys.SNOWY_TAIGA}, {BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.FOREST, BiomeKeys.TAIGA, BiomeKeys.GIANT_SPRUCE_TAIGA}, {BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.DARK_FOREST}, {BiomeKeys.SAVANNA_PLATEAU, BiomeKeys.SAVANNA_PLATEAU, BiomeKeys.FOREST, BiomeKeys.FOREST, BiomeKeys.JUNGLE}, {BiomeKeys.BADLANDS, BiomeKeys.BADLANDS, BiomeKeys.BADLANDS, BiomeKeys.WOODED_BADLANDS_PLATEAU, BiomeKeys.WOODED_BADLANDS_PLATEAU}};
    private final RegistryKey<Biome>[][] SPECIAL_NEAR_MOUNTAIN_BIOMES = new RegistryKey[][]{{BiomeKeys.ICE_SPIKES, null, null, null, null}, {null, null, BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.GIANT_TREE_TAIGA}, {null, null, BiomeKeys.FOREST, BiomeKeys.BIRCH_FOREST, null}, {null, null, null, null, null}, {BiomeKeys.ERODED_BADLANDS, BiomeKeys.ERODED_BADLANDS, null, null, null}};
    private final RegistryKey<Biome>[][] HILL_BIOMES = new RegistryKey[][]{{BiomeKeys.GRAVELLY_MOUNTAINS, BiomeKeys.GRAVELLY_MOUNTAINS, BiomeKeys.MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS}, {BiomeKeys.GRAVELLY_MOUNTAINS, BiomeKeys.GRAVELLY_MOUNTAINS, BiomeKeys.MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS}, {BiomeKeys.MOUNTAINS, BiomeKeys.MOUNTAINS, BiomeKeys.MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS}, {null, null, null, null, null}, {null, null, null, null, null}};

    protected void writeVanillaBiomeParameters(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer) {
        if (SharedConstants.DEBUG_BIOME_SOURCE) {
            new VanillaTerrainParameters().method_38215(consumer);
            return;
        }
        this.writeOceanBiomes(consumer);
        this.writeLandBiomes(consumer);
        this.writeCaveBiomes(consumer);
    }

    private void writeOceanBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer) {
        this.writeBiomeParameters(consumer, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, this.MUSHROOM_FIELDS_CONTINENTALNESS, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, 0.0f, BiomeKeys.MUSHROOM_FIELDS);
        for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; ++i) {
            MultiNoiseUtil.ParameterRange parameterRange = this.TEMPERATURE_PARAMETERS[i];
            this.writeBiomeParameters(consumer, parameterRange, this.DEFAULT_PARAMETER, this.DEEP_OCEAN_CONTINENTALNESS, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, 0.0f, this.OCEAN_BIOMES[0][i]);
            this.writeBiomeParameters(consumer, parameterRange, this.DEFAULT_PARAMETER, this.OCEAN_CONTINENTALNESS, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, 0.0f, this.OCEAN_BIOMES[1][i]);
        }
    }

    private void writeLandBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer) {
        this.writeMixedBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(-1.0f, -0.93333334f));
        this.writePlainBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(-0.93333334f, -0.7666667f));
        this.writeMountainousBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(-0.7666667f, -0.56666666f));
        this.writePlainBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(-0.56666666f, -0.4f));
        this.writeMixedBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(-0.4f, -0.26666668f));
        this.writeBiomesNearRivers(consumer, MultiNoiseUtil.ParameterRange.method_38121(-0.26666668f, -0.05f));
        this.writeRiverBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(-0.05f, 0.05f));
        this.writeBiomesNearRivers(consumer, MultiNoiseUtil.ParameterRange.method_38121(0.05f, 0.26666668f));
        this.writeMixedBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(0.26666668f, 0.4f));
        this.writePlainBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(0.4f, 0.56666666f));
        this.writeMountainousBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(0.56666666f, 0.7666667f));
        this.writePlainBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(0.7666667f, 0.93333334f));
        this.writeMixedBiomes(consumer, MultiNoiseUtil.ParameterRange.method_38121(0.93333334f, 1.0f));
    }

    private void writeMountainousBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer, MultiNoiseUtil.ParameterRange weirdness) {
        for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; ++i) {
            MultiNoiseUtil.ParameterRange parameterRange = this.TEMPERATURE_PARAMETERS[i];
            for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; ++j) {
                MultiNoiseUtil.ParameterRange parameterRange2 = this.HUMIDITY_PARAMETERS[j];
                RegistryKey<Biome> registryKey = this.getRegularBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey2 = this.getBadlandsOrRegularBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey3 = this.method_38194(i, j, weirdness);
                RegistryKey<Biome> registryKey4 = this.getNearMountainBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey5 = this.getHillBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey6 = this.method_38183(i, j, weirdness, registryKey5);
                RegistryKey<Biome> registryKey7 = this.getPeakBiome(i, j, weirdness);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[0], weirdness, 0.0f, registryKey7);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[1], weirdness, 0.0f, registryKey3);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[1], weirdness, 0.0f, registryKey7);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.NEAR_INLAND_CONTINENTALNESS, MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[3]), weirdness, 0.0f, registryKey);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[2], weirdness, 0.0f, registryKey4);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.MID_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[3], weirdness, 0.0f, registryKey2);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.FAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[3], weirdness, 0.0f, registryKey4);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[4], weirdness, 0.0f, registryKey);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.NEAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[5], weirdness, 0.0f, registryKey6);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[5], weirdness, 0.0f, registryKey5);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], weirdness, 0.0f, registryKey);
            }
        }
    }

    private void writePlainBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer, MultiNoiseUtil.ParameterRange weirdness) {
        for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; ++i) {
            MultiNoiseUtil.ParameterRange parameterRange = this.TEMPERATURE_PARAMETERS[i];
            for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; ++j) {
                MultiNoiseUtil.ParameterRange parameterRange2 = this.HUMIDITY_PARAMETERS[j];
                RegistryKey<Biome> registryKey = this.getRegularBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey2 = this.getBadlandsOrRegularBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey3 = this.method_38194(i, j, weirdness);
                RegistryKey<Biome> registryKey4 = this.getNearMountainBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey5 = this.getHillBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey6 = this.method_38183(i, j, weirdness, registryKey);
                RegistryKey<Biome> registryKey7 = this.getMountainSlopeBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey8 = this.getPeakBiome(i, j, weirdness);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]), weirdness, 0.0f, registryKey);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[0], weirdness, 0.0f, registryKey7);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[0], weirdness, 0.0f, registryKey8);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[1], weirdness, 0.0f, registryKey3);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[1], weirdness, 0.0f, registryKey7);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.NEAR_INLAND_CONTINENTALNESS), MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[3]), weirdness, 0.0f, registryKey);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[2], weirdness, 0.0f, registryKey4);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.MID_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[3], weirdness, 0.0f, registryKey2);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.FAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[3], weirdness, 0.0f, registryKey4);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[4], weirdness, 0.0f, registryKey);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.NEAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[5], weirdness, 0.0f, registryKey6);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[5], weirdness, 0.0f, registryKey5);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], weirdness, 0.0f, registryKey);
            }
        }
    }

    private void writeMixedBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer, MultiNoiseUtil.ParameterRange weirdness) {
        this.writeBiomeParameters(consumer, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, this.SHORE_CONTINENTALNESS, MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[2]), weirdness, 0.0f, BiomeKeys.STONE_SHORE);
        this.writeBiomeParameters(consumer, this.NON_FROZEN_TEMPERATURE_PARAMETERS, this.DEFAULT_PARAMETER, MultiNoiseUtil.ParameterRange.method_38123(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], weirdness, 0.0f, BiomeKeys.SWAMP);
        for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; ++i) {
            MultiNoiseUtil.ParameterRange parameterRange = this.TEMPERATURE_PARAMETERS[i];
            for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; ++j) {
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
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[0], weirdness, 0.0f, registryKey9);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.NEAR_INLAND_CONTINENTALNESS, this.MID_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[1], weirdness, 0.0f, registryKey3);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.FAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[1], weirdness, 0.0f, i == 0 ? registryKey9 : registryKey5);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[2], weirdness, 0.0f, registryKey);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.MID_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[2], weirdness, 0.0f, registryKey2);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.FAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[2], weirdness, 0.0f, registryKey5);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.NEAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[3], weirdness, 0.0f, registryKey);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[3], weirdness, 0.0f, registryKey2);
                if (weirdness.getMax() < 0.0f) {
                    this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[4], weirdness, 0.0f, registryKey6);
                    this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[4], weirdness, 0.0f, registryKey);
                } else {
                    this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[4], weirdness, 0.0f, registryKey);
                }
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[5], weirdness, 0.0f, registryKey8);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[5], weirdness, 0.0f, registryKey7);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[5], weirdness, 0.0f, registryKey4);
                if (weirdness.getMax() < 0.0f) {
                    this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[6], weirdness, 0.0f, registryKey6);
                } else {
                    this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[6], weirdness, 0.0f, registryKey);
                }
                if (i != 0) continue;
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], weirdness, 0.0f, registryKey);
            }
        }
    }

    private void writeBiomesNearRivers(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer, MultiNoiseUtil.ParameterRange weirdness) {
        this.writeBiomeParameters(consumer, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, this.SHORE_CONTINENTALNESS, MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[2]), weirdness, 0.0f, BiomeKeys.STONE_SHORE);
        this.writeBiomeParameters(consumer, this.NON_FROZEN_TEMPERATURE_PARAMETERS, this.DEFAULT_PARAMETER, MultiNoiseUtil.ParameterRange.method_38123(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], weirdness, 0.0f, BiomeKeys.SWAMP);
        for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; ++i) {
            MultiNoiseUtil.ParameterRange parameterRange = this.TEMPERATURE_PARAMETERS[i];
            for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; ++j) {
                MultiNoiseUtil.ParameterRange parameterRange2 = this.HUMIDITY_PARAMETERS[j];
                RegistryKey<Biome> registryKey = this.getRegularBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey2 = this.getBadlandsOrRegularBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey3 = this.method_38194(i, j, weirdness);
                RegistryKey<Biome> registryKey4 = this.method_38181(i, j);
                RegistryKey<Biome> registryKey5 = this.method_38183(i, j, weirdness, registryKey);
                RegistryKey<Biome> registryKey6 = this.method_38198(i, j, weirdness);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.NEAR_INLAND_CONTINENTALNESS, MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]), weirdness, 0.0f, registryKey2);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]), weirdness, 0.0f, registryKey3);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.NEAR_INLAND_CONTINENTALNESS, MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[3]), weirdness, 0.0f, registryKey);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[3]), weirdness, 0.0f, registryKey2);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[3], this.EROSION_PARAMETERS[4]), weirdness, 0.0f, registryKey4);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[4], weirdness, 0.0f, registryKey);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[5], weirdness, 0.0f, registryKey6);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.NEAR_INLAND_CONTINENTALNESS, this.EROSION_PARAMETERS[5], weirdness, 0.0f, registryKey5);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[5], weirdness, 0.0f, registryKey);
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[6], weirdness, 0.0f, registryKey4);
                if (i != 0) continue;
                this.writeBiomeParameters(consumer, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.method_38123(this.NEAR_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], weirdness, 0.0f, registryKey);
            }
        }
    }

    private void writeRiverBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer, MultiNoiseUtil.ParameterRange parameterRange) {
        this.writeBiomeParameters(consumer, this.FROZEN_TEMPERATURE, this.DEFAULT_PARAMETER, this.SHORE_CONTINENTALNESS, MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]), parameterRange, 0.0f, parameterRange.getMax() < 0.0f ? BiomeKeys.STONE_SHORE : BiomeKeys.FROZEN_RIVER);
        this.writeBiomeParameters(consumer, this.NON_FROZEN_TEMPERATURE_PARAMETERS, this.DEFAULT_PARAMETER, this.SHORE_CONTINENTALNESS, MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]), parameterRange, 0.0f, parameterRange.getMax() < 0.0f ? BiomeKeys.STONE_SHORE : BiomeKeys.RIVER);
        this.writeBiomeParameters(consumer, this.FROZEN_TEMPERATURE, this.DEFAULT_PARAMETER, this.NEAR_INLAND_CONTINENTALNESS, MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]), parameterRange, 0.0f, BiomeKeys.FROZEN_RIVER);
        this.writeBiomeParameters(consumer, this.NON_FROZEN_TEMPERATURE_PARAMETERS, this.DEFAULT_PARAMETER, this.NEAR_INLAND_CONTINENTALNESS, MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]), parameterRange, 0.0f, BiomeKeys.RIVER);
        this.writeBiomeParameters(consumer, this.FROZEN_TEMPERATURE, this.DEFAULT_PARAMETER, MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[5]), parameterRange, 0.0f, BiomeKeys.FROZEN_RIVER);
        this.writeBiomeParameters(consumer, this.NON_FROZEN_TEMPERATURE_PARAMETERS, this.DEFAULT_PARAMETER, MultiNoiseUtil.ParameterRange.method_38123(this.SHORE_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[2], this.EROSION_PARAMETERS[5]), parameterRange, 0.0f, BiomeKeys.RIVER);
        this.writeBiomeParameters(consumer, this.FROZEN_TEMPERATURE, this.DEFAULT_PARAMETER, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[6], parameterRange, 0.0f, BiomeKeys.FROZEN_RIVER);
        this.writeBiomeParameters(consumer, this.NON_FROZEN_TEMPERATURE_PARAMETERS, this.DEFAULT_PARAMETER, this.SHORE_CONTINENTALNESS, this.EROSION_PARAMETERS[6], parameterRange, 0.0f, BiomeKeys.RIVER);
        this.writeBiomeParameters(consumer, this.NON_FROZEN_TEMPERATURE_PARAMETERS, this.DEFAULT_PARAMETER, MultiNoiseUtil.ParameterRange.method_38123(this.RIVER_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], parameterRange, 0.0f, BiomeKeys.SWAMP);
        this.writeBiomeParameters(consumer, this.FROZEN_TEMPERATURE, this.DEFAULT_PARAMETER, MultiNoiseUtil.ParameterRange.method_38123(this.RIVER_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), this.EROSION_PARAMETERS[6], parameterRange, 0.0f, BiomeKeys.FROZEN_RIVER);
        for (int i = 0; i < this.TEMPERATURE_PARAMETERS.length; ++i) {
            MultiNoiseUtil.ParameterRange parameterRange2 = this.TEMPERATURE_PARAMETERS[i];
            for (int j = 0; j < this.HUMIDITY_PARAMETERS.length; ++j) {
                MultiNoiseUtil.ParameterRange parameterRange3 = this.HUMIDITY_PARAMETERS[j];
                RegistryKey<Biome> registryKey = this.getBadlandsOrRegularBiome(i, j, parameterRange);
                this.writeBiomeParameters(consumer, parameterRange2, parameterRange3, MultiNoiseUtil.ParameterRange.method_38123(this.MID_INLAND_CONTINENTALNESS, this.FAR_INLAND_CONTINENTALNESS), MultiNoiseUtil.ParameterRange.method_38123(this.EROSION_PARAMETERS[0], this.EROSION_PARAMETERS[1]), parameterRange, 0.0f, registryKey);
            }
        }
    }

    private void writeCaveBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer) {
        this.writeCaveBiomeParameters(consumer, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, MultiNoiseUtil.ParameterRange.method_38121(0.8f, 1.0f), this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, 0.0f, BiomeKeys.DRIPSTONE_CAVES);
        this.writeCaveBiomeParameters(consumer, this.DEFAULT_PARAMETER, MultiNoiseUtil.ParameterRange.method_38121(0.7f, 1.0f), this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, this.DEFAULT_PARAMETER, 0.0f, BiomeKeys.LUSH_CAVES);
    }

    private RegistryKey<Biome> getRegularBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
        if (weirdness.getMax() < 0.0f) {
            return this.COMMON_BIOMES[temperature][humidity];
        }
        RegistryKey<Biome> registryKey = this.UNCOMMON_BIOMES[temperature][humidity];
        return registryKey == null ? this.COMMON_BIOMES[temperature][humidity] : registryKey;
    }

    private RegistryKey<Biome> getBadlandsOrRegularBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
        return temperature == 4 ? this.getBadlandsBiome(humidity, weirdness) : this.getRegularBiome(temperature, humidity, weirdness);
    }

    private RegistryKey<Biome> method_38194(int i, int j, MultiNoiseUtil.ParameterRange parameterRange) {
        return i == 0 ? this.getMountainSlopeBiome(i, j, parameterRange) : this.getBadlandsOrRegularBiome(i, j, parameterRange);
    }

    private RegistryKey<Biome> method_38183(int i, int j, MultiNoiseUtil.ParameterRange parameterRange, RegistryKey<Biome> registryKey) {
        if (i > 1 && j < 4 && parameterRange.getMax() >= 0.0f) {
            return BiomeKeys.SHATTERED_SAVANNA;
        }
        return registryKey;
    }

    private RegistryKey<Biome> method_38198(int i, int j, MultiNoiseUtil.ParameterRange parameterRange) {
        RegistryKey<Biome> registryKey = parameterRange.getMax() >= 0.0f ? this.getRegularBiome(i, j, parameterRange) : this.method_38181(i, j);
        return this.method_38183(i, j, parameterRange, registryKey);
    }

    private RegistryKey<Biome> method_38181(int i, int j) {
        if (i == 0) {
            return BiomeKeys.SNOWY_BEACH;
        }
        if (i == 4 && j < 3) {
            return BiomeKeys.DESERT;
        }
        return BiomeKeys.BEACH;
    }

    private RegistryKey<Biome> getBadlandsBiome(int humidity, MultiNoiseUtil.ParameterRange weirdness) {
        if (humidity < 2) {
            return weirdness.getMax() < 0.0f ? BiomeKeys.ERODED_BADLANDS : BiomeKeys.BADLANDS;
        }
        if (humidity < 3) {
            return BiomeKeys.BADLANDS;
        }
        return BiomeKeys.WOODED_BADLANDS_PLATEAU;
    }

    private RegistryKey<Biome> getNearMountainBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
        if (weirdness.getMax() < 0.0f) {
            return this.NEAR_MOUNTAIN_BIOMES[temperature][humidity];
        }
        RegistryKey<Biome> registryKey = this.SPECIAL_NEAR_MOUNTAIN_BIOMES[temperature][humidity];
        return registryKey == null ? this.NEAR_MOUNTAIN_BIOMES[temperature][humidity] : registryKey;
    }

    private RegistryKey<Biome> getPeakBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
        if (temperature <= 2) {
            return weirdness.getMax() < 0.0f ? BiomeKeys.LOFTY_PEAKS : BiomeKeys.SNOWCAPPED_PEAKS;
        }
        if (temperature == 3) {
            return BiomeKeys.STONY_PEAKS;
        }
        return this.getBadlandsBiome(humidity, weirdness);
    }

    private RegistryKey<Biome> getMountainSlopeBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
        if (temperature >= 3) {
            return this.getNearMountainBiome(temperature, humidity, weirdness);
        }
        if (humidity <= 1) {
            return BiomeKeys.SNOWY_SLOPES;
        }
        return BiomeKeys.GROVE;
    }

    private RegistryKey<Biome> getHillBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
        RegistryKey<Biome> registryKey = this.HILL_BIOMES[temperature][humidity];
        return registryKey == null ? this.getRegularBiome(temperature, humidity, weirdness) : registryKey;
    }

    private void writeBiomeParameters(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer, MultiNoiseUtil.ParameterRange temperature, MultiNoiseUtil.ParameterRange humidity, MultiNoiseUtil.ParameterRange continentalness, MultiNoiseUtil.ParameterRange erosion, MultiNoiseUtil.ParameterRange weirdness, float offset, RegistryKey<Biome> biome) {
        consumer.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.method_38120(0.0f), weirdness, offset), biome));
        consumer.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.method_38120(1.0f), weirdness, offset), biome));
    }

    private void writeCaveBiomeParameters(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> consumer, MultiNoiseUtil.ParameterRange temperature, MultiNoiseUtil.ParameterRange humidity, MultiNoiseUtil.ParameterRange continentalness, MultiNoiseUtil.ParameterRange erosion, MultiNoiseUtil.ParameterRange weirdness, float offset, RegistryKey<Biome> biome) {
        consumer.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.method_38121(0.1f, 0.9f), weirdness, offset), biome));
    }

    public static String getWeirdnessDescription(double weirdness) {
        if (weirdness < (double)VanillaTerrainParameters.getNormalizedWeirdness(0.05f)) {
            return "Valley";
        }
        if (weirdness < (double)VanillaTerrainParameters.getNormalizedWeirdness(0.26666668f)) {
            return "Low";
        }
        if (weirdness < (double)VanillaTerrainParameters.getNormalizedWeirdness(0.4f)) {
            return "Mid";
        }
        if (weirdness < (double)VanillaTerrainParameters.getNormalizedWeirdness(0.56666666f)) {
            return "High";
        }
        return "Peak";
    }

    public String getContinentalnessDescription(double continentalness) {
        if (continentalness < (double)this.MUSHROOM_FIELDS_CONTINENTALNESS.getMax()) {
            return "Mushroom fields";
        }
        if (continentalness < (double)this.DEEP_OCEAN_CONTINENTALNESS.getMax()) {
            return "Deep ocean";
        }
        if (continentalness < (double)this.OCEAN_CONTINENTALNESS.getMax()) {
            return "Ocean";
        }
        if (continentalness < (double)this.SHORE_CONTINENTALNESS.getMax()) {
            return "Coast";
        }
        if (continentalness < (double)this.NEAR_INLAND_CONTINENTALNESS.getMax()) {
            return "Near inland";
        }
        if (continentalness < (double)this.MID_INLAND_CONTINENTALNESS.getMax()) {
            return "Mid inland";
        }
        return "Far inland";
    }

    public String getErosionDescription(double erosion) {
        return VanillaBiomeParameters.getNoiseRangeIndex(erosion, this.EROSION_PARAMETERS);
    }

    public String getTemperatureDescription(double temperature) {
        return VanillaBiomeParameters.getNoiseRangeIndex(temperature, this.TEMPERATURE_PARAMETERS);
    }

    public String getHumidityDescription(double humidity) {
        return VanillaBiomeParameters.getNoiseRangeIndex(humidity, this.HUMIDITY_PARAMETERS);
    }

    private static String getNoiseRangeIndex(double d, MultiNoiseUtil.ParameterRange[] parameterRanges) {
        for (int i = 0; i < parameterRanges.length; ++i) {
            if (!(d < (double)parameterRanges[i].getMax())) continue;
            return "" + i;
        }
        return "?";
    }
}

