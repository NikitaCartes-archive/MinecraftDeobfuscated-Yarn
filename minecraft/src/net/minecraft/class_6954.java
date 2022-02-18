package net.minecraft;

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
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.noise.NoiseRouter;
import net.minecraft.world.gen.noise.NoiseType;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomDeriver;

public class class_6954 {
	private static final float field_36614 = 0.08F;
	private static final double field_36615 = 1.5;
	private static final double field_36616 = 1.5;
	private static final double field_36617 = 1.5625;
	private static final NoiseType field_36618 = class_6916.method_40480(10.0);
	private static final NoiseType field_36619 = class_6916.method_40479();
	private static final RegistryKey<NoiseType> ZERO = method_41109("zero");
	private static final RegistryKey<NoiseType> Y = method_41109("y");
	private static final RegistryKey<NoiseType> SHIFT_X = method_41109("shift_x");
	private static final RegistryKey<NoiseType> SHIFT_Z = method_41109("shift_z");
	private static final RegistryKey<NoiseType> BASE_3D_NOISE_OVERWORLD = method_41109("overworld/base_3d_noise");
	private static final RegistryKey<NoiseType> CONTINENTS_OVERWORLD = method_41109("overworld/continents");
	private static final RegistryKey<NoiseType> EROSION_OVERWORLD = method_41109("overworld/erosion");
	private static final RegistryKey<NoiseType> RIDGES_OVERWORLD = method_41109("overworld/ridges");
	private static final RegistryKey<NoiseType> FACTOR_OVERWORLD = method_41109("overworld/factor");
	private static final RegistryKey<NoiseType> DEPTH_OVERWORLD = method_41109("overworld/depth");
	private static final RegistryKey<NoiseType> SLOPED_CHEESE_OVERWORLD = method_41109("overworld/sloped_cheese");
	private static final RegistryKey<NoiseType> CONTINENTS_OVERWORLD_LARGE_BIOME = method_41109("overworld_large_biomes/continents");
	private static final RegistryKey<NoiseType> EROSION_OVERWORLD_LARGE_BIOME = method_41109("overworld_large_biomes/erosion");
	private static final RegistryKey<NoiseType> FACTOR_OVERWORLD_LARGE_BIOME = method_41109("overworld_large_biomes/factor");
	private static final RegistryKey<NoiseType> DEPTH_OVERWORLD_LARGE_BIOME = method_41109("overworld_large_biomes/depth");
	private static final RegistryKey<NoiseType> SLOPED_CHEESE_OVERWORLD_LARGE_BIOME = method_41109("overworld_large_biomes/sloped_cheese");
	private static final RegistryKey<NoiseType> SLOPED_CHEESE_END = method_41109("end/sloped_cheese");
	private static final RegistryKey<NoiseType> CAVES_SPAGHETTI_ROUGHNESS_FUNCTION_OVERWORLD = method_41109("overworld/caves/spaghetti_roughness_function");
	private static final RegistryKey<NoiseType> CAVES_ENTRANCES_OVERWORLD = method_41109("overworld/caves/entrances");
	private static final RegistryKey<NoiseType> CAVES_NOODLE_OVERWORLD = method_41109("overworld/caves/noodle");
	private static final RegistryKey<NoiseType> CAVES_PILLARS_OVERWORLD = method_41109("overworld/caves/pillars");
	private static final RegistryKey<NoiseType> CAVES_SPAGHETTI_2D_THICKNESS_MODULATOR_OVERWORLD = method_41109("overworld/caves/spaghetti_2d_thickness_modulator");
	private static final RegistryKey<NoiseType> CAVES_SPAGHETTI_2D_OVERWORLD = method_41109("overworld/caves/spaghetti_2d");

	public static class_7056 method_41103(GenerationShapeConfig generationShapeConfig) {
		return method_41104(generationShapeConfig, true, true);
	}

	public static class_7056 method_41114(GenerationShapeConfig generationShapeConfig) {
		return method_41104(generationShapeConfig, false, false);
	}

	public static class_7056 method_41118(GenerationShapeConfig generationShapeConfig) {
		return method_41104(generationShapeConfig, false, false);
	}

	public static class_7056 method_41120(GenerationShapeConfig generationShapeConfig) {
		return method_41104(generationShapeConfig, false, false);
	}

