package net.minecraft.world.gen.densityfunction;

import java.util.HashMap;
import java.util.Map;
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
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.noise.NoiseRouter;
import net.minecraft.world.gen.noise.SimpleNoiseRouter;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomDeriver;

public class DensityFunctions {
	private static final float field_36614 = 0.08F;
	private static final double field_36615 = 1.5;
	private static final double field_36616 = 1.5;
	private static final double field_36617 = 1.5625;
	private static final DensityFunction field_36618 = DensityFunctionTypes.constant(10.0);
	private static final DensityFunction field_36619 = DensityFunctionTypes.zero();
	private static final RegistryKey<DensityFunction> ZERO = of("zero");
	private static final RegistryKey<DensityFunction> Y = of("y");
	private static final RegistryKey<DensityFunction> SHIFT_X = of("shift_x");
	private static final RegistryKey<DensityFunction> SHIFT_Z = of("shift_z");
	private static final RegistryKey<DensityFunction> BASE_3D_NOISE_OVERWORLD = of("overworld/base_3d_noise");
	private static final RegistryKey<DensityFunction> CONTINENTS_OVERWORLD = of("overworld/continents");
	private static final RegistryKey<DensityFunction> EROSION_OVERWORLD = of("overworld/erosion");
	private static final RegistryKey<DensityFunction> RIDGES_OVERWORLD = of("overworld/ridges");
	private static final RegistryKey<DensityFunction> FACTOR_OVERWORLD = of("overworld/factor");
	private static final RegistryKey<DensityFunction> DEPTH_OVERWORLD = of("overworld/depth");
	private static final RegistryKey<DensityFunction> SLOPED_CHEESE_OVERWORLD = of("overworld/sloped_cheese");
	private static final RegistryKey<DensityFunction> CONTINENTS_OVERWORLD_LARGE_BIOME = of("overworld_large_biomes/continents");
	private static final RegistryKey<DensityFunction> EROSION_OVERWORLD_LARGE_BIOME = of("overworld_large_biomes/erosion");
	private static final RegistryKey<DensityFunction> FACTOR_OVERWORLD_LARGE_BIOME = of("overworld_large_biomes/factor");
	private static final RegistryKey<DensityFunction> DEPTH_OVERWORLD_LARGE_BIOME = of("overworld_large_biomes/depth");
	private static final RegistryKey<DensityFunction> SLOPED_CHEESE_OVERWORLD_LARGE_BIOME = of("overworld_large_biomes/sloped_cheese");
	private static final RegistryKey<DensityFunction> SLOPED_CHEESE_END = of("end/sloped_cheese");
	private static final RegistryKey<DensityFunction> CAVES_SPAGHETTI_ROUGHNESS_FUNCTION_OVERWORLD = of("overworld/caves/spaghetti_roughness_function");
	private static final RegistryKey<DensityFunction> CAVES_ENTRANCES_OVERWORLD = of("overworld/caves/entrances");
	private static final RegistryKey<DensityFunction> CAVES_NOODLE_OVERWORLD = of("overworld/caves/noodle");
	private static final RegistryKey<DensityFunction> CAVES_PILLARS_OVERWORLD = of("overworld/caves/pillars");
	private static final RegistryKey<DensityFunction> CAVES_SPAGHETTI_2D_THICKNESS_MODULATOR_OVERWORLD = of("overworld/caves/spaghetti_2d_thickness_modulator");
	private static final RegistryKey<DensityFunction> CAVES_SPAGHETTI_2D_OVERWORLD = of("overworld/caves/spaghetti_2d");

	public static SimpleNoiseRouter method_41103(GenerationShapeConfig generationShapeConfig, boolean bl) {
		return method_41209(generationShapeConfig, bl);
	}

	private static RegistryKey<DensityFunction> of(String id) {
		return RegistryKey.of(Registry.DENSITY_FUNCTION_KEY, new Identifier(id));
	}

