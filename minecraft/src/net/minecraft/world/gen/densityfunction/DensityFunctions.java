package net.minecraft.world.gen.densityfunction;

import java.util.stream.Stream;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.source.util.VanillaTerrainParametersCreator;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.OreVeinSampler;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.noise.NoiseRouter;

public class DensityFunctions {
	public static final float field_37690 = -0.50375F;
	private static final float field_36614 = 0.08F;
	private static final double field_36615 = 1.5;
	private static final double field_36616 = 1.5;
	private static final double field_36617 = 1.5625;
	private static final double field_38250 = -0.703125;
	public static final int field_37691 = 64;
	public static final long field_37692 = 4096L;
	private static final DensityFunction TEN_FUNCTION = DensityFunctionTypes.constant(10.0);
	private static final DensityFunction ZERO_FUNCTION = DensityFunctionTypes.zero();
	private static final RegistryKey<DensityFunction> ZERO = of("zero");
	private static final RegistryKey<DensityFunction> Y = of("y");
	private static final RegistryKey<DensityFunction> SHIFT_X = of("shift_x");
	private static final RegistryKey<DensityFunction> SHIFT_Z = of("shift_z");
	private static final RegistryKey<DensityFunction> BASE_3D_NOISE_OVERWORLD = of("overworld/base_3d_noise");
	private static final RegistryKey<DensityFunction> BASE_3D_NOISE_NETHER = of("nether/base_3d_noise");
	private static final RegistryKey<DensityFunction> BASE_3D_NOISE_END = of("end/base_3d_noise");
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

