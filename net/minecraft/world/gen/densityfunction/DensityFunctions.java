/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.densityfunction;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import net.minecraft.world.biome.source.util.VanillaTerrainParameters;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.OreVeinSampler;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.noise.NoiseRouter;
import net.minecraft.world.gen.noise.SimpleNoiseRouter;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomDeriver;

public class DensityFunctions {
    private static final float field_36614 = 0.08f;
    private static final double field_36615 = 1.5;
    private static final double field_36616 = 1.5;
    private static final double field_36617 = 1.5625;
    private static final DensityFunction field_36618 = DensityFunctionTypes.constant(10.0);
    private static final DensityFunction field_36619 = DensityFunctionTypes.zero();
    private static final RegistryKey<DensityFunction> ZERO = DensityFunctions.of("zero");
    private static final RegistryKey<DensityFunction> Y = DensityFunctions.of("y");
    private static final RegistryKey<DensityFunction> SHIFT_X = DensityFunctions.of("shift_x");
    private static final RegistryKey<DensityFunction> SHIFT_Z = DensityFunctions.of("shift_z");
    private static final RegistryKey<DensityFunction> BASE_3D_NOISE_OVERWORLD = DensityFunctions.of("overworld/base_3d_noise");
    private static final RegistryKey<DensityFunction> CONTINENTS_OVERWORLD = DensityFunctions.of("overworld/continents");
    private static final RegistryKey<DensityFunction> EROSION_OVERWORLD = DensityFunctions.of("overworld/erosion");
    private static final RegistryKey<DensityFunction> RIDGES_OVERWORLD = DensityFunctions.of("overworld/ridges");
    private static final RegistryKey<DensityFunction> FACTOR_OVERWORLD = DensityFunctions.of("overworld/factor");
    private static final RegistryKey<DensityFunction> DEPTH_OVERWORLD = DensityFunctions.of("overworld/depth");
    private static final RegistryKey<DensityFunction> SLOPED_CHEESE_OVERWORLD = DensityFunctions.of("overworld/sloped_cheese");
    private static final RegistryKey<DensityFunction> CONTINENTS_OVERWORLD_LARGE_BIOME = DensityFunctions.of("overworld_large_biomes/continents");
    private static final RegistryKey<DensityFunction> EROSION_OVERWORLD_LARGE_BIOME = DensityFunctions.of("overworld_large_biomes/erosion");
    private static final RegistryKey<DensityFunction> FACTOR_OVERWORLD_LARGE_BIOME = DensityFunctions.of("overworld_large_biomes/factor");
    private static final RegistryKey<DensityFunction> DEPTH_OVERWORLD_LARGE_BIOME = DensityFunctions.of("overworld_large_biomes/depth");
    private static final RegistryKey<DensityFunction> SLOPED_CHEESE_OVERWORLD_LARGE_BIOME = DensityFunctions.of("overworld_large_biomes/sloped_cheese");
    private static final RegistryKey<DensityFunction> SLOPED_CHEESE_END = DensityFunctions.of("end/sloped_cheese");
    private static final RegistryKey<DensityFunction> CAVES_SPAGHETTI_ROUGHNESS_FUNCTION_OVERWORLD = DensityFunctions.of("overworld/caves/spaghetti_roughness_function");
    private static final RegistryKey<DensityFunction> CAVES_ENTRANCES_OVERWORLD = DensityFunctions.of("overworld/caves/entrances");
    private static final RegistryKey<DensityFunction> CAVES_NOODLE_OVERWORLD = DensityFunctions.of("overworld/caves/noodle");
    private static final RegistryKey<DensityFunction> CAVES_PILLARS_OVERWORLD = DensityFunctions.of("overworld/caves/pillars");
    private static final RegistryKey<DensityFunction> CAVES_SPAGHETTI_2D_THICKNESS_MODULATOR_OVERWORLD = DensityFunctions.of("overworld/caves/spaghetti_2d_thickness_modulator");
    private static final RegistryKey<DensityFunction> CAVES_SPAGHETTI_2D_OVERWORLD = DensityFunctions.of("overworld/caves/spaghetti_2d");

    protected static SimpleNoiseRouter method_41103(GenerationShapeConfig generationShapeConfig, boolean bl) {
        return DensityFunctions.method_41209(generationShapeConfig, bl);
    }

    private static RegistryKey<DensityFunction> of(String id) {
        return RegistryKey.of(Registry.DENSITY_FUNCTION_KEY, new Identifier(id));
    }