	private static RegistryKey<NoiseType> method_41109(String string) {
		return RegistryKey.of(Registry.DENSITY_FUNCTION_WORLDGEN, new Identifier(string));
	}

	public static RegistryEntry<? extends NoiseType> method_41100() {
		method_41112(ZERO, class_6916.method_40479());
		int i = DimensionType.MIN_HEIGHT * 2;
		int j = DimensionType.MAX_COLUMN_HEIGHT * 2;
		method_41112(Y, class_6916.method_40481(i, j, (double)i, (double)j));
		NoiseType noiseType = method_41112(
			SHIFT_X, class_6916.method_40499(class_6916.method_40504(class_6916.method_40501(method_41111(NoiseParametersKeys.OFFSET))))
		);
		NoiseType noiseType2 = method_41112(
			SHIFT_Z, class_6916.method_40499(class_6916.method_40504(class_6916.method_40506(method_41111(NoiseParametersKeys.OFFSET))))
		);
		method_41112(BASE_3D_NOISE_OVERWORLD, InterpolatedNoiseSampler.field_37205);
		NoiseType noiseType3 = method_41112(
			CONTINENTS_OVERWORLD, class_6916.method_40499(class_6916.method_40487(noiseType, noiseType2, 0.25, method_41111(NoiseParametersKeys.CONTINENTALNESS)))
		);
		NoiseType noiseType4 = method_41112(
			EROSION_OVERWORLD, class_6916.method_40499(class_6916.method_40487(noiseType, noiseType2, 0.25, method_41111(NoiseParametersKeys.EROSION)))
		);
		NoiseType noiseType5 = method_41112(
			RIDGES_OVERWORLD, class_6916.method_40499(class_6916.method_40487(noiseType, noiseType2, 0.25, method_41111(NoiseParametersKeys.RIDGE)))
		);
		NoiseType noiseType6 = class_6916.method_40502(method_41111(NoiseParametersKeys.JAGGED), 1500.0, 0.0);
		NoiseType noiseType7 = method_40541(noiseType3, noiseType4, noiseType5, class_6916.class_6942.class_7054.OFFSET, -0.81, 2.5, class_6916.method_40503());
		NoiseType noiseType8 = method_41112(
			FACTOR_OVERWORLD, method_40541(noiseType3, noiseType4, noiseType5, class_6916.class_6942.class_7054.FACTOR, 0.0, 8.0, field_36618)
		);
		NoiseType noiseType9 = method_41112(DEPTH_OVERWORLD, class_6916.method_40486(class_6916.method_40481(-64, 320, 1.5, -1.5), noiseType7));
		method_41112(SLOPED_CHEESE_OVERWORLD, method_41102(noiseType3, noiseType4, noiseType5, noiseType8, noiseType9, noiseType6));
		NoiseType noiseType10 = method_41112(
			CONTINENTS_OVERWORLD_LARGE_BIOME,
			class_6916.method_40499(class_6916.method_40487(noiseType, noiseType2, 0.25, method_41111(NoiseParametersKeys.CONTINENTALNESS_LARGE)))
		);
		NoiseType noiseType11 = method_41112(
			EROSION_OVERWORLD_LARGE_BIOME,
			class_6916.method_40499(class_6916.method_40487(noiseType, noiseType2, 0.25, method_41111(NoiseParametersKeys.EROSION_LARGE)))
		);
		NoiseType noiseType12 = method_40541(noiseType10, noiseType11, noiseType5, class_6916.class_6942.class_7054.OFFSET, -0.81, 2.5, class_6916.method_40503());
		NoiseType noiseType13 = method_41112(
			FACTOR_OVERWORLD_LARGE_BIOME, method_40541(noiseType10, noiseType11, noiseType5, class_6916.class_6942.class_7054.FACTOR, 0.0, 8.0, field_36618)
		);
		NoiseType noiseType14 = method_41112(DEPTH_OVERWORLD_LARGE_BIOME, class_6916.method_40486(class_6916.method_40481(-64, 320, 1.5, -1.5), noiseType12));
		method_41112(SLOPED_CHEESE_OVERWORLD_LARGE_BIOME, method_41102(noiseType10, noiseType11, noiseType5, noiseType13, noiseType14, noiseType6));
		method_41112(SLOPED_CHEESE_END, class_6916.method_40486(class_6916.method_40482(0L), method_41116(BASE_3D_NOISE_OVERWORLD)));
		method_41112(CAVES_SPAGHETTI_ROUGHNESS_FUNCTION_OVERWORLD, method_41113());
		method_41112(
			CAVES_SPAGHETTI_2D_THICKNESS_MODULATOR_OVERWORLD,
			class_6916.method_40507(class_6916.method_40496(method_41111(NoiseParametersKeys.SPAGHETTI_2D_THICKNESS), 2.0, 1.0, -0.6, -1.3))
		);
		method_41112(CAVES_SPAGHETTI_2D_OVERWORLD, method_41122());
		method_41112(CAVES_ENTRANCES_OVERWORLD, method_41117());
		method_41112(CAVES_NOODLE_OVERWORLD, method_41119());
		method_41112(CAVES_PILLARS_OVERWORLD, method_41121());
		return (RegistryEntry<? extends NoiseType>)BuiltinRegistries.field_37232.streamEntries().iterator().next();
	}