	public static RegistryEntry<? extends DensityFunction> init() {
		method_41112(ZERO, DensityFunctionTypes.zero());
		int i = DimensionType.MIN_HEIGHT * 2;
		int j = DimensionType.MAX_COLUMN_HEIGHT * 2;
		method_41112(Y, DensityFunctionTypes.yClampedGradient(i, j, (double)i, (double)j));
		DensityFunction densityFunction = method_41112(
			SHIFT_X, DensityFunctionTypes.flatCache(DensityFunctionTypes.cache2d(DensityFunctionTypes.shiftA(method_41111(NoiseParametersKeys.OFFSET))))
		);
		DensityFunction densityFunction2 = method_41112(
			SHIFT_Z, DensityFunctionTypes.flatCache(DensityFunctionTypes.cache2d(DensityFunctionTypes.shiftB(method_41111(NoiseParametersKeys.OFFSET))))
		);
		method_41112(BASE_3D_NOISE_OVERWORLD, InterpolatedNoiseSampler.field_37205);
		DensityFunction densityFunction3 = method_41112(
			CONTINENTS_OVERWORLD,
			DensityFunctionTypes.flatCache(DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, method_41111(NoiseParametersKeys.CONTINENTALNESS)))
		);
		DensityFunction densityFunction4 = method_41112(
			EROSION_OVERWORLD,
			DensityFunctionTypes.flatCache(DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, method_41111(NoiseParametersKeys.EROSION)))
		);
		DensityFunction densityFunction5 = method_41112(
			RIDGES_OVERWORLD,
			DensityFunctionTypes.flatCache(DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, method_41111(NoiseParametersKeys.RIDGE)))
		);
		DensityFunction densityFunction6 = DensityFunctionTypes.method_40502(method_41111(NoiseParametersKeys.JAGGED), 1500.0, 0.0);
		DensityFunction densityFunction7 = method_40541(
			densityFunction3, densityFunction4, densityFunction5, DensityFunctionTypes.TerrainShaperSpline.Spline.OFFSET, -0.81, 2.5, DensityFunctionTypes.blendOffset()
		);
		DensityFunction densityFunction8 = method_41112(
			FACTOR_OVERWORLD,
			method_40541(densityFunction3, densityFunction4, densityFunction5, DensityFunctionTypes.TerrainShaperSpline.Spline.FACTOR, 0.0, 8.0, field_36618)
		);
		DensityFunction densityFunction9 = method_41112(
			DEPTH_OVERWORLD, DensityFunctionTypes.method_40486(DensityFunctionTypes.yClampedGradient(-64, 320, 1.5, -1.5), densityFunction7)
		);
		method_41112(
			SLOPED_CHEESE_OVERWORLD, method_41102(densityFunction3, densityFunction4, densityFunction5, densityFunction8, densityFunction9, densityFunction6)
		);
		DensityFunction densityFunction10 = method_41112(
			CONTINENTS_OVERWORLD_LARGE_BIOME,
			DensityFunctionTypes.flatCache(
				DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, method_41111(NoiseParametersKeys.CONTINENTALNESS_LARGE))
			)
		);
		DensityFunction densityFunction11 = method_41112(
			EROSION_OVERWORLD_LARGE_BIOME,
			DensityFunctionTypes.flatCache(DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, method_41111(NoiseParametersKeys.EROSION_LARGE)))
		);
		DensityFunction densityFunction12 = method_40541(
			densityFunction10,
			densityFunction11,
			densityFunction5,
			DensityFunctionTypes.TerrainShaperSpline.Spline.OFFSET,
			-0.81,
			2.5,
			DensityFunctionTypes.blendOffset()
		);
		DensityFunction densityFunction13 = method_41112(
			FACTOR_OVERWORLD_LARGE_BIOME,
			method_40541(densityFunction10, densityFunction11, densityFunction5, DensityFunctionTypes.TerrainShaperSpline.Spline.FACTOR, 0.0, 8.0, field_36618)
		);
		DensityFunction densityFunction14 = method_41112(
			DEPTH_OVERWORLD_LARGE_BIOME, DensityFunctionTypes.method_40486(DensityFunctionTypes.yClampedGradient(-64, 320, 1.5, -1.5), densityFunction12)
		);
		method_41112(
			SLOPED_CHEESE_OVERWORLD_LARGE_BIOME,
			method_41102(densityFunction10, densityFunction11, densityFunction5, densityFunction13, densityFunction14, densityFunction6)
		);
		method_41112(SLOPED_CHEESE_END, DensityFunctionTypes.method_40486(DensityFunctionTypes.endIslands(0L), method_41116(BASE_3D_NOISE_OVERWORLD)));
		method_41112(CAVES_SPAGHETTI_ROUGHNESS_FUNCTION_OVERWORLD, method_41113());
		method_41112(
			CAVES_SPAGHETTI_2D_THICKNESS_MODULATOR_OVERWORLD,
			DensityFunctionTypes.cacheOnce(DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.SPAGHETTI_2D_THICKNESS), 2.0, 1.0, -0.6, -1.3))
		);
		method_41112(CAVES_SPAGHETTI_2D_OVERWORLD, method_41122());
		method_41112(CAVES_ENTRANCES_OVERWORLD, method_41117());
		method_41112(CAVES_NOODLE_OVERWORLD, method_41119());
		method_41112(CAVES_PILLARS_OVERWORLD, method_41121());
		return (RegistryEntry<? extends DensityFunction>)BuiltinRegistries.DENSITY_FUNCTION.streamEntries().iterator().next();
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

	private static DensityFunction method_41102(
		DensityFunction densityFunction,
		DensityFunction densityFunction2,
		DensityFunction densityFunction3,
		DensityFunction densityFunction4,
		DensityFunction densityFunction5,
		DensityFunction densityFunction6
	) {
		DensityFunction densityFunction7 = method_40541(
			densityFunction, densityFunction2, densityFunction3, DensityFunctionTypes.TerrainShaperSpline.Spline.JAGGEDNESS, 0.0, 1.28, field_36619
		);
		DensityFunction densityFunction8 = DensityFunctionTypes.method_40500(densityFunction7, densityFunction6.halfNegative());
		DensityFunction densityFunction9 = method_40540(densityFunction4, DensityFunctionTypes.method_40486(densityFunction5, densityFunction8));
		return DensityFunctionTypes.method_40486(densityFunction9, method_41116(BASE_3D_NOISE_OVERWORLD));
	}

	private static DensityFunction method_41113() {
		DensityFunction densityFunction = DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.SPAGHETTI_ROUGHNESS));
		DensityFunction densityFunction2 = DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0, -0.1);
		return DensityFunctionTypes.cacheOnce(
			DensityFunctionTypes.method_40500(densityFunction2, DensityFunctionTypes.method_40486(densityFunction.abs(), DensityFunctionTypes.constant(-0.4)))
		);
	}

	private static DensityFunction method_41117() {
		DensityFunction densityFunction = DensityFunctionTypes.cacheOnce(
			DensityFunctionTypes.method_40502(method_41111(NoiseParametersKeys.SPAGHETTI_3D_RARITY), 2.0, 1.0)
		);
		DensityFunction densityFunction2 = DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.SPAGHETTI_3D_THICKNESS), -0.065, -0.088);
		DensityFunction densityFunction3 = DensityFunctionTypes.weirdScaledSampler(
			densityFunction, method_41111(NoiseParametersKeys.SPAGHETTI_3D_1), DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper.TYPE1
		);
		DensityFunction densityFunction4 = DensityFunctionTypes.weirdScaledSampler(
			densityFunction, method_41111(NoiseParametersKeys.SPAGHETTI_3D_2), DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper.TYPE1
		);
		DensityFunction densityFunction5 = DensityFunctionTypes.method_40486(DensityFunctionTypes.method_40508(densityFunction3, densityFunction4), densityFunction2)
			.clamp(-1.0, 1.0);
		DensityFunction densityFunction6 = method_41116(CAVES_SPAGHETTI_ROUGHNESS_FUNCTION_OVERWORLD);
		DensityFunction densityFunction7 = DensityFunctionTypes.method_40502(method_41111(NoiseParametersKeys.CAVE_ENTRANCE), 0.75, 0.5);
		DensityFunction densityFunction8 = DensityFunctionTypes.method_40486(
			DensityFunctionTypes.method_40486(densityFunction7, DensityFunctionTypes.constant(0.37)), DensityFunctionTypes.yClampedGradient(-10, 30, 0.3, 0.0)
		);
		return DensityFunctionTypes.cacheOnce(
			DensityFunctionTypes.method_40505(densityFunction8, DensityFunctionTypes.method_40486(densityFunction6, densityFunction5))
		);
	}

	private static DensityFunction method_41119() {
		DensityFunction densityFunction = method_41116(Y);
		int i = -64;
		int j = -60;
		int k = 320;
		DensityFunction densityFunction2 = method_40539(
			densityFunction, DensityFunctionTypes.method_40502(method_41111(NoiseParametersKeys.NOODLE), 1.0, 1.0), -60, 320, -1
		);
		DensityFunction densityFunction3 = method_40539(
			densityFunction, DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.NOODLE_THICKNESS), 1.0, 1.0, -0.05, -0.1), -60, 320, 0
		);
		double d = 2.6666666666666665;
		DensityFunction densityFunction4 = method_40539(
			densityFunction, DensityFunctionTypes.method_40502(method_41111(NoiseParametersKeys.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665), -60, 320, 0
		);
		DensityFunction densityFunction5 = method_40539(
			densityFunction, DensityFunctionTypes.method_40502(method_41111(NoiseParametersKeys.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665), -60, 320, 0
		);
		DensityFunction densityFunction6 = DensityFunctionTypes.method_40500(
			DensityFunctionTypes.constant(1.5), DensityFunctionTypes.method_40508(densityFunction4.abs(), densityFunction5.abs())
		);
		return DensityFunctionTypes.rangeChoice(
			densityFunction2, -1000000.0, 0.0, DensityFunctionTypes.constant(64.0), DensityFunctionTypes.method_40486(densityFunction3, densityFunction6)
		);
	}

	private static DensityFunction method_41121() {
		double d = 25.0;
		double e = 0.3;
		DensityFunction densityFunction = DensityFunctionTypes.method_40502(method_41111(NoiseParametersKeys.PILLAR), 25.0, 0.3);
		DensityFunction densityFunction2 = DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.PILLAR_RARENESS), 0.0, -2.0);
		DensityFunction densityFunction3 = DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.PILLAR_THICKNESS), 0.0, 1.1);
		DensityFunction densityFunction4 = DensityFunctionTypes.method_40486(
			DensityFunctionTypes.method_40500(densityFunction, DensityFunctionTypes.constant(2.0)), densityFunction2
		);
		return DensityFunctionTypes.cacheOnce(DensityFunctionTypes.method_40500(densityFunction4, densityFunction3.cube()));
	}

	private static DensityFunction method_41122() {
		DensityFunction densityFunction = DensityFunctionTypes.method_40502(method_41111(NoiseParametersKeys.SPAGHETTI_2D_MODULATOR), 2.0, 1.0);
		DensityFunction densityFunction2 = DensityFunctionTypes.weirdScaledSampler(
			densityFunction, method_41111(NoiseParametersKeys.SPAGHETTI_2D), DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper.TYPE2
		);
		DensityFunction densityFunction3 = DensityFunctionTypes.noise(
			method_41111(NoiseParametersKeys.SPAGHETTI_2D_ELEVATION), 0.0, (double)Math.floorDiv(-64, 8), 8.0
		);
		DensityFunction densityFunction4 = method_41116(CAVES_SPAGHETTI_2D_THICKNESS_MODULATOR_OVERWORLD);
		DensityFunction densityFunction5 = DensityFunctionTypes.method_40486(densityFunction3, DensityFunctionTypes.yClampedGradient(-64, 320, 8.0, -40.0)).abs();
		DensityFunction densityFunction6 = DensityFunctionTypes.method_40486(densityFunction5, densityFunction4).cube();
		double d = 0.083;
		DensityFunction densityFunction7 = DensityFunctionTypes.method_40486(
			densityFunction2, DensityFunctionTypes.method_40500(DensityFunctionTypes.constant(0.083), densityFunction4)
		);
		return DensityFunctionTypes.method_40508(densityFunction7, densityFunction6).clamp(-1.0, 1.0);
	}

	private static DensityFunction method_41101(DensityFunction densityFunction) {
		DensityFunction densityFunction2 = method_41116(CAVES_SPAGHETTI_2D_OVERWORLD);
		DensityFunction densityFunction3 = method_41116(CAVES_SPAGHETTI_ROUGHNESS_FUNCTION_OVERWORLD);
		DensityFunction densityFunction4 = DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.CAVE_LAYER), 8.0);
		DensityFunction densityFunction5 = DensityFunctionTypes.method_40500(DensityFunctionTypes.constant(4.0), densityFunction4.square());
		DensityFunction densityFunction6 = DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.CAVE_CHEESE), 0.6666666666666666);
		DensityFunction densityFunction7 = DensityFunctionTypes.method_40486(
			DensityFunctionTypes.method_40486(DensityFunctionTypes.constant(0.27), densityFunction6).clamp(-1.0, 1.0),
			DensityFunctionTypes.method_40486(
					DensityFunctionTypes.constant(1.5), DensityFunctionTypes.method_40500(DensityFunctionTypes.constant(-0.64), densityFunction)
				)
				.clamp(0.0, 0.5)
		);
		DensityFunction densityFunction8 = DensityFunctionTypes.method_40486(densityFunction5, densityFunction7);
		DensityFunction densityFunction9 = DensityFunctionTypes.method_40505(
			DensityFunctionTypes.method_40505(densityFunction8, method_41116(CAVES_ENTRANCES_OVERWORLD)),
			DensityFunctionTypes.method_40486(densityFunction2, densityFunction3)
		);
		DensityFunction densityFunction10 = method_41116(CAVES_PILLARS_OVERWORLD);
		DensityFunction densityFunction11 = DensityFunctionTypes.rangeChoice(
			densityFunction10, -1000000.0, 0.03, DensityFunctionTypes.constant(-1000000.0), densityFunction10
		);
		return DensityFunctionTypes.method_40508(densityFunction9, densityFunction11);
	}

	private static DensityFunction method_41207(GenerationShapeConfig generationShapeConfig, DensityFunction densityFunction) {
		DensityFunction densityFunction2 = DensityFunctionTypes.slide(generationShapeConfig, densityFunction);
		DensityFunction densityFunction3 = DensityFunctionTypes.blendDensity(densityFunction2);
		return DensityFunctionTypes.method_40500(DensityFunctionTypes.interpolated(densityFunction3), DensityFunctionTypes.constant(0.64)).squeeze();
	}

	private static SimpleNoiseRouter method_41209(GenerationShapeConfig generationShapeConfig, boolean bl) {
		DensityFunction densityFunction = DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.AQUIFER_BARRIER), 0.5);
		DensityFunction densityFunction2 = DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67);
		DensityFunction densityFunction3 = DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
		DensityFunction densityFunction4 = DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.AQUIFER_LAVA));
		DensityFunction densityFunction5 = method_41116(SHIFT_X);
		DensityFunction densityFunction6 = method_41116(SHIFT_Z);
		DensityFunction densityFunction7 = DensityFunctionTypes.shiftedNoise(
			densityFunction5, densityFunction6, 0.25, method_41111(bl ? NoiseParametersKeys.TEMPERATURE_LARGE : NoiseParametersKeys.TEMPERATURE)
		);
		DensityFunction densityFunction8 = DensityFunctionTypes.shiftedNoise(
			densityFunction5, densityFunction6, 0.25, method_41111(bl ? NoiseParametersKeys.VEGETATION_LARGE : NoiseParametersKeys.VEGETATION)
		);
		DensityFunction densityFunction9 = method_41116(bl ? FACTOR_OVERWORLD_LARGE_BIOME : FACTOR_OVERWORLD);
		DensityFunction densityFunction10 = method_41116(bl ? DEPTH_OVERWORLD_LARGE_BIOME : DEPTH_OVERWORLD);
		DensityFunction densityFunction11 = method_40540(DensityFunctionTypes.cache2d(densityFunction9), densityFunction10);
		DensityFunction densityFunction12 = method_41116(bl ? SLOPED_CHEESE_OVERWORLD_LARGE_BIOME : SLOPED_CHEESE_OVERWORLD);
		DensityFunction densityFunction13 = DensityFunctionTypes.method_40505(
			densityFunction12, DensityFunctionTypes.method_40500(DensityFunctionTypes.constant(5.0), method_41116(CAVES_ENTRANCES_OVERWORLD))
		);
		DensityFunction densityFunction14 = DensityFunctionTypes.rangeChoice(
			densityFunction12, -1000000.0, 1.5625, densityFunction13, method_41101(densityFunction12)
		);
		DensityFunction densityFunction15 = DensityFunctionTypes.method_40505(
			method_41207(generationShapeConfig, densityFunction14), method_41116(CAVES_NOODLE_OVERWORLD)
		);
		DensityFunction densityFunction16 = method_41116(Y);
		int i = generationShapeConfig.minimumY();
		int j = Stream.of(OreVeinSampler.VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(i);
		int k = Stream.of(OreVeinSampler.VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(i);
		DensityFunction densityFunction17 = method_40539(
			densityFunction16, DensityFunctionTypes.method_40502(method_41111(NoiseParametersKeys.ORE_VEININESS), 1.5, 1.5), j, k, 0
		);
		float f = 4.0F;
		DensityFunction densityFunction18 = method_40539(
				densityFunction16, DensityFunctionTypes.method_40502(method_41111(NoiseParametersKeys.ORE_VEIN_A), 4.0, 4.0), j, k, 0
			)
			.abs();
		DensityFunction densityFunction19 = method_40539(
				densityFunction16, DensityFunctionTypes.method_40502(method_41111(NoiseParametersKeys.ORE_VEIN_B), 4.0, 4.0), j, k, 0
			)
			.abs();
		DensityFunction densityFunction20 = DensityFunctionTypes.method_40486(
			DensityFunctionTypes.constant(-0.08F), DensityFunctionTypes.method_40508(densityFunction18, densityFunction19)
		);
		DensityFunction densityFunction21 = DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.ORE_GAP));
		return new SimpleNoiseRouter(
			densityFunction,
			densityFunction2,
			densityFunction3,
			densityFunction4,
			densityFunction7,
			densityFunction8,
			method_41116(bl ? CONTINENTS_OVERWORLD_LARGE_BIOME : CONTINENTS_OVERWORLD),
			method_41116(bl ? EROSION_OVERWORLD_LARGE_BIOME : EROSION_OVERWORLD),
			method_41116(bl ? DEPTH_OVERWORLD_LARGE_BIOME : DEPTH_OVERWORLD),
			method_41116(RIDGES_OVERWORLD),
			densityFunction11,
			densityFunction15,
			densityFunction17,
			densityFunction20,
			densityFunction21
		);
	}

	private static SimpleNoiseRouter method_41211(GenerationShapeConfig generationShapeConfig) {
		DensityFunction densityFunction = method_41116(SHIFT_X);
		DensityFunction densityFunction2 = method_41116(SHIFT_Z);
		DensityFunction densityFunction3 = DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, method_41111(NoiseParametersKeys.TEMPERATURE));
		DensityFunction densityFunction4 = DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, method_41111(NoiseParametersKeys.VEGETATION));
		DensityFunction densityFunction5 = method_40540(DensityFunctionTypes.cache2d(method_41116(FACTOR_OVERWORLD)), method_41116(DEPTH_OVERWORLD));
		DensityFunction densityFunction6 = method_41207(generationShapeConfig, method_41116(SLOPED_CHEESE_OVERWORLD));
		return new SimpleNoiseRouter(
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			densityFunction3,
			densityFunction4,
			method_41116(CONTINENTS_OVERWORLD),
			method_41116(EROSION_OVERWORLD),
			method_41116(DEPTH_OVERWORLD),
			method_41116(RIDGES_OVERWORLD),
			densityFunction5,
			densityFunction6,
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero()
		);
	}

	public static SimpleNoiseRouter method_41114(GenerationShapeConfig generationShapeConfig) {
		return method_41211(generationShapeConfig);
	}

	public static SimpleNoiseRouter method_41118(GenerationShapeConfig generationShapeConfig) {
		return method_41211(generationShapeConfig);
	}

	public static SimpleNoiseRouter method_41120(GenerationShapeConfig generationShapeConfig) {
		DensityFunction densityFunction = DensityFunctionTypes.cache2d(DensityFunctionTypes.endIslands(0L));
		DensityFunction densityFunction2 = method_41207(generationShapeConfig, method_41116(SLOPED_CHEESE_END));
		return new SimpleNoiseRouter(
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			densityFunction,
			densityFunction2,
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero()
		);
	}

	private static DoublePerlinNoiseSampler method_41107(
		RandomDeriver randomDeriver,
		Registry<DoublePerlinNoiseSampler.NoiseParameters> registry,
		RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry
	) {
		return NoiseParametersKeys.method_41127(
			randomDeriver, (RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters>)registryEntry.getKey().flatMap(registry::getEntry).orElse(registryEntry)
		);
	}

	public static NoiseRouter method_40544(
		GenerationShapeConfig generationShapeConfig,
		long l,
		Registry<DoublePerlinNoiseSampler.NoiseParameters> registry,
		ChunkRandom.RandomProvider randomProvider,
		SimpleNoiseRouter simpleNoiseRouter
	) {
		boolean bl = randomProvider == ChunkRandom.RandomProvider.LEGACY;
		RandomDeriver randomDeriver = randomProvider.create(l).createRandomDeriver();
		Map<DensityFunction, DensityFunction> map = new HashMap();
		DensityFunction.DensityFunctionVisitor densityFunctionVisitor = densityFunction -> {
			if (densityFunction instanceof DensityFunctionTypes.Noise noise) {
				RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry = noise.noiseData();
				return new DensityFunctionTypes.Noise(registryEntry, method_41107(randomDeriver, registry, registryEntry), noise.xzScale(), noise.yScale());
			} else if (densityFunction instanceof DensityFunctionTypes.class_6939 lv) {
				RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry2 = lv.noiseData();
				DoublePerlinNoiseSampler doublePerlinNoiseSampler;
				if (bl) {
					doublePerlinNoiseSampler = DoublePerlinNoiseSampler.create(
						randomDeriver.createRandom(NoiseParametersKeys.OFFSET.getValue()), new DoublePerlinNoiseSampler.NoiseParameters(0, 0.0)
					);
				} else {
					doublePerlinNoiseSampler = method_41107(randomDeriver, registry, registryEntry2);
				}

				return lv.method_41086(doublePerlinNoiseSampler);
			} else if (densityFunction instanceof DensityFunctionTypes.ShiftedNoise shiftedNoise) {
				if (bl) {
					RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry = shiftedNoise.noiseData();
					if (Objects.equals(registryEntry.getKey(), Optional.of(NoiseParametersKeys.TEMPERATURE))) {
						DoublePerlinNoiseSampler doublePerlinNoiseSampler2 = DoublePerlinNoiseSampler.createLegacy(
							randomProvider.create(l), new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0)
						);
						return new DensityFunctionTypes.ShiftedNoise(
							shiftedNoise.shiftX(),
							shiftedNoise.shiftY(),
							shiftedNoise.shiftZ(),
							shiftedNoise.xzScale(),
							shiftedNoise.yScale(),
							registryEntry,
							doublePerlinNoiseSampler2
						);
					}

					if (Objects.equals(registryEntry.getKey(), Optional.of(NoiseParametersKeys.VEGETATION))) {
						DoublePerlinNoiseSampler doublePerlinNoiseSampler2 = DoublePerlinNoiseSampler.createLegacy(
							randomProvider.create(l + 1L), new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0)
						);
						return new DensityFunctionTypes.ShiftedNoise(
							shiftedNoise.shiftX(),
							shiftedNoise.shiftY(),
							shiftedNoise.shiftZ(),
							shiftedNoise.xzScale(),
							shiftedNoise.yScale(),
							registryEntry,
							doublePerlinNoiseSampler2
						);
					}
				}

				RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntryx = shiftedNoise.noiseData();
				return new DensityFunctionTypes.ShiftedNoise(
					shiftedNoise.shiftX(),
					shiftedNoise.shiftY(),
					shiftedNoise.shiftZ(),
					shiftedNoise.xzScale(),
					shiftedNoise.yScale(),
					registryEntryx,
					method_41107(randomDeriver, registry, registryEntryx)
				);
			} else if (densityFunction instanceof DensityFunctionTypes.WeirdScaledSampler weirdScaledSampler) {
				return new DensityFunctionTypes.WeirdScaledSampler(
					weirdScaledSampler.input(),
					weirdScaledSampler.noiseData(),
					method_41107(randomDeriver, registry, weirdScaledSampler.noiseData()),
					weirdScaledSampler.rarityValueMapper()
				);
			} else if (densityFunction instanceof InterpolatedNoiseSampler) {
				return bl
					? new InterpolatedNoiseSampler(
						randomProvider.create(l), generationShapeConfig.sampling(), generationShapeConfig.horizontalBlockSize(), generationShapeConfig.verticalBlockSize()
					)
					: new InterpolatedNoiseSampler(
						randomDeriver.createRandom(new Identifier("terrain")),
						generationShapeConfig.sampling(),
						generationShapeConfig.horizontalBlockSize(),
						generationShapeConfig.verticalBlockSize()
					);
			} else if (densityFunction instanceof DensityFunctionTypes.EndIslands) {
				return new DensityFunctionTypes.EndIslands(l);
			} else if (densityFunction instanceof DensityFunctionTypes.TerrainShaperSpline terrainShaperSpline) {
				VanillaTerrainParameters vanillaTerrainParameters = generationShapeConfig.terrainParameters();
				return new DensityFunctionTypes.TerrainShaperSpline(
					terrainShaperSpline.continentalness(),
					terrainShaperSpline.erosion(),
					terrainShaperSpline.weirdness(),
					vanillaTerrainParameters,
					terrainShaperSpline.spline(),
					terrainShaperSpline.minValue(),
					terrainShaperSpline.maxValue()
				);
			} else {
				return (DensityFunction)(densityFunction instanceof DensityFunctionTypes.Slide slide
					? new DensityFunctionTypes.Slide(generationShapeConfig, slide.input())
					: densityFunction);
			}
		};
		DensityFunction.DensityFunctionVisitor densityFunctionVisitor2 = densityFunction -> (DensityFunction)map.computeIfAbsent(
				densityFunction, densityFunctionVisitor
			);
		SimpleNoiseRouter simpleNoiseRouter2 = simpleNoiseRouter.apply(densityFunctionVisitor2);
		RandomDeriver randomDeriver2 = randomDeriver.createRandom(new Identifier("aquifer")).createRandomDeriver();
		RandomDeriver randomDeriver3 = randomDeriver.createRandom(new Identifier("ore")).createRandomDeriver();
		return new NoiseRouter(
			simpleNoiseRouter2.barrierNoise(),
			simpleNoiseRouter2.fluidLevelFloodednessNoise(),
			simpleNoiseRouter2.fluidLevelSpreadNoise(),
			simpleNoiseRouter2.lavaNoise(),
			randomDeriver2,
			randomDeriver3,
			simpleNoiseRouter2.temperature(),
			simpleNoiseRouter2.vegetation(),
			simpleNoiseRouter2.continents(),
			simpleNoiseRouter2.erosion(),
			simpleNoiseRouter2.depth(),
			simpleNoiseRouter2.ridges(),
			simpleNoiseRouter2.initialDensityWithoutJaggedness(),
			simpleNoiseRouter2.finalDensity(),
			simpleNoiseRouter2.veinToggle(),
			simpleNoiseRouter2.veinRidged(),
			simpleNoiseRouter2.veinGap(),
			new VanillaBiomeParameters().getSpawnSuitabilityNoises()
		);
	}

	private static DensityFunction method_40541(
		DensityFunction densityFunction,
		DensityFunction densityFunction2,
		DensityFunction densityFunction3,
		DensityFunctionTypes.TerrainShaperSpline.Spline spline,
		double d,
		double e,
		DensityFunction densityFunction4
	) {
		DensityFunction densityFunction5 = DensityFunctionTypes.method_40489(densityFunction, densityFunction2, densityFunction3, spline, d, e);
		DensityFunction densityFunction6 = DensityFunctionTypes.method_40488(DensityFunctionTypes.blendAlpha(), densityFunction4, densityFunction5);
		return DensityFunctionTypes.flatCache(DensityFunctionTypes.cache2d(densityFunction6));
	}

	private static DensityFunction method_40540(DensityFunction densityFunction, DensityFunction densityFunction2) {
		DensityFunction densityFunction3 = DensityFunctionTypes.method_40500(densityFunction2, densityFunction);
		return DensityFunctionTypes.method_40500(DensityFunctionTypes.constant(4.0), densityFunction3.quarterNegative());
	}

	private static DensityFunction method_40539(DensityFunction densityFunction, DensityFunction densityFunction2, int i, int j, int k) {
		return DensityFunctionTypes.interpolated(
			DensityFunctionTypes.rangeChoice(densityFunction, (double)i, (double)(j + 1), densityFunction2, DensityFunctionTypes.constant((double)k))
		);
	}

	protected static double method_40542(GenerationShapeConfig generationShapeConfig, double d, double e) {
		double f = (double)((int)e / generationShapeConfig.verticalBlockSize() - generationShapeConfig.minimumBlockY());
		d = generationShapeConfig.topSlide().method_38414(d, (double)generationShapeConfig.verticalBlockCount() - f);
		return generationShapeConfig.bottomSlide().method_38414(d, f);
	}

	public static double method_40543(GenerationShapeConfig generationShapeConfig, DensityFunction densityFunction, int i, int j) {
		for (int k = generationShapeConfig.minimumBlockY() + generationShapeConfig.verticalBlockCount(); k >= generationShapeConfig.minimumBlockY(); k--) {
			int l = k * generationShapeConfig.verticalBlockSize();
			double d = -0.703125;
			double e = densityFunction.sample(new DensityFunction.UnblendedNoisePos(i, l, j)) + -0.703125;
			double f = MathHelper.clamp(e, -64.0, 64.0);
			f = method_40542(generationShapeConfig, f, (double)l);
			if (f > 0.390625) {
				return (double)l;
			}
		}

		return 2.147483647E9;
	}

	protected static final class CaveScaler {
		protected static double scaleCaves(double value) {
			if (value < -0.75) {
				return 0.5;
			} else if (value < -0.5) {
				return 0.75;
			} else if (value < 0.5) {
				return 1.0;
			} else {
				return value < 0.75 ? 2.0 : 3.0;
			}
		}

		protected static double scaleTunnels(double value) {
			if (value < -0.5) {
				return 0.75;
			} else if (value < 0.0) {
				return 1.0;
			} else {
				return value < 0.5 ? 1.5 : 2.0;
			}
		}
	}
}