    public static RegistryEntry<? extends DensityFunction> init() {
        DensityFunctions.method_41112(ZERO, DensityFunctionTypes.zero());
        int i = DimensionType.MIN_HEIGHT * 2;
        int j = DimensionType.MAX_COLUMN_HEIGHT * 2;
        DensityFunctions.method_41112(Y, DensityFunctionTypes.yClampedGradient(i, j, i, j));
        DensityFunction densityFunction = DensityFunctions.method_41112(SHIFT_X, DensityFunctionTypes.flatCache(DensityFunctionTypes.cache2d(DensityFunctionTypes.shiftA(DensityFunctions.method_41111(NoiseParametersKeys.OFFSET)))));
        DensityFunction densityFunction2 = DensityFunctions.method_41112(SHIFT_Z, DensityFunctionTypes.flatCache(DensityFunctionTypes.cache2d(DensityFunctionTypes.shiftB(DensityFunctions.method_41111(NoiseParametersKeys.OFFSET)))));
        DensityFunctions.method_41112(BASE_3D_NOISE_OVERWORLD, InterpolatedNoiseSampler.field_37205);
        DensityFunction densityFunction3 = DensityFunctions.method_41112(CONTINENTS_OVERWORLD, DensityFunctionTypes.flatCache(DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, DensityFunctions.method_41111(NoiseParametersKeys.CONTINENTALNESS))));
        DensityFunction densityFunction4 = DensityFunctions.method_41112(EROSION_OVERWORLD, DensityFunctionTypes.flatCache(DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, DensityFunctions.method_41111(NoiseParametersKeys.EROSION))));
        DensityFunction densityFunction5 = DensityFunctions.method_41112(RIDGES_OVERWORLD, DensityFunctionTypes.flatCache(DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, DensityFunctions.method_41111(NoiseParametersKeys.RIDGE))));
        DensityFunction densityFunction6 = DensityFunctionTypes.method_40502(DensityFunctions.method_41111(NoiseParametersKeys.JAGGED), 1500.0, 0.0);
        DensityFunction densityFunction7 = DensityFunctions.method_40541(densityFunction3, densityFunction4, densityFunction5, DensityFunctionTypes.TerrainShaperSpline.Spline.OFFSET, -0.81, 2.5, DensityFunctionTypes.blendOffset());
        DensityFunction densityFunction8 = DensityFunctions.method_41112(FACTOR_OVERWORLD, DensityFunctions.method_40541(densityFunction3, densityFunction4, densityFunction5, DensityFunctionTypes.TerrainShaperSpline.Spline.FACTOR, 0.0, 8.0, field_36618));
        DensityFunction densityFunction9 = DensityFunctions.method_41112(DEPTH_OVERWORLD, DensityFunctionTypes.method_40486(DensityFunctionTypes.yClampedGradient(-64, 320, 1.5, -1.5), densityFunction7));
        DensityFunctions.method_41112(SLOPED_CHEESE_OVERWORLD, DensityFunctions.method_41102(densityFunction3, densityFunction4, densityFunction5, densityFunction8, densityFunction9, densityFunction6));
        DensityFunction densityFunction10 = DensityFunctions.method_41112(CONTINENTS_OVERWORLD_LARGE_BIOME, DensityFunctionTypes.flatCache(DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, DensityFunctions.method_41111(NoiseParametersKeys.CONTINENTALNESS_LARGE))));
        DensityFunction densityFunction11 = DensityFunctions.method_41112(EROSION_OVERWORLD_LARGE_BIOME, DensityFunctionTypes.flatCache(DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, DensityFunctions.method_41111(NoiseParametersKeys.EROSION_LARGE))));
        DensityFunction densityFunction12 = DensityFunctions.method_40541(densityFunction10, densityFunction11, densityFunction5, DensityFunctionTypes.TerrainShaperSpline.Spline.OFFSET, -0.81, 2.5, DensityFunctionTypes.blendOffset());
        DensityFunction densityFunction13 = DensityFunctions.method_41112(FACTOR_OVERWORLD_LARGE_BIOME, DensityFunctions.method_40541(densityFunction10, densityFunction11, densityFunction5, DensityFunctionTypes.TerrainShaperSpline.Spline.FACTOR, 0.0, 8.0, field_36618));
        DensityFunction densityFunction14 = DensityFunctions.method_41112(DEPTH_OVERWORLD_LARGE_BIOME, DensityFunctionTypes.method_40486(DensityFunctionTypes.yClampedGradient(-64, 320, 1.5, -1.5), densityFunction12));
        DensityFunctions.method_41112(SLOPED_CHEESE_OVERWORLD_LARGE_BIOME, DensityFunctions.method_41102(densityFunction10, densityFunction11, densityFunction5, densityFunction13, densityFunction14, densityFunction6));
        DensityFunctions.method_41112(SLOPED_CHEESE_END, DensityFunctionTypes.method_40486(DensityFunctionTypes.endIslands(0L), DensityFunctions.method_41116(BASE_3D_NOISE_OVERWORLD)));
        DensityFunctions.method_41112(CAVES_SPAGHETTI_ROUGHNESS_FUNCTION_OVERWORLD, DensityFunctions.method_41113());
        DensityFunctions.method_41112(CAVES_SPAGHETTI_2D_THICKNESS_MODULATOR_OVERWORLD, DensityFunctionTypes.cacheOnce(DensityFunctionTypes.noise(DensityFunctions.method_41111(NoiseParametersKeys.SPAGHETTI_2D_THICKNESS), 2.0, 1.0, -0.6, -1.3)));
        DensityFunctions.method_41112(CAVES_SPAGHETTI_2D_OVERWORLD, DensityFunctions.method_41122());
        DensityFunctions.method_41112(CAVES_ENTRANCES_OVERWORLD, DensityFunctions.method_41117());
        DensityFunctions.method_41112(CAVES_NOODLE_OVERWORLD, DensityFunctions.method_41119());
        DensityFunctions.method_41112(CAVES_PILLARS_OVERWORLD, DensityFunctions.method_41121());
        return (RegistryEntry)BuiltinRegistries.DENSITY_FUNCTION.streamEntries().iterator().next();
    }

    private static DensityFunction method_41112(RegistryKey<DensityFunction> registryKey, DensityFunction densityFunction) {
        return new DensityFunctionTypes.class_7051(BuiltinRegistries.add(BuiltinRegistries.DENSITY_FUNCTION, registryKey, densityFunction));
    }

    private static RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> method_41111(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey) {
        return BuiltinRegistries.NOISE_PARAMETERS.entryOf(registryKey);
    }

    private static DensityFunction method_41116(RegistryKey<DensityFunction> registryKey) {
        return new DensityFunctionTypes.class_7051(BuiltinRegistries.DENSITY_FUNCTION.entryOf(registryKey));
    }

    private static DensityFunction method_41102(DensityFunction densityFunction, DensityFunction densityFunction2, DensityFunction densityFunction3, DensityFunction densityFunction4, DensityFunction densityFunction5, DensityFunction densityFunction6) {
        DensityFunction densityFunction7 = DensityFunctions.method_40541(densityFunction, densityFunction2, densityFunction3, DensityFunctionTypes.TerrainShaperSpline.Spline.JAGGEDNESS, 0.0, 1.28, field_36619);
        DensityFunction densityFunction8 = DensityFunctionTypes.method_40500(densityFunction7, densityFunction6.halfNegative());
        DensityFunction densityFunction9 = DensityFunctions.method_40540(densityFunction4, DensityFunctionTypes.method_40486(densityFunction5, densityFunction8));
        return DensityFunctionTypes.method_40486(densityFunction9, DensityFunctions.method_41116(BASE_3D_NOISE_OVERWORLD));
    }

    private static DensityFunction method_41113() {
        DensityFunction densityFunction = DensityFunctionTypes.noise(DensityFunctions.method_41111(NoiseParametersKeys.SPAGHETTI_ROUGHNESS));
        DensityFunction densityFunction2 = DensityFunctionTypes.noise(DensityFunctions.method_41111(NoiseParametersKeys.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0, -0.1);
        return DensityFunctionTypes.cacheOnce(DensityFunctionTypes.method_40500(densityFunction2, DensityFunctionTypes.method_40486(densityFunction.abs(), DensityFunctionTypes.constant(-0.4))));
    }

    private static DensityFunction method_41117() {
        DensityFunction densityFunction = DensityFunctionTypes.cacheOnce(DensityFunctionTypes.method_40502(DensityFunctions.method_41111(NoiseParametersKeys.SPAGHETTI_3D_RARITY), 2.0, 1.0));
        DensityFunction densityFunction2 = DensityFunctionTypes.noise(DensityFunctions.method_41111(NoiseParametersKeys.SPAGHETTI_3D_THICKNESS), -0.065, -0.088);
        DensityFunction densityFunction3 = DensityFunctionTypes.weirdScaledSampler(densityFunction, DensityFunctions.method_41111(NoiseParametersKeys.SPAGHETTI_3D_1), DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper.TYPE1);
        DensityFunction densityFunction4 = DensityFunctionTypes.weirdScaledSampler(densityFunction, DensityFunctions.method_41111(NoiseParametersKeys.SPAGHETTI_3D_2), DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper.TYPE1);
        DensityFunction densityFunction5 = DensityFunctionTypes.method_40486(DensityFunctionTypes.method_40508(densityFunction3, densityFunction4), densityFunction2).clamp(-1.0, 1.0);
        DensityFunction densityFunction6 = DensityFunctions.method_41116(CAVES_SPAGHETTI_ROUGHNESS_FUNCTION_OVERWORLD);
        DensityFunction densityFunction7 = DensityFunctionTypes.method_40502(DensityFunctions.method_41111(NoiseParametersKeys.CAVE_ENTRANCE), 0.75, 0.5);
        DensityFunction densityFunction8 = DensityFunctionTypes.method_40486(DensityFunctionTypes.method_40486(densityFunction7, DensityFunctionTypes.constant(0.37)), DensityFunctionTypes.yClampedGradient(-10, 30, 0.3, 0.0));
        return DensityFunctionTypes.cacheOnce(DensityFunctionTypes.method_40505(densityFunction8, DensityFunctionTypes.method_40486(densityFunction6, densityFunction5)));
    }

    private static DensityFunction method_41119() {
        DensityFunction densityFunction = DensityFunctions.method_41116(Y);
        int i = -64;
        int j = -60;
        int k = 320;
        DensityFunction densityFunction2 = DensityFunctions.method_40539(densityFunction, DensityFunctionTypes.method_40502(DensityFunctions.method_41111(NoiseParametersKeys.NOODLE), 1.0, 1.0), -60, 320, -1);
        DensityFunction densityFunction3 = DensityFunctions.method_40539(densityFunction, DensityFunctionTypes.noise(DensityFunctions.method_41111(NoiseParametersKeys.NOODLE_THICKNESS), 1.0, 1.0, -0.05, -0.1), -60, 320, 0);
        double d = 2.6666666666666665;
        DensityFunction densityFunction4 = DensityFunctions.method_40539(densityFunction, DensityFunctionTypes.method_40502(DensityFunctions.method_41111(NoiseParametersKeys.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665), -60, 320, 0);
        DensityFunction densityFunction5 = DensityFunctions.method_40539(densityFunction, DensityFunctionTypes.method_40502(DensityFunctions.method_41111(NoiseParametersKeys.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665), -60, 320, 0);
        DensityFunction densityFunction6 = DensityFunctionTypes.method_40500(DensityFunctionTypes.constant(1.5), DensityFunctionTypes.method_40508(densityFunction4.abs(), densityFunction5.abs()));
        return DensityFunctionTypes.rangeChoice(densityFunction2, -1000000.0, 0.0, DensityFunctionTypes.constant(64.0), DensityFunctionTypes.method_40486(densityFunction3, densityFunction6));
    }

    private static DensityFunction method_41121() {
        double d = 25.0;
        double e = 0.3;
        DensityFunction densityFunction = DensityFunctionTypes.method_40502(DensityFunctions.method_41111(NoiseParametersKeys.PILLAR), 25.0, 0.3);
        DensityFunction densityFunction2 = DensityFunctionTypes.noise(DensityFunctions.method_41111(NoiseParametersKeys.PILLAR_RARENESS), 0.0, -2.0);
        DensityFunction densityFunction3 = DensityFunctionTypes.noise(DensityFunctions.method_41111(NoiseParametersKeys.PILLAR_THICKNESS), 0.0, 1.1);
        DensityFunction densityFunction4 = DensityFunctionTypes.method_40486(DensityFunctionTypes.method_40500(densityFunction, DensityFunctionTypes.constant(2.0)), densityFunction2);
        return DensityFunctionTypes.cacheOnce(DensityFunctionTypes.method_40500(densityFunction4, densityFunction3.cube()));
    }

    private static DensityFunction method_41122() {
        DensityFunction densityFunction = DensityFunctionTypes.method_40502(DensityFunctions.method_41111(NoiseParametersKeys.SPAGHETTI_2D_MODULATOR), 2.0, 1.0);
        DensityFunction densityFunction2 = DensityFunctionTypes.weirdScaledSampler(densityFunction, DensityFunctions.method_41111(NoiseParametersKeys.SPAGHETTI_2D), DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper.TYPE2);
        DensityFunction densityFunction3 = DensityFunctionTypes.noise(DensityFunctions.method_41111(NoiseParametersKeys.SPAGHETTI_2D_ELEVATION), 0.0, Math.floorDiv(-64, 8), 8.0);
        DensityFunction densityFunction4 = DensityFunctions.method_41116(CAVES_SPAGHETTI_2D_THICKNESS_MODULATOR_OVERWORLD);
        DensityFunction densityFunction5 = DensityFunctionTypes.method_40486(densityFunction3, DensityFunctionTypes.yClampedGradient(-64, 320, 8.0, -40.0)).abs();
        DensityFunction densityFunction6 = DensityFunctionTypes.method_40486(densityFunction5, densityFunction4).cube();
        double d = 0.083;
        DensityFunction densityFunction7 = DensityFunctionTypes.method_40486(densityFunction2, DensityFunctionTypes.method_40500(DensityFunctionTypes.constant(0.083), densityFunction4));
        return DensityFunctionTypes.method_40508(densityFunction7, densityFunction6).clamp(-1.0, 1.0);
    }

    private static DensityFunction method_41101(DensityFunction densityFunction) {
        DensityFunction densityFunction2 = DensityFunctions.method_41116(CAVES_SPAGHETTI_2D_OVERWORLD);
        DensityFunction densityFunction3 = DensityFunctions.method_41116(CAVES_SPAGHETTI_ROUGHNESS_FUNCTION_OVERWORLD);
        DensityFunction densityFunction4 = DensityFunctionTypes.noise(DensityFunctions.method_41111(NoiseParametersKeys.CAVE_LAYER), 8.0);
        DensityFunction densityFunction5 = DensityFunctionTypes.method_40500(DensityFunctionTypes.constant(4.0), densityFunction4.square());
        DensityFunction densityFunction6 = DensityFunctionTypes.noise(DensityFunctions.method_41111(NoiseParametersKeys.CAVE_CHEESE), 0.6666666666666666);
        DensityFunction densityFunction7 = DensityFunctionTypes.method_40486(DensityFunctionTypes.method_40486(DensityFunctionTypes.constant(0.27), densityFunction6).clamp(-1.0, 1.0), DensityFunctionTypes.method_40486(DensityFunctionTypes.constant(1.5), DensityFunctionTypes.method_40500(DensityFunctionTypes.constant(-0.64), densityFunction)).clamp(0.0, 0.5));
        DensityFunction densityFunction8 = DensityFunctionTypes.method_40486(densityFunction5, densityFunction7);
        DensityFunction densityFunction9 = DensityFunctionTypes.method_40505(DensityFunctionTypes.method_40505(densityFunction8, DensityFunctions.method_41116(CAVES_ENTRANCES_OVERWORLD)), DensityFunctionTypes.method_40486(densityFunction2, densityFunction3));
        DensityFunction densityFunction10 = DensityFunctions.method_41116(CAVES_PILLARS_OVERWORLD);
        DensityFunction densityFunction11 = DensityFunctionTypes.rangeChoice(densityFunction10, -1000000.0, 0.03, DensityFunctionTypes.constant(-1000000.0), densityFunction10);
        return DensityFunctionTypes.method_40508(densityFunction9, densityFunction11);
    }

    private static DensityFunction method_41207(GenerationShapeConfig generationShapeConfig, DensityFunction densityFunction) {
        DensityFunction densityFunction2 = DensityFunctionTypes.slide(generationShapeConfig, densityFunction);
        DensityFunction densityFunction3 = DensityFunctionTypes.blendDensity(densityFunction2);
        return DensityFunctionTypes.method_40500(DensityFunctionTypes.interpolated(densityFunction3), DensityFunctionTypes.constant(0.64)).squeeze();
    }

    private static SimpleNoiseRouter method_41209(GenerationShapeConfig generationShapeConfig, boolean bl) {
        DensityFunction densityFunction = DensityFunctionTypes.noise(DensityFunctions.method_41111(NoiseParametersKeys.AQUIFER_BARRIER), 0.5);
        DensityFunction densityFunction2 = DensityFunctionTypes.noise(DensityFunctions.method_41111(NoiseParametersKeys.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67);
        DensityFunction densityFunction3 = DensityFunctionTypes.noise(DensityFunctions.method_41111(NoiseParametersKeys.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
        DensityFunction densityFunction4 = DensityFunctionTypes.noise(DensityFunctions.method_41111(NoiseParametersKeys.AQUIFER_LAVA));
        DensityFunction densityFunction5 = DensityFunctions.method_41116(SHIFT_X);
        DensityFunction densityFunction6 = DensityFunctions.method_41116(SHIFT_Z);
        DensityFunction densityFunction7 = DensityFunctionTypes.shiftedNoise(densityFunction5, densityFunction6, 0.25, DensityFunctions.method_41111(bl ? NoiseParametersKeys.TEMPERATURE_LARGE : NoiseParametersKeys.TEMPERATURE));
        DensityFunction densityFunction8 = DensityFunctionTypes.shiftedNoise(densityFunction5, densityFunction6, 0.25, DensityFunctions.method_41111(bl ? NoiseParametersKeys.VEGETATION_LARGE : NoiseParametersKeys.VEGETATION));
        DensityFunction densityFunction9 = DensityFunctions.method_41116(bl ? FACTOR_OVERWORLD_LARGE_BIOME : FACTOR_OVERWORLD);
        DensityFunction densityFunction10 = DensityFunctions.method_41116(bl ? DEPTH_OVERWORLD_LARGE_BIOME : DEPTH_OVERWORLD);
        DensityFunction densityFunction11 = DensityFunctions.method_40540(DensityFunctionTypes.cache2d(densityFunction9), densityFunction10);
        DensityFunction densityFunction12 = DensityFunctions.method_41116(bl ? SLOPED_CHEESE_OVERWORLD_LARGE_BIOME : SLOPED_CHEESE_OVERWORLD);
        DensityFunction densityFunction13 = DensityFunctionTypes.method_40505(densityFunction12, DensityFunctionTypes.method_40500(DensityFunctionTypes.constant(5.0), DensityFunctions.method_41116(CAVES_ENTRANCES_OVERWORLD)));
        DensityFunction densityFunction14 = DensityFunctionTypes.rangeChoice(densityFunction12, -1000000.0, 1.5625, densityFunction13, DensityFunctions.method_41101(densityFunction12));
        DensityFunction densityFunction15 = DensityFunctionTypes.method_40505(DensityFunctions.method_41207(generationShapeConfig, densityFunction14), DensityFunctions.method_41116(CAVES_NOODLE_OVERWORLD));
        DensityFunction densityFunction16 = DensityFunctions.method_41116(Y);
        int i = generationShapeConfig.minimumY();
        int j = Stream.of(OreVeinSampler.VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(i);
        int k = Stream.of(OreVeinSampler.VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(i);
        DensityFunction densityFunction17 = DensityFunctions.method_40539(densityFunction16, DensityFunctionTypes.method_40502(DensityFunctions.method_41111(NoiseParametersKeys.ORE_VEININESS), 1.5, 1.5), j, k, 0);
        float f = 4.0f;
        DensityFunction densityFunction18 = DensityFunctions.method_40539(densityFunction16, DensityFunctionTypes.method_40502(DensityFunctions.method_41111(NoiseParametersKeys.ORE_VEIN_A), 4.0, 4.0), j, k, 0).abs();
        DensityFunction densityFunction19 = DensityFunctions.method_40539(densityFunction16, DensityFunctionTypes.method_40502(DensityFunctions.method_41111(NoiseParametersKeys.ORE_VEIN_B), 4.0, 4.0), j, k, 0).abs();
        DensityFunction densityFunction20 = DensityFunctionTypes.method_40486(DensityFunctionTypes.constant(-0.08f), DensityFunctionTypes.method_40508(densityFunction18, densityFunction19));
        DensityFunction densityFunction21 = DensityFunctionTypes.noise(DensityFunctions.method_41111(NoiseParametersKeys.ORE_GAP));
        return new SimpleNoiseRouter(densityFunction, densityFunction2, densityFunction3, densityFunction4, densityFunction7, densityFunction8, DensityFunctions.method_41116(bl ? CONTINENTS_OVERWORLD_LARGE_BIOME : CONTINENTS_OVERWORLD), DensityFunctions.method_41116(bl ? EROSION_OVERWORLD_LARGE_BIOME : EROSION_OVERWORLD), DensityFunctions.method_41116(bl ? DEPTH_OVERWORLD_LARGE_BIOME : DEPTH_OVERWORLD), DensityFunctions.method_41116(RIDGES_OVERWORLD), densityFunction11, densityFunction15, densityFunction17, densityFunction20, densityFunction21);
    }

    private static SimpleNoiseRouter method_41211(GenerationShapeConfig generationShapeConfig) {
        DensityFunction densityFunction = DensityFunctions.method_41116(SHIFT_X);
        DensityFunction densityFunction2 = DensityFunctions.method_41116(SHIFT_Z);
        DensityFunction densityFunction3 = DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, DensityFunctions.method_41111(NoiseParametersKeys.TEMPERATURE));
        DensityFunction densityFunction4 = DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, DensityFunctions.method_41111(NoiseParametersKeys.VEGETATION));
        DensityFunction densityFunction5 = DensityFunctions.method_40540(DensityFunctionTypes.cache2d(DensityFunctions.method_41116(FACTOR_OVERWORLD)), DensityFunctions.method_41116(DEPTH_OVERWORLD));
        DensityFunction densityFunction6 = DensityFunctions.method_41207(generationShapeConfig, DensityFunctions.method_41116(SLOPED_CHEESE_OVERWORLD));
        return new SimpleNoiseRouter(DensityFunctionTypes.zero(), DensityFunctionTypes.zero(), DensityFunctionTypes.zero(), DensityFunctionTypes.zero(), densityFunction3, densityFunction4, DensityFunctions.method_41116(CONTINENTS_OVERWORLD), DensityFunctions.method_41116(EROSION_OVERWORLD), DensityFunctions.method_41116(DEPTH_OVERWORLD), DensityFunctions.method_41116(RIDGES_OVERWORLD), densityFunction5, densityFunction6, DensityFunctionTypes.zero(), DensityFunctionTypes.zero(), DensityFunctionTypes.zero());
    }

    protected static SimpleNoiseRouter method_41114(GenerationShapeConfig generationShapeConfig) {
        return DensityFunctions.method_41211(generationShapeConfig);
    }

    protected static SimpleNoiseRouter method_41118(GenerationShapeConfig generationShapeConfig) {
        return DensityFunctions.method_41211(generationShapeConfig);
    }

    protected static SimpleNoiseRouter method_41120(GenerationShapeConfig generationShapeConfig) {
        DensityFunction densityFunction = DensityFunctionTypes.cache2d(DensityFunctionTypes.endIslands(0L));
        DensityFunction densityFunction2 = DensityFunctions.method_41207(generationShapeConfig, DensityFunctions.method_41116(SLOPED_CHEESE_END));
        return new SimpleNoiseRouter(DensityFunctionTypes.zero(), DensityFunctionTypes.zero(), DensityFunctionTypes.zero(), DensityFunctionTypes.zero(), DensityFunctionTypes.zero(), DensityFunctionTypes.zero(), DensityFunctionTypes.zero(), DensityFunctionTypes.zero(), DensityFunctionTypes.zero(), DensityFunctionTypes.zero(), densityFunction, densityFunction2, DensityFunctionTypes.zero(), DensityFunctionTypes.zero(), DensityFunctionTypes.zero());
    }

    private static DoublePerlinNoiseSampler method_41107(RandomDeriver randomDeriver, Registry<DoublePerlinNoiseSampler.NoiseParameters> registry, RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry) {
        return NoiseParametersKeys.method_41127(randomDeriver, registryEntry.getKey().flatMap(registry::getEntry).orElse(registryEntry));
    }

    public static NoiseRouter method_40544(GenerationShapeConfig generationShapeConfig, long l, Registry<DoublePerlinNoiseSampler.NoiseParameters> registry, ChunkRandom.RandomProvider randomProvider, SimpleNoiseRouter simpleNoiseRouter) {
        boolean bl = randomProvider == ChunkRandom.RandomProvider.LEGACY;
        RandomDeriver randomDeriver = randomProvider.create(l).createRandomDeriver();
        HashMap map = new HashMap();
        DensityFunction.DensityFunctionVisitor densityFunctionVisitor = densityFunction -> {
            if (densityFunction instanceof DensityFunctionTypes.Noise) {
                DensityFunctionTypes.Noise noise = (DensityFunctionTypes.Noise)densityFunction;
                RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry = noise.noiseData();
                return new DensityFunctionTypes.Noise(registryEntry, DensityFunctions.method_41107(randomDeriver, registry, registryEntry), noise.xzScale(), noise.yScale());
            }
            if (densityFunction instanceof DensityFunctionTypes.class_6939) {
                DensityFunctionTypes.class_6939 lv = (DensityFunctionTypes.class_6939)densityFunction;
                RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry2 = lv.noiseData();
                DoublePerlinNoiseSampler doublePerlinNoiseSampler = bl ? DoublePerlinNoiseSampler.create(randomDeriver.createRandom(NoiseParametersKeys.OFFSET.getValue()), new DoublePerlinNoiseSampler.NoiseParameters(0, 0.0, new double[0])) : DensityFunctions.method_41107(randomDeriver, registry, registryEntry2);
                return lv.method_41086(doublePerlinNoiseSampler);
            }
            if (densityFunction instanceof DensityFunctionTypes.ShiftedNoise) {
                RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry;
                DensityFunctionTypes.ShiftedNoise shiftedNoise = (DensityFunctionTypes.ShiftedNoise)densityFunction;
                if (bl) {
                    registryEntry = shiftedNoise.noiseData();
                    if (Objects.equals(registryEntry.getKey(), Optional.of(NoiseParametersKeys.TEMPERATURE))) {
                        DoublePerlinNoiseSampler doublePerlinNoiseSampler2 = DoublePerlinNoiseSampler.createLegacy(randomProvider.create(l), new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0));
                        return new DensityFunctionTypes.ShiftedNoise(shiftedNoise.shiftX(), shiftedNoise.shiftY(), shiftedNoise.shiftZ(), shiftedNoise.xzScale(), shiftedNoise.yScale(), registryEntry, doublePerlinNoiseSampler2);
                    }
                    if (Objects.equals(registryEntry.getKey(), Optional.of(NoiseParametersKeys.VEGETATION))) {
                        DoublePerlinNoiseSampler doublePerlinNoiseSampler2 = DoublePerlinNoiseSampler.createLegacy(randomProvider.create(l + 1L), new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0));
                        return new DensityFunctionTypes.ShiftedNoise(shiftedNoise.shiftX(), shiftedNoise.shiftY(), shiftedNoise.shiftZ(), shiftedNoise.xzScale(), shiftedNoise.yScale(), registryEntry, doublePerlinNoiseSampler2);
                    }
                }
                registryEntry = shiftedNoise.noiseData();
                return new DensityFunctionTypes.ShiftedNoise(shiftedNoise.shiftX(), shiftedNoise.shiftY(), shiftedNoise.shiftZ(), shiftedNoise.xzScale(), shiftedNoise.yScale(), registryEntry, DensityFunctions.method_41107(randomDeriver, registry, registryEntry));
            }
            if (densityFunction instanceof DensityFunctionTypes.WeirdScaledSampler) {
                DensityFunctionTypes.WeirdScaledSampler weirdScaledSampler = (DensityFunctionTypes.WeirdScaledSampler)densityFunction;
                return new DensityFunctionTypes.WeirdScaledSampler(weirdScaledSampler.input(), weirdScaledSampler.noiseData(), DensityFunctions.method_41107(randomDeriver, registry, weirdScaledSampler.noiseData()), weirdScaledSampler.rarityValueMapper());
            }
            if (densityFunction instanceof InterpolatedNoiseSampler) {
                if (bl) {
                    return new InterpolatedNoiseSampler(randomProvider.create(l), generationShapeConfig.sampling(), generationShapeConfig.horizontalBlockSize(), generationShapeConfig.verticalBlockSize());
                }
                return new InterpolatedNoiseSampler(randomDeriver.createRandom(new Identifier("terrain")), generationShapeConfig.sampling(), generationShapeConfig.horizontalBlockSize(), generationShapeConfig.verticalBlockSize());
            }
            if (densityFunction instanceof DensityFunctionTypes.EndIslands) {
                return new DensityFunctionTypes.EndIslands(l);
            }
            if (densityFunction instanceof DensityFunctionTypes.TerrainShaperSpline) {
                DensityFunctionTypes.TerrainShaperSpline terrainShaperSpline = (DensityFunctionTypes.TerrainShaperSpline)densityFunction;
                VanillaTerrainParameters vanillaTerrainParameters = generationShapeConfig.terrainParameters();
                return new DensityFunctionTypes.TerrainShaperSpline(terrainShaperSpline.continentalness(), terrainShaperSpline.erosion(), terrainShaperSpline.weirdness(), vanillaTerrainParameters, terrainShaperSpline.spline(), terrainShaperSpline.minValue(), terrainShaperSpline.maxValue());
            }
            if (densityFunction instanceof DensityFunctionTypes.Slide) {
                DensityFunctionTypes.Slide slide = (DensityFunctionTypes.Slide)densityFunction;
                return new DensityFunctionTypes.Slide(generationShapeConfig, slide.input());
            }
            return densityFunction;
        };
        DensityFunction.DensityFunctionVisitor densityFunctionVisitor2 = densityFunction -> map.computeIfAbsent(densityFunction, densityFunctionVisitor);
        SimpleNoiseRouter simpleNoiseRouter2 = simpleNoiseRouter.apply(densityFunctionVisitor2);
        RandomDeriver randomDeriver2 = randomDeriver.createRandom(new Identifier("aquifer")).createRandomDeriver();
        RandomDeriver randomDeriver3 = randomDeriver.createRandom(new Identifier("ore")).createRandomDeriver();
        return new NoiseRouter(simpleNoiseRouter2.barrierNoise(), simpleNoiseRouter2.fluidLevelFloodednessNoise(), simpleNoiseRouter2.fluidLevelSpreadNoise(), simpleNoiseRouter2.lavaNoise(), randomDeriver2, randomDeriver3, simpleNoiseRouter2.temperature(), simpleNoiseRouter2.vegetation(), simpleNoiseRouter2.continents(), simpleNoiseRouter2.erosion(), simpleNoiseRouter2.depth(), simpleNoiseRouter2.ridges(), simpleNoiseRouter2.initialDensityWithoutJaggedness(), simpleNoiseRouter2.finalDensity(), simpleNoiseRouter2.veinToggle(), simpleNoiseRouter2.veinRidged(), simpleNoiseRouter2.veinGap(), new VanillaBiomeParameters().getSpawnSuitabilityNoises());
    }

    private static DensityFunction method_40541(DensityFunction densityFunction, DensityFunction densityFunction2, DensityFunction densityFunction3, DensityFunctionTypes.TerrainShaperSpline.Spline spline, double d, double e, DensityFunction densityFunction4) {
        DensityFunction densityFunction5 = DensityFunctionTypes.method_40489(densityFunction, densityFunction2, densityFunction3, spline, d, e);
        DensityFunction densityFunction6 = DensityFunctionTypes.method_40488(DensityFunctionTypes.blendAlpha(), densityFunction4, densityFunction5);
        return DensityFunctionTypes.flatCache(DensityFunctionTypes.cache2d(densityFunction6));
    }

    private static DensityFunction method_40540(DensityFunction densityFunction, DensityFunction densityFunction2) {
        DensityFunction densityFunction3 = DensityFunctionTypes.method_40500(densityFunction2, densityFunction);
        return DensityFunctionTypes.method_40500(DensityFunctionTypes.constant(4.0), densityFunction3.quarterNegative());
    }

    private static DensityFunction method_40539(DensityFunction densityFunction, DensityFunction densityFunction2, int i, int j, int k) {
        return DensityFunctionTypes.interpolated(DensityFunctionTypes.rangeChoice(densityFunction, i, j + 1, densityFunction2, DensityFunctionTypes.constant(k)));
    }

    protected static double method_40542(GenerationShapeConfig generationShapeConfig, double d, double e) {
        double f = (int)e / generationShapeConfig.verticalBlockSize() - generationShapeConfig.minimumBlockY();
        d = generationShapeConfig.topSlide().method_38414(d, (double)generationShapeConfig.verticalBlockCount() - f);
        d = generationShapeConfig.bottomSlide().method_38414(d, f);
        return d;
    }

    protected static double method_40543(GenerationShapeConfig generationShapeConfig, DensityFunction densityFunction, int i, int j) {
        for (int k = generationShapeConfig.minimumBlockY() + generationShapeConfig.verticalBlockCount(); k >= generationShapeConfig.minimumBlockY(); --k) {
            int l = k * generationShapeConfig.verticalBlockSize();
            double d = -0.703125;
            double e = densityFunction.sample(new DensityFunction.UnblendedNoisePos(i, l, j)) + -0.703125;
            double f = MathHelper.clamp(e, -64.0, 64.0);
            if (!((f = DensityFunctions.method_40542(generationShapeConfig, f, l)) > 0.390625)) continue;
            return l;
        }
        return 2.147483647E9;
    }

    protected static final class CaveScaler {
        protected CaveScaler() {
        }

        protected static double scaleCaves(double value) {
            if (value < -0.75) {
                return 0.5;
            }
            if (value < -0.5) {
                return 0.75;
            }
            if (value < 0.5) {
                return 1.0;
            }
            if (value < 0.75) {
                return 2.0;
            }
            return 3.0;
        }

        protected static double scaleTunnels(double value) {
            if (value < -0.5) {
                return 0.75;
            }
            if (value < 0.0) {
                return 1.0;
            }
            if (value < 0.5) {
                return 1.5;
            }
            return 2.0;
        }
    }
}

