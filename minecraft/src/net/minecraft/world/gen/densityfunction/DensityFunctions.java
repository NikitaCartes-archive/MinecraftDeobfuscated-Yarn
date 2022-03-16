package net.minecraft.world.gen.densityfunction;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.class_7139;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.source.util.VanillaTerrainParametersCreator;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.OreVeinSampler;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.noise.NoiseRouter;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.RandomDeriver;

public class DensityFunctions {
	public static final float field_37690 = -0.50375F;
	private static final float field_36614 = 0.08F;
	private static final double field_36615 = 1.5;
	private static final double field_36616 = 1.5;
	private static final double field_36617 = 1.5625;
	public static final int field_37691 = 64;
	public static final long field_37692 = 4096L;
	private static final DensityFunction field_36618 = DensityFunctionTypes.constant(10.0);
	private static final DensityFunction field_36619 = DensityFunctionTypes.zero();
	private static final RegistryKey<DensityFunction> ZERO = of("zero");
	private static final RegistryKey<DensityFunction> Y = of("y");
	private static final RegistryKey<DensityFunction> SHIFT_X = of("shift_x");
	private static final RegistryKey<DensityFunction> SHIFT_Z = of("shift_z");
	private static final RegistryKey<DensityFunction> BASE_3D_NOISE_OVERWORLD = of("overworld/base_3d_noise");
	public static final RegistryKey<DensityFunction> CONTINENTS_OVERWORLD = of("overworld/continents");
	public static final RegistryKey<DensityFunction> EROSION_OVERWORLD = of("overworld/erosion");
	public static final RegistryKey<DensityFunction> RIDGES_OVERWORLD = of("overworld/ridges");
	public static final RegistryKey<DensityFunction> RIDGES_FOLDED_OVERWORLD = of("overworld/ridges_folded");
	public static final RegistryKey<DensityFunction> OFFSET_OVERWORLD = of("overworld/offset");
	public static final RegistryKey<DensityFunction> FACTOR_OVERWORLD = of("overworld/factor");
	public static final RegistryKey<DensityFunction> JAGGEDNESS_OVERWORLD = of("overworld/jaggedness");
	public static final RegistryKey<DensityFunction> DEPTH_OVERWORLD = of("overworld/depth");
	private static final RegistryKey<DensityFunction> SLOPED_CHEESE_OVERWORLD = of("overworld/sloped_cheese");
	public static final RegistryKey<DensityFunction> CONTINENTS_OVERWORLD_LARGE_BIOME = of("overworld_large_biomes/continents");
	public static final RegistryKey<DensityFunction> EROSION_OVERWORLD_LARGE_BIOME = of("overworld_large_biomes/erosion");
	private static final RegistryKey<DensityFunction> OFFSET_OVERWORLD_LARGE_BIOME = of("overworld_large_biomes/offset");
	private static final RegistryKey<DensityFunction> FACTOR_OVERWORLD_LARGE_BIOME = of("overworld_large_biomes/factor");
	private static final RegistryKey<DensityFunction> JAGGEDNESS_OVERWORLD_LARGE_BIOME = of("overworld_large_biomes/jaggedness");
	private static final RegistryKey<DensityFunction> DEPTH_OVERWORLD_LARGE_BIOME = of("overworld_large_biomes/depth");
	private static final RegistryKey<DensityFunction> SLOPED_CHEESE_OVERWORLD_LARGE_BIOME = of("overworld_large_biomes/sloped_cheese");
	private static final RegistryKey<DensityFunction> OFFSET_OVERWORLD_AMPLIFIED = of("overworld_amplified/offset");
	private static final RegistryKey<DensityFunction> FACTOR_OVERWORLD_AMPLIFIED = of("overworld_amplified/factor");
	private static final RegistryKey<DensityFunction> JAGGEDNESS_OVERWORLD_AMPLIFIED = of("overworld_amplified/jaggedness");
	private static final RegistryKey<DensityFunction> DEPTH_OVERWORLD_AMPLIFIED = of("overworld_amplified/depth");
	private static final RegistryKey<DensityFunction> SLOPED_CHEESE_OVERWORLD_AMPLIFIED = of("overworld_amplified/sloped_cheese");
	private static final RegistryKey<DensityFunction> SLOPED_CHEESE_END = of("end/sloped_cheese");
	private static final RegistryKey<DensityFunction> CAVES_SPAGHETTI_ROUGHNESS_FUNCTION_OVERWORLD = of("overworld/caves/spaghetti_roughness_function");
	private static final RegistryKey<DensityFunction> CAVES_ENTRANCES_OVERWORLD = of("overworld/caves/entrances");
	private static final RegistryKey<DensityFunction> CAVES_NOODLE_OVERWORLD = of("overworld/caves/noodle");
	private static final RegistryKey<DensityFunction> CAVES_PILLARS_OVERWORLD = of("overworld/caves/pillars");
	private static final RegistryKey<DensityFunction> CAVES_SPAGHETTI_2D_THICKNESS_MODULATOR_OVERWORLD = of("overworld/caves/spaghetti_2d_thickness_modulator");
	private static final RegistryKey<DensityFunction> CAVES_SPAGHETTI_2D_OVERWORLD = of("overworld/caves/spaghetti_2d");

