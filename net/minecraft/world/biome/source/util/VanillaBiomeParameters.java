/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source.util;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.SharedConstants;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.function.ToFloatFunction;
import net.minecraft.util.math.Spline;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryWrapper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.VanillaTerrainParametersCreator;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.densityfunction.DensityFunctions;

public final class VanillaBiomeParameters {
    private static final float MAX_VALLEY_WEIRDNESS = 0.05f;
    private static final float MAX_LOW_WEIRDNESS = 0.26666668f;
    public static final float MAX_MID_WEIRDNESS = 0.4f;
    private static final float MAX_SECOND_HIGH_WEIRDNESS = 0.93333334f;
    private static final float field_34501 = 0.1f;
    public static final float MAX_HIGH_WEIRDNESS = 0.56666666f;
    private static final float MAX_PEAK_WEIRDNESS = 0.7666667f;
    public static final float field_35042 = -0.11f;
    public static final float field_35043 = 0.03f;
    public static final float field_35044 = 0.3f;
    public static final float field_35045 = -0.78f;
    public static final float field_35046 = -0.375f;
    private static final float field_39134 = -0.225f;
    private static final float field_39135 = 0.9f;
    private final MultiNoiseUtil.ParameterRange defaultParameter = MultiNoiseUtil.ParameterRange.of(-1.0f, 1.0f);
    private final MultiNoiseUtil.ParameterRange[] temperatureParameters = new MultiNoiseUtil.ParameterRange[]{MultiNoiseUtil.ParameterRange.of(-1.0f, -0.45f), MultiNoiseUtil.ParameterRange.of(-0.45f, -0.15f), MultiNoiseUtil.ParameterRange.of(-0.15f, 0.2f), MultiNoiseUtil.ParameterRange.of(0.2f, 0.55f), MultiNoiseUtil.ParameterRange.of(0.55f, 1.0f)};
    private final MultiNoiseUtil.ParameterRange[] humidityParameters = new MultiNoiseUtil.ParameterRange[]{MultiNoiseUtil.ParameterRange.of(-1.0f, -0.35f), MultiNoiseUtil.ParameterRange.of(-0.35f, -0.1f), MultiNoiseUtil.ParameterRange.of(-0.1f, 0.1f), MultiNoiseUtil.ParameterRange.of(0.1f, 0.3f), MultiNoiseUtil.ParameterRange.of(0.3f, 1.0f)};
    private final MultiNoiseUtil.ParameterRange[] erosionParameters = new MultiNoiseUtil.ParameterRange[]{MultiNoiseUtil.ParameterRange.of(-1.0f, -0.78f), MultiNoiseUtil.ParameterRange.of(-0.78f, -0.375f), MultiNoiseUtil.ParameterRange.of(-0.375f, -0.2225f), MultiNoiseUtil.ParameterRange.of(-0.2225f, 0.05f), MultiNoiseUtil.ParameterRange.of(0.05f, 0.45f), MultiNoiseUtil.ParameterRange.of(0.45f, 0.55f), MultiNoiseUtil.ParameterRange.of(0.55f, 1.0f)};
    private final MultiNoiseUtil.ParameterRange frozenTemperature = this.temperatureParameters[0];
    private final MultiNoiseUtil.ParameterRange nonFrozenTemperatureParameters = MultiNoiseUtil.ParameterRange.combine(this.temperatureParameters[1], this.temperatureParameters[4]);
    private final MultiNoiseUtil.ParameterRange mushroomFieldsContinentalness = MultiNoiseUtil.ParameterRange.of(-1.2f, -1.05f);
    private final MultiNoiseUtil.ParameterRange deepOceanContinentalness = MultiNoiseUtil.ParameterRange.of(-1.05f, -0.455f);
    private final MultiNoiseUtil.ParameterRange oceanContinentalness = MultiNoiseUtil.ParameterRange.of(-0.455f, -0.19f);
    private final MultiNoiseUtil.ParameterRange coastContinentalness = MultiNoiseUtil.ParameterRange.of(-0.19f, -0.11f);
    private final MultiNoiseUtil.ParameterRange riverContinentalness = MultiNoiseUtil.ParameterRange.of(-0.11f, 0.55f);
    private final MultiNoiseUtil.ParameterRange nearInlandContinentalness = MultiNoiseUtil.ParameterRange.of(-0.11f, 0.03f);
    private final MultiNoiseUtil.ParameterRange midInlandContinentalness = MultiNoiseUtil.ParameterRange.of(0.03f, 0.3f);
    private final MultiNoiseUtil.ParameterRange farInlandContinentalness = MultiNoiseUtil.ParameterRange.of(0.3f, 1.0f);
    private final RegistryKey<Biome>[][] oceanBiomes = new RegistryKey[][]{{BiomeKeys.DEEP_FROZEN_OCEAN, BiomeKeys.DEEP_COLD_OCEAN, BiomeKeys.DEEP_OCEAN, BiomeKeys.DEEP_LUKEWARM_OCEAN, BiomeKeys.WARM_OCEAN}, {BiomeKeys.FROZEN_OCEAN, BiomeKeys.COLD_OCEAN, BiomeKeys.OCEAN, BiomeKeys.LUKEWARM_OCEAN, BiomeKeys.WARM_OCEAN}};
    private final RegistryKey<Biome>[][] commonBiomes = new RegistryKey[][]{{BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_TAIGA, BiomeKeys.TAIGA}, {BiomeKeys.PLAINS, BiomeKeys.PLAINS, BiomeKeys.FOREST, BiomeKeys.TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA}, {BiomeKeys.FLOWER_FOREST, BiomeKeys.PLAINS, BiomeKeys.FOREST, BiomeKeys.BIRCH_FOREST, BiomeKeys.DARK_FOREST}, {BiomeKeys.SAVANNA, BiomeKeys.SAVANNA, BiomeKeys.FOREST, BiomeKeys.JUNGLE, BiomeKeys.JUNGLE}, {BiomeKeys.DESERT, BiomeKeys.DESERT, BiomeKeys.DESERT, BiomeKeys.DESERT, BiomeKeys.DESERT}};
    private final RegistryKey<Biome>[][] uncommonBiomes = new RegistryKey[][]{{BiomeKeys.ICE_SPIKES, null, BiomeKeys.SNOWY_TAIGA, null, null}, {null, null, null, null, BiomeKeys.OLD_GROWTH_PINE_TAIGA}, {BiomeKeys.SUNFLOWER_PLAINS, null, null, BiomeKeys.OLD_GROWTH_BIRCH_FOREST, null}, {null, null, BiomeKeys.PLAINS, BiomeKeys.SPARSE_JUNGLE, BiomeKeys.BAMBOO_JUNGLE}, {null, null, null, null, null}};
    private final RegistryKey<Biome>[][] nearMountainBiomes = new RegistryKey[][]{{BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_TAIGA, BiomeKeys.SNOWY_TAIGA}, {BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.FOREST, BiomeKeys.TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA}, {BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.DARK_FOREST}, {BiomeKeys.SAVANNA_PLATEAU, BiomeKeys.SAVANNA_PLATEAU, BiomeKeys.FOREST, BiomeKeys.FOREST, BiomeKeys.JUNGLE}, {BiomeKeys.BADLANDS, BiomeKeys.BADLANDS, BiomeKeys.BADLANDS, BiomeKeys.WOODED_BADLANDS, BiomeKeys.WOODED_BADLANDS}};
    private final RegistryKey<Biome>[][] specialNearMountainBiomes = new RegistryKey[][]{{BiomeKeys.ICE_SPIKES, null, null, null, null}, {null, null, BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.OLD_GROWTH_PINE_TAIGA}, {null, null, BiomeKeys.FOREST, BiomeKeys.BIRCH_FOREST, null}, {null, null, null, null, null}, {BiomeKeys.ERODED_BADLANDS, BiomeKeys.ERODED_BADLANDS, null, null, null}};
    private final RegistryKey<Biome>[][] windsweptBiomes = new RegistryKey[][]{{BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, BiomeKeys.WINDSWEPT_HILLS, BiomeKeys.WINDSWEPT_FOREST, BiomeKeys.WINDSWEPT_FOREST}, {BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, BiomeKeys.WINDSWEPT_HILLS, BiomeKeys.WINDSWEPT_FOREST, BiomeKeys.WINDSWEPT_FOREST}, {BiomeKeys.WINDSWEPT_HILLS, BiomeKeys.WINDSWEPT_HILLS, BiomeKeys.WINDSWEPT_HILLS, BiomeKeys.WINDSWEPT_FOREST, BiomeKeys.WINDSWEPT_FOREST}, {null, null, null, null, null}, {null, null, null, null, null}};