	public static RegistryEntry<? extends DensityFunction> initAndGetDefault(Registry<DensityFunction> registry) {
		register(registry, ZERO, DensityFunctionTypes.zero());
		int i = DimensionType.MIN_HEIGHT * 2;
		int j = DimensionType.MAX_COLUMN_HEIGHT * 2;
		register(registry, Y, DensityFunctionTypes.yClampedGradient(i, j, (double)i, (double)j));
		DensityFunction densityFunction = registerAndGetHolder(
			registry,
			SHIFT_X,
			DensityFunctionTypes.flatCache(DensityFunctionTypes.cache2d(DensityFunctionTypes.shiftA(getNoiseParametersEntry(NoiseParametersKeys.OFFSET))))
		);
		DensityFunction densityFunction2 = registerAndGetHolder(
			registry,
			SHIFT_Z,
			DensityFunctionTypes.flatCache(DensityFunctionTypes.cache2d(DensityFunctionTypes.shiftB(getNoiseParametersEntry(NoiseParametersKeys.OFFSET))))
		);
		register(registry, BASE_3D_NOISE_OVERWORLD, InterpolatedNoiseSampler.createBase3dNoiseFunction(0.25, 0.125, 80.0, 160.0, 8.0));
		register(registry, BASE_3D_NOISE_NETHER, InterpolatedNoiseSampler.createBase3dNoiseFunction(0.25, 0.375, 80.0, 60.0, 8.0));
		register(registry, BASE_3D_NOISE_END, InterpolatedNoiseSampler.createBase3dNoiseFunction(0.25, 0.25, 80.0, 160.0, 4.0));
		RegistryEntry<DensityFunction> registryEntry = register(
			registry,
			CONTINENTS_OVERWORLD,
			DensityFunctionTypes.flatCache(
				DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, getNoiseParametersEntry(NoiseParametersKeys.CONTINENTALNESS))
			)
		);
		RegistryEntry<DensityFunction> registryEntry2 = register(
			registry,
			EROSION_OVERWORLD,
			DensityFunctionTypes.flatCache(
				DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, getNoiseParametersEntry(NoiseParametersKeys.EROSION))
			)
		);
		DensityFunction densityFunction3 = registerAndGetHolder(
			registry,
			RIDGES_OVERWORLD,
			DensityFunctionTypes.flatCache(
				DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, getNoiseParametersEntry(NoiseParametersKeys.RIDGE))
			)
		);
		register(registry, RIDGES_FOLDED_OVERWORLD, createRidgesFoldedOverworldFunction(densityFunction3));
		DensityFunction densityFunction4 = DensityFunctionTypes.noise(getNoiseParametersEntry(NoiseParametersKeys.JAGGED), 1500.0, 0.0);
		registerSlopedCheeseFunction(
			registry,
			densityFunction4,
			registryEntry,
			registryEntry2,
			OFFSET_OVERWORLD,
			FACTOR_OVERWORLD,
			JAGGEDNESS_OVERWORLD,
			DEPTH_OVERWORLD,
			SLOPED_CHEESE_OVERWORLD,
			false
		);
		RegistryEntry<DensityFunction> registryEntry3 = register(
			registry,
			CONTINENTS_OVERWORLD_LARGE_BIOME,
			DensityFunctionTypes.flatCache(
				DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, getNoiseParametersEntry(NoiseParametersKeys.CONTINENTALNESS_LARGE))
			)
		);
		RegistryEntry<DensityFunction> registryEntry4 = register(
			registry,
			EROSION_OVERWORLD_LARGE_BIOME,
			DensityFunctionTypes.flatCache(
				DensityFunctionTypes.shiftedNoise(densityFunction, densityFunction2, 0.25, getNoiseParametersEntry(NoiseParametersKeys.EROSION_LARGE))
			)
		);
		registerSlopedCheeseFunction(
			registry,
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
		registerSlopedCheeseFunction(
			registry,
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
		register(registry, SLOPED_CHEESE_END, DensityFunctionTypes.add(DensityFunctionTypes.endIslands(0L), entryHolder(registry, BASE_3D_NOISE_END)));
		register(registry, CAVES_SPAGHETTI_ROUGHNESS_FUNCTION_OVERWORLD, createCavesSpaghettiRoughnessOverworldFunction());
		register(
			registry,
			CAVES_SPAGHETTI_2D_THICKNESS_MODULATOR_OVERWORLD,
			DensityFunctionTypes.cacheOnce(DensityFunctionTypes.noiseInRange(getNoiseParametersEntry(NoiseParametersKeys.SPAGHETTI_2D_THICKNESS), 2.0, 1.0, -0.6, -1.3))
		);
		register(registry, CAVES_SPAGHETTI_2D_OVERWORLD, createCavesSpaghetti2dOverworldFunction(registry));
		register(registry, CAVES_ENTRANCES_OVERWORLD, createCavesEntrancesOverworldFunction(registry));
		register(registry, CAVES_NOODLE_OVERWORLD, createCavesNoodleOverworldFunction(registry));
		return register(registry, CAVES_PILLARS_OVERWORLD, createCavePillarsOverworldFunction());
	}

	/**
	 * Creates and registers the {@code sloped_cheese} density function.
	 * This is used for the normal, large biomes and amplified world types.
	 */
	private static void registerSlopedCheeseFunction(
		Registry<DensityFunction> registry,
		DensityFunction jaggedNoise,
		RegistryEntry<DensityFunction> continents,
		RegistryEntry<DensityFunction> erosion,
		RegistryKey<DensityFunction> offsetKey,
		RegistryKey<DensityFunction> factorKey,
		RegistryKey<DensityFunction> jaggednessKey,
		RegistryKey<DensityFunction> depthKey,
		RegistryKey<DensityFunction> slopedCheeseKey,
		boolean amplified
	) {
		DensityFunctionTypes.Spline.DensityFunctionWrapper densityFunctionWrapper = new DensityFunctionTypes.Spline.DensityFunctionWrapper(continents);
		DensityFunctionTypes.Spline.DensityFunctionWrapper densityFunctionWrapper2 = new DensityFunctionTypes.Spline.DensityFunctionWrapper(erosion);
		DensityFunctionTypes.Spline.DensityFunctionWrapper densityFunctionWrapper3 = new DensityFunctionTypes.Spline.DensityFunctionWrapper(
			registry.entryOf(RIDGES_OVERWORLD)
		);
		DensityFunctionTypes.Spline.DensityFunctionWrapper densityFunctionWrapper4 = new DensityFunctionTypes.Spline.DensityFunctionWrapper(
			registry.entryOf(RIDGES_FOLDED_OVERWORLD)
		);
		DensityFunction densityFunction = registerAndGetHolder(
			registry,
			offsetKey,
			applyBlending(
				DensityFunctionTypes.add(
					DensityFunctionTypes.constant(-0.50375F),
					DensityFunctionTypes.spline(
						VanillaTerrainParametersCreator.createOffsetSpline(densityFunctionWrapper, densityFunctionWrapper2, densityFunctionWrapper4, amplified)
					)
				),
				DensityFunctionTypes.blendOffset()
			)
		);
		DensityFunction densityFunction2 = registerAndGetHolder(
			registry,
			factorKey,
			applyBlending(
				DensityFunctionTypes.spline(
					VanillaTerrainParametersCreator.createFactorSpline(
						densityFunctionWrapper, densityFunctionWrapper2, densityFunctionWrapper3, densityFunctionWrapper4, amplified
					)
				),
				TEN_FUNCTION
			)
		);
		DensityFunction densityFunction3 = registerAndGetHolder(
			registry, depthKey, DensityFunctionTypes.add(DensityFunctionTypes.yClampedGradient(-64, 320, 1.5, -1.5), densityFunction)
		);
		DensityFunction densityFunction4 = registerAndGetHolder(
			registry,
			jaggednessKey,
			applyBlending(
				DensityFunctionTypes.spline(
					VanillaTerrainParametersCreator.createJaggednessSpline(
						densityFunctionWrapper, densityFunctionWrapper2, densityFunctionWrapper3, densityFunctionWrapper4, amplified
					)
				),
				ZERO_FUNCTION
			)
		);
		DensityFunction densityFunction5 = DensityFunctionTypes.mul(densityFunction4, jaggedNoise.halfNegative());
		DensityFunction densityFunction6 = createInitialDensityFunction(densityFunction2, DensityFunctionTypes.add(densityFunction3, densityFunction5));
		register(registry, slopedCheeseKey, DensityFunctionTypes.add(densityFunction6, entryHolder(registry, BASE_3D_NOISE_OVERWORLD)));
	}

	private static DensityFunction registerAndGetHolder(Registry<DensityFunction> registry, RegistryKey<DensityFunction> key, DensityFunction densityFunction) {
		return new DensityFunctionTypes.RegistryEntryHolder(BuiltinRegistries.add(registry, key, densityFunction));
	}

	private static RegistryEntry<DensityFunction> register(Registry<DensityFunction> registry, RegistryKey<DensityFunction> key, DensityFunction densityFunction) {
		return BuiltinRegistries.add(registry, key, densityFunction);
	}

	private static RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> getNoiseParametersEntry(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> key) {
		return BuiltinRegistries.NOISE_PARAMETERS.entryOf(key);
	}

	private static DensityFunction entryHolder(Registry<DensityFunction> registry, RegistryKey<DensityFunction> key) {
		return new DensityFunctionTypes.RegistryEntryHolder(registry.entryOf(key));
	}

	private static DensityFunction createRidgesFoldedOverworldFunction(DensityFunction input) {
		return DensityFunctionTypes.mul(
			DensityFunctionTypes.add(
				DensityFunctionTypes.add(input.abs(), DensityFunctionTypes.constant(-0.6666666666666666)).abs(), DensityFunctionTypes.constant(-0.3333333333333333)
			),
			DensityFunctionTypes.constant(-3.0)
		);
	}

	public static float getPeaksValleysNoise(float weirdness) {
		return -(Math.abs(Math.abs(weirdness) - 0.6666667F) - 0.33333334F) * 3.0F;
	}

	private static DensityFunction createCavesSpaghettiRoughnessOverworldFunction() {
		DensityFunction densityFunction = DensityFunctionTypes.noise(getNoiseParametersEntry(NoiseParametersKeys.SPAGHETTI_ROUGHNESS));
		DensityFunction densityFunction2 = DensityFunctionTypes.noiseInRange(getNoiseParametersEntry(NoiseParametersKeys.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0, -0.1);
		return DensityFunctionTypes.cacheOnce(
			DensityFunctionTypes.mul(densityFunction2, DensityFunctionTypes.add(densityFunction.abs(), DensityFunctionTypes.constant(-0.4)))
		);
	}

	private static DensityFunction createCavesEntrancesOverworldFunction(Registry<DensityFunction> registry) {
		DensityFunction densityFunction = DensityFunctionTypes.cacheOnce(
			DensityFunctionTypes.noise(getNoiseParametersEntry(NoiseParametersKeys.SPAGHETTI_3D_RARITY), 2.0, 1.0)
		);
		DensityFunction densityFunction2 = DensityFunctionTypes.noiseInRange(getNoiseParametersEntry(NoiseParametersKeys.SPAGHETTI_3D_THICKNESS), -0.065, -0.088);
		DensityFunction densityFunction3 = DensityFunctionTypes.weirdScaledSampler(
			densityFunction, getNoiseParametersEntry(NoiseParametersKeys.SPAGHETTI_3D_1), DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper.TYPE1
		);
		DensityFunction densityFunction4 = DensityFunctionTypes.weirdScaledSampler(
			densityFunction, getNoiseParametersEntry(NoiseParametersKeys.SPAGHETTI_3D_2), DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper.TYPE1
		);
		DensityFunction densityFunction5 = DensityFunctionTypes.add(DensityFunctionTypes.max(densityFunction3, densityFunction4), densityFunction2).clamp(-1.0, 1.0);
		DensityFunction densityFunction6 = entryHolder(registry, CAVES_SPAGHETTI_ROUGHNESS_FUNCTION_OVERWORLD);
		DensityFunction densityFunction7 = DensityFunctionTypes.noise(getNoiseParametersEntry(NoiseParametersKeys.CAVE_ENTRANCE), 0.75, 0.5);
		DensityFunction densityFunction8 = DensityFunctionTypes.add(
			DensityFunctionTypes.add(densityFunction7, DensityFunctionTypes.constant(0.37)), DensityFunctionTypes.yClampedGradient(-10, 30, 0.3, 0.0)
		);
		return DensityFunctionTypes.cacheOnce(DensityFunctionTypes.min(densityFunction8, DensityFunctionTypes.add(densityFunction6, densityFunction5)));
	}

	private static DensityFunction createCavesNoodleOverworldFunction(Registry<DensityFunction> registry) {
		DensityFunction densityFunction = entryHolder(registry, Y);
		int i = -64;
		int j = -60;
		int k = 320;
		DensityFunction densityFunction2 = verticalRangeChoice(
			densityFunction, DensityFunctionTypes.noise(getNoiseParametersEntry(NoiseParametersKeys.NOODLE), 1.0, 1.0), -60, 320, -1
		);
		DensityFunction densityFunction3 = verticalRangeChoice(
			densityFunction, DensityFunctionTypes.noiseInRange(getNoiseParametersEntry(NoiseParametersKeys.NOODLE_THICKNESS), 1.0, 1.0, -0.05, -0.1), -60, 320, 0
		);
		double d = 2.6666666666666665;
		DensityFunction densityFunction4 = verticalRangeChoice(
			densityFunction,
			DensityFunctionTypes.noise(getNoiseParametersEntry(NoiseParametersKeys.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665),
			-60,
			320,
			0
		);
		DensityFunction densityFunction5 = verticalRangeChoice(
			densityFunction,
			DensityFunctionTypes.noise(getNoiseParametersEntry(NoiseParametersKeys.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665),
			-60,
			320,
			0
		);
		DensityFunction densityFunction6 = DensityFunctionTypes.mul(
			DensityFunctionTypes.constant(1.5), DensityFunctionTypes.max(densityFunction4.abs(), densityFunction5.abs())
		);
		return DensityFunctionTypes.rangeChoice(
			densityFunction2, -1000000.0, 0.0, DensityFunctionTypes.constant(64.0), DensityFunctionTypes.add(densityFunction3, densityFunction6)
		);
	}

	private static DensityFunction createCavePillarsOverworldFunction() {
		double d = 25.0;
		double e = 0.3;
		DensityFunction densityFunction = DensityFunctionTypes.noise(getNoiseParametersEntry(NoiseParametersKeys.PILLAR), 25.0, 0.3);
		DensityFunction densityFunction2 = DensityFunctionTypes.noiseInRange(getNoiseParametersEntry(NoiseParametersKeys.PILLAR_RARENESS), 0.0, -2.0);
		DensityFunction densityFunction3 = DensityFunctionTypes.noiseInRange(getNoiseParametersEntry(NoiseParametersKeys.PILLAR_THICKNESS), 0.0, 1.1);
		DensityFunction densityFunction4 = DensityFunctionTypes.add(DensityFunctionTypes.mul(densityFunction, DensityFunctionTypes.constant(2.0)), densityFunction2);
		return DensityFunctionTypes.cacheOnce(DensityFunctionTypes.mul(densityFunction4, densityFunction3.cube()));
	}

	private static DensityFunction createCavesSpaghetti2dOverworldFunction(Registry<DensityFunction> registry) {
		DensityFunction densityFunction = DensityFunctionTypes.noise(getNoiseParametersEntry(NoiseParametersKeys.SPAGHETTI_2D_MODULATOR), 2.0, 1.0);
		DensityFunction densityFunction2 = DensityFunctionTypes.weirdScaledSampler(
			densityFunction, getNoiseParametersEntry(NoiseParametersKeys.SPAGHETTI_2D), DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper.TYPE2
		);
		DensityFunction densityFunction3 = DensityFunctionTypes.noiseInRange(
			getNoiseParametersEntry(NoiseParametersKeys.SPAGHETTI_2D_ELEVATION), 0.0, (double)Math.floorDiv(-64, 8), 8.0
		);
		DensityFunction densityFunction4 = entryHolder(registry, CAVES_SPAGHETTI_2D_THICKNESS_MODULATOR_OVERWORLD);
		DensityFunction densityFunction5 = DensityFunctionTypes.add(densityFunction3, DensityFunctionTypes.yClampedGradient(-64, 320, 8.0, -40.0)).abs();
		DensityFunction densityFunction6 = DensityFunctionTypes.add(densityFunction5, densityFunction4).cube();
		double d = 0.083;
		DensityFunction densityFunction7 = DensityFunctionTypes.add(
			densityFunction2, DensityFunctionTypes.mul(DensityFunctionTypes.constant(0.083), densityFunction4)
		);
		return DensityFunctionTypes.max(densityFunction7, densityFunction6).clamp(-1.0, 1.0);
	}

	private static DensityFunction createCavesFunction(Registry<DensityFunction> registry, DensityFunction slopedCheese) {
		DensityFunction densityFunction = entryHolder(registry, CAVES_SPAGHETTI_2D_OVERWORLD);
		DensityFunction densityFunction2 = entryHolder(registry, CAVES_SPAGHETTI_ROUGHNESS_FUNCTION_OVERWORLD);
		DensityFunction densityFunction3 = DensityFunctionTypes.noise(getNoiseParametersEntry(NoiseParametersKeys.CAVE_LAYER), 8.0);
		DensityFunction densityFunction4 = DensityFunctionTypes.mul(DensityFunctionTypes.constant(4.0), densityFunction3.square());
		DensityFunction densityFunction5 = DensityFunctionTypes.noise(getNoiseParametersEntry(NoiseParametersKeys.CAVE_CHEESE), 0.6666666666666666);
		DensityFunction densityFunction6 = DensityFunctionTypes.add(
			DensityFunctionTypes.add(DensityFunctionTypes.constant(0.27), densityFunction5).clamp(-1.0, 1.0),
			DensityFunctionTypes.add(DensityFunctionTypes.constant(1.5), DensityFunctionTypes.mul(DensityFunctionTypes.constant(-0.64), slopedCheese)).clamp(0.0, 0.5)
		);
		DensityFunction densityFunction7 = DensityFunctionTypes.add(densityFunction4, densityFunction6);
		DensityFunction densityFunction8 = DensityFunctionTypes.min(
			DensityFunctionTypes.min(densityFunction7, entryHolder(registry, CAVES_ENTRANCES_OVERWORLD)), DensityFunctionTypes.add(densityFunction, densityFunction2)
		);
		DensityFunction densityFunction9 = entryHolder(registry, CAVES_PILLARS_OVERWORLD);
		DensityFunction densityFunction10 = DensityFunctionTypes.rangeChoice(
			densityFunction9, -1000000.0, 0.03, DensityFunctionTypes.constant(-1000000.0), densityFunction9
		);
		return DensityFunctionTypes.max(densityFunction8, densityFunction10);
	}

	private static DensityFunction applyBlendDensity(DensityFunction density) {
		DensityFunction densityFunction = DensityFunctionTypes.blendDensity(density);
		return DensityFunctionTypes.mul(DensityFunctionTypes.interpolated(densityFunction), DensityFunctionTypes.constant(0.64)).squeeze();
	}

	/**
	 * Creates the {@link NoiseRouter} for the overworld and the surface world type.
	 * 
	 * @return the created {@code NoiseRouter}
	 * 
	 * @param largeBiomes whether the large biomes world type is used
	 * @param amplified whether the amplified world type is used
	 */
	public static NoiseRouter createSurfaceNoiseRouter(Registry<DensityFunction> registry, boolean largeBiomes, boolean amplified) {
		DensityFunction densityFunction = DensityFunctionTypes.noise(getNoiseParametersEntry(NoiseParametersKeys.AQUIFER_BARRIER), 0.5);
		DensityFunction densityFunction2 = DensityFunctionTypes.noise(getNoiseParametersEntry(NoiseParametersKeys.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67);
		DensityFunction densityFunction3 = DensityFunctionTypes.noise(getNoiseParametersEntry(NoiseParametersKeys.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
		DensityFunction densityFunction4 = DensityFunctionTypes.noise(getNoiseParametersEntry(NoiseParametersKeys.AQUIFER_LAVA));
		DensityFunction densityFunction5 = entryHolder(registry, SHIFT_X);
		DensityFunction densityFunction6 = entryHolder(registry, SHIFT_Z);
		DensityFunction densityFunction7 = DensityFunctionTypes.shiftedNoise(
			densityFunction5, densityFunction6, 0.25, getNoiseParametersEntry(largeBiomes ? NoiseParametersKeys.TEMPERATURE_LARGE : NoiseParametersKeys.TEMPERATURE)
		);
		DensityFunction densityFunction8 = DensityFunctionTypes.shiftedNoise(
			densityFunction5, densityFunction6, 0.25, getNoiseParametersEntry(largeBiomes ? NoiseParametersKeys.VEGETATION_LARGE : NoiseParametersKeys.VEGETATION)
		);
		DensityFunction densityFunction9 = entryHolder(
			registry, largeBiomes ? FACTOR_OVERWORLD_LARGE_BIOME : (amplified ? FACTOR_OVERWORLD_AMPLIFIED : FACTOR_OVERWORLD)
		);
		DensityFunction densityFunction10 = entryHolder(
			registry, largeBiomes ? DEPTH_OVERWORLD_LARGE_BIOME : (amplified ? DEPTH_OVERWORLD_AMPLIFIED : DEPTH_OVERWORLD)
		);
		DensityFunction densityFunction11 = createInitialDensityFunction(DensityFunctionTypes.cache2d(densityFunction9), densityFunction10);
		DensityFunction densityFunction12 = entryHolder(
			registry, largeBiomes ? SLOPED_CHEESE_OVERWORLD_LARGE_BIOME : (amplified ? SLOPED_CHEESE_OVERWORLD_AMPLIFIED : SLOPED_CHEESE_OVERWORLD)
		);
		DensityFunction densityFunction13 = DensityFunctionTypes.min(
			densityFunction12, DensityFunctionTypes.mul(DensityFunctionTypes.constant(5.0), entryHolder(registry, CAVES_ENTRANCES_OVERWORLD))
		);
		DensityFunction densityFunction14 = DensityFunctionTypes.rangeChoice(
			densityFunction12, -1000000.0, 1.5625, densityFunction13, createCavesFunction(registry, densityFunction12)
		);
		DensityFunction densityFunction15 = DensityFunctionTypes.min(
			applyBlendDensity(applySurfaceSlides(amplified, densityFunction14)), entryHolder(registry, CAVES_NOODLE_OVERWORLD)
		);
		DensityFunction densityFunction16 = entryHolder(registry, Y);
		int i = Stream.of(OreVeinSampler.VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(-DimensionType.MIN_HEIGHT * 2);
		int j = Stream.of(OreVeinSampler.VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(-DimensionType.MIN_HEIGHT * 2);
		DensityFunction densityFunction17 = verticalRangeChoice(
			densityFunction16, DensityFunctionTypes.noise(getNoiseParametersEntry(NoiseParametersKeys.ORE_VEININESS), 1.5, 1.5), i, j, 0
		);
		float f = 4.0F;
		DensityFunction densityFunction18 = verticalRangeChoice(
				densityFunction16, DensityFunctionTypes.noise(getNoiseParametersEntry(NoiseParametersKeys.ORE_VEIN_A), 4.0, 4.0), i, j, 0
			)
			.abs();
		DensityFunction densityFunction19 = verticalRangeChoice(
				densityFunction16, DensityFunctionTypes.noise(getNoiseParametersEntry(NoiseParametersKeys.ORE_VEIN_B), 4.0, 4.0), i, j, 0
			)
			.abs();
		DensityFunction densityFunction20 = DensityFunctionTypes.add(
			DensityFunctionTypes.constant(-0.08F), DensityFunctionTypes.max(densityFunction18, densityFunction19)
		);
		DensityFunction densityFunction21 = DensityFunctionTypes.noise(getNoiseParametersEntry(NoiseParametersKeys.ORE_GAP));
		return new NoiseRouter(
			densityFunction,
			densityFunction2,
			densityFunction3,
			densityFunction4,
			densityFunction7,
			densityFunction8,
			entryHolder(registry, largeBiomes ? CONTINENTS_OVERWORLD_LARGE_BIOME : CONTINENTS_OVERWORLD),
			entryHolder(registry, largeBiomes ? EROSION_OVERWORLD_LARGE_BIOME : EROSION_OVERWORLD),
			densityFunction10,
			entryHolder(registry, RIDGES_OVERWORLD),
			applySurfaceSlides(amplified, DensityFunctionTypes.add(densityFunction11, DensityFunctionTypes.constant(-0.703125)).clamp(-64.0, 64.0)),
			densityFunction15,
			densityFunction17,
			densityFunction20,
			densityFunction21
		);
	}

	private static NoiseRouter createMultiNoiseDependentNoiseRouter(Registry<DensityFunction> registry, DensityFunction density) {
		DensityFunction densityFunction = entryHolder(registry, SHIFT_X);
		DensityFunction densityFunction2 = entryHolder(registry, SHIFT_Z);
		DensityFunction densityFunction3 = DensityFunctionTypes.shiftedNoise(
			densityFunction, densityFunction2, 0.25, getNoiseParametersEntry(NoiseParametersKeys.TEMPERATURE)
		);
		DensityFunction densityFunction4 = DensityFunctionTypes.shiftedNoise(
			densityFunction, densityFunction2, 0.25, getNoiseParametersEntry(NoiseParametersKeys.VEGETATION)
		);
		DensityFunction densityFunction5 = applyBlendDensity(density);
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

	/**
	 * Applies the slides for the overworld and the surface world type.
	 * 
	 * @return the created density function
	 * 
	 * @param amplified whether the amplified world type is used
	 * @param density the base density function
	 */
	private static DensityFunction applySurfaceSlides(boolean amplified, DensityFunction density) {
		return applySlides(density, -64, 384, amplified ? 16 : 80, amplified ? 0 : 64, -0.078125, 0, 24, amplified ? 0.4 : 0.1171875);
	}

	/**
	 * Applies the slides for the nether and the caves world type.
	 * 
	 * @return the created density function
	 * 
	 * @param minY the minimum block Y coordinate of the world
	 * @param maxY the maximum block Y coordinate of the world
	 */
	private static DensityFunction applyCavesSlides(Registry<DensityFunction> registry, int minY, int maxY) {
		return applySlides(entryHolder(registry, BASE_3D_NOISE_NETHER), minY, maxY, 24, 0, 0.9375, -8, 24, 2.5);
	}

	/**
	 * Applies the slides for The End or the floating islands world type.
	 * 
	 * @return the created density function
	 * 
	 * @param minY the minimum block Y coordinate of the world
	 * @param maxY the maximum block Y coordinate of the world
	 */
	private static DensityFunction applyFloatingIslandsSlides(DensityFunction function, int minY, int maxY) {
		return applySlides(function, minY, maxY, 72, -184, -23.4375, 4, 32, -0.234375);
	}

	/**
	 * Creates the {@link NoiseRouter} for the nether.
	 * 
	 * @return the created {@code NoiseRouter}
	 */
	public static NoiseRouter createNetherNoiseRouter(Registry<DensityFunction> registry) {
		return createMultiNoiseDependentNoiseRouter(registry, applyCavesSlides(registry, 0, 128));
	}

	/**
	 * Creates the {@link NoiseRouter} for the caves world type.
	 * 
	 * @return the created {@code NoiseRouter}
	 */
	public static NoiseRouter createCavesNoiseRouter(Registry<DensityFunction> registry) {
		return createMultiNoiseDependentNoiseRouter(registry, applyCavesSlides(registry, -64, 192));
	}

	/**
	 * Creates the {@link NoiseRouter} for the floating islands world type.
	 * 
	 * @return the created {@code NoiseRouter}
	 */
	public static NoiseRouter createFloatingIslandsNoiseRouter(Registry<DensityFunction> registry) {
		return createMultiNoiseDependentNoiseRouter(registry, applyFloatingIslandsSlides(entryHolder(registry, BASE_3D_NOISE_END), 0, 256));
	}

	/**
	 * Applies the slides for The End.
	 * 
	 * @return the created density function
	 * 
	 * @param slopedCheese the base density function
	 */
	private static DensityFunction applyEndSlides(DensityFunction slopedCheese) {
		return applyFloatingIslandsSlides(slopedCheese, 0, 128);
	}

	/**
	 * Creates the {@link NoiseRouter} for The End.
	 * 
	 * @return the created {@code NoiseRouter}
	 */
	public static NoiseRouter createEndNoiseRouter(Registry<DensityFunction> registry) {
		DensityFunction densityFunction = DensityFunctionTypes.cache2d(DensityFunctionTypes.endIslands(0L));
		DensityFunction densityFunction2 = applyBlendDensity(applyEndSlides(entryHolder(registry, SLOPED_CHEESE_END)));
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
			applyEndSlides(DensityFunctionTypes.add(densityFunction, DensityFunctionTypes.constant(-0.703125))),
			densityFunction2,
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero()
		);
	}

	public static NoiseRouter createMissingNoiseRouter() {
		return new NoiseRouter(
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
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero(),
			DensityFunctionTypes.zero()
		);
	}

	private static DensityFunction applyBlending(DensityFunction function, DensityFunction blendOffset) {
		DensityFunction densityFunction = DensityFunctionTypes.lerp(DensityFunctionTypes.blendAlpha(), blendOffset, function);
		return DensityFunctionTypes.flatCache(DensityFunctionTypes.cache2d(densityFunction));
	}

	private static DensityFunction createInitialDensityFunction(DensityFunction factor, DensityFunction depth) {
		DensityFunction densityFunction = DensityFunctionTypes.mul(depth, factor);
		return DensityFunctionTypes.mul(DensityFunctionTypes.constant(4.0), densityFunction.quarterNegative());
	}

	private static DensityFunction verticalRangeChoice(DensityFunction y, DensityFunction whenInRange, int minInclusive, int maxInclusive, int whenOutOfRange) {
		return DensityFunctionTypes.interpolated(
			DensityFunctionTypes.rangeChoice(y, (double)minInclusive, (double)(maxInclusive + 1), whenInRange, DensityFunctionTypes.constant((double)whenOutOfRange))
		);
	}

	/**
	 * Interpolates the density at the top and bottom of the world.
	 * 
	 * @return the created density function
	 * 
	 * @param density the base density function
	 * @param minY the minimum block Y coordinate of the world
	 * @param maxY the maximum block Y coordinate of the world
	 * @param topRelativeMinY the minimum block Y of the top slide, subtracted from the top height
	 * @param topRelativeMaxY the maximum block Y of the top slide, subtracted from the top height
	 * @param topDensity the density at the top of the world. For height levels between the top-relative minimum and maximum heights, this is interpolated with the actual density
	 * @param bottomRelativeMinY the minimum block Y of the bottom slide, added to the bottom height
	 * @param bottomRelativeMaxY the maximum block Y of the bottom slide, added to the bottom height
	 * @param bottomDensity the density at the bottom of the world. For height levels between the bottom-relative minimum and maximum heights, this is interpolated with the actual density
	 */
	private static DensityFunction applySlides(
		DensityFunction density,
		int minY,
		int maxY,
		int topRelativeMinY,
		int topRelativeMaxY,
		double topDensity,
		int bottomRelativeMinY,
		int bottomRelativeMaxY,
		double bottomDensity
	) {
		DensityFunction densityFunction2 = DensityFunctionTypes.yClampedGradient(minY + maxY - topRelativeMinY, minY + maxY - topRelativeMaxY, 1.0, 0.0);
		DensityFunction densityFunction = DensityFunctionTypes.lerp(densityFunction2, topDensity, density);
		DensityFunction densityFunction3 = DensityFunctionTypes.yClampedGradient(minY + bottomRelativeMinY, minY + bottomRelativeMaxY, 0.0, 1.0);
		return DensityFunctionTypes.lerp(densityFunction3, bottomDensity, densityFunction);
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