	private static NoiseType method_41112(RegistryKey<NoiseType> registryKey, NoiseType noiseType) {
		return new class_6916.class_7051(BuiltinRegistries.add(BuiltinRegistries.field_37232, registryKey, noiseType));
	}

	private static RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> method_41111(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey) {
		return BuiltinRegistries.NOISE_PARAMETERS.entryOf(registryKey);
	}

	private static NoiseType method_41116(RegistryKey<NoiseType> registryKey) {
		return new class_6916.class_7051(BuiltinRegistries.field_37232.entryOf(registryKey));
	}

	private static NoiseType method_41102(
		NoiseType noiseType, NoiseType noiseType2, NoiseType noiseType3, NoiseType noiseType4, NoiseType noiseType5, NoiseType noiseType6
	) {
		NoiseType noiseType7 = method_40541(noiseType, noiseType2, noiseType3, class_6916.class_6942.class_7054.JAGGEDNESS, 0.0, 1.28, field_36619);
		NoiseType noiseType8 = class_6916.method_40500(noiseType7, noiseType6.method_40474());
		NoiseType noiseType9 = method_40540(noiseType5, noiseType4, noiseType8);
		return class_6916.method_40486(noiseType9, method_41116(BASE_3D_NOISE_OVERWORLD));
	}