    public List<MultiNoiseUtil.NoiseHypercube> getSpawnSuitabilityNoises() {
        MultiNoiseUtil.ParameterRange parameterRange = MultiNoiseUtil.ParameterRange.of(0.0f);
        float f = 0.16f;
        return List.of(new MultiNoiseUtil.NoiseHypercube(this.defaultParameter, this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.riverContinentalness, this.defaultParameter), this.defaultParameter, parameterRange, MultiNoiseUtil.ParameterRange.of(-1.0f, -0.16f), 0L), new MultiNoiseUtil.NoiseHypercube(this.defaultParameter, this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.riverContinentalness, this.defaultParameter), this.defaultParameter, parameterRange, MultiNoiseUtil.ParameterRange.of(0.16f, 1.0f), 0L));
    }

    /**
     * Writes all biome parameters for the overworld to the passed parameter consumer.
     */
    protected void writeOverworldBiomeParameters(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters) {
        if (SharedConstants.DEBUG_BIOME_SOURCE) {
            this.writeDebug(parameters);
            return;
        }
        this.writeOceanBiomes(parameters);
        this.writeLandBiomes(parameters);
        this.writeCaveBiomes(parameters);
    }

    private void writeDebug(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters) {
        Spline spline2;
        RegistryWrapper.WrapperLookup wrapperLookup = BuiltinRegistries.createWrapperLookup();
        RegistryWrapper.Impl<DensityFunction> registryEntryLookup = wrapperLookup.getWrapperOrThrow(Registry.DENSITY_FUNCTION_KEY);
        DensityFunctionTypes.Spline.DensityFunctionWrapper densityFunctionWrapper = new DensityFunctionTypes.Spline.DensityFunctionWrapper(registryEntryLookup.getOrThrow(DensityFunctions.CONTINENTS_OVERWORLD));
        DensityFunctionTypes.Spline.DensityFunctionWrapper densityFunctionWrapper2 = new DensityFunctionTypes.Spline.DensityFunctionWrapper(registryEntryLookup.getOrThrow(DensityFunctions.EROSION_OVERWORLD));
        DensityFunctionTypes.Spline.DensityFunctionWrapper densityFunctionWrapper3 = new DensityFunctionTypes.Spline.DensityFunctionWrapper(registryEntryLookup.getOrThrow(DensityFunctions.RIDGES_FOLDED_OVERWORLD));
        parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(this.defaultParameter, this.defaultParameter, this.defaultParameter, this.defaultParameter, MultiNoiseUtil.ParameterRange.of(0.0f), this.defaultParameter, 0.01f), BiomeKeys.PLAINS));
        Spline spline = VanillaTerrainParametersCreator.createContinentalOffsetSpline(densityFunctionWrapper2, densityFunctionWrapper3, -0.15f, 0.0f, 0.0f, 0.1f, 0.0f, -0.03f, false, false, ToFloatFunction.IDENTITY);
        if (spline instanceof Spline.Implementation) {
            Spline.Implementation implementation = (Spline.Implementation)spline;
            RegistryKey<Biome> registryKey = BiomeKeys.DESERT;
            for (float f : implementation.locations()) {
                parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(this.defaultParameter, this.defaultParameter, this.defaultParameter, MultiNoiseUtil.ParameterRange.of(f), MultiNoiseUtil.ParameterRange.of(0.0f), this.defaultParameter, 0.0f), registryKey));
                registryKey = registryKey == BiomeKeys.DESERT ? BiomeKeys.BADLANDS : BiomeKeys.DESERT;
            }
        }
        if ((spline2 = VanillaTerrainParametersCreator.createOffsetSpline(densityFunctionWrapper, densityFunctionWrapper2, densityFunctionWrapper3, false)) instanceof Spline.Implementation) {
            Spline.Implementation implementation2 = (Spline.Implementation)spline2;
            for (float f : implementation2.locations()) {
                parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(this.defaultParameter, this.defaultParameter, MultiNoiseUtil.ParameterRange.of(f), this.defaultParameter, MultiNoiseUtil.ParameterRange.of(0.0f), this.defaultParameter, 0.0f), BiomeKeys.SNOWY_TAIGA));
            }
        }
    }

    /**
     * Writes all parameters for ocean biomes.
     * This includes oceans, deep oceans and mushroom fields.
     */
    private void writeOceanBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters) {
        this.writeBiomeParameters(parameters, this.defaultParameter, this.defaultParameter, this.mushroomFieldsContinentalness, this.defaultParameter, this.defaultParameter, 0.0f, BiomeKeys.MUSHROOM_FIELDS);
        for (int i = 0; i < this.temperatureParameters.length; ++i) {
            MultiNoiseUtil.ParameterRange parameterRange = this.temperatureParameters[i];
            this.writeBiomeParameters(parameters, parameterRange, this.defaultParameter, this.deepOceanContinentalness, this.defaultParameter, this.defaultParameter, 0.0f, this.oceanBiomes[0][i]);
            this.writeBiomeParameters(parameters, parameterRange, this.defaultParameter, this.oceanContinentalness, this.defaultParameter, this.defaultParameter, 0.0f, this.oceanBiomes[1][i]);
        }
    }

    /**
     * Writes all parameters for land biomes.
     * This means that {@code continentalness} is greater than about {@code -0.16} for them.
     */
    private void writeLandBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters) {
        this.writeMidBiomes(parameters, MultiNoiseUtil.ParameterRange.of(-1.0f, -0.93333334f));
        this.writeHighBiomes(parameters, MultiNoiseUtil.ParameterRange.of(-0.93333334f, -0.7666667f));
        this.writePeakBiomes(parameters, MultiNoiseUtil.ParameterRange.of(-0.7666667f, -0.56666666f));
        this.writeHighBiomes(parameters, MultiNoiseUtil.ParameterRange.of(-0.56666666f, -0.4f));
        this.writeMidBiomes(parameters, MultiNoiseUtil.ParameterRange.of(-0.4f, -0.26666668f));
        this.writeLowBiomes(parameters, MultiNoiseUtil.ParameterRange.of(-0.26666668f, -0.05f));
        this.writeValleyBiomes(parameters, MultiNoiseUtil.ParameterRange.of(-0.05f, 0.05f));
        this.writeLowBiomes(parameters, MultiNoiseUtil.ParameterRange.of(0.05f, 0.26666668f));
        this.writeMidBiomes(parameters, MultiNoiseUtil.ParameterRange.of(0.26666668f, 0.4f));
        this.writeHighBiomes(parameters, MultiNoiseUtil.ParameterRange.of(0.4f, 0.56666666f));
        this.writePeakBiomes(parameters, MultiNoiseUtil.ParameterRange.of(0.56666666f, 0.7666667f));
        this.writeHighBiomes(parameters, MultiNoiseUtil.ParameterRange.of(0.7666667f, 0.93333334f));
        this.writeMidBiomes(parameters, MultiNoiseUtil.ParameterRange.of(0.93333334f, 1.0f));
    }

    /**
     * Writes biome parameters for the "peak" weirdness range.
     * 
     * These can be regular biomes for higher erosion values or near the coast,
     * biomes that are usually near mountains (like plateaus or mountain slopes), or,
     * for lower erosion values, peak biomes like jagged, frozen or stony peaks.
     */
    private void writePeakBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness) {
        for (int i = 0; i < this.temperatureParameters.length; ++i) {
            MultiNoiseUtil.ParameterRange parameterRange = this.temperatureParameters[i];
            for (int j = 0; j < this.humidityParameters.length; ++j) {
                MultiNoiseUtil.ParameterRange parameterRange2 = this.humidityParameters[j];
                RegistryKey<Biome> registryKey = this.getRegularBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey2 = this.getBadlandsOrRegularBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey3 = this.getMountainStartBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey4 = this.getNearMountainBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey5 = this.getWindsweptOrRegularBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey6 = this.getBiomeOrWindsweptSavanna(i, j, weirdness, registryKey5);
                RegistryKey<Biome> registryKey7 = this.getPeakBiome(i, j, weirdness);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.farInlandContinentalness), this.erosionParameters[0], weirdness, 0.0f, registryKey7);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.nearInlandContinentalness), this.erosionParameters[1], weirdness, 0.0f, registryKey3);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[1], weirdness, 0.0f, registryKey7);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.nearInlandContinentalness), MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[2], this.erosionParameters[3]), weirdness, 0.0f, registryKey);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[2], weirdness, 0.0f, registryKey4);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.midInlandContinentalness, this.erosionParameters[3], weirdness, 0.0f, registryKey2);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.farInlandContinentalness, this.erosionParameters[3], weirdness, 0.0f, registryKey4);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.farInlandContinentalness), this.erosionParameters[4], weirdness, 0.0f, registryKey);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.nearInlandContinentalness), this.erosionParameters[5], weirdness, 0.0f, registryKey6);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[5], weirdness, 0.0f, registryKey5);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0f, registryKey);
            }
        }
    }

    /**
     * Writes biome parameters for the "high" weirdness range.
     * 
     * These can be regular biomes for higher erosion values or near the coast,
     * biomes that usually appear near mountains, like meadows or slopes,
     * or, rarely (for very low erosion and high continentalness values), peak biomes.
     */
    private void writeHighBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness) {
        for (int i = 0; i < this.temperatureParameters.length; ++i) {
            MultiNoiseUtil.ParameterRange parameterRange = this.temperatureParameters[i];
            for (int j = 0; j < this.humidityParameters.length; ++j) {
                MultiNoiseUtil.ParameterRange parameterRange2 = this.humidityParameters[j];
                RegistryKey<Biome> registryKey = this.getRegularBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey2 = this.getBadlandsOrRegularBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey3 = this.getMountainStartBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey4 = this.getNearMountainBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey5 = this.getWindsweptOrRegularBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey6 = this.getBiomeOrWindsweptSavanna(i, j, weirdness, registryKey);
                RegistryKey<Biome> registryKey7 = this.getMountainSlopeBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey8 = this.getPeakBiome(i, j, weirdness);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.coastContinentalness, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[1]), weirdness, 0.0f, registryKey);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.nearInlandContinentalness, this.erosionParameters[0], weirdness, 0.0f, registryKey7);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[0], weirdness, 0.0f, registryKey8);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.nearInlandContinentalness, this.erosionParameters[1], weirdness, 0.0f, registryKey3);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[1], weirdness, 0.0f, registryKey7);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.nearInlandContinentalness), MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[2], this.erosionParameters[3]), weirdness, 0.0f, registryKey);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[2], weirdness, 0.0f, registryKey4);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.midInlandContinentalness, this.erosionParameters[3], weirdness, 0.0f, registryKey2);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.farInlandContinentalness, this.erosionParameters[3], weirdness, 0.0f, registryKey4);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.farInlandContinentalness), this.erosionParameters[4], weirdness, 0.0f, registryKey);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.nearInlandContinentalness), this.erosionParameters[5], weirdness, 0.0f, registryKey6);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[5], weirdness, 0.0f, registryKey5);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0f, registryKey);
            }
        }
    }

    /**
     * Writes biome parameters for the "mid" weirdness range.
     * 
     * These will be regular biomes in most cases, but can also be shore biomes near the coast,
     * or the start of a mountain biome for very low erosion.
     */
    private void writeMidBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness) {
        this.writeBiomeParameters(parameters, this.defaultParameter, this.defaultParameter, this.coastContinentalness, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[2]), weirdness, 0.0f, BiomeKeys.STONY_SHORE);
        this.writeBiomeParameters(parameters, MultiNoiseUtil.ParameterRange.combine(this.temperatureParameters[1], this.temperatureParameters[2]), this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0f, BiomeKeys.SWAMP);
        this.writeBiomeParameters(parameters, MultiNoiseUtil.ParameterRange.combine(this.temperatureParameters[3], this.temperatureParameters[4]), this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0f, BiomeKeys.MANGROVE_SWAMP);
        for (int i = 0; i < this.temperatureParameters.length; ++i) {
            MultiNoiseUtil.ParameterRange parameterRange = this.temperatureParameters[i];
            for (int j = 0; j < this.humidityParameters.length; ++j) {
                MultiNoiseUtil.ParameterRange parameterRange2 = this.humidityParameters[j];
                RegistryKey<Biome> registryKey = this.getRegularBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey2 = this.getBadlandsOrRegularBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey3 = this.getMountainStartBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey4 = this.getWindsweptOrRegularBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey5 = this.getNearMountainBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey6 = this.getShoreBiome(i, j);
                RegistryKey<Biome> registryKey7 = this.getBiomeOrWindsweptSavanna(i, j, weirdness, registryKey);
                RegistryKey<Biome> registryKey8 = this.getErodedShoreBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey9 = this.getMountainSlopeBiome(i, j, weirdness);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[0], weirdness, 0.0f, registryKey9);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.midInlandContinentalness), this.erosionParameters[1], weirdness, 0.0f, registryKey3);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.farInlandContinentalness, this.erosionParameters[1], weirdness, 0.0f, i == 0 ? registryKey9 : registryKey5);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.nearInlandContinentalness, this.erosionParameters[2], weirdness, 0.0f, registryKey);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.midInlandContinentalness, this.erosionParameters[2], weirdness, 0.0f, registryKey2);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.farInlandContinentalness, this.erosionParameters[2], weirdness, 0.0f, registryKey5);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.nearInlandContinentalness), this.erosionParameters[3], weirdness, 0.0f, registryKey);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[3], weirdness, 0.0f, registryKey2);
                if (weirdness.max() < 0L) {
                    this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.coastContinentalness, this.erosionParameters[4], weirdness, 0.0f, registryKey6);
                    this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[4], weirdness, 0.0f, registryKey);
                } else {
                    this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.farInlandContinentalness), this.erosionParameters[4], weirdness, 0.0f, registryKey);
                }
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.coastContinentalness, this.erosionParameters[5], weirdness, 0.0f, registryKey8);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.nearInlandContinentalness, this.erosionParameters[5], weirdness, 0.0f, registryKey7);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[5], weirdness, 0.0f, registryKey4);
                if (weirdness.max() < 0L) {
                    this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.coastContinentalness, this.erosionParameters[6], weirdness, 0.0f, registryKey6);
                } else {
                    this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.coastContinentalness, this.erosionParameters[6], weirdness, 0.0f, registryKey);
                }
                if (i != 0) continue;
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0f, registryKey);
            }
        }
    }

    /**
     * Writes biome parameters for the "low" weirdness range.
     * 
     * These will be regular biomes in most cases,
     * but can also be shore biomes near the coast,
     * or swamps for very high erosion.
     */
    private void writeLowBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness) {
        this.writeBiomeParameters(parameters, this.defaultParameter, this.defaultParameter, this.coastContinentalness, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[2]), weirdness, 0.0f, BiomeKeys.STONY_SHORE);
        this.writeBiomeParameters(parameters, MultiNoiseUtil.ParameterRange.combine(this.temperatureParameters[1], this.temperatureParameters[2]), this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0f, BiomeKeys.SWAMP);
        this.writeBiomeParameters(parameters, MultiNoiseUtil.ParameterRange.combine(this.temperatureParameters[3], this.temperatureParameters[4]), this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0f, BiomeKeys.MANGROVE_SWAMP);
        for (int i = 0; i < this.temperatureParameters.length; ++i) {
            MultiNoiseUtil.ParameterRange parameterRange = this.temperatureParameters[i];
            for (int j = 0; j < this.humidityParameters.length; ++j) {
                MultiNoiseUtil.ParameterRange parameterRange2 = this.humidityParameters[j];
                RegistryKey<Biome> registryKey = this.getRegularBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey2 = this.getBadlandsOrRegularBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey3 = this.getMountainStartBiome(i, j, weirdness);
                RegistryKey<Biome> registryKey4 = this.getShoreBiome(i, j);
                RegistryKey<Biome> registryKey5 = this.getBiomeOrWindsweptSavanna(i, j, weirdness, registryKey);
                RegistryKey<Biome> registryKey6 = this.getErodedShoreBiome(i, j, weirdness);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.nearInlandContinentalness, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[1]), weirdness, 0.0f, registryKey2);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[1]), weirdness, 0.0f, registryKey3);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.nearInlandContinentalness, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[2], this.erosionParameters[3]), weirdness, 0.0f, registryKey);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[2], this.erosionParameters[3]), weirdness, 0.0f, registryKey2);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.coastContinentalness, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[3], this.erosionParameters[4]), weirdness, 0.0f, registryKey4);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[4], weirdness, 0.0f, registryKey);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.coastContinentalness, this.erosionParameters[5], weirdness, 0.0f, registryKey6);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.nearInlandContinentalness, this.erosionParameters[5], weirdness, 0.0f, registryKey5);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[5], weirdness, 0.0f, registryKey);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.coastContinentalness, this.erosionParameters[6], weirdness, 0.0f, registryKey4);
                if (i != 0) continue;
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0f, registryKey);
            }
        }
    }

    /**
     * Writes biome parameters for the "valley" weirdness range.
     * 
     * In most cases, a valley will be a river. In low temperatures, a river can also be frozen.
     * Valleys that go through a swamp will remain a swamp biome.
     * 
     * Mountain ranges can also sometimes have valleys that are not a river,
     * in which case this method will pick a regular or badlands biome.
     */
    private void writeValleyBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness) {
        this.writeBiomeParameters(parameters, this.frozenTemperature, this.defaultParameter, this.coastContinentalness, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[1]), weirdness, 0.0f, weirdness.max() < 0L ? BiomeKeys.STONY_SHORE : BiomeKeys.FROZEN_RIVER);
        this.writeBiomeParameters(parameters, this.nonFrozenTemperatureParameters, this.defaultParameter, this.coastContinentalness, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[1]), weirdness, 0.0f, weirdness.max() < 0L ? BiomeKeys.STONY_SHORE : BiomeKeys.RIVER);
        this.writeBiomeParameters(parameters, this.frozenTemperature, this.defaultParameter, this.nearInlandContinentalness, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[1]), weirdness, 0.0f, BiomeKeys.FROZEN_RIVER);
        this.writeBiomeParameters(parameters, this.nonFrozenTemperatureParameters, this.defaultParameter, this.nearInlandContinentalness, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[1]), weirdness, 0.0f, BiomeKeys.RIVER);
        this.writeBiomeParameters(parameters, this.frozenTemperature, this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.farInlandContinentalness), MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[2], this.erosionParameters[5]), weirdness, 0.0f, BiomeKeys.FROZEN_RIVER);
        this.writeBiomeParameters(parameters, this.nonFrozenTemperatureParameters, this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.farInlandContinentalness), MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[2], this.erosionParameters[5]), weirdness, 0.0f, BiomeKeys.RIVER);
        this.writeBiomeParameters(parameters, this.frozenTemperature, this.defaultParameter, this.coastContinentalness, this.erosionParameters[6], weirdness, 0.0f, BiomeKeys.FROZEN_RIVER);
        this.writeBiomeParameters(parameters, this.nonFrozenTemperatureParameters, this.defaultParameter, this.coastContinentalness, this.erosionParameters[6], weirdness, 0.0f, BiomeKeys.RIVER);
        this.writeBiomeParameters(parameters, MultiNoiseUtil.ParameterRange.combine(this.temperatureParameters[1], this.temperatureParameters[2]), this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.riverContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0f, BiomeKeys.SWAMP);
        this.writeBiomeParameters(parameters, MultiNoiseUtil.ParameterRange.combine(this.temperatureParameters[3], this.temperatureParameters[4]), this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.riverContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0f, BiomeKeys.MANGROVE_SWAMP);
        this.writeBiomeParameters(parameters, this.frozenTemperature, this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.riverContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0f, BiomeKeys.FROZEN_RIVER);
        for (int i = 0; i < this.temperatureParameters.length; ++i) {
            MultiNoiseUtil.ParameterRange parameterRange = this.temperatureParameters[i];
            for (int j = 0; j < this.humidityParameters.length; ++j) {
                MultiNoiseUtil.ParameterRange parameterRange2 = this.humidityParameters[j];
                RegistryKey<Biome> registryKey = this.getBadlandsOrRegularBiome(i, j, weirdness);
                this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[1]), weirdness, 0.0f, registryKey);
            }
        }
    }

    /**
     * Writes biome parameters for all cave biomes.
     * Currently, this only consists of dripstone caves, lush caves, and the deep dark.
     * 
     * Dripstone caves can generate anywhere where there are high high continentalness values.
     * Lush caves can generate anywhere at high humidity values.
     * 
     * The deep dark can generate anywhere at low erosion values, which usually means
     * it will be near mountains.
     */
    private void writeCaveBiomes(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters) {
        this.writeCaveBiomeParameters(parameters, this.defaultParameter, this.defaultParameter, MultiNoiseUtil.ParameterRange.of(0.8f, 1.0f), this.defaultParameter, this.defaultParameter, 0.0f, BiomeKeys.DRIPSTONE_CAVES);
        this.writeCaveBiomeParameters(parameters, this.defaultParameter, MultiNoiseUtil.ParameterRange.of(0.7f, 1.0f), this.defaultParameter, this.defaultParameter, this.defaultParameter, 0.0f, BiomeKeys.LUSH_CAVES);
        this.writeDeepDarkParameters(parameters, this.defaultParameter, this.defaultParameter, this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[1]), this.defaultParameter, 0.0f, BiomeKeys.DEEP_DARK);
    }

    /**
     * {@return a regular biome.} This can be a {@linkplain #commonBiomes common} or {@linkplain #uncommonBiomes uncommon} biome, depending on
     * temperature and humidity.
     * 
     * Note that for negative weirdness values, only common biomes can get picked by this
     * method.
     */
    private RegistryKey<Biome> getRegularBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
        if (weirdness.max() < 0L) {
            return this.commonBiomes[temperature][humidity];
        }
        RegistryKey<Biome> registryKey = this.uncommonBiomes[temperature][humidity];
        return registryKey == null ? this.commonBiomes[temperature][humidity] : registryKey;
    }

    /**
     * {@return badlands if {@code temperature} is {@code 4}, otherwise a regular biome}.
     * 
     * @see #getRegularBiome
     */
    private RegistryKey<Biome> getBadlandsOrRegularBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
        return temperature == 4 ? this.getBadlandsBiome(humidity, weirdness) : this.getRegularBiome(temperature, humidity, weirdness);
    }

    /**
     * {@return a slope biome if {@code temperature} is {@code 0}, otherwise a regular biome}.
     * 
     * @see #getMountainSlopeBiome
     * @see getBadlandsOrRegularBiome
     */
    private RegistryKey<Biome> getMountainStartBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
        return temperature == 0 ? this.getMountainSlopeBiome(temperature, humidity, weirdness) : this.getBadlandsOrRegularBiome(temperature, humidity, weirdness);
    }

    /**
     * {@return a windswept savanna for specific conditions, otherwise the given biome}.
     * 
     * For a windswept savanna being returned by this method, {@code temperature} must be
     * greater than {@code 1}, {@code humidity} must be less than {@code 4} and
     * {@code weirdness} must be positive.
     */
    private RegistryKey<Biome> getBiomeOrWindsweptSavanna(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness, RegistryKey<Biome> biomeKey) {
        if (temperature > 1 && humidity < 4 && weirdness.max() >= 0L) {
            return BiomeKeys.WINDSWEPT_SAVANNA;
        }
        return biomeKey;
    }

    /**
     * {@return a shore biome for high erosion values}.
     * 
     * If {@code weirdness} is positive, this will be a regular biome.
     * For some specific conditions, this can also be a windswept savanna.
     * 
     * @see #getShoreBiome
     * @see #getRegularBiome
     * @see #getBiomeOrWindsweptSavanna
     */
    private RegistryKey<Biome> getErodedShoreBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
        RegistryKey<Biome> registryKey = weirdness.max() >= 0L ? this.getRegularBiome(temperature, humidity, weirdness) : this.getShoreBiome(temperature, humidity);
        return this.getBiomeOrWindsweptSavanna(temperature, humidity, weirdness, registryKey);
    }

    /**
     * {@return an appropriate shore biome for the given temperature and humidity}.
     */
    private RegistryKey<Biome> getShoreBiome(int temperature, int humidity) {
        if (temperature == 0) {
            return BiomeKeys.SNOWY_BEACH;
        }
        if (temperature == 4) {
            return BiomeKeys.DESERT;
        }
        return BiomeKeys.BEACH;
    }

    /**
     * {@return a badlands for the given humidity and weirdness}.
     */
    private RegistryKey<Biome> getBadlandsBiome(int humidity, MultiNoiseUtil.ParameterRange weirdness) {
        if (humidity < 2) {
            return weirdness.max() < 0L ? BiomeKeys.BADLANDS : BiomeKeys.ERODED_BADLANDS;
        }
        if (humidity < 3) {
            return BiomeKeys.BADLANDS;
        }
        return BiomeKeys.WOODED_BADLANDS;
    }

    /**
     * {@return a biome to generate near mountains.}
     * This can be a {@linkplain #nearMountainBiomes normal} or
     * {@linkplain #specialNearMountainBiomes special} biome, depending on
     * temperature and humidity.
     * 
     * Note that for negative weirdness values, no special biomes can get picked by this method.
     */
    private RegistryKey<Biome> getNearMountainBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
        if (weirdness.max() < 0L) {
            return this.nearMountainBiomes[temperature][humidity];
        }
        RegistryKey<Biome> registryKey = this.specialNearMountainBiomes[temperature][humidity];
        return registryKey == null ? this.nearMountainBiomes[temperature][humidity] : registryKey;
    }

    /**
     * {@return a peak biome for the given temperature, humidity and weirdness}.
     */
    private RegistryKey<Biome> getPeakBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
        if (temperature <= 2) {
            return weirdness.max() < 0L ? BiomeKeys.JAGGED_PEAKS : BiomeKeys.FROZEN_PEAKS;
        }
        if (temperature == 3) {
            return BiomeKeys.STONY_PEAKS;
        }
        return this.getBadlandsBiome(humidity, weirdness);
    }

    /**
     * {@return a mountain slope biome for the given temperature, humidity and weirdness}
     * 
     * @see #getNearMountainBiome
     */
    private RegistryKey<Biome> getMountainSlopeBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
        if (temperature >= 3) {
            return this.getNearMountainBiome(temperature, humidity, weirdness);
        }
        if (humidity <= 1) {
            return BiomeKeys.SNOWY_SLOPES;
        }
        return BiomeKeys.GROVE;
    }

    /**
     * {@return a windswept or regular biome, depending on temperature and humidity}.
     * 
     * @see #getRegularBiome
     */
    private RegistryKey<Biome> getWindsweptOrRegularBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
        RegistryKey<Biome> registryKey = this.windsweptBiomes[temperature][humidity];
        return registryKey == null ? this.getRegularBiome(temperature, humidity, weirdness) : registryKey;
    }

    private void writeBiomeParameters(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange temperature, MultiNoiseUtil.ParameterRange humidity, MultiNoiseUtil.ParameterRange continentalness, MultiNoiseUtil.ParameterRange erosion, MultiNoiseUtil.ParameterRange weirdness, float offset, RegistryKey<Biome> biome) {
        parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.of(0.0f), weirdness, offset), biome));
        parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.of(1.0f), weirdness, offset), biome));
    }

    private void writeCaveBiomeParameters(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange temperature, MultiNoiseUtil.ParameterRange humidity, MultiNoiseUtil.ParameterRange continentalness, MultiNoiseUtil.ParameterRange erosion, MultiNoiseUtil.ParameterRange weirdness, float offset, RegistryKey<Biome> biome) {
        parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.of(0.2f, 0.9f), weirdness, offset), biome));
    }

    private void writeDeepDarkParameters(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange temperature, MultiNoiseUtil.ParameterRange humidity, MultiNoiseUtil.ParameterRange continentalness, MultiNoiseUtil.ParameterRange erosion, MultiNoiseUtil.ParameterRange weirdness, float offset, RegistryKey<Biome> biome) {
        parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.of(1.1f), weirdness, offset), biome));
    }

    public static boolean method_43718(DensityFunction densityFunction, DensityFunction densityFunction2, DensityFunction.NoisePos noisePos) {
        return densityFunction.sample(noisePos) < (double)-0.225f && densityFunction2.sample(noisePos) > (double)0.9f;
    }

    public static String getPeaksValleysDescription(double weirdness) {
        if (weirdness < (double)DensityFunctions.getPeaksValleysNoise(0.05f)) {
            return "Valley";
        }
        if (weirdness < (double)DensityFunctions.getPeaksValleysNoise(0.26666668f)) {
            return "Low";
        }
        if (weirdness < (double)DensityFunctions.getPeaksValleysNoise(0.4f)) {
            return "Mid";
        }
        if (weirdness < (double)DensityFunctions.getPeaksValleysNoise(0.56666666f)) {
            return "High";
        }
        return "Peak";
    }

    public String getContinentalnessDescription(double continentalness) {
        double d = MultiNoiseUtil.toLong((float)continentalness);
        if (d < (double)this.mushroomFieldsContinentalness.max()) {
            return "Mushroom fields";
        }
        if (d < (double)this.deepOceanContinentalness.max()) {
            return "Deep ocean";
        }
        if (d < (double)this.oceanContinentalness.max()) {
            return "Ocean";
        }
        if (d < (double)this.coastContinentalness.max()) {
            return "Coast";
        }
        if (d < (double)this.nearInlandContinentalness.max()) {
            return "Near inland";
        }
        if (d < (double)this.midInlandContinentalness.max()) {
            return "Mid inland";
        }
        return "Far inland";
    }

    public String getErosionDescription(double erosion) {
        return VanillaBiomeParameters.getNoiseRangeIndex(erosion, this.erosionParameters);
    }

    public String getTemperatureDescription(double temperature) {
        return VanillaBiomeParameters.getNoiseRangeIndex(temperature, this.temperatureParameters);
    }

    public String getHumidityDescription(double humidity) {
        return VanillaBiomeParameters.getNoiseRangeIndex(humidity, this.humidityParameters);
    }

    private static String getNoiseRangeIndex(double noisePoint, MultiNoiseUtil.ParameterRange[] noiseRanges) {
        double d = MultiNoiseUtil.toLong((float)noisePoint);
        for (int i = 0; i < noiseRanges.length; ++i) {
            if (!(d < (double)noiseRanges[i].max())) continue;
            return "" + i;
        }
        return "?";
    }

    @Debug
    public MultiNoiseUtil.ParameterRange[] getTemperatureParameters() {
        return this.temperatureParameters;
    }

    @Debug
    public MultiNoiseUtil.ParameterRange[] getHumidityParameters() {
        return this.humidityParameters;
    }

    @Debug
    public MultiNoiseUtil.ParameterRange[] getErosionParameters() {
        return this.erosionParameters;
    }

    @Debug
    public MultiNoiseUtil.ParameterRange[] getContinentalnessParameters() {
        return new MultiNoiseUtil.ParameterRange[]{this.mushroomFieldsContinentalness, this.deepOceanContinentalness, this.oceanContinentalness, this.coastContinentalness, this.nearInlandContinentalness, this.midInlandContinentalness, this.farInlandContinentalness};
    }

    @Debug
    public MultiNoiseUtil.ParameterRange[] getWeirdnessParameters() {
        return new MultiNoiseUtil.ParameterRange[]{MultiNoiseUtil.ParameterRange.of(-2.0f, DensityFunctions.getPeaksValleysNoise(0.05f)), MultiNoiseUtil.ParameterRange.of(DensityFunctions.getPeaksValleysNoise(0.05f), DensityFunctions.getPeaksValleysNoise(0.26666668f)), MultiNoiseUtil.ParameterRange.of(DensityFunctions.getPeaksValleysNoise(0.26666668f), DensityFunctions.getPeaksValleysNoise(0.4f)), MultiNoiseUtil.ParameterRange.of(DensityFunctions.getPeaksValleysNoise(0.4f), DensityFunctions.getPeaksValleysNoise(0.56666666f)), MultiNoiseUtil.ParameterRange.of(DensityFunctions.getPeaksValleysNoise(0.56666666f), 2.0f)};
    }

    @Debug
    public MultiNoiseUtil.ParameterRange[] method_40015() {
        return new MultiNoiseUtil.ParameterRange[]{MultiNoiseUtil.ParameterRange.of(-2.0f, 0.0f), MultiNoiseUtil.ParameterRange.of(0.0f, 2.0f)};
    }
}