	private static RegistryKey<DensityFunction> of(String id) {
		return RegistryKey.of(Registry.DENSITY_FUNCTION_KEY, new Identifier(id));
	}

	public static RegistryEntry<? extends DensityFunction> init() {
		register(ZERO, DensityFunctionTypes.zero());
		int i = DimensionType.MIN_HEIGHT * 2;
		int j = DimensionType.MAX_COLUMN_HEIGHT * 2;
		register(Y, DensityFunctionTypes.yClampedGradient(i, j, (double)i, (double)j));
		DensityFunction densityFunction = method_41551(
			SHIFT_X, DensityFunctionTypes.flatCache(DensityFunctionTypes.cache2d(DensityFunctionTypes.shiftA(method_41111(NoiseParametersKeys.OFFSET))))
		);
		DensityFunction densityFunction2 = method_41551(
			SHIFT_Z, DensityFunctionTypes.flatCache(DensityFunctionTypes.cache2d(DensityFunctionTypes.shiftB(method_41111(NoiseParametersKeys.OFFSET))))
		);
		register(BASE_3D_NOISE_OVERWORLD, InterpolatedNoiseSampler.field_37205);
		RegistryEntry<DensityFunction> registryEntry = register(
			CONTINENTS_OVERWORLD,
			DensityFunctionTypes.flatCache(DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, method_41111(NoiseParametersKeys.CONTINENTALNESS)))
		);
		RegistryEntry<DensityFunction> registryEntry2 = register(
			EROSION_OVERWORLD,
			DensityFunctionTypes.flatCache(DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, method_41111(NoiseParametersKeys.EROSION)))
		);
		DensityFunction densityFunction3 = method_41551(
			RIDGES_OVERWORLD,
			DensityFunctionTypes.flatCache(DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, method_41111(NoiseParametersKeys.RIDGE)))
		);
		register(RIDGES_FOLDED_OVERWORLD, method_41547(densityFunction3));
		DensityFunction densityFunction4 = DensityFunctionTypes.method_40502(method_41111(NoiseParametersKeys.JAGGED), 1500.0, 0.0);
		method_41548(
			densityFunction4, registryEntry, registryEntry2, OFFSET_OVERWORLD, FACTOR_OVERWORLD, JAGGEDNESS_OVERWORLD, DEPTH_OVERWORLD, SLOPED_CHEESE_OVERWORLD, false
		);
		RegistryEntry<DensityFunction> registryEntry3 = register(
			CONTINENTS_OVERWORLD_LARGE_BIOME,
			DensityFunctionTypes.flatCache(
				DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, method_41111(NoiseParametersKeys.CONTINENTALNESS_LARGE))
			)
		);
		RegistryEntry<DensityFunction> registryEntry4 = register(
			EROSION_OVERWORLD_LARGE_BIOME,
			DensityFunctionTypes.flatCache(DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, method_41111(NoiseParametersKeys.EROSION_LARGE)))
		);
		method_41548(
			densityFunction4,
			registryEntry3,
			registryEntry4,
			OFFSET_OVERWORLD_LARGE_BIOME,
			FACTOR_OVERWORLD_LARGE_BIOME,
			JAGGEDNESS_OVERWORLD_LARGE_BIOME,
			DEPTH_OVERWORLD_LARGE_BIOME,
			SLOPED_CHEESE_OVERWORLD_LARGE_BIOME,
			false
		);
		method_41548(
			densityFunction4,
			registryEntry,
			registryEntry2,
			OFFSET_OVERWORLD_AMPLIFIED,
			FACTOR_OVERWORLD_AMPLIFIED,
			JAGGEDNESS_OVERWORLD_AMPLIFIED,
			DEPTH_OVERWORLD_AMPLIFIED,
			SLOPED_CHEESE_OVERWORLD_AMPLIFIED,
			true
		);
		register(SLOPED_CHEESE_END, DensityFunctionTypes.add(DensityFunctionTypes.endIslands(0L), method_41116(BASE_3D_NOISE_OVERWORLD)));
		register(CAVES_SPAGHETTI_ROUGHNESS_FUNCTION_OVERWORLD, method_41113());
		register(
			CAVES_SPAGHETTI_2D_THICKNESS_MODULATOR_OVERWORLD,
			DensityFunctionTypes.cacheOnce(DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.SPAGHETTI_2D_THICKNESS), 2.0, 1.0, -0.6, -1.3))
		);
		register(CAVES_SPAGHETTI_2D_OVERWORLD, method_41122());
		register(CAVES_ENTRANCES_OVERWORLD, method_41117());
		register(CAVES_NOODLE_OVERWORLD, method_41119());
		register(CAVES_PILLARS_OVERWORLD, method_41121());
		return (RegistryEntry<? extends DensityFunction>)BuiltinRegistries.DENSITY_FUNCTION.streamEntries().iterator().next();
	}

	private static void method_41548(
		DensityFunction densityFunction,
		RegistryEntry<DensityFunction> registryEntry,
		RegistryEntry<DensityFunction> registryEntry2,
		RegistryKey<DensityFunction> registryKey,
		RegistryKey<DensityFunction> registryKey2,
		RegistryKey<DensityFunction> registryKey3,
		RegistryKey<DensityFunction> registryKey4,
		RegistryKey<DensityFunction> registryKey5,
		boolean bl
	) {
		DensityFunctionTypes.Spline.class_7135 lv = new DensityFunctionTypes.Spline.class_7135(registryEntry);
		DensityFunctionTypes.Spline.class_7135 lv2 = new DensityFunctionTypes.Spline.class_7135(registryEntry2);
		DensityFunctionTypes.Spline.class_7135 lv3 = new DensityFunctionTypes.Spline.class_7135(BuiltinRegistries.DENSITY_FUNCTION.entryOf(RIDGES_OVERWORLD));
		DensityFunctionTypes.Spline.class_7135 lv4 = new DensityFunctionTypes.Spline.class_7135(BuiltinRegistries.DENSITY_FUNCTION.entryOf(RIDGES_FOLDED_OVERWORLD));
		DensityFunction densityFunction2 = method_41551(
			registryKey,
			method_40541(
				DensityFunctionTypes.add(
					DensityFunctionTypes.constant(-0.50375F), DensityFunctionTypes.spline(VanillaTerrainParametersCreator.method_42056(lv, lv2, lv4, bl))
				),
				DensityFunctionTypes.blendOffset()
			)
		);
		DensityFunction densityFunction3 = method_41551(
			registryKey2, method_40541(DensityFunctionTypes.spline(VanillaTerrainParametersCreator.method_42055(lv, lv2, lv3, lv4, bl)), field_36618)
		);
		DensityFunction densityFunction4 = method_41551(
			registryKey4, DensityFunctionTypes.add(DensityFunctionTypes.yClampedGradient(-64, 320, 1.5, -1.5), densityFunction2)
		);
		DensityFunction densityFunction5 = method_41551(
			registryKey3, method_40541(DensityFunctionTypes.spline(VanillaTerrainParametersCreator.method_42058(lv, lv2, lv3, lv4, bl)), field_36619)
		);
		DensityFunction densityFunction6 = DensityFunctionTypes.mul(densityFunction5, densityFunction.halfNegative());
		DensityFunction densityFunction7 = method_40540(densityFunction3, DensityFunctionTypes.add(densityFunction4, densityFunction6));
		register(registryKey5, DensityFunctionTypes.add(densityFunction7, method_41116(BASE_3D_NOISE_OVERWORLD)));
	}

	private static DensityFunction method_41551(RegistryKey<DensityFunction> registryKey, DensityFunction densityFunction) {
		return new DensityFunctionTypes.RegistryEntryHolder(BuiltinRegistries.add(BuiltinRegistries.DENSITY_FUNCTION, registryKey, densityFunction));
	}

	private static RegistryEntry<DensityFunction> register(RegistryKey<DensityFunction> key, DensityFunction function) {
		return BuiltinRegistries.add(BuiltinRegistries.DENSITY_FUNCTION, key, function);
	}

	private static RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> method_41111(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey) {
		return BuiltinRegistries.NOISE_PARAMETERS.entryOf(registryKey);
	}

	private static DensityFunction method_41116(RegistryKey<DensityFunction> registryKey) {
		return new DensityFunctionTypes.RegistryEntryHolder(BuiltinRegistries.DENSITY_FUNCTION.entryOf(registryKey));
	}

	private static DensityFunction method_41547(DensityFunction densityFunction) {
		return DensityFunctionTypes.mul(
			DensityFunctionTypes.add(
				DensityFunctionTypes.add(densityFunction.abs(), DensityFunctionTypes.constant(-0.6666666666666666)).abs(),
				DensityFunctionTypes.constant(-0.3333333333333333)
			),
			DensityFunctionTypes.constant(-3.0)
		);
	}

	public static float method_41546(float f) {
		return -(Math.abs(Math.abs(f) - 0.6666667F) - 0.33333334F) * 3.0F;
	}

	private static DensityFunction method_41113() {
		DensityFunction densityFunction = DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.SPAGHETTI_ROUGHNESS));
		DensityFunction densityFunction2 = DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0, -0.1);
		return DensityFunctionTypes.cacheOnce(
			DensityFunctionTypes.mul(densityFunction2, DensityFunctionTypes.add(densityFunction.abs(), DensityFunctionTypes.constant(-0.4)))
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
		DensityFunction densityFunction5 = DensityFunctionTypes.add(DensityFunctionTypes.max(densityFunction3, densityFunction4), densityFunction2).clamp(-1.0, 1.0);
		DensityFunction densityFunction6 = method_41116(CAVES_SPAGHETTI_ROUGHNESS_FUNCTION_OVERWORLD);
		DensityFunction densityFunction7 = DensityFunctionTypes.method_40502(method_41111(NoiseParametersKeys.CAVE_ENTRANCE), 0.75, 0.5);
		DensityFunction densityFunction8 = DensityFunctionTypes.add(
			DensityFunctionTypes.add(densityFunction7, DensityFunctionTypes.constant(0.37)), DensityFunctionTypes.yClampedGradient(-10, 30, 0.3, 0.0)
		);
		return DensityFunctionTypes.cacheOnce(DensityFunctionTypes.min(densityFunction8, DensityFunctionTypes.add(densityFunction6, densityFunction5)));
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
		DensityFunction densityFunction6 = DensityFunctionTypes.mul(
			DensityFunctionTypes.constant(1.5), DensityFunctionTypes.max(densityFunction4.abs(), densityFunction5.abs())
		);
		return DensityFunctionTypes.rangeChoice(
			densityFunction2, -1000000.0, 0.0, DensityFunctionTypes.constant(64.0), DensityFunctionTypes.add(densityFunction3, densityFunction6)
		);
	}

	private static DensityFunction method_41121() {
		double d = 25.0;
		double e = 0.3;
		DensityFunction densityFunction = DensityFunctionTypes.method_40502(method_41111(NoiseParametersKeys.PILLAR), 25.0, 0.3);
		DensityFunction densityFunction2 = DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.PILLAR_RARENESS), 0.0, -2.0);
		DensityFunction densityFunction3 = DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.PILLAR_THICKNESS), 0.0, 1.1);
		DensityFunction densityFunction4 = DensityFunctionTypes.add(DensityFunctionTypes.mul(densityFunction, DensityFunctionTypes.constant(2.0)), densityFunction2);
		return DensityFunctionTypes.cacheOnce(DensityFunctionTypes.mul(densityFunction4, densityFunction3.cube()));
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
		DensityFunction densityFunction5 = DensityFunctionTypes.add(densityFunction3, DensityFunctionTypes.yClampedGradient(-64, 320, 8.0, -40.0)).abs();
		DensityFunction densityFunction6 = DensityFunctionTypes.add(densityFunction5, densityFunction4).cube();
		double d = 0.083;
		DensityFunction densityFunction7 = DensityFunctionTypes.add(
			densityFunction2, DensityFunctionTypes.mul(DensityFunctionTypes.constant(0.083), densityFunction4)
		);
		return DensityFunctionTypes.max(densityFunction7, densityFunction6).clamp(-1.0, 1.0);
	}

	private static DensityFunction method_41101(DensityFunction densityFunction) {
		DensityFunction densityFunction2 = method_41116(CAVES_SPAGHETTI_2D_OVERWORLD);
		DensityFunction densityFunction3 = method_41116(CAVES_SPAGHETTI_ROUGHNESS_FUNCTION_OVERWORLD);
		DensityFunction densityFunction4 = DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.CAVE_LAYER), 8.0);
		DensityFunction densityFunction5 = DensityFunctionTypes.mul(DensityFunctionTypes.constant(4.0), densityFunction4.square());
		DensityFunction densityFunction6 = DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.CAVE_CHEESE), 0.6666666666666666);
		DensityFunction densityFunction7 = DensityFunctionTypes.add(
			DensityFunctionTypes.add(DensityFunctionTypes.constant(0.27), densityFunction6).clamp(-1.0, 1.0),
			DensityFunctionTypes.add(DensityFunctionTypes.constant(1.5), DensityFunctionTypes.mul(DensityFunctionTypes.constant(-0.64), densityFunction))
				.clamp(0.0, 0.5)
		);
		DensityFunction densityFunction8 = DensityFunctionTypes.add(densityFunction5, densityFunction7);
		DensityFunction densityFunction9 = DensityFunctionTypes.min(
			DensityFunctionTypes.min(densityFunction8, method_41116(CAVES_ENTRANCES_OVERWORLD)), DensityFunctionTypes.add(densityFunction2, densityFunction3)
		);
		DensityFunction densityFunction10 = method_41116(CAVES_PILLARS_OVERWORLD);
		DensityFunction densityFunction11 = DensityFunctionTypes.rangeChoice(
			densityFunction10, -1000000.0, 0.03, DensityFunctionTypes.constant(-1000000.0), densityFunction10
		);
		return DensityFunctionTypes.max(densityFunction9, densityFunction11);
	}

	private static DensityFunction method_41207(GenerationShapeConfig generationShapeConfig, DensityFunction densityFunction) {
		DensityFunction densityFunction2 = DensityFunctionTypes.slide(generationShapeConfig, densityFunction);
		DensityFunction densityFunction3 = DensityFunctionTypes.blendDensity(densityFunction2);
		return DensityFunctionTypes.mul(DensityFunctionTypes.interpolated(densityFunction3), DensityFunctionTypes.constant(0.64)).squeeze();
	}

	public static NoiseRouter method_41103(GenerationShapeConfig generationShapeConfig, boolean bl, boolean bl2) {
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
		DensityFunction densityFunction9 = method_41116(bl ? FACTOR_OVERWORLD_LARGE_BIOME : (bl2 ? FACTOR_OVERWORLD_AMPLIFIED : FACTOR_OVERWORLD));
		DensityFunction densityFunction10 = method_41116(bl ? DEPTH_OVERWORLD_LARGE_BIOME : (bl2 ? DEPTH_OVERWORLD_AMPLIFIED : DEPTH_OVERWORLD));
		DensityFunction densityFunction11 = method_40540(DensityFunctionTypes.cache2d(densityFunction9), densityFunction10);
		DensityFunction densityFunction12 = method_41116(
			bl ? SLOPED_CHEESE_OVERWORLD_LARGE_BIOME : (bl2 ? SLOPED_CHEESE_OVERWORLD_AMPLIFIED : SLOPED_CHEESE_OVERWORLD)
		);
		DensityFunction densityFunction13 = DensityFunctionTypes.min(
			densityFunction12, DensityFunctionTypes.mul(DensityFunctionTypes.constant(5.0), method_41116(CAVES_ENTRANCES_OVERWORLD))
		);
		DensityFunction densityFunction14 = DensityFunctionTypes.rangeChoice(
			densityFunction12, -1000000.0, 1.5625, densityFunction13, method_41101(densityFunction12)
		);
		DensityFunction densityFunction15 = DensityFunctionTypes.min(method_41207(generationShapeConfig, densityFunction14), method_41116(CAVES_NOODLE_OVERWORLD));
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
		DensityFunction densityFunction20 = DensityFunctionTypes.add(
			DensityFunctionTypes.constant(-0.08F), DensityFunctionTypes.max(densityFunction18, densityFunction19)
		);
		DensityFunction densityFunction21 = DensityFunctionTypes.noise(method_41111(NoiseParametersKeys.ORE_GAP));
		return new NoiseRouter(
			densityFunction,
			densityFunction2,
			densityFunction3,
			densityFunction4,
			densityFunction7,
			densityFunction8,
			method_41116(bl ? CONTINENTS_OVERWORLD_LARGE_BIOME : CONTINENTS_OVERWORLD),
			method_41116(bl ? EROSION_OVERWORLD_LARGE_BIOME : EROSION_OVERWORLD),
			densityFunction10,
			method_41116(RIDGES_OVERWORLD),
			densityFunction11,
			densityFunction15,
			densityFunction17,
			densityFunction20,
			densityFunction21
		);
	}

	private static NoiseRouter method_41211(GenerationShapeConfig generationShapeConfig) {
		DensityFunction densityFunction = method_41116(SHIFT_X);
		DensityFunction densityFunction2 = method_41116(SHIFT_Z);
		DensityFunction densityFunction3 = DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, method_41111(NoiseParametersKeys.TEMPERATURE));
		DensityFunction densityFunction4 = DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, method_41111(NoiseParametersKeys.VEGETATION));
		DensityFunction densityFunction5 = method_41207(generationShapeConfig, method_41116(BASE_3D_NOISE_OVERWORLD));
		return new NoiseRouter(
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			densityFunction3,
			densityFunction4,
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			densityFunction5,
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero()
		);
	}

	public static NoiseRouter method_41549(GenerationShapeConfig generationShapeConfig) {
		return method_41211(generationShapeConfig);
	}

	public static NoiseRouter method_41552(GenerationShapeConfig generationShapeConfig) {
		return method_41211(generationShapeConfig);
	}

	public static NoiseRouter method_41118(GenerationShapeConfig generationShapeConfig) {
		return method_41211(generationShapeConfig);
	}

	public static NoiseRouter method_41120(GenerationShapeConfig generationShapeConfig) {
		DensityFunction densityFunction = DensityFunctionTypes.cache2d(DensityFunctionTypes.endIslands(0L));
		DensityFunction densityFunction2 = method_41207(generationShapeConfig, method_41116(SLOPED_CHEESE_END));
		return new NoiseRouter(
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			densityFunction,
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			densityFunction,
			densityFunction2,
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero()
		);
	}

	static DoublePerlinNoiseSampler method_41107(
		RandomDeriver randomDeriver,
		Registry<DoublePerlinNoiseSampler.NoiseParameters> registry,
		RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry
	) {
		return NoiseParametersKeys.method_41127(
			randomDeriver, (RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters>)registryEntry.getKey().flatMap(registry::getEntry).orElse(registryEntry)
		);
	}

	public static NoiseRouter method_40544(
		GenerationShapeConfig generationShapeConfig, Registry<DoublePerlinNoiseSampler.NoiseParameters> registry, NoiseRouter noiseRouter, class_7139 arg
	) {
		final RandomDeriver randomDeriver = arg.random();
		final boolean bl = arg.useLegacyInit();

		class class_7137 implements DensityFunction.DensityFunctionVisitor {
			private final Map<DensityFunction, DensityFunction> field_37702 = new HashMap();

			private DensityFunction method_41555(DensityFunction densityFunction) {
				if (densityFunction instanceof DensityFunctionTypes.Noise noise) {
					RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry = noise.noiseData();
					return new DensityFunctionTypes.Noise(
						registryEntry, DensityFunctions.method_41107(randomDeriver, registry, registryEntry), noise.xzScale(), noise.yScale()
					);
				} else if (densityFunction instanceof DensityFunctionTypes.class_6939 lv) {
					RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry2 = lv.noiseData();
					DoublePerlinNoiseSampler doublePerlinNoiseSampler;
					if (bl) {
						doublePerlinNoiseSampler = DoublePerlinNoiseSampler.create(
							randomDeriver.createRandom(NoiseParametersKeys.OFFSET.getValue()), new DoublePerlinNoiseSampler.NoiseParameters(0, 0.0)
						);
					} else {
						doublePerlinNoiseSampler = DensityFunctions.method_41107(randomDeriver, registry, registryEntry2);
					}

					return lv.method_41086(doublePerlinNoiseSampler);
				} else if (densityFunction instanceof DensityFunctionTypes.ShiftedNoise shiftedNoise) {
					if (bl) {
						RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry = shiftedNoise.noiseData();
						if (Objects.equals(registryEntry.getKey(), Optional.of(NoiseParametersKeys.TEMPERATURE))) {
							DoublePerlinNoiseSampler doublePerlinNoiseSampler2 = DoublePerlinNoiseSampler.createLegacy(
								arg.method_41562(0L), new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0)
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
								arg.method_41562(1L), new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0)
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
						DensityFunctions.method_41107(randomDeriver, registry, registryEntryx)
					);
				} else if (densityFunction instanceof DensityFunctionTypes.WeirdScaledSampler weirdScaledSampler) {
					return new DensityFunctionTypes.WeirdScaledSampler(
						weirdScaledSampler.input(),
						weirdScaledSampler.noiseData(),
						DensityFunctions.method_41107(randomDeriver, registry, weirdScaledSampler.noiseData()),
						weirdScaledSampler.rarityValueMapper()
					);
				} else if (densityFunction instanceof InterpolatedNoiseSampler) {
					AbstractRandom abstractRandom = bl ? arg.method_41562(0L) : randomDeriver.createRandom(new Identifier("terrain"));
					return new InterpolatedNoiseSampler(
						abstractRandom, generationShapeConfig.sampling(), generationShapeConfig.horizontalBlockSize(), generationShapeConfig.verticalBlockSize()
					);
				} else if (densityFunction instanceof DensityFunctionTypes.EndIslands) {
					return new DensityFunctionTypes.EndIslands(arg.legacyLevelSeed());
				} else {
					return (DensityFunction)(densityFunction instanceof DensityFunctionTypes.Slide slide
						? new DensityFunctionTypes.Slide(generationShapeConfig, slide.input())
						: densityFunction);
				}
			}

			public DensityFunction apply(DensityFunction densityFunction) {
				return (DensityFunction)this.field_37702.computeIfAbsent(densityFunction, this::method_41555);
			}
		}

		return noiseRouter.method_41544(new class_7137());
	}

	private static DensityFunction method_40541(DensityFunction densityFunction, DensityFunction densityFunction2) {
		DensityFunction densityFunction3 = DensityFunctionTypes.method_40488(DensityFunctionTypes.blendAlpha(), densityFunction2, densityFunction);
		return DensityFunctionTypes.flatCache(DensityFunctionTypes.cache2d(densityFunction3));
	}

	private static DensityFunction method_40540(DensityFunction densityFunction, DensityFunction densityFunction2) {
		DensityFunction densityFunction3 = DensityFunctionTypes.mul(densityFunction2, densityFunction);
		return DensityFunctionTypes.mul(DensityFunctionTypes.constant(4.0), densityFunction3.quarterNegative());
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