	private static NoiseType method_41113() {
		NoiseType noiseType = class_6916.method_40493(method_41111(NoiseParametersKeys.SPAGHETTI_ROUGHNESS));
		NoiseType noiseType2 = class_6916.method_40495(method_41111(NoiseParametersKeys.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0, -0.1);
		return class_6916.method_40507(class_6916.method_40500(noiseType2, class_6916.method_40486(noiseType.method_40471(), class_6916.method_40480(-0.4))));
	}

	private static NoiseType method_41117() {
		NoiseType noiseType = class_6916.method_40507(class_6916.method_40502(method_41111(NoiseParametersKeys.SPAGHETTI_3D_RARITY), 2.0, 1.0));
		NoiseType noiseType2 = class_6916.method_40495(method_41111(NoiseParametersKeys.SPAGHETTI_3D_THICKNESS), -0.065, -0.088);
		NoiseType noiseType3 = class_6916.method_40491(noiseType, method_41111(NoiseParametersKeys.SPAGHETTI_3D_1), class_6916.class_6944.class_7048.TYPE1);
		NoiseType noiseType4 = class_6916.method_40491(noiseType, method_41111(NoiseParametersKeys.SPAGHETTI_3D_2), class_6916.class_6944.class_7048.TYPE1);
		NoiseType noiseType5 = class_6916.method_40486(class_6916.method_40508(noiseType3, noiseType4), noiseType2).method_40468(-1.0, 1.0);
		NoiseType noiseType6 = method_41116(CAVES_SPAGHETTI_ROUGHNESS_FUNCTION_OVERWORLD);
		NoiseType noiseType7 = class_6916.method_40502(method_41111(NoiseParametersKeys.CAVE_ENTRANCE), 0.75, 0.5);
		NoiseType noiseType8 = class_6916.method_40486(class_6916.method_40486(noiseType7, class_6916.method_40480(0.37)), class_6916.method_40481(-10, 30, 0.3, 0.0));
		return class_6916.method_40507(class_6916.method_40505(noiseType8, class_6916.method_40486(noiseType6, noiseType5)));
	}

	private static NoiseType method_41119() {
		NoiseType noiseType = method_41116(Y);
		int i = -64;
		int j = -60;
		int k = 320;
		NoiseType noiseType2 = method_40539(noiseType, class_6916.method_40502(method_41111(NoiseParametersKeys.NOODLE), 1.0, 1.0), -60, 320, -1);
		NoiseType noiseType3 = method_40539(
			noiseType, class_6916.method_40496(method_41111(NoiseParametersKeys.NOODLE_THICKNESS), 1.0, 1.0, -0.05, -0.1), -60, 320, 0
		);
		double d = 2.6666666666666665;
		NoiseType noiseType4 = method_40539(
			noiseType, class_6916.method_40502(method_41111(NoiseParametersKeys.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665), -60, 320, 0
		);
		NoiseType noiseType5 = method_40539(
			noiseType, class_6916.method_40502(method_41111(NoiseParametersKeys.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665), -60, 320, 0
		);
		NoiseType noiseType6 = class_6916.method_40500(class_6916.method_40480(1.5), class_6916.method_40508(noiseType4.method_40471(), noiseType5.method_40471()));
		return class_6916.method_40485(noiseType2, -1000000.0, 0.0, class_6916.method_40480(64.0), class_6916.method_40486(noiseType3, noiseType6));
	}

	private static NoiseType method_41121() {
		double d = 25.0;
		double e = 0.3;
		NoiseType noiseType = class_6916.method_40502(method_41111(NoiseParametersKeys.PILLAR), 25.0, 0.3);
		NoiseType noiseType2 = class_6916.method_40495(method_41111(NoiseParametersKeys.PILLAR_RARENESS), 0.0, -2.0);
		NoiseType noiseType3 = class_6916.method_40495(method_41111(NoiseParametersKeys.PILLAR_THICKNESS), 0.0, 1.1);
		NoiseType noiseType4 = class_6916.method_40486(class_6916.method_40500(noiseType, class_6916.method_40480(2.0)), noiseType2);
		return class_6916.method_40507(class_6916.method_40500(noiseType4, noiseType3.method_40473()));
	}

	private static NoiseType method_41122() {
		NoiseType noiseType = class_6916.method_40502(method_41111(NoiseParametersKeys.SPAGHETTI_2D_MODULATOR), 2.0, 1.0);
		NoiseType noiseType2 = class_6916.method_40491(noiseType, method_41111(NoiseParametersKeys.SPAGHETTI_2D), class_6916.class_6944.class_7048.TYPE2);
		NoiseType noiseType3 = class_6916.method_40497(method_41111(NoiseParametersKeys.SPAGHETTI_2D_ELEVATION), 0.0, (double)Math.floorDiv(-64, 8), 8.0);
		NoiseType noiseType4 = method_41116(CAVES_SPAGHETTI_2D_THICKNESS_MODULATOR_OVERWORLD);
		NoiseType noiseType5 = class_6916.method_40486(noiseType3, class_6916.method_40481(-64, 320, 8.0, -40.0)).method_40471();
		NoiseType noiseType6 = class_6916.method_40486(noiseType5, noiseType4).method_40473();
		double d = 0.083;
		NoiseType noiseType7 = class_6916.method_40486(noiseType2, class_6916.method_40500(class_6916.method_40480(0.083), noiseType4));
		return class_6916.method_40508(noiseType7, noiseType6).method_40468(-1.0, 1.0);
	}

	private static NoiseType method_41101(NoiseType noiseType) {
		NoiseType noiseType2 = method_41116(CAVES_SPAGHETTI_2D_OVERWORLD);
		NoiseType noiseType3 = method_41116(CAVES_SPAGHETTI_ROUGHNESS_FUNCTION_OVERWORLD);
		NoiseType noiseType4 = class_6916.method_40494(method_41111(NoiseParametersKeys.CAVE_LAYER), 8.0);
		NoiseType noiseType5 = class_6916.method_40500(class_6916.method_40480(4.0), noiseType4.method_40472());
		NoiseType noiseType6 = class_6916.method_40494(method_41111(NoiseParametersKeys.CAVE_CHEESE), 0.6666666666666666);
		NoiseType noiseType7 = class_6916.method_40486(
			class_6916.method_40486(class_6916.method_40480(0.27), noiseType6).method_40468(-1.0, 1.0),
			class_6916.method_40486(class_6916.method_40480(1.5), class_6916.method_40500(class_6916.method_40480(-0.64), noiseType)).method_40468(0.0, 0.5)
		);
		NoiseType noiseType8 = class_6916.method_40486(noiseType5, noiseType7);
		NoiseType noiseType9 = class_6916.method_40505(
			class_6916.method_40505(noiseType8, method_41116(CAVES_ENTRANCES_OVERWORLD)), class_6916.method_40486(noiseType2, noiseType3)
		);
		NoiseType noiseType10 = method_41116(CAVES_PILLARS_OVERWORLD);
		NoiseType noiseType11 = class_6916.method_40485(noiseType10, -1000000.0, 0.03, class_6916.method_40480(-1000000.0), noiseType10);
		return class_6916.method_40508(noiseType9, noiseType11);
	}

	private static NoiseType method_41105(GenerationShapeConfig generationShapeConfig, boolean bl, boolean bl2, boolean bl3) {
		boolean bl4 = !bl;
		NoiseType noiseType;
		if (generationShapeConfig.islandNoiseOverride()) {
			noiseType = method_41116(SLOPED_CHEESE_END);
		} else {
			noiseType = method_41116(bl3 ? SLOPED_CHEESE_OVERWORLD_LARGE_BIOME : SLOPED_CHEESE_OVERWORLD);
		}

		NoiseType noiseType2;
		if (bl4) {
			noiseType2 = noiseType;
		} else {
			NoiseType noiseType3 = class_6916.method_40505(noiseType, class_6916.method_40500(class_6916.method_40480(5.0), method_41116(CAVES_ENTRANCES_OVERWORLD)));
			noiseType2 = class_6916.method_40485(noiseType, -1000000.0, 1.5625, noiseType3, method_41101(noiseType));
		}

		NoiseType noiseType3 = class_6916.method_40492(generationShapeConfig, noiseType2);
		NoiseType noiseType4 = class_6916.method_40483(class_6916.method_40512(noiseType3));
		NoiseType noiseType5 = class_6916.method_40500(noiseType4, class_6916.method_40480(0.64));
		NoiseType noiseType6 = bl2 ? method_41116(CAVES_NOODLE_OVERWORLD) : class_6916.method_40480(64.0);
		return class_6916.method_40505(noiseType5.method_40476(), noiseType6);
	}

	protected static class_7056 method_41104(GenerationShapeConfig generationShapeConfig, boolean bl, boolean bl2) {
		NoiseType noiseType = class_6916.method_40494(method_41111(NoiseParametersKeys.AQUIFER_BARRIER), 0.5);
		NoiseType noiseType2 = class_6916.method_40494(method_41111(NoiseParametersKeys.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67);
		NoiseType noiseType3 = class_6916.method_40494(method_41111(NoiseParametersKeys.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
		NoiseType noiseType4 = class_6916.method_40493(method_41111(NoiseParametersKeys.AQUIFER_LAVA));
		boolean bl3 = generationShapeConfig.largeBiomes();
		NoiseType noiseType5 = method_41116(SHIFT_X);
		NoiseType noiseType6 = method_41116(SHIFT_Z);
		NoiseType noiseType7 = class_6916.method_40487(
			noiseType5, noiseType6, 0.25, method_41111(bl3 ? NoiseParametersKeys.TEMPERATURE_LARGE : NoiseParametersKeys.TEMPERATURE)
		);
		NoiseType noiseType8 = class_6916.method_40487(
			noiseType5, noiseType6, 0.25, method_41111(bl3 ? NoiseParametersKeys.VEGETATION_LARGE : NoiseParametersKeys.VEGETATION)
		);
		NoiseType noiseType9;
		if (generationShapeConfig.islandNoiseOverride()) {
			noiseType9 = class_6916.method_40504(class_6916.method_40482(0L));
		} else {
			NoiseType noiseType10 = method_41116(bl3 ? FACTOR_OVERWORLD_LARGE_BIOME : FACTOR_OVERWORLD);
			NoiseType noiseType11 = method_41116(bl3 ? DEPTH_OVERWORLD_LARGE_BIOME : DEPTH_OVERWORLD);
			noiseType9 = method_40540(noiseType11, class_6916.method_40504(noiseType10), class_6916.method_40479());
		}

		NoiseType noiseType10 = method_41105(generationShapeConfig, bl, bl2, bl3);
		NoiseType noiseType11 = method_41116(Y);
		int i = generationShapeConfig.minimumY();
		int j = Stream.of(class_6955.VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(i);
		int k = Stream.of(class_6955.VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(i);
		NoiseType noiseType12 = method_40539(noiseType11, class_6916.method_40502(method_41111(NoiseParametersKeys.ORE_VEININESS), 1.5, 1.5), j, k, 0);
		float f = 4.0F;
		NoiseType noiseType13 = method_40539(noiseType11, class_6916.method_40502(method_41111(NoiseParametersKeys.ORE_VEIN_A), 4.0, 4.0), j, k, 0).method_40471();
		NoiseType noiseType14 = method_40539(noiseType11, class_6916.method_40502(method_41111(NoiseParametersKeys.ORE_VEIN_B), 4.0, 4.0), j, k, 0).method_40471();
		NoiseType noiseType15 = class_6916.method_40486(class_6916.method_40480(-0.08F), class_6916.method_40508(noiseType13, noiseType14));
		NoiseType noiseType16 = class_6916.method_40493(method_41111(NoiseParametersKeys.ORE_GAP));
		return new class_7056(
			noiseType,
			noiseType2,
			noiseType3,
			noiseType4,
			noiseType7,
			noiseType8,
			method_41116(bl3 ? CONTINENTS_OVERWORLD_LARGE_BIOME : CONTINENTS_OVERWORLD),
			method_41116(bl3 ? EROSION_OVERWORLD_LARGE_BIOME : EROSION_OVERWORLD),
			method_41116(bl3 ? DEPTH_OVERWORLD_LARGE_BIOME : DEPTH_OVERWORLD),
			method_41116(RIDGES_OVERWORLD),
			noiseType9,
			noiseType10,
			noiseType12,
			noiseType15,
			noiseType16
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
		class_7056 arg
	) {
		boolean bl = randomProvider == ChunkRandom.RandomProvider.LEGACY;
		RandomDeriver randomDeriver = randomProvider.create(l).createRandomDeriver();
		Map<NoiseType, NoiseType> map = new HashMap();
		NoiseType.class_6915 lv = noiseType -> {
			if (noiseType instanceof class_6916.class_6931 lvx) {
				RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry = lvx.noiseData();
				return new class_6916.class_6931(registryEntry, method_41107(randomDeriver, registry, registryEntry), lvx.xzScale(), lvx.yScale());
			} else if (noiseType instanceof class_6916.class_6939 lv2x) {
				RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry2 = lv2x.noiseData();
				DoublePerlinNoiseSampler doublePerlinNoiseSampler;
				if (bl) {
					doublePerlinNoiseSampler = DoublePerlinNoiseSampler.create(
						randomDeriver.createRandom(NoiseParametersKeys.OFFSET.getValue()), new DoublePerlinNoiseSampler.NoiseParameters(0, 0.0)
					);
				} else {
					doublePerlinNoiseSampler = method_41107(randomDeriver, registry, registryEntry2);
				}

				return lv2x.method_41086(doublePerlinNoiseSampler);
			} else if (noiseType instanceof class_6916.class_6940 lv3x) {
				if (bl) {
					RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry = lv3x.noiseData();
					if (Objects.equals(registryEntry.getKey(), Optional.of(NoiseParametersKeys.TEMPERATURE))) {
						DoublePerlinNoiseSampler doublePerlinNoiseSampler2 = DoublePerlinNoiseSampler.createLegacy(
							randomProvider.create(l), new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0)
						);
						return new class_6916.class_6940(lv3x.shiftX(), lv3x.shiftY(), lv3x.shiftZ(), lv3x.xzScale(), lv3x.yScale(), registryEntry, doublePerlinNoiseSampler2);
					}

					if (Objects.equals(registryEntry.getKey(), Optional.of(NoiseParametersKeys.VEGETATION))) {
						DoublePerlinNoiseSampler doublePerlinNoiseSampler2 = DoublePerlinNoiseSampler.createLegacy(
							randomProvider.create(l + 1L), new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0)
						);
						return new class_6916.class_6940(lv3x.shiftX(), lv3x.shiftY(), lv3x.shiftZ(), lv3x.xzScale(), lv3x.yScale(), registryEntry, doublePerlinNoiseSampler2);
					}
				}

				RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntryx = lv3x.noiseData();
				return new class_6916.class_6940(
					lv3x.shiftX(), lv3x.shiftY(), lv3x.shiftZ(), lv3x.xzScale(), lv3x.yScale(), registryEntryx, method_41107(randomDeriver, registry, registryEntryx)
				);
			} else if (noiseType instanceof class_6916.class_6944 lv4) {
				return new class_6916.class_6944(lv4.input(), lv4.noiseData(), method_41107(randomDeriver, registry, lv4.noiseData()), lv4.rarityValueMapper());
			} else if (noiseType instanceof InterpolatedNoiseSampler) {
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
			} else if (noiseType instanceof class_6916.class_6924) {
				return new class_6916.class_6924(l);
			} else if (noiseType instanceof class_6916.class_6942 lv5) {
				VanillaTerrainParameters vanillaTerrainParameters = generationShapeConfig.terrainParameters();
				return new class_6916.class_6942(
					lv5.continentalness(), lv5.erosion(), lv5.weirdness(), vanillaTerrainParameters, lv5.spline(), lv5.minValue(), lv5.maxValue()
				);
			} else {
				return (NoiseType)(noiseType instanceof class_6916.class_6941 lv6 ? new class_6916.class_6941(generationShapeConfig, lv6.input()) : noiseType);
			}
		};
		NoiseType.class_6915 lv2 = noiseType -> (NoiseType)map.computeIfAbsent(noiseType, lv);
		class_7056 lv3 = arg.method_41124(lv2);
		RandomDeriver randomDeriver2 = randomDeriver.createRandom(new Identifier("aquifer")).createRandomDeriver();
		RandomDeriver randomDeriver3 = randomDeriver.createRandom(new Identifier("ore")).createRandomDeriver();
		return new NoiseRouter(
			lv3.barrierNoise(),
			lv3.fluidLevelFloodednessNoise(),
			lv3.fluidLevelSpreadNoise(),
			lv3.lavaNoise(),
			randomDeriver2,
			randomDeriver3,
			lv3.temperature(),
			lv3.vegetation(),
			lv3.continents(),
			lv3.erosion(),
			lv3.depth(),
			lv3.ridges(),
			lv3.initialDensityWithoutJaggedness(),
			lv3.finalDensity(),
			lv3.veinToggle(),
			lv3.veinRidged(),
			lv3.veinGap(),
			new VanillaBiomeParameters().getSpawnSuitabilityNoises()
		);
	}

	private static NoiseType method_40541(
		NoiseType noiseType, NoiseType noiseType2, NoiseType noiseType3, class_6916.class_6942.class_7054 arg, double d, double e, NoiseType noiseType4
	) {
		NoiseType noiseType5 = class_6916.method_40489(noiseType, noiseType2, noiseType3, arg, d, e);
		NoiseType noiseType6 = class_6916.method_40488(class_6916.method_40498(), noiseType4, noiseType5);
		return class_6916.method_40499(class_6916.method_40504(noiseType6));
	}

	private static NoiseType method_40540(NoiseType noiseType, NoiseType noiseType2, NoiseType noiseType3) {
		NoiseType noiseType4 = class_6916.method_40500(class_6916.method_40486(noiseType, noiseType3), noiseType2);
		return class_6916.method_40500(class_6916.method_40480(4.0), noiseType4.method_40475());
	}

	private static NoiseType method_40539(NoiseType noiseType, NoiseType noiseType2, int i, int j, int k) {
		return class_6916.method_40483(class_6916.method_40485(noiseType, (double)i, (double)(j + 1), noiseType2, class_6916.method_40480((double)k)));
	}

	protected static double method_40542(GenerationShapeConfig generationShapeConfig, double d, double e) {
		double f = (double)((int)e / generationShapeConfig.verticalBlockSize() - generationShapeConfig.minimumBlockY());
		d = generationShapeConfig.topSlide().method_38414(d, (double)generationShapeConfig.verticalBlockCount() - f);
		return generationShapeConfig.bottomSlide().method_38414(d, f);
	}

	public static double method_40543(GenerationShapeConfig generationShapeConfig, NoiseType noiseType, int i, int j) {
		for (int k = generationShapeConfig.minimumBlockY() + generationShapeConfig.verticalBlockCount(); k >= generationShapeConfig.minimumBlockY(); k--) {
			int l = k * generationShapeConfig.verticalBlockSize();
			double d = -0.703125;
			double e = noiseType.sample(new NoiseType.UnblendedNoisePos(i, l, j)) + -0.703125;
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
